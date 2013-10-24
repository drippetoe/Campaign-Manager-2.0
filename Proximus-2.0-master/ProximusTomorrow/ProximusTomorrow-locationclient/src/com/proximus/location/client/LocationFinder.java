/*
 * Copyright 2010-2012, Proximus Mobility, LLC.
 * 
 */
package com.proximus.location.client;

import com.proximus.data.Brand;
import com.proximus.data.sms.GeoFence;
import com.proximus.data.sms.LocationData;
import com.proximus.data.sms.MobileOfferSettings;
import com.proximus.data.sms.Subscriber;
import com.proximus.data.util.DateUtil;
import com.proximus.manager.sms.LocationDataManagerLocal;
import com.proximus.manager.sms.SubscriberManagerLocal;
import com.proximus.util.server.GeoFenceResponse;
import com.proximus.util.server.GeoUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.ws.WebServiceRef;
import net.locaid.portico.webservice.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.primefaces.model.map.LatLng;

/**
 * this class locates a subscriber and calculates the distance from a given
 * geo-point
 *
 * @author Angela Mercer
 */
public class LocationFinder implements LocationFinderLocal {

    @WebServiceRef(wsdlLocation = "META-INF/wsdl/LatitudeLongitudeServices?wsdl")
    private static LatitudeLongitudeServices_Service service;
    private static LatitudeLongitudeServices locationPort;
    private static final String STRINGTODATE_FMT = "yyyyMMddHHmmss";
    private static final Logger logger = Logger.getLogger(LocationFinder.class.getName());

    public static LatitudeLongitudeServices getLocationPort() {
        if (locationPort == null) {
            service = new LatitudeLongitudeServices_Service();
            locationPort = service.getLatitudeLongitudeServicesPort();
        }

        return locationPort;
    }

    private static LocationAnswerResponseBean getLocationsX(String login, String password, String classId,
            List<String> msisdnList, String coorType, String locationMethod, String syncType, Integer overage,
            Integer delayTime) {

        return getLocationPort().getLocationsX(login, password, classId, msisdnList, coorType,
                locationMethod, syncType, overage, delayTime);
    }

    /**
     * synchronous call to locate a list of subscribers by phone number
     *
     * @param numbers
     * @param geoFenceX
     * @param geoFenceY
     * @return
     */
    @Override
    public void synchFind(List<String> numbers, double geoFenceX, double geoFenceY, LocationDataManagerLocal locationMgr, SubscriberManagerLocal subscriberMgr, Brand brand, MobileOfferSettings mobileOfferSettings, List<GeoFence> geofences) {
        logger.info("LocationFinder synchFind() method numbers list size = " + numbers.size());
        LocationAnswerResponseBean response = null;
        int counter = 0;
        List<String> subNumbers = new ArrayList<String>();

        for (String num : numbers) {
            subNumbers.add(num);
            counter++;
            if (counter % 10 == 0) {
                response = getLocationsX(LocationRequest.LOGIN, LocationRequest.PASSWORD, LocationRequest.PROXIMUS_PROD_CLASS_ID,
                        subNumbers, LocationRequest.DECIMAL_COOR_TYPE, LocationRequest.CELL_LOCATION_METHOD,
                        LocationRequest.SYNCH, LocationRequest.OVERAGE, LocationRequest.DELAY_TIME_500);

                saveToDatabase(buildLocationData(response, geoFenceX, geoFenceY), subscriberMgr, locationMgr, brand, mobileOfferSettings, geofences);

                subNumbers = new ArrayList<String>();
            }
        }
        if (!subNumbers.isEmpty()) {
            response = getLocationsX(LocationRequest.LOGIN, LocationRequest.PASSWORD, LocationRequest.PROXIMUS_PROD_CLASS_ID,
                    subNumbers, LocationRequest.DECIMAL_COOR_TYPE, LocationRequest.CELL_LOCATION_METHOD,
                    LocationRequest.SYNCH, LocationRequest.OVERAGE, LocationRequest.DELAY_TIME_500);
            saveToDatabase(buildLocationData(response, geoFenceX, geoFenceY), subscriberMgr, locationMgr, brand, mobileOfferSettings, geofences);
        }

    }

