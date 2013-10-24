package com.proximus.bean.sms;

import com.proximus.data.xml.googleapi.GeocodeResponse;
import com.proximus.data.xml.googleapi.Result;
import com.proximus.util.server.GeoUtil;
import java.util.ArrayList;
import java.util.List;
import org.primefaces.model.map.*;

/**
 *
 * @author eric
 */
public class GeoPointMarker {

    private Marker northEast;
    private Marker southWest;
    private Marker marker;
    private Marker centerMarker;
    private LatLngBounds bounds;
    private double radius; // in meters;
    private String name;
    private String address;
    private String color;
    public static final double BEARING_NORTH = 0.0;
    public static final double BEARING_NORTH_EAST = 45.0;
    public static final double BEARING_EAST = 90.0;
    public static final double BEARING_SOUTH_EAST = 135.0;
    public static final double BEARING_SOUTH = 180.0;
    public static final double BEARING_SOUTH_WEST = 225.0;
    public static final double BEARING_WEST = 270.0;
    public static final double BEARING_NORTH_WEST = 315.0;

    public GeoPointMarker() {
        this.radius = 3.0;
        this.color = "#7AFF6E";
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public LatLngBounds getBounds() {
        return bounds;
    }

    public void setBounds(LatLngBounds bounds) {
        this.bounds = bounds;
    }

    public Marker getMarker() {
        return centerMarker;
    }

    public void setMarker(Marker marker) {
        this.centerMarker = marker;
        setName(this.centerMarker.getTitle());
    }

    public Marker getCenterMarker() {
        return centerMarker;
    }

    public void setCenterMarker(Marker centerMarker) {
        this.centerMarker = centerMarker;
    }

    public Marker getNorthEast() {
        return northEast;
    }

    public void setNorthEast(Marker northEast) {
        this.northEast = northEast;
    }

    public Marker getSouthWest() {
        return southWest;
    }

    public void setSouthWest(Marker southWest) {
        this.southWest = southWest;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.centerMarker.setTitle(this.name);
    }

    public double getRadius() {
        return this.radius;
    }

    public void setRadius(double radius) {
        if (radius < 0) {
            radius = 0;
        }
        this.radius = radius;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Overlay> getCircularFence() {
        List<Overlay> overlays = new ArrayList<Overlay>();
        if (this.centerMarker != null) {
            System.out.println("Creating circle with radius: " + GeoUtil.MiTom(this.radius) + " miles");
            Circle cir = new Circle(this.centerMarker.getLatlng(), GeoUtil.MiTom(this.radius));
            cir.setFillColor(this.color);
            cir.setStrokeColor("#FFFFFF");
            cir.setFillOpacity(0.5);
            overlays.add(cir);
            overlays.add(this.centerMarker);
        }
        return overlays;

    }

    public List<Overlay> getRectangularFence() {
        List<Overlay> overlays = new ArrayList<Overlay>();
        if (this.centerMarker != null) {
            System.out.println("Creating RECT");
            double distance = GeoUtil.MiTom(this.radius) / 1000;
            LatLng ne = GeoUtil.Destination(this.centerMarker.getLatlng(), distance, BEARING_NORTH_EAST);
            LatLng sw = GeoUtil.Destination(this.centerMarker.getLatlng(), distance, BEARING_SOUTH_WEST);
            LatLngBounds rectangleBounds = GeoUtil.SmallestBounds(ne, sw);

            this.northEast = new Marker(ne, this.name);
            this.northEast.setDraggable(true);
            this.southWest = new Marker(sw, this.name);
            this.southWest.setDraggable(true);

            Rectangle rect = new Rectangle(rectangleBounds);
            rect.setFillColor(this.color);
            rect.setStrokeColor("#FFFFFF");
            rect.setFillOpacity(0.5);
            overlays.add(rect);
            overlays.add(southWest);
            overlays.add(northEast);
            overlays.add(new Marker(this.centerMarker.getLatlng()));
        }
        return overlays;

    }

    public static String getMarkerAddress(Marker marker) {
        GeocodeResponse locationResponse = GeoUtil.GeocodeLocation(marker.getLatlng());
        if (locationResponse != null && locationResponse.getStatus().equalsIgnoreCase("OK")) {
            List<Result> results = locationResponse.getResults();
            if (results != null && results.size() > 0) {
                String markerAddress = results.get(0).getFormattedAddress();
                return markerAddress;
            }
        }
        return null;
    }
}
