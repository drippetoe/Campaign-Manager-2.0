package com.proximus.phonegaptest.plugin;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;

import android.content.Context;
import android.telephony.TelephonyManager;

public class MsisdnPlugin extends CordovaPlugin {
	private static final String GET = "get";

	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) {
		if (action.equals(GET)) {
			TelephonyManager telephonyManager = (TelephonyManager) this.cordova
					.getActivity().getSystemService(Context.TELEPHONY_SERVICE);
			String result = telephonyManager.getLine1Number();
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
