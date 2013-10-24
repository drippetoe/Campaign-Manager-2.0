package com.proximus.phonegaptest.plugin;

import org.apache.cordova.DroidGap;
import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;

import android.content.Intent;

public class WirelessSettingsPlugin extends CordovaPlugin {
	private static final String ACTION = "show";

	@Override
	public boolean execute(String action, JSONArray data,
			CallbackContext callbackContext) {
		if (ACTION.equals(action)) {
			((DroidGap) this.cordova).getContext().startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
			return true;
		} else {
			callbackContext.sendPluginResult(new PluginResult(
					PluginResult.Status.INVALID_ACTION));
			return false;
		}
	}

}
