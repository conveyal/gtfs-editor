var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  G.RouteSummaryView = Backbone.View.extend({
    render: function () {
      // Set the container
      this.$el.html(ich['route-summary-container-tpl'](this.model.toJSON()));

      // Construct the popover content in memory
      var $popoverContent = ich['route-summary-tpl'](this.model.toJSON());
      $popoverContent
        .find('.route-summary-color')
        .css('background-color', '#' + this.model.get('routeColor'));

      // Init the popover plugin
      this.$('.route-summary-link').popover({
        placement: 'bottom',
        trigger: 'hover',
        content: $popoverContent,
        html: true
      });
    }
  });
})(GtfsEditor, jQuery, ich);