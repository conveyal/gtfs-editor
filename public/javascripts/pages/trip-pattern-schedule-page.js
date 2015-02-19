// init timetable page

var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  $(document).ready(function() {
    // figure out the trippattern and calendar
    var match = /timetable\/([^\/]+)\/([^\/]+)/.exec(window.location.href);
    var tripPattern = match[1];
    var calendar = match[2];

    // grab the data
    var trips = new G.Trips();

    var pattern = new G.TripPattern({
      id: tripPattern
    });
    var patDf = pattern.fetch();

    var cal = new G.Calendar({
      id: calendar
    });
    var calDf = cal.fetch();

    var tripDf = trips.fetch({
      data: {
        patternId: tripPattern,
        calendarId: calendar
      }
    });

    patDf.done(function() {
      var stops = new G.Stops();
      var ids = _.uniq(_.pluck(pattern.get('patternStops'), 'stopId'));
      var stopsDf = stops.fetch({
        data: {
          id: ids
        }
      });

      // when all those requests are done
      $.when(stopsDf, calDf, tripDf).then(function() {
        new G.TripPatternScheduleView({
          el: '#timetable-container',
          collection: trips,
          stops: stops,
          pattern: pattern,
          calendar: cal
        }).render();
      });
    });
  });
})(GtfsEditor, jQuery, ich);
