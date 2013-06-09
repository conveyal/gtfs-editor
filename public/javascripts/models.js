var GtfsEditor = GtfsEditor || {};

(function(G, $) {

  G.Agency = Backbone.Model.extend({
    urlRoot: '/api/agency/',
    
    defaults: {
      id: null,
      gtfsAgencyId: null,
      name: null,
      url: null,
      timezone: null,
      lang: null,
      phone: null,
      defaultLat: null,
      defaultLon: null,
      defaultRouteType: null
    }
  });

  G.Agencies = Backbone.Collection.extend({
    type: 'Agencies',
    model: G.Agency,
    url: '/api/agency/'
  });

G.RouteType = Backbone.Model.extend({
    urlRoot: '/api/routetype/',

    defaults: {
      id: null,
      gtfsRouteType: null,
      hvtRouteType: null,
      localizedVehicleType: null,
      description: null
    }
  });

G.RouteTypes = Backbone.Collection.extend({
    type: 'RouteTypes',
    model: G.RouteType,
    url: '/api/routetype/'
  });


  G.Route = Backbone.Model.extend({
    defaults: {
      id: null,
      status: 'IN_PROGRESS',
      publiclyVisible: false,
      gtfsRouteId: null,
      routeShortName: null,
      routeLongName: null,
      routeDesc: null,
      routeType: null,
      routeUrl: null,
      routeColor: 'FFFFFF',
      routeTextColor: '000000',
      agency: null
    },

    initialize: function() {
      this.tripPatterns = new G.TripPatterns();
    }
  });

  G.Routes = Backbone.Collection.extend({
    type: 'Routes',
    model: G.Route,
    url: '/api/route/'
  });

  G.Stop = Backbone.Model.extend({
    defaults: {
      id: null,
      gtfsStopId: null,
      stopCode: null,
      stopName: null,
      stopDesc: null,
      zoneId: null,
      stopUrl: null,
      agency: null,
      locationType: null,
      parentStation: null,
      bikeParking: null,
      wheelchairBoarding: null,
      majorStop: false,
      location: null
    },

    blacklist: ['justAdded','lat','lng'],
    toJSON: function(options) {
        return _.omit(this.attributes, this.blacklist);
    },

    set: function(attributes, options) {    
            //my custom code
        var finalAttributes = _.omit(attributes, this.blacklist);

        if(attributes.lat != undefined &&  attributes.lng != undefined)
          finalAttributes['location'] = {lat: attributes.lat, lng: attributes.lng};

        return Backbone.Model.prototype.set.call(this, finalAttributes, options);
    }
  });

  G.Stops = Backbone.Collection.extend({
    type: 'Stops',
    model: G.Stop,
    url: '/api/stop/'
  });

  G.StopGroup = Backbone.Model.extend({
    defaults: {
      id: null,
      mergedStop: null,
      stops: []
    },
    initialize: function() {
       this.set('stops', []);

       _.bindAll(this, 'setMergedStop', 'addStop', 'addGroup', 'merge', 'onFinishedMerge');
    },

    addStop: function(stop) {

      if(this.existingStops == undefined) {
        this.existingStops = {}
      }

      // make first stop the merged stop by default
      if(this.get('mergedStop') == null)
        this.set('mergedStop', stop);

      // don't add duplicate stops
      if(!this.existingStops[stop.id]) {

        this.existingStops[stop.id] = true;
        
        var stops = this.get('stops');
        stops.push(stop);
        
        this.set('stops', stops);
      }

    },

    setMergedStop: function(stop) {
        this.addStop(stop);
        this.set('mergedStop', stop);
    },

    addGroup: function(group){

      var existingGroup = this;

      // merge stops into group
      _.each(group.stops, function(stop){
        if(!_.contains(existingStops, stop.id)) {
          existingGroup.addStop(stop);
        }
      });
    },

    onFinishedMerge: function(evt) {
      this.trigger('merge');
    },

    merge: function() {


        if(this.get('mergedStop') != null && this.get('stops').length > 0) {

          var ids = _.without(_.pluck(this.get('stops'), 'id'), this.get('mergedStop').id);
          var idList = ids.join(',');

          $.get('/api/mergeStops', {stop1Id: this.get('mergedStop').id, mergedStopIds: idList}, this.onFinishedMerge);  
        }

    }

  });

  G.StopGroups = Backbone.Collection.extend({
    type: 'StopGroups',
    model: G.StopGroup,

    initialize: function(opts) {
      
      this.agencyId = opts.agencyId;
      this.success = opts.success;
      this.groupMap = {};

      this.on('merge', this.onMerge);

      _.bindAll(this, 'loadGroups', 'group', 'findDuplicateStops', 'onMerge');
    },

    onMerge: function(evt) {
      this.findDuplicateStops();
    },

    findDuplicateStops: function() {

      this.reset();
      this.groupMap = {};

      $.get('/api/findDuplicateStops', {agencyId: this.agencyId}, this.loadGroups);
    },

    loadGroups: function(pairs) {

      this.success();

      var _stopGroups = this;

      _.each(pairs, function(pair) {

        _stopGroups.group(new G.Stop(pair[0]), new G.Stop(pair[1]));

      });
    },

    merge: function(groupId) {

      this.groupMap[groupId].merge();

    },

    group: function(stop1, stop2){
      
      if(this.groupMap[stop1.id] == undefined) {
        if(this.groupMap[stop2.id] != undefined) {
          // add stop1 to existing group for stop2
          (this.groupMap[stop2.id]).addStop(stop1);
        }
        else {
          // add stop1 and stop2 to new group
          this.groupMap[stop1.id] = new G.StopGroup();
          (this.groupMap[stop1.id]).addStop(stop1);
          (this.groupMap[stop1.id]).addStop(stop2);

          this.add(this.groupMap[stop1.id]);
        }
      }
      else {
        if(this.groupMap[stop2.id] == undefined) {
          // add stop2 to existing group for stop1
          this.groupMap[stop1.id].addStop(stop2);
        }
        else {
          // both stop1 and stop2 belong to existing group, merge stop2 group into stop1 group
          this.groupMap[stop1.id].addGroup(this.groupMap[stop2.id]);
        }
      }
    }

  });

  G.TripPattern = Backbone.Model.extend({
    defaults: {
      id: null,
      name: null,
      headsign: null,
      encodedShape: null,
      patternStops: [],
      shape: null,
      route: null
    },

    initialize: function() {
      this.on('change:patternStops', this.normalizeSequence, this);

      this.trips = new G.Trips({patternId: this.id});

      this.sortPatternStops();
    },

    getPatternStop: function(stopId) {
      return this.isPatternStop(stopId);
    },

    isPatternStop: function(stopId) {
      var isPatternStop = false;
      _.each(this.get('patternStops'), function(ps, i) {
        if(ps.stop.id == stopId) {
          isPatternStop = ps;
        }
      });
      return isPatternStop;
    },

    getPatternStopLabel: function(stopId) {
      var stopsSequences = [];
      _.each(this.get('patternStops'), function(ps, i) {
        if(ps.stop.id == stopId) {
          stopsSequences.push(ps.stopSequence);
        }
      });
      return stopsSequences.join(" & ");
    },

    reverse: function() {

      var patternStops = this.get('patternStops');
      var newPatternStops = new Array();

      _.each(patternStops, function(stop, i){
        if(i+1 < patternStops.length) {
          stop.defaultTravelTime = patternStops[i+1].defaultTravelTime;
          stop.defaultDwellTime = patternStops[i+1].defaultDwellTime;
        }

        if(patternStops.length == i+1) {
          stop.defaultTravelTime = null;
          stop.defaultDwellTime = null;
        }
        
        newPatternStops.push(stop) 
      }); 

      newPatternStops.reverse();

      this.set('patternStops', newPatternStops);

      this.normalizeSequence();

      var latlngs = (new L.EncodedPolyline(this.get('encodedShape'))).getLatLngs();

      latlngs.reverse();

      var reversedLine = createEncodedPolyline(L.polyline(latlngs));

      this.get('encodedShape', reversedLine);

    },
  
    sortPatternStops: function() {
      var patternStops = _.sortBy(this.get('patternStops'), function(ps){
        return ps.stopSequence;
      });

      this.set('patternStops', patternStops, {silent: true});
    },

    normalizeSequence: function () {
      _.each(this.get('patternStops'), function(ps, i) {
        ps.stopSequence = i+1;
      });
      this.sortPatternStops();
      //this.save();
    },

    validate: function(attrs) {
      // Override the sequence value to match the array order
      this.sortPatternStops();
    },
    // name, headsign, alignment, stop_times[], shape, route_id (fk)
      // stop_id, travel_time, dwell_time

    addStop: function(stopTime) {
      var patternStops = this.get('patternStops');
      patternStops.push(stopTime);
      this.set('patternStops', patternStops);
      this.normalizeSequence();
    },

    insertStopAt: function(stopTime, i) {
      var patternStops = this.get('patternStops');
      patternStops.splice(i, 0, stopTime);
      this.set('patternStops', patternStops);
      this.normalizeSequence();
    },

    removeStopAt: function(i) {
      var patternStops = this.get('patternStops'),
          removed = patternStops.splice(i, 1)[0];
      this.set('patternStops', patternStops);
      this.normalizeSequence();
      return removed;
    },

    removeAllStops: function() {
      this.set('patternStops', []);
    },

    moveStopTo: function(fromIndex, toIndex) {
      var stopTimes = this.get('patternStops'),
          stopTime;

      stopTime = this.removeStopAt(fromIndex);
      this.insertStopAt(stopTime, toIndex);
      this.normalizeSequence();
    },
    updatePatternStop: function(data) {
      var patternStops = this.get('patternStops');
      this.removeAllStops();
      this.save();
      
      patternStops[data.stopSequence] = data;
      this.set('patternStops', patternStops);
      this.save();
    }
  });

  G.TripPatterns = Backbone.Collection.extend({
    type: 'TripPatterns',
    model: G.TripPattern,
    url: '/api/trippattern/'
  });

  G.Calendar = Backbone.Model.extend({
    defaults: {
      id: null,
      agency: null,
      description: null,
      gtfsServiceId: null,
      monday: null,
      tuesday: null,
      wednesday: null,
      thursday: null,
      friday: null,
      saturday: null,
      sunday: null,
      startDate: null,
      endDate: null
    }
    // days, start_date, end_date, exceptions[]
  });


  G.Calendars = Backbone.Collection.extend({
    type: 'Calendars',
    model: G.Calendar,
    url: '/api/calendar/'
  });

G.Trip = Backbone.Model.extend({
    defaults: {
      tripDescription: null,
      pattern: null,
      serviceCalendar: null,
      useFrequency: null,
      startTime: null,
      endTime: null,
      headway: null,
    }
   });

  G.Trips = Backbone.Collection.extend({
    type: 'Trips',
    model: G.Trip,
    url: '/api/trip/',
    
    initialize: function(opts) {
      if(opts != undefined)
        this.patternId  = opts.patternId;
    },

    fetchTrips: function() {
      this.fetch({data: {patternId: this.patternId}});
    }

  });

})(GtfsEditor, jQuery);
                                                                