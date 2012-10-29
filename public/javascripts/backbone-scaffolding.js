var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  var S = G.Scaffolding = {},
      configs = [],
      router,
      $navList = $('#nav-list'),
      $title = $('#title'),
      $content = $('#content');

  var getConfig = function(name) {
    return _.find(configs, function(c) {
      return c.name === name;
    });
  };

  var collectionToDatatables = function(collection, collectionType) {
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
          val = val.id || val;
        }

        if (field === 'id') {
          val = '<a href="#'+collectionType+'/'+val+'">'+val+'</a>';
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
    configs = c;

    // Build the collection list
    $navList.empty();
    _.each(configs, function(config) {
      $navList.append('<li><a href="#'+config.name+'">'+config.name+'</a></li>');
    });

    _.each(configs, function(config) {
      config.collection.fetch();
    });

    router = new GtfsEditor.Scaffolding.Routes();
  };

  S.ListView = Backbone.View.extend({
    initialize: function() {
      this.collectionType = this.options.collectionType;
      this.collection.on('reset', this.render, this);
    },
    render: function() {
      $title.html(this.collectionType);

      this.$el.html(ich['collection-table-tpl'](
        {new_link: '#'+this.collectionType+'/new'}
      ));

      if (this.collection.first()) {
        var tableOptions = collectionToDatatables(this.collection, this.collectionType);
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
      this.collectionType = this.options.collectionType;
      this.collection.on('reset', this.render, this);
    },

    render: function() {
      var data = [],
          tempModel;
      $title.html(this.collectionType);
      this.model = this.collection.get(this.options.modelId);

      tempModel = this.model || new this.collection.model();
      _.each(tempModel.toJSON(), function(val, key) {
        if (key !== 'id') {
          if (_.isObject(val)) {
            val = val.id;
          }

          data.push({key: key, val: val});
        }
      });

      var tplData = {
        new_link: '#'+this.collectionType+'/new',
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
        this.model.save(data, {
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
            router.navigate(this.collectionType + '/' + this.model.id, {trigger: true});
          }, this),
          error: function() { alert('Oh noes! That didn\'t work.'); }
        });
      }
    },

    'delete': function(evt){
      evt.preventDefault();
      var ct = this.collectionType;

      this.model.destroy({
        success: function() { router.navigate(ct, {trigger: true}); },
        error: function() { alert('Oh noes! That didn\'t work.'); }
      });
    }
  });

  S.Routes = Backbone.Router.extend({
    routes: {
      ':collectionName':     'listCollection',
      ':collectionName/new': 'viewModel',
      ':collectionName/:id': 'viewModel'
    },

    initialize: function() {
      Backbone.history.start({root: '/models'});
    },

    renderView: function(view) {
      $content.html(view.render().el);
    },

    listCollection: function(collectionName) {
      var config = getConfig(collectionName),
          view = new S.ListView({
            collection: config.collection,
            collectionType: config.name
          });
      this.renderView(view);
    },

    viewModel: function(collectionName, modelId) {
      var config = getConfig(collectionName),
          view = new S.SingleView({
            collection: config.collection,
            modelId: modelId,
            collectionType: config.name
          });
      this.renderView(view);
    }
  });
})(GtfsEditor, jQuery, ich);

// Add collections here
GtfsEditor.Scaffolding.init([
  {name: 'Agencies', collection: new GtfsEditor.Agencies()},
  {name: 'Routes', collection: new GtfsEditor.Routes()}
]);