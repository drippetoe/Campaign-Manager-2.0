/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.response;

import com.proximus.data.sms.report.ViewActiveOffers;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ronald
 */
@XmlRootElement(name = "offerPropertyResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class OfferPropertyResponse extends BaseResponse {

    List<ViewActiveOffers> propertyOffers;

    public List<ViewActiveOffers> getPropertyOffers() {
        return propertyOffers;
    }

    public void setPropertyOffers(List<ViewActiveOffers> propertyOffers) {
        this.propertyOffers = propertyOffers;
    }
}
