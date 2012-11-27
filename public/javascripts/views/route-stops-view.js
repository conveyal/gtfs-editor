var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  G.RouteStopsView = Backbone.View.extend({
    events: {
    },

    initialize: function () {
      this.stopLayers = {};

      this.collection.on('add', this.onModelAdd, this);
      this.collection.on('reset', this.onCollectionReset, this);
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
        zoom: 15,
        maxZoom: 17
      });
      this.map.addLayer(baseLayer);

      // Remove default prefix
      this.map.attributionControl.setPrefix('');

      this.stopGroup = L.layerGroup().addTo(this.map);

      this.map.on('click', this.onMapClick, this);

      this.collection.fetch();

      return this;
    },

    onMapClick: function(evt) {
      this.addStop(evt.latlng.lat, evt.latlng.lng);
    },

    onStopMarkerDrag: function(evt) {

    },

    onCollectionReset: function() {
      this.stopGroup.clearLayers();

      this.collection.each(function(model, i) {
        this.onModelAdd(model);
      }, this);
    },

    onModelAdd: function(model) {
      this.stopLayers[model.id] = L.marker([model.get('location').lat,
        model.get('location').lng], {
          draggable: true
        });

      this.stopLayers[model.id].on('dragend', function(evt) {
        var latLng = evt.target.getLatLng();
        model.save({location: {lat: latLng.lat, lng: latLng.lng} });
      });

      this.map.addLayer(this.stopLayers[model.id]);
    },

    addStop: function(lat, lng) {
      var data = {
        location: {lat: lat, lng: lng},
        agency: this.options.agencyId
      };

      this.collection.create(data, {
        wait: true,
        success: _.bind(function() {
          console.log('success');
        }, this),
        error: function() { console.log('Oh noes! That didn\'t work.'); }
      });
    }
  });
})(GtfsEditor, jQuery, ich);