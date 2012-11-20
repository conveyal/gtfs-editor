var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  G.RouteStopsView = Backbone.View.extend({
    events: {
    },

    initialize: function () {

    },

    render: function () {
      this.$el.html(ich['map-tpl']());

      // Base layer config is optional, default to Mapbox Streets
      var url = 'http://{s}.tiles.mapbox.com/v3/mapbox.mapbox-streets/{z}/{x}/{y}.png',
          baseLayer = L.tileLayer(url, {
            attribution: '&copy; OpenStreetMap contributors, CC-BY-SA. <a href="http://mapbox.com/about/maps" target="_blank">Terms &amp; Feedback</a>'
          });

      // Init the map
      this.map = L.map(this.$('#map').get(0), {
        center: [14.5907, 120.9794],
        zoom: 15
      });
      this.map.addLayer(baseLayer);

      // Remove default prefix
      this.map.attributionControl.setPrefix('');

      return this;
    }
  });
})(GtfsEditor, jQuery, ich);