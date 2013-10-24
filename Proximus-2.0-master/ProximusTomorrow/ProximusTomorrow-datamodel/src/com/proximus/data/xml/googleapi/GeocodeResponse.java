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
/*
 * Trying to parse google data
 *
 * <status>OK</status> <result> <type>street_address</type>
 * <formatted_address>1600 Amphitheatre Pkwy, Mountain View, CA 94043,
 * USA</formatted_address> <address_component> <long_name>1600</long_name>
 * <short_name>1600</short_name> <type>street_number</type> </address_component>
 */
@XmlRootElement(name = "GeocodeResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GeocodeResponse {

    @XmlElement(name = "status")
    private String status;
    @XmlElement(name = "result")
    private List<Result> results;

    public GeocodeResponse() {
        this.results = new ArrayList<Result>();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void addResult(Result result){
        this.results.add(result);
    }
    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }
}
