var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  G.RouteInfoView = Backbone.View.extend({
    events: {
      'submit .route-info-form': 'save',
      'click #route-cancel-btn': 'cancel'
    },

    initialize: function () {
      this.onSave = this.options.onSave || function() {};
      this.onCancel = this.options.onCancel || function() {};

      this.collection.on('reset', this.render, this);

      if (this.model) {
        this.setModel(this.model);
      }
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
      var data = this.model ? this.model.toJSON() : {};

      data.agency = data.agency ? data.agency.id : this.options.agencyId;

      var $tpl = ich['route-info-tpl'](data);

      $tpl
        .find('#routeType option[value="'+data.routeType+'"]')
        .attr('selected', true);

      this.$el.html($tpl);

      this.$('input, select, textarea').popover({
        placement: 'right',
        trigger: 'focus'
      });

      return this;
    },

    cancel: function(evt) {
      evt.preventDefault();
      console.log('cancel', arguments);

      this.onCancel(this.model);
    },

    save: function(evt){
      evt.preventDefault();

      var data = G.Utils.serializeForm(this.$('form'));

      if (this.model) {
        // This seems redundant, but we need to call set first so that the
        // validator work as expected. Otherwise any attribute overrides in the
        // validator will not be set.
        this.model.set(data, { silent: true });
        this.model.save(null, {
          wait: true,
          success: _.bind(function() {
            this.onSave(this.model);
          }, this),
          error: function() { console.log('Oh noes! That save didn\'t work.'); }
        });
      } else {
        this.model = this.collection.create(data, {
          wait: true,
          success: _.bind(function() {
            this.onSave(this.model);
          }, this),
          error: _.bind(function() {
            this.model = null;
            console.log('Oh noes! That create didn\'t work.');
          }, this)
        });

        this.setModel(this.model);
      }
    }
  });
})(GtfsEditor, jQuery, ich);
