package com.valutext.service;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.valutext.api.ValuTextListener;
import com.valutext.util.Constants;

/**
 * Public end point to communicate with our ValuText Service. Init() must be
 * called before you can Start() or Stop() the Service
 * @author Gilberto Gaxiola
 */
public class ValuTextAPI {

    private static ValuTextServiceConnection serviceConnection;

    /** The ValuText username */
    private static String USERNAME;
    /** The ValuText api token provided */
    private static String API_TOKEN;
    /** Maximum distance your geo fence will enclose */
    private static int MAX_DISTANCE;
    /**
     * How often in milliseconds will the GPS tracker fetch location updates
     * (defaults to 30 minutes)
     */
    private static long MIN_TIME_BETWEEN_UPDATE = Constants.THIRTY_MINUTES;
    /**
     * Minimum delta change in meters before GPS tracker will fetch location
     * updates (defaults to 50 meters)
     */
    private static long MIN_DISTANCE_CHANGE_FOR_UPDATE = Constants.FIFTY_METERS;

    private static List<String> CATEGORIES = new ArrayList<String>();

    private static boolean areVariablesSet() {
        String user = ValuTextAPI.USERNAME;
        String token = ValuTextAPI.API_TOKEN;

        if (user == null || user.equals("")) {
            return false;
        }
        if (token == null || token.equals("")) {
            return false;
        }
        return true;

    }

    /**
     * Default Initialization mechanism for the API. It must be called before
     * Start()
     * @param username The ValuText username
     * @param apiToken The ValuText api token provided
     */
    public static void Init(String username, String apiToken) {
        ValuTextAPI.USERNAME = username;
        ValuTextAPI.API_TOKEN = apiToken;
    }

    /**
     * Initialize the API with optional parameter maxDistance. It must be called
     * before Start()
     * @param username The ValuText username
     * @param apiToken The ValuText api token provided
     * @param maxDistance Maximum distance your geo fence will enclose
     */
    public static void Init(String username, String apiToken, int maxDistance) {
        ValuTextAPI.Init(username, apiToken);
        ValuTextAPI.MAX_DISTANCE = maxDistance;
    }

    /**
     * Initialize the API with 3 optional parameters maxDistance,
     * minTimeBetweenUpdates and minDistanceChangeForUpdate. It must be called
     * before Start()
     * @param username The ValuText username
     * @param apiToken The ValuText api token provided
     * @param maxDistance Maximum distance your geo fence will enclose
     * @param minTimeBetweenUpdates How often in milliseconds will the GPS
     * tracker fetch location updates
     * @param minDistanceChangeForUpdate Minimum delta change in meters before
     * GPS tracker will fetch location updates
     */
    public static void Init(String username, String apiToken, int maxDistance, long minTimeBetweenUpdates, long minDistanceChangeForUpdate) {
        ValuTextAPI.Init(username, apiToken, maxDistance);
        ValuTextAPI.MIN_TIME_BETWEEN_UPDATE = minTimeBetweenUpdates;
        ValuTextAPI.MIN_DISTANCE_CHANGE_FOR_UPDATE = minDistanceChangeForUpdate;
    }

    /**
     * Initialize the Service, by adding the Android Context as well as
     * providing the listeners that will be getting notified by our Service
     * Init() must be called before Start()
     * @param context the Android context where our service will bind
     * @param listeners the list of ValuTextListener that will get notified upon
     * successful lookups
     * @throw IllegalArgumentException if ValuTextAPI doesn't have at least the
     * USERNAME and API_TOKEN set up prior to using the service
     */
    public static void Start(Context context, List<ValuTextListener.Stub> listeners) {
        if (!areVariablesSet()) {
            throw new IllegalArgumentException("ValuTextAPI needs at least username and apiToken to be set");
        }

        Intent intent = new Intent(ValuTextService.class.getName());
        Bundle extras = createBundle();
        intent.putExtras(extras);
        // start service explicitly
        // otherwise it will only run while the IPC connection is up
        serviceConnection = new ValuTextServiceConnection(listeners);
        context.startService(intent);
        context.bindService(intent, serviceConnection, 0);
    }

    /**
     * Stop our Service
     * @param context The Android Context that the service will unbind
     */
    public static void Stop(Context context) {
        try {

            context.unbindService(serviceConnection);
            Intent intent = new Intent(ValuTextService.class.getName());
            context.stopService(intent);

        } catch (IllegalArgumentException e) {

        }
    }

    /**
     * Stop the GPS tracker
     */
    public static void stopGPS() {
        serviceConnection.stopGPS();

    }

    /**
     * Start the GPS tracker
     */
    public static void startGPS() {
        serviceConnection.startGPS();
    }

    private static Bundle createBundle() {
        if (areVariablesSet()) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.MAX_DISTANCE, MAX_DISTANCE <= 0 ? 6 : MAX_DISTANCE);
            bundle.putString(Constants.USERNAME, USERNAME);
            bundle.putString(Constants.API_TOKEN, API_TOKEN);
            bundle.putLong(Constants.MIN_TIME_BETWEEN_UPDATE, MIN_TIME_BETWEEN_UPDATE <= 0 ? Constants.THIRTY_MINUTES : MIN_TIME_BETWEEN_UPDATE);
            bundle.putLong(Constants.MIN_DISTANCE_CHANGE_FOR_UPDATE, MIN_DISTANCE_CHANGE_FOR_UPDATE <= 0 ? Constants.FIFTY_METERS : MIN_DISTANCE_CHANGE_FOR_UPDATE);
            return bundle;
        } else {
            return null;
        }
    }

}
