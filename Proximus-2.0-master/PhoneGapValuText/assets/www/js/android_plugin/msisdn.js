cordova.define("cordova/plugin/msisdn", function(require, exports,
		module) {
	var exec = require("cordova/exec");
	var Msisdn = function() {
	};

	var MsisdnError = function(code, message) {
		this.code = code || null;
		this.message = message || '';
	};

	Msisdn.NO_MSISDN = 0;

	Msisdn.prototype.get = function(success, fail) {
		exec(success, fail, "Msisdn", "get", []);
	};

	var msisdn = new Msisdn();
	module.exports = msisdn;
});
