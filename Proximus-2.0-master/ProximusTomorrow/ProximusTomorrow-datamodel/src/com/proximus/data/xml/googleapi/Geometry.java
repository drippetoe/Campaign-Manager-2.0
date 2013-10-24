/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.data.xml.googleapi;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author eric
 */
/*
 * <geometry> <location> <lat>37.4217550</lat> <lng>-122.0846330</lng>
 * </location> <location_type>ROOFTOP</location_type> <viewport> <southwest>
 * <lat>37.4188514</lat> <lng>-122.0874526</lng> </southwest> <northeast>
 * <lat>37.4251466</lat> <lng>-122.0811574</lng> </northeast> </viewport>
 * </geometry>
 */
@XmlRootElement(name = "address_component")
@XmlAccessorType(XmlAccessType.FIELD)
public class Geometry {

    @XmlElement(name = "location")
    private XmlLatLng location;
    @XmlElement(name = "location_type")
    private String location_type;    
    @XmlElement(name = "viewport")
    private Bounds viewport;
    @XmlElement(name = "bounds")
    private Bounds bounds;

    public Geometry() {
    }

    public XmlLatLng getLocation() {
        return location;
    }

    public void setLocation(XmlLatLng location) {
        this.location = location;
    }

    public String getLocationType() {
        return location_type;
    }

    public void setLocationType(String location_type) {
        this.location_type = location_type;
    }

    public Bounds getViewport() {
        return viewport;
    }

    public void setViewport(Bounds viewport) {
        this.viewport = viewport;
    }

    public Bounds getBounds() {
        return bounds;
    }

    public void setBounds(Bounds bounds) {
        this.bounds = bounds;
    }
    
}
