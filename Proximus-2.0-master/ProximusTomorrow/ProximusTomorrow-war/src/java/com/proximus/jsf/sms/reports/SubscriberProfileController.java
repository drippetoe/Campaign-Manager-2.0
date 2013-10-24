/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.sms.reports;

import com.proximus.bean.sms.GeoPointMarker;
import com.proximus.bean.sms.SubscriberLocationProfile;
import com.proximus.data.Company;
import com.proximus.data.sms.LocationData;
import com.proximus.data.sms.GeoFence;
import com.proximus.data.sms.GeoPoint;
import com.proximus.data.sms.MobileOfferSendLog;
import com.proximus.data.sms.Subscriber;
import com.proximus.data.util.DateUtil;
import com.proximus.data.xml.googleapi.GeocodeResponse;
import com.proximus.data.xml.googleapi.Result;
import com.proximus.helper.util.JsfUtil;
import com.proximus.jsf.AbstractController;
import com.proximus.jsf.datamodel.sms.GeoFenceModel;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.sms.LocationDataManagerLocal;
import com.proximus.manager.sms.GeoFenceManagerLocal;
import com.proximus.manager.sms.MobileOfferSendLogManagerLocal;
import com.proximus.manager.sms.SubscriberManagerLocal;
import com.proximus.registration.client.Registrar;
import com.proximus.util.server.GeoUtil;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.model.map.*;

/**
 *
 * @author ronald
 */
@ManagedBean(name = "subscriberProfileController")
@SessionScoped
public class SubscriberProfileController extends AbstractController implements Serializable {

    private static final long serialVersionUID = 1;
    private static final String ICON_GREEN = "http://maps.google.com/mapfiles/ms/micons/green.png";
    private static final String ICON_YELLOW = "http://maps.google.com/mapfiles/ms/micons/yellow.png";
    private static final String ICON_RED = "http://maps.google.com/mapfiles/ms/micons/red.png";
    private static final String RED = "#FF0000";
    private static final String WHITE = "#FFFFFF";
    @EJB
    CompanyManagerLocal companyMgr;
    @EJB
    SubscriberManagerLocal subscriberMgr;
    @EJB
    LocationDataManagerLocal locationMgr;
    @EJB
    MobileOfferSendLogManagerLocal sendLogMgr;
    @EJB
    GeoFenceManagerLocal geoFenceMgr;
    private Company company;
    private String msisdn;
    private Date startDate;
    private Date endDate;
    private List<LocationData> lookups;
    private List<MobileOfferSendLog> mobileOffers;
    private List<Subscriber> subscriberList;
    private List<SubscriberLocationProfile> locationProfiles;
    private GeoFenceModel fenceModel;
    private GeoPointMarker selectedGeoPointMarker;
    private LatLng center;

