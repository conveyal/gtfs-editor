var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  G.Router = Backbone.Router.extend({
    routes: {
      ':type':               'hideContent',
      ':type/new':           'newModel',
      ':type/new/:lat,:lng': 'newModel',
      ':type/:id':           'viewModel'
    },

    initialize: function() {
      Backbone.history.start();
    },

    renderView: function(view) {
      $content.show().html(view.render().el);
    },

    destroyNewModels: function(collection) {
      collection.each(function(model) {
        if (model.isNew()) {
          model.destroy();
        }
      });
    },

    hideContent: function() {
      $content.hide();
    },

    newModel: function(type, lat, lng) {
      this.destroyNewModels(_stopsCollection);

      var model = new _stopsCollection.model({
            location: {lat: lat, lng: lng}
          });
          view = new G.FormView({
            collection: _stopsCollection,
            model: model
          });
      this.renderView(view);
      _stopsCollection.add(model);
    },

    viewModel: function(type, modelId) {
      this.destroyNewModels(_stopsCollection);

      var model = _stopsCollection.get(modelId),
          view = new G.FormView({
            collection: _stopsCollection,
            model: model
          });
      this.renderView(view);
      model.trigger('focus');
    }
  });

  var $content = $('.content'),
      _stopsCollection = new G.Stops(),
      _mapView,
      agencyId = 21;

  ich.grabTemplates();


  _stopsCollection.fetch({success: function(){

    _mapView = new G.MapView({
      el: '#map',
      collection: _stopsCollection,
      agencyId: agencyId,
      map: {
        options: {
          center: [39.952467541125955, -75.16360759735107],
          zoom: 12
        }
      }
    }).render();

    G.router = new G.Router();

  }});

})(GtfsEditor, jQuery, ich);