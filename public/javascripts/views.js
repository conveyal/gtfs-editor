var GtfsEditor = GtfsEditor || {};

(function(G, $, ich) {
  var collectionToDatatables = function(collection) {
    var fields = _.keys(new collection.model().defaults),
        aaData = [],
        aoColumns = _.map(fields, function(key) {
          return { sTitle: key };
        });

    _.each(collection.toJSON(), function(obj){
      var rowData = [];
      _.each(fields, function(field) {
        var val = obj[field];

        if (_.isObject(val)) {
          val = val.id || JSON.stringify(val);
        }

        if (field === 'id') {
          val = '<a href="#'+collection.type+'/'+val+'">'+val+'</a>';
        }

        rowData.push(val);
      });
      aaData.push(rowData);
    });

    return {
      aoColumns: aoColumns,
      aaData: aaData
    };
  };

  G.TableView = Backbone.View.extend({
    initialize: function() {
      this.collection.on('reset', this.render, this);
    },
    render: function() {
      this.$el.html(ich['collection-table-tpl'](
        {new_link: '#'+this.collection.type+'/new', title: this.collection.type}
      ));

      if (this.collection.first()) {
        var tableOptions = collectionToDatatables(this.collection, this.collection.type);
        tableOptions.bPaginate = false;
        tableOptions.bInfo = false;

        this.$('#collection-table').show().dataTable(tableOptions);
      }

      return this;
    }
  });

  G.FormView = Backbone.View.extend({
    events: {
      'click #save-btn': 'save',
      'click #delete-btn': 'delete'
    },

    initialize: function() {
      this.collection.on('reset', this.render, this);

      if (this.model) {
        this.setModel(this.model);
      }
    },

    setModel: function(model) {
      this.model = model;
      this.model.on('change', this.render, this);
    },

    render: function() {
      var data = [],
          tempModel;

      tempModel = this.model || new this.collection.model();
      _.each(tempModel.toJSON(), function(val, key) {
        if (key !== 'id') {
          if (_.isObject(val)) {
            val = val.id || JSON.stringify(val);
          }

          data.push({key: key, val: val});
        }
      });

      var tplData = {
        new_link: '#'+this.collection.type+'/new',
        title: this.collection.type,
        can_delete: !!this.model,
        data: data
      };

      this.$el.html(ich['model-form-tpl'](tplData));
      return this;
    },

    save: function(evt){
      evt.preventDefault();

      var data = {};
      this.$('input').each(function(i, el) {
        var $input = $(el);
        data[$input.attr('id')] = $input.val() || null;
      });

      if (this.model) {
        // This seems redundant, but we need to call set first so that the
        // validator work as expected. Otherwise any attribute overrides in the
        // validator will not be set.
        this.model.set(data, { silent: true });
        this.model.save(null, {
          wait: true,
          success: _.bind(function() {
            alert('Saved!');
            G.router.navigate(this.collection.type + '/' + this.model.id);
          }, this),
          error: function() { alert('Oh noes! That didn\'t work.'); }
        });
      } else {
        this.model = this.collection.create(data, {
          wait: true,
          success: _.bind(function() {
            alert('Created!');
            G.router.navigate(this.collection.type + '/' + this.model.id, {trigger: true});
          }, this),
          error: _.bind(function() {
            this.model = null;
            alert('Oh noes! That didn\'t work.');
          }, this)
        });

        this.setModel(this.model);
      }
    },

    'delete': function(evt){
      evt.preventDefault();
      var ct = this.collection.type;

      if (window.confirm('Really delete?')) {
        this.model.destroy({
          success: function() { G.router.navigate(ct, {trigger: true}); },
          error: function() { alert('Oh noes! That didn\'t work.'); }
        });
      }
    }
  });

  G.MapView = Backbone.View.extend({
    initialize: function() {
      // Base layer config is optional, default to Mapbox Streets
      var baseLayerConfig = _.extend({
            url: 'http://{s}.tiles.mapbox.com/v3/mapbox.mapbox-streets/{z}/{x}/{y}.png',
            attribution: '&copy; OpenStreetMap contributors, CC-BY-SA. <a href="http://mapbox.com/about/maps" target="_blank">Terms &amp; Feedback</a>'
          }, this.options.map.baseLayer),
          baseLayer = L.tileLayer(baseLayerConfig.url, baseLayerConfig);

      // Init the map
      this.map = L.map(this.el, this.options.map.options);
      this.layerGroup = L.layerGroup();
      this.map.addLayer(baseLayer);

      // Remove default prefix
      this.map.attributionControl.setPrefix('');

      this.map.addLayer(this.layerGroup);

      var drawControl = new L.Control.Draw({
        polygon: false,
        polyline: false,
        rectangle: false,
        circle: false
      });
      this.map.addControl(drawControl);
      this.map.on('draw:marker-created', function (evt) {
        var latLng = evt.marker.getLatLng();
        G.router.navigate('Stops/new/' + latLng.lat.toPrecision(6) + ',' +
          latLng.lng.toPrecision(6), {trigger: true});
      }, this);

      // Init the layer view cache
      this.layerViews = {};

      if (this.collection) {
        this.setCollection(this.collection);
      }
    },
    setCollection: function(collection) {
      this.collection = collection;

      // Bind data events
      this.collection.on('reset', this.render, this);
      this.collection.on('add', this.addLayerView, this);
      this.collection.on('remove', this.removeLayerView, this);
    },
    render: function() {
      // Clear any existing stuff on the map, and free any views in
      // the list of layer views.
      _.each(this.layerViews, function(lv) {
        lv.remove();
      });
      this.layerGroup.clearLayers();
      this.layerViews = {};

      this.collection.each(function(model, i) {
        this.addLayerView(model);
      }, this);
    },
    addLayerView: function(model) {
      this.layerViews[model.cid] = new G.StopLayerView({
        model: model,
        map: this.map,
        layerGroup: this.layerGroup
      });
    },
    removeLayerView: function(model) {
      this.layerViews[model.cid].remove();
      delete this.layerViews[model.cid];
    }
  });


  G.StopLayerView = Backbone.View.extend({
     // A view responsible for the representation of a place on the map.
    initialize: function(){
      this.map = this.options.map;

      // Bind model events
      this.model.on('change', this.updateLayer, this);
      this.model.on('focus', this.focus, this);

      this.initLayer();
    },
    initLayer: function() {
      var location = this.model.get('location');
      this.latLng = L.latLng(location.lat, location.lng);

      this.layer = L.marker(this.latLng, {
        draggable: true
      });

      this.layer.on('dragend', this.onMarkerDragEnd, this);
      this.layer.on('click', this.onMarkerClick, this);

      this.render();
    },
    updateLayer: function() {
      // Update the marker layer if the model changes and the layer exists
      this.removeLayer();
      this.initLayer();
    },
    removeLayer: function() {
      if (this.layer) {
        this.options.layerGroup.removeLayer(this.layer);
      }
    },
    render: function() {
      this.show();
    },
    onMarkerClick: function() {
      if (!this.model.isNew()) {
        G.router.navigate('/Stops/' + this.model.id, {trigger: true});
      }
    },
    onMarkerDragEnd: function(evt) {
      var latLng = evt.target.getLatLng();
      this.model.set({'location': {
        'lng': latLng.lng.toPrecision(6),
        'lat': latLng.lat.toPrecision(6)
      }});

      if (!this.model.isNew()) {
        G.router.navigate('/Stops/' + this.model.id, {trigger: true});
      }
    },
    focus: function() {
      var mapBounds = this.map.getBounds(),
          latLng = L.latLng([this.model.get('location').lat, this.model.get('location').lng]);

      if (latLng && !mapBounds.contains(latLng)) {
        this.map.panTo(latLng);
      }
    },
    remove: function() {
      this.removeLayer();
    },
    show: function() {
      if (this.layer) {
        this.options.layerGroup.addLayer(this.layer);
      }
    },
    hide: function() {
      this.removeLayer();
    }
  });

})(GtfsEditor, jQuery, ich);