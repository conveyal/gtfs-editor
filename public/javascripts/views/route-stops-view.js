var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  G.RouteStopsView = Backbone.View.extend({
    events: {
      'submit .stop-details-form': 'save',
      'change .stops-toggle': 'onStopVisibilityChange'
    },

    initialize: function () {
      this.standardStopLayers = {};
      this.majorStopLayers = {};

      this.collection.on('add', this.onModelAdd, this);
      this.collection.on('reset', this.onCollectionReset, this);
      this.collection.on('remove', this.onModelRemove, this);
      this.collection.on('change:majorStop', this.onModelMajorStopChange, this);

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

      this.$('.route-sidebar').html(ich['route-sidebar-tpl']());
      this.$('.step-instructions').html(ich['stop-instructions-tpl']());

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

      this.map.on('contextmenu', this.onMapRightClick, this);
      this.map.on('popupopen', this.onPopupOpen, this);

      this.collection.fetch();

      return this;
    },

    onMapRightClick: function(evt) {
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
      this.stopLayerGroup.clearLayers();

      this.collection.each(function(model, i) {
        this.onModelAdd(model);
      }, this);
    },

    onModelMajorStopChange: function(model) {
      if (model.get('majorStop')) {
        this.majorStopLayers[model.id] = this.standardStopLayers[model.id];
        this.majorStopLayers[model.id].setIcon(this.majorStopIcon);
        delete this.standardStopLayers[model.id];

        if ($('#major-stops-toggle').is(':not(:checked)')) {
          this.stopLayerGroup.removeLayer(this.majorStopLayers[model.id]);
        }
      } else {
        this.standardStopLayers[model.id] = this.majorStopLayers[model.id];
        this.standardStopLayers[model.id].setIcon(this.standardStopIcon);
        delete this.majorStopLayers[model.id];

        if ($('#standard-stops-toggle').is(':not(:checked)')) {
          this.stopLayerGroup.removeLayer(this.standardStopLayers[model.id]);
        }
      }
    },

    onModelRemove: function(model) {
      this.map.closePopup();

      if (model.get('majorStop')) {
        this.stopLayerGroup.removeLayer(this.majorStopLayers[model.id]);
        delete this.majorStopLayers[model.id];
      } else {
        this.stopLayerGroup.removeLayer(this.standardStopLayers[model.id]);
        delete this.standardStopLayers[model.id];
      }
    },

    onModelAdd: function(model) {
      var markerLayer;
      if (model.get('majorStop')) {
        this.majorStopLayers[model.id] = markerLayer = L.marker([model.get('location').lat,
          model.get('location').lng], {
            draggable: true,
            id: model.id,
            icon: this.majorStopIcon
          });
      } else {
        this.standardStopLayers[model.id] = markerLayer = L.marker([model.get('location').lat,
          model.get('location').lng], {
            draggable: true,
            id: model.id,
            icon: this.standardStopIcon
          });
      }

      markerLayer.on('click', function(evt) {
        evt.target
          .unbindPopup()
          .bindPopup($('<div>').append(ich['stop-form-tpl'](model.toJSON())).html())
          .openPopup();
      }, this);

      markerLayer.on('dragend', this.onStopMarkerDrag, this);

      this.stopLayerGroup.addLayer(markerLayer);
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

    onStopVisibilityChange: function(evt) {
      var $checkbox = $(evt.target);

      if ($checkbox.attr('id') === 'major-stops-toggle') {
        if ($checkbox.is(':checked')) {
          this.showMajorStops();
        } else {
          this.hideMajorStops();
        }
      } else {
        if ($checkbox.is(':checked')) {
          this.showStandardStops();
        } else {
          this.hideStandardStops();
        }
      }
    },

    hideStandardStops: function() {
      _.each(this.standardStopLayers, function(marker) {
        this.stopLayerGroup.removeLayer(marker);
      }, this);
    },
    showStandardStops: function() {
      _.each(this.standardStopLayers, function(marker) {
        this.stopLayerGroup.addLayer(marker);
      }, this);
    },
    hideMajorStops: function() {
      _.each(this.majorStopLayers, function(marker) {
        this.stopLayerGroup.removeLayer(marker);
      }, this);
    },
    showMajorStops: function() {
      _.each(this.majorStopLayers, function(marker) {
        this.stopLayerGroup.addLayer(marker);
      }, this);
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
