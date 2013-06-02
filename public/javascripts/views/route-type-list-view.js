(function(G, $, ich) {
  
  G.RouteTypeListView = Backbone.View.extend({

  		events: {
        'click .route-type-edit': 'editRouteType',
        'click .route-type-delete': 'deleteRouteType',
	    },

  		initialize: function (opts) {
  			this.collection = new G.RouteTypes();

  			var self = this;

  			this.collection.on('remove', this.render, this)

  			this.collection.fetch().complete(function() {

  			  self.render();
          
  			});

  			_.bindAll(this, 'editRouteType', 'createRouteType', 'deleteRouteType');
  		},

  		render: function() {

        var view = this;

  			$('tr.routeType').remove();

  			var data = {
  				routes: _.pluck(this.collection.models, 'attributes')
  			}

        $('#route-type-dialog-button').on('click', this.createRouteType);

  			this.$el.html(ich['route-type-table-tpl'](data));

        $('#route-type-form').on('submit', function (evt) {

          evt.preventDefault();

          var data = GtfsEditor.Utils.serializeForm($(evt.target));

          if(data.id == "" ) {


            view.collection.create(_.omit(data, 'id'), {error: function(){ 

              $('#route-type-modal').modal('hide');
              G.Utils.error('Route type save failed');

            },
            success: function(){ 

              $('#route-type-modal').modal('hide');
              //G.Utils.error('Route type save failed');
              location.reload();

            }});


          }
          else {

            var routeType = new GtfsEditor.RouteType();
            routeType.save(data, {error: function(){ 

              $('#route-type-modal').modal('hide');
              G.Utils.error('Route type save failed');

            },
            success: function(){ 

              $('#route-type-modal').modal('hide');
              //G.Utils.error('Route type save failed');
              location.reload();

            }});

          }

          


  });

  		}, 

  		editRouteType: function(evt) {

  			var id = $(evt.currentTarget).data("id");

        var routeType = this.collection.get(id);
  			

        var $tpl = ich['route-type-dialog-tpl'](routeType.attributes);

        $tpl.find('#gtfsRouteType option[value="' + routeType.attributes.gtfsRouteType + '"]')
            .attr('selected', true);

        $tpl.find('#hvtRouteType option[value="' + routeType.attributes.hvtRouteType + '"]')
            .attr('selected', true);

        $('#route-type-modal-body').html($tpl);
        $('#route-type-modal').modal();

  		},

      createRouteType: function(evt) {
        
        $('#route-type-modal-body').html(ich['route-type-dialog-tpl']());

      },

  		deleteRouteType: function(evt) {
  			var id = $(evt.currentTarget).data("id");
  			
  			var view = this;
  			if (G.Utils.confirm('Delete route?')) {
	  			view.collection.get(id).destroy();
	  		}
  		},


  });


})(GtfsEditor, jQuery, ich);