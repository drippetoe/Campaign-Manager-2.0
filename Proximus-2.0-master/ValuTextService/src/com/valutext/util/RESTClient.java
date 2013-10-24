package com.valutext.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

/**
 * 
 * @author Ronald Williams Android RESTClient implementation
 */
public class RESTClient {

    private static final String BASE_URL = "https://secure.proximusmobility.com/ProximusTomorrow-war/api/";
    private static final String ENCODING = "UTF-8";

    private static String restResponse(String targetURL, String encoding, LinkedHashMap<String, String> mapParameters) throws ClientProtocolException, IOException {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(targetURL);

        ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : mapParameters.entrySet()) {
            parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        post.setEntity(new UrlEncodedFormEntity(parameters, encoding));
        post.addHeader("Accept", "application/json");

        HttpResponse response = client.execute(post);

        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 8192);
        String line;
        StringBuffer jsonResponse = new StringBuffer();
        while ((line = rd.readLine()) != null) {
            jsonResponse.append(line);
            jsonResponse.append('\r');
        }
        rd.close();
        return jsonResponse.toString();

    }

    public static JSONObject getClosestProperties(Context mContext, String username, String token, Double latitude, Double longitude, int maxDistance, List<String> categories) throws ClientProtocolException, JSONException, IOException {

        String targetUrl = BASE_URL + Constants.CLOSEST_PROPERTIES_RESPONSE;
        LinkedHashMap<String, String> parameterMap = new LinkedHashMap<String, String>();
        parameterMap.put(Constants.LATITUDE, latitude.toString());
        parameterMap.put(Constants.LONGITUDE, longitude.toString());
        parameterMap.put(Constants.USERNAME, username);
        parameterMap.put(Constants.API_TOKEN, token);
        parameterMap.put(Constants.MAX_DISTANCE, maxDistance + "");
        parameterMap.put(Constants.UUID, DeviceIdentifier.id(mContext));

        if (categories != null && !categories.isEmpty()) {
            StringBuffer sb = new StringBuffer();
            String delim = "";
            for (String s : categories) {
                sb.append(delim).append(s);
                delim = ",";
            }
            parameterMap.put(Constants.CATEGORY, sb.toString());
        }

        String mac = DeviceInfo.getMacAddress(mContext);
        if (mac != null) {
            parameterMap.put(Constants.MAC_ADDRESS, mac);
        }

        String msisdn = DeviceInfo.getMsisdn(mContext);
        if (msisdn != null) {
            parameterMap.put(Constants.MSISDN, msisdn);
        }
        String response = RESTClient.restResponse(targetUrl, ENCODING, parameterMap);
        return new JSONObject(response);
    }

    public static JSONObject getCategories(String token, String username) throws ClientProtocolException, JSONException, IOException {
        String targetUrl = BASE_URL + Constants.CATEGORY_RESPONSE;
        LinkedHashMap<String, String> parameterMap = new LinkedHashMap<String, String>();

        parameterMap.put(Constants.USERNAME, username);
        parameterMap.put(Constants.API_TOKEN, token);

        String response = RESTClient.restResponse(targetUrl, ENCODING, parameterMap);
        return new JSONObject(response);
    }

}