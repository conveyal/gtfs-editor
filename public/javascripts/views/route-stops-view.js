var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  G.RouteStopsView = Backbone.View.extend({
    events: {
      'submit .stop-details-form': 'save',
      'click .stop-find-duplicates-btn': 'findDuplicateStops',
      'click .stop-find-duplicates-cancel-btn': 'cancelFindDuplicateStops',
      'change .stops-toggle': 'onStopVisibilityChange',
      'change input[name="stop-use-satellite"]': 'onSatelliteToggle',
    },

    initialize: function () {

      // Marker caches
      this.stopLayers = {};
      this.stopIcons = {}

      // Event bindings for the Stop collection
      this.collection.on('add', this.onModelAdd, this);
      this.collection.on('reset', this.onCollectionReset, this);
      this.collection.on('remove', this.onModelRemove, this);
      this.collection.on('change', this.onModelStopChange, this);

      this.stopGroups = new G.StopGroups({agencyId: this.model.get('agencyId'), success: this.finishedFindDuplicateStops});

      this.stopGroups.on('reset', this.resetDuplicateStops, this);
      this.stopGroups.on('add', this.addStopGroup, this);


      // Custom icons
      this.agencyMajorStopIcon = L.divIcon({
        iconSize: [10, 10],
        iconAnchor: [5, 5],
        popupAnchor: [5, 5],
        className: 'stop stop-major'
      });

      // Custom icons
      this.agencyMinorStopIcon = L.divIcon({
        iconSize: [10, 10],
        iconAnchor: [5, 5],
        popupAnchor: [5, 5],
        className: 'stop stop-minor'
      });

      this.selectedStopIcon = L.divIcon({
        iconSize: [8, 8],
        iconAnchor: [4, 4],
        popupAnchor: [4, 4],
        className: 'stop stop-selected'
      });

        _.bindAll(this, 'sizeContent', 'destroy', 'save', 'cancelFindDuplicateStops', 'findDuplicateStops', 'finishedFindDuplicateStops', 'addStopGroup', 'resetDuplicateStops', 'mergeStops', 'onSatelliteToggle');

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
        route: this.model.attributes,
        agency: G.session.agencies[G.session.agencyId],
        useSatellite : G.session.useSatellite
      }

      // Add the instructions and sidebar contents
      this.$('.route-sidebar').html(ich['stops-sidebar-tpl'](sidebarData));
      this.$('.step-instructions').html(ich['stop-instructions-tpl']());

      // Base layer config is optional, default to Mapbox Streets

      var tileKey;
      if(G.session.useSatellite)
        tileKey = G.config.mapboxSatelliteKey;
      else
        tileKey = G.config.mapboxKey;


      var url = 'http://{s}.tiles.mapbox.com/v3/' + tileKey + '/{z}/{x}/{y}.png',
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

      // create a mergeStopLayerGroup
      this.mergeStopLayerGroup = L.layerGroup()

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

        if(view.map.getZoom() < 13)
          $('#stops-hidden-message').show();
        else
          $('#stops-hidden-message').hide();

         G.session.mapZoom= view.map.getZoom();
      });

      if(view.map.getZoom() < 13)
        $('#stops-hidden-message').show();
      else
        $('#stops-hidden-message').hide();

      var mapCenter = this.map.getCenter();

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
       if(this.collection.length > 750)
          this.collection.remove(this.collection.slice(0, 200));

       if(G.config.showStandardStops && this.map.getZoom() >= 13) {
        var bounds = this.map.getBounds();
        var nw = bounds.getNorthWest();
        var se = bounds.getSouthEast();
        this.collection.fetch({remove: false, data: {west: nw.lng, east: se.lng, north: nw.lat, south: se.lat}});
      }


      if(G.config.showMajorStops)
        this.collection.fetch({remove: false, data: {majorStops: true}});
    },

    clearStops: function() {
      this.collection.reset();
    },

    findDuplicateStops: function() {

      this.mergeStopLayerGroup.clearLayers();


      this.stopGroups.findDuplicateStops();

      this.map.removeLayer(this.stopLayerGroup);
      this.map.addLayer(this.mergeStopLayerGroup);

      $('#stop-duplicate-search-progress').show();
      $('.stop-find-duplicates-btn').hide();

      /*this.duplicateStopsCollection = new G.Stops();


      this.duplicateStopsCollection.on('reset', this.deduplicateStops);

      this.duplicateStopsCollection.fetch({reset: true, data: {agencyId: this.model.get('agencyId')}});*/

    },

    finishedFindDuplicateStops: function() {

      $('#stop-duplicate-search-progress').hide();
      $('#stop-duplicate-search-cancel').show();

    },

    cancelFindDuplicateStops: function(evt) {


      this.mergeStopLayerGroup.clearLayers();

      this.map.addLayer(this.stopLayerGroup);
      this.map.removeLayer(this.mergeStopLayerGroup);

      $('#stop-duplicate-search-cancel').hide();
      $('.stop-find-duplicates-btn').show();

    },

    resetDuplicateStops: function() {


      this.mergeStopLayerGroup.clearLayers();

    },

    mergeStops: function(evt) {
      //alert('merge clicked: ' + $(evt.target).data('id'));

      this.stopGroups.merge($(evt.target).data('id'));
    },

    addStopGroup: function(stopGroup) {


      var data = {
        mergedStop: stopGroup.get('mergedStop'),
        stops: stopGroup.get('stops')
      }
      var $popupContent = ich['stop-merge-view-tpl'](data);

      var markerLayer = L.marker([stopGroup.get('mergedStop').get('lat'),
        stopGroup.get('mergedStop').get('lon')], {
        draggable: false,
        icon: this.selectedStopIcon
      });


      markerLayer.bindPopup($('<div>').append($popupContent).html());

      this.mergeStopLayerGroup.addLayer(markerLayer);


      /*var view = this;

      var stopLatLngs = new Array();

      this.map.removeLayer(this.stopLayerGroup);
      this.map.addLayer(this.mergeStopLayerGroup);

      this.duplicateStopsCollection.each(function(stop) {

        stopLatLngs.push({latitude: stop.get('location').lat, longitude: stop.get('location').lng, stopId: stop.id});

      });

      // check distance for each stop pair
      _.each(stopLatLngs, function(stop1) {

        _.each(stopLatLngs, function(stop2) {

            var dist = geolib.getDistance(stop1, stop2);
            if(dist < 5 &&  stop1.stopId != stop2.stopId) {
              view.stopGroups.group(view.duplicateStopsCollection.get(stop1.stopId), view.duplicateStopsCollection.get(stop2.stopId));
            }

        });

      });

      this.stopGroups.each(function(stopGroup) {


        var data = {
          mergedStop: stopGroup.get('mergedStop'),
          stops: stopGroup.get('stops')
        }
        var $popupContent = ich['stop-merge-view-tpl'](data);

        var markerLayer = L.marker([stopGroup.get('mergedStop').get('location').lat,
          stopGroup.get('mergedStop').get('location').lng], {
          draggable: false,
          icon: view.selectedStopIcon
        });


        markerLayer.bindPopup($('<div>').append($popupContent).html());

        view.mergeStopLayerGroup.addLayer(markerLayer);



      });*/

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
          var id = $(this).parent().find('[name=id]').val();
          self.destroy(id);
        });


        // Bind the delete button inside the popup when it opens
      $(this.map.getPanes().popupPane)
        .find('.stop-merge-btn')
        .on('click', this.mergeStops);
    },

    // Save the location of a stop after you drag it around
    onStopMarkerDrag: function(evt) {
      var latLng = evt.target.getLatLng();
      evt.target.dragging.disable();


      evt.target.setIcon(this.stopIcons[evt.target.options.id]);

      this.collection.get(evt.target.options.id)
        .save({lat: latLng.lat, lon: latLng.lng});
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
    onModelStopChange: function(model) {

      this.stopLayers[model.id].setLatLng([model.get('lat'),model.get('lon')]);

      if (model.get('majorStop')) {

        this.stopLayers[model.id].setIcon(this.agencyMajorStopIcon);

        // It's a major stop now, are those visible?
        if ($('#major-stops-toggle').is(':not(:checked)')) {
          this.stopLayerGroup.removeLayer(this.stopLayers[model.id]);
        }
      } else {

        this.stopLayers[model.id].setIcon(this.agencyMinorStopIcon);
      }

      var $popupContent;

      $popupContent = ich['stop-form-tpl'](model.toJSON());

      if(model.get('majorStop'))
        this.stopIcons[model.id] = this.agencyMajorStopIcon;
      else
        this.stopIcons[model.id] = this.agencyMinorStopIcon;

      $popupContent
        .find('#bikeParking option[value="' + model.get('bikeParking') + '"]')
        .attr('selected', true);

     $popupContent
       .find('#carParking option[value="' + model.get('carParking') + '"]')
       .attr('selected', true);

      $popupContent
        .find('#wheelchairBoarding option[value="' + model.get('wheelchairBoarding') + '"]')
        .attr('selected', true);

      $popupContent
        .find('#pickupType option[value="' + model.get('pickupType') + '"]')
        .attr('selected', true);

      $popupContent
        .find('#dropOffType option[value="' + model.get('dropOffType') + '"]')
        .attr('selected', true);

      this.stopLayers[model.id].on('click', function(evt) {
        evt.target
          .unbindPopup()
          .bindPopup($('<div>').append($popupContent).html())
          .openPopup();
      }, this);

    },

    onSatelliteToggle: function(evt) {

      if($('input[name="stop-use-satellite"]').attr('checked')) {
          G.session.useSatellite  = true;
      }
      else {
          G.session.useSatellite = false;
      }

      this.render();

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

      var $popupContent;

      $popupContent = ich['stop-form-tpl'](model.toJSON());

      if(model.get('majorStop'))
        this.stopIcons[model.id] = this.agencyMajorStopIcon;
      else
        this.stopIcons[model.id] = this.agencyMinorStopIcon;


      $popupContent
        .find('#bikeParking option[value="' + model.get('bikeParking') + '"]')
        .attr('selected', true);

      $popupContent
          .find('#carParking option[value="' + model.get('carParking') + '"]')
          .attr('selected', true);

      $popupContent
        .find('#wheelchairBoarding option[value="' + model.get('wheelchairBoarding') + '"]')
        .attr('selected', true);

      $popupContent
        .find('#pickupType option[value="' + model.get('pickupType') + '"]')
        .attr('selected', true);

      $popupContent
        .find('#dropOffType option[value="' + model.get('dropOffType') + '"]')
        .attr('selected', true);

      this.stopLayers[model.id] = markerLayer = L.marker([model.get('lat'),
          model.get('lon')], {
          draggable: false,
          id: model.id,
          icon: this.stopIcons[model.id]
        });

      // Show the popup when you click a marker
      markerLayer.on('click', function(evt) {
        evt.target
          .unbindPopup()
          .bindPopup($('<div>').append($popupContent).html())
          .openPopup();
      }, this);



      if(model.get('agencyId') == this.model.get('agencyId')) {

        markerLayer.on('dblclick', function(evt) {
          evt.target.setIcon(this.selectedStopIcon);
          evt.target.dragging.enable();

        }, this);

        // Save the location after you drag it around

        markerLayer.on('dragend', this.onStopMarkerDrag, this);
      }

      markerLayer.bindLabel(model.get('stopName'));

      if((model.get('majorStop') && G.config.showMajorStops) || (!model.get('majorStop') && G.config.showStandardStops))
        this.stopLayerGroup.addLayer(markerLayer);

       if(model.get('justAdded')) {
        markerLayer
          .bindPopup($('<div>').append($popupContent).html())
          .openPopup();
      }
    },

    // How to create a brand new stop
    addStop: function(lat, lng) {

      // default to major stops when that's all that's showing
      var majorStop = false;
      if(G.config.showMajorStops && !G.config.showStandardStops || this.map.getZoom() < 13)
        majorStop = true;

      var data = {
        majorStop: majorStop,
        justAdded: true,
        lat: lat,
        lon: lng,
        agencyId: this.model.get('agencyId')
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

      var view = this;

      this.collection.get(data.id).save(data, {
        success: function() {
          view.map.closePopup();

          G.Utils.success('Stop successfully saved');
        },
        error: function() {
          G.Utils.error('Failed to save stop');
        }
      });
    },

    // Delete and existing stop model
    destroy: function(modelId) {
      if (G.Utils.confirm(G.strings.stopsDeleteStopConfirm)) {
        this.collection.get(modelId).destroy({
          wait: true,
          success: function() {
            G.Utils.success('Stop successfully deleted');
          },
          error: function() {
            G.Utils.error('Failed to delete stop. Stops must be used by no trip patterns.');
          }
        });
      }
    }
  });
})(GtfsEditor, jQuery, ich);
