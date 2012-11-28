var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  G.RouteSummaryView = Backbone.View.extend({
    events: {
    },

    initialize: function () {
    },

    render: function () {

      this.$el.html(ich['route-summary-container-tpl'](this.model.toJSON()));

      var $popoverContent = ich['route-summary-tpl'](this.model.toJSON());
      $popoverContent
        .find('.route-summary-color')
        .css('background-color', '#' + this.model.get('routeColor'));

      this.$('.route-summary-link').popover({
        placement: 'bottom',
        trigger: 'hover',
        content: $popoverContent,
        html: true
      });
    }
  });
})(GtfsEditor, jQuery, ich);