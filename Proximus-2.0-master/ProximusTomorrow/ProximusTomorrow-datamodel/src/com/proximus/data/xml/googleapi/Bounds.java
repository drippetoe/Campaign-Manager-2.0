/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.data.xml.googleapi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author eric
 */
@XmlRootElement(name = "viewport")
@XmlAccessorType(XmlAccessType.FIELD)
public class Bounds {

    @XmlElement(name = "southwest")
    private XmlLatLng southwest;
    @XmlElement(name = "northeast")
    private XmlLatLng northeast;

    public Bounds() {
    }

    public XmlLatLng getNortheast() {
        return northeast;
    }

    public void setNortheast(XmlLatLng northeast) {
        this.northeast = northeast;
    }

    public XmlLatLng getSouthwest() {
        return southwest;
    }

    public void setSouthwest(XmlLatLng southwest) {
        this.southwest = southwest;
    }
}
