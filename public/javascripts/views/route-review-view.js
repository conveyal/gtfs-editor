var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  G.RouteReviewView = Backbone.View.extend({

  	events: {
      'submit #route-review-form': 'saveRoute'
    },

	initialize: function () {
	
		this.model.tripPatterns.fetch({data: {routeId: this.model.id}});

	},

    render: function () {
      // Set the container

      var data = this.model.toJSON();

      var $tpl = ich['route-review-container-tpl'](data);

     

      $tpl
        .find('#status option[value="'+data.status +'"]')
        .attr('selected', true);

      this.$el.html($tpl);

      // Add the route summary
      new G.RouteSummaryView({
        el: this.$('.route-context'),
        model: this.model
      }).render();

    },

    saveRoute: function(evt) {

      evt.preventDefault();

      var data = G.Utils.serializeForm($(evt.target));

      this.model.save(data, {
        wait: true,
        success: _.bind(function() {
          G.Utils.success('Route successfully saved');
        }, this),
        error: function() {
          G.Utils.error('Route save failed');
        }
      });
    }

  });
})(GtfsEditor, jQuery, ich);