var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  G.Router = Backbone.Router.extend({
    routes: {
      ':type':               'listCollection',
      ':type/new':           'newModel',
      ':type/new/:lat,:lng': 'newModel',
      ':type/:id':           'viewModel'
    },

    initialize: function() {
      Backbone.history.start();
    },

    renderView: function(view) {
      $content.html(view.render().el);
    },

    destroyNewModels: function(collection) {
      collection.each(function(model) {
        if (model.isNew()) {
          model.destroy();
        }
      });
    },

    listCollection: function(type) {
      this.destroyNewModels(_stopsCollection);

      var view = new G.TableView({
            collection: _stopsCollection
          });
      this.renderView(view);
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
      _mapView;

  ich.grabTemplates();


  _stopsCollection.fetch({success: function(){

    _mapView = new G.MapView({
      el: '#map',
      collection: _stopsCollection,
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