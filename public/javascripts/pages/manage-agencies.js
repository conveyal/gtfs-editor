// init route type management page


$(document).ready(function() {

	// Bind help popovers
	$('input, select, textarea').popover({
		placement: 'right',
		trigger: 'focus', 
		container: 'body'
	});

	$('#new-agency-form').on('submit', function (evt) {

		evt.preventDefault();

		var data = GtfsEditor.Utils.serializeForm($(evt.target));

		var agency = new GtfsEditor.Agency();

		agency.save(data, {error: function(){ 

			$('#new-agency-modal').modal('hide');
			G.Utils.error('Agency save failed');

		},
		
		success: function(){ 

			$('#new-agency-modal').modal('hide');
		
			location.reload();

		}});


	});

});