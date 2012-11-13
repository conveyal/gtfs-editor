var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  G.Router = Backbone.Router.extend({
    routes: {
      '': 'showStep',
      ':step': 'showStep'
    },

    initialize: function () {
      var router = this;
      $('#route-nav').on('click', 'a', function (evt) {
        evt.preventDefault();
        router.navigate($(this).attr('href'), {trigger: true});
      });
    },

    showStep: function (step) {
      if (!step) {
        step = '1';
        this.navigate(step);
      }

      $('#route-step-content').html('This is step ' + step);
    }
  });

})(GtfsEditor, jQuery, ich);

var router;
$(function(){
  router = new GtfsEditor.Router();
  Backbone.history.start({pushState: true, root: '/route/'});
});