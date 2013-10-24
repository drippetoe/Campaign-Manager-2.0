package com.proximus.util.server;

import com.proximus.data.sms.GeoFence;
import com.proximus.data.sms.GeoPoint;
import com.proximus.data.sms.MobileOfferSettings;
import com.proximus.data.xml.googleapi.GeocodeResponse;
import com.proximus.data.xml.googleapi.Geometry;
import com.proximus.data.xml.googleapi.Result;
import com.proximus.util.JAXBUtil;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.LatLngBounds;
import org.xml.sax.SAXException;

/**
 *
 * @author eric
 */
public class GeoUtil {

    private static final Logger logger = Logger.getLogger(GeoUtil.class);
    private static final String GEOCODER_REQUEST_PREFIX_FOR_XML = "http://maps.googleapis.com/maps/api/geocode/xml";
    private static final String GEOCODER_REQUEST_PREFIX_FOR_JSON = "http://maps.googleapis.com/maps/api/geocode/json";

    //public static final Double MIN_UPPERBOUND_MILES = 10.0;
    //public static final Double MAX_UPPERBOUND_MILES = 300.0;
    public static double mToMi(double m) {
        return (m * 0.00062137119);
    }

    public static double MiTom(double mi) {
        return (mi / 0.00062137119);
    }

    /**
     *
     * @param pos
     * @param d distance in kilometers
     * @param bearing in degrees North = 0.0 South = 180.0
     * @return
     */
    public static LatLng Destination(LatLng pos, double d, double bearing) {
        double R = 6371.0;  //KM  
        double dist = d / R;
        double brng = Math.toRadians(bearing);
        double lat1 = Math.toRadians(pos.getLat());
        double lon1 = Math.toRadians(pos.getLng());
        double lat2 = Math.asin(Math.sin(lat1) * Math.cos(dist) + Math.cos(lat1) * Math.sin(dist) * Math.cos(brng));
        double a = Math.atan2(Math.sin(brng) * Math.sin(dist) * Math.cos(lat1), Math.cos(dist) - Math.sin(lat1) * Math.sin(lat2));
        double lon2 = lon1 + a;
        lon2 = (lon2 + 3 * Math.PI) % (2 * Math.PI) - Math.PI;
        return new LatLng(Math.toDegrees(lat2), Math.toDegrees(lon2));
    }

    /**
     * Lookup Lat & Lng from google map api. Found it on the interwebz. Replaced
     * XPath with JAX-B
     *
     * @param actionEvent
     * @throws MalformedURLException
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XPathExpressionException
     */
    public static GeocodeResponse GeocodeAddress(String address) {

        if (address == null || address.isEmpty()) {
            return null;
        }
        address = Normalizer.normalize(address, Normalizer.Form.NFD);
        address = address.replaceAll("[^\\p{ASCII}]", "");
        URL url;
        try {
            url = new URL(GEOCODER_REQUEST_PREFIX_FOR_XML + "?address="
                    + URLEncoder.encode(address, "UTF-8") + "&sensor=false");

        } catch (UnsupportedEncodingException ex) {
            System.err.println(ex);
            return null;
        } catch (MalformedURLException ex) {
            System.err.println(ex);
            return null;
        }

        // prepare an HTTP connection to the geocoder
        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException ex) {
            System.err.println(ex);
            return null;
        }

        String xml = "";
        try {

            conn.connect();
            StringWriter writer = new StringWriter();
            IOUtils.copy(conn.getInputStream(), writer, "UTF-8");
            xml = writer.toString();
        } catch (IOException ex) {
            System.err.println(ex);
            return null;
        } finally {
            conn.disconnect();
        }
        GeocodeResponse response = null;
        try {
            response = JAXBUtil.fromXml(GeocodeResponse.class, xml);
        } catch (Exception ex) {
            System.err.println(ex);
            return null;
        }
        return response;
    }

    public static GeocodeResponse GeocodeLocation(LatLng latLng) {

        URL url;
        try {
            url = new URL(GEOCODER_REQUEST_PREFIX_FOR_XML + "?latlng="
                    + URLEncoder.encode(latLng.getLat() + "," + latLng.getLng(), "UTF-8") + "&sensor=false");

        } catch (UnsupportedEncodingException ex) {
            System.err.println(ex);
            return null;
        } catch (MalformedURLException ex) {
            System.err.println(ex);
            return null;
        }

        // prepare an HTTP connection to the geocoder
        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException ex) {
            System.err.println(ex);
            return null;
        }

        String xml = "";
        try {

            conn.connect();
            StringWriter writer = new StringWriter();
            IOUtils.copy(conn.getInputStream(), writer, "UTF-8");
            xml = writer.toString();
        } catch (IOException ex) {
            System.err.println(ex);
            return null;
        } finally {
            conn.disconnect();
        }
        GeocodeResponse response = null;
        try {
            response = JAXBUtil.fromXml(GeocodeResponse.class, xml);
        } catch (Exception ex) {
            System.err.println(ex);
            return null;
        }
        return response;
    }

    public static double DistanceMi(double latitudeOne, double longitudeOne, double latitudeTwo, double longitudeTwo) {
        double earthRadius = 3958.75;
        double lat = Math.toRadians(latitudeTwo - latitudeOne);
        double lon = Math.toRadians(longitudeTwo - longitudeOne);
        double sinLat = Math.sin(lat / 2);
        double sinLon = Math.sin(lon / 2);
        double a = Math.pow(sinLat, 2) + Math.pow(sinLon, 2) * Math.cos(latitudeOne) * Math.cos(latitudeTwo);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;

        return dist;
    }

    public static double DistanceKM(double latitudeOne, double longitudeOne, double latitudeTwo, double longitudeTwo) {
        double earthRadius = 6371;
        double lat = Math.toRadians(latitudeTwo - latitudeOne);
        double lon = Math.toRadians(longitudeTwo - longitudeOne);
        double sinLat = Math.sin(lat / 2);
        double sinLon = Math.sin(lon / 2);
        double a = Math.pow(sinLat, 2) + Math.pow(sinLon, 2) * Math.cos(latitudeOne) * Math.cos(latitudeTwo);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;

        return dist;
    }

    public static double DistanceMiLatLng(LatLng latLng1, LatLng latLng2) {
        return DistanceMi(latLng1.getLat(), latLng1.getLng(), latLng2.getLat(), latLng2.getLng());
    }

    public static double DistanceKMLatLng(LatLng latLng1, LatLng latLng2) {
        return DistanceKM(latLng1.getLat(), latLng1.getLng(), latLng2.getLat(), latLng2.getLng());
    }

    /*
     * Used to calculate the smallest rectangel
     */
    public static LatLngBounds SmallestBounds(LatLng latLng1, LatLng latLng2) {
        double lat1 = latLng1.getLat();
        double lng1 = latLng1.getLng();
        double lat2 = latLng2.getLat();
        double lng2 = latLng2.getLng();
        LatLng northEast;
        LatLng southWest;
        if (lat1 > lat2) {
            if (lng1 < lng2) {
                northEast = new LatLng(lat1, lng1);
                southWest = new LatLng(lat2, lng2);
            } else {
                northEast = new LatLng(lat1, lng2);
                southWest = new LatLng(lat2, lng1);
            }
        } else {
            if (lng1 < lng2) {
                northEast = new LatLng(lat2, lng1);
                southWest = new LatLng(lat1, lng2);
            } else {
                northEast = new LatLng(lat2, lng2);
                southWest = new LatLng(lat1, lng1);
            }
        }
        return new LatLngBounds(northEast, southWest);
    }

    public static double BoundsToBiggestRadiusMi(LatLngBounds bounds) {

        double d1 = GeoUtil.DistanceMi(bounds.getNorthEast().getLat(), bounds.getNorthEast().getLng(), bounds.getNorthEast().getLat(), bounds.getSouthWest().getLng());
        double d2 = GeoUtil.DistanceMi(bounds.getNorthEast().getLat(), bounds.getNorthEast().getLng(), bounds.getSouthWest().getLat(), bounds.getNorthEast().getLng());
        if (d1 >= d2) {
            return d1;
        } else {
            return d2;
        }
    }

    public static double BoundsToSmallestRadiusMi(LatLngBounds bounds) {

        double d1 = GeoUtil.DistanceMi(bounds.getNorthEast().getLat(), bounds.getNorthEast().getLng(), bounds.getNorthEast().getLat(), bounds.getSouthWest().getLng());
        double d2 = GeoUtil.DistanceMi(bounds.getNorthEast().getLat(), bounds.getNorthEast().getLng(), bounds.getSouthWest().getLat(), bounds.getNorthEast().getLng());
        if (d1 <= d2) {
            return d1;
        } else {
            return d2;
        }
    }

    public static double BoundsToBiggestRadiusKM(LatLngBounds bounds) {

        double d1 = GeoUtil.DistanceKM(bounds.getNorthEast().getLat(), bounds.getNorthEast().getLng(), bounds.getNorthEast().getLat(), bounds.getSouthWest().getLng());
        double d2 = GeoUtil.DistanceKM(bounds.getNorthEast().getLat(), bounds.getNorthEast().getLng(), bounds.getSouthWest().getLat(), bounds.getNorthEast().getLng());
        if (d1 >= d2) {
            return d1;
        } else {
            return d2;
        }
    }

    public static double BoundsToSmallestRadiusKM(LatLngBounds bounds) {

        double d1 = GeoUtil.DistanceKM(bounds.getNorthEast().getLat(), bounds.getNorthEast().getLng(), bounds.getNorthEast().getLat(), bounds.getSouthWest().getLng());
        double d2 = GeoUtil.DistanceKM(bounds.getNorthEast().getLat(), bounds.getNorthEast().getLng(), bounds.getSouthWest().getLat(), bounds.getNorthEast().getLng());
        if (d1 <= d2) {
            return d1;
        } else {
            return d2;
        }
    }

    /**
     *
     * @param bounds desired bounds
     * @param mapWidth in pixels
     * @return
     */
    public static int zoomLevel(LatLngBounds bounds, int mapWidth) {
        int GLOBE_WIDTH = 256; // a constant in Google's map projection
        double east = bounds.getNorthEast().getLng();
        double west = bounds.getSouthWest().getLng();

        double angle = east - west;
        if (angle < 0) {
            angle += 360;
        }
        double zoom = Math.round(Math.log(mapWidth * 360 / angle / GLOBE_WIDTH) / Math.log10(2));
        return (int) zoom;
    }

    /**
     * This method grows the radius of the Geofence as the month progresses
     *
     * y = mx + b where m = getGeofenceDateSizingSlope, b =
     * getGeofenceDateSizingIntercept and y = the multiplier of the radius as
     * defined in the database
     *
     * @param initialRadius
     * @return
     */
    private static double calculateRadiusBasedOnDateOfMonth(MobileOfferSettings settings, double initialRadius) {
        //y=mx+b, m=0.0991, b=-0.0543
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

        return calculateRadius(settings, initialRadius, dayOfMonth);
    }

    public static double calculateRadius(MobileOfferSettings settings, double initialRadius, int dayOfMonth) {
        if (settings != null && settings.getGeofenceDateSizingSlope() != null && settings.getGeofenceDateSizingIntercept() != null) {
            double y = (settings.getGeofenceDateSizingSlope() * dayOfMonth + settings.getGeofenceDateSizingIntercept());
            double calcRadius = (y > 1) ? y * initialRadius : initialRadius;
            logger.debug("Calcradius was " + calcRadius + " for day of month " + dayOfMonth);
            return calcRadius;
        }
        return initialRadius;
    }

    /**
     * Iterate over the list If point distance to geoFence is within the radius
     * then save GeoFence and real distance from point to Δ distance If point
     * distance is not within radius, still save the Δ distance to that GeoFence
     * and GeoFence (only if we don't have a saved GeoFence
     *
     * @param gp
     * @param fences
     * @return
     */
    public static GeoFenceResponse CalculateClosestGeoFence(MobileOfferSettings settings, LatLng point, List<GeoFence> fences) {


        boolean isWithinFence = false;
        GeoFenceResponse result = new GeoFenceResponse();
        result.setPoint(point);

        double minDistance = Double.MAX_VALUE;

        for (GeoFence geoFence : fences) {

            if (geoFence.getType().equalsIgnoreCase(GeoFence.CIRCLE)) {
                for (GeoPoint geoPoint : geoFence.getGeoPoints()) {

                    LatLng p = new LatLng(geoPoint.getLat(), geoPoint.getLng());
                    /**
                     * The existing calculation resulted in NaN, trying an
                     * alternate
                     */
                    double tempDist = GeoUtil.distanceMiles(point, p);

                    double calcRadius = calculateRadiusBasedOnDateOfMonth(settings, geoPoint.getRadius());

                    if (calcRadius >= tempDist) {
                        //One Time Check to overwrite a minimum result that was NOT ON GEOFENCE
                        if (isWithinFence == false) {
                            isWithinFence = true;
                            result.setGeoFence(geoFence);
                            result.setIsWithinFence(isWithinFence);
                            minDistance = tempDist;
                            result.setDistance(minDistance);

                        } else {
                            //We already had a GeoFence so we compare to see if this one is closest
                            if (geoFence.getPriority() > 0 && (geoFence.getPriority() < result.getGeoFence().getPriority())) {
                                //This fence has better priority changing!
                                result.setGeoFence(geoFence);
                                result.setIsWithinFence(isWithinFence);
                                minDistance = tempDist;
                                result.setDistance(minDistance);
                            } else if (geoFence.getPriority() == result.getGeoFence().getPriority()) {
                                if (tempDist < minDistance) {
                                    result.setGeoFence(geoFence);
                                    result.setIsWithinFence(isWithinFence);
                                    minDistance = tempDist;
                                    result.setDistance(minDistance);
                                }
                            }
                        }
                        //CASE NOT IN GEOFENCE (only add if don't have a previous point within a GeoFence    
                    } else if (!isWithinFence) {
                        if (tempDist < minDistance) {
                            result.setGeoFence(geoFence);
                            result.setIsWithinFence(isWithinFence);
                            minDistance = tempDist;
                            result.setDistance(minDistance);
                        }
                    }
                }

            } else {
                //TODO NO SUPPORT FOR RECTANGLE  AT THE MOMENT
            }

        }
        return result;

    }

    public static double distanceMiles(LatLng point1, LatLng point2) {
        return distance(point1.getLat(), point1.getLng(), point2.getLat(), point2.getLng(), 'M');
    }

    /**
     * Returns the distance
     *
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @param unit one of 'K', 'N' or 'M' for Kilometers, Nautical Miles, or
     * Miles
     * @return
     */
    public static double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }
        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static void main(String[] args) {
        /*
         GeoPoint gp = new GeoPoint(37.655662536621094, -90.33012390136719, 3.0);
         GeoPoint gp2 = new GeoPoint(37.555662536621094, -90.33012390136719, 3.0);
         GeoPoint gp3 = new GeoPoint(47.655662536621094, -99.33012390136719, 3.0);
         LatLng la = new LatLng(gp.getLat(), gp.getLng());
         LatLng lb = new LatLng(gp2.getLat(), gp2.getLng());
         GeoFence gf = new GeoFence();
         gf.setType(GeoFence.CIRCLE);
         gf.setName("close geofence");
         gf.addGeoPoint(gp);
         GeoFence gf2 = new GeoFence();
         gf2.setType(GeoFence.CIRCLE);
         gf2.setName("far geofence");
         gf2.addGeoPoint(gp3);
         List<GeoFence> gfList = new ArrayList<GeoFence>();
         gfList.add(gf);
         System.out.println(distanceMiles(la, lb) + " miles");
         GeoFenceResponse result = CalculateClosestGeoFence(null, lb, gfList);
         System.out.println(result.getGeoFence().getName());
         System.out.println(DistanceMiLatLng(la, lb) + " miles, alt calc");
         */
        //        MobileOfferSettings mos = new MobileOfferSettings();
        //        mos.setGeofenceDateSizingSlope(0.07);
        //        mos.setGeofenceDateSizingIntercept(0.0);
        //
        //        double initialRadius = 3.0;
        //
        //        int[] days = {1, 5, 10, 15, 20, 25, 30};
        //        for (int dayOfMonth : days) {
        //            System.out.println("Day " + dayOfMonth + ": " + calculateRadius(mos, initialRadius, dayOfMonth));
        //        }
        GeocodeResponse geocodeAddress = GeoUtil.GeocodeAddress("30021");
        if (geocodeAddress != null) {
            List<Result> results = geocodeAddress.getResults();
            if (results != null && results.size() > 0) {
                Geometry geom = results.get(0).getGeometry();
                if (geom != null) {
                    LatLng locationData = new LatLng(geom.getLocation().getLat(), geom.getLocation().getLng());
                    System.out.println(locationData);

                }
            }
        }
    }

    public static LatLng getLocationFromZip(String zipcode) {
        GeocodeResponse geocodeAddress = GeoUtil.GeocodeAddress(zipcode);
        if (geocodeAddress != null) {
            List<Result> results = geocodeAddress.getResults();
            if (results != null && results.size() > 0) {
                Geometry geom = results.get(0).getGeometry();
                if (geom != null) {
                    LatLng locationData = new LatLng(geom.getLocation().getLat(), geom.getLocation().getLng());
                    return locationData;
                }
            }
        }
        return null;
    }

    public static List<GeoFence> getGeoFencesInRange(long geoFenceRange,
            double geoFenceDistance, String latitude, String longitude,
            MobileOfferSettings mobileOfferSettings, List<GeoFence> geoFenceList) {
        List<GeoFence> closestGeoFenceList = new ArrayList<GeoFence>();
        for (int i = 0; i < geoFenceRange; i++) {
            LatLng location = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
            GeoFenceResponse geoResponse = GeoUtil.CalculateClosestGeoFence(mobileOfferSettings, location, geoFenceList);
            if (geoResponse.getDistance() < geoFenceDistance) {
                GeoFence closestGeoFence = geoResponse.getGeoFence();
                closestGeoFence.setDistanceAway(geoResponse.getDistance());
                geoFenceList.remove(closestGeoFence);
                closestGeoFenceList.add(closestGeoFence);
            }
        }
        return closestGeoFenceList;
    }
}