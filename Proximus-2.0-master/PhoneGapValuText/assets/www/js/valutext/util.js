/**
 * Utility FUNCTIONS
 */
// How often to run the getLocationOfDevice method
var TIMEOUT = 900000;
var timeOutVar;
var isTimeOutEnabled = true;

/**
 * LIST OF things we save on localStorage: use_my_location (boolean to check if
 * using device gps or a zipcode) zipcode (if not using geo location point then
 * using a gps from this zipcode) max_distance (current filter for offers)
 * categories (in JSON Format we must convert them to comma separated list for
 * REST call)
 * 
 * 
 * 
 */

function isValidZipCode(zipcode) {
	return /(^\d{5}$)|(^\d{5}-\d{4}$)/.test(zipcode);
}

/*
 * Storage functions
 */

/*
 * Make sure onDeviceReady() was called before you use this
 */
function saveUserPreferences() {
	/*
	 * preferences include:
	 * 
	 * use_my_location (true/false) zipcode (if use_my_location is false)
	 * offer_max_distance (miles) categories (list)
	 * 
	 */
	var data = window.localStorage;

	var maxDistance = $('[name=offer_max_distance]:checked').val();
	data.setItem("max_distance", maxDistance);
	window.maxDistance = maxDistance;

	var useMyLocation = $("input[name='use_my_location']").attr("checked") == 'checked';
	data.setItem("use_my_location", useMyLocation);

	if (!useMyLocation) {
		var zipcode = $("input[name='zipcode']").val();
		if (isValidZipCode(zipcode)) {
			data.setItem("zipcode", zipcode);
			stopTimeout();
		} else {
			data.removeItem("zipcode");
			$("input[name='zipcode']").val("");
			data.setItem("use_my_location", true);
			resetTimeout();
		}

	} else {
		resetTimeout();
	}

	var checkboxSave = new Object();
	var checkboxes = $("input[name=category]");
	$.each(checkboxes, function(key, checkbox) {

		var checkId = checkbox.id;
		var label = checkbox.value;
		var checked = checkbox.checked;

		checkboxSave[checkId] = checked;
		window.localStorage['categories'] = JSON.stringify(checkboxSave);

	});

	getOfferList(onOfferListSuccess, onOfferListFailure);

}

function loadUserPreferences() {

    /*
	 * preferences include:
	 * 
	 * use_my_location (true/false) zipcode (if use_my_location is false)
	 * offer_max_distance (miles) categories (list)
	 * 
	 */
    var data = window.localStorage;
    var use_my_location = data.getItem("use_my_location");
    var locationCheckbox = $("input[name='use_my_location']");
    if ((use_my_location == true) || (use_my_location == "true")) {
        locationCheckbox.prop('checked', true).checkboxradio('refresh');
        $('#zipcodeField').hide();
    } else {
        locationCheckbox.removeAttr('checked').checkboxradio('refresh');
        $('#zipcodeField').show();

    }
    
    var distances = $("input[name='offer_max_distance']");
    $.each(distances, function (key, radiobox) {
	var id = radiobox.id
	var value = radiobox.value;
	var checked = radiobox.checked;
	var $radioBox = $('#' + id);
	if(localStorage.getItem("max_distance") == value) {
	    $radioBox.prop('checked', true).checkboxradio('refresh');
	} else {
	    $radioBox.removeAttr('checked').checkboxradio('refresh');
	}
	
    });

    var cats = localStorage.getItem("categories");
    if(cats) {
	var savedStates = JSON.parse(cats);
	    var checkboxes = $("input[name=category]");
	    $.each(checkboxes, function (key, checkbox) {
		var checkId = checkbox.id;
		var label = checkbox.value;
		var checked = checkbox.checked;
		var $checkBox = $("#" + checkId);
		if ((savedStates[checkId] == true) || (savedStates[checkId] == "true")) {
		    $checkBox.prop('checked', true).checkboxradio('refresh');
		} else {
		    $checkBox.removeAttr('checked').checkboxradio('refresh');
		}
	    });
    }
    
}

function getLocationOfDevice() {
	console.log("locate device");
	navigator.geolocation.getCurrentPosition(geoLocationSuccess,
			geoLocationError);
	repeatTask();

}

// GEO LOCATION SUCCESS
var geoLocationSuccess = function(position) {
	console.log("lat: " + position.coords.latitude + ", lon:"
			+ position.coords.longitude);
	window.latitude = position.coords.latitude;
	window.longitude = position.coords.longitude;
	getOfferList(onOfferListSuccess, onOfferListFailure);
};

