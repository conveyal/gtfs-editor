// init route type management page


$(document).ready(function() {

	// Bind help popovers
	$('input, select, textarea').popover({
		placement: 'right',
		trigger: 'focus', 
		container: 'body'
	});

	$('#new-route-type-form').on('submit', function (evt) {

		evt.preventDefault();

		var data = GtfsEditor.Utils.serializeForm($(evt.target));

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


	});

});