    public Company getCompany() {
        if (company == null) {
            company = companyMgr.find(getCompanyIdFromSession());
        }
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public GeoPointMarker getSelectedGeoPointMarker() {
        return selectedGeoPointMarker;
    }

    public void setSelectedGeoPointMarker(GeoPointMarker selectedGeoPointMarker) {
        this.selectedGeoPointMarker = selectedGeoPointMarker;
    }

    public void setCenter(LatLng center) {
        this.center = center;
    }

    public String getCenter() {
        if (this.center == null) {
            /*
             * Center on this property or the center of the United States
             */
            this.center = new LatLng(37.09024, -95.71289100000001);
        }
        return "" + this.center.getLat() + "," + this.center.getLng();
    }

    public MapModel getFenceModel() {

        return fenceModel;
    }

    public List<Subscriber> getSubscriberList() {
        return subscriberList;
    }

    public void setSubscriberList(List<Subscriber> subscriberList) {
        this.subscriberList = subscriberList;
    }

    public List<LocationData> getLookups() {
        return lookups;
    }

    public void setLookups(List<LocationData> lookups) {
        this.lookups = lookups;
    }

    public List<MobileOfferSendLog> getMobileOffers() {
        return mobileOffers;
    }

    public void setMobileOffers(List<MobileOfferSendLog> mobileOffers) {
        this.mobileOffers = mobileOffers;
    }

    public List<SubscriberLocationProfile> getLocationProfiles() {
        return locationProfiles;
    }

    public void setLocationProfiles(List<SubscriberLocationProfile> locationProfiles) {
        this.locationProfiles = locationProfiles;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = DateUtil.getStartOfDay(startDate);
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = DateUtil.getEndOfDay(endDate);
    }

    public String getFormattedStartDate() {
        return DateUtil.formatDateForWeb(startDate);
    }

    public String getFormattedEndDate() {
        return DateUtil.formatDateForWeb(endDate);
    }

    public void createSubscriberProfile() {
        subscriberList = new ArrayList<Subscriber>();
        locationProfiles = new ArrayList<SubscriberLocationProfile>();
        Subscriber subscriber = subscriberMgr.findByMsisdn(msisdn);
        if (subscriber == null || subscriber.getMsisdn().isEmpty()) {
            JsfUtil.addErrorMessage("The subscriber for " + msisdn + " does not exist.");
            return;
        }
        Registrar registrar = new Registrar();
        String locaidStatus;
        try {
            locaidStatus = registrar.getSingleMsisdnStatus(msisdn);

        } catch (Exception e) {
            locaidStatus = null;
        }
        lookups = locationMgr.getLocationDataByBrandAndMsisdnInRange(getCompany().getBrand(), msisdn, startDate, endDate);
        for (LocationData locationData : lookups) {
            SubscriberLocationProfile subLocationProfile = new SubscriberLocationProfile();
            Long geoFenceId = locationData.getCurrentClosestGeoFenceId();
            if (geoFenceId != null) {
                DecimalFormat twoPlaces = new DecimalFormat("#.##");
                Double formattedLocation = Double.valueOf(twoPlaces.format(locationData.getDistanceAwayFromGeoFence()));
                subLocationProfile.setGeoFence(geoFenceMgr.find(geoFenceId));
                locationData.setDistanceAwayFromGeoFence(formattedLocation);
            } else {
                subLocationProfile.setGeoFence(null);
            }
            subLocationProfile.setLocationData(locationData);
            locationProfiles.add(subLocationProfile);
        }
        mobileOffers = sendLogMgr.getMobileOffersInRange(getCompany(), subscriber, startDate, endDate);
        subscriber.setLocaidStatus(locaidStatus);
        subscriberList.add(subscriber);
    }

    public void createSubscriberMap() {
        fenceModel = new GeoFenceModel();
        this.selectedGeoPointMarker = new GeoPointMarker();
        List<GeoFence> geoFences = new ArrayList<GeoFence>();
        for (SubscriberLocationProfile subscriberLocationProfile : locationProfiles) {

            LatLng subscriberLocation = new LatLng(subscriberLocationProfile.getLocationData().getLatitude(), subscriberLocationProfile.getLocationData().getLongitude());
            GeocodeResponse geocodeAddress = GeoUtil.GeocodeLocation(subscriberLocation);
            if (geocodeAddress != null) {
                List<Result> results = geocodeAddress.getResults();
                if (results != null && results.size() > 0) {
                    String markerAddress = results.get(0).getFormattedAddress();
                    if (markerAddress != null) {
                        Marker subscriberMarker = new Marker(subscriberLocation);
                        subscriberMarker.setTitle(msisdn);
                        subscriberMarker.setDraggable(false);
                        subscriberMarker.setClickable(true);
                        subscriberMarker.setIcon(ICON_RED);
                        this.selectedGeoPointMarker.setMarker(subscriberMarker);
                        this.selectedGeoPointMarker.setAddress(GeoPointMarker.getMarkerAddress(subscriberMarker));
                        fenceModel.addOverlay(subscriberMarker);
                    }
                }
            }
            GeoFence geoFence = subscriberLocationProfile.getGeoFence();
            if (geoFence != null) {
                if (geoFences.contains(geoFence)) {
                    continue;
                }
                if (geoFence.getType().equals(GeoFence.CIRCLE)) {
                    for (GeoPoint geoPoint : geoFence.getGeoPoints()) {
                        LatLng latLng = new LatLng(geoPoint.getLat(), geoPoint.getLng());
                        setCenter(latLng);
                        Marker marker = new Marker(latLng);
                        marker.setTitle(geoPoint.getGeoFence().getName());
                        marker.setDraggable(false);
                        marker.setClickable(true);
                        marker.setIcon(ICON_GREEN);
                        this.selectedGeoPointMarker.setMarker(marker);
                        this.selectedGeoPointMarker.setAddress(GeoPointMarker.getMarkerAddress(marker));
                        this.selectedGeoPointMarker.setRadius(geoPoint.getRadius());
                        fenceModel.addOverlay(marker);
                        Circle cir = new Circle(latLng, GeoUtil.MiTom(geoPoint.getRadius()));
                        cir.setFillColor(RED);
                        cir.setStrokeColor(WHITE);
                        cir.setFillOpacity(0.25);
                        fenceModel.addOverlay(cir);
                    }
                } else if (geoFence.getType().equals(GeoFence.RECTANGLE)) {
                    LatLng centerPoint = new LatLng(geoFence.getGeoPoints().get(0).getLat(), geoFence.getGeoPoints().get(0).getLng());
                    LatLng ne = new LatLng(geoFence.getGeoPoints().get(1).getLat(), geoFence.getGeoPoints().get(1).getLng());
                    LatLng sw = new LatLng(geoFence.getGeoPoints().get(2).getLat(), geoFence.getGeoPoints().get(2).getLng());
                    LatLngBounds bounds = GeoUtil.SmallestBounds(ne, sw);

                    setCenter(centerPoint);
                    Marker centerMarker = new Marker(centerPoint);
                    centerMarker.setTitle(geoFence.getGeoPoints().get(0).getGeoFence().getName());
                    centerMarker.setDraggable(false);
                    centerMarker.setClickable(true);
                    centerMarker.setIcon(ICON_YELLOW);
                    this.selectedGeoPointMarker.setMarker(centerMarker);
                    this.selectedGeoPointMarker.setAddress(GeoPointMarker.getMarkerAddress(centerMarker));
                    this.selectedGeoPointMarker.setRadius(geoFence.getGeoPoints().get(0).getRadius());
                    fenceModel.addOverlay(centerMarker);

                    Marker neMarker = new Marker(ne);

                    neMarker.setTitle(geoFence.getGeoPoints().get(1).getGeoFence().getName());
                    neMarker.setDraggable(false);
                    neMarker.setClickable(false);
                    neMarker.setIcon(ICON_GREEN);
                    this.selectedGeoPointMarker.setMarker(neMarker);
                    this.selectedGeoPointMarker.setAddress(GeoPointMarker.getMarkerAddress(neMarker));
                    this.selectedGeoPointMarker.setRadius(geoFence.getGeoPoints().get(1).getRadius());
                    fenceModel.addOverlay(neMarker);

                    Marker swMarker = new Marker(sw);
                    swMarker.setTitle(geoFence.getGeoPoints().get(2).getGeoFence().getName());
                    swMarker.setDraggable(false);
                    swMarker.setClickable(false);
                    swMarker.setIcon(ICON_RED);
                    this.selectedGeoPointMarker.setMarker(swMarker);
                    this.selectedGeoPointMarker.setAddress(GeoPointMarker.getMarkerAddress(swMarker));
                    this.selectedGeoPointMarker.setRadius(geoFence.getGeoPoints().get(2).getRadius());
                    fenceModel.addOverlay(swMarker);
                    Rectangle rect = new Rectangle(bounds);
                    rect.setFillColor(RED);
                    rect.setStrokeColor(WHITE);
                    rect.setFillOpacity(0.25);
                    fenceModel.addOverlay(rect);
                }
                geoFences.add(geoFence);
            }
        }
    }



    private void prepareVars() {
        msisdn = null;
        startDate = DateUtil.getFirstDayOfMonth(new Date());
        endDate = new Date();
        company = null;
        subscriberList = null;
        lookups = null;
        mobileOffers = null;
        locationProfiles = null;
    }

    public String prepareProfile() {
        prepareVars();
        return "/geo-reports/SubscriberProfile?faces-redirect=true";
    }
}