// GEO LOCATION ERROR
var geoLocationError = function() {
	console.log('Error retrieving geolocation.  Please try again later');
}

function repeatTask() {
	if (isTimeOutEnabled) {
		timeOutVar = setTimeout(getLocationOfDevice, TIMEOUT);
	}

}

function startTimeout() {
	isTimeOutEnabled = true;
	repeatTask();
}

function stopTimeout() {
	isTimeOutEnabled = false;
	clearTimeout(timeOutVar);

}

function resetTimeout() {
	stopTimeout();
	startTimeout();
}

/* UUID/GUID functions */
function s4() {
	return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
}

function guid() {
	return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4()
			+ s4() + s4();
}

/**
 * Give a unique uuid for this device Trying to get the actual uuid from the
 * device via phonegap if it fails we'll use the localStorage entry if it exists
 * otherwise we'll generate a random uuid for this device
 * 
 * @returns {Number}
 */
function getDeviceUUID() {
	var uuid = 0;
	try {
		uuid = device.uuid; // this is a Phonegap API
	} catch (err) {
		if (localStorage.getItem("uuid") == null) {
			uuid = guid();
			localStorage.setItem("uuid", uuid);
		} else {
			uuid = localStorage.getItem("uuid");
		}

	}

	return uuid;
}

function getAndroidMacAddress() {
	var macAddress = cordova.require("cordova/plugin/macaddress");
	macAddress.get(function(result) {
		console.log("mac: " + result);
		window.macAddress = result;
	}, function(error) {
		console.log("mac error: " + result);
	});

}

function getiOSMacAddress() {
    window.ios_macaddress("", function(result) {
                          console.log("mac: " + result);
                          window.macAddress = result;
                          });

}

function getiOSMCC() {
    window.ios_mcc("", function(result) {
                          console.log("mcc: " + result);
                          window.mcc = result;
                          });
    
}

function getiOSMNC() {
    window.ios_mnc("", function(result) {
                          console.log("mnc: " + result);
                          window.mnc = result;
                          });
    
}

function getiOSCarrier() {
    window.ios_carrier("", function(result) {
                          console.log("carrier: " + result);
                          window.carrier = result;
                          });
    
}


function getMsisdn() {
	var msisdn = cordova.require("cordova/plugin/msisdn");
	msisdn.get(function(result) {
		console.log("msisdn: " + result);
		window.msisdn = result;
	}, function(error) {
		console.log("msisdn error: " + result);
	});

}

function getMapChooser(){
	var mapChooser = cordova.require("cordova/plugin/mapchooser");
	mapChooser.show(function(result) {
	}, function(error) {
	});
}


function checkConnection() {
	var networkState = navigator.connection.type;
	var states = {};
	states[Connection.UNKNOWN] = 'Unknown connection';
	states[Connection.ETHERNET] = 'Ethernet connection';
	states[Connection.WIFI] = 'WiFi connection';
	states[Connection.CELL_2G] = 'Cell 2G connection';
	states[Connection.CELL_3G] = 'Cell 3G connection';
	states[Connection.CELL_4G] = 'Cell 4G connection';
	states[Connection.NONE] = 'No network connection';

	if (networkState == Connection.NONE || networkState == Connection.UNKNOWN)
		return false;
	else
		return true;
}

function getAndroidWirelessSettings() {
	var wirelessSettings = cordova.require("cordova/plugin/wireless-settings");
	wirelessSettings.show(function(result) {
	}, function(error) {
	});
}

function onConfirm() {
	getAndroidWirelessSettings();
}

function showConfirm() {
	navigator.notification.confirm(
			'Please enable either WI-FI and/or Mobile network', onConfirm,
			'No active internet connection', 'Settings');
}


function loadExternalUrl(url) {
    window.open(url, "_system");
    
}

function debug(obj) {
	alert(JSON.stringify(obj, undefined, 2));
}

function showDisclaimer(callback) {
    navigator.notification.confirm(
                                   'ValuText tracks your location and device to offer you the best deals and user experience. Though we only use your device data for your benefit, we still have to notify you.', callback,
                                   'Thanks for downloading ValuText!', 'I accept. Show me the deals!');
}

function checkFirstTimeUser(callback) {
    if (localStorage.getItem("first_time") == null) {
        showDisclaimer(callback);
        localStorage.setItem("first_time", false);
        
    } else {
        callback();
    }
}

