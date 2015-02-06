/* Schedule exception editor page */

var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  /** A list of schedule exceptions */
  G.ScheduleExceptionList = Backbone.View.extend({
    render: function () {
      this.$el.html(ich['exception-list-tpl']({exceptions: this.collection.toJSON()}));
    }
  });

  /** Edit a single schedule exception */
  G.ScheduleExceptionEditor = Backbone.View.extend({
    events: {
      'click button.save': 'save',
      'click button.add-date': 'addDate',
      'click a.remove-date': 'removeDate',
      'change #exception-exemplar': 'showHideCustomSchedule'
    },

    initialize: function (attr) {
      this.calendars = attr.calendars;
      _.bindAll(this, 'save', 'addDate', 'removeDate');
    },

    render: function () {
      var instance = this;
      this.$el.html(ich['exception-edit-tpl'](_.extend({calendars: this.calendars.toJSON()}, this.model.toJSON())));
      this.$('#exception-exemplar').val(this.model.get('exemplar'));

      _.each(this.model.get('customSchedule'), function (calendar) {
        instance.$('#custom-schedule-container input[value="' + calendar.id + '"]').prop('checked', true);
      });

      this.$('#exception-date').datepicker()
        .datepicker('setValue', new Date());
      this.showHideCustomSchedule();
      this.renderDates();
    },

    addDate: function () {
      var utcDate = this.$('#exception-date').data('datepicker').date;
      this.model.addDate(utcDate);
      this.renderDates();
    },

    removeDate: function (e) {
      e.preventDefault();
      var $t = $(e.target).parent();
      this.model.removeDate($t.attr('data-year'), $t.attr('data-month'), $t.attr('data-date'));
      this.renderDates();
    },

    renderDates: function () {
      var renderedDates = _.map(this.model.get('dates'), function (epoch) {
        var date = new Date(epoch);
        // TODO: i18n
        return {
          date: date.getUTCDate(),
          month: date.getUTCMonth(),
          year: date.getUTCFullYear(),
          renderedDate: moment([date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate()]).format('dddd, Do MMMM YYYY')
        };
      });
      this.$('#exception-date-list').html(ich['exception-date-list-tpl']({dates: renderedDates}));
    },

    /** show or hide the custom schedule based on whether the exception is custom or not */
    showHideCustomSchedule: function () {
      if (this.$('#exception-exemplar').val() == 'CUSTOM')
        this.$('#custom-schedule-container').removeClass('hidden');
      else
        this.$('#custom-schedule-container').addClass('hidden');
    },

    /** save the model with the updates from the form */
    save: function () {
      this.model.set('name', this.$('#exception-name').val());
      // dates already set
      this.model.set('exemplar', this.$('#exception-exemplar').val());

      // we save the custom schedule even if we're not using it. It doesn't hurt anything and that way it's there
      // if anyone ever wants it back
      var customSchedule = [];

      this.$('#custom-schedule-container input[name="custom-schedule"]').each(function () {
        var $el = $(this);
        if ($el.is(':checked'))
          customSchedule.push({id: $el.attr('value')});
      });

      this.model.set('customSchedule', customSchedule);

      this.model.save();
    }
  });

  G.ScheduleExceptionRouter = Backbone.Router.extend({
    routes: {
      '': 'index',
      'new':          'exception',
      'edit/:exceptionId': 'exception'
    },

    index: function () {
      // pull down the data on schedule exceptions for this agency
      var exceptions = new G.ScheduleExceptions();
      exceptions.fetch({data: {agencyId: G.session.agencyId}})
      .done(function () {
        new G.ScheduleExceptionList({collection: exceptions, el: '#exceptions'}).render();
      });
    },

    exception: function (exceptionId) {
      var exception;

      // get the calendars
      var calendars = new G.Calendars();
      var promises = [calendars.fetch({data: {agencyId: G.session.agencyId}})];

      if (exceptionId === undefined) {
        // new exception
        exception = new G.ScheduleException({agency: {id: G.session.agencyId}});
      } else {
        // existing exception
        exception = new G.ScheduleException({id: exceptionId});
        promises.push(exception.fetch());
      }

      Promise.all(promises).then(function () {
          new G.ScheduleExceptionEditor({model: exception, calendars: calendars, el: '#exceptions'}).render();
        });
      }
  });

  $(document).ready(function () {
    new G.ScheduleExceptionRouter();
    Backbone.history.start();
  });
})(GtfsEditor, jQuery, ich);
