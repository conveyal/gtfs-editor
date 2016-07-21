(function (window, document, undefined) {

L.EncodedPolyline = L.Polyline.extend ({
	
	initialize: function (polyline, options) {
		
		this._latlngs = this._decode(polyline);

		L.Polyline.prototype.initialize.call(this, this._latlngs, options);
	
	},
	
	setPolyline: function (polyline) {
	
		this.setLatLngs(this._decode(polyline));
	},
	
	_decode: function (polyline) {
		
		  var currentPosition = 0;

		  var currentLat = 0;
		  var currentLng = 0;
	
		  var dataLength  = polyline.length;
		  
		  var polylineLatLngs = new Array();
		  
		  while (currentPosition < dataLength) {
			  
			  var shift = 0;
			  var result = 0;
			  
			  var byte;
			  
			  do {
				  byte = polyline.charCodeAt(currentPosition++) - 63;
				  result |= (byte & 0x1f) << shift;
				  shift += 5;
			  } while (byte >= 0x20);
			  
			  var deltaLat = ((result & 1) ? ~(result >> 1) : (result >> 1));
			  currentLat += deltaLat;
	
			  shift = 0;
			  result = 0;
			
			  do {
				  byte = polyline.charCodeAt(currentPosition++) - 63;
				  result |= (byte & 0x1f) << shift;
				  shift += 5;
			  } while (byte >= 0x20);
			  
			  var deltLng = ((result & 1) ? ~(result >> 1) : (result >> 1));
			  
			  currentLng += deltLng;
	
			  polylineLatLngs.push(new L.LatLng((currentLat * 0.00001).toFixed(5), (currentLng * 0.00001).toFixed(5)));
			  
			  
		  }	
		  
		  return polylineLatLngs;
	}
});

}(this, document));

function createEncodedPolyline(polyline) {
			
	var coords = polyline.getLatLngs();

	if(coords.length == 0)
		return null;

	var i = 0;
 
	var plat = 0;
	var plng = 0;
 
	var encoded_points = "";
 
	for(i = 0; i < coords.length; ++i) {
	    var lat = coords[i].lat;				
		var lng = coords[i].lng;		
 
		encoded_points += this._encodePoint(plat, plng, lat, lng);
 
	    plat = lat;
	    plng = lng;
	}
 
	// close polyline
	// we don't want the polyline closed, and this doesn't close it anyhow because we want coords[0].lat and coords[0].lng
	// encoded_points += this._encodePoint(plat, plng, coords[0][0], coords[0][1]);
 
	return encoded_points;
}

function _encodePoint(plat, plng, lat, lng) {
	var late5 = Math.round(lat * 1e5);
    var plate5 = Math.round(plat * 1e5)    
 
	var lnge5 = Math.round(lng * 1e5);
    var plnge5 = Math.round(plng * 1e5)
 
	dlng = lnge5 - plnge5;
	dlat = late5 - plate5;
 
    return this._encodeSignedNumber(dlat) + this._encodeSignedNumber(dlng);
}
	 
function _encodeSignedNumber(num) {
	var sgn_num = num << 1;

	if (num < 0) {
		sgn_num = ~(sgn_num);
	}

	return(this._encodeNumber(sgn_num));
}
	 
function _encodeNumber(num) {
	var encodeString = "";

	while (num >= 0x20) {
		encodeString += (String.fromCharCode((0x20 | (num & 0x1f)) + 63));
		num >>= 5;
	}

	encodeString += (String.fromCharCode(num + 63));
	return encodeString;
}

