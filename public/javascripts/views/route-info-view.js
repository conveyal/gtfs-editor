var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  G.RouteInfoView = Backbone.View.extend({
    events: {
      'submit .route-info-form': 'save',
    },

    initialize: function () {
      this.onSave = this.options.onSave || function() {};

      this.routeTypes = new G.RouteTypes();

      this.collection.on('reset', this.render, this);
      this.routeTypes.on('change', this.updateRouteTypes, this);
      this.routeTypes.fetch();

      // Model is undefined if this is a new route. It is set when it is created.
      if (this.model) {
        this.setModel(this.model);
      }
    },

    setModel: function(model) {
      this.model = model;
      this.model.on('change', this.render, this);
      this.model.on('destroy', this.onModelDestroy, this);
    },

    onModelDestroy: function(){
      this.model = null;
      this.render();
    },

    render: function () {
      var data = this.model ? this.model.toJSON() : {};

      data.agency = data.agency ? data.agency.id : this.options.agencyId;

      // Get the markup from icanhaz
      var $tpl = ich['route-info-tpl'](data);

      var routeTypeData = {routeTypes: this.routeTypes.models};
      
      // Easily select the option
      $tpl
        .find('#routeType option[value="'+data.routeType+'"]')
        .attr('selected', true);

      // Render to the dom
      this.$el.html($tpl);

      // Bind help popovers
      this.$('input, select, textarea').popover({
        placement: 'right',
        trigger: 'focus'
      });

      return this;
    },

    /*
       // old code from backbone-driven route-type manager
       
     updateRouteTypes: function(evt) {
        var data = this.model ? this.model.toJSON() : {};

       var routeTypeData = {routeTypes: this.routeTypes.models};

      // create the routeType selector list
      var $routeTypeTpl = ich['route-info-tpl-route-type-options-tpl'](routeTypeData);

      // insert routeType selector list into form
      this.$el.find('#routeType').html($routeTypeTpl);
      
      // Easily select the option
      this.$el.find('#routeType option[value="'+ data.routeType+'"]')
        .attr('selected', true);

      // insert route type tabel into modal dialog
      var $routeTypeTable = ich['route-type-modal-body-tpl'](routeTypeData);
      this.$el.find('#route-type-modal-body').html($routeTypeTable); 

    }, 

    saveRouteType: function(evt) {
      evt.preventDefault();

      var data = G.Utils.serializeForm($(evt.target));

      this.routeTypes.create(data, {
          wait: true,
          success: _.bind(function() {
            this.updateRouteTypes();
            //G.Utils.success('Route successfully created');

            G.Utils.message('sucess', 'new route type created', $('#route-type-modal-body'));
          }, this),
          error: _.bind(function() {
            G.Utils.message('error', 'Unable to create new route type', $('#route-type-modal-body'));
          }, this)
        });
    } */

    save: function(evt){
      evt.preventDefault();

      var data = G.Utils.serializeForm($(evt.target));

      // Currently existing route, save it
      if (this.model) {
        this.model.save(data, {
          wait: true,
          success: _.bind(function() {
            this.onSave(this.model);
            G.Utils.success('Route successfully saved');
          }, this),
          error: function() {
            G.Utils.error('Route save failed');
          }
        });
      } else {
        // New route, create it
        var model = this.collection.create(data, {
          wait: true,
          success: _.bind(function() {
            this.onSave(this.model);
            G.Utils.success('Route successfully created');
          }, this),
          error: _.bind(function() {
            G.Utils.error('Route save failed');
          }, this)
        });

        // Set and bind events
        this.setModel(model);
      }
    }

  });
})(GtfsEditor, jQuery, ich);
