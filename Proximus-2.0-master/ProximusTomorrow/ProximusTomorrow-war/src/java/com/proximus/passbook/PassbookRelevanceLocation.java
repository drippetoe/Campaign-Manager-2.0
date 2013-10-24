/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.passbook;

/**
 * Show pass on lock screen if within range
 *
 * Location queried on Screen wake, inexpensive lookup performed
 * Provide up to 10 coordinates of pass.json
 * Radius is tied to pass style
 * Exit fence for location-relevant passes, silently withdraw pass from lock screen
 * 
 * @author dshaw
 */
public class PassbookRelevanceLocation {

    private Double longitude;
    private Double latitude;

    public PassbookRelevanceLocation(Double longitude, Double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

}
