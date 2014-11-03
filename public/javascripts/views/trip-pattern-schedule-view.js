/**
 * A timetable view of a TripPattern and Calendar.
 */

var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  /**
   * A model representing a cell in the table. We need a toString because Handsontable calls toString before
   * calling the editor setValue method, so the string needs to be something useful.
   */
  var StopTimeCell = Backbone.Model.extend({
    defaults: {
      stopTime: null,
      // is this the arrival time cell, or the departure time cell?
      arr: null,
      trip: null
    },
    toString: function() {
      var st = this.get('stopTime');

      if (st === null)
        return 'does-not-stop';

      var time = this.get('arr') ? st.arrivalTime : st.departureTime

      return String(time !== null ? time : 'no-time');
    }
  })

  /**
   * A renderer that displays the time as a time rather than seconds since midnight.
   */
   var stopTimeRenderer = function(instance, td, row, col, prop, value, cellProperties) {
     // TODO: 12h time
     // TODO: single-time view
     // time is seconds since midnight
     var text;
     if (value.get('stopTime') === null) {
       text = '<span class="time no-stop">-</span>';
     } else {

       var arr = value.get('arr');
       var st = value.get('stopTime');
       var time = arr ? st.arrivalTime : st.departureTime;

       if (time === null) {
         // time is to be interpolated by consumer
         text = '';
       } else {
         var secs = time % 60;
         var mins = (time - secs) % (60 * 60) / 60;
         var hours = (time - secs - mins * 60) / (60 * 60);

         // TODO: template
         text =
           '<div class="time ' + (arr ? 'time-arr ' : 'time-dep ') +
           // dim departure times that are the same as their arrival times
           // TODO: only in two-time mode
           (!arr && st.departureTime == st.arrivalTime ? 'time-dep-dimmed ' : '') +
           (value.get('trip').get('invalid') === true ? 'trip-invalid' : '') + '">' +
           '<span class="hours">' + hours + '</span>' +
           '<span class="minutes">' + (mins < 10 ? '0' + mins : mins) + '</span>' +
           '<span class="seconds">' + (secs < 10 ? '0' + secs : secs) + '</span>' +
           '</div>';
       }
     }

     Handsontable.renderers.HtmlRenderer(instance, td, row, col, prop, text, cellProperties);
   };


  /**
   * Edit a time
   */
  var StopTimeEditor = Handsontable.editors.TextEditor.prototype.extend();

  StopTimeEditor.prototype.setValue = function(time) {
    if (time == 'does-not-stop') {
      value = '-';
    } else if (time == 'no-time') {
      value = '';
    } else {

      time = Number(time);

      var secs = time % 60;
      var mins = (time - secs) % (60 * 60) / 60;
      var hours = (time - secs - mins * 60) / (60 * 60);

      var value = hours + ':' + (mins < 10 ? '0' + mins : mins) + ':' + (secs < 10 ? '0' + secs : secs);
    }

    Handsontable.editors.TextEditor.prototype.setValue.apply(this, [value]);
    $(this.TEXTAREA).addClass('time');
  };


  // select everything, since folks are usually overwriting
  StopTimeEditor.prototype.focus = function() {
    Handsontable.editors.TextEditor.prototype.focus.apply(this, arguments);
    this.TEXTAREA.setSelectionRange(0, this.TEXTAREA.value.length);
    $(this.TEXTAREA).css('font-family', 'mono');
  };

  /**
   * Parse time in many formats
   * return null if there is no time
   * return false if the vehicle does not stop here
   * No time means "the vehicle does stop here, but it's up to the consumer to interpolate when"
   */
  var parseTime = function(time) {
    // get things into a standard format
    // everything lower case
    // no whitespace
    // get rid of the m on am, pm, and mm (post midnight)
    // we replace semicolons with colons, to allow folks to enter 3;32;21 pm, to be easier on the fingers
    // than having to type shift each time
    time = time.toLowerCase().replace(/([apm])(m?)/, '$1').replace(';', ':').replace(/\W/g, '');

    if (time === '')
      return null;

    // TODO: handle this properly on re-render
    if (time == '-')
      return false;

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
   * we disallow it. If you're running the trans-Siberian railway (or another long-distance service), you'll just
   * have to enter the colons.
   *
   * Semicolons and colons are treated as equivalent, for ease of typing.
   */
  var validTimeFormat =
    /^\W*$|^\W*-\W*|^\W*[0-9]{1,2}([0-5][0-9]){1,2}\W*[apmAPM]?[mM]?\W*$|^\W*[0-9]{1,}([:;][0-5][0-9]){1,2}\W*[apmAPM]?[mM]?\W*$/;

  G.TripPatternScheduleView = Backbone.View.extend({
    initialize: function(attr) {
      this.calendar = attr.calendar;
      this.pattern = attr.pattern;

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

      // Handsontable needs the collection to have a splice method
      // inspired by code at http://handsontable.com/demo/backbone.html
    this.collection.splice = function (idx, size) {
      var toRemove = this.toArray().slice(idx, size + 1);
      this.remove(toRemove);
      // collections are not ordered, since this one has a comparator; thus it doesn't matter if we insert stuff in the middle
      // backbone will just move it around anyhow, and handsontable doesn't seem to care if things don't end up where it put them
      this.add(arguments.slice(2));
      return toRemove;
    };

      // event handlers
      _.bindAll(this, 'saveAll', 'newTrip');
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
              return new StopTimeCell({
                arr: arr,
                stopTime: st,
                trip: trip
              });
            } else {
              if (st == null) {
                // find the appropriate pattern stop
                var ps = _.find(instance.pattern.get('patternStops'), function (ps) {
                  return ps.stop.id == stopId && ps.stopSequence == stopSeq;
                });

                st = instance.makeStopTime(ps);

                // find the index of the previous stop
                var largestIndex = -1;
                _.each(trip.get('stopTimes'), function (eachSt, idx) {
                  if (eachSt.stopSequence < st.stopSequence && idx > largestIndex) {
                    largestIndex = idx;
                  }
                });

                // insert
                trip.get('stopTimes').splice(largestIndex + 1, 0, st);
                trip.set('stopTimes', trip.get('stopTimes'));
              }

              if (arr) {
                st.arrivalTime = parseTime(val);
              } else {
                st.departureTime = parseTime(val);
              }

              // keep track of modifications
              // Backbone doesn't know quite how to handle nested objects inside models, so we track state ourselves
              trip.modified = true;
            }
          } else if (name == 'tripId') {
            if (_.isUndefined(val)) {
              return trip.get('gtfsTripId');
            } else {
              trip.set('gtfsTripId', val);
              trip.modified = true;
            }
          } else if (name == 'blockId') {
            if (_.isUndefined(val)) {
              return trip.get('blockId');
            } else {
              trip.set('blockId', val);
              trip.modified = true;
            }
          } else if (name == 'tripHeadsign') {
            if (_.isUndefined(val)) {
              return trip.get('tripHeadsign');
            } else {
              trip.set('tripHeadsign', val);
              trip.modified = true;
            }
          } else if (name == 'modified') {
            return trip.modified ? '*' : '';
          }
        },
      };

      if (name.indexOf('stop:') === 0) {
        ret.validator = validTimeFormat;
        ret.renderer = stopTimeRenderer;
        ret.editor = StopTimeEditor;
        ret.allowInvalid = false;
      }

      return ret;
    },

    saveAll: function() {
      var instance = this;
      var deferreds = [];

      this.collection.each(function(trip) {
        if (trip.modified) {
          deferreds.push(
            trip.save().done(function() {
              trip.modified = false;
            })
          );
        }
      });

      $.when.apply($, deferreds).always(function () {
        instance.$container.handsontable('render');
      });
    },

    // create a new trip based on the pattern
    newTrip: function () {
      var trip = new G.Trip();
      trip.set('pattern', this.pattern.toJSON());
      trip.set('serviceCalendar', this.calendar.toJSON());
      trip.set('route', this.pattern.get('route'));
      trip.set('useFrequency', false);
      var stopTimes = [];

      // prepopulate stop times based on the pattern
      // TODO: midnight is a bad initial time. what is a good intial time?
      var currentTime = 0;

      var instance = this;
      _.each(this.pattern.get('patternStops'), function (patternStop) {
        var st = instance.makeStopTime(patternStop);

        currentTime += patternStop.defaultTravelTime;
        st.arrivalTime = currentTime;
        currentTime += patternStop.defaultDwellTime;
        st.departureTime = currentTime;

        stopTimes.push(st);
      });

      trip.set('stopTimes', stopTimes);

      // flag so it gets saved
      trip.modified = true;

      this.collection.add(trip);

      // trigger redraw, show the new trip
      this.$container.handsontable('render');
    },

    /** Make a stop time from a pattern stop */
    makeStopTime: function (patternStop) {
      var st = {};

      st.stop = patternStop.stop;
      st.patternStop = patternStop;
      st.stopSequence = patternStop.stopSequence;

      st.arrivalTime = st.departureTime = null;

      return st;
    },

    render: function() {
      // render the template
      this.$el.html(ich['timetable-tpl']());

      // figure out what columns we're rendering
      var columns = [this.attr('modified'), this.attr('tripId'), this.attr('blockId'), this.attr('tripHeadsign')];
      // TODO: i18n
      var headers = ['', 'Trip ID', 'Block ID', 'Trip headsign'];

      var instance = this;

      // note: the merged header cells aren't actually merged, but depend on a really awful hack (in timetable.css) where the width of
      // two columns is hardcoded as the header width. As a side effect, these numbers must be exactly twice the width of the time
      // display cells
      var colWidths = [15, 150, 150, 150];

      _.each(this.pattern.get('patternStops'), function(patternStop, idx) {
        // we put stopSequence here for loop routes
        columns.push(instance.attr('stop:' + patternStop.stop.id + ':' + patternStop.stopSequence + ':arr'));
        columns.push(instance.attr('stop:' + patternStop.stop.id + ':' + patternStop.stopSequence + ':dep'));
        headers.push(patternStop.stop.stopName);
        // dummy header, will be overwritten when cells are merged
        headers.push('');

        colWidths.push(75);
        colWidths.push(75);
      });

      // make a new trip, and since it's new it's been modified (from its previous state of non-existence)
      var schema = function() {
        var t = new G.Trip();
        t.modified = true;
        return t;
      };

      this.$container = this.$('#timetable');
      this.$container.handsontable({
        data: this.collection,
        dataSchema: schema,
        // we'll be defining interaction on our own
        // also adding a column doesn't make sense
        // and adding a row (trip) causes display issues with handsontable
        contextMenu: false,
        columns: columns,
        colHeaders: headers,
        colWidths: colWidths
      });

      // add the event handlers
      this.$('.save').click(this.saveAll);
      this.$('.new-trip').click(this.newTrip);

      return this;
    }
  });
})(GtfsEditor, jQuery, ich);
