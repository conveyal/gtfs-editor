var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  G.RouteTripPatternsView = Backbone.View.extend({
    events: {
      'click .trippattern-create-btn': 'createNewTripPattern',
      'click .trippattern-create-cancel-btn': 'cancelCreateNewTripPattern',
      'click .trippattern-duplicate-btn': 'duplicateTripPattern',
      'click .trippattern-duplicate-cancel-btn': 'cancelDuplicateTripPattern',
      'click .trippattern-edit-btn': 'editTripPattern',
      'click .trippattern-edit-cancel-btn': 'cancelEditTripPattern',
      'click #create-pattern-from-alignment-btn': 'createTripPatternLine',
      'click #zoom-pattern-extent-btn': 'zoomToPatternExtent',
      'click #calc-times-from-velocity-btn': 'calcTimesFromVelocity',
      'click #clear-pattern-btn': 'clearPatternButton',
      'click #reverse-pattern-btn': 'reversePatternButton',
      'click #delete-pattern-btn': 'deletePatternButton',
      'click .trippattern-load-transitwand-btn': 'loadTransitWand',
      'submit .trippattern-create-form': 'addNewTripPattern',
      'submit .trippattern-duplicate-form': 'addDuplicateTripPattern',
      'submit .trippattern-edit-form': 'submitEditTripPattern',
      'submit .trippattern-stop-add-form': 'addStopToPattern',
      'change #trip-pattern-select': 'onTripPatternChange',
      'change #trip-pattern-stop-select': 'onTripPatternStopSelectChange',
      'change input[name="stopFilterRadio"]': 'onStopFilterChange',
      'change #transit-wand-select': 'updateTransitWandOverlay',

      'change input[name="trip-pattern-use-satellite"]': 'onSatelliteToggle'
    },


    initialize: function (opts) {
      window.rtpv = this;

      this.stopIcons = {};
      this.stopLayers = {};

      this.options = opts;

      this.impportedPattern = null;

      this.options.stops.on('add', this.onStopModelAdd, this);
      this.options.stops.on('remove', this.onModelRemove, this);
      this.options.stops.on('reset', this.onStopsReset, this);

      this.model.tripPatterns.on('add', this.onTripPatternsReset, this);
      this.model.tripPatterns.on('remove', this.onTripPatternsReset, this);
      this.model.tripPatterns.on('reset', this.onTripPatternsReset, this);
      this.model.tripPatterns.on('change:patternStops', this.onTripPatternChange, this);

      this.sizeContent();

      this.model.tripPatterns.fetch({data: {routeId: this.model.id}});

     // Custom icons
      this.agencyMajorStopIcon = L.icon({
        iconUrl: G.config.baseUrl + 'public/images/markers/marker-0d85e9.png',
        iconSize: [25, 41],
        iconAnchor: [12, 41],
        popupAnchor: [1, -34],
        shadowUrl: G.config.baseUrl + 'public/images/markers/marker-shadow.png',
        shadowSize: [41, 41],
        labelAnchor: [10, -16]
      });

      // Custom icons
      this.agencyMinorStopIcon = L.icon({
        iconUrl: G.config.baseUrl + 'public/images/markers/marker-blue-gray.png',
        iconSize: [25, 41],
        iconAnchor: [12, 41],
        popupAnchor: [1, -34],
        shadowUrl: G.config.baseUrl + 'public/images/markers/marker-shadow.png',
        shadowSize: [41, 41],
        labelAnchor: [10, -16]
      });

      this.otherStopIcon = L.icon({
        iconUrl: G.config.baseUrl + 'public/images/markers/marker-gray.png',
        iconSize: [25, 41],
        iconAnchor: [12, 41],
        popupAnchor: [1, -34],
        shadowUrl: G.config.baseUrl + 'public/images/markers/marker-shadow.png',
        shadowSize: [41, 41],
        labelAnchor: [10, -16]
      });

      this.patternStopIcon = L.icon({
        iconUrl: G.config.baseUrl + 'public/images/markers/marker-4ab767.png',
        iconSize: [25, 41],
        iconAnchor: [12, 41],
        popupAnchor: [1, -34],
        shadowUrl: G.config.baseUrl + 'public/images/markers/marker-shadow.png',
        shadowSize: [41, 41],
        labelAnchor: [10, -16]
      });

      this.selectedStopIcon = L.icon({
        iconUrl: G.config.baseUrl + 'public/images/markers/marker-dbcf2c.png',
        iconSize: [25, 41],
        iconAnchor: [12, 41],
        popupAnchor: [1, -34],
        shadowUrl: G.config.baseUrl + 'public/images/markers/marker-shadow.png',
        shadowSize: [41, 41],
        labelAnchor: [10, -16]
      });

      _.bindAll(this, 'sizeContent', 'duplicateTripPattern', 'addDuplicateTripPattern', 'cancelDuplicateTripPattern', 'editTripPattern', 'submitEditTripPattern', 'cancelEditTripPattern', 'onStopFilterChange', 'loadTransitWand', 'calcTimesFromVelocity', 'saveTripPatternLine', 'onTripPatternChange', 'onTripPatternStopSelectChange', 'updateStops', 'zoomToPatternExtent', 'clearPatternButton', 'deletePatternButton', 'stopUpdateButton', 'stopRemoveButton', 'updateTransitWandOverlay', 'onSatelliteToggle', 'stopAddAgainButton');
        $(window).resize(this.sizeContent);
    },

    render: function () {
      this.$el.html(ich['map-tpl']());

      new G.RouteSummaryView({
        el: this.$('.route-context'),
        model: this.model
      }).render();

      var sidebarData = {
        route: this.model.attributes,
        useSatellite : G.session.useSatellite
      }

      this.$('.route-sidebar').html(ich['trippatterns-sidebar-tpl'](sidebarData));
      this.$('.step-instructions').html(ich['trippatterns-instructions-tpl']());

      this.$(".collapse").collapse()


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
        center: G.session.mapCenter,
        zoom: G.session.mapZoom,
        maxZoom: 17
      });
      this.map.addLayer(baseLayer);

      // Remove default prefix
      this.map.attributionControl.setPrefix('');

      this.stopLayerGroup = L.layerGroup().addTo(this.map);

      this.transitWandOverlayGroup = L.layerGroup().addTo(this.map);


      this.drawnItems = new L.FeatureGroup();
      this.map.addLayer(this.drawnItems);

      var view = this;

      var drawControl = new L.Control.Draw({
        draw: {
          position: 'topleft',
          polygon: false,
          rectangle: false,
          circle: false,
          marker: false,
          polyline:false
        },
        edit: {
          featureGroup: this.drawnItems,
          remove: false
        }

      });
      this.map.addControl(drawControl);

      this.map.on('draw:edited', this.saveTripPatternLine);


      this.map.on('moveend', function(evt) {
        view.updateStops();
        G.session.mapCenter = view.map.getCenter();
      });

      this.map.on('popupopen', this.onPopupOpen, this);

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

      this.onTripPatternsReset();

      this.clearStops();

      this.updateStops(mapCenter);

      this.sizeContent();

      return this;
    },

    onSatelliteToggle: function(evt) {

      if($('input[name="trip-pattern-use-satellite"]').attr('checked')) {
          G.session.useSatellite  = true;
      }
      else {
          G.session.useSatellite = false;
      }

      this.render();

    },

    loadTransitWand: function(evt) {

        var view = this;

        var jqxhr = $.getJSON('http://transitwand.com/list', {unitId: $("#transit-wand-id").val()}, function(data) {

            if(data.length == 0) {
              G.Utils.error(G.strings.tripPatternNoTWData);
              return;
            }

            var patternData = {
              patterns: data
            }

            $('#transit-wand-select').append(ich['transit-wand-select-tpl'](patternData));
            $('#transit-wand-select').show();
            $('#transit-wand-id').hide();
            $('.trippattern-load-transitwand-btn').hide();

            view.updateTransitWandOverlay();


          }).fail(function() { G.Utils.error(G.strings.tripPatternTWUnknownId); });

    },

    updateTransitWandOverlay: function() {

      var view = this;


      var jqxhr = $.getJSON('http://transitwand.com/pattern', {patternId: $("#transit-wand-select").val()}, function(data) {

        view.drawnItems.clearLayers();
        view.clearStops();

        view.impportedPattern = data;

        if(data.shape != '' && data.shape != null) {

          var polyline =  new L.EncodedPolyline(data.shape, {color: 'blue'});

          polyline.addTo(view.transitWandOverlayGroup);
        }

        for(var i in data.stops) {

            var stop = data.stops[i];

            var markerLayer = L.marker([stop.lat, stop.lon], {
              draggable: false,
              icon: view.agencyMinorStopIcon
            });

            view.transitWandOverlayGroup.addLayer(markerLayer);
        }

        view.map.fitBounds(polyline.getBounds());

      }).fail(function() { G.Utils.error(G.strings.tripPatternTWUnableLoad); });

    },


    onPopupOpen: function(evt) {
      var self = this;

      // Bind the delete button inside the popup when it opens
      $(this.map.getPanes().popupPane)
        .find('.trippattern-stop-update-btn')
        .on('click', this.stopUpdateButton);

      $(this.map.getPanes().popupPane)
        .find('.trippattern-stop-remove-btn')
        .on('click', this.stopRemoveButton);

        $(this.map.getPanes().popupPane)
          .find('.trippattern-stop-add-again-btn')
          .on('click', this.stopAddAgainButton);
    },

    sizeContent: function() {
        var newHeight = $(window).height() - (175) + "px";
        $("#map").css("height", newHeight);

        if(this.map != undefined)
          this.map.invalidateSize();
    },

    updateStops: function (mapCenter) {
      var instance = this;

      if(this.map == undefined)
        return;

      // don't keep more than 500 markers on map at anytime.
      if(this.options.stops.length > 500)
        this.options.stops.remove(this.options.stops.slice(0, 200));

      var selectedPatternId  = this.$('#trip-pattern-select').val();
      if(this.model.tripPatterns.get(selectedPatternId) != undefined) {
        var patternStops = _.pluck(this.model.tripPatterns.get(selectedPatternId).attributes.patternStops, 'stopId');
        var stopsToFetch = [];
        _.each(patternStops, function (stopId) {
          var stop = instance.options.stops.get(stopId);
          if (stop !== undefined) {
            instance.onStopModelAdd(stop);
          } else {
            stopsToFetch.push(stopId);
          }
        });

        if (stopsToFetch.length > 0)
          this.options.stops.fetch({remove: false, data: {id: stopsToFetch}});
      }

      var agencyId = null;
       if($('input[name="stopFilterRadio"]:checked').val() != 'all' )
          agencyId = this.model.attributes.agencyId;

      if(G.config.showStandardStops && this.map.getZoom() >= 15) {
        var bounds = this.map.getBounds();
        var nw = bounds.getNorthWest();
        var se = bounds.getSouthEast();
        this.options.stops.fetch({remove: false, data: {agencyId: agencyId, west: nw.lng, east: se.lng, north: nw.lat, south: se.lat}});
      }

      this.updatePatternList();

      if(G.config.showMajorStops)
        this.options.stops.fetch({remove: false, data: {agencyId: agencyId, majorStops: true}});
    },


    clearStops: function() {
      this.options.stops.reset();
      //this.updatePatternList();
    },

    onStopsReset: function() {

      if(this.stopLayerGroup != undefined)
        this.stopLayerGroup.clearLayers();

      this.options.stops.each(function(model, i) {
        this.onStopModelAdd(model);
      }, this);
    },

    onModelRemove: function(model) {
      this.map.closePopup();
      this.stopLayerGroup.removeLayer(this.stopLayers[model.id]);
      this.stopLayers[model.id];
    },

    onStopModelAdd: function(model) {
      var instance = this;
      var $popupContent, markerLayer;

      var selectedPatternId  = this.$('#trip-pattern-select').val();

      var patternStopLabel = false;

      if(this.model.tripPatterns.get(selectedPatternId) !== undefined && this.model.tripPatterns.get(selectedPatternId).isPatternStop(model.id, model.agencyId)) {

        var pss = this.model.tripPatterns.get(selectedPatternId).patternStops;

        // grab all the requisite information from each of the pattern stops
        var instance = this;
        var data = _.reduce(pss, function(memo, ps, idx) {

          if (ps.stopId != model.id)
            return;

          var travelTimeStr = instance.convertTime(ps.defaultTravelTime);
          var dwellTimeStr = instance.convertTime(ps.defaultDwellTime);

          var sequenceList = [];

          for (var i = 0; i < instance.model.tripPatterns.get(selectedPatternId).get('patternStops').length; i++)
            sequenceList.push({
              sequence: i + 1
            });

          memo.push({
            travelTime: travelTimeStr,
            dwellTime: dwellTimeStr,
            patternStop: ps,
            stopSequence: idx + 1,
            sequenceList: sequenceList
          });

          return memo;
        }, []);

        $popupContent = ich['trippattern-stop-edit-form-tpl']({data: data});

        // select the correct stop sequence for each patternstop
        var selects = $popupContent.find('select[name="sequencePositionList"]');
        var stopSequences = _.map(data, function (v) { return v.stopSequence; });
        var stopSequencesBySelect = _.zip(selects, stopSequences);
        _.each(stopSequencesBySelect, function (v) {
          // _.zip seems to unwrap the jQuery object
          $(v[0]).find('option[value="' + v[1] + '"]')
          .attr('selected', true);
        });

        $popupContent.find('.trippattern-stop-update-btn').on('click', this.stopUpdateButton);

        // somehow the bootstrap data api stuff isn't working in the map popup, so we define interaction through javascript
        $popupContent.find('.accordion-toggle').click(function (e) {
          var $t = $(e.target);
          $popupContent.find($t.attr('data-target')).collapse('toggle');
        });

        patternStopLabel = this.model.tripPatterns.get(selectedPatternId).getPatternStopLabel(model.id, model.agencyId);

        // can only reasonably display dwell/travel time when there is but a single value
        if (data.length == 1)
          patternStopLabel = patternStopLabel + ' (+' + data[0].travelTime + '|+' + data[0].dwellTime + ')';

        this.stopIcons[model.id] = this.patternStopIcon;
      }
      else if (model.get('agencyId') == this.model.get('agencyId')) {

        $popupContent = ich['trippattern-stop-add-form-tpl'](model.toJSON());

        if(model.get('majorStop'))
          this.stopIcons[model.id] = this.agencyMajorStopIcon;
        else
          this.stopIcons[model.id] = this.agencyMinorStopIcon;

      }
      else {

        $popupContent = ich['stop-view-tpl'](model.toJSON());

        this.stopIcons[model.id] = this.otherStopIcon;
      }

      markerLayer = L.marker([model.get('lat'),
          model.get('lon')], {
          draggable: false,
          id: model.id,
          icon: this.stopIcons[model.id]
        });

      if(patternStopLabel) {
        markerLayer.bindLabel(patternStopLabel, { noHide: true });
      }
      else {
        markerLayer.bindLabel(model.get('stopName'));
      }

      this.stopLayers[model.id] = markerLayer;

      markerLayer.on('click', function(evt) {
        //console.log('add to the cart');

        evt.target
          .unbindPopup()
          .bindPopup($('<div>').append($popupContent).html())
          .openPopup();

      }, this);

      this.stopLayerGroup.addLayer(markerLayer);

      if(patternStopLabel)
        markerLayer.showLabel();
    },

    createNewTripPattern: function() {
      this.$('#delete-pattern-btn').hide();
      this.$('.trippattern-create-btn').hide();
      this.$('.trippattern-duplicate-btn').hide();

      this.$('#trippattern-create').html(ich['trippattern-create-tpl']());

      this.$('#trippattern-create-form').bind('submit', this.addNewTripPattern);
    },


    cancelCreateNewTripPattern: function() {
      this.$('#delete-pattern-btn').show();
      this.$('.trippattern-create-btn').show();
      this.$('.trippattern-duplicate-btn').show();

      this.$('#trippattern-create').html("");

      this.impportedPattern = null;
      this.transitWandOverlayGroup.clearLayers();

    },

    duplicateTripPattern: function() {

      var selectedPatternId  = this.$('#trip-pattern-select').val();

      if(selectedPatternId != undefined && selectedPatternId != "") {

        var data = {
              patternName: this.model.tripPatterns.get(selectedPatternId).attributes.name,
              id : selectedPatternId
        }

        this.$('#delete-pattern-btn').hide();
        this.$('.trippattern-create-btn').hide();
        this.$('.trippattern-duplicate-btn').hide();
        this.$('.trippattern-edit-btn').hide();


        this.$('#trippattern-duplicate').html(ich['trippattern-duplicate-tpl'](data));

        this.$('#trippattern-duplicate-form').bind('submit', this.duplicateTripPattern);
      }
    },

    editTripPattern: function() {

      var selectedPatternId  = this.$('#trip-pattern-select').val();

      if(selectedPatternId != undefined && selectedPatternId != "") {

        var data = {
              patternName: this.model.tripPatterns.get(selectedPatternId).attributes.name,
              id : selectedPatternId
        }

        this.$('#delete-pattern-btn').hide();
        this.$('.trippattern-create-btn').hide();
        this.$('.trippattern-duplicate-btn').hide();
        this.$('.trippattern-edit-btn').hide();


        this.$('#trippattern-duplicate').html(ich['trippattern-edit-tpl'](data));

        this.$('#trippattern-edit-form').bind('submit', this.submitEditTripPattern);
      }
    },


    cancelDuplicateTripPattern: function() {
      this.$('#delete-pattern-btn').show();
      this.$('.trippattern-create-btn').show();
      this.$('.trippattern-duplicate-btn').show();
      this.$('.trippattern-edit-btn').show();

      this.$('#trippattern-duplicate').html("");

      this.impportedPattern = null;
      this.transitWandOverlayGroup.clearLayers();

    },

    cancelEditTripPattern: function() {
      this.$('#delete-pattern-btn').show();
      this.$('.trippattern-create-btn').show();
      this.$('.trippattern-duplicate-btn').show();
      this.$('.trippattern-edit-btn').show();

      this.$('#trippattern-duplicate').html("");

    },

    addNewTripPattern: function(evt) {
      evt.preventDefault();

      if(this.$('[name=name]').val() == "") {
        G.Utils.error(G.strings.tripPatternPatternCreateFailedNoName);
        return;
      }


       var tripPatternData = {
        routeId: this.model.id,
        agencyId: this.model.get('agencyId'),
        name: this.$('[name=name]').val()
      };

      var newStops = [];
      var view = this;
      if(this.impportedPattern != null) {

        for(var i in this.impportedPattern.stops) {

            var stop = this.impportedPattern.stops[i];

            var stopData = {
              majorStop: true,
              justAdded: false,
              lat: stop.lat,
              lon: stop.lon,
              agencyId: this.model.get('agencyId')
            };

            this.options.stops.create(stopData, {
              wait: true,
              success: function(data) {
                newStops.push(data);
                // no op
              },
              error: function() {
                G.Utils.error(G.strings.tripPatternAddStopFailed);
              }
            });
        }

        tripPatternData.encodedShape = this.impportedPattern.shape;

      }

      this.model.tripPatterns.create(tripPatternData, {
        wait: true,
        success: function(data) {

            if(view.impportedPattern) {

              for(var i in newStops) {

                  view.model.tripPatterns.get(data.id).addStop({stopId: newStops[i].id, defaultTravelTime: view.impportedPattern.stops[i].travelTime, defaultDwellTime: 0});

              }

              view.model.tripPatterns.get(data.id).save();

            }

            view.impportedPattern = null;
            view.transitWandOverlayGroup.clearLayers();

            G.session.tripPattern = data.id;

            view.onTripPatternsReset();


        },
        error: function() {
          G.Utils.error(G.strings.tripPatternPatternCreateFailed);
        }
      });
    },




    addDuplicateTripPattern: function(evt) {
      evt.preventDefault();

      if(this.$('[name=name]').val() == "") {
        G.Utils.error(G.strings.tripPatternPatternCreateFailedNoName);
        return;
      }


      var originalTpData = _.clone(this.model.tripPatterns.get(this.$('[name=id]').val()).attributes);

      originalTpData.id = null;
      originalTpData.name = this.$('[name=name]').val();
      originalTpData.patternStops = _.map(originalTpData.patternStops, function(data){
        data.id = null;
        return data;
      });

      var view = this;

      this.model.tripPatterns.create(originalTpData, {
        wait: true,
        success: function(data) {


            view.impportedPattern = null;
            view.transitWandOverlayGroup.clearLayers();

            G.session.tripPattern = data.id;

            view.onTripPatternsReset();


        },
        error: function() {
          G.Utils.error(G.strings.tripPatternPatternCreateFailed);
        }
      });
    },

    submitEditTripPattern: function(evt) {
      evt.preventDefault();

      var view = this;

      if(this.$('[name=name]').val() == "") {
        G.Utils.error(G.strings.tripPatternPatternCreateFailedNoName);
        return;
      }

      var originalTp = this.model.tripPatterns.get(this.$('[name=id]').val());

      originalTp.set('name', this.$('[name=name]').val());

      originalTp.save(null, { success: function(model, response) {
         view.onTripPatternsReset();
      }});

    },


    onStopFilterChange: function(evt) {

        this.clearStops();
        this.updateStops();
    },

    onTripPatternsReset: function() {

      var tripPatterns = {
        items : this.model.tripPatterns.models
      }

      this.$('.trippattern-details').html(ich['trippatterns-details-tpl'](tripPatterns));

      this.$('#trip-pattern-select option[value="' + G.session.tripPattern + '"]')
        .attr('selected', true);

      this.onTripPatternChange();

    },

    createTripPatternLine: function () {

       var selectedPatternId  = this.$('#trip-pattern-select').val();

      var data = {
            stops : this.model.tripPatterns.get(selectedPatternId).attributes.patternStops
      }

      this.drawnItems.clearLayers();

      var polyline = L.polyline([], {color: 'red'}).addTo(this.drawnItems);

      for(var s in data.stops) {
        var id = data.stops[s].stopId;

        if(this.stopLayers[id] != undefined) {
             polyline.addLatLng(this.stopLayers[id].getLatLng());
        }
      }

      this.saveTripPatternLine();

    },

    clearTripPatternLine: function () {

       var selectedPatternId  = this.$('#trip-pattern-select').val();

      var data = {
            stops : this.model.tripPatterns.get(selectedPatternId).attributes.patternStops
      }

      this.drawnItems.clearLayers();

      var polyline = L.polyline([], {color: 'red'}).addTo(this.drawnItems);

      this.saveTripPatternLine();

    },

    saveTripPatternLine: function (e) {

      var view = this;
      this.drawnItems.eachLayer(function (layer) {
          var encodedPolyline = createEncodedPolyline(layer);
          var selectedPatternId  = this.$('#trip-pattern-select').val();

          view.model.tripPatterns.get(selectedPatternId).set('encodedShape', encodedPolyline);
          view.model.tripPatterns.get(selectedPatternId).save();

      });

    },


    onTripPatternChange: function() {

      G.session.tripPattern = this.$('#trip-pattern-select').val();

      this.clearStops();
      this.updateStops();

      this.updatePatternList();
      this.updatePatternLine();

    },

    updatePatternLine: function() {

      var selectedPatternId  = this.$('#trip-pattern-select').val();

      if(this.drawnItems == undefined || this.model.tripPatterns.get(selectedPatternId) == undefined) {

        if(this.drawnItems != undefined)
          this.drawnItems.clearLayers();

        return;
      }

      var encodedShape = this.model.tripPatterns.get(selectedPatternId).get('encodedShape');

      this.drawnItems.clearLayers();

      if(encodedShape != '' && encodedShape != null) {

        var polyline =  new L.EncodedPolyline(encodedShape, {color: 'red'});

        polyline.addTo(this.drawnItems);
      }

    },

    updatePatternList: function() {
       var selectedPatternId  = this.$('#trip-pattern-select').val();

      if( this.model.tripPatterns.get(selectedPatternId) == undefined) {

        this.$('#delete-pattern-btn').addClass("disabled");
        this.$('.trippattern-duplicate-btn').addClass("disabled");
        this.$('.trippattern-edit-btn').addClass("disabled");
        this.$('#trippattern-stop-list').html("");
        return;
      }

      this.$('#delete-pattern-btn').removeClass("disabled");
      this.$('.trippattern-duplicate-btn').removeClass("disabled");
      this.$('.trippattern-edit-btn').removeClass("disabled");


      var data = {
        stops : this.model.tripPatterns.get(selectedPatternId).attributes.patternStops
      }

      this.$('#trippattern-stop-list').html(ich['trippattern-stop-list-tpl'](data));

      this.$('#tripPattern option[value="' + G.session.tripPattern + '"]')
        .attr('selected', true);

    },

    addStopToPattern: function(evt) {
      evt.preventDefault();
      var data = G.Utils.serializeForm($(evt.target));

      var selectedPatternId  = this.$('#trip-pattern-select').val();

      if(this.model.tripPatterns.get(selectedPatternId) == undefined) {
        G.Utils.error(G.strings.tripPatternAddStopFailedNoName);
        return;
      }

      var travelTimeString  = this.$('#travel-time').val();
      var dwellTimeString  = this.$('#dwell-time').val();

      var travelTime = 0;

      this.model.tripPatterns.get(selectedPatternId).addStop({
        stopId: data.id,
        defaultTravelTime: this.calcTime(travelTimeString),
        defaultDwellTime: this.calcTime(dwellTimeString),
        timepoint: this.$('#timepoint').is(':checked')
      });
      this.model.tripPatterns.get(selectedPatternId).save();

      this.clearStops();
      this.updateStops();
    },

    zoomToPatternExtent: function(evt) {
      var instance = this;
      var selectedPatternId  = this.$('#trip-pattern-select').val();

      var latlngs = _.map(this.model.tripPatterns.get(selectedPatternId).get('patternStops'), function (ps) {
        var stop = instance.options.stops.get(ps.stopId);
        return new L.LatLng(stop.get('lat'), stop.get('lon'));
      });

      var bounds = new L.LatLngBounds(latlngs);

      this.map.fitBounds(bounds);


    },

    stopRemoveButton: function(evt) {

      var selectedPatternId  = this.$('#trip-pattern-select').val();

      var form = $(evt.target).closest('form')
      this.model.tripPatterns.get(selectedPatternId).removeStopAt(form.find('select[name="sequencePositionList"]').val() - 1);
      this.model.tripPatterns.get(selectedPatternId).save();

      this.onTripPatternChange();
    },



    stopUpdateButton: function(evt) {

      var selectedPatternId  = this.$('#trip-pattern-select').val();

      var form = $(evt.target).closest('form')
      var ps = this.model.tripPatterns.get(selectedPatternId)
        .getPatternStop(form.find('input[name="id"]').val(), form.find('input[name="stopSequence"]').val() - 1);

      var newSequence = form.find('select[name="sequencePositionList"]').val();

      ps.defaultDwellTime = this.calcTime(form.find('input[name="dwellTime"]').val());
      ps.defaultTravelTime = this.calcTime(form.find('input[name="travelTime"]').val());
      ps.timepoint = form.find('input[name="timepoint"]').is(':checked');

      this.model.tripPatterns.get(selectedPatternId).moveStopTo(ps.stopSequence -1,newSequence -1);
      this.onTripPatternChange();
      //.updatePatternStop(ps);
      this.model.tripPatterns.get(selectedPatternId).save();
      this.map.closePopup();

      //.addStop({stop: data.id, defaultTravelTime: this.calcTime(travelTimeString), defaultDwellTime: this.calcTime(dwellTimeString)});

      //$(evt.target).closest('form').find('#oringal').val();
    },

    /**
     * Add the stop to the pattern again (i.e. create a loop route)
     */
    stopAddAgainButton: function(evt) {
      var selectedPatternId  = this.$('#trip-pattern-select').val();

      var form = $(evt.target).closest('form')
      var pat = this.model.tripPatterns.get(selectedPatternId);
      // we just grab the first patternstop for this stop; we're overwriting sequence, and the
      // dwell/travel times from the first are as good an estimate as any.
      var origPs = pat.getPatternStops(form.find('input[name="id"]').val())[0];

      // create the new pattern stop
      var ps = _.extend({}, origPs);

      // update the stop sequence and save
      var patStops = pat.get('patternStops');
      // make it the last stop
      ps.id = undefined;
      patStops.push(ps);

      var instance = this;
      pat.set({patternStops: patStops});
      pat.save().done(function () {
        // re-open popup
        instance.stopLayers[ps.stop.id].fireEvent('click');
      });
    },

    clearPatternButton: function(evt) {

      if (G.Utils.confirm(G.strings.tripPatternClearPatternConfirm)) {
        var selectedPatternId  = this.$('#trip-pattern-select').val();
        this.model.tripPatterns.get(selectedPatternId).removeAllStops();
        this.clearTripPatternLine();
      }
    },

    reversePatternButton: function(evt) {

      if (G.Utils.confirm(G.strings['tripPatternReverseconfirm'])) {
        var selectedPatternId  = this.$('#trip-pattern-select').val();
        this.model.tripPatterns.get(selectedPatternId).reverse();
        this.model.tripPatterns.get(selectedPatternId).save();
        this.onTripPatternChange();
      }
    },

    onTripPatternStopSelectChange: function(evt) {

      var selectedPatternId  = this.$('#trip-pattern-select').val();
      // TODO: find the correct form control here
      var selectedStopId  = this.$('#trip-pattern-stop-select').val();

      var selectedPatternStop = this.model.tripPatterns.get(selectedPatternId).getPatternStop(selectedStopId);

      if(selectedPatternStop == undefined)
        return;

      this.map.setView(new L.LatLng(selectedPatternStop.stop.location.lat, selectedPatternStop.stop.location.lng), 15);
    },

    deletePatternButton: function(evt) {

      var selectedPatternId  = this.$('#trip-pattern-select').val();

      if (this.model.tripPatterns.get(selectedPatternId) != undefined && G.Utils.confirm(G.strings.tripPatternDeletePatternConfirm)) {
        var selectedPatternId  = this.$('#trip-pattern-select').val();
        this.model.tripPatterns.get(selectedPatternId).destroy();
      }
    },

    calcTimesFromVelocity: function(evt) {

      var selectedPatternId  = this.$('#trip-pattern-select').val();
      var velocity  = this.$('#velocity-input').val();

      var defaultDwell  = this.calcTime(this.$('#default-dwell-input').val());

      if(velocity != undefined && velocity != '') {

        // convert to m/s
        velocity = parseFloat(velocity) * 0.277778;

        var view = this;
        $.get(G.config.baseUrl + 'api/calctrippatterntimes', {id: selectedPatternId, velocity: velocity, defaultDwell: defaultDwell}, function(){

          view.model.tripPatterns.fetch({data: {routeId: view.model.id}});
        });
      }

    },


    save: function(evt) {
      evt.preventDefault();
    },

    // converts mm:ss into seconds or returns 0
    calcTime: function(timeString) {

      var time = 0;

      timeStringParts = timeString.split(":");
      if(timeStringParts.length == 2)
        time = (parseInt(timeStringParts[0]) * 60) + parseInt(timeStringParts[1]);

      return time;
    },

    // converts mm:ss into seconds or returns 0
    convertTime: function(time) {

      var seconds = time % 60;
      var minutes = (time - seconds) / 60;

      var timeStr = this._pad(minutes, 2) + ':' + this._pad(seconds, 2);

      return timeStr;
    },

    _pad: function (num, size) {
      var s = num+"";
      while (s.length < size) s = "0" + s;
      return s;
    }

  });
})(GtfsEditor, jQuery, ich);
