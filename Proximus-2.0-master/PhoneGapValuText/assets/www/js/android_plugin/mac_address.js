cordova.define("cordova/plugin/macaddress", function(require, exports,
		module) {
	var exec = require("cordova/exec");
	var MacAddress = function() {
	};

	var MacAddressError = function(code, message) {
		this.code = code || null;
		this.message = message || '';
	};

	MacAddress.NO_MAC_ADDRESS = 0;

	MacAddress.prototype.get = function(success, fail) {
		exec(success, fail, "MacAddress", "get", []);
	};

	var macAddress = new MacAddress();
	module.exports = macAddress;
});
