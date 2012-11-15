var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  G.RouteInfoView = Backbone.View.extend({
    events: {
      'click #route-save-btn': 'save',
      'click #route-cancel-btn': 'cancel'
    },

    initialize: function () {
      this.onSave = this.options.onSave || function() {};
      this.onCancel = this.options.onCancel || function() {};
    },

    render: function () {
      var $tpl = ich['route-info-tpl'](this.model.toJSON());

      $tpl
        .find('#routeType option[value="'+this.model.get('routeType')+'"]')
        .attr('selected', true);

      this.$el.html($tpl);
      return this;
    },

    cancel: function(evt) {
      evt.preventDefault();
      console.log('cancel', arguments);

      this.onCancel(this.model);
    },

    save: function(evt){
      evt.preventDefault();

      var data = this.$('form').serializeObject(),
          $uncheckedCheckboxes = this.$('[type="checkbox"]:not(:checked)');

      $uncheckedCheckboxes.each(function(i, el) {
        if (el.name) {
          data[el.name] = false;
        }
      });

      // This seems redundant, but we need to call set first so that the
      // validator work as expected. Otherwise any attribute overrides in the
      // validator will not be set.
      this.model.set(data, { silent: true });
      this.model.save(null, {
        wait: true,
        success: _.bind(function() {
          this.onSave(this.model);
        }, this),
        error: function() { console.log('Oh noes! That didn\'t work.'); }
      });
    }
  });
})(GtfsEditor, jQuery, ich);