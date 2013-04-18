var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  G.TripInfoView = Backbone.View.extend({
    events: {
      'submit .route-info-form': 'save'
    },

    initialize: function () {
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
      var $tpl = ich['trip-info-tpl'](data);

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

      
    
      }

  });
})(GtfsEditor, jQuery, ich);
