package com.dshaw.locationtester;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

public class SimpleLocationManager implements LocationListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
	
	private static final long TIMESTAMP_MAX_LIFE = 1000 * 60 * 30;
	
	private Location lastLocation;
	
	private ArrayList<LocationNotifier> interestedParties = new ArrayList<LocationNotifier>();
	
	public static final String TAG = SimpleLocationManager.class.getSimpleName();
	
	private FragmentActivity parent;
	private LocationClient locationClient;
	private LocationManager locationMgr;
	
	private static final long MIN_TIME_MS = 1000 * 15;
	private static final float MIN_DISTANCE_METERS = 5;
	
	public SimpleLocationManager(MainActivity parent)
	{
		this.parent = parent;
	}
	
	public void addListener(LocationNotifier listener)
	{
		interestedParties.add(listener);
	}
	
	public Location getLastLocation()
	{
		return lastLocation;
	}
	
	public void enableLocationServices()
	{
		Log.d(TAG, "Enable location service");
		//Toast.makeText(parent, "Enable location service", Toast.LENGTH_SHORT).show();
		
		if ( isSomeWeirdPlatform() )
    	{
			enableGPSDirect();
    		return;
    	}
		
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(parent);
    	if (status == ConnectionResult.SUCCESS)
    	{
    		if (locationClient == null )
    		{
    			locationClient = new LocationClient(parent, this, this);
    		}
    		locationClient.connect();
    		
    		 Log.i(TAG, "Google play is installed, sent a connect");
    		return;
    	}
    	else if (GooglePlayServicesUtil.isUserRecoverableError(status))
    	{
    		// they could have google play but chose not to
    	    Log.i(TAG, "Google play is NOT installed, but could be, sent a suggestion to install");
			ErrorDialogFragment.newInstance(status).show(parent.getSupportFragmentManager(), "errorDialog");
    	    
    	    return;
    	}
    	else
    	{
    		// try GPS
    		enableGPSDirect();
    	}
		
	}
	
	/**
	 * Put exceptions here for odd platforms
	 * @return
	 */
	private boolean isSomeWeirdPlatform()
	{
		if (System.getProperty("os.name").equals("qnx") || android.os.Build.MANUFACTURER.equalsIgnoreCase("RIM") )
		{
			Log.i(TAG, "Blackberry");
			return true; // Blackberry
		}
		
		if ( android.os.Build.MANUFACTURER.equalsIgnoreCase("Amazon") || android.os.Build.DEVICE.contains("Kindle") )
		{
			Log.i(TAG, "Amazon");
			return true;
		}
		
		return false;
	}
	
	private void enableGPSDirect()
	{
		if ( locationMgr == null)
		{
			locationMgr = (LocationManager)parent.getSystemService(Context.LOCATION_SERVICE);
		}
		locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_MS, MIN_DISTANCE_METERS, this);
		
		// @TODO maybe a dialog here if GPS is not available
	}
	
	public void disableLocationServices()
	{
		Log.d(TAG, "Disabling location service");
		//Toast.makeText(parent, "Disable location service", Toast.LENGTH_SHORT).show();
		
		if ( locationMgr != null )
        {
			locationMgr.removeUpdates(this);
        }
		
		if ( locationClient != null )
		{
			locationClient.disconnect();
		}
	}


	@Override
	public void onLocationChanged(Location location) {
		Log.d(TAG, "onLocationChanged");
		updateLocation(location, "onLocationChanged");
	}


	@Override
	public void onProviderDisabled(String provider) {
		// needed for Interface, not used
	}


	@Override
	public void onProviderEnabled(String provider) {
		updateLocation(locationClient.getLastLocation(), "onProviderEnabled");
	}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		updateLocation(locationClient.getLastLocation(), "onStatusChanged");
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.e(TAG, "onConnectionFailed");
	}

	@Override
	public void onConnected(Bundle savedInstanceState) {
		Log.i(TAG, "onConnected");
		updateLocation(locationClient.getLastLocation(), "onConnected");
	}

	@Override
	public void onDisconnected() {
		Log.i(TAG, "onDisconnected");
	}
	
	private void updateLocation(Location location, String caller)
	{
		if ( location != null )
		{
			lastLocation = location;
	        
	        for (LocationNotifier listener : interestedParties) {
				listener.newLocationFound(lastLocation);
			}
		}
		else
		{
			enableGPSDirect();
		}
	}
	
	/**
	 * @return  true if the location we have is valid (not too old, non-null)
	 */
	public boolean isLocationAvailable()
	{
		if ( lastLocation == null )
		{
			return false;
		}
		if ( System.currentTimeMillis() - lastLocation.getTime() > TIMESTAMP_MAX_LIFE )
		{
			return false;
		}
		
		return true;
	}
	
	public static class ErrorDialogFragment extends DialogFragment {
		protected static final String TAG_ERROR_DIALOG_FRAGMENT="errorDialog";
		static final String ARG_STATUS = "status";

		static ErrorDialogFragment newInstance(int status) {
			Bundle args = new Bundle();
			args.putInt(ARG_STATUS, status);
			ErrorDialogFragment result = new ErrorDialogFragment();
			result.setArguments(args);
			return (result);
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Bundle args = getArguments();
			return GooglePlayServicesUtil.getErrorDialog(
					args.getInt(ARG_STATUS), getActivity(), 0);
		}

		@Override
		public void onDismiss(DialogInterface dlg) {
			if (getActivity() != null) {
				getActivity().finish();
			}
		}
	}

}
