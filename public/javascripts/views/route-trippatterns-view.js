var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  G.RouteTripPatternsView = Backbone.View.extend({
    events: {
      'click .trippattern-create-btn': 'createNewTripPattern',
      'click .trippattern-create-cancel-btn': 'cancelCreateNewTripPattern',
      'click .trippattern-create-line-btn': 'createLine',
      'submit .trippattern-create-form': 'addNewTripPattern',
      'submit .trippattern-stop-details-form': 'addStopToPattern',
      'change #selectedTripPattern': 'onTripPatternStopChange'

    },

    initialize: function () {

      this.stopIcons = {};
      this.stopLayers = {};

      this.options.stops.on('add', this.onStopModelAdd, this);
      this.options.stops.on('remove', this.onModelRemove, this);
      this.options.stops.on('reset', this.onStopsReset, this);

      this.model.tripPatterns.on('add', this.onTripPatternsReset, this);
      this.model.tripPatterns.on('reset', this.onTripPatternsReset, this);
      this.model.tripPatterns.on('change:patternStops', this.onTripPatternStopChange, this);

      this.sizeContent();

      this.model.tripPatterns.fetch({data: {routeId: this.model.id}});

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

      _.bind(this.onTripPatternStopChange, this.updateStops);

      _.bindAll(this, 'sizeContent');
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
        tripPatterns: {
              items : this.model.attributes.tripPatterns
      }}

      this.$('.route-sidebar').html(ich['trippatterns-sidebar-tpl'](sidebarData));
      this.$('.step-instructions').html(ich['trippatterns-instructions-tpl']());

      this.$(".collapse").collapse() 

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

      var drawControl = new L.Control.Draw({
        draw: {
          position: 'topleft',
          polygon: false,
          rectangle: false,
          circle: false,
          marker: false,
          polyline: {
            title: 'Draw a route alignment.'
          }
        },
        edit: {
          featureGroup: this.drawnItems
        }
      });
      this.map.addControl(drawControl);

      this.map.on('draw:created', function (e) {
        var type = e.layerType,
          layer = e.layer;

        if (type === 'marker') {
              layer.bindPopup('A popup!');
            }

            drawnItems.addLayer(layer);
      });

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

      $('input[name="stopFilterRadio"]').change( function() {
          view.clearStops();
          view.updateStops();
      });

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
        

      if(G.config.showMajorStops)
        this.options.stops.fetch({remove: false, data: {agencyId: agencyId, majorStops: true}});

       this.onTripPatternStopChange();
    },


    clearStops: function() {
      this.options.stops.reset();

      this.onTripPatternStopChange();
    },

    onStopsReset: function() {
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

      markerLayer.on('click', function(evt) {
        //console.log('add to the cart');

        evt.target
          .unbindPopup()
          .bindPopup($('<div>').append(ich['trippattern-stop-form-tpl'](model.toJSON())).html())
          .openPopup();

      }, this);

      this.stopLayerGroup.addLayer(markerLayer);
    },

    createNewTripPattern: function() {
      this.$('.trippattern-create-btn').hide();

      this.$('#trippattern-create').html(ich['trippattern-create-tpl']());
    },


    cancelCreateNewTripPattern: function() {
      this.$('.trippattern-create-btn').show();

      this.$('#trippattern-create').html("");
    },

    addNewTripPattern: function(evt) {
      evt.preventDefault();

       var data = {
        route: this.model,
        name: this.$('[name=name]').val()
      };

      this.model.tripPatterns.create(data, {
        wait: true,
        success: function() {
          //G.Utils.success('Trip pattern successfully created');
        },
        error: function() {
          //G.Utils.error('Failed to create trip pattern');
        }
      });

    },

    onTripPatternsReset: function() {
      
      var tripPatterns = {
            items : this.model.tripPatterns.models
      }

      this.$('.route-sidebar').html(ich['trippatterns-sidebar-tpl'](tripPatterns));

    },

    createLine: function () {

       var selectedPatternId  = this.$('#selectedTripPattern').val();

      var data = {
            stops : this.model.tripPatterns.get(selectedPatternId).collection.models[0].attributes.patternStops
      }

      var polyline = L.polyline([], {color: 'red'}).addTo(this.drawnItems );

      for(var s in data.stops) {
        var id = data.stops[s].stop.id
        
        if(this.stopLayers[id] != undefined) {
             polyline.addLatLng(this.stopLayers[id].getLatLng());
        }
      }

    },


    onTripPatternStopChange: function() {

      var selectedPatternId  = this.$('#selectedTripPattern').val();

      if( this.model.tripPatterns.get(selectedPatternId) == undefined)
        return;

      var data = {
            stops : this.model.tripPatterns.get(selectedPatternId).collection.models[0].attributes.patternStops
      }

      for(var s in data.stops) {
        var id = data.stops[s].stop.id
        
        if(this.stopLayers[id] != undefined) {
           this.stopLayers[id].setIcon(this.patternStopIcon);  
        }
      }

      if(this.model.tripPatterns.get(selectedPatternId).collection.length) {

        this.$('#trippattern-sequence-message').hide();
        this.$('#trippattern-sequence-list').show();
        this.$('#trippattern-sequence-list').html(ich['trippattern-stop-list-tpl'](data));
      }
      else {

        this.$('#trippattern-sequence-message').show();

      }      

    },

    addStopToPattern: function(evt) {
      evt.preventDefault();
      var data = G.Utils.serializeForm($(evt.target));
    
      var selectedPatternId  = this.$('#selectedTripPattern').val();

      this.stopLayers[data.id].setIcon(this.patternStopIcon);

      this.model.tripPatterns.get(selectedPatternId).addStop({stop: data.id})
    },

    save: function(evt) {
      evt.preventDefault();
    }
  });
})(GtfsEditor, jQuery, ich);

