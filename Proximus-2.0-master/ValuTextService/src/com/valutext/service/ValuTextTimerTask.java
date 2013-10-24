package com.valutext.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;

import com.valutext.api.ValuTextListener;
import com.valutext.data.Response;
import com.valutext.util.Constants;
import com.valutext.util.JSONParser;
import com.valutext.util.RESTClient;

/**
 * Proximus Mobility LLC.
 * @author Gilberto Gaxiola
 * @version 1.0
 * TimerTask process that goes and fetches the Properties and Offers closest to
 * the device via REST api calls
 */
public class ValuTextTimerTask extends TimerTask {

    private static final String TAG = ValuTextTimerTask.class.getSimpleName();

    /** Hooks to our Service and LocationListener */
    private ValuTextService service;

    private Location lastLocation;
    private JSONObject jsonObject;

    private String username;
    private String token;
    private int maxDistance;
    private List<String> categories;

    /**
     * Constructor
     * 
     * @param locationListener
     */
    public ValuTextTimerTask(ValuTextService service) {
        this.service = service;
        
    }

    @Override
    public void run() {
        try {
            synchronized (service.getValuTextLock()) {
                Log.i(TAG, "ValuTextTimerTask Running");
                if (service.getLastLocation() != null) {
                    double lat = service.getLastLocation().getLatitude();
                    double lon = service.getLastLocation().getLongitude();
                    //Settings the sensible defaults for the parameters used in the REST client
                    username = service.getUsername();
                    token = service.getToken();
                    categories = null;
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(service);
                    //SharedPreferences sharedPref = service.getSharedPreferences("com.valutext.sharedPrefs", Context.MODE_PRIVATE);
                    String cats = sharedPref.getString(Constants.CATEGORY, "");
                    Log.w("CATEGORIES", "CATEGORIES found: " + cats);
                    
                    if(cats != null) {
                        categories = new ArrayList<String>(Arrays.asList(cats.split(","))); 
                    }
                    
                    
                    

                    maxDistance = service.getMaxDistance();
                    maxDistance = maxDistance == 0 ? 6 : maxDistance;
                    // Call to Rest API Server to get closest Properties with
                    if (lastLocation == null || !lastLocation.equals(service.getLastLocation())) {
                        try {
                            jsonObject = RESTClient.getClosestProperties(service, username, token, lat, lon, maxDistance, categories);
                            
                            // Log.i(TAG, jsonObject.toString());
                            Response response = JSONParser.ParseResponse(jsonObject);
                            if (response == null) {
                                Log.d(TAG, "Couldn't parse JSON correctly finishing task");
                                return;
                            }

                            List<ValuTextListener> listeners = service.getListeners();

                            synchronized (listeners) {
                                for (ValuTextListener l : listeners) {
                                    try {
                                        l.handleLocationUpdate(response.getProperties());
                                    } catch (Throwable t) {
                                        Log.w(TAG, "Failed to notify listener " + l, t);

                                    }
                                }
                            }

                        } catch (Throwable t) {
                            Log.e(TAG, "ERROR retrieving REST call from api.proximusmobility.com", t);
                        }

                        lastLocation = service.getLastLocation();
                    } else {
                        Log.i(TAG, "GPS location has not changed since last time here is the same response");
                    }
                } else {
                    Log.i(TAG, "GPS last Location was not set up");
                }
            }
        } catch (Throwable t) {
            Log.e(TAG, "Error on ValuTextTimerTask", t);
        }
    }

}
