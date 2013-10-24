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
@XmlRootElement(name = "address_component")
@XmlAccessorType(XmlAccessType.FIELD)
public class AddressComponent {

    @XmlElement(name = "long_name")
    private String long_name;
    @XmlElement(name = "short_name")
    private String short_name;
    @XmlElement(name = "type")
    private List<String> types;

    public AddressComponent() {
        this.types = new ArrayList<String>();
    }

    public String getLongName() {
        return long_name;
    }

    public void setLongName(String long_name) {
        this.long_name = long_name;
    }

    public String getShortName() {
        return short_name;
    }

    public void setShortName(String short_name) {
        this.short_name = short_name;
    }

    public void addType(String type){
        this.types.add(type);
    }
    
    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }


}
