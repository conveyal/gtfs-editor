var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  G.RouteStopsView = Backbone.View.extend({
    events: {
      'submit .stop-details-form': 'save'
    },

    initialize: function () {
      this.stopLayers = {};

      this.collection.on('add', this.onModelAdd, this);
      this.collection.on('reset', this.onCollectionReset, this);
      this.collection.on('remove', this.onModelRemove, this);
    },

    render: function () {
      this.$el.html(ich['map-tpl']());

      new G.RouteSummaryView({
        el: this.$('.route-context'),
        model: this.model
      }).render();

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
      this.map.on('popupopen', this.onPopupOpen, this);

      this.collection.fetch();

      return this;
    },

    onMapClick: function(evt) {
      this.addStop(evt.latlng.lat, evt.latlng.lng);
    },

    onPopupOpen: function(evt) {
      var self = this;
      $(this.map.getPanes().popupPane)
      .find('.stop-delete-btn')
      .on('click', function() {
        var id = $(this).siblings('[name=id]').val();
        self.destroy(id);
      });
    },

    onStopMarkerDrag: function(evt) {
      var latLng = evt.target.getLatLng();
      this.collection.get(evt.target.options.id)
        .save({location: {lat: latLng.lat, lng: latLng.lng} });
    },

    onCollectionReset: function() {
      this.stopGroup.clearLayers();

      this.collection.each(function(model, i) {
        this.onModelAdd(model);
      }, this);
    },

    onModelRemove: function(model) {
      this.map.closePopup();
      this.stopGroup.removeLayer(this.stopLayers[model.id]);
    },

    onModelAdd: function(model) {
      this.stopLayers[model.id] = L.marker([model.get('location').lat,
        model.get('location').lng], {
          draggable: true,
          id: model.id
        });

      this.stopLayers[model.id].on('click', function(evt) {
        evt.target
          .unbindPopup()
          .bindPopup($('<div>').append(ich['stop-form-tpl'](model.toJSON())).html())
          .openPopup();
      }, this);

      this.stopLayers[model.id].on('dragend', this.onStopMarkerDrag, this);

      this.map.addLayer(this.stopLayers[model.id]);
    },

    addStop: function(lat, lng) {
      var data = {
        location: {lat: lat, lng: lng},
        agency: this.options.agencyId
      };

      this.collection.create(data, {
        wait: true,
        success: function() {
          G.Utils.success('Stop successfully created');
        },
        error: function() { 
          G.Utils.error('Failed to create stop');
        }
      });
    },

    save: function(evt) {
      evt.preventDefault();
      var data = G.Utils.serializeForm($(evt.target));
      this.collection.get(data.id).save(data, {
        success: function() {
          G.Utils.success('Stop successfully saved');
        },
        error: function() {
          G.Utils.error('Failed to save stop');
        }
      });
    },

    destroy: function(modelId) {
      if (G.Utils.confirm('Are you sure you want to delete this stop?')) {
        this.collection.get(modelId).destroy({ 
          wait: true,
          success: function() {
            G.Utils.success('Stop successfully deleted');
          },
          error: function() {
            G.Utils.error('Failed to delete stop');
          }
        });
      }
    }
  });
})(GtfsEditor, jQuery, ich);
