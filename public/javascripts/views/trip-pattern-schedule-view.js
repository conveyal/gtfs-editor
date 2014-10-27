/**
 * A timetable view of a TripPattern and Calendar.
 */

var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  var displayTime = function(st, arr) {
    // TODO: 12h time
    // TODO: single-time view

    var time = arr ? st.arrivalTime : st.departureTime

    var text;

    if (_.isUndefined(time)) {
      text = '-';
    } else {
      var secs = time % 60;
      var mins = (time - secs) % (60 * 60) / 60;
      var hours = (time - secs - mins * 60) / (60 * 60);

      // TODO: template
      text =
        '<span class="time ' + (arr ? 'time-arr' : 'time-dep') +
        // dim departure times that are the same as their arrival times
        // TODO: only in two-time mode
        (!arr && st.departureTime == st.arrivalTime ? 'time-dep-dimmed' : '') + '">' +
        '<span class="hours">' + hours + '</span>' +
        '<span class="minutes">' + (mins < 10 ? '0' + mins : mins) + '</span>' +
        '<span class="seconds">' + (secs < 10 ? '0' + secs : secs) + '</span>';

      return text;
    }
  };

  /**
   * Parse time in many formats
   * return null if there is no time
   * No time means "the vehicle does stop here, but it's up to the consumer to interpolate when"
   */
  var parseTime = function(time) {
    // TODO: many time formats
    // 332p
    // 1532
    // 153200
    // 15:33
    // 3:33 PM
    // etc.
    // TODO: validation
    if (time == '')
      return null;

    // get things into a standard format
    // everything lower case
    // no whitespace
    // get rid of the m on am, pm, and mm (post midnight)
    // we replace semicolons with colons, to allow folks to enter 3;32;21 pm, to be easier on the fingers
    // than having to type shift each time
    time = time.toLowerCase().replace(/([apm])(m?)/, '$1').replace(';', ':').replace(/\W/g, '');

    // separate the numbers from the letter
    var match = /([0-9:]{3,})([apm]?)/.exec(time);

    // figure out how much to add on for AM/PM/past-midnight
    var additionalSeconds = 0;
    if (match[2] == 'p')
      additionalSeconds = 12 * 60 * 60;
    else if (match[2] == 'm')
      additionalSeconds = 24 * 60 * 60;

    // am and 24-hour cases are implied

    // parse out hours, minutes and seconds
    var rawTime = match[1];
    var hours, minutes, seconds = 0;

    // if we have colons, use them as separators
    if (rawTime.indexOf(':') != -1) {
      var components = rawTime.split(':');
      hours = Number(components[0]);
      minutes = Number(components[1]);
      seconds = components.length >= 3 ? Number(components[2]) : 0;
    } else {

      // no colons, infer things positionally
      if (rawTime.length >= 5) {
        // we have seconds
        seconds = Number(rawTime.slice(-2));
        rawTime = rawTime.slice(0, -2);
      }

      var minutes = Number(rawTime.slice(-2));
      var hours = Number(rawTime.slice(0, -2));
    }

    // patch up 12am, 12pm, since they break the pattern
    if (hours == 12 && match[2] == 'p')
    // 12pm is noon
      additionalSeconds = 0;

    if (hours == 12 && match[2] == 'a')
    // 12am is 00:00:00
      additionalSeconds = -12 * 60 * 60;

    return hours * 60 * 60 + minutes * 60 + seconds + additionalSeconds;
  };

  /**
   * all of the valid time formats:
   * 1342 332p 332 PM 1:01:03 PM 10:10:10 etc.
   * 332m means 3 hours, thirty-two minutes past midnight and is used for overnight runs
   * (e.g. starting at 11:55 PM and ending at 1:31 M, or 1:31 the next day).
   *
   * Note that 2090102 is not a valid time, while 209:01:02 is. Though 209 hours past midnight on the day
   * of the start of the trip is in fact a valid time, it's almost always a typo when entered without colons, so
   * we disallow. If you're running the trans-Siberian railway (or another long-distance service), you'll just
   * have to enter the colons.
   *
   * Semicolons and colons are treated as equivalent, for ease of typing.
   */
  var validTimeFormat =
    /^\W*[0-9]{3,6}\W*[apmAPM]?[mM]?\W*$|^\W*[0-9]{1,}[:;][0-9]{2}([:;][0-9]{2})?\W*[apmAPM]?[mM]?\W*$/;

  G.TripPatternScheduleView = Backbone.View.extend({
    initialize: function(attr) {
      // consistency check
      var tripPatternId = null;
      var serviceCalendarId = null;
      var first = true;
      this.collection.each(function(trip) {
        if (first) {
          tripPatternId = trip.get('pattern').id;
          serviceCalendarId = trip.get('serviceCalendar').id;
          first = false;
        } else {
          if (trip.get('pattern').id != tripPatternId ||
            trip.get('serviceCalendar').id != serviceCalendarId) {
            throw new Error("Trip pattern or service calendars differ between trips");
          }
        }
      });
    },

    // combination of getAttr and setAttr for handsontable
    attr: function(name) {
      var instance = this;
      var ret = {
        data: function(trip, val) {
          if (name.indexOf('stop:') === 0) {
            // we need to return a stop time, so first parse out the column header
            var sp = name.split(':');
            var stopId = sp[1];
            var stopSeq = sp[2];
            var arr = sp[3] == 'arr';

            // find the stopTime with this stop id and stop sequence
            // note that this makes an assumption about stop_sequence: when a stop is ommitted,
            // its sequence number is omitted as well
            var st = _.find(trip.get('stopTimes'), function(st) {
              return st.stop.id == stopId && st.stopSequence == stopSeq;
            });

            if (_.isUndefined(val)) {
              return displayTime(st, arr);
            } else {
              if (arr) {
                st.arrivalTime = parseTime(val);
              } else {
                st.departureTime = parseTime(val);
              }
            }
          } else if (name == 'trip_id') {
            return trip.get('gtfsTripId');
          } else if (name == 'block_id') {
            return trip.get('blockId');
          } else if (name == 'trip_name') {
            return trip.get('tripName');
          }
        },
      };

      if (name.indexOf('stop:') === 0) {
        ret.validator = validTimeFormat;
        ret.renderer = Handsontable.renderers.HtmlRenderer
      }

      return ret;
    },

    render: function() {
      // render the template
      this.$el.html(ich['timetable-tpl']());

      // figure out what columns we're rendering
      var columns = [this.attr('trip_id'), this.attr('block_id'), this.attr('name')];
      // TODO: i18n
      var headers = ['Trip ID', 'Block ID', 'Trip name'];

      var instance = this;

      // note: the merged header cells aren't actually merged, but depend on a really awful hack (in timetable.css) where the width of
      // two columns is hardcoded as the header width. As a side effect, these numbers must be exactly twice the width of the time
      // display cells
      var colWidths = [150, 150, 150];

      _.each(this.collection.at(0).get('pattern').patternStops, function(patternStop, idx) {
        // we put stopSequence here for loop routes
        columns.push(instance.attr('stop:' + patternStop.stop.id + ':' + patternStop.stopSequence + ':arr'));
        columns.push(instance.attr('stop:' + patternStop.stop.id + ':' + patternStop.stopSequence + ':dep'));
        headers.push(patternStop.stop.stopName);
        // dummy header, will be overwritten when cells are merged
        headers.push('');

        colWidths.push(75);
        colWidths.push(75);
      });

      var $container = this.$('#timetable');
      $container.handsontable({
        data: this.collection.toArray(),
        dataSchema: function() {
          return new G.Trip();
        },
        // we'll be defining interaction on our own
        contextMenu: false,
        columns: columns,
        colHeaders: headers,
        colWidths: colWidths
      });
      return this;
    }
  });
})(GtfsEditor, jQuery, ich);
