(function(G, $, ich) {

  G.RouteListView = Backbone.View.extend({

  		events: {
	      'click .route-edit': 'editRoute',
	      'click .route-delete': 'deleteRoute',
        'click #show-merge-routes': 'showMergeRouteDialog',
        'click #merge-routes': 'mergeRoutes'
	    },

  		initialize: function (opts) {
  			this.collection = new G.Routes();

  			var self = this;

  			this.collection.on('remove', this.render, this)

  			this.collection.fetch({data: {agencyId: G.session.agencyId}}).complete(function() {

  			  self.render();

  			});

  			_.bindAll(this, 'editRoute', 'deleteRoute', 'showMergeRouteDialog', 'mergeRoutes');
  		},

  		render: function() {

  			this.$el.empty();

  			var data = {
  				routes: _.pluck(this.collection.models, 'attributes')
  			}

  			this.$el.append(ich['route-table-tpl'](data));
  		},

      showMergeRouteDialog: function (e) {
        $('#modal-container').empty().append(ich['merge-routes-tpl']({routes: this.collection.toJSON()}))
          .find('.modal').modal('show');
      },

      mergeRoutes: function (e) {
        var instance = this;

        var from = $('#merge-from').val();
        var into = $('#merge-into').val();

        $.ajax({
          url: G.config.baseUrl + 'api/route/merge',
          data: {
            from: from,
            into: into
          },
          method: 'POST'
        }).done(function () {
          G.Utils.success("Route merge suceeded");
          instance.collection.fetch()
            .done(function () {
              instance.render();
            });
        })
        .error(function () {
          G.Utils.error("Route merge failed");
        });
      },

  		editRoute: function(evt) {
  			var id = $(evt.currentTarget).data("id");
  			//alert('edit ' + id);

        // need to use route action !!!
  			window.location = G.config.baseUrl + 'route/' + id + '/info';

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
