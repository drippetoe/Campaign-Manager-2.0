cordova.define("cordova/plugin/wireless-settings", function(require, exports, module) {
	var exec = require("cordova/exec");
	var WirelessSettings = function() {
	};

	var WirelessSettingsError = function(code, message) {
		this.code = code || null;
		this.message = message || '';
	};

	WirelessSettings.NO_WIRELESS_SETTINGS = 0;

	WirelessSettings.prototype.show = function(success, fail) {
		exec(success, fail, "WirelessSettings", "show", []);
	};

	var wirelessSettings = new WirelessSettings();
	module.exports = wirelessSettings;
});