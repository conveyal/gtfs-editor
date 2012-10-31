var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  var S = G.Scaffolding = {},
      _collections = [],
      _router,
      $navList = $('#nav-list'),
      $title = $('#title'),
      $content = $('#content');

  var getCollection = function(type) {
    return _.find(_collections, function(c) {
      return c.type === type;
    });
  };

  var collectionToDatatables = function(collection) {
    var fields = _.keys(new collection.model().defaults),
        aaData = [],
        aoColumns = _.map(fields, function(key) {
          return { sTitle: key };
        });

    _.each(collection.toJSON(), function(obj){
      var rowData = [];
      _.each(fields, function(field) {
        var val = obj[field];

        if (_.isObject(val)) {
          val = val.id || JSON.stringify(val);
        }

        if (field === 'id') {
          val = '<a href="#'+collection.type+'/'+val+'">'+val+'</a>';
        }

        rowData.push(val);
      });
      aaData.push(rowData);
    });

    return {
      aoColumns: aoColumns,
      aaData: aaData
    };
  };

  S.init = function(c) {
    ich.grabTemplates();

    // array of objects with key/val properties
    _collections = c;

    // Build the collection list
    $navList.empty();
    _.each(_collections, function(collection) {
      $navList.append('<li><a href="#'+collection.type+'">'+collection.type+'</a></li>');
      collection.fetch();
    });

    _router = new GtfsEditor.Scaffolding.Routes();
  };

  S.ListView = Backbone.View.extend({
    initialize: function() {
      this.collection.on('reset', this.render, this);
    },
    render: function() {
      $title.html(this.collection.type);

      this.$el.html(ich['collection-table-tpl'](
        {new_link: '#'+this.collection.type+'/new'}
      ));

      if (this.collection.first()) {
        var tableOptions = collectionToDatatables(this.collection, this.collection.type);
        tableOptions.bPaginate = false;
        tableOptions.bInfo = false;

        this.$('#collection-table').show().dataTable(tableOptions);
      }

      return this;
    }
  });

  S.SingleView = Backbone.View.extend({
    events: {
      'click #save-btn': 'save',
      'click #delete-btn': 'delete'
    },

    initialize: function() {
      this.collection.on('reset', this.render, this);
    },

    render: function() {
      var data = [],
          tempModel;
      $title.html(this.collection.type);
      this.model = this.collection.get(this.options.modelId);

      tempModel = this.model || new this.collection.model();
      _.each(tempModel.toJSON(), function(val, key) {
        if (key !== 'id') {
          if (_.isObject(val)) {
            val = val.id || JSON.stringify(val);
          }

          data.push({key: key, val: val});
        }
      });

      var tplData = {
        new_link: '#'+this.collection.type+'/new',
        can_delete: !!this.model,
        data: data
      };

      this.$el.html(ich['model-form-tpl'](tplData));
      return this;
    },

    save: function(evt){
      evt.preventDefault();

      var data = {};
      this.$('input').each(function(i, el) {
        var $input = $(el);
        data[$input.attr('id')] = $input.val() || null;
      });

      if (this.model) {
        // This seems redundant, but we need to call set first so that the
        // validator work as expected. Otherwise any attribute overrides in the
        // validator will not be set.
        this.model.set(data, { silent: true });
        this.model.save(null, {
          wait: true,
          success: function() {
            alert('Saved!');
          },
          error: function() { alert('Oh noes! That didn\'t work.'); }
        });
      } else {
        this.model = this.collection.create(data, {
          wait: true,
          success: _.bind(function() {
            alert('Created!');
            _router.navigate(this.collection.type + '/' + this.model.id, {trigger: true});
          }, this),
          error: _.bind(function() {
            this.model = null;
            alert('Oh noes! That didn\'t work.');
          }, this)
        });
      }
    },

    'delete': function(evt){
      evt.preventDefault();
      var ct = this.collection.type;

      this.model.destroy({
        success: function() { _router.navigate(ct, {trigger: true}); },
        error: function() { alert('Oh noes! That didn\'t work.'); }
      });
    }
  });

  S.Routes = Backbone.Router.extend({
    routes: {
      ':type':     'listCollection',
      ':type/new': 'viewModel',
      ':type/:id': 'viewModel'
    },

    initialize: function() {
      Backbone.history.start();
    },

    renderView: function(view) {
      $content.html(view.render().el);
    },

    listCollection: function(type) {
      var view = new S.ListView({
            collection: getCollection(type)
          });
      this.renderView(view);
    },

    viewModel: function(type, modelId) {
      var view = new S.SingleView({
            collection: getCollection(type),
            modelId: modelId
          });
      this.renderView(view);
    }
  });
})(GtfsEditor, jQuery, ich);

// Add collections here
GtfsEditor.Scaffolding.init([
  new GtfsEditor.Agencies(),
  new GtfsEditor.Routes(),
  new GtfsEditor.Stops()
]);