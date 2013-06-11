var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  var _routeCollection = new G.Routes(),
      _stopCollection = new G.Stops(),
      _router,
      _steps = ['info', 'stops', 'trippatterns', 'trips', 'review'],
      _instantiatedViews = {}
      _views = {
        'info': function(model) {
          if(_instantiatedViews['info'] == null) {

            _instantiatedViews['info'] = new G.RouteInfoView({
              el: '#route-step-content',
              collection: _routeCollection,
              model: model,
              agencyId: _agencyId,
              onSave: function(model) {
                _router.navigate(model.id + '/stops', {trigger: true});
              }
            });

          }

          return _instantiatedViews['info'];
        },
        'stops': function(model) {
          if(_instantiatedViews['stops'] == null) {

            _instantiatedViews['stops'] = new G.RouteStopsView({
              el: '#route-step-content',
              collection: _stopCollection,
              model: model, //Route info model
              agencyId: _agencyId
            }); 
          }

          return _instantiatedViews['stops'];
        },
        'trippatterns': function(model) {
          if(_instantiatedViews['trippatterns'] == null) {
            _instantiatedViews['trippatterns'] = new G.RouteTripPatternsView({
              el: '#route-step-content',
              model: model, //Route info model
              stops: _stopCollection,
              agencyId: _agencyId
            });
          }

          return _instantiatedViews['trippatterns'];
        },
        'trips': function(model) {  
          if(_instantiatedViews['trips'] == null) {
            _instantiatedViews['trips'] = new G.TripInfoView({
              el: '#route-step-content',
              model: model, //Route info model
              stops: _stopCollection,
              agencyId: _agencyId
            });
          }

          return _instantiatedViews['trips']; 
        },
        'review': function(model) { 
          if(_instantiatedViews['review'] == null) {
            _instantiatedViews['review'] = new G.RouteReviewView({
              el: '#route-step-content',
              model: model, //Route info model
              stops: _stopCollection,
              agencyId: _agencyId
            });
          }

          return _instantiatedViews['review']; 
         }
      };

  G.Router = Backbone.Router.extend({
    routes: {
      '': 'root'
    },

    initialize: function () {
      // Fancy routes are not support above, so it's being done here.
      var stepRegex = new RegExp('^([^\/]+)?\/?('+_steps.join('|')+')?\/?$');
      this.route(stepRegex, 'setStep');

      $('.route-link').on('click', function (evt) {
        evt.preventDefault();
        var href;

        // Only trigger if not disabled. TODO: make this smarter
        if ($(this).parent('li').hasClass('disabled') === false) {
          href = $(this).get(0).getAttribute('href').split(Backbone.history.options.root)[1];
          _router.navigate(href, {trigger: true});
        }
      });
    },

    root: function() {
      this.navigate(_steps[0], {trigger: true});
    },

    setStep: function(id, step){
      var model;

      // Handles when there is an id (existing route) or not
      if (!step) {
        step = id;
        id = null;
      }

      // If this route exists already
      if (id) {
        model = _routeCollection.get(parseInt(id, 10));

        if (model) {
          $('.route-link').each(function(i, el) {
            $(el).attr('href', '/route/' + id + '/' +$(el).attr('data-route-step'));
          });

          this.enableDependentSteps();
          this.showStep(step, model);
        }
      } else {
        this.disableDependentSteps();

        // Updates the hrefs for each step link (id vs non-id)
        $('.route-link').each(function(i, el) {
          $(el).attr('href', '/route/' + $(el).attr('data-route-step'));
        });

        if (step === _steps[0]) {
          this.showStep(step, model);
        } else {
          this.root();
        }
      }
    },

    // Since $content is always the target, why not just pass it in and let
    // the view worry about rendering? Would make the map part easier.
    showStep: function (step, model) {

      var view = _views[step](model);

      // Update the active step classes
      $('#route-nav li').removeClass('active');
      $('.route-link[data-route-step="'+step+'"]').parent('li').addClass('active');

      view.render();
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
      _routeCollection.fetch({data: {agencyId: agencyId},
        success: function(collection, response, options) {
          Backbone.history.start({pushState: true, root: '/route/'});
        }
      });
    }
  };
})(GtfsEditor, jQuery, ich);