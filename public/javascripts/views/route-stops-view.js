var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  G.RouteStopsView = Backbone.View.extend({
    events: {
      'submit .stop-details-form': 'save',
      'change .stops-toggle': 'onStopVisibilityChange'
    },

    initialize: function () {

      // Marker caches
      this.stopLayers = {};
      this.stopIcons = {}
      

      // Event bindings for the Stop collection
      this.collection.on('add', this.onModelAdd, this);
      this.collection.on('reset', this.onCollectionReset, this);
      this.collection.on('remove', this.onModelRemove, this);
      this.collection.on('change:majorStop', this.onModelMajorStopChange, this);

      // Custom icons
      this.agencyMajorStopIcon = L.icon({
        iconUrl: '/public/images/markers/marker-0d85e9.png',
        iconSize: [25, 41],
        iconAnchor: [12, 41],
        popupAnchor: [1, -34],
        shadowUrl: '/public/images/markers/marker-shadow.png',
        shadowSize: [41, 41]
      });

      // Custom icons
      this.agencyMinorStopIcon = L.icon({
        iconUrl: '/public/images/markers/marker-31b2c4.png',
        iconSize: [25, 41],
        iconAnchor: [12, 41],
        popupAnchor: [1, -34],
        shadowUrl: '/public/images/markers/marker-shadow.png',
        shadowSize: [41, 41]
      });

      this.otherStopIcon = L.icon({
        iconUrl: '/public/images/markers/marker-gray.png',
        iconSize: [25, 41],
        iconAnchor: [12, 41],
        popupAnchor: [1, -34],
        shadowUrl: '/public/images/markers/marker-shadow.png',
        shadowSize: [41, 41]
      });

      this.selectedStopIcon = L.icon({
        iconUrl: '/public/images/markers/marker-dbcf2c.png',
        iconSize: [25, 41],
        iconAnchor: [12, 41],
        popupAnchor: [1, -34],
        shadowUrl: '/public/images/markers/marker-shadow.png',
        shadowSize: [41, 41]
      });

        _.bindAll(this, 'sizeContent');
        $(window).resize(this.sizeContent);
    },

    render: function () {
      // Setup the containers for a map page
      this.$el.html(ich['map-tpl']());

      // Add the route summary
      new G.RouteSummaryView({
        el: this.$('.route-context'),
        model: this.model
      }).render();

      var sidebarData = {
        route: this.model.attributes
      }

      // Add the instructions and sidebar contents
      this.$('.route-sidebar').html(ich['stops-sidebar-tpl'](sidebarData));
      this.$('.step-instructions').html(ich['stop-instructions-tpl']());

      // Base layer config is optional, default to Mapbox Streets
      var url = 'http://{s}.tiles.mapbox.com/v3/' + G.config.mapboxKey + '/{z}/{x}/{y}.png',
          baseLayer = L.tileLayer(url, {
            attribution: '&copy; OpenStreetMap contributors, CC-BY-SA. <a href="http://mapbox.com/about/maps" target="_blank">Terms &amp; Feedback</a>'
          });

      // Init the map
      this.map = L.map(this.$('#map').get(0), {
        center: G.session.mapCenter, //TODO: add to the config file for now
        zoom: G.session.mapZoom,
        maxZoom: 17
      });
      this.map.addLayer(baseLayer);

      // Remove default prefix
      this.map.attributionControl.setPrefix('');

      // Add a layer group for standard and major stops
      this.stopLayerGroup = L.layerGroup().addTo(this.map);

      // Bind map events
      this.map.on('contextmenu', this.onMapRightClick, this);
      this.map.on('popupopen', this.onPopupOpen, this);

       var view = this;
      this.map.on('moveend', function(evt) {
        view.updateStops();
        G.session.mapCenter = view.map.getCenter();
      });

      this.map.on('zoomend', function(evt) {
        view.clearStops();

        if(view.map.getZoom() < 15)
          $('#stops-hidden-message').show();
        else
          $('#stops-hidden-message').hide();

         G.session.mapZoom= view.map.getZoom();
      });

      if(view.map.getZoom() < 15)
        $('#stops-hidden-message').show();
      else
        $('#stops-hidden-message').hide();

      var mapCenter = this.map.getCenter();

      $('input[name="stopFilterRadio"]').change( function() {
          view.clearStops();
          view.updateStops();
      });

      this.clearStops();

      this.updateStops(mapCenter);

      this.sizeContent();

      return this;
    },

    sizeContent: function() {
        var newHeight = $(window).height() - (175) + "px";
      
        $("#map").css("height", newHeight);

        if(this.map != undefined)
          this.map.invalidateSize();
    },

    updateStops: function (mapCenter) {
      // don't keep more than 500 markers on map at anytime. 
       if(this.collection.length > 500)
          this.collection.remove(this.collection.slice(0, 200));
      
       var agencyId = null;
       if($('input[name="stopFilterRadio"]:checked').val() != 'all' )
          agencyId = this.model.get('agency').id;
      
       if(G.config.showStandardStops && this.map.getZoom() >= 15) {
          if(mapCenter == null)
            mapCenter = this.map.getCenter();

         

        this.collection.fetch({remove: false, data: {agencyId: agencyId, lat: mapCenter.lat, lon: mapCenter.lng}});
      }
        

      if(G.config.showMajorStops)
        this.collection.fetch({remove: false, data: {agencyId: agencyId, majorStops: true}});
    },

    clearStops: function() {
      this.collection.reset();
    },

    onMapRightClick: function(evt) {
      this.addStop(evt.latlng.lat, evt.latlng.lng);
    },

    onPopupOpen: function(evt) {
      var self = this;

      // Bind the delete button inside the popup when it opens
      $(this.map.getPanes().popupPane)
        .find('.stop-delete-btn')
        .on('click', function() {
          var id = $(this).siblings('[name=id]').val();
          self.destroy(id);
        });
    },

    // Save the location of a stop after you drag it around
    onStopMarkerDrag: function(evt) {
      var latLng = evt.target.getLatLng();
      evt.target.dragging.disable();

      
      if(this.collection.get(evt.target.options.id).get('majorStop'))
        evt.target.setIcon(this.stopIcons[model.id]);
      else
        evt.target.setIcon(this.stopIcons[model.id]);

      this.collection.get(evt.target.options.id)
        .save({location: {lat: latLng.lat, lng: latLng.lng} });
    },

    // When the stop collection resets, clear all the markers and add new ones.
    onCollectionReset: function() {
      this.stopLayerGroup.clearLayers();

      this.collection.each(function(model, i) {
        this.onModelAdd(model);
      }, this);
    },

    // Update the marker if the majorStop property changes, including:
    //  - Swap caches
    //  - Change icons
    //  - Handle visiblity
    onModelMajorStopChange: function(model) {
      if (model.get('majorStop')) {
  
        this.stopLayers[model.id].setIcon(this.agencyMajorStopIcon);
        
        // It's a major stop now, are those visible?
        if ($('#major-stops-toggle').is(':not(:checked)')) {
          this.stopLayerGroup.removeLayer(this.stopLayers[model.id]);
        }
      } else {
        
        this.stopLayers[model.id].setIcon(this.agencyMinorStopIcon);
      }
    },

    // Clean up when a stop model is removed from the collection (ie deleted)
    onModelRemove: function(model) {
      this.map.closePopup();

        this.stopLayerGroup.removeLayer(this.stopLayers[model.id]);
        delete this.stopLayers[model.id];
    },

    // Add the marker and bind events when a new stop is created.
    onModelAdd: function(model) {
      var markerLayer;


      if (model.get('agency').id == this.model.get('agency').id) {

        if(model.get('majorStop'))
          this.stopIcons[model.id] = this.agencyMajorStopIcon;  
        else
          this.stopIcons[model.id] = this.agencyMinorStopIcon;  

      } 
      else {
        this.stopIcons[model.id] = this.otherStopIcon;
      }

      this.stopLayers[model.id] = markerLayer = L.marker([model.get('location').lat,
          model.get('location').lng], {
          draggable: false,
          id: model.id,
          icon: this.stopIcons[model.id]
        });

      // Show the popup when you click a marker
      markerLayer.on('click', function(evt) {
        evt.target
          .unbindPopup()
          .bindPopup($('<div>').append(ich['stop-form-tpl'](model.toJSON())).html())
          .openPopup();
      }, this);




       markerLayer.on('dblclick', function(evt) {
        evt.target.setIcon(this.selectedStopIcon);
        evt.target.dragging.enable();
        
      }, this);

      // Save the location after you drag it around
      markerLayer.on('dragend', this.onStopMarkerDrag, this);

      if((model.get('majorStop') && G.config.showMajorStops) || (!model.get('majorStop') && G.config.showStandardStops))
        this.stopLayerGroup.addLayer(markerLayer);

       if(model.get('justAdded')) {
        markerLayer
          .bindPopup($('<div>').append(ich['stop-form-tpl'](model.toJSON())).html())
          .openPopup();
      }
    },

    // How to create a brand new stop
    addStop: function(lat, lng) {

      // default to major stops when that's all that's showing
      var majorStop = false;
      if(G.config.showMajorStops && !G.config.showStandardStops || this.map.getZoom() < 15)
        majorStop = true;

      var data = {
        majorStop: majorStop,
        justAdded: true,
        location: {lat: lat, lng: lng},
        agency: this.model.get('agency').id
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

    // Handle visibility checkbox changes
  /*  onStopVisibilityChange: function(evt) {
      var $checkbox = $(evt.target);

     
      if ($checkbox.attr('id') === 'major-stops-toggle') {
        if ($checkbox.is(':checked')) {
          this.showMajorStops();
          G.config.showMajorStops = true;
        } else {
          this.hideMajorStops();
          G.config.showMajorStops = false;
        }
      } else {
        if ($checkbox.is(':checked')) {
          this.showStandardStops();
          G.config.showStandardStops = true;
        } else {
          this.hideStandardStops();
          G.config.showStandardStops = false;
        }
      }

       this.clearStops();
      this.updateStops();

      
    },

    // Toggle marker visibility
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
    }, */

    // Save the stop form contents
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

    // Delete and existing stop model
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
