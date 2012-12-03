var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  G.RouteInfoView = Backbone.View.extend({
    events: {
      'submit .route-info-form': 'save'
    },

    initialize: function () {
      this.onSave = this.options.onSave || function() {};

      this.collection.on('reset', this.render, this);

      // Model is undefined if this is a new route. It is set when it is created.
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

    render: function () {
      var data = this.model ? this.model.toJSON() : {};

      data.agency = data.agency ? data.agency.id : this.options.agencyId;

      // Get the markup from icanhaz
      var $tpl = ich['route-info-tpl'](data);

      // Easily select the option
      $tpl
        .find('#routeType option[value="'+data.routeType+'"]')
        .attr('selected', true);

      // Render to the dom
      this.$el.html($tpl);

      // Bind help popovers
      this.$('input, select, textarea').popover({
        placement: 'right',
        trigger: 'focus'
      });

      return this;
    },

    save: function(evt){
      evt.preventDefault();

      var data = G.Utils.serializeForm(this.$('form'));

      // Currently existing route, save it
      if (this.model) {
        this.model.save(data, {
          wait: true,
          success: _.bind(function() {
            this.onSave(this.model);
            G.Utils.success('Route successfully saved');
          }, this),
          error: function() {
            G.Utils.error('Route save failed');
          }
        });
      } else {
        // New route, create it
        var model = this.collection.create(data, {
          wait: true,
          success: _.bind(function() {
            this.onSave(this.model);
            G.Utils.success('Route successfully created');
          }, this),
          error: _.bind(function() {
            G.Utils.error('Route save failed');
          }, this)
        });

        // Set and bind events
        this.setModel(model);
      }
    }
  });
})(GtfsEditor, jQuery, ich);
