/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.response;

import com.proximus.data.sms.GeoFence;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ronald
 */
@XmlRootElement(name = "closestGeoFencesReponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ClosestGeoFencesResponse extends BaseResponse {

    List<GeoFence> closestGeoFences;

    public List<GeoFence> getClosestGeoFences() {
        return closestGeoFences;
    }

    public void setClosestGeoFences(List<GeoFence> closestGeoFences) {
        this.closestGeoFences = closestGeoFences;
    }
}
