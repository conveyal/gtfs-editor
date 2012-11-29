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
    }
  };

})(GtfsEditor, jQuery);
