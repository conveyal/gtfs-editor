var GtfsEditor = GtfsEditor || {};

(function(G, $) {

  G.Agency = Backbone.Model.extend({
    urlRoot: G.config.baseUrl + 'api/agency/',

    defaults: {
      id: null,
      gtfsAgencyId: null,
      name: null,
      url: null,
      timezone: null,
      lang: null,
      phone: null,
      defaultLat: null,
      defaultLon: null
    }
  });

  G.Agencies = Backbone.Collection.extend({
    type: 'Agencies',
    model: G.Agency,
    url: G.config.baseUrl + 'api/agency/'
  });

G.RouteType = Backbone.Model.extend({
    urlRoot: G.config.baseUrl + 'api/routetype/',

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
    url: G.config.baseUrl + 'api/routetype/'
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
      routeTypeId: null,
      routeUrl: null,
      routeColor: 'FFFFFF',
      routeTextColor: '000000',
      agencyId: null
    },

    initialize: function() {
      this.tripPatterns = new G.TripPatterns();
    }
  });

  G.Routes = Backbone.Collection.extend({
    type: 'Routes',
    model: G.Route,
    url: G.config.baseUrl + 'api/route/'
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
      agencyId: null,
      locationType: null,
      parentStation: null,
      bikeParking: null,
      carParking: null,
      wheelchairBoarding: null,
      majorStop: false,
      lat: null,
      lon: null
    },

    blacklist: ['justAdded'],
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
    url: G.config.baseUrl + 'api/stop/'
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

          $.get(G.config.baseUrl + 'api/mergeStops', {stop1Id: this.get('mergedStop').id, mergedStopIds: idList}, this.onFinishedMerge);
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

      $.get(G.config.baseUrl + 'api/findDuplicateStops', {agencyId: this.agencyId}, this.loadGroups);
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
      shape: null,
      patternStops: [],
      routeId: null,
      agencyId: null
    },

    urlRoot: G.config.baseUrl + 'api/trippattern/',

    initialize: function() {
      this.trips = new G.Trips({patternId: this.id});
    },

    blacklist: ['stopConnections'],
    toJSON: function(options) {
      return _.omit(this.attributes, this.blacklist);
    },

    /**
     * Get all of the pattern stops for this stop and agency
     */
    getPatternStops: function(stopId, agencyId) {
      return _.filter(this.get('patternStops'), function (ps) {
        return ps.stopId == stopId && ps.agencyId == agencyId;
      });
    },

    getStopSequences: function (stopId) {
      var stopSequences = [];

      _.each(this.get('patternStops'), function (ps, i) {
        if (ps.stopId == stopId)
          stopSequences.put(i);
      });
      return stopSequences;
    },

    /** is this stop/agency a pattern stop on this pattern */
    isPatternStop: function (stopId) {
      return this.getPatternStops(stopId).length > 0;
    },

    getPatternStopLabel: function(stopId) {
      var stopsSequences = [];
      _.each(this.get('patternStops'), function(ps, i) {
        if(ps.stopId == stopId) {
          stopsSequences.push(i + 1);
        }
      });
      return stopsSequences.join(" & ");
    },

    reverse: function() {

      var patternStops = this.get('patternStops');
      patternStops.reverse();
      this.set('patternStops', patternStops);

      var latlngs = (new L.EncodedPolyline(this.get('shape'))).getLatLngs();
      latlngs.reverse();
      var reversedLine = createEncodedPolyline(L.polyline(latlngs));
      this.set('shape', reversedLine);
    },

    addStop: function(stopTime) {
      var patternStops = this.get('patternStops');
      patternStops.push(stopTime);
      this.set('patternStops', patternStops);
    },

    insertStopAt: function(stopTime, i) {
      var patternStops = this.get('patternStops');
      patternStops.splice(i, 0, stopTime);
      this.set('patternStops', patternStops);
    },

    removeStopAt: function(i) {
      var patternStops = this.get('patternStops'),
          removed = patternStops.splice(i, 1)[0];
      this.set('patternStops', patternStops);
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
    },

    useFrequency: function() {
      this.trips.clearTrips();
      this.set('useFrequency', true);
      this.save();
    },

    useTimetable: function() {
      this.trips.clearTrips();
      this.set('useFrequency', false);
      this.save()
    }

  });

  G.TripPatterns = Backbone.Collection.extend({
    type: 'TripPatterns',
    model: G.TripPattern,
    url: G.config.baseUrl + 'api/trippattern/'
  });

  G.Calendar = Backbone.Model.extend({
    defaults: {
      id: null,
      agencyId: null,
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
      endDate: null,
      numberOfTrips: 0,
      routes: []
    },
    urlRoot: G.config.baseUrl + 'api/calendar/',
    // days, start_date, end_date, exceptions[]

    getRoutesText: function () {
      var routes = this.get('routes');
      if (routes.length > 5) {
        routes = routes.slice(0, 5);
        routes.push('...');
      }

      return routes.join(', ');
    }
  });


  G.Calendars = Backbone.Collection.extend({
    type: 'Calendars',
    model: G.Calendar,
    url: G.config.baseUrl + 'api/calendar/',

    /** get text labels for the routes in each calendar */
    getRoutesText: function () {
      this.each(function (model) {
        model.set('routesText', model.getRoutesText());
      });
    }
  });

