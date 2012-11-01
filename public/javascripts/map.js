var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  var $content = $('.content');
  ich.grabTemplates();


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
    }
  });

  var _stopsCollection = new G.Stops(),
      _mapView;

  _stopsCollection.fetch({success: function(){
    G.router = new G.Router();

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
  }});

})(GtfsEditor, jQuery, ich);