var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  G.Router = Backbone.Router.extend({
    routes: {
      '': 'showStep',
      ':step': 'showStep'
    },

    initialize: function () {
      var router = this;
      $('.route-link').on('click', function (evt) {
        evt.preventDefault();
        router.navigate($(this).attr('href'), {trigger: true});
      });
    },

    showStep: function (step) {
      if (!step) {
        step = '1';
        this.navigate(step);
      }

      $('.route-link').parent('li').removeClass('active');
      $('.route-link[href="'+step+'"]').parent().addClass('active');
      $('#route-step-content').html('This is step ' + step);
    }
  });

})(GtfsEditor, jQuery, ich);

var router;
$(function(){
  router = new GtfsEditor.Router();
  Backbone.history.start({pushState: true, root: '/route/'});
});