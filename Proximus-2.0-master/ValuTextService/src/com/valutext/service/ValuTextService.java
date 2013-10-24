package com.valutext.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.valutext.api.ValuTextListener;
import com.valutext.api.ValuTextServiceWrapper;
import com.valutext.data.Property;
import com.valutext.util.Constants;

/**
 * Proximus Mobility LLC.
 * @author Gilberto Gaxiola Service and API end point to communicate with 3rd
 * party Applications/Activities
 */
public class ValuTextService extends Service implements LocationListener {

    private static final String TAG = ValuTextService.class.getSimpleName();
    private List<ValuTextListener> listeners = new ArrayList<ValuTextListener>();
    private List<Property> lastProperties;

    private int maxDistance = 0;
    private String username;
    private String token;
    private long minTimeBetweenUpdate;
    private long minDistanceChangeForUpdate;
    private List<String> categories;
    
    private final Object valuTextLock = new Object();

    private Timer valuTextUpdateTimer;
    private ValuTextTimerTask updateTask;

    
    
    /** LOCATION MANAGER SPECIFICS */
    /** Constants for determining how often to check for location */
    private LocationManager locationManager;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    private Location lastLocation;

    
    private final Handler handler = new Handler();


    private ValuTextServiceWrapper.Stub apiEndPoint = new ValuTextServiceWrapper.Stub() {

        @Override
        public List<Property> updateLocation() throws RemoteException {
            synchronized (valuTextLock) {
                return lastProperties;
            }

        }

        @Override
        public void addListener(ValuTextListener listener) throws RemoteException {
            synchronized (listeners) {
                listeners.add(listener);

            }
        }

        @Override
        public void removeListener(ValuTextListener listener) throws RemoteException {
            synchronized (listeners) {
                listeners.remove(listener);
            }

        }

        @Override
        public void stopGPS() throws RemoteException {
            synchronized (valuTextLock) {
                stopUsingGPS();
            }

        }

        @Override
        public void startGPS() throws RemoteException {
            synchronized (valuTextLock) {
                resumeGPSThreadSafe();
                //ValuTextService.this.onCreate();
            }

        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        if (ValuTextService.class.getName().equals(intent.getAction())) {
            return apiEndPoint;
        } else {
            return null;
        }
    }
    
    public void resumeGPSThreadSafe() {
        handler.post(new Runnable() {

            @Override
            public void run() {
                startGPS();
            }
            
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "ON START CALLED VALU TEXT SERVICE");
        Bundle extras = intent.getExtras();
        maxDistance = extras.getInt(Constants.MAX_DISTANCE);
        username = extras.getString(Constants.USERNAME);
        token = extras.getString(Constants.API_TOKEN);
        minDistanceChangeForUpdate = extras.getLong(Constants.MIN_DISTANCE_CHANGE_FOR_UPDATE);
        minTimeBetweenUpdate = extras.getLong(Constants.MIN_TIME_BETWEEN_UPDATE);
        categories = extras.getStringArrayList(Constants.CATEGORY);
        handler.post(new Runnable() {

            @Override
            public void run() {
                startGPS();
            }
            
        });

        valuTextUpdateTimer = new Timer("ValuTextUpdateTimer");
        updateTask = new ValuTextTimerTask(this);
        
        valuTextUpdateTimer.schedule(updateTask, 1000L, minTimeBetweenUpdate);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "ON CREATE CALLED VALU TEXT SERVICE");
        
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ON DESTROY CALLED VALU TEXT SERVICE");
        valuTextUpdateTimer.cancel();
        valuTextUpdateTimer = null;
    }

    public List<ValuTextListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<ValuTextListener> listeners) {
        this.listeners = listeners;
    }

    public Object getValuTextLock() {
        return valuTextLock;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }

    //Handling everything related to Location Manager
    public boolean startGPS() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            Log.e(TAG, "Couldn't initalize LocationManager from Android System Service");
            return false;
        }
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        
        if(!isGPSEnabled && !isNetworkEnabled) {
        } else {
            this.canGetLocation = true;
            // First Get location from Network Provider
            if(isNetworkEnabled) {
                
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTimeBetweenUpdate, minDistanceChangeForUpdate, this);
                    lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if(isGPSEnabled) {
                //Network Provider could get a location
                if(lastLocation == null) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTimeBetweenUpdate, minDistanceChangeForUpdate, this);
                        lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }
        }
        return true;
        
    }

    @Override
    public void onLocationChanged(Location paramLocation) {
        Log.i(TAG, "Got a new GPS location");
        lastLocation = paramLocation;

    }

    @Override
    public void onStatusChanged(String paramString, int paramInt, Bundle paramBundle) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String paramString) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderDisabled(String paramString) {
        // TODO Auto-generated method stub

    }

    /**
     * @return the isGPSEnabled
     */
    public boolean isGPSEnabled() {
        return isGPSEnabled;
    }

    /**
     * @param isGPSEnabled the isGPSEnabled to set
     */
    public void setGPSEnabled(boolean isGPSEnabled) {
        this.isGPSEnabled = isGPSEnabled;
    }

    /**
     * @return the isNetworkEnabled
     */
    public boolean isNetworkEnabled() {
        return isNetworkEnabled;
    }

    /**
     * @param isNetworkEnabled the isNetworkEnabled to set
     */
    public void setNetworkEnabled(boolean isNetworkEnabled) {
        this.isNetworkEnabled = isNetworkEnabled;
    }

    /**
     * @return the canGetLocation
     */
    public boolean isCanGetLocation() {
        return canGetLocation;
    }

    /**
     * @param canGetLocation the canGetLocation to set
     */
    public void setCanGetLocation(boolean canGetLocation) {
        this.canGetLocation = canGetLocation;
    }
    


    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
        locationManager = null;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    /**
     * @param lastLocation the lastLocation to set
     */
    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    /**
     * @return the categories
     */
    public List<String> getCategories() {
        return categories;
    }

    /**
     * @param categories the categories to set
     */
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
    
    

}