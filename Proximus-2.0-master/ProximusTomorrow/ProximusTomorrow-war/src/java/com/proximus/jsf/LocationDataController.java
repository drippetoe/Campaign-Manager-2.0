/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf;

import com.proximus.data.sms.LocationData;
import com.proximus.data.sms.MobileOfferSettings;
import com.proximus.data.sms.SMSCampaign;
import com.proximus.data.sms.Subscriber;
import com.proximus.helper.util.JsfUtil;
import com.proximus.jsf.datamodel.LocationDataModel;
import com.proximus.jsf.datamodel.sms.GeoFenceModel;
import com.proximus.location.client.LocationFinder;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.sms.GeoPointManagerLocal;
import com.proximus.manager.sms.LocationDataManagerLocal;
import com.proximus.manager.sms.MobileOfferSettingsManagerLocal;
import com.proximus.manager.sms.SMSCampaignManagerLocal;
import com.proximus.manager.sms.SubscriberManagerLocal;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.apache.log4j.Logger;
import org.primefaces.event.map.StateChangeEvent;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

/**
 *
 * @author ronald
 */
@ManagedBean(name = "locationDataController")
@SessionScoped
public class LocationDataController extends AbstractController implements Serializable {

    private static final long serialVersionUID = 1;
    //GENERAL
    private LocationDataModel locationDataModel;
    private List<LocationData> filteredLocationData;
    private LocationData locationData;
    private String mobileNumber;
    private List<LocationData> previousLocations;
    private LatLng mapCenter;
    private int mapZoomLevel = 8;
    private MapModel mapModel;
    private static final String ICON_YELLOW_DOT = "http://maps.google.com/mapfiles/ms/micons/yellow-dot.png";
    private static final String ICON_GREEN_DOT = "http://maps.google.com/mapfiles/ms/micons/green-dot.png";
    @EJB
    LocationDataManagerLocal locationDataMgr;
    @EJB
    CompanyManagerLocal companyMgr;
    @EJB
    SMSCampaignManagerLocal smsCampaignMgr;
    @EJB
    SubscriberManagerLocal subscriberMgr;
    @EJB
    GeoPointManagerLocal geoPointMgr;
    @EJB
    MobileOfferSettingsManagerLocal mobileOfferSettingsMgr;
    private static final Logger logger = Logger.getLogger(LocationDataController.class.getName());

    public LocationDataModel getLocationDataModel() {
        if (locationDataModel == null) {
            populateLocationDataModel();
        }
        return locationDataModel;
    }

    public List<LocationData> getFilteredLocationData() {
        return filteredLocationData;
    }

    public void setFilteredLocationData(List<LocationData> filteredLocationData) {
        this.filteredLocationData = filteredLocationData;
    }

    private LatLng getLatLngForLocationData(LocationData locationData) {
        if (locationData != null) {
            return new LatLng(this.locationData.getLatitude(), this.locationData.getLongitude());
        } else {
            return null;
        }
    }

    public void setLocationDataModel(LocationDataModel locationDataModel) {
        this.locationDataModel = locationDataModel;
    }

    public LocationData getTrackerData() {
        return locationData;
    }

