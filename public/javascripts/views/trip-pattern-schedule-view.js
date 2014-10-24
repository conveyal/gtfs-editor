/**
* A timetable view of a TripPattern and Calendar.
*/

var GtfsEditor = GtfsEditor || {};

(function (G, $, ich) {
  G.TripPatternScheduleView = Backbone.View.extend({
    initialize: function (attr) {
      // consistency check
      var tripPatternId = null;
      var serviceCalendarId = null;
      var first = true;
      this.collection.each(function (trip) {
        if (first) {
          tripPatternId = trip.get('pattern').id;
          serviceCalendarId = trip.get('serviceCalendar').id;
          first = false;
        }
        else {
          if (trip.get('pattern').id != tripPatternId ||
                trip.get('serviceCalendar').id != serviceCalendarId) {
            throw new Error("Trip pattern or service calendars differ between trips");
          }
        }
      });
    },

    // combination of getAttr and setAttr for handsontable
    attr: function (name) {
      return {data: function (trip, val) {
        if (name.indexOf('stop:') === 0) {
          // we need to return a stop time, so first parse out the column header
          var sp = name.split(':');
          var stopId = sp[1];
          var stopSeq = sp[2];

          // find the stopTime with this stop id and stop sequence
          // note that this makes an assumption about stop_sequence: when a stop is ommitted,
          // its sequence number is omitted as well
          var st = _.find(trip.get('stopTimes'), function (st) {
            return st.stop.id == stopId && st.stopSequence == stopSeq;
          });

          //return st;
          // TODO: this is absolutely the wrong place to do time formatting
          // TODO: don't just give departure time
          // TODO: create a special cell editor for this
          if (st === null) {
            return '-';
          }

          var secs = st.arrivalTime % 60;
          var mins = (st.arrivalTime - secs) % (60 * 60) / 60;
          var hours = (st.arrivalTime - secs - mins * 60) / (60 * 60);

          return hours + ':' + mins + ':' + secs;
        }

        else if (name == 'trip_id') {
          return trip.get('gtfsTripId');
        }

        else if (name == 'block_id') {
          return trip.get('blockId');
        }

        else if (name == 'trip_name') {
          return trip.get('tripName');
        }
      }
    };
  },

  render: function () {
    // render the template
    this.$el.html(ich['timetable-tpl']());

    // figure out what columns we're rendering
    var columns = [this.attr('trip_id'), this.attr('block_id'), this.attr('name')];
    // TODO: i18n
    var headers = ['Trip ID', 'Block ID', 'Trip name'];

    var instance = this;

    _.each(this.collection.at(0).get('pattern').patternStops, function (patternStop) {
      // we put stopSequence here for loop routes
      columns.push(instance.attr('stop:' + patternStop.stop.id + ':' + patternStop.stopSequence));
      headers.push(patternStop.stop.stopName);
    });

    var $container = this.$('#timetable');
    $container.handsontable({
      data: this.collection.toArray(),
      dataSchema: function () { return new G.Trip(); },
      // we'll be defining interaction on our own
      contextMenu: false,
      columns: columns,
      colHeaders: headers
    });
    return this;
  }
});
})(GtfsEditor, jQuery, ich);
