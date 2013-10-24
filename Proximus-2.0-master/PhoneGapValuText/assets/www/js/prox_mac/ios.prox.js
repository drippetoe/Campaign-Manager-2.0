window.ios_macaddress = function(str, callback) {
    cordova.exec(callback, function(err) {
                 callback('No MAC address available');
                 }, "ProxIOS", "macaddress", [str]);
};

window.ios_mcc = function(str, callback) {
    cordova.exec(callback, function(err) {
                 callback('No MCC available');
                 }, "ProxIOS", "mcc", [str]);
};

window.ios_mnc = function(str, callback) {
    cordova.exec(callback, function(err) {
                 callback('No MNC available');
                 }, "ProxIOS", "mnc", [str]);
};

window.ios_carrier = function(str, callback) {
    cordova.exec(callback, function(err) {
                 callback('No carrier available');
                 }, "ProxIOS", "carriername", [str]);
};