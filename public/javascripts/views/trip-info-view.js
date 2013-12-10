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
      'change #tripPattern': 'onTripPatternChange',
      'change #trip': 'onTripChange',
      'change #calendar': 'onCalendarSelectChange',

      'change input[name=scheduleType]': 'onScheduleTypeChange',

      'submit #trip-create-form': 'onTripCreate',
      'submit #trip-copy-form': 'onTripCopy',
      'submit #trip-info-form':  'onSaveTrip',
      'submit #create-calendar-form': 'createCalendar'
    },

    initialize: function () {


      this.agencyTrips = new G.Trips();
      this.calendars = new G.Calendars();

      this.calendars.fetch({data: {agencyId: this.model.get('agency').id}});

      this.calendars.on('add', this.onCalendarsReset, this);
      this.calendars.on('remove', this.onCalendarsReset, this);
      this.calendars.on('reset', this.onCalendarsReset, this);

      this.model.tripPatterns.fetch({data: {routeId: this.model.id}});

      this.model.tripPatterns.on('add', this.onTripPatternsReset, this);
      this.model.tripPatterns.on('remove', this.onTripPatternsReset, this);
      this.model.tripPatterns.on('reset', this.onTripPatternsReset, this);

      _.bindAll(this, 'onTripPatternChange', 'bindPopovers', 'onTripChange', 'onTripsLoaded', 'createCalendar', 'showCreateTrip', 'showCopyTrip', 'onTripCreate', 'onTripCopy', 'cancelTripCreate', 'deleteTrip', 'onSaveTrip', 'showCalendarCreateModal', 'onCalendarsReset', 'calendarCreateClose', 'onCalendarSelectChange', 'onLoadAgencyTrips', 'onScheduleTypeChange', 'initTimetable', 'updatePatternType');

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

      this.$('#trip-timetable').hide();
      this.$('#trippattern-type').hide();

      this.onTripPatternsReset();
  
      return this;
    },

    onSaveTrip: function(evt){

      evt.preventDefault();

      var selectedPatternId  = this.$('#tripPattern').val();

      var data = G.Utils.serializeForm($(evt.target));

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

      var tripData = {
        useFrequency: true,
        pattern: selectedPatternId,
        tripDescription: this.$('[name=name]').val(),
        serviceCalendar: existingTrip.serviceCalendar.id,
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

      this.onTripPatternChange();

    },

    updatePatternType: function() {

        var selectedPatternId  = this.$('#tripPattern').val();

        this.$('#trip-details').html("");
        this.$('#trip-select').html("");

        if(this.model.tripPatterns.get(selectedPatternId) != undefined) {

          this.$('#trippattern-type').show();

          if(this.model.tripPatterns.get(selectedPatternId).get('useFrequency') == false  || this.model.tripPatterns.get(selectedPatternId).get('useFrequency') == null) {
            this.model.tripPatterns.get(selectedPatternId).set('useFrequency', false);

            this.$('#scheduleTypeFrequency').prop("checked", false);
            this.$('#scheduleTypeTimetable').prop("checked", true);

            this.initTimetable();

          }
          else {
            this.model.tripPatterns.get(selectedPatternId).set('useFrequency', true);

            this.$('#scheduleTypeFrequency').prop("checked", true);
            this.$('#scheduleTypeTimetable').prop("checked", false);
            this.updateTrips();
          }    
        }
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

        this.onCalendarSelectChange();

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

      data.agency = this.options.agencyId;

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

      var useFrequency  = this.$('#scheduleTypeFrequency').prop("checked")

      if(this.model.tripPatterns.get(selectedPatternId) != undefined) {
        
        this.model.tripPatterns.get(selectedPatternId).attributes.useFrequency = useFrequency;

        if(useFrequency) {
          this.$('#trip-frequency').show();
          this.$('#trip-timetable').hide();
          this.updateTrips();
        }
        else  {
          this.$('#trip-frequency').hide();
          this.$('#trip-timetable').show();
          this.initTimetable();
        }
      }
    },

    initTimetable : function() {

      var this_ = this;

      if(this.$('#trip-timetable #calendar').val() != "") {

        var selectedPatternId  = this.$('#tripPattern').val();

        var selectedPattern = this.model.tripPatterns.get(selectedPatternId);

        var patternStops = selectedPattern.get('patternStops');


        var colHeaders = ['Trip Headsign'];

        _.each(patternStops, function(patternStop) {

          colHeaders.push(patternStop.stop.stopName + "<br/>" +  this_.convertTime(patternStop.defaultTravelTime) + " | " + this_.convertTime(patternStop.defaultDwellTime));

        });

        var trips = [];

        _.each(selectedPattern.trips.models, function(tripData) {

          var trip = [];

          trip.push(tripData.get('tripHeadsign'));

          _.each(tripData.get('stopTimes'), function(stopTimeData) {

            trip.push(stopTimeData);

          });
          
          trips.push(trip);

        });

        var stopTimeRenderer = function(instance, td, row, col, prop, value, cellProperties) {

            var stopTimeObject = instance.getDataAtCell(row, col);

            if(stopTimeObject != null) {
              value = this_.convertTime(stopTimeObject.arrivalTime);

              if(stopTimeObject.boardOnly)
                value = value + " (BO)";
              else if(stopTimeObject.alightOnly)
                value = value + " (AO)";
            }
            else
              value = "--";

          
            Handsontable.TextRenderer(instance, td, row, col, prop, value, cellProperties);
        };

        if(this.table == undefined) {

          this.table = $('#timetable-data').handsontable({
            data: trips,
            minSpareRows: 3,
            colHeaders: colHeaders,
            rowHeaders: true,
            contextMenu: true,
            cells: function (row, col, prop) {
              if (col > 0) {
                return { type: {renderer: stopTimeRenderer}};
              } 
            }
          });

        }
        else {

          this.table.updateColHeaders(colHeaders);
          this.table.updateData(trips);

          this.table.render();
        }

        
        $('#timetable-data').show();
      }
      else {
        $('#timetable-data').hide();
      }
    },

    onCalendarsReset: function(evt) {
      
      var data = {
        items: this.calendars.models
      }

      var selectedPatternId  = this.$('#tripPattern').val();
      var selectedTripId  = this.$('#trip').val();

      this.$('#calendar-select').html(ich['calendar-select-tpl'](data));

      if(this.model.tripPatterns.get(selectedPatternId) != undefined) {
        if(this.model.tripPatterns.get(selectedPatternId).trips.get(selectedTripId).attributes.serviceCalendar != undefined)
          this.$('#calendar').val(this.model.tripPatterns.get(selectedPatternId).trips.get(selectedTripId).attributes.serviceCalendar.id);
        else  
          this.$('#calendar').val(G.session.calendar);
      }

      this.onCalendarSelectChange();

    },

    onCalendarSelectChange: function(evt) {


      var useFrequency  = this.$('#scheduleTypeFrequency').prop("checked");

      if(useFrequency) {

        if(this.$('#calendar').val() == "") {

          this.$('#calendar-modify-btn').addClass("disabled");

        }
        else {

          G.session.calendar = this.$('#calendar').val();

          this.$('#calendar-modify-btn').removeClass("disabled");

          var selectedPatternId  = this.$('#tripPattern').val();
          if(this.model.tripPatterns.get(selectedPatternId).attributes.useFrequency)
            this.initTimetable();
        }

      }
      else {

        this.initTimetable();

      }
    },  

    onTripPatternsReset: function() {
  
      this.updateTripPatterns();

      this.onTripPatternChange();

    },

    onTripPatternChange: function(evt) {

      G.session.tripPattern = this.$('#tripPattern').val();

      G.session.trip = -1;

      this.updatePatternType();

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

