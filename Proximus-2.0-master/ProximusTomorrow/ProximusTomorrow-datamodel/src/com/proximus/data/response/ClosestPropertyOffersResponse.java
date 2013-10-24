/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.data.response;

import com.proximus.data.sms.GeoPoint;
import com.proximus.data.sms.Property;
import com.proximus.data.sms.report.ViewActiveOffers;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ronald
 */
@XmlRootElement(name = "closestPropertyOffersResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ClosestPropertyOffersResponse extends BaseResponse {

    private List<GeoPoint> propertyGeoPoints;
    private List<Property> closestProperties;
    private List<List<ViewActiveOffers>> propertyOffers;

    public List<GeoPoint> getPropertyGeoPoints() {
        return propertyGeoPoints;
    }

    public void setPropertyGeoPoints(List<GeoPoint> propertyGeoPoints) {
        this.propertyGeoPoints = propertyGeoPoints;
    }

    public List<Property> getClosestProperties() {
        return closestProperties;
    }

    public void setClosestProperties(List<Property> closestProperties) {
        this.closestProperties = closestProperties;
    }

    public List<List<ViewActiveOffers>> getPropertyOffers() {
        return propertyOffers;
    }

    public void setPropertyOffers(List<List<ViewActiveOffers>> propertyOffers) {
        this.propertyOffers = propertyOffers;
    }
}
