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
      trips.fetch({data: {patternId: tripPattern, calendarId: calendar}}).done(function () {
        new G.TripPatternScheduleView({
          el: '#timetable-container',
          collection: trips
        }).render();
      })
    }
  });

  new Router();

  Backbone.history.start({pushState: true});
})(GtfsEditor, jQuery, ich);
