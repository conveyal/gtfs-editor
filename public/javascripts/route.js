var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  var _$content = $('#route-step-content'),
      _routeCollection = new G.Routes(),
      _router,
      _steps = ['info', 'stops', 'patterns', 'trips', 'review'],
      _views = {
        'info': function(model) {
          return new G.RouteInfoView({
            collection: _routeCollection,
            model: model,
            agencyId: _agencyId,
            onSave: function(model) {
              _router.navigate(model.id + '/stops', {trigger: true});
            },
            onCancel: function() {
              console.log('cancel');
            }
          });
        },
        'stops': function() { return new Backbone.View(); },
        'patterns': function() { return new Backbone.View(); },
        'trips': function() { return new Backbone.View(); },
        'review': function() { return new Backbone.View(); }
      };

  G.Router = Backbone.Router.extend({
    routes: {
      '': 'root'
    },

    initialize: function () {
      var stepRegex      = new RegExp('^(info)\/?$');
          modelStepRegex = new RegExp('^([^\/]+)?\/('+_steps.join('|')+')?\/?$');

      this.route(stepRegex, 'setStep');
      this.route(modelStepRegex, 'setStepWithModel');

      $('.route-link').on('click', function (evt) {
        evt.preventDefault();

        // Only trigger if not disabled. TODO: make this smarter
        if ($(this).parent('li').hasClass('disabled') === false) {
          _router.navigate($(this).attr('data-route-step'), {trigger: true});
        }
      });
    },

    test: function () {
      console.log(arguments);
    },

    root: function() {
      this.navigate(_steps[0], {trigger: true});
    },

    setStep: function (step) {
      this.setStepWithModel(null, step);
    },

    setStepWithModel: function(id, step){
      var model;

      if (id) {
        model = _routeCollection.get(parseInt(id, 10));

        if (model) {
          this.enableDependentSteps();
          this.showStep(step, model);
        }
      } else {
        this.disableDependentSteps();

        if (step === 'info') {
          this.showStep(step, model);
        }
      }
    },

    showStep: function (step, model) {
      var view = _views[step](model);

      // Update the active step classes
      $('#route-nav li').removeClass('active');
      $('.route-link[data-route-step="'+step+'"]').parent('li').addClass('active');

      _$content.html(view.render().el);
    },

    enableDependentSteps: function() {
      $('#route-nav li').removeClass('disabled');
    },

    disableDependentSteps: function() {
      $('.route-link:not([data-route-step="info"])').parent('li').addClass('disabled');
    }
  });

  _router = new G.Router();

  G.RoutePage = {
    init: function(agencyId) {
      _agencyId = agencyId;

      // Populate the route collection
      _routeCollection.fetch({
        success: function(collection, response, options) {
          Backbone.history.start({pushState: true, root: '/route/'});
        }
      });
    }
  };
})(GtfsEditor, jQuery, ich);