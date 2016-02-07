(function(G, $, ich) {

  G.AvailableAgencyListView = Backbone.View.extend({

      events: {
        'click .load-agency-button': 'loadAgency'
      },

      initialize: function (opts) {
        var self = this;
        var token = localStorage.getItem('userToken');
        $.ajax({
            url : "http://localhost:9000/api/feedsources",
            data : {
              feedcollection: G.config.projectId
            },
            headers: {
              'Authorization' : 'Bearer ' + token
            },
            success: function(data) {
              self.agencies = [];
              data.forEach(function(feed) {
                if(!feed.latestValidation || !feed.latestValidation.agencies) return;
                feed.latestValidation.agencies.forEach(function(agency) {
                  self.agencies.push({
                    sourceId: feed.id,
                    sourceName: feed.name,
                    agencyId: agency
                  });
                });
              });
              self.render();
            },
            error: (err) => {
              console.log('error getting feed sources', err)
            }
          })
      },

      render: function() {
        this.$el.html(ich['available-agency-table-tpl']({
          agencies: this.agencies
        }));
      },

      loadAgency : function (evt) {
        var sourceId = $(evt.currentTarget).data("source-id");
        var agencyId = $(evt.currentTarget).data("agency-id");
        var agency = this.getAgency(sourceId, agencyId);
        if(agency != null) {
          this.options.agencyListView.createAgency(null, {
            gtfsAgencyId: agency.agencyId,
            name : agency.sourceName,
            sourceId: agency.sourceId
          });
        }
      },

      getAgency : function (sourceId, agencyId) {
        for(var i=0; i < this.agencies.length; i++) {
          var agency = this.agencies[i];
          if(agency.sourceId == sourceId && agency.agencyId === agencyId) return agency;
        }
        return null;
      }
  });

  G.AgencyListView = Backbone.View.extend({

      events: {
        'click .agency-edit': 'editAgency',
        'click .agency-delete': 'deleteAgency',
        'click .agency-duplicate': 'duplicateAgency'
      },

      initialize: function (opts) {
        this.collection = new G.Agencies();

        var self = this;

        this.collection.on('remove', this.render, this);

        this.collection.fetch().complete(function() {
          self.render();
        });

        this.clickMarkerIcon = L.icon({
          iconUrl: G.config.baseUrl + 'public/images/markers/marker-0d85e9.png',
          iconSize: [25, 41],
          iconAnchor: [12, 41],
          popupAnchor: [1, -34],
          shadowUrl: G.config.baseUrl + 'public/images/markers/marker-shadow.png',
          shadowSize: [41, 41],
          labelAnchor: [10, -16]
        });

        _.bindAll(this, 'editAgency', 'createAgency', 'deleteAgency');
      },

      render: function() {

        var view = this;

        $('tr.agency').remove();

        var data = {
          routes: _.pluck(this.collection.models, 'attributes')
        }

        $('#agency-dialog-button').on('click', this.createAgency);

        this.$el.html(ich['agency-table-tpl'](data));

        $('#agency-form').on('submit', function (evt) {

          evt.preventDefault();

          var data = GtfsEditor.Utils.serializeForm($(evt.target));

          if(data.id == "" ) {

            view.collection.create(_.omit(data, 'id'), {error: function(){

              $('#agency-modal').modal('hide');
              G.Utils.error('Route type save failed');

            },
            success: function(){

              $('#agency-modal').modal('hide');
              //G.Utils.error('Route type save failed');
              location.reload();

            }});
          }
          else {

            var agency = new GtfsEditor.Agency();
            agency.save(data, {error: function(){

              $('#agency-modal').modal('hide');
              G.Utils.error('Route type save failed');

            },
            success: function(){

              $('#agency-modal').modal('hide');
              //G.Utils.error('Route type save failed');
              location.reload();

            }});

          }
        });
      },

      editAgency: function(evt) {

        var id = $(evt.currentTarget).data("id");

        var agency = this.collection.get(id);


        var $tpl = ich['agency-dialog-tpl'](agency.attributes);

        if(agency.attributes.defaultRouteType != null) {
          $tpl.find('#defaultRouteType option[value="' + agency.attributes.defaultRouteType.id + '"]')
              .attr('selected', true);
        }

        $tpl.find('#timezone option[value="' + agency.attributes.timezone + '"]')
            .attr('selected', true);

        $tpl.find('#lang option[value="' + agency.attributes.lang + '"]')
            .attr('selected', true);

        $('#agency-modal-body').html($tpl);
        $('#agency-modal').modal();

        this.buildMap();

      },

      createAgency: function(evt, values) {

        $('#agency-modal-body').html(ich['agency-dialog-tpl'](values));

        this.buildMap();

      },

      buildMap: function() {

          // Base layer config is optional, default to Mapbox Streets
        var url = 'http://{s}.tiles.mapbox.com/v3/' + G.config.mapboxKey + '/{z}/{x}/{y}.png',
            baseLayer = L.tileLayer(url, {
              attribution: '&copy; OpenStreetMap contributors, CC-BY-SA. <a href="http://mapbox.com/about/maps" target="_blank">Terms &amp; Feedback</a>'
            });



        // Init the map
        var mapCenter = undefined;
        if(G.session.mapCenter != undefined) {
            mapCenter = G.session.mapCenter;
            mapZoom = 15;
        }
        else {
            mapCenter = [0.0,0.0];
            mapZoom = 1;
        }



        this.map = L.map($('#map').get(0), {
          center: mapCenter,
          zoom: mapZoom,
          maxZoom: 17
        });
        this.map.addLayer(baseLayer);


        var view = this;

        if($('#defaultLat').val() != '' && $('#defaultLon').val() != '') {

           if(view.clickMarker != undefined)
            view.map.removeLayer(view.clickMarker);

           var lat = parseFloat($('#defaultLat').val());
           var lon = parseFloat($('#defaultLon').val());

           view.clickMarker = L.marker([lat, lon], {icon: view.clickMarkerIcon}).addTo(view.map);
           view.map.panTo([lat, lon]);
        }


        this.map.on('click', function(evt) {

           if(view.clickMarker != undefined)
            view.map.removeLayer(view.clickMarker);


          var latlng = evt.latlng.wrap();
           view.clickMarker = L.marker(latlng, {icon: view.clickMarkerIcon}).addTo(view.map);
           $('#defaultLat').val(latlng.lat);
           $('#defaultLon').val(latlng.lng);

        });

        $('.agency-map-toggle').on('click', function (evt) {

          $('#map').toggle();
          view.map.invalidateSize();

        });

      },

      deleteAgency: function(evt) {
        var id = $(evt.currentTarget).data("id");

        var view = this;
        if (G.Utils.confirm('Delete route?')) {
          view.collection.get(id).destroy();
        }
      },

      duplicateAgency: function(evt) {
        var instance = this;
        var id = $(evt.currentTarget).data("id");
        $.ajax({
          url: G.config.baseUrl + 'api/agency/' + id + '/duplicate',
          method: 'POST'
        })
        .done(function () {
          G.Utils.success("Agency duplicated");
          instance.collection.fetch().done(function () {
            instance.render();
          });
        })
        .error(function () {
          G.Utils.error("Agency duplicate failed");
        });
      },
  });


})(GtfsEditor, jQuery, ich);
