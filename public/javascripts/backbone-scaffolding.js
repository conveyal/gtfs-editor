var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  var S = G.Scaffolding = {},
      _collections = [],
      $navList = $('#nav-list'),
      $content = $('#content');

  var getCollection = function(type) {
    return _.find(_collections, function(c) {
      return c.type === type;
    });
  };

  S.init = function(c) {
    ich.grabTemplates();

    // array of objects with key/val properties
    _collections = c;

    // Build the collection list
    $navList.empty();
    _.each(_collections, function(collection) {
      $navList.append('<li><a href="#'+collection.type+'">'+collection.type+'</a></li>');
    });

    G.router = new GtfsEditor.Scaffolding.Router();
  };

  S.Router = Backbone.Router.extend({
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
      var collection = getCollection(type);

      collection.fetch({success: _.bind(function(){
        var view = new G.TableView({
              collection: getCollection(type)
            });
        this.renderView(view);
      }, this)});
    },

    viewModel: function(type, modelId) {
      var collection = getCollection(type);

      collection.fetch({success: _.bind(function(){
        var model = collection.get(modelId),
            view = new G.FormView({
              collection: collection,
              model: model
            });
        this.renderView(view);
      }, this)});
    }
  });
})(GtfsEditor, jQuery, ich);

// Add collections here
GtfsEditor.Scaffolding.init([
  new GtfsEditor.Agencies(),
  new GtfsEditor.Routes(),
  new GtfsEditor.Stops(),
  new GtfsEditor.TripPatterns(),
  new GtfsEditor.Calendars()
]);