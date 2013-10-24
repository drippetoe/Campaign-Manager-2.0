/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice.request;

import java.util.Date;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class EndUserRequestModel {

    private String deviceId;
    private String deviceId2;
    private String pringoId;
    private String clientVersion;
    private String timezone;
    private Double latitude;
    private Double longitude;
    private Double locationAccuracy;
    private Date locationTimestamp;

    public EndUserRequestModel(String deviceId, String deviceId2, String pringoId, String clientVersion, String timezone, Double latitude, Double longitude, Double locationAccuracy, Date locationTimestamp) {
        this.deviceId = deviceId;
        this.deviceId2 = deviceId2;
        this.pringoId = pringoId;
        this.clientVersion = clientVersion;
        this.timezone = timezone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationAccuracy = locationAccuracy;
        this.locationTimestamp = locationTimestamp;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId2() {
        return deviceId2;
    }

    public void setDeviceId2(String deviceId2) {
        this.deviceId2 = deviceId2;
    }

    public String getPringoId() {
        return pringoId;
    }

    public void setPringoId(String pringoId) {
        this.pringoId = pringoId;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLocationAccuracy() {
        return locationAccuracy;
    }

    public void setLocationAccuracy(Double locationAccuracy) {
        this.locationAccuracy = locationAccuracy;
    }

    public Date getLocationTimestamp() {
        return locationTimestamp;
    }

    public void setLocationTimestamp(Date locationTimestamp) {
        this.locationTimestamp = locationTimestamp;
    }
}
