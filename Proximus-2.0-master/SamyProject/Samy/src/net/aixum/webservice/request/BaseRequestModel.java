/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice.request;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import net.aixum.webservice.gson.GSONTransient;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class BaseRequestModel {

    
    
    @SerializedName("UserId")
    private String userId; // Guid
    @SerializedName("ClientVersion")
    private Integer clientVersion;
    @SerializedName("LastSync")
    private Date lastSync;
    @SerializedName("Latitude")
    private Double latitude;
    @SerializedName("Longitude")
    private Double longitude;
    @SerializedName("LocationAccuracy")
    private Double locationAccuracy;
    @SerializedName("LocationTimestamp")
    private Date locationTimestamp;
    @SerializedName("Test")
    private String test; // Guid
    
    

    public BaseRequestModel() {
        //String get = "http://admin.samy.net/api/sync/GetCompanies?serialisedModel=%7B%22UserId%22%3A%2260f4977d-70ee-4ff8-9ea6-6b39daac6e0a%22%2C%22ClientVersion%22%3A%223%22%7D&_dc=1362582126312";
        this.userId = "60f4977d-70ee-4ff8-9ea6-6b39daac6e0a";
        this.clientVersion = 3;
        this.lastSync = null;
        this.latitude = null;
        this.longitude = null;
        this.locationAccuracy = null;
        this.locationTimestamp = null;

    }

    public BaseRequestModel(String userId, int clientVersion, Date lastSync, double latitude, double longitude, double locationAccuracy, Date locationTimestamp) {
        this.userId = userId;
        this.clientVersion = clientVersion;
        this.lastSync = lastSync;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationAccuracy = locationAccuracy;
        this.locationTimestamp = locationTimestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(int clientVersion) {
        this.clientVersion = clientVersion;
    }

    public Date getLastSync() {
        return lastSync;
    }

    public void setLastSync(Date lastSync) {
        this.lastSync = lastSync;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLocationAccuracy() {
        return locationAccuracy;
    }

    public void setLocationAccuracy(double locationAccuracy) {
        this.locationAccuracy = locationAccuracy;
    }

    public Date getLocationTimestamp() {
        return locationTimestamp;
    }

    public void setLocationTimestamp(Date locationTimestamp) {
        this.locationTimestamp = locationTimestamp;
    }
}
