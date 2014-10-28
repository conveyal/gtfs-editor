// init timetable page

var GtfsEditor = GtfsEditor || {};

(function (G, $, ich) {
  // parse the url
  var Router = Backbone.Router.extend({
    routes: {
      'timetable/:tripPattern/:calendar' : 'showTimetable'
    },

    showTimetable: function(tripPattern, calendar) {
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
    }
  });

  new Router();

  Backbone.history.start({pushState: true});
})(GtfsEditor, jQuery, ich);
