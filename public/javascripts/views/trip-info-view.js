var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  G.TripInfoView = Backbone.View.extend({
    events: {
      'click .create-trip': 'showCreateTrip',
      'click .copy-trip': 'showCopyTrip',
      'click .trip-create-cancel-btn': 'cancelTripCreate',
      'click #delete-trip-btn': 'deleteTrip',
      'click #calendar-create-btn': 'showCalendarCreateModal',
      'click #calendar-create-close': 'calendarCreateClose',
      'click #calendar-modify-btn': 'modifyCalendar',
      'click #timetable-download-btn' : 'downloadSchedule',
      'change #tripPattern': 'onTripPatternChange',
      'change #trip': 'onTripChange',
      'change #trip-timetable #calendar': 'onTimetableCalendarSelectChange',
      'change #trip-frequency #calendar': 'onFrequencyCalendarSelectChange',


      'change input[name=scheduleType]': 'onScheduleTypeChange',

      'submit #trip-create-form': 'onTripCreate',
      'submit #trip-copy-form': 'onTripCopy',
      'submit #trip-info-form':  'onSaveTrip',
      'submit #create-calendar-form': 'createCalendar'
    },

    initialize: function () {


      this.agencyTrips = new G.Trips();
      this.calendars = new G.Calendars();

      this.calendars.fetch({data: {agencyId: this.model.get('agencyId')}});

      this.calendars.on('add', this.onCalendarsReset, this);
      this.calendars.on('remove', this.onCalendarsReset, this);
      this.calendars.on('reset', this.onCalendarsReset, this);

      this.model.tripPatterns.fetch({data: {routeId: this.model.id}});

      this.model.tripPatterns.on('add', this.onTripPatternsReset, this);
      this.model.tripPatterns.on('remove', this.onTripPatternsReset, this);
      this.model.tripPatterns.on('reset', this.onTripPatternsReset, this);

      _.bindAll(this, 'onTripPatternChange', 'downloadSchedule', 'bindPopovers', 'onTripChange', 'onTripsLoaded', 'createCalendar', 'showCreateTrip', 'showCopyTrip', 'onTripCreate', 'onTripCopy', 'cancelTripCreate', 'deleteTrip', 'onSaveTrip', 'showCalendarCreateModal', 'onCalendarsReset', 'calendarCreateClose', 'onTimetableCalendarSelectChange', 'onFrequencyCalendarSelectChange', 'onLoadAgencyTrips', 'onScheduleTypeChange', 'initTimetable', 'updatePatternType', 'updateTimetableLink');

    },

    setModel: function(model) {
      this.model = model;
      this.model.on('change', this.render, this);
      this.model.on('destroy', this.onModelDestroy, this);


    },

    onModelDestroy: function(){
      this.model = null;
      this.render();
    },

   render: function () {

      // Get the markup from icanhaz
      var $tpl = ich['trip-info-tpl']();

      // Render to the dom
      this.$el.html($tpl);

      // Add the route summary
      new G.RouteSummaryView({
        el: this.$('.route-context'),
        model: this.model
      }).render();

      this.$('.create-calendar-form').on('submit', this.createCalendar);

      // Bind help popovers
      this.bindPopovers();

      this.onTripPatternsReset();

      return this;
    },

    onSaveTrip: function(evt){

      evt.preventDefault();

      var selectedPatternId  = this.$('#tripPattern').val();

      var data = G.Utils.serializeForm($(evt.target));

      data.serviceCalendar = data.frequencyServiceCalendar;

      data = _.omit(data, ['file', 'scheduleType', 'frequencyServiceCalendar', 'timetableServiceCalendar']);

      data.pattern = selectedPatternId;
      data.startTime = this.calcTime(data.startTimeString);
      data.endTime = this.calcTime(data.endTimeString);
      data.headway = this.calcTime(data.serviceFrequencyString);

      delete data.startTimeString;
      delete data.endTimeString;
      delete data.serviceFrequencyString;

      this.model.tripPatterns.get(selectedPatternId).trips.get(data.id).save(data, {
        wait: true,
        success: _.bind(function() {
          this.onCalendarsReset();
          this.$('#calendar-create-modal').modal('hide');
          G.Utils.success('Trip successfully saved');
        }, this),
        error: function() {
          G.Utils.error('Trip save failed');
        }
      });

    },

    bindPopovers: function() {

      this.$('input, select, textarea').popover({
        placement: 'right',
        trigger: 'focus',
        container: 'body'
      });

    },

    showCreateTrip: function() {

      this.$('.create-trip').hide();
      this.$('.copy-trip').hide();
      this.$('#delete-trip-btn').hide();

      this.$('#trip-create').html(ich['trip-create-tpl']());

      // something upstream is causing this form not to bind automagically
      this.$('#trip-create-form').bind('submit', this.onTripCreate);

    },

    showCopyTrip: function() {
      this.$('.create-trip').hide();
      this.$('.copy-trip').hide();
      this.$('#delete-trip-btn').hide();

      this.agencyTrips.fetch({data: {agencyId: this.model.get('agency').id}, success: this.onLoadAgencyTrips});

    },

    onLoadAgencyTrips: function(data) {

      var tripData =  {
        trips: data.models
      }

      this.$('#trip-copy').html(ich['trip-copy-tpl'](tripData));

      // something upstream is causing this form not to bind automagically
      this.$('#trip-copy-form').bind('submit', this.onTripCopy);

      this.bindPopovers();

    },

    cancelTripCreate: function() {

      this.$('.create-trip').show();
      this.$('.copy-trip').show();
      this.$('#delete-trip-btn').show();

      this.$('#trip-create').html("");
      this.$('#trip-copy').html("");

    },

    deleteTrip: function(evt) {

      var selectedPatternId  = this.$('#tripPattern').val();
      var selectedTripId  = this.$('#trip').val();

      if (this.model.tripPatterns.get(selectedPatternId) != undefined && this.model.tripPatterns.get(selectedPatternId).trips.get(selectedTripId) != undefined && G.Utils.confirm(G.strings.tripInfoDeleteTripconfirm)) {

        this.model.tripPatterns.get(selectedPatternId).trips.get(selectedTripId).destroy();

      }

    },

    onTripCreate: function(evt) {

      evt.preventDefault();

      var selectedPatternId  = this.$('#tripPattern').val();

      var tripData = {
        useFrequency: true,
        pattern: selectedPatternId,
        tripDescription: this.$('[name=name]').val()
      };

      var view = this;

      this.model.tripPatterns.get(selectedPatternId).trips.create(tripData, {
        wait: true,
        success: function(data) {

            G.session.trip = data.id;

        },
        error: function() {
          G.Utils.error('Failed to create trip.');
        }
      });

    },

    onTripCopy: function(evt) {

      evt.preventDefault();

      var selectedPatternId  = this.$('#tripPattern').val();

      var existingTripId  = this.$('#existingTripId').val();

      var existingTrip = this.agencyTrips.get(existingTripId).attributes;

      var serviceCalendarId;

      if( existingTrip.serviceCalendar)
        serviceCalendarId = existingTrip.serviceCalendar.id;
      else
        serviceCalendarId = null;

      var tripData = {
        useFrequency: true,
        pattern: selectedPatternId,
        tripDescription: this.$('[name=name]').val(),
        serviceCalendar: serviceCalendarId,
        startTime: existingTrip.startTime,
        endTime: existingTrip.endTime,
        headway: existingTrip.headway,

      };

      var view = this;

      this.model.tripPatterns.get(selectedPatternId).trips.create(tripData, {
        wait: true,
        success: function(data) {

            G.session.trip = data.id;

        },
        error: function() {
          G.Utils.error('Failed to create trip.');
        }
      });

    },

    updateTripPatterns: function() {

      var data = {
        items: this.model.tripPatterns.models
      }

      this.$('#trippattern-select').html(ich['trippattern-select-tpl'](data));

      this.$('#tripPattern option[value="' + G.session.tripPattern + '"]')
        .attr('selected', true);;

      this.$('#trip-frequency').hide();
      this.$('#trip-timetable').hide();

      this.onTripPatternChange();

    },

    updatePatternType: function() {

        var selectedPatternId  = this.$('#tripPattern').val();

        this.$('#trip-details').html("");
        this.$('#trip-select').html("");

        if(this.model.tripPatterns.get(selectedPatternId) != undefined) {

          this.$('#trip-pattern-type').show();

          var useFrequency = this.model.tripPatterns.get(selectedPatternId).get('useFrequency');

          if(useFrequency == null)
            useFrequency = false;


          if(useFrequency == false) {

            this.model.tripPatterns.get(selectedPatternId).set('useFrequency', false);

            this.$('#scheduleTypeFrequency').prop("checked", false);
            this.$('#scheduleTypeTimetable').prop("checked", true);

            this.initTimetable();

          }
          else {

            this.model.tripPatterns.get(selectedPatternId).set('useFrequency', true);

            this.$('#scheduleTypeFrequency').prop("checked", true);
            this.$('#scheduleTypeTimetable').prop("checked", false);

            this.initFrequency();

          }
        }
        else
          this.$('#trip-pattern-type').hide();
    },

    updateTrips: function() {

        var selectedPatternId  = this.$('#tripPattern').val();

        this.$('#trip-details').html("");
        this.$('#trip-select').html("");

        if(this.model.tripPatterns.get(selectedPatternId) != undefined) {

          this.model.tripPatterns.get(selectedPatternId).trips.on('add', this.onTripsLoaded, this);
          this.model.tripPatterns.get(selectedPatternId).trips.on('remove', this.onTripsLoaded, this);
          this.model.tripPatterns.get(selectedPatternId).trips.on('sync', this.onTripsLoaded, this);

          this.model.tripPatterns.get(selectedPatternId).trips.fetchTrips();
        }
    },

    updateTripDetail: function() {

       var selectedPatternId  = this.$('#tripPattern').val();
       var selectedTripId  = this.$('#trip').val();

        if(selectedTripId == "" || selectedTripId == undefined) {

          this.$('#delete-trip-btn').addClass("disabled");
          this.$('#trip-details').html("");
          return;

        }
        else {

          var data =  jQuery.extend({}, this.model.tripPatterns.get(selectedPatternId).trips.get(selectedTripId).attributes)  ;

          data.pattern = selectedPatternId;
          data.startTimeString = this.convertTime(data.startTime);
          data.endTimeString = this.convertTime(data.endTime);
          data.serviceFrequencyString = this.convertTime(data.headway);

          this.$('#delete-trip-btn').removeClass("disabled");
          this.$('#trip-details').html(ich['trip-details-tpl'](data));

          this.onCalendarsReset();

        }

        this.onFrequencyCalendarSelectChange();

        this.bindPopovers();
    },

    showCalendarCreateModal: function(evt) {

      $('#calendar-create-modal').modal({keyboard: false});
      $('#calendar-create-modal-body').html(ich['create-calendar-detail-tpl']());

    },

    calendarCreateClose: function(evt) {

        $('#calendar-create-modal').modal('hide');

    },

    modifyCalendar: function(evt) {

       if(this.$('#calendar').val() != "") {

        var calendarId = this.$('#calendar').val();

        $('#calendar-create-modal').modal({keyboard: false});
        $('#calendar-create-modal-body').html(ich['create-calendar-detail-tpl'](this.calendars.get(calendarId).attributes));

      }
    },

    createCalendar: function(evt) {

      evt.preventDefault();

      var data = G.Utils.serializeForm($(evt.target));

      data.agencyId = this.options.agencyId;

      // Currently existing route, save it
      if (data.id != undefined && data.id != "") {
        this.calendars.get(data.id).save(data, {
          wait: true,
          success: _.bind(function(data) {
            this.onCalendarsReset();
            this.$('#calendar-create-modal').modal('hide');
            G.Utils.success('Calendar successfully saved');
          }, this),
          error: function() {
            G.Utils.error('Calendar save failed');
          }
        });
      } else {
        // New calendar, create it
        var model = this.calendars.create(data, {
          wait: true,
          success: _.bind(function(data) {

            G.session.calendar = data.id;
            this.onCalendarsReset();
            this.$('#calendar-select option[value=' + data.id + ']').prop('selected', true);
            this.$('#calendar-create-modal').modal('hide');
            G.Utils.success('Calendar successfully created');
          }, this),
          error: _.bind(function() {
            G.Utils.error('Calendar create failed');
          }, this)
        });
      }
    },

    onScheduleTypeChange : function(evt) {

      var selectedPatternId  = this.$('#tripPattern').val();

      var useFrequencyChange  = this.$('#scheduleTypeFrequency').prop("checked");

      if(this.model.tripPatterns.get(selectedPatternId) != undefined) {

        var useFrequencyCurrent = this.model.tripPatterns.get(selectedPatternId).get('useFrequency');

        if(useFrequencyCurrent != useFrequencyChange) {


          if (G.Utils.confirm(G.strings.tripInfoClearAllTripsConfirm)) {

            if(useFrequencyChange)
              this.model.tripPatterns.get(selectedPatternId).useFrequency();
            else
              this.model.tripPatterns.get(selectedPatternId).useTimetable();

            this.updatePatternType();

          }
          else {
            if(useFrequencyCurrent)
              this.$('#scheduleTypeFrequency').prop("checked", true);
            else
              this.$('#scheduleTypeTimetable').prop("checked", true);



          }
        }
      }
    },

    initFrequency : function() {

      this.$('#route-save-btn').show();
      this.$('#trip-frequency').show();
      this.$('#trip-timetable').hide();

      this.updateTrips();

    },

    initTimetable : function() {

      this.$('#route-save-btn').hide();
      this.$('#trip-frequency').hide();
      this.$('#trip-timetable').show();

      this.onCalendarsReset();

      this.onTimetableCalendarSelectChange();

    },

    updateTimetableLink : function () {
      this.$('#timetable-edit-btn').attr('href',
        G.config.baseUrl + 'timetable/' +
        this.$('#tripPattern').val() + '/' +
        this.$('#calendar').val()
      );
    },

    editTimetableInPlace : function () {
      // var this_ = this;

      // if(this.$('#trip-timetable #calendar').val() != "") {

      //   var selectedPatternId  = this.$('#tripPattern').val();

      //   var selectedPattern = this.model.tripPatterns.get(selectedPatternId);

      //   var patternStops = selectedPattern.get('patternStops');


      //   var colHeaders = ['Trip Headsign'];

      //   _.each(patternStops, function(patternStop) {

      //     colHeaders.push(patternStop.stop.stopName + "<br/>" +  this_.convertTime(patternStop.defaultTravelTime) + " | " + this_.convertTime(patternStop.defaultDwellTime));

      //   });

      //   var trips = [];

      //   _.each(selectedPattern.trips.models, function(tripData) {

      //     var trip = [];

      //     trip.push(tripData.get('tripHeadsign'));

      //     _.each(tripData.get('stopTimes'), function(stopTimeData) {

      //       trip.push(stopTimeData);

      //     });

      //     trips.push(trip);

      //   });

      //   var stopTimeRenderer = function(instance, td, row, col, prop, value, cellProperties) {

      //       var stopTimeObject = instance.getDataAtCell(row, col);

      //       if(stopTimeObject != null) {
      //         value = this_.convertTime(stopTimeObject.arrivalTime);

      //         if(stopTimeObject.boardOnly)
      //           value = value + " (BO)";
      //         else if(stopTimeObject.alightOnly)
      //           value = value + " (AO)";
      //       }
      //       else
      //         value = "--";


      //       Handsontable.TextRenderer(instance, td, row, col, prop, value, cellProperties);
      //   };

      //   if(this.table == undefined) {

      //     this.table = $('#timetable-data').handsontable({
      //       data: trips,
      //       minSpareRows: 3,
      //       colHeaders: colHeaders,
      //       rowHeaders: true,
      //       contextMenu: true,
      //       cells: function (row, col, prop) {
      //         if (col > 0) {
      //           return { type: {renderer: stopTimeRenderer}};
      //         }
      //       }
      //     });

      //   }
      //   else {

      //     this.table.updateColHeaders(colHeaders);
      //     this.table.updateData(trips);

      //     this.table.render();
      //   }


      //   $('#timetable-data').show();
      // }
      // else {
      //   $('#timetable-data').hide();
      // }
    },

    onCalendarsReset: function(evt) {

      this.calendars.getRoutesText();

      var data = {
        items: this.calendars.toJSON()
      }

      var selectedPatternId  = this.$('#tripPattern').val();
      var selectedTripId  = this.$('#trip').val();

      this.$('#timetable-calendar-select').html(ich['timetable-calendar-select-tpl'](data));
      this.$('#frequency-calendar-select').html(ich['frequency-calendar-select-tpl'](data));

      if(this.model.tripPatterns.get(selectedPatternId) != undefined && selectedTripId != undefined) {
        if(this.model.tripPatterns.get(selectedPatternId).trips.get(selectedTripId).attributes.serviceCalendar != undefined)
          this.$('#calendar').val(this.model.tripPatterns.get(selectedPatternId).trips.get(selectedTripId).attributes.serviceCalendar.id);
        else
          this.$('#calendar').val(G.session.calendar);
      }

    },

    onFrequencyCalendarSelectChange: function(evt) {


      var useFrequency  = this.$('#scheduleTypeFrequency').prop("checked");

      var selectedPatternId  = this.$('#tripPattern').val();

      if(selectedPatternId == null || selectedPatternId == "")
        return;


      if(this.$('#trip-frequency #calendar').val() == "") {

        this.$('#calendar-modify-btn').addClass("disabled");

      }
      else {

          G.session.calendar = this.$('#trip-frequency #calendar').val();

          this.$('#calendar-modify-btn').removeClass("disabled");
      }

    },

    onTimetableCalendarSelectChange: function(evt) {

      var useFrequency  = this.$('#scheduleTypeFrequency').prop("checked");

      var selectedPatternId  = this.$('#tripPattern').val();

      var selectedCalendarId = this.$('#trip-timetable #calendar').val();

      if(selectedPatternId == null || selectedPatternId == "")
        return;

      if(this.$('#trip-timetable #calendar').val() == "")
        $('#csv-upload-download').hide();
      else {

        $('#csv-upload-download').show();

        var csvDataUploader = new qq.FileUploaderBasic ({
                action: G.config.baseUrl + "application/uploadCsvSchedule",
                allowedExtensions: ['csv'],
                button: $('#timetable-upload-btn')[0],
            multiple: false,
            onSubmit: function(id, fileName){ csvDataUploader.setParams({patternId: selectedPatternId, calendarId: selectedCalendarId });   }
            });
      }

      this.updateTimetableLink();
    },

    onTripPatternsReset: function() {

      this.updateTripPatterns();

      this.onTripPatternChange();

    },

    onTripPatternChange: function(evt) {

      G.session.tripPattern = this.$('#tripPattern').val();

      G.session.trip = -1;

      this.updatePatternType();

      this.updateTimetableLink();

    },

    onTripChange: function(evt) {

      G.session.trip = this.$('#trip').val();

      this.updateTripDetail();

    },

    onTripsLoaded: function(evt) {

      var selectedPatternId  = this.$('#tripPattern').val();

      var data = [];

      if(this.model.tripPatterns.get(selectedPatternId).trips != undefined && this.model.tripPatterns.get(selectedPatternId).trips.models != undefined) {
        data = {
          items: this.model.tripPatterns.get(selectedPatternId).trips.models
        }
      }

      this.$('#trip-select').html(ich['trip-select-tpl'](data));

      this.$('#trip-details').html("");

      this.$('#trip option[value=' + G.session.trip+ ']').prop('selected', true);

      this.bindPopovers();

      this.onTripChange();

    },


    downloadSchedule: function(e) {

        var selectedPatternId  = this.$('#tripPattern').val();
        var calendarId = this.$('#calendar').val();

        if(calendarId != undefined && selectedPatternId != undefined)
        {
          e.preventDefault();  //stop the browser from following
          window.location.href =  G.config.baseUrl + "export/schedule?patternId=" + selectedPatternId + "&calendarId=" + calendarId;
        }
    },



    // converts mm:ss into seconds or returns 0
    calcTime: function(timeString) {

      var time = 0;

      timeStringParts = timeString.split(":");

      // hh:mm
      if(timeStringParts.length == 2)
        time = (parseInt(timeStringParts[0]) * 60 * 60) + (parseInt(timeStringParts[1]) * 60);

      // hh:mm:ss
      else if(timeStringParts.length == 3)
        time = (parseInt(timeStringParts[0]) * 60 * 60) + (parseInt(timeStringParts[1]) * 60) + parseInt(timeStringParts[2]);

      return time;
    },

    convertTime: function(time) {

      var seconds = time % 60;
      var totalMinutes = (time - seconds) / 60;
      var minutes = totalMinutes % 60;
      var hours = (totalMinutes - minutes) / 60;

      var timeStr = this._pad(hours, 2) + ':' + this._pad(minutes, 2) + ':' + this._pad(seconds, 2);

      return timeStr;
    },

    _pad: function (num, size) {
      var s = num+"";
      while (s.length < size) s = "0" + s;
      return s;
    }

  });
})(GtfsEditor, jQuery, ich);
