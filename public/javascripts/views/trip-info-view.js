var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  G.TripInfoView = Backbone.View.extend({
    events: {
      'click .create-trip': 'showCreateTrip', 
      'click .trip-create-cancel-btn': 'cancelTripCreate',
      'click #delete-trip-btn': 'deleteTrip',
      'click #calendar-create-btn': 'showCalendarCreateModal',
      'click #calendar-create-close': 'calendarCreateClose',
      'click #calendar-modify-btn': 'modifyCalendar',
      'change #tripPattern': 'onTripPatternChange',
      'change #trip': 'onTripChange',
      'change #calendar': 'onCalendarSelectChange',
      'submit #trip-create-form': 'onTripCreate',
      'submit #trip-info-form':  'onSaveTrip',
      'submit #create-calendar-form': 'createCalendar'
    },

    initialize: function () {

      this.calendars = new G.Calendars();

      this.calendars.fetch();

      this.calendars.on('add', this.onCalendarsReset, this);
      this.calendars.on('remove', this.onCalendarsReset, this);
      this.calendars.on('reset', this.onCalendarsReset, this);

      this.model.tripPatterns.fetch({data: {routeId: this.model.id}});

      this.model.tripPatterns.on('add', this.onTripPatternsReset, this);
      this.model.tripPatterns.on('remove', this.onTripPatternsReset, this);
      this.model.tripPatterns.on('reset', this.onTripPatternsReset, this);

      _.bindAll(this, 'onTripPatternChange', 'bindPopovers', 'onTripChange', 'onTripsLoaded', 'createCalendar', 'showCreateTrip', 'onTripCreate', 'cancelTripCreate', 'deleteTrip', 'onSaveTrip', 'showCalendarCreateModal', 'onCalendarsReset', 'calendarCreateClose', 'onCalendarSelectChange');

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

      this.updateTripPatterns();

      // Bind help popovers
      this.bindPopovers();

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
      this.$('#delete-trip-btn').hide();

      this.$('#trip-create').html(ich['trip-create-tpl']());

      // something upstream is causing this form not to bind automagically
      this.$('#trip-create-form').bind('submit', this.onTripCreate);

    },

    cancelTripCreate: function() {
    
      this.$('.create-trip').show();
      this.$('#delete-trip-btn').show();

      this.$('#trip-create').html("");
    },

    deleteTrip: function(evt) {

      var selectedPatternId  = this.$('#tripPattern').val();
      var selectedTripId  = this.$('#trip').val();

      if (this.model.tripPatterns.get(selectedPatternId) != undefined && this.model.tripPatterns.get(selectedPatternId).trips.get(selectedTripId) != undefined && G.Utils.confirm('Are you sure you want to delete this trip?')) {
        
        this.model.tripPatterns.get(selectedPatternId).trips.get(selectedTripId).destroy();

      }

    },

    onTripCreate: function(evt) {

      evt.preventDefault();

      var selectedPatternId  = this.$('#tripPattern').val();

      var tripData = {
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
          G.Utils.error('Failed to create trip pattern');
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

        //this.$('#trip-info-form').on('submit', this.onSaveTrip);

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

      var data = G.Utls.serializeForm($(evt.target));

      data.agency = this.options.agencyId;

      // Currently existing route, save it
      if (data.id != undefined && data.id != "") {
        this.calendars.get(data.id).save(data, {
          wait: true,
          success: _.bind(function() {
            this.onCalendarsReset();
            this.$('#calendar-create-modal').modal('hide');
            G.Utils.success('Calendar successfully saved');
          }, this),
          error: function() {
            G.Utils.error('Calendar save failed');
          }
        });
      } else {
        // New route, create it
        var model = this.calendars.create(data, {
          wait: true,
          success: _.bind(function(data) {
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

    onCalendarsReset: function(evt) {
      
       var data = {
        items: this.calendars.models
      }

      var selectedPatternId  = this.$('#tripPattern').val();
      var selectedTripId  = this.$('#trip').val();

      this.$('#calendar-select').html(ich['calendar-select-tpl'](data));

      if(this.model.tripPatterns.get(selectedPatternId) != undefined)
        this.$('#calendar').val(this.model.tripPatterns.get(selectedPatternId).trips.get(selectedTripId).attributes.serviceCalendar.id);

      this.onCalendarSelectChange();

    },

    onCalendarSelectChange: function(evt) {

      if(this.$('#calendar').val() == "") {

        this.$('#calendar-modify-btn').addClass("disabled");

      }
      else {

        this.$('#calendar-modify-btn').removeClass("disabled");

      }

    },  

    onTripPatternsReset: function() {
  
      this.updateTripPatterns();

      this.onTripPatternChange();

    },

    onTripPatternChange: function(evt) {

      G.session.tripPattern = this.$('#tripPattern').val();

      G.session.trip = -1;

      this.updateTrips();

    },

    onTripChange: function(evt) {

      G.session.trip = this.$('#trip').val();

      this.updateTripDetail();

    },

    onTripsLoaded: function(evt) {
      
      var selectedPatternId  = this.$('#tripPattern').val();

      var data = {
        items: this.model.tripPatterns.get(selectedPatternId).trips.models
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

