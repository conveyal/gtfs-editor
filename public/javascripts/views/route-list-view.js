(function(G, $, ich) {
  
  G.RouteListView = Backbone.View.extend({

  		events: {
	      'click .route-edit': 'editRoute',
	      'click .route-delete': 'deleteRoute'
	    },

  		initialize: function (opts) {
  			this.collection = new G.Routes();

  			var self = this;

  			this.collection.on('remove', this.render, this)

  			this.collection.fetch({data: {agencyId: G.session.agencyId}}).complete(function() {

  			  self.render();

  			});

  			_.bindAll(this, 'editRoute', 'deleteRoute');
  		},

  		render: function() {

  			$('tr.route').remove();

  			var data = {
  				routes: _.pluck(this.collection.models, 'attributes')
  			}

  			this.$el.append(ich['route-table-tpl'](data)); 
  		}, 

  		editRoute: function(evt) {
  			var id = $(evt.currentTarget).data("id");
  			//alert('edit ' + id);

        // need to use route action !!!
  			window.location = '/route/' + id + '/info'; //editRouteAction({id:id});

  		},

  		deleteRoute: function(evt) {
  			var id = $(evt.currentTarget).data("id");
  			
  			var view = this;
  			if (G.Utils.confirm('Delete route?')) {
	  			view.collection.get(id).destroy();
	  		}
  		},


  });


})(GtfsEditor, jQuery, ich);