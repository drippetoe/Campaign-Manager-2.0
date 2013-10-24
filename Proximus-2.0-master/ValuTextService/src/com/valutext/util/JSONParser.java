package com.valutext.util;

import static com.valutext.data.MobileOffer.*;
import static com.valutext.data.Property.*;
import static com.valutext.data.Response.*;
import static com.valutext.data.Category.*;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.AssetManager;
import android.location.Location;
import android.util.Log;

import com.valutext.data.Category;
import com.valutext.data.MobileOffer;
import com.valutext.data.Property;
import com.valutext.data.Response;

public class JSONParser {

    private static final String TAG = JSONParser.class.getSimpleName();

    public JSONParser(Context context) {

    }

    public static Response DebugResponseAsset(Context context, String filename) {
        try {
            //filename = "pretty.json";
            AssetManager am = context.getAssets();
            InputStream is = am.open(filename);
            String json = convertStreamToString(is);
            Response response = JSONParser.ParseResponse(new JSONObject(json));
            return response;
        } catch (Exception ex) {
            Log.e(TAG, "Caught Error, " + ex.getMessage());
            // ex.printStackTrace();
        }
        return null;
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is), 8192);
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        is.close();
        return sb.toString();
    }

    public static Response ParseResponse(JSONObject jsonResponse) throws Exception {
        String status = jsonResponse.getString(JSON_RESPONSE_STATUS);
        String status_message = jsonResponse.getString(JSON_RESPONSE_STATUS_MESSAGES);
        if(!status.equalsIgnoreCase("200")) {
            Log.e("SERVER API CALL", "Server Response: " + status + " [" + status_message + "]");
            
            return null;
        }
        JSONArray jsonProperties = jsonResponse.getJSONArray(JSON_RESPONSE_PROPERTIES_ARRAY);
        List<Property> properties = new ArrayList<Property>();
        for (int i = 0; i < jsonProperties.length(); i++) {
            JSONObject p = jsonProperties.getJSONObject(i);

            /**
             * Offers
             */
            List<MobileOffer> offers = new ArrayList<MobileOffer>();

            JSONArray jsonOffers = p.getJSONArray(JSON_PROPERTY_OFFERS_ARRAY);
            for (int j = 0; j < jsonOffers.length(); j++) {
                String id, name, text, passbookBarcode, passbookHeader, passbookSubheader;
                JSONObject o = jsonOffers.getJSONObject(j).getJSONObject(JSON_MOBILE_OFFER);

                id = o.getString(JSON_MOBILE_OFFER_ID);
                name = o.getString(JSON_MOBILE_OFFER_NAME);
                text = o.getString(JSON_MOBILE_OFFER_TEXT);
                passbookBarcode = o.getString(JSON_MOBILE_OFFER_PASSBOOK_BARCODE);
                passbookHeader = o.getString(JSON_MOBILE_OFFER_PASSBOOK_HEADER);
                passbookSubheader = o.getString(JSON_MOBILE_OFFER_PASSBOOK_SUBHEADER);
                MobileOffer offer = new MobileOffer(id, name, text, passbookBarcode, passbookHeader, passbookSubheader);
                offers.add(offer);

            }
            /**
             * Parse Property
             */
            String id, name, address, city, stateProvince, country, zipcode, distance;
            id = p.getString(JSON_PROPERTY_ID);
            name = p.getString(JSON_PROPERTY_NAME);
            address = p.getString(JSON_PROPERTY_ADDRESS);
            city = p.getString(JSON_PROPERTY_CITY);
            stateProvince = p.getString(JSON_PROPERTY_STATE_PROVINCE);
            country = p.getString(JSON_PROPERTY_COUNTRY);
            zipcode = p.getString(JSON_PROPERTY_ZIPCODE);
            distance = p.getString(JSON_PROPERTY_DISTANCE);

            JSONObject geoPoint = p.getJSONObject(JSON_PROPERTY_LOCATION);
            double latitude = geoPoint.getDouble("lat");
            double longitude = geoPoint.getDouble("lng");

            Location location = new Location(id);
            location.setLatitude(latitude);
            location.setLongitude(longitude);

            Property property = new Property(id, name, address, city, stateProvince, country, zipcode, distance, location, offers);
            properties.add(property);

        }
        //TODO currently we are not parsing Categories at the moment therefore we are passing null
        Response response = new Response(status, status_message, properties, null);
        //Log.d(TAG, response.toString());
        return response;

    }
    
    
    public static Response parseCategoriesResponse(JSONObject jsonResponse)
            throws Exception {

        String status = jsonResponse.getString(JSON_RESPONSE_STATUS);
        String status_message = jsonResponse
                .getString(JSON_RESPONSE_STATUS_MESSAGES);
        if (!status.equalsIgnoreCase("200")) {
            Log.e("SERVER API CALL", "Server Response: " + status + " ["
                    + status_message + "]");

            return null;
        }

        /**
         * Categories
         */
        List<Category> categories = new ArrayList<Category>();

        JSONArray jsonOffers = jsonResponse.getJSONArray(JSON_CATEGORY);
        for (int j = 0; j < jsonOffers.length(); j++) {
            String id, name, webName;
            JSONObject o = jsonOffers.getJSONObject(j);

            id = o.getString(JSON_CATEGORY_ID);
            name = o.getString(JSON_CATEGORY_NAME);
            webName = o.getString(JSON_CATEGORY_WEB_NAME);
            Category category = new Category(id, name, webName);

            categories.add(category);

        }

        Response response = new Response(status, status_message, null,categories);
        // Log.d(TAG, response.toString());
        return response;

    }


}
