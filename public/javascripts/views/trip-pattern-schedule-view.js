/**
 * A timetable view of a TripPattern and Calendar.
 */

var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  /**
   * the keyboard shortcuts for the editor
   * defined here to to allow for easy conversion to non-English mneuomics
   * These are e.keyCode for a keydown event.
   */
  var keyCodes = {
    // o: offset times
    offset: 79,
    // insert new trip (really, duplicate this trip . . .)
    insert: 73,
    // delete trip (also requires shift key, as coded in the function)
    deleteTrip: 46,
    // also backspace, for laptop users with no delete key...
    deleteTripAlternate: 8
  };

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
          (value.get('trip').get('invalid') === true ? 'trip-invalid ' : '') +
          (value.get('trip').deleted === true ? 'trip-deleted' : '') + '">' +
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
    time = time.toLowerCase().replace(/([apm])(m?)/, '$1').replace(';', ':').replace(/\s/g, '');

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
      this.collection.splice = function(idx, size) {
        var toRemove = this.toArray().slice(idx, size + 1);
        this.remove(toRemove);
        this.add(arguments.slice(2), {at: idx});
        return toRemove;
      };

      // event handlers
      _.bindAll(this, 'saveAll', 'newTrip', 'handleKeyDown', 'closeMinibuffer', 'sort', 'autofill');
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
              return st.stop.id == stopId && st.stopSequence == stopSeq && st.deleted !== true;
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
                var ps = _.find(instance.pattern.get('patternStops'), function(ps) {
                  return ps.stop.id == stopId && ps.stopSequence == stopSeq;
                });

                st = instance.makeStopTime(ps);

                // find the index of the previous stop
                var largestIndex = -1;
                _.each(trip.get('stopTimes'), function(eachSt, idx) {
                  if (eachSt.stopSequence < st.stopSequence && idx > largestIndex) {
                    largestIndex = idx;
                  }
                });

                // insert
                trip.get('stopTimes').splice(largestIndex + 1, 0, st);
                trip.set('stopTimes', trip.get('stopTimes'));
              }

              var ptime = parseTime(val);

              if (ptime === false) {
                // trip does not stop here
                st.deleted = true;
              } else if (arr) {
                st.arrivalTime = ptime;
              } else {
                st.departureTime = ptime;
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
        if (trip.modified && !trip.deleted) {
          deferreds.push(
            // we assume that, if it's been modified, it is now valid
            // the invalid flag just indicates that something has occurred that requires human intervention
            // but we don't clear the flag until the save is successful
            trip.save({
              invalid: false
            }, {
              wait: true
            }).done(function() {
              trip.modified = false;
            })
          );
        }
      });

      // issues are created if we delete trips whilst iterating over them, so we save the ones to be deleted and
      // delete them manually.
      // we delete trips after saving the ones that are to be saved, so that if the server responds really fast
      // the collection cannot be changed by having models deleted whilst the loop above is still running
      var deletedTrips = this.collection.filter(function (trip) {
        return trip.deleted;
      });
      var tripsDeleted = deletedTrips.length > 0;
      while (deletedTrips.length > 0) {
        var trip = deletedTrips.pop();
        deferreds.push(trip.destroy({wait: true}));
      }

      $.when.apply($, deferreds).always(function() {
        instance.$container.handsontable('render');
      });
    },

    // create a new trip based on the pattern
    newTrip: function() {
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
      _.each(this.pattern.get('patternStops'), function(patternStop) {
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

      this.collection.add(trip, {at: 0, sort: false});

      // trigger redraw, show the new trip
      this.$container.handsontable('render');
    },

    /**
     * Parse a user-entered offset for a trip, and return seconds to offset.
     */
    parseOffset: function (time) {
      time = String(time);
      var negative = false;
      if (time[0] == '-') {
        negative = true;
        time = time.slice(1);
      }

      var offset;
      // allow ; or . instead of :
      time.replace(';', ':').replace('.', ':');
      if (/^-?[0-9]+$/.test(time))
      // assume offset is minutes
        offset = Number(time) * 60;

      else if (validTimeFormat.test(time)) {
        offset = parseTime(time);

        // false generally indicates a skipped stop
        // but that doesn't make sense in the context of an offset
        if (offset === false)
          return null;
      }
      // ignore other formats
      else return null;

      if (negative)
        offset *= -1;

      return offset;
    },

    offsetTimes: function(coords, time) {
      var instance = this;

      var offset = this.parseOffset(time);

      if (offset === null)
        return;

      // get the affected trips
      // [0] and [2] are the first and last selected rows. Javascript's slice excludes the end, so we add one.
      var trips = this.collection.slice(coords[0], coords[2] + 1);

      _.each(trips, function(trip) {
        // find the affected stoptimes
        var fromCell = coords[1] - 4;
        var toCell = coords[3] - 4;
        var from = Math.floor(fromCell / 2);
        var to = Math.floor(toCell / 2) + 1;
        var patternStops = instance.pattern.get('patternStops');
        patternStops = _.sortBy(patternStops, 'stopSequence').slice(from, to);

        _.each(patternStops, function(ps, idx) {
          // get the stoptime
          var st = _.find(trip.get('stopTimes'), function(st) {
            return st.stop.id == ps.stop.id && st.stopSequence == ps.stopSequence;
          });

          if (st === null)
            return;

          // if we're not looking at the first stoptime, and/or the first cell is even, update arrival time
          // if the first cell is odd and this is the first stoptime, we're only updating the departureTime.
          if (idx !== 0 || fromCell % 2 === 0)
            st.arrivalTime += offset;

          // same idea at the end. if the tocell is odd or we're in the middle, update both arrival and departure times
          if (idx != patternStops.length - 1 || toCell % 2 == 1)
            st.departureTime += offset;
        });

        trip.modified = true;
      });

      this.$container.handsontable('render');
    },

    /** sort the trips by first stop time, nulls first */
    sort: function () {
      this.collection.sort();
      this.$container.handsontable('render');
    },

    /** Make a stop time from a pattern stop */
    makeStopTime: function(patternStop) {
      var st = {};

      st.stop = patternStop.stop;
      st.patternStop = patternStop;
      st.stopSequence = patternStop.stopSequence;
      st.pickupType = patternStop.stop.pickupType;
      st.dropOffType = patternStop.stop.dropOffType;

      st.arrivalTime = st.departureTime = null;

      return st;
    },

    /* handle a key press in the time editing area */
    handleKeyDown: function(e) {
      console.log(e, sel);

      // if the minibuffer is open, don't allow any input whatsoever
      if (this.allInputPrevented) {
        e.stopImmediatePropagation();
        return;
      }

      // figure out what's selected
      var ht = this.$container.handsontable('getInstance');
      var sel = ht.getSelected();
      var instance = this;

      // keyboard shortcuts only apply in the times section
      if (sel[1] < 4)
        return;

      // if we never set this to false, we got a reserved key and will not pass the event
      var commandFound = true;

      // o: offset times
      if (e.keyCode == keyCodes.offset) {
        this.getInput('Offset amount', function(input) {
          instance.offsetTimes(sel, input);
          instance.collection.trigger('change');
        });

      // i: insert new trip
      // basically, duplicate this trip or these trips, with the entered offset
      } else if (e.keyCode == keyCodes.insert) {
        this.getInput('New trip offset', function (input) {
          var offset = instance.parseOffset(input);

          var newTrips = [];

          if (offset === null)
            return;

          var templateTrips = instance.collection.slice(sel[0], sel[2] + 1);
          _.each(templateTrips, function (templateTrip) {
            var newTrip = templateTrip.clone();
            newTrip.modified = true;

            _.each(newTrip.get('stopTimes'), function (st) {
              st.arrivalTime += offset;
              st.departureTime += offset;
            });

            newTrips.push(newTrip);
          });

          // put the trips directly after the selected trips if the offset is positive
          // otherwise, put them directly before
          // this can still result in out-of-order schedules, but is more intuitive; imagine you offsetted trips in such
          // a way as to interleave them with other trips; the new trips could wind up spread all over the schedule.
          if (offset >= 0)
            instance.collection.add(newTrips, {at: sel[2] + 1, sort: false});
          else
            instance.collection.add(newTrips, {at: sel[0], sort: false});

          instance.$container.handsontable('render');
        });
        // two key codes because the default config allows shift-del or shift-backspace
      } else if ((e.keyCode == keyCodes.deleteTrip || e.keyCode == keyCodes.deleteTripAlternate) && e.shiftKey) {
        var trips = this.collection.slice(sel[0], sel[2] + 1);

        // we don't actually delete the trip yet, we just mark it as deleted
        // that way the user can revert if necessary
        // the trip will actually be deleted if and when the user saves it or all trips
        _.each(trips, function (trip) {
          // we set this directly on the trip object because it's not an attribute of the trip, but rather a state
          // variable that stays on the client side
          // so it's not a mistake that we're not using set()
          trip.modified = trip.deleted = true;
        });

        this.$container.handsontable('render');
      } else {
        // not a command, pass through
        commandFound = false;
      }

      if (commandFound) {
        e.stopImmediatePropagation();
        e.preventDefault();
      }
    },

    // get user input for a command that requires it, using the minibuffer
    getInput: function(prompt, callback) {
      var instance = this;

      // don't allow keystrokes to bubble to form
      this.allInputPrevented = true;

      $('.minibuffer').removeClass('hidden');

      $('.minibuffer .prompt').text(prompt);

      $('.minibuffer input').val('')
      .keydown(function(e) {
        if (e.keyCode == 13) {
          // return was pressed
          e.stopImmediatePropagation();
          instance.closeMinibuffer();
          callback($('.minibuffer input').val());
        } else if (e.keyCode == 27) {
          // esc was pressed
          e.stopImmediatePropagation();
          instance.closeMinibuffer();
        }
      })
      .focus();
    },

    // close the minibuffer
    closeMinibuffer: function () {
      // don't call the callback again
      // IE9 leaves the blinking cursor after the input box is gone, banish it
      $('.minibuffer input').off('keydown').blur();
      $('.minibuffer').addClass('hidden');

      // allow user to type again
      this.allInputPrevented = false;
    },

    /** Find a stop time given a trip and a pattern stop, or null if no such stop time exists */
    findStopTimeByPatternStop: function (trip, patternStop) {
      return _.find(trip.stopTimes || trip.get('stopTimes'), function (st) {
        return st.stop.id == patternStop.stop.id && st.stopSequence == patternStop.stopSequence;
      });
    },

    /** handle horizontal autofill, fill in times from pattern */
    autofill: function (start, end, data) {
      // figure out which direction the autofill was in
      var direction, horizontal;
      var sel = this.$container.handsontable('getSelected');

      if (sel[0] == start.row) {
        horizontal = true;
        direction = start.col > sel[1] ? 'east' : 'west';
      }
      else {
        horizontal = false;
        direction = start.row > sel[0] ? 'south' : 'north';
      }

      // horizontal: autofilling times
      if (horizontal) {
        if (start.col <= 4 || end.col < 4)
          // doesn't make sense to horizontally autofill anything except times
          return false;

        var fromCol = start.col - 4;
        var toCol = end.col - 4;

        var initialPatternStop = Math.floor(fromCol / 2);
        var finalPatternStop = Math.floor(toCol / 2);

        var trips;

        if (end.row > start.row)
          trips = this.collection.slice(start.row, end.row + 1);
        else
          trips = this.collection.slice(end.row, start.row + 1);

        var instance = this;

        if (direction == 'east') {
          // update the relevant stop times
          _.each(trips, function (trip) {
            var patternStops = instance.pattern.get('patternStops');

            // Get the pattern stop to the left of the first filled stop time
            // this could be the same as the pattern stop of the first filled
            var filledPatternStop = patternStops[(fromCol % 2) == 0 ? initialPatternStop - 1 : initialPatternStop];
            // can't autofill from a skipped or interpolated stop, don't autofill this trip
            var filledStopTime = instance.findStopTimeByPatternStop(trip, filledPatternStop);

            if (filledStopTime === null || _.isUndefined(filledStopTime))
              return;

            // fromCol % 2 == 0: first filled cell is arrival time, so we are filling from the previous stops'
            // departure time
            if ((fromCol % 2) === 0 && filledStopTime.departureTime === null)
              return;

            // fromCol % 2 == 1: first filled cell is departure time, so we are filling from the arrival time
            if ((fromCol % 2) == 1 && filledStopTime.arrivalTime === null)
              return;

            for (var i = initialPatternStop; i <= finalPatternStop; i++) {
              var ps = patternStops[i];
              var st = instance.findStopTimeByPatternStop(trip, ps);

              // edge condition at start if filling only departure
              if ((fromCol % 2) == 1 && i == initialPatternStop) {
                st.departureTime = st.arrivalTime + ps.defaultDwellTime;
                continue;
              }

              var wasStopTimeCreated = false;

              // previously skipped, fill it in now
              // note that this cannot happen if we are filling the second half of a stop time
              // at the start, due to conditions above. So there is no race condition with the block above
              if (st == null) {
                st = instance.makeStopTime(ps);
                trip.get('stopTimes').push(st);
                // shouldn't matter, but it's nice to keep this sorted
                trip.set('stopTimes', _.sortBy(trip.get('stopTimes'), 'stopSequence'));
                wasStopTimeCreated = true;
              }

              var previousPs = patternStops[i - 1];
              var previousSt = instance.findStopTimeByPatternStop(trip, previousPs);

              // previousSt will always be defined, because we will have just made it if it didn't exist before

              st.arrivalTime = previousSt.departureTime + ps.defaultTravelTime;

              // edge condition at end: we may be only filling the arrival time
              // but if we created the stop time, we should fill them both, since GTFS requires both an arrival
              // and departure time if either is specified
              if (i != finalPatternStop || i == finalPatternStop && ((toCol % 2) == 1 || wasStopTimeCreated))
                st.departureTime = st.arrivalTime + ps.defaultDwellTime;

              trip.set('stopTimes', trip.get('stopTimes'));
              trip.modified = true;
            }
          });
        }
      }

      instance.$container.handsontable('render');
      // block the default autofill action
      return false;
    },

    render: function() {
      // render the template
      this.$el.html(ich['timetable-tpl'](this.pattern.toJSON()));

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
        colWidths: colWidths,
        beforeKeyDown: this.handleKeyDown,
        beforeAutofill: this.autofill,
        // It's somewhat confusing what vertical autofill would do, so we don't support it
        fillHandle: true
      });

      // add the event handlers
      this.$('.save').click(this.saveAll);
      this.$('.new-trip').click(this.newTrip);
      this.$('.sort-trips').click(this.sort);
      this.$('.minibuffer-bg').click(this.closeMinibuffer);

      return this;
    }
  });
})(GtfsEditor, jQuery, ich);