    public void setTrackerData(LocationData trackerData) {
        this.locationData = trackerData;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getMapCenter() {
        LatLng latLon = getLatLngForLocationData(locationData);
        return "" + latLon.getLat() + "," + latLon.getLng();
    }

    public void onStateChange(StateChangeEvent event) {

        this.mapZoomLevel = event.getZoomLevel();
        this.mapCenter = event.getCenter();
    }

    public void setMapCenter(LatLng mapCenter) {
        this.mapCenter = mapCenter;
    }

    public int getMapZoomLevel() {
        return mapZoomLevel;
    }

    public void setMapZoomLevel(int mapZoomLevel) {
        this.mapZoomLevel = mapZoomLevel;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    private void populateLocationDataModel() {
        List<LocationData> data = locationDataMgr.findByBrandAndStatus(this.getCompanyFromSession().getBrand(), LocationData.STATUS_FOUND);
        locationDataModel = new LocationDataModel(data);
        filteredLocationData = new ArrayList<LocationData>(data);
    }

    public String prepareList() {
        populateLocationDataModel();
        return "/location-data/List";
    }

    public String prepareTracker() {
        mobileNumber = null;
        locationData = null;
        return "/location-data/track";
    }

    public void clearTracker() {
        this.mobileNumber = null;
        this.locationData = null;
    }

    public LocationData getLocationData() {
        return locationData;
    }

    public void setLocationData(LocationData locationData) {
        this.locationData = locationData;
    }

    public MapModel getMapModel() {
        return mapModel;
    }

    public void setMapModel(MapModel mapModel) {
        this.mapModel = mapModel;
    }

    public void registerMobile() {
//        Registrar registrar = new Registrar();
//        
//        List<Subscriber> subscribers = new ArrayList<Subscriber>();
//        Subscriber sub = new Subscriber();
//        sub.setPhoneNumber(mobileNumber);
//        subscribers.add(sub);
//        
//        
//        Subscriber alreadyRegistered = subscriberMgr.findByPhoneNumber(mobileNumber);
//        
//        if ( alreadyRegistered != null && alreadyRegistered.getRegistrationDate() != null )
//        {
//            addAlertMessage(FacesMessage.SEVERITY_INFO, "Subscriber already registered");
//            return;
//        }
//        
//        subscribers = registrar.registerMobiles(subscribers);
//        
//        if ( subscribers.size() > 0 )
//        {
//            sub = subscribers.get(0);
//            locationData = new LocationData();
//            locationData.setStatus(sub.getStatus());
//            sub.setRegistrationDate(new Date());
//        }
//        
//        if ( alreadyRegistered != null )
//            subscriberMgr.update(sub);
//        else
//            subscriberMgr.create(sub);
//        
//        addAlertMessage(FacesMessage.SEVERITY_INFO, "Subscriber registered.  Opt-in to send messages");
    }

    public void optInMobile() {
//        Registrar registrar = new Registrar();
//        
//        List<Subscriber> subscribers = new ArrayList<Subscriber>();
//        Subscriber sub = subscriberMgr.findByPhoneNumber(mobileNumber);
//        if ( sub == null )
//        {
//            addAlertMessage(FacesMessage.SEVERITY_ERROR, "Subscriber could not be found.  Register first.");
//            return;
//        }
//        else if ( sub.getTrackingOptInDate() != null )
//        {
//            addAlertMessage(FacesMessage.SEVERITY_INFO, "Subscriber already opted in.");
//            return; 
//        }
//        
//        subscribers.add(sub);
//        
//        subscribers = registrar.autoOptin(subscribers);
//        
//        if ( subscribers.size() > 0 )
//        {
//            sub = subscribers.get(0);
//            locationData = new LocationData();
//            locationData.setStatus(sub.getStatus());
//            addAlertMessage(FacesMessage.SEVERITY_INFO, "Subscriber successfully opted in.");
//            
//            Date now = new Date();
//            sub.setSmsOptInDate(now);
//            sub.setTrackingOptInDate(now);
//            subscriberMgr.update(sub);
//            
//            return;
//        }
//        else
//        {
//            addAlertMessage(FacesMessage.SEVERITY_WARN, "Subscriber was not opted in.");
//        }
    }

    /*
     * this method is temporary to show that we can test the Loc-aid api with the given test data.
     * Once we are assigned a class-id this method will need to be expanded to show the true functionality of 
     * tracking a mobile number with Loc-aid.
     */
    public void trackMobile() {
        LocationFinder finder = new LocationFinder();
        SMSCampaign smsCampaign = smsCampaignMgr.getSMSCampaignById(companyMgr.find(getCompanyIdFromSession()), 1L);
        List<String> numbers = new ArrayList();
        List<LocationData> locationDataList = null;
        numbers.add(mobileNumber);
        double geoFenceX = 0;
        double geoFenceY = 0;

        Subscriber sub = subscriberMgr.findByMsisdn(mobileNumber);
        if (sub == null) {
            JsfUtil.addErrorMessage("Subscriber could not be found.  Register and opt-in first.");
            return;
        } else if (sub.getTrackingOptInDate() == null) {
            JsfUtil.addErrorMessage("Subscriber has not opted in.");
            return;
        }

        try {
            MobileOfferSettings settings = mobileOfferSettingsMgr.findByBrand(this.getBrandFromSession());
            finder.synchFind(numbers, geoFenceX, geoFenceY, locationDataMgr, subscriberMgr, this.getBrandFromSession(), settings, null);

            previousLocations = locationDataMgr.findAllByBrandAndMsisdn(this.getCompanyFromSession().getBrand(), mobileNumber);



            if (previousLocations != null && previousLocations.size() > 0) {
                locationData = previousLocations.get(0);
                locationData.setBrand(this.getCompanyFromSession().getBrand());
                locationDataMgr.create(locationData);
                previousLocations.add(locationData);

                this.setMapCenter(getLatLngForLocationData(locationData));
                populateLookupMap();

            } else {
                locationData = new LocationData();
                locationData.setStatus(LocationData.STATUS_NOT_FOUND);
            }
        } catch (Exception e) {
            locationData = new LocationData();
            locationData.setStatus(LocationData.STATUS_ERROR);
            logger.fatal(e);

        }
    }

    public List<LocationData> getPreviousLocations() {
        return previousLocations;
    }

    private void populateLookupMap() {
        mapModel = new GeoFenceModel();

        //Shared coordinates  
        for (LocationData locData : previousLocations) {
            LatLng latLon = getLatLngForLocationData(locData);
            mapModel.addOverlay(new Marker(latLon, ICON_YELLOW_DOT));
        }

        LatLng latLon = getLatLngForLocationData(locationData);
        mapModel.addOverlay(new Marker(latLon, ICON_GREEN_DOT));


    }
}
