/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.data.app;

import com.proximus.data.response.BaseResponse;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ronald
 */
@XmlRootElement(name = "appOfferResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class AppOfferResponse extends BaseResponse {

    private List<AppOffer> appOffers;

    public List<AppOffer> getAppOffers() {
        return appOffers;
    }

    public void setAppOffers(List<AppOffer> appOffers) {
        this.appOffers = appOffers;
    }
}
