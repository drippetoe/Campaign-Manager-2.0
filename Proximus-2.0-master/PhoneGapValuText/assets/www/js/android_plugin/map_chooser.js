cordova.define("cordova/plugin/mapchooser", function(require, exports, module) {
	var exec = require("cordova/exec");
	var MapChooser = function() {
	};

	var MapChooserError = function(code, message) {
		this.code = code || null;
		this.message = message || '';
	};

	MapChooser.NO_INTENT_CHOOSER = 0;

	MapChooser.prototype.show = function(success, fail) {
		exec(success, fail, "MapChooser", "show", [ {
			'address' : window.activeOffer.address,
			'city' : window.activeOffer.city,
			'state' : window.activeOffer.stateProvince,
			'zip' : window.activeOffer.zipcode
		} ]);
	};

	var mapChooser = new MapChooser();
	module.exports = mapChooser;
});