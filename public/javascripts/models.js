var GtfsEditor = GtfsEditor || {};

(function(G, $) {

  G.Agency = Backbone.Model.extend({
    defaults: {
      id: null,
      gtfsAgencyId: null,
      name: null,
      url: null,
      timezone: null,
      lang: null,
      phone: null
    }
  });

  G.Agencies = Backbone.Collection.extend({
    type: 'Agencies',
    model: G.Agency,
    url: '/api/agency/'
  });

  G.Route = Backbone.Model.extend({
    defaults: {
      id: null,
      gtfsRouteId: null,
      routeShortName: null,
      routeLongName: null,
      routeDesc: null,
      routeType: null,
      routeUrl: null,
      routeColor: null,
      routeTextColor: null,
      weekday: null,
      saturday: null,
      sunday: null,
      agency: null
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
      location: null
    },
    // This function serves to allow stringified JSON as a valid input. A bit
    // hacky, but it's a great place to do it.
    validate: function(attrs) {
      var loc;
      if (_.isString(attrs.location)) {
        try {
          loc = JSON.parse(attrs.location);
        } catch(e) {
          // location was a string, but not JSON. This will break on the server.
        }
        if (loc) {
          this.set('location', loc);
        }
      }
    }
  });

  G.Stops = Backbone.Collection.extend({
    type: 'Stops',
    model: G.Stop,
    url: '/api/stop/'
  });

  // Untested
  //    |
  //    V
  G.TripPattern = Backbone.Model.extend({
    // name, headsign, alignment, stop_times[], shape, route_id (fk)
      // stop_id, travel_time, dwell_time

    addStop: function(stopTime) {
      this.get('stop_times').push(stopTime);
    },

    addStopAt: function(stopTime, i) {
      this.get('stop_times').splice(i, 0, stopTime);
    },

    removeStopAt: function(i) {
      return this.get('stop_times').splice(i, 1)[0];
    },

    moveStopTo: function(fromIndex, toIndex) {
      var stopTimes = this.get('stop_times'),
          stopTime;

      stopTime = this.removeStopAt(fromIndex);
      this.addStopAt(stopTime, toIndex);
    }
  });

  G.TripPatterns = Backbone.Collection.extend({
    model: G.TripPattern
  });

  G.Calendars = Backbone.Collection.extend({
    // days, start_date, end_date, exceptions[]
  });

  G.Trips = Backbone.Collection.extend({
    // trip_pattern_id (fk), cal_id (fk),
      // start_time (for static trips)
      // start_time, end_time, headway (for frequencies)
      // is_frequency
  });

})(GtfsEditor, jQuery);