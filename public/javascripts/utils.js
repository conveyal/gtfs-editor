var GtfsEditor = GtfsEditor || {};

(function(G, $) {

  G.Utils = {
    serializeForm : function($form) {
      var data = $form.serializeObject(),
          $checkedCheckboxes = $form.find('[type="checkbox"]:checked');
          $uncheckedCheckboxes = $form.find('[type="checkbox"]:not(:checked)');

      // Handle check box values
      $checkedCheckboxes.each(function(i, el) {
        if (el.name) {
          data[el.name] = true;
        }
      });

      $uncheckedCheckboxes.each(function(i, el) {
        if (el.name) {
          data[el.name] = false;
        }
      });
      
      return data;
    },

    // Messages
    message: function(type, text, delay) {
      var $message = $(
        '<div class="alert alert-' + type + ' fade in message">' +
          '<a class="close" data-dismiss="alert" href="#">&times;</a>' + 
          text + 
        '</div>');
      
      if (delay === undefined) {
        delay = 2000;
      }
      
      $message.prependTo('body')
        .alert();
      
      _.delay(function() { $message.alert('close'); }, delay);
    },
    
    confirm: function(text) {
      return confirm(text);
    },
    
    success: function(text, delay) { return G.Utils.message('success', text, delay); },
    error: function(text, delay) { return G.Utils.message('error', text, delay); }
  };

})(GtfsEditor, jQuery);


$(document).ready(function() {
   // page init scripts
   
   // add language select handler
   $(".lang-select").click(function(e){
		$.get('/application/setLang', {lang: $(e.target).data('lang')}, function(){
			location.reload();
		});
		e.preventDefault();
	});
 });
