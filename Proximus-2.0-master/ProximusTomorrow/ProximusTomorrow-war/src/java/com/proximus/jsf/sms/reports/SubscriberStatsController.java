/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.sms.reports;

import com.proximus.data.Brand;
import com.proximus.data.util.DateUtil;
import com.proximus.jsf.AbstractController;
import com.proximus.manager.sms.*;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import org.apache.log4j.Logger;

/**
 *
 * @author Angela Mercer
 */
@ManagedBean(name = "subscriberStatsController")
@SessionScoped
public class SubscriberStatsController extends AbstractController implements Serializable {

    private Brand selectedBrand;
    private List<Brand> brands;
    private Date startDate;
    private Date endDate;
    private double averageNumberOfOffersSentPerSubscriber = 0.0;
    private double averageTimeBetweenOffers = 0.0;
    private double averageDistanceFromGeoFence = 0.0;
    private double averageDistanceFromGeoFenceLessThanTwenty = 0.0;
    private static String TWENTY_MILES = "20";
    @EJB
    SubscriberManagerLocal subscriberMgr;
    @EJB
    BrandManagerLocal brandMgr;
    @EJB
    MobileOfferSettingsManagerLocal settingsMgr;
    @EJB
    LocationDataManagerLocal locationMgr;
    @EJB
    MobileOfferSendLogManagerLocal sendLogMgr;
    @EJB
    PropertyManagerLocal propertyMgr;
    private static final Logger logger = Logger.getLogger(SubscriberStatsController.class.getName());

    public Date getEndDate() {
        if (endDate == null) {
            endDate = DateUtil.getLastDayOfMonth(new Date());
        }
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        if (startDate == null) {
            startDate = DateUtil.getFirstDayOfMonth(new Date());
        }
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public double getAverageDistanceFromGeoFence() {
        return averageDistanceFromGeoFence;
    }

    public void setAverageDistanceFromGeoFence(double averageDistanceFromGeoFence) {
        this.averageDistanceFromGeoFence = averageDistanceFromGeoFence;
    }

    public double getAverageDistanceFromGeoFenceLessThanTwenty() {
        return averageDistanceFromGeoFenceLessThanTwenty;
    }

    public void setAverageDistanceFromGeoFenceLessThanTwenty(double averageDistanceFromGeoFenceLessThanTwenty) {
        this.averageDistanceFromGeoFenceLessThanTwenty = averageDistanceFromGeoFenceLessThanTwenty;
    }

    public List<Brand> getBrands() {
        if (brands == null) {
            retrieveBrands();
        }
        return brands;
    }

    public void setBrands(List<Brand> brands) {
        this.brands = brands;
    }

    private void retrieveBrands() {
        brands = brandMgr.getAllSorted();
    }

    public Brand getSelectedBrand() {
        if (this.selectedBrand == null) {
            this.selectedBrand = this.getBrandFromSession();
        }
        return selectedBrand;
    }

    public void setSelectedBrand(Brand selectedBrand) {
        this.selectedBrand = selectedBrand;
    }

    public double getAverageNumberOfOffersSentPerSubscriber() {
        return averageNumberOfOffersSentPerSubscriber;
    }

    public void setAverageNumberOfOffersSentPerSubscriber(double averageNumberOfOffersSentPerSubscriber) {
        this.averageNumberOfOffersSentPerSubscriber = averageNumberOfOffersSentPerSubscriber;
    }

    public double getAverageTimeBetweenOffers() {
        return averageTimeBetweenOffers;
    }

    public void setAverageTimeBetweenOffers(double averageTimeBetweenOffers) {
        this.averageTimeBetweenOffers = averageTimeBetweenOffers;
    }

    public String prepareStatistics() {
        selectedBrand = this.getBrandFromSession();
        startDate = DateUtil.getFirstDayOfMonth(new Date());
        endDate = new Date();
        return "/geo-reports/SubscriberStats?faces-redirect=true";
    }

    public void updateStatistics() {
        if (selectedBrand == null || selectedBrand.getName() == null || selectedBrand.getName().isEmpty()) {
            selectedBrand = this.getBrandFromSession();
        } else {
            selectedBrand = brandMgr.getBrandByName(selectedBrand.getName());
        }
        calculateRealAverageNumberOfOffersSentPerSubscriber();
        calculateAverageDistanceFromGeoFenceLessThanTwenty();
        calculateAverageDistanceFromGeoFence();
        calculateAverageTimeBetweenOffers();

    }

    private void calculateAverageTimeBetweenOffers() {
        if (selectedBrand != null) {
            averageTimeBetweenOffers = subscriberMgr.getAverageDaysBetweenMessagesByBrandAndDate(selectedBrand, startDate, endDate);
        }
    }

    private void calculateRealAverageNumberOfOffersSentPerSubscriber() {
        if (selectedBrand != null) {
            averageNumberOfOffersSentPerSubscriber = subscriberMgr.getAverageMessagesSentByBrandAndDate(selectedBrand, startDate, endDate);
        }
    }

    private void calculateAverageDistanceFromGeoFenceLessThanTwenty() {
        if (selectedBrand != null) {
            averageDistanceFromGeoFenceLessThanTwenty =
                    roundTwoDecimalPlaces(locationMgr.getAverageDistanceFromGeoFence(selectedBrand, startDate, endDate, TWENTY_MILES));
        }
    }

    private void calculateAverageDistanceFromGeoFence() {
        if (selectedBrand != null) {
            averageDistanceFromGeoFence =
                    roundTwoDecimalPlaces(locationMgr.getAverageDistanceFromGeoFence(selectedBrand, startDate, endDate, null));
        }
    }

    double roundTwoDecimalPlaces(double d) {
        DecimalFormat twoPlaces = new DecimalFormat("###.##");
        return Double.valueOf(twoPlaces.format(d));
    }
}
