/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.data.xml.googleapi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author eric
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlLatLng {

    @XmlElement(name = "lat")
    private double lat;
    @XmlElement(name = "lng")
    private double lng;

    public XmlLatLng() {
    }

    public XmlLatLng(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
