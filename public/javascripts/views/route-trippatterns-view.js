var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  G.RouteTripPatternsView = Backbone.View.extend({
    events: {
    },

    initialize: function () {
      this.standardStopLayers = {};
      this.majorStopLayers = {};

      this.options.stops.on('add', this.onStopModelAdd, this);
      this.options.stops.on('reset', this.onStopsReset, this);

      this.model.tripPatterns.on('reset', this.onTripPatternsReset, this);

      // TODO: filter by route id
      this.model.tripPatterns.fetch();

      this.majorStopIcon = L.icon({
        iconUrl: '/public/images/markers/marker-e1264d.png',
        iconSize: [25, 41],
        iconAnchor: [12, 41],
        popupAnchor: [1, -34],
        shadowUrl: '/public/images/markers/marker-shadow.png',
        shadowSize: [41, 41]
      });

      this.standardStopIcon = L.icon({
        iconUrl: '/public/images/markers/marker-2654d2.png',
        iconSize: [25, 41],
        iconAnchor: [12, 41],
        popupAnchor: [1, -34],
        shadowUrl: '/public/images/markers/marker-shadow.png',
        shadowSize: [41, 41]
      });
    },

    render: function () {
      this.$el.html(ich['map-tpl']());

      new G.RouteSummaryView({
        el: this.$('.route-context'),
        model: this.model
      }).render();

      this.$('.route-sidebar').html(ich['trippatterns-sidebar-tpl']());
      this.$('.step-instructions').html(ich['trippatterns-instructions-tpl']());

      // Base layer config is optional, default to Mapbox Streets
      var url = 'http://{s}.tiles.mapbox.com/v3/mapbox.mapbox-streets/{z}/{x}/{y}.png',
          baseLayer = L.tileLayer(url, {
            attribution: '&copy; OpenStreetMap contributors, CC-BY-SA. <a href="http://mapbox.com/about/maps" target="_blank">Terms &amp; Feedback</a>'
          });

      // Init the map
      this.map = L.map(this.$('#map').get(0), {
        center: [14.5907, 120.9794],
        zoom: 15,
        maxZoom: 17
      });
      this.map.addLayer(baseLayer);

      // Remove default prefix
      this.map.attributionControl.setPrefix('');

      this.stopLayerGroup = L.layerGroup().addTo(this.map);

      this.options.stops.fetch();

      return this;
    },

    onStopsReset: function() {
      this.stopLayerGroup.clearLayers();

      this.options.stops.each(function(model, i) {
        this.onStopModelAdd(model);
      }, this);
    },

    onStopModelAdd: function(model) {
      var markerLayer;
      if (model.get('majorStop')) {
        this.majorStopLayers[model.id] = markerLayer = L.marker([model.get('location').lat,
          model.get('location').lng], {
            id: model.id,
            icon: this.majorStopIcon
          });
      } else {
        this.standardStopLayers[model.id] = markerLayer = L.marker([model.get('location').lat,
          model.get('location').lng], {
            id: model.id,
            icon: this.standardStopIcon
          });
      }

      markerLayer.on('click', function(evt) {
        console.log('add to the cart');
      }, this);

      this.stopLayerGroup.addLayer(markerLayer);
    },

    onTripPatternsReset: function() {
      console.log('just reset the trip patterns!!!!!');
    },

    save: function(evt) {
      evt.preventDefault();
    }
  });
})(GtfsEditor, jQuery, ich);