    /**
     * Making saves every 10 responses so we don't hit a bottleneck
     *
     * @param results
     * @param subscriberMgr
     * @param locMgr
     * @param brand
     * @param mobileOfferSettings
     */
    private void saveToDatabase(List<LocationData> results, SubscriberManagerLocal subscriberMgr, LocationDataManagerLocal locMgr, Brand brand, MobileOfferSettings mobileOfferSettings, List<GeoFence> geofences) {
        for (LocationData locationData : results) {
            if (locationData.getMsisdn() != null) {
                Subscriber subscriber = subscriberMgr.findByMsisdn(locationData.getMsisdn());
                if (subscriber == null) {
                    //This is legacy location data 
                    logger.error("We don't know why number exists in location Data and not in Subscribers | " + locationData.getMsisdn());
                } else {
                    locationData.setBrand(brand);
                    //Call util to set up closest geofence
                    GeoFenceResponse response = null;
                    if ((geofences != null) && (locationData.getStatus().equals(LocationData.STATUS_FOUND))) {
                        response = GeoUtil.CalculateClosestGeoFence(mobileOfferSettings, new LatLng(locationData.getLatitude(), locationData.getLongitude()), geofences);
                        locationData.setCurrentClosestGeoFenceId(response.getGeoFence().getId());
                        locationData.setDistanceAwayFromGeoFence(response.getDistance());

                        //Update Subscriber's Next Lookup if next_lookup_request is null or the newest locationData eventDate() is more recent that the subcriber's next_ping_request
                        //To avoid doing more than one location by the min_time_between_lookups
                        subscriber.setDistanceAwayFromGeoFence(response.getDistance());
                        subscriber.setCurrentClosestGeoFenceId(response.getGeoFence().getId());

                        if (subscriber.getOptInProperty() == null) {
                            subscriber.setOptInProperty(response.getGeoFence().getProperty());
                        }

                        if (subscriber.getNextLookupRequest() == null || locationData.getEventDate().getTime() >= subscriber.getNextLookupRequest().getTime()) {
                            subscriber.setNextLookupRequest(new Date(System.currentTimeMillis() + mobileOfferSettings.getMinLookupWaitTime()));
                        }

                        // we update the subscriber if status was found
                        logger.info("Located subscriber " + subscriber.getCarrier() + " " + subscriber.getMsisdn() + " won't lookup until " + DateUtil.formatTimestampForWeb(subscriber.getNextLookupRequest()));
                        subscriberMgr.update(subscriber);

                    }
                    // save location data, found or not, so we have a record
                    locMgr.create(locationData);  
                }
            } else {
                logger.fatal("location data with either NULL MSISDN or no LAT LNG points returned");
            }
        }
    }

    /**
     * builds the location data for persistance
     *
     * @param response
     * @param longitude
     * @param latitude
     * @return
     */
    private List<LocationData> buildLocationData(LocationAnswerResponseBean response, double longitude, double latitude) {
        List<LocationData> locationData = new ArrayList();
        List<LocationResponseBean> responseList = response.getLocationResponse();
        List<MsisdnErrorResponseBean> errorList = response.getMsisdnError();
        logger.info("response List size = " + responseList.size());
        Date eventDate = null;

        for (LocationResponseBean r : responseList) {
            LocationData data = new LocationData();
            data.setMsisdn(r.getNumber());
            data.setStatus(r.getStatus());
            if (r.getStatus().equalsIgnoreCase(LocationData.STATUS_NOT_FOUND)) {
                data.setEventDate(new Date());
            } else {
                CoordinateGeo coorGeo = r.getCoordinateGeo();
                data.setLongitude(Double.parseDouble(coorGeo.getX())); //longitude
                data.setLatitude(Double.parseDouble(coorGeo.getY())); //latitude
                data.setEventDate(new Date());
            }
            locationData.add(data);
        }

        for (MsisdnErrorResponseBean r : errorList) {
            LocationData data = new LocationData();
            data.setMsisdn(r.getMsisdn());
            data.setStatus(r.getErrorMessage());
            data.setEventDate(new Date());
            locationData.add(data);
        }

        return locationData;

    }

    @Override
    public double distanceCalc(double latitudeOne, double longitudeOne, double latitudeTwo, double longitudeTwo) {
//        in miles
        double earthRadius = 3958.75;
//        in kilometers
//        double earthRadius = 6371;
        double lat = Math.toRadians(latitudeTwo - latitudeOne);
        double lon = Math.toRadians(longitudeTwo - longitudeOne);
        double sinLat = Math.sin(lat / 2);
        double sinLon = Math.sin(lon / 2);
        double a = Math.pow(sinLat, 2) + Math.pow(sinLon, 2) * Math.cos(latitudeOne) * Math.cos(latitudeTwo);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;

        return dist;
    }

    public static Date formatStringToDate(String dateString) {
        try {
            return new SimpleDateFormat(STRINGTODATE_FMT).parse(dateString);
        } catch (ParseException ex) {
            return null;
        }
    }

    public static void main(String[] args) {
        BasicConfigurator.configure();

        LocationFinder l = new LocationFinder();
        List<String> numbers = new ArrayList<String>();
        numbers.add("17703314550");

        LocationAnswerResponseBean response = getLocationsX(LocationRequest.LOGIN, LocationRequest.PASSWORD, LocationRequest.PROXIMUS_PROD_CLASS_ID,
                        numbers, LocationRequest.DECIMAL_COOR_TYPE, LocationRequest.CELL_LOCATION_METHOD,
                        LocationRequest.SYNCH, LocationRequest.OVERAGE, LocationRequest.DELAY_TIME_500);

        List<LocationResponseBean> responseList = response.getLocationResponse();
        List<MsisdnErrorResponseBean> errorList = response.getMsisdnError();

        logger.info("Overall Status: " + response.getStatus());

        for (LocationResponseBean r : responseList) {

            logger.info("Number: " + r.getNumber());
            logger.info("Status: " + r.getStatus());
            if (!r.getStatus().equalsIgnoreCase(LocationData.STATUS_NOT_FOUND)) {
                CoordinateGeo coorGeo = r.getCoordinateGeo();
                logger.info("Longitude: " + coorGeo.getX());
                logger.info("Latitude: " + coorGeo.getY());
            }
        }

        for (MsisdnErrorResponseBean r : errorList) {
            logger.error("Number: " + r.getMsisdn());
            logger.error("Code: " + r.getErrorCode());
            logger.error("Message: " + r.getErrorMessage());
        }
    }
}
