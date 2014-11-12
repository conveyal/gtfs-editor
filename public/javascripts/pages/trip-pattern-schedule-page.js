// init timetable page

var GtfsEditor = GtfsEditor || {};

(function (G, $, ich) {
  $(document).ready(function() {
      // figure out the trippattern and calendar
      var match = /timetable\/([0-9]+)\/([0-9]+)/.exec(window.location.href);
      var tripPattern = match[1];
      var calendar = match[2];

      // grab the data
      var trips = new G.Trips();

      var pattern = new G.TripPattern({id: tripPattern});
      var patDf = pattern.fetch();

      var cal = new G.Calendar({id: calendar});
      var calDf = cal.fetch();

      var tripDf = trips.fetch({data: {patternId: tripPattern, calendarId: calendar}});

      // when all those requests are done
      $.when(patDf, calDf, tripDf).then(function () {
        new G.TripPatternScheduleView({
          el: '#timetable-container',
          collection: trips,
          pattern: pattern,
          calendar: cal
        }).render();
      })
    });
})(GtfsEditor, jQuery, ich);
