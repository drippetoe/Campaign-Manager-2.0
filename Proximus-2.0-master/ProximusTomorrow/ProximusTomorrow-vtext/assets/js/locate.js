var map;
function initialise() {
    var latlng = new google.maps.LatLng(-25.363882,131.044922);
    var myOptions = {
        zoom: 4,
        center: latlng,
        mapTypeId: google.maps.MapTypeId.TERRAIN,
        disableDefaultUI: true
    }
    map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
    prepareGeolocation();
    doGeolocation();
}

function doGeolocation() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(positionSuccess, positionError);
    } else {
        positionError(-1);
    }
}

function positionError(err) {
    var msg;
    switch(err.code) {
        case err.UNKNOWN_ERROR:
            msg = "Unable to find your location";
            break;
        case err.PERMISSION_DENINED:
            msg = "Permission denied in finding your location";
            break;
        case err.POSITION_UNAVAILABLE:
            msg = "Your location is currently unknown";
            break;
        case err.BREAK:
            msg = "Attempt to find location took too long";
            break;
        default:
            msg = "Location detection not supported in browser";
    }
    document.getElementById('info').innerHTML = msg;
}

function positionSuccess(position) {
    // Centre the map on the new location
    var coords = position.coords || position.coordinate || position;
    var latLng = new google.maps.LatLng(coords.latitude, coords.longitude);
    map.setCenter(latLng);
    map.setZoom(12);
    var marker = new google.maps.Marker({
        map: map,
        position: latLng,
        title: 'Why, there you are!'
    });
    document.getElementById('info').innerHTML = 'Looking for <b>' +
    coords.latitude + ', ' + coords.longitude + '</b>...';

    // And reverse geocode.
    (new google.maps.Geocoder()).geocode({
        latLng: latLng
    }, function(resp) {
        var place = "You're around here somewhere!";
        if (resp[0]) {
            var bits = [];
            for (var i = 0, I = resp[0].address_components.length; i < I; ++i) {
                var component = resp[0].address_components[i];
                if (contains(component.types, 'political')) {
                    bits.push('<b>' + component.long_name + '</b>');
                }
            }
            if (bits.length) {
                place = bits.join(' > ');
            }
            marker.setTitle(resp[0].formatted_address);
        }
        document.getElementById('info').innerHTML = place;
    });
}

function contains(array, item) {
    for (var i = 0, I = array.length; i < I; ++i) {
        if (array[i] == item) return true;
    }
    return false;
}

