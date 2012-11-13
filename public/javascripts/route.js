var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  var router;

  G.Router = Backbone.Router.extend({
    routes: {
      '': 'setStep',
      ':step': 'setStep'
    },

    initialize: function () {
      var router = this;
      $('.route-link').on('click', function (evt) {
        evt.preventDefault();
        router.navigate($(this).attr('data-route-step'), {trigger: true});
      });
    },

    setStep: function (step) {
      if (!step) {
        step = '1';
        this.navigate(step);
      }

      $('.route-link').parent('li').removeClass('active');
      $('.route-link[data-route-step="'+step+'"]').parent().addClass('active');

      this.showView(step);
    },

    showView: function (view) {
      $('#route-step-content').html('This is step ' + view);
    }
  });





  $(function(){
    router = new G.Router();
    Backbone.history.start({pushState: true, root: '/route/'});
  });

})(GtfsEditor, jQuery, ich);