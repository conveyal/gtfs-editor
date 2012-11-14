var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  var _$content = $('#route-step-content'),
      _routeCollection = new G.Routes();
      _steps = ['info', 'stops', 'patterns', 'trips', 'review'],
      _views = {
        'info': G.RouteInfoView
      };

  G.Router = Backbone.Router.extend({
    routes: {
      '': 'root'
    },

    initialize: function () {
      var stepRegex      = new RegExp('^('+_steps.join('|')+')\/?$');
          modelStepRegex = new RegExp('^([^\/]+)?\/('+_steps.join('|')+')?\/?$');

      this.route(stepRegex, 'setStep');
      this.route(modelStepRegex, 'setModelStep');

      $('.route-link').on('click', function (evt) {
        evt.preventDefault();
        _router.navigate($(this).attr('data-route-step'), {trigger: true});
      });
    },

    test: function () {
      console.log(arguments);
    },

    root: function() {
      this.navigate(_steps[0], {trigger: true});
    },

    setStep: function (step) {
      this.setModelStep(null, step);
    },

    setModelStep: function(id, step){
      var view,
          model = id ? _routeCollection.get(parseInt(id, 10)) :
                  new _routeCollection.model();

      if (model) {
        view = new _views[step]({
          model: model
        });

        // Update the active step classes
        $('.route-link').parent('li').removeClass('active');
        $('.route-link[data-route-step="'+step+'"]').parent().addClass('active');

        this.showView(view);
      } else {
        this.root();
      }
    },

    showView: function (view) {
      _$content.html(view.render().el);
    }
  });

  _router = new G.Router();
  // Populate the route collection
  _routeCollection.fetch({
    success: function(collection, response, options) {
      $(function(){
        Backbone.history.start({pushState: true, root: '/route/'});
      });
    }
  });
})(GtfsEditor, jQuery, ich);