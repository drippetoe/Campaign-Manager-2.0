/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.data.xml.googleapi;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author eric
 */
@XmlRootElement(name = "result")
@XmlAccessorType(XmlAccessType.FIELD)
public class Result {

    @XmlElement(name = "type")
    private String type;
    @XmlElement(name = "formatted_address")
    private String formatted_address;
    @XmlElement(name = "address_component")
    private List<AddressComponent> address_components;
    @XmlElement(name = "geometry")
    private Geometry geometry;

    public Result() {
        this.address_components = new ArrayList<AddressComponent>();
    }

    public void addAddressComponent(AddressComponent address_component) {
        this.address_components.add(address_component);
    }

    public List<AddressComponent> getAddressComponents() {
        return address_components;
    }

    public void setAddressComponents(List<AddressComponent> address_components) {
        this.address_components = address_components;
    }

    public String getFormattedAddress() {
        return formatted_address;
    }

    public void setFormattedAddress(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
}
