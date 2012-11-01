var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  G.Router = Backbone.Router.extend({
    routes: {
      ':type':     'listCollection',
      ':type/new': 'viewModel',
      ':type/:id': 'viewModel'
    },

    initialize: function() {
      Backbone.history.start();
    },

    renderView: function(view) {
      $content.html(view.render().el);
    },

    listCollection: function(type) {
      var view = new G.TableView({
            collection: _stopsCollection
          });
      this.renderView(view);
    },

    viewModel: function(type, modelId) {
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