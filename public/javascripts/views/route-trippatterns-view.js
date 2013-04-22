var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  G.RouteTripPatternsView = Backbone.View.extend({
    events: {
      'click .trippattern-create-btn': 'createNewTripPattern',
      'click .trippattern-create-cancel-btn': 'cancelCreateNewTripPattern',
      'click #create-pattern-from-alignment-btn': 'createTripPatternLine',
      'click #zoom-pattern-extent-btn': 'zoomToPatternExtent',
      'click #calc-times-from-velocity-btn': 'calcTimesFromVelocity',
      'click #clear-pattern-btn': 'clearPatternButton',
      'click #delete-pattern-btn': 'deletePatternButton',
      'click trippattern-load-transitwand-btn': 'showLoadTransitWand',
      'submit .trippattern-create-form': 'addNewTripPattern',
      'submit .trippattern-stop-add-form': 'addStopToPattern',
      'change #trip-pattern-select': 'onTripPatternChange',
      'change #trip-pattern-stop-select': 'onTripPatternStopSelectChange',      
      'change input[name="stopFilterRadio"]': 'onStopFilterChange'

    },

    initialize: function () {

      this.stopIcons = {};
      this.stopLayers = {};

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
        iconUrl: '/public/images/markers/marker-0d85e9.png',
        iconSize: [25, 41],
        iconAnchor: [12, 41],
        popupAnchor: [1, -34],
        shadowUrl: '/public/images/markers/marker-shadow.png',
        shadowSize: [41, 41],
        labelAnchor: [10, -16]
      });

      // Custom icons
      this.agencyMinorStopIcon = L.icon({
        iconUrl: '/public/images/markers/marker-blue-gray.png',
        iconSize: [25, 41],
        iconAnchor: [12, 41],
        popupAnchor: [1, -34],
        shadowUrl: '/public/images/markers/marker-shadow.png',
        shadowSize: [41, 41],
        labelAnchor: [10, -16]
      });

      this.otherStopIcon = L.icon({
        iconUrl: '/public/images/markers/marker-gray.png',
        iconSize: [25, 41],
        iconAnchor: [12, 41],
        popupAnchor: [1, -34],
        shadowUrl: '/public/images/markers/marker-shadow.png',
        shadowSize: [41, 41],
        labelAnchor: [10, -16]
      });

      this.patternStopIcon = L.icon({
        iconUrl: '/public/images/markers/marker-4ab767.png',
        iconSize: [25, 41],
        iconAnchor: [12, 41],
        popupAnchor: [1, -34],
        shadowUrl: '/public/images/markers/marker-shadow.png',
        shadowSize: [41, 41],
        labelAnchor: [10, -16]
      });

      this.selectedStopIcon = L.icon({
        iconUrl: '/public/images/markers/marker-dbcf2c.png',
        iconSize: [25, 41],
        iconAnchor: [12, 41],
        popupAnchor: [1, -34],
        shadowUrl: '/public/images/markers/marker-shadow.png',
        shadowSize: [41, 41],
        labelAnchor: [10, -16]
      });

      _.bindAll(this, 'sizeContent', 'onStopFilterChange', 'showLoadTransitWand', 'calcTimesFromVelocity', 'saveTripPatternLine', 'onTripPatternChange', 'onTripPatternStopSelectChange', 'updateStops', 'zoomToPatternExtent', 'clearPatternButton', 'deletePatternButton', 'stopUpdateButton', 'stopRemoveButton');
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
      }

      this.$('.route-sidebar').html(ich['trippatterns-sidebar-tpl'](sidebarData));
      this.$('.step-instructions').html(ich['trippatterns-instructions-tpl']());

      this.$(".collapse").collapse() 

      this.onTripPatternsReset();

      // Base layer config is optional, default to Mapbox Streets
      var url = 'http://{s}.tiles.mapbox.com/v3/' + G.config.mapboxKey + '/{z}/{x}/{y}.png',
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

      this.clearStops();

      this.updateStops(mapCenter);

      this.sizeContent();

      return this;
    },

    showLoadTransitWand: function(evt) {

    },

    onPopupOpen: function(evt) {
      var self = this;

      // Bind the delete button inside the popup when it opens
      $(this.map.getPanes().popupPane)
        .find('#trippattern-stop-update-btn')
        .on('click', this.stopUpdateButton);

      $(this.map.getPanes().popupPane)
        .find('#trippattern-stop-remove-btn')
        .on('click', this.stopRemoveButton);
    },

    sizeContent: function() {
        var newHeight = $(window).height() - (175) + "px";
        $("#map").css("height", newHeight);

        if(this.map != undefined)
          this.map.invalidateSize();
    },

    updateStops: function (mapCenter) {

      if(this.map == undefined)
        return;

      // don't keep more than 500 markers on map at anytime. 
       if(this.options.stops.length > 500)
          this.options.stops.remove(this.options.stops.slice(0, 200));
      
      var agencyId = null;
       if($('input[name="stopFilterRadio"]:checked').val() != 'all' )
          agencyId = this.model.attributes.agency.id;

      if(G.config.showStandardStops && this.map.getZoom() >= 15) {
          if(mapCenter == null)
            mapCenter = this.map.getCenter();

          this.options.stops.fetch({remove: false, data: {agencyId: agencyId, lat: mapCenter.lat, lon: mapCenter.lng}});
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
      var markerLayer;

      var $popupContent;

      var selectedPatternId  = this.$('#trip-pattern-select').val();

      var patternStopLabel = false;

      if(this.model.tripPatterns.get(selectedPatternId) != undefined && this.model.tripPatterns.get(selectedPatternId).isPatternStop(model.id)) {

        var ps = this.model.tripPatterns.get(selectedPatternId).getPatternStop(model.id)

        var travelTimeStr = this.convertTime(ps.defaultTravelTime);
        var dwellTimeStr = this.convertTime(ps.defaultDwellTime);

        var sequenceList = []

        for(var i = 1; i <= this.model.tripPatterns.get(selectedPatternId).get('patternStops').length; i++)
          sequenceList.push({sequence: i});


        var data = {
          travelTime: travelTimeStr,
          dwellTime: dwellTimeStr,
          patternStop: ps,
          sequenceList: sequenceList
        };

        $popupContent = ich['trippattern-stop-edit-form-tpl'](data);

        $popupContent
          .find('#sequence-position-list option[value="'+ps.stopSequence+'"]')
          .attr('selected', true);

        $popupContent.find('#trippattern-stop-update-btn').on('click', this.stopUpdateButton);

        patternStopLabel = this.model.tripPatterns.get(selectedPatternId).getPatternStopLabel(model.id);

        patternStopLabel = patternStopLabel + ' (+' + travelTimeStr + '|+' + dwellTimeStr + ')';

        this.stopIcons[model.id] = this.patternStopIcon;
      }
      else if (model.get('agency').id == this.model.get('agency').id) {

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

      markerLayer = L.marker([model.get('location').lat,
          model.get('location').lng], {
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

      this.$('#trippattern-create').html(ich['trippattern-create-tpl']());
    },


    cancelCreateNewTripPattern: function() {
      this.$('#delete-pattern-btn').show();
      this.$('.trippattern-create-btn').show();

      this.$('#trippattern-create').html("");
    },

    addNewTripPattern: function(evt) {
      evt.preventDefault();

      if(this.$('[name=name]').val() == "") {
        G.Utils.error('Failed to create trip pattern, please enter a name.');
        return;
      }


       var data = {
        route: this.model,
        name: this.$('[name=name]').val()
      };

      this.model.tripPatterns.create(data, {
        wait: true,
        success: function() {
          G.Utils.success('Trip pattern successfully created');
        },
        error: function() {
          G.Utils.error('Failed to create trip pattern');
        }
      });

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
        var id = data.stops[s].stop.id
        
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
        this.$('#trippattern-stop-list').html("");
        return;      
      }

      this.$('#delete-pattern-btn').removeClass("disabled");
      
      var data = {
        stops : this.model.tripPatterns.get(selectedPatternId).attributes.patternStops
      }

      this.$('#trippattern-stop-list').html(ich['trippattern-stop-list-tpl'](data));

    },

    addStopToPattern: function(evt) {
      evt.preventDefault();
      var data = G.Utils.serializeForm($(evt.target));
    
      var selectedPatternId  = this.$('#trip-pattern-select').val();

      if(this.model.tripPatterns.get(selectedPatternId) == undefined) {
        G.Utils.error("Select/create pattern before adding stops.")
        return;
      }

      var travelTimeString  = this.$('#travel-time').val();
      var dwellTimeString  = this.$('#dwell-time').val();

      var travelTime = 0;

      this.model.tripPatterns.get(selectedPatternId).addStop({stop: data.id, defaultTravelTime: this.calcTime(travelTimeString), defaultDwellTime: this.calcTime(dwellTimeString)});
      this.model.tripPatterns.get(selectedPatternId).save();

      this.clearStops();
      this.updateStops();
    },

    zoomToPatternExtent: function(evt) {

      var selectedPatternId  = this.$('#trip-pattern-select').val();

      var latlngs = [];
      for(var stop in this.model.tripPatterns.get(selectedPatternId).attributes.patternStops) {

        var latlng = new L.LatLng(this.model.tripPatterns.get(selectedPatternId).attributes.patternStops[stop].stop.location.lat, this.model.tripPatterns.get(selectedPatternId).attributes.patternStops[stop].stop.location.lng);
        latlngs.push(latlng);
      }

      var bounds = new L.LatLngBounds(latlngs);

      this.map.fitBounds(bounds);


    },

    stopRemoveButton: function(evt) {

      var selectedPatternId  = this.$('#trip-pattern-select').val();

      var form = $(evt.target).closest('form')
      var ps = this.model.tripPatterns.get(selectedPatternId).getPatternStop(form.find('input[name="id"]').val());   

      this.model.tripPatterns.get(selectedPatternId).removeStopAt(ps.stopSequence  -1);
      this.model.tripPatterns.get(selectedPatternId).save();

      this.onTripPatternChange();
    },



    stopUpdateButton: function(evt) {

      var selectedPatternId  = this.$('#trip-pattern-select').val();

      var form = $(evt.target).closest('form')
      var ps = this.model.tripPatterns.get(selectedPatternId).getPatternStop(form.find('input[name="id"]').val());

      var newSequence = form.find('#sequence-position-list').val();

      ps.defaultDwellTime = this.calcTime(form.find('input[name="dwellTime"]').val());
      ps.defaultTravelTime = this.calcTime(form.find('input[name="travelTime"]').val());

      this.model.tripPatterns.get(selectedPatternId).moveStopTo(ps.stopSequence -1,newSequence -1);
      this.onTripPatternChange();
      //.updatePatternStop(ps);
      this.model.tripPatterns.get(selectedPatternId).save();

      //.addStop({stop: data.id, defaultTravelTime: this.calcTime(travelTimeString), defaultDwellTime: this.calcTime(dwellTimeString)});

      //$(evt.target).closest('form').find('#oringal').val();
    },

    clearPatternButton: function(evt) {

      if (G.Utils.confirm('Are you sure you want to clear stops from this pattern?')) {
        var selectedPatternId  = this.$('#trip-pattern-select').val();
        this.model.tripPatterns.get(selectedPatternId).removeAllStops();
        this.clearTripPatternLine();
      }
    },

    onTripPatternStopSelectChange: function(evt) {

      var selectedPatternId  = this.$('#trip-pattern-select').val();
      var selectedStopId  = this.$('#trip-pattern-stop-select').val();   
      
      var selectedPatternStop = this.model.tripPatterns.get(selectedPatternId).getPatternStop(selectedStopId);

      if(selectedPatternStop == undefined) 
        return;

      this.map.setView(new L.LatLng(selectedPatternStop.stop.location.lat, selectedPatternStop.stop.location.lng), 15);
    },

    deletePatternButton: function(evt) {

      var selectedPatternId  = this.$('#trip-pattern-select').val();

      if (this.model.tripPatterns.get(selectedPatternId) != undefined && G.Utils.confirm('Are you sure you want to delete this pattern?')) {
        var selectedPatternId  = this.$('#trip-pattern-select').val();
        this.model.tripPatterns.get(selectedPatternId).destroy();
      }
    },

    calcTimesFromVelocity: function(evt) {

      var selectedPatternId  = this.$('#trip-pattern-select').val();
      var velocity  = this.$('#velocity-input').val();

      if(velocity != undefined && velocity != '') {

        // convert to m/s 
        velocity = parseFloat(velocity) * 0.277778;

        var view = this;
        $.get('/api/calctrippatterntimes', {id: selectedPatternId, velocity: velocity}, function(){

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

