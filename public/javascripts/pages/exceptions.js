/* Schedule exception editor page */

var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  /** A list of schedule exceptions */
  G.ScheduleExceptionList = Backbone.View.extend({
    events: {
      'click .remove-exception': 'removeException'
    },

    initialize: function () {
      _.bindAll(this, 'removeException');
    },

    removeException: function (e) {
      this.collection.get($(e.target).attr('data-id')).destroy();
      this.render();
    },

    render: function () {
      var exceptions = this.collection.toJSON();

      _.each(exceptions, function (ex) {
        var dates = ex.dates.slice(0, ex.dates.length);
        dates.sort();
        var rendered = _.map(dates, function (dateLong) {
          var date = new Date(dateLong);
          return moment([date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate()]).format('MM/DD/YYYY');
        });

        if (rendered.length > 5) {
          rendered = rendered.slice(0, 5);
          rendered.push('...');
        }

        ex.datesText = rendered.join(', ');
      });

      this.$el.html(ich['exception-list-tpl']({exceptions: exceptions}));
    }
  });

  /** Edit a single schedule exception */
  G.ScheduleExceptionEditor = Backbone.View.extend({
    events: {
      'click a.save': 'save',
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

      this.calendars.getRoutesText();

      this.$el.html(ich['exception-edit-tpl'](_.extend({calendars: this.calendars.toJSON()}, this.model.toJSON())));
      this.$('#exception-exemplar').val(this.model.get('exemplar'));

      _.each(this.model.get('customSchedule'), function (calendar) {
        instance.$('#custom-schedule-container input[value="' + calendar + '"]').prop('checked', true);
      });

      this.$('#exception-date').datepicker()
        .datepicker('setValue', new Date());
      this.showHideCustomSchedule();
      this.renderDates();
    },

    addDate: function () {
      var utcDate = this.$('#exception-date').data('datepicker').date;
      this.model.addDate(utcDate);
      this.$('#enter-date').addClass("hidden");
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
    save: function (e) {
      e.preventDefault();

      // don't let them save an exception with no dates
      if (this.model.get('dates').length === 0)
        this.$('#enter-date').removeClass("hidden");

      this.model.set('name', this.$('#exception-name').val());
      // dates already set
      this.model.set('exemplar', this.$('#exception-exemplar').val());

      // we save the custom schedule even if we're not using it. It doesn't hurt anything and that way it's there
      // if anyone ever wants it back
      var customSchedule = [];

      this.$('#custom-schedule-container input[name="custom-schedule"]').each(function () {
        var $el = $(this);
        if ($el.is(':checked'))
          customSchedule.push($el.attr('value'));
      });

      this.model.set('customSchedule', customSchedule);

      this.model.save({}, {
        success: function () {
          G.Utils.success('Exception saved');
          window.location.hash = '#';
        },
        error: function () {
          G.Utils.error('Exception save failed. Exception dates must be unique.');
        }
      });
    }
  });

  G.ScheduleExceptionRouter = Backbone.Router.extend({
    routes: {
      '': 'index',
      'new':          'exception',
      'edit/:exceptionId': 'exception'
    },

    index: function () {
      var rtr = this;

      // pull down the data on schedule exceptions for this agency
      var exceptions = new G.ScheduleExceptions();
      exceptions.fetch({data: {agencyId: G.session.agencyId}})
      .done(function () {
        rtr.render(new G.ScheduleExceptionList({collection: exceptions}));
      });
    },

    exception: function (exceptionId) {
      var rtr = this;
      var exception = null;

      // get the calendars
      var calendars = new G.Calendars();
      var promises = [calendars.fetch({data: {agencyId: G.session.agencyId}})];

      if (exceptionId === undefined) {
        // new exception
        exception = new G.ScheduleException({agencyId: G.session.agencyId});
      } else {
        // existing exception
        exception = new G.ScheduleException({id: exceptionId});
        promises.push(exception.fetch());
      }

      Promise.all(promises).then(function () {
          rtr.render(new G.ScheduleExceptionEditor({model: exception, calendars: calendars}));
        });
      },

      // http://stackoverflow.com/questions/9079491/cleaning-views-with-backbone-js
      render: function (view) {
        if (this.currentView)
          this.currentView.remove();

        var el = document.createElement('div');
        var $el = $(el);
        $el.appendTo($('#exceptions'));
        view.setElement(el);

        this.currentView = view;
        view.render();
      }
  });

  $(document).ready(function () {
    new G.ScheduleExceptionRouter();
    Backbone.history.start();
  });
})(GtfsEditor, jQuery, ich);
