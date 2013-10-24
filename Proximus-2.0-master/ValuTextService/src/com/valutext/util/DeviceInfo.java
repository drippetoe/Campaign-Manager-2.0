package com.valutext.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

/**
 * Proximus Mobility LLC. 
 * @version 1.0
 * @author Eric Johansson
 *
 */
public class DeviceInfo {

	/**
	 * Returns the macAddress of the Wifi interface
	 * 
	 * @param context
	 * 	Usage: DeviceInfo.getMacAddress(this.getApplicationContext());
	 * @return
	 */
	public static String getMacAddress(Context context) {
		WifiInfo info = ((WifiManager)context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
		return info==null?null:info.getMacAddress();
	}
	
    public static String getMsisdn(Context context) {
        TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tMgr.getLine1Number();
    }

	
	 public static boolean isConnectingToInternet(Context context){
	        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	          if (connectivity != null)
	          {
	              NetworkInfo[] info = connectivity.getAllNetworkInfo();
	              if (info != null)
	                  for (int i = 0; i < info.length; i++)
	                      if (info[i].getState() == NetworkInfo.State.CONNECTED)
	                      {
	                          return true;
	                      }
	 
	          }
	          return false;
	    }
}