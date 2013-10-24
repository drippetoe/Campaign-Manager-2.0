package com.proximus.phonegaptest.plugin;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;

import android.content.Context;
import android.net.wifi.WifiManager;

public class MacAddressPlugin extends CordovaPlugin {
	private static final String GET = "get";

	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) {
		if (action.equals(GET)) {
			WifiManager wm = (WifiManager) this.cordova.getActivity()
					.getSystemService(Context.WIFI_SERVICE);
			String result = wm.getConnectionInfo().getMacAddress();
			if (result != null) {
				callbackContext.sendPluginResult(new PluginResult(
						PluginResult.Status.OK, result));
				return true;
			}
		}
		callbackContext.sendPluginResult(new PluginResult(
				PluginResult.Status.ERROR));
		return false;
	}

}
