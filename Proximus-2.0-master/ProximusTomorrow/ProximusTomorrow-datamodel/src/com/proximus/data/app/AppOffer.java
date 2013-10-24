/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.data.app;

import com.proximus.data.sms.Category;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ronald
 */
@XmlRootElement(name = "appOffer")
@XmlAccessorType(XmlAccessType.FIELD)
public class AppOffer {

    private String offerId;
    private Long webOfferId;
    private String propertyWebHash;
    private BigDecimal distance;
    private String retailerName;
    private String cleanOfferText;
    private String offerImage;
    private Date expirationDate;
    private String address;
    private String city;
    private String stateProvince;
    private String zipcode;
    private Double latitude;
    private Double longitude;
    private String passbookBarcode;
    private List<Category> categories;

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public Long getWebOfferId() {
        return webOfferId;
    }

    public void setWebOfferId(Long webOfferId) {
        this.webOfferId = webOfferId;
    }

    public String getPropertyWebHash() {
        return propertyWebHash;
    }

    public void setPropertyWebHash(String propertyWebHash) {
        this.propertyWebHash = propertyWebHash;
    }

    public BigDecimal getDistance() {
        return distance;
    }

    public void setDistance(BigDecimal distance) {
        this.distance = distance;
    }

    public String getRetailerName() {
        return retailerName;
    }

    public void setRetailerName(String retailerName) {
        this.retailerName = retailerName;
    }

    public String getCleanOfferText() {
        return cleanOfferText;
    }

    public void setCleanOfferText(String cleanOfferText) {
        this.cleanOfferText = cleanOfferText;
    }

    public String getOfferImage() {
        return offerImage;
    }

    public void setOfferImage(String offerImage) {
        this.offerImage = offerImage;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getPassbookBarcode() {
        return passbookBarcode;
    }

    public void setPassbookBarcode(String passbookBarcode) {
        this.passbookBarcode = passbookBarcode;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
