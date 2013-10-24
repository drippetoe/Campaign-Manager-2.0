/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.rest.sms;

import com.proximus.data.Company;
import com.proximus.data.User;
import com.proximus.data.app.AppOffer;
import com.proximus.data.app.AppOfferResponse;
import com.proximus.data.app.AppUser;
import com.proximus.data.app.AppUserLocation;
import com.proximus.data.response.ClosestPropertyOffersResponse;
import com.proximus.data.sms.Category;
import com.proximus.data.sms.GeoFence;
import com.proximus.data.sms.GeoPoint;
import com.proximus.data.sms.MobileOfferSettings;
import com.proximus.data.sms.Property;
import com.proximus.data.sms.report.ViewActiveOffers;
import com.proximus.manager.UserManagerLocal;
import com.proximus.manager.app.AppUserLocationManagerLocal;
import com.proximus.manager.app.AppUserManagerLocal;
import com.proximus.manager.sms.CategoryManagerLocal;
import com.proximus.manager.sms.GeoFenceManagerLocal;
import com.proximus.manager.sms.GeoPointManagerLocal;
import com.proximus.manager.sms.MobileOfferSettingsManagerLocal;
import com.proximus.manager.sms.PropertyManagerLocal;
import com.proximus.manager.sms.report.ViewActiveOffersManagerLocal;
import com.proximus.util.RESTAuthenticationUtil;
import com.proximus.util.server.GeoFenceResponse;
import com.proximus.util.server.GeoUtil;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.log4j.Logger;
import org.primefaces.model.map.LatLng;

/**
 *
 * @author ronald
 */
@Path("/closestpropertyoffers")
@RequestScoped
public class ClosestPropertyOffersREST {

    static final double MINIMUM_DISTANCE = 2.0;
    static final double MAXIMUM_DISTANCE = Double.MAX_VALUE;
    static final Logger logger = Logger.getLogger(ClosestPropertyOffersREST.class.getName());
//    static final String BASE_URL = "http://localhost:8080/ProximusTomorrow-war/api/closestpropertyoffers/";
    static final String BASE_URL = "https://secure.proximusmobility.com/ProximusTomorrow-war/api/closestpropertyoffers/";
    @Context
    private UriInfo context;
    @Context
    Request request;
    @Context
    HttpServletResponse response;
    @EJB
    CategoryManagerLocal categoryMgr;
    @EJB
    PropertyManagerLocal propertyMgr;
    @EJB
    UserManagerLocal userMgr;
    @EJB
    MobileOfferSettingsManagerLocal settingsMgr;
    @EJB
    GeoPointManagerLocal geoPointMgr;
    @EJB
    GeoFenceManagerLocal geoFenceMgr;
    @EJB
    ViewActiveOffersManagerLocal offersMgr;
    @EJB
    AppUserManagerLocal appUserMgr;
    @EJB
    AppUserLocationManagerLocal appLocationMgr;

