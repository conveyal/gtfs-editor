var map = new L.Map('map'),
    mapboxUrl = 'http://{s}.tiles.mapbox.com/v3/atogle.map-vo4oycva/{z}/{x}/{y}.png',
    mabboxAttribution = 'Map data &copy; OpenStreetMap contributors, CC-BY-SA <a href="http://mapbox.com/about/maps" target="_blank">Terms &amp; Feedback</a>',
    mapboxLayer = new L.TileLayer(mapboxUrl, {maxZoom: 17, attribution: mabboxAttribution, subdomains: 'abcd'});

map.setView(new L.LatLng(39.952467541125955, -75.16360759735107), 14).addLayer(mapboxLayer);

var drawControl = new L.Control.Draw({
      polygon: {
        allowIntersection: false,
        shapeOptions: {
          color: '#bada55'
        }
      }
    });
    map.addControl(drawControl);

    var drawnItems = new L.LayerGroup();
    map.on('draw:poly-created', function (e) {
      drawnItems.addLayer(e.poly);
    });
    map.on('draw:rectangle-created', function (e) {
      drawnItems.addLayer(e.rect);
    });
    map.on('draw:circle-created', function (e) {
      drawnItems.addLayer(e.circ);
    });
    map.on('draw:marker-created', function (e) {
      drawnItems.addLayer(e.marker);
    });
    map.addLayer(drawnItems);