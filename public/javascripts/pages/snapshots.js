/** Snapshot save/restore page */

var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  G.SnapshotView = Backbone.View.extend({
    events: {
      'click #new-snapshot': 'showNewSnapshotDialog',
      'click .restore-snapshot': 'showRestoreSnapshotDialog',
      'click #take-snapshot': 'takeSnapshot',
      'click #take-restore': 'takeSnapshotAndRestore'
    },

    initialize: function () {
      _.bindAll(this, 'showNewSnapshotDialog', 'showRestoreSnapshotDialog', 'takeSnapshot', 'takeSnapshotAndRestore');
      this.listenTo(this.collection, 'reset', this.render);
    },

    showRestoreSnapshotDialog: function (e) {
      this.$('#modal-container').html(ich['snapshot-new-tpl']({restore: $(e.target).attr('data-snapshot')}))
        .find('.modal').modal('show');

        var dfrom = new Date();
        dfrom.setDate(dfrom.getDate() - 7);
        this.$('#valid-from').datepicker()
        .datepicker('setValue', dfrom);

        var date = new Date();
        date.setYear(date.getYear() + 1);
        this.$('#valid-to').datepicker()
        .datepicker('setValue', date);
    },

    showNewSnapshotDialog: function () {
      this.$('#modal-container').html(ich['snapshot-new-tpl']({restore: false})).find('.modal').modal('show');

      var dfrom = new Date();
      dfrom.setDate(dfrom.getDate() - 7);
      this.$('#valid-from').datepicker()
        .datepicker('setValue', dfrom);

      var date = new Date();
      date.setYear(date.getYear() + 1);
      this.$('#valid-to').datepicker()
        .datepicker('setValue', date);
    },

    showRestoredStopsDialog: function (stops) {
      this.$('#modal-container').html(ich['snapshot-stops-tpl']({stops: stops})).find('.modal').modal('show');
    },

    takeSnapshot: function () {
      var instance = this;
      var name = this.$('#snapshot-name').val();
      var dfrom = this.toIso(this.$('#valid-from').data('datepicker').getDate());
      var dto = this.toIso(this.$('#valid-to').data('datepicker').getDate());

      this.$('#modal-container button.snapshot').prop('disabled', true).text('Saving . . .');

      this.collection.create({
            name: name,
            validFrom: dfrom,
            validTo: dto,
            agencyId: G.session.agencyId
          }, {
            wait: true,


      success: function () {
        this.$('#modal-container > .modal').modal('hide').remove();
        instance.collection.fetch({reset: true, data: {agencyId: G.session.agencyId}});
      }});
    },

    /** convert a local date to an iso date */
    toIso: function (dfrom) {
      return dfrom.getFullYear() + '-' + (dfrom.getMonth() + 1) + '-' + dfrom.getDate();
    },

    takeSnapshotAndRestore: function (e) {
      var instance = this;
      var name = this.$('#snapshot-name').val();
      this.$('#modal-container button.snapshot').prop('disabled', true).text('Saving . . .');
      this.collection.create({name: name, agencyId: G.session.agencyId},
        {wait: true, success: function () {
          var id = $(e.target).attr('data-snapshot');
          instance.$('.restore-snapshot').prop('disabled', true);
          var originalHtml = this.$('.restore-snapshot[data-snapshot="' + id + '"]').html();
          instance.$('.restore-snapshot[data-snapshot="' + id + '"]').text('Restoring . . .');
          instance.collection.get(id).restore().done(function (stops) {
            instance.$('#modal-container > .modal').modal('hide').remove();
            G.Utils.success('Snapshot restored');

            if (stops.length > 0) {
              instance.showRestoredStopsDialog(stops);
              instance.$('#modal-container .modal').on('hide', function () {
                // don't pull down the data and re-render until the modal is no longer shown
                instance.collection.fetch({reset: true, data: {agencyId: G.session.agencyId}});
              });
            } else {
              // this will restore all of the buttons when it completes
              instance.collection.fetch({reset: true, data: {agencyId: G.session.agencyId}});
            }
          });
        }});
    },

    onModelAdd: function () {
      this.render();
    },

    render: function () {
      // if there is a modal displayed, hide it and save it to be reshown
      this.$('#modal-container > .modal').modal('hide');

      var snapshots = this.collection.toJSON();

      _.each(snapshots, function (snapshot) {
        var date = new Date(snapshot.snapshotTime);
        snapshot.renderedDate = moment([date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate()]).format('YYYY-MM-DD');
      });

      this.$el.html(ich['snapshot-list-tpl']({snapshots: snapshots, agencyName: G.session.agencies[G.session.agencyId].name}));

      return this;
    }
  });

  var snapshots = new G.Snapshots();
  snapshots.fetch({data: {agencyId: G.session.agencyId}}).done(function () {
    new G.SnapshotView({collection: snapshots, el: '#snapshots'}).render();
  });
})(GtfsEditor, jQuery, ich);