    public ClosestPropertyOffersREST() {
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response post(MultivaluedMap<String, String> formParams) {
        String latitude = formParams.getFirst("latitude");
        String longitude = formParams.getFirst("longitude");
        String zipcode = formParams.getFirst("zipcode");
        String distance = formParams.getFirst("maxDistance");
        String userName = formParams.getFirst("username");
        String requestToken = formParams.getFirst("token");
        String uniqueIdentifier = formParams.getFirst("uuid");
        String macAddress = formParams.getFirst("mac");
        String msisdn = formParams.getFirst("msisdn");
        String categories = formParams.getFirst("category");
        String locale = formParams.getFirst("locale");
        String mobileCountryCode = formParams.getFirst("mcc");
        String mobileNetworkCode = formParams.getFirst("mnc");
        String userAgent = formParams.getFirst("userAgent");
        String devicePlatform = formParams.getFirst("devicePlatform");
        String deviceVersion = formParams.getFirst("deviceVersion");

        Company company;
        String password;
        String requestURL;
        String companySalt;
        String decodedUserName;

        //These are very important
        logger.info("latitude: " + latitude);
        logger.info("longitude: " + longitude);
        logger.info("zipcode: " + zipcode);
        logger.info("user: " + userName);


        logger.debug("maxDistance: " + distance);
        logger.debug("unique identifier: " + uniqueIdentifier);
        logger.debug("macAddress: " + macAddress);
        logger.debug("msisdn: " + msisdn);
        logger.debug("categories: " + categories);
        logger.debug("locale: " + locale);
        logger.debug("mcc: " + mobileCountryCode);
        logger.debug("mnc: " + mobileNetworkCode);
        logger.debug("user agent: " + userAgent);
        logger.debug("platform: " + devicePlatform);
        logger.debug("version: " + deviceVersion);



        ClosestPropertyOffersResponse defaultResponse = new ClosestPropertyOffersResponse();
        if (userName != null && requestToken != null) {
            try {
                decodedUserName = URLDecoder.decode(userName, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                logger.fatal("Decoding Exception: " + ex);
                defaultResponse.setStatus(Response.Status.BAD_REQUEST.getStatusCode() + "");
                defaultResponse.setStatus_message(ClosestPropertyOffersResponse.MISSING_PARAMETERS);
                return Response.status(Response.Status.BAD_REQUEST).entity(defaultResponse).build();
            }
        } else {
            logger.warn("User name and request token can't be empty.");
            defaultResponse.setStatus(Response.Status.BAD_REQUEST.getStatusCode() + "");
            defaultResponse.setStatus_message(ClosestPropertyOffersResponse.MISSING_PARAMETERS);
            return Response.status(Response.Status.BAD_REQUEST).entity(defaultResponse).build();
        }
        User user = userMgr.getUserByUsername(decodedUserName);
        if (user == null) {
            logger.warn("User: " + userName + " not found.");
            defaultResponse.setStatus(Response.Status.BAD_REQUEST.getStatusCode() + "");
            defaultResponse.setStatus_message(ClosestPropertyOffersResponse.INVALID_CREDENTIALS);
            return Response.status(Response.Status.BAD_REQUEST).entity(defaultResponse).build();
        }
        if ((latitude == null || longitude == null) && zipcode == null || uniqueIdentifier == null) {
            defaultResponse.setStatus(Response.Status.BAD_REQUEST.getStatusCode() + "");
            defaultResponse.setStatus_message(ClosestPropertyOffersResponse.MISSING_PARAMETERS);
            return Response.status(Response.Status.BAD_REQUEST).entity(defaultResponse).build();
        }

        if (msisdn != null) {
            msisdn = msisdn.replaceAll("\\D", "");
            if ((msisdn != null) && (msisdn.length() == 10)) {
                // assume US country code if not specified
                msisdn = "1" + msisdn;
            }
        }

        if (macAddress != null) {
            macAddress = macAddress.replaceAll("\\W", "");
            macAddress = macAddress.toUpperCase();
        }
        /*
         * TODO: Get the company in a better way than this.
         */
        company = user.getCompanies().get(0);
        password = user.getApiPassword();
        requestURL = BASE_URL + userName + "/" + password;
        companySalt = company.getSalt();

        logger.warn("url to encode: " + requestURL);
        logger.warn("campany SALT: " + companySalt);
        logger.debug("request token: " + requestToken);


        return closestPropertyOffersResponse(userName, requestToken, password,
                requestURL, companySalt, company, latitude, longitude, distance,
                categories, msisdn, uniqueIdentifier, macAddress, locale, zipcode,
                mobileCountryCode, mobileNetworkCode, userAgent, devicePlatform, deviceVersion);

    }

    public Response closestPropertyOffersResponse(String userName, String requestToken,
            String password, String requestURL, String companySalt,
            Company company, String latitude, String longitude,
            String maxDistance, String categories, String msisdn, String uniqueIdentifier, String macAddress,
            String locale, String zipcode, String mobileCountryCode, String mobileNetworkCode, String userAgent, 
            String devicePlatform, String deviceVersion) {
        try {
            logger.info("Closest Properties Response CALLED");

            AppOfferResponse offerResponse = new AppOfferResponse();
            //authenticate
            if (RESTAuthenticationUtil.apiAuthorized(userName, password, requestToken, requestURL, companySalt)) {
                String decodedCategories;
                List<Category> realCategories = new ArrayList<Category>();

                try {
                    decodedCategories = URLDecoder.decode(categories, "UTF-8");
                } catch (Exception ex) {
                    logger.info("Error decoding categories" + categories);
                    decodedCategories = null;
                }
                if (decodedCategories != null) {
                    decodedCategories = decodedCategories.toLowerCase();

                    try {
                        List<String> categoryList = Arrays.asList(decodedCategories.split("\\s*,\\s*"));
                        for (String name : categoryList) {
                            Category category = categoryMgr.getCategoryByBrandAndWebSafeName(company.getBrand(), name);
                            if (category != null) {
                                realCategories.add(category);
                            }
                        }
                    } catch (Exception ex) {
                        logger.error("Could not parse categories: " + categories);
                        logger.error(ex.getMessage());
                        realCategories = null;
                    }

                }
                Double realLatitude, realLongitude;
                LatLng location = getLocation(latitude, longitude, zipcode);
                if (location != null) {
                    realLatitude = location.getLat();
                    realLongitude = location.getLng();
                } else {
                    realLatitude = null;
                    realLongitude = null;
                }

                Date eventDate = new Date();
                AppUser appUser = appUserMgr.getByMsisdnOrIdentifierOrMac(msisdn, uniqueIdentifier, macAddress);
                AppUserLocation appUserLocation = appLocationMgr.findAppUserLocation(realLatitude, realLongitude);
                MobileOfferSettings mobileOfferSettings = settingsMgr.findByBrand(company.getBrand());
                List<GeoFence> allGeoFences = geoFenceMgr.getGeoFencesByBrand(company.getBrand());
                List<GeoFence> closestGeoFenceList;
                double geoFenceDistance;
                List<GeoPoint> locationList = new LinkedList<GeoPoint>();
                List<ViewActiveOffers> propertyOffers = new LinkedList<ViewActiveOffers>();
                List<Double> distanceList = new LinkedList<Double>();


                if (maxDistance == null || maxDistance.isEmpty()) {
                    geoFenceDistance = MINIMUM_DISTANCE;
                } else {
                    try {
                        geoFenceDistance = Double.parseDouble(maxDistance);
                    } catch (Exception ex) {
                        geoFenceDistance = MINIMUM_DISTANCE;
                    }
                }
                if (geoFenceDistance < MINIMUM_DISTANCE) {
                    geoFenceDistance = MAXIMUM_DISTANCE;
                }

                closestGeoFenceList = getGeoFencesInRangeWithOffers(geoFenceDistance,
                        latitude, longitude, mobileOfferSettings, allGeoFences, zipcode);

                if (!closestGeoFenceList.isEmpty()) {
                    List<ViewActiveOffers> currentPropertyOffers;
                    for (GeoFence geoFence : closestGeoFenceList) {
                        Property geoFenceProperty = geoFence.getProperty();
                        GeoPoint propertyGeoPoint = geoPointMgr.findGeoPointByGeoFenceAndProperty(geoFence, geoFenceProperty);
                        currentPropertyOffers = offersMgr.getByPropertyAndLocaleAndCategoryList(geoFenceProperty, locale, realCategories);
                        if (currentPropertyOffers != null && !currentPropertyOffers.isEmpty()) {
                            propertyOffers.addAll(currentPropertyOffers);
                            for (int i = 0; i < currentPropertyOffers.size(); i++) {
                                locationList.add(propertyGeoPoint);
                                distanceList.add(geoFence.getDistanceAway());
                            }
                        }
                    }
                }

                //These need to be the same size
                logger.info("property offers size: " + propertyOffers.size());
                logger.info("location list size: " + locationList.size());
                logger.info("distance list size: " + distanceList.size());

                List<AppOffer> appOffers = new LinkedList<AppOffer>();
                for (int i = 0; i < propertyOffers.size(); i++) {
                    AppOffer offer = new AppOffer();
                    try {
                        Property p = propertyOffers.get(i).getProperty();
                        offer.setOfferId(propertyOffers.get(i).getId());
                        offer.setWebOfferId(propertyOffers.get(i).getWebOffer().getId());
                        offer.setPropertyWebHash(propertyOffers.get(i).getWebHash());
                        String cleanText = propertyOffers.get(i).getCleanOfferText().replaceAll(Property.ADDRESS_REGEX, p.getAddress());
                        cleanText = cleanText.replaceAll(Property.PROPERTY_REGEX, p.getName());
                        offer.setCleanOfferText(cleanText);
                        offer.setDistance(new BigDecimal(distanceList.get(i)).setScale(2, RoundingMode.HALF_UP));
                        offer.setRetailerName(propertyOffers.get(i).getRetailer().getName());
                        offer.setOfferImage(propertyOffers.get(i).getWebOffer().getOfferImage());
                        offer.setAddress(propertyOffers.get(i).getProperty().getAddress());
                        offer.setCity(propertyOffers.get(i).getProperty().getCity());
                        offer.setStateProvince(propertyOffers.get(i).getProperty().getStateProvince());
                        offer.setZipcode(propertyOffers.get(i).getProperty().getZipcode());
                        offer.setExpirationDate(propertyOffers.get(i).getEndDate());
                        offer.setLatitude(locationList.get(i).getLat());
                        offer.setLongitude(locationList.get(i).getLng());
                        offer.setPassbookBarcode(propertyOffers.get(i).getWebOffer().getPassbookBarcode());
                        offer.setCategories(propertyOffers.get(i).getWebOffer().getCategories());

                        appOffers.add(offer);
                    } catch (Exception e) {
                        continue;
                    }
                }
                offerResponse.setAppOffers(appOffers);
                offerResponse.setStatus(Response.Status.OK.getStatusCode() + "");
                offerResponse.setStatus_message(ClosestPropertyOffersResponse.SUCCESSFUL);
                logger.info(offerResponse.getStatus() + " : " + offerResponse.getStatus_message());
                if (appUser == null) {
                    appUser = new AppUser(uniqueIdentifier, msisdn, macAddress, mobileCountryCode, mobileNetworkCode, 
                            userAgent, devicePlatform, deviceVersion, realCategories);
                    appUserMgr.create(appUser);
                } else {
                    for (Category category : realCategories) {
                        if (!appUser.getCategories().contains(category)) {
                            appUser.addCategory(category);
                            appUserMgr.update(appUser);
                        }
                    }
                }

                if (appUserLocation == null) {
                    appUserLocation = new AppUserLocation(realLongitude, realLatitude, eventDate, appUser);
                    appLocationMgr.create(appUserLocation);
                } else {
                    appUserLocation.setEventDate(eventDate);
                    appLocationMgr.update(appUserLocation);
                }

                return Response.status(Response.Status.OK).header("Access-Control-Allow-Origin", "*").entity(offerResponse).build();
            } else {
                offerResponse.setStatus(Response.Status.UNAUTHORIZED.getStatusCode() + "");
                offerResponse.setStatus_message(ClosestPropertyOffersResponse.INVALID_CREDENTIALS);
                logger.info(offerResponse.getStatus() + " : " + offerResponse.getStatus_message());
                return Response.status(Response.Status.UNAUTHORIZED).entity(offerResponse).build();
            }
        } catch (Exception ex) {
            logger.fatal("Closest Offers Response fatal exception: " + ex);
            ClosestPropertyOffersResponse errorResponse = new ClosestPropertyOffersResponse();
            errorResponse.setStatus(Response.Status.SERVICE_UNAVAILABLE.getStatusCode() + "");
            errorResponse.setStatus_message(ClosestPropertyOffersResponse.SERVICE_UNAVAILABLE);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(errorResponse).build();
        }
    }

    private List<GeoFence> getGeoFencesInRangeWithOffers(
            double geoFenceDistance, String latitude, String longitude,
            MobileOfferSettings mobileOfferSettings, List<GeoFence> geoFenceList, String zipcode) {
        List<GeoFence> closestGeoFenceList = new ArrayList<GeoFence>();
        LatLng location = getLocation(latitude, longitude, zipcode);

        if (location == null) {
            return closestGeoFenceList;
        }

        for (int i = 0; i < geoFenceList.size(); i++) {

            GeoFenceResponse geoResponse = GeoUtil.CalculateClosestGeoFence(mobileOfferSettings, location, geoFenceList);
            if (geoResponse.getDistance() <= geoFenceDistance) {
                GeoFence closestGeoFence = geoResponse.getGeoFence();
                List<ViewActiveOffers> propertyOffers = offersMgr.getByProperty(closestGeoFence.getProperty());
                if (propertyOffers != null && !propertyOffers.isEmpty()) {
                    closestGeoFence.setDistanceAway(geoResponse.getDistance());
                    geoFenceList.remove(closestGeoFence);
                    closestGeoFenceList.add(closestGeoFence);
                } else {
                    geoFenceList.remove(closestGeoFence);
                    i--;
                }
            } else {
                return closestGeoFenceList;
            }
        }
        return closestGeoFenceList;
    }

    private LatLng getLocation(String latitude, String longitude, String zipcode) {
        LatLng location;
        try {
            location = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        } catch (Exception e) {
            location = GeoUtil.getLocationFromZip(zipcode);
        }
        return location;
    }
}