G.Trip = Backbone.Model.extend({
    defaults: {
      tripDescription: null,
      patternId: null,
      calendarId: null,
      startTime: null,
      endTime: null,
      headway: null,
      useFrequency: null
    },

    clone: function () {
      var ret = new G.Trip(this.toJSON());

      // wipe out the IDs
      ret.set('id', null);
      ret.set('gtfsTripId', null);
      ret.set('blockId', null);

      // clone the stop times
      var stopTimes = _.map(this.get('stopTimes'), function (st) {
        if (st === null)
          return null;

        // clone
        st = _.extend({}, st);
        return st;
      });

      ret.set('stopTimes', stopTimes);

      return ret;
    }
   });

  G.Trips = Backbone.Collection.extend({
    type: 'Trips',
    model: G.Trip,
    url: G.config.baseUrl + 'api/trip/',

    comparator: function (trip) {
      var sts = trip.get('stopTimes');
      if (!_.isUndefined(sts) && sts != null && sts.length > 0) {
        return sts[0].departureTime;
      }
      // 500 hours past midnight should put these trips at the end
      // the longest train journey is Moscow to Pyongyang (210 h), per Wikipedia
      return 500 * 60 * 60;
    },


    initialize: function(opts) {
      if(opts != undefined)
        this.patternId  = opts.patternId;
    },

    fetchTrips: function(onSuccess) {
      if(onSuccess)
        this.fetch({data: {patternId: this.patternId}, success: onSuccess});
      else
        this.fetch({data: {patternId: this.patternId}});
    },

    clearTrips: function() {
      var _this = this;

      var deleteTripModels = function() {
        var model;

        while (model = _this.first()) {
          model.destroy();
        }
      }

      this.fetchTrips(deleteTripModels)
    }
  });

  /** Represents an exception to the schedule */
  G.ScheduleException = Backbone.Model.extend({
    defaults: {
      agencyId: null,
      exemplar: null,
      dates: null,
      customSchedule: null
    },

    /** add a javascript date object to the effective dates of this */
    addDate: function (date) {
      var dates = this.get('dates');

      if (dates == null)
        dates = [];

      var foundDate = false;

      _.each(dates, function (rawDate) {
        var exDate = new Date(rawDate);
        if (exDate.getUTCDate() == date.getUTCDate() && exDate.getUTCMonth() == date.getUTCMonth() &&
          exDate.getUTCFullYear() == date.getUTCFullYear())
          foundDate = true;
      });

      if (!foundDate) {
        dates.push(date.getTime());
        this.set('dates', dates);
      }
    },

    removeDate: function (year, month, dayOfMonth) {
      var dates = _.filter(this.get('dates'), function (epoch) {
        var date = new Date(epoch);
        return date.getUTCFullYear() != year || date.getUTCMonth() != month || date.getUTCDate() != dayOfMonth;
      });

      this.set('dates', dates);
    },

    urlRoot: G.config.baseUrl + 'api/scheduleexception/'
  });

  G.ScheduleExceptions = Backbone.Collection.extend({
    model: G.ScheduleException,
    url: G.config.baseUrl + 'api/scheduleexception/'
  });

  G.Snapshot = Backbone.Model.extend({
    defaults: {
      name: null,
      id: null,
      agencyId: null,
      snapshotTime: null,
      version: null
    },

    restore: function () {
      return $.ajax({
        url: G.config.baseUrl + 'api/snapshot/' + this.id + '/restore',
        method: 'POST'
      });
    }
  });

  G.Snapshots = Backbone.Collection.extend({
    model: G.Snapshot,
    url: G.config.baseUrl + 'api/snapshot',
    // sort backwards, newest first
    comparator: function (snap) { return -snap.get('version') }
  });
})(GtfsEditor, jQuery);
