var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  G.RouteInfoView = Backbone.View.extend({
    initialize: function () {

    },

    render: function () {
      this.$el.html(ich['route-info-tpl'](this.model.toJSON()));
      return this;
    }
  });
})(GtfsEditor, jQuery, ich);