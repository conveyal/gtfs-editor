var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  G.FormView = Backbone.View.extend({
    events: {
      'click #save-btn': 'save',
      'click #delete-btn': 'destroy'
    },

    initialize: function() {
      this.collection.on('reset', this.render, this);

      if (this.model) {
        this.setModel(this.model);
      }
    },

    setModel: function(model) {
      this.model = model;
      this.model.on('change', this.render, this);
      this.model.on('destroy', this.onModelDestroy, this);
    },

    onModelDestroy: function(){
      this.model = null;
      this.render();
    },

    render: function() {
      var data = [],
          tempModel;

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
        title: this.collection.type,
        can_delete: !!this.model,
        data: data
      };

      this.$el.html(ich['model-form-tpl'](tplData));
      return this;
    },

    // This parses JSON strings into objects or arrays
    parseInput: function(val) {
      var obj;

      if (_.isString(val)) {
        try {
          obj = JSON.parse(val);
        } catch(e) {
          // val was a string, but not JSON. This will break on the server.
        }
        if (obj) {
          val = obj;
        }
      }

      return (_.isUndefined(val) || val === '' ? null : val);
    },

    save: function(evt){
      evt.preventDefault();

      var data = {},
          self = this;
      this.$('input').each(function(i, el) {
        var $input = $(el),
            val = self.parseInput($input.val());

        data[$input.attr('id')] = val;
      });

      if (this.model) {
        this.model.save(data, {
          wait: true,
          success: _.bind(function() {
            alert('Saved!');
            G.router.navigate(this.collection.type + '/' + this.model.id);
          }, this),
          error: function() { alert('Oh no! That didn\'t work.'); }
        });
      } else {
        this.model = this.collection.create(data, {
          wait: true,
          success: _.bind(function() {
            alert('Created!');
            G.router.navigate(this.collection.type + '/' + this.model.id, {trigger: true});
          }, this),
          error: _.bind(function() {
            this.model = null;
            alert('Oh no! That didn\'t work.');
          }, this)
        });

        this.setModel(this.model);
      }
    },

    destroy: function(evt){
      evt.preventDefault();
      var ct = this.collection.type;

      if (window.confirm('Really delete?')) {
        this.model.destroy({
          success: function() { G.router.navigate(ct, {trigger: true}); },
          error: function() { alert('Oh no! That didn\'t work.'); }
        });
      }
    }
  });
})(GtfsEditor, jQuery, ich);
