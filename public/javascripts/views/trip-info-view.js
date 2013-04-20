var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  G.TripInfoView = Backbone.View.extend({
    events: {
      'submit .route-info-form': 'save',
      'change #tripPattern': 'onTripPatternChange'
    },

    initialize: function () {

      this.trips = new G.Trips();
      this.calendars = new G.Calendars();

      this.model.tripPatterns.fetch({data: {routeId: this.model.id}});

      this.model.tripPatterns.on('add', this.onTripPatternsReset, this);
      this.model.tripPatterns.on('remove', this.onTripPatternsReset, this);
      this.model.tripPatterns.on('reset', this.onTripPatternsReset, this);

      this.trips.on('add', this.onTripsChange, this);
      this.trips.on('reset', this.onTripsChange, this);

      _.bindAll(this, 'onTripPatternChange', 'onTripsChange');

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
      var data = {
        items: this.model.tripPatterns.models
      }

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

    },

    updateTrips: function() {
        var selectedPatternId  = this.$('#tripPattern').val();

        this.trips.fetch({data: {patternId: selectedPatternId}});
    },

    onTripPatternsReset: function() {
      this.render();

    },

    onTripPatternChange: function(evt) {
      this.updateTrips();
    },

    onTripsChange: function(evt) {
      
      var data = {
        items: this.trips.models
      }

      this.$('#trip-details').html(ich['trip-details-tpl']());

    }

  });
})(GtfsEditor, jQuery, ich);
