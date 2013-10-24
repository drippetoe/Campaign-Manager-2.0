package com.proximus.phonegaptest.plugin;

import java.util.List;

import org.apache.cordova.DroidGap;
import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.PluginResult;
import org.apache.cordova.api.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

public class MapChooserPlugin extends CordovaPlugin {
	private static final String ACTION = "show";

	@Override
	public boolean execute(String action, JSONArray data,
			CallbackContext callbackContext) {

		if (ACTION.equals(action)) {
			PluginResult result = null;
			String address, city, state, zip;
			try {
				address = data.getJSONObject(0).getString("address");
				city = data.getJSONObject(0).getString("city");
				state = data.getJSONObject(0).getString("state");
				zip = data.getJSONObject(0).getString("zip");

				result = show(address, city, state, zip);
			} catch (JSONException e) {
				return false;
			}
			callbackContext.sendPluginResult(result);
			return true;

		} else {
			callbackContext.sendPluginResult(new PluginResult(
					PluginResult.Status.INVALID_ACTION));
			return false;
		}
	}

	private PluginResult show(String address, String city, String state,
			String zip) {

		Intent navigationIntent = getNavigationIntent(address, city, state, zip);

		// Verify it resolves
		PackageManager packageManager = ((DroidGap) this.cordova)
				.getPackageManager();
		List<ResolveInfo> activities = packageManager.queryIntentActivities(
				navigationIntent, 0);
		boolean isNavigationIntentSafe = activities.size() > 0;

		// Start an activity if it's safe
		if (isNavigationIntentSafe) {
			String title = "Get directions with:";
			// Create and start the chooser
			Intent chooser = Intent.createChooser(navigationIntent, title);
			((DroidGap) this.cordova).getContext().startActivity(chooser);
		} else {
			String title = "Open map with:";
			Intent mapIntent = getMapIntent(address, city, state, zip);
			// Create and start the chooser
			Intent chooser = Intent.createChooser(mapIntent, title);
			((DroidGap) this.cordova).getContext().startActivity(chooser);
		}
		return new PluginResult(Status.OK);
	}

	/**
	 * Navigate using these instead of lat, lon because the navigator apps give
	 * better results with full address
	 * 
	 * @param address
	 * @param city
	 * @param state
	 * @param zip
	 * @return navigationIntent
	 */
	private Intent getNavigationIntent(String address, String city,
			String state, String zip) {
		String url = "google.navigation:";
		url += "q=" + address + "+" + city + "+" + state + "+" + zip;
		url += "&z=18";
		Uri location = Uri.parse(url);
		Intent navigationIntent = new Intent(Intent.ACTION_VIEW, location);
		return navigationIntent;

	}

	/**
	 * Navigate using these instead of lat, lon because the navigator apps give
	 * better results with full address
	 * 
	 * @param address
	 * @param city
	 * @param state
	 * @param zip
	 * @return mapIntent
	 */
	private Intent getMapIntent(String address, String city, String state,
			String zip) {
		String url = "http://maps.google.com/maps?";
		url += "q=" + address + "+" + city + "+" + state + "+" + zip;
		url += "&z=18";
		Uri location = Uri.parse(url);
		Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
		return mapIntent;
	}
}
