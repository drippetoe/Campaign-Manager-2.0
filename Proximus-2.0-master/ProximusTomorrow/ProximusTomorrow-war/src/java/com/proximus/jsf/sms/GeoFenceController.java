/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.jsf.sms;

import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;
import com.proximus.bean.sms.GeoPointMarker;
import com.proximus.data.sms.*;
import com.proximus.data.xml.googleapi.GeocodeResponse;
import com.proximus.data.xml.googleapi.Geometry;
import com.proximus.data.xml.googleapi.Result;
import com.proximus.helper.util.JsfUtil;
import com.proximus.jsf.AbstractController;
import com.proximus.jsf.datamodel.sms.DMADataModel;
import com.proximus.jsf.datamodel.sms.GeoFenceDataModel;
import com.proximus.jsf.datamodel.sms.GeoFenceModel;
import com.proximus.jsf.datamodel.sms.PropertyDataModel;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.sms.*;
import com.proximus.util.server.GeoUtil;
import java.io.Serializable;
import java.util.*;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.map.MarkerDragEvent;
import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.event.map.PointSelectEvent;
import org.primefaces.event.map.StateChangeEvent;
import org.primefaces.model.map.*;

/**
 *
 * @author eric
 */
@ManagedBean(name = "geoFenceController")
@SessionScoped
public class GeoFenceController extends AbstractController implements Serializable {

    @EJB
    CompanyManagerLocal companyMgr;
    @EJB
    GeoPointManagerLocal geoPointMgr;
    @EJB
    GeoFenceManagerLocal geoFenceMgr;
    @EJB
    DMAManagerLocal dmaMgr;
    @EJB
    PropertyManagerLocal propertyMgr;
    @EJB
    CountryManagerLocal countryMgr;
    @EJB
    LocationDataManagerLocal locationDataMgr;
    public static final double BEARING_NORTH = 0.0;
    public static final double BEARING_NORTH_EAST = 45.0;
    public static final double BEARING_EAST = 90.0;
    public static final double BEARING_SOUTH_EAST = 135.0;
    public static final double BEARING_SOUTH = 180.0;
    public static final double BEARING_SOUTH_WEST = 225.0;
    public static final double BEARING_WEST = 270.0;
    public static final double BEARING_NORTH_WEST = 315.0;
    static final Logger logger = Logger.getLogger(GeoFenceController.class.getName());
    private static final String ICON_BLUE_DOT = "http://maps.google.com/mapfiles/ms/micons/blue-dot.png";
    private static final String ICON_BLUE = "http://maps.google.com/mapfiles/ms/micons/blue.png";
    private static final String ICON_RED_DOT = "http://maps.google.com/mapfiles/ms/micons/red-dot.png";
    private static final String ICON_RED = "http://maps.google.com/mapfiles/ms/micons/red.png";
    private static final String ICON_YELLOW_DOT = "http://maps.google.com/mapfiles/ms/micons/yellow-dot.png";
    private static final String ICON_YELLOW = "http://maps.google.com/mapfiles/ms/micons/yellow.png";
    private static final String ICON_GREEN_DOT = "http://maps.google.com/mapfiles/ms/micons/green-dot.png";
    private static final String ICON_GREEN = "http://maps.google.com/mapfiles/ms/micons/green.png";
    private static final String RED = "#FF0000";
    private static final String GREEN = "#00FF00";
    private static final String YELLOW = "#FFFF00";
    private static final String BLUE = "#0000FF";
    private static final String WHITE = "#FFFFFF";
    private static final String BLACK = "#000000";
    public static final int MIN_RADIUS = 10;
    public static final int MAX_RADIUS = 15000;
    private int zoom;
    private GeoFenceModel fenceModel;
    private String address;
    private Marker lastMarker;
    private Marker newMarker;
    private Marker selectedMarker;
    private LatLng center;
    private String location;
    private double radius;
    private String name;
    private String shape;
    public String selectedShape;
    public String enteredRadius;
    private LatLng propertyLocation;
    private GeoFenceDataModel geoFenceModel;
    private GeoFence selectedGeoFence;
    private List<GeoFence> geoFences;
    private DMA selectedDma;
    private int activeTab;
    private GeoPoint propertyGeoPoint;
    private DMADataModel dmaDataModel;
    private List<DMA> filteredDMAs;
    private Property[] selectedProperties;
    private Property selectedProperty;
    private PropertyDataModel propertyDataModel;
    private List<Property> filteredProperties;
    private Country selectedCountry;
    private List<String> countryList;

    /*
     * SELECTED
     */
    //private GeoPoint selectedGeoPoint;
    private int selectedRadius;
    private GeoFence newGeoFence;
    private GeoPointMarker selectedGeoPointMarker;
    private ResourceBundle message;

    public GeoFenceController() {
        message = this.getHttpSession().getMessages();
    }

    public String getSelectedShape() {
        return selectedShape;
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }

    public List<String> getCountryList() {
        if (countryList == null) {
            List<Country> list = countryMgr.findAllSortedCountriesByProperty();
            countryList = new ArrayList<String>();
            for (Country c : list) {
                countryList.add(c.getName());
            }
        }
        return countryList;
    }

    public void setCountryList(List<String> countryList) {
        this.countryList = countryList;
    }

    public Country getSelectedCountry() {
        if (selectedCountry == null) {
            selectedCountry = countryMgr.findByCode("US");
        }
        return selectedCountry;
    }

    public void setSelectedCountry(Country selectedCountry) {
        this.selectedCountry = selectedCountry;
    }

    public void setSelectedShape(String selectedShape) {
        this.selectedShape = selectedShape;
    }

    public String getEnteredRadius() {
        return enteredRadius;
    }

    public void setEnteredRadius(String enteredRadius) {
        this.enteredRadius = enteredRadius;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String inputtedAddress) {
        this.address = inputtedAddress;
    }

    public GeoPoint getPropertyGeoPoint() {
        propertyGeoPoint = geoPointMgr.findGeoPointByLocation(this.propertyLocation.getLat(), this.propertyLocation.getLng());
        return propertyGeoPoint;
    }

    public void setPropertyGeoPoint(GeoPoint propertyGeoPoint) {
        this.propertyGeoPoint = propertyGeoPoint;
    }

    public GeoFence getNewGeoFence() {
        return newGeoFence;
    }

    public void setNewGeoFence(GeoFence newGeoFence) {
        this.newGeoFence = newGeoFence;
    }

    public Property[] getSelectedProperties() {
        return selectedProperties;
    }

    public void setSelectedProperties(Property[] selectedProperties) {
        this.selectedProperties = selectedProperties;
    }

    public Property getSelectedProperty() {
        return selectedProperty;
    }

    public void setSelectedProperty(Property selectedProperty) {
        this.selectedProperty = selectedProperty;
    }

    public PropertyDataModel getPropertyDataModel() {
        return propertyDataModel;
    }

    public void setPropertyDataModel(PropertyDataModel propertyDataModel) {
        this.propertyDataModel = propertyDataModel;
    }

    public List<Property> getFilteredProperties() {
        return filteredProperties;
    }

    public void setFilteredProperties(List<Property> filteredProperties) {
        this.filteredProperties = filteredProperties;
    }

    public DMADataModel getDmaDataModel() {
        populateDMADataModel();
        return dmaDataModel;
    }

    public void setDmaDataModel(DMADataModel dmaDataModel) {
        this.dmaDataModel = dmaDataModel;
    }

    public List<DMA> getFilteredDMAs() {
        return filteredDMAs;
    }

    public void setFilteredDMAs(List<DMA> filteredDMAs) {
        this.filteredDMAs = filteredDMAs;
    }

    public DMA getSelectedDma() {
        if (selectedDma == null) {
            selectedDma = new DMA();
        }
        return selectedDma;
    }

    public void setSelectedDma(DMA selectedDma) {
        this.selectedDma = selectedDma;
    }

    public int getSelectedRadius() {
        return selectedRadius;
    }

    public void setSelectedRadius(int selectedRadius) {
        this.selectedRadius = selectedRadius;
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
        updateMap();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getMaxRadius() {
        return MAX_RADIUS;
    }

    public int getMinRadius() {
        return MIN_RADIUS;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public LatLng getPropertyLocation() {
        return propertyLocation;
    }

    public void setPropertyLocation(LatLng propertyLocation) {
        this.propertyLocation = propertyLocation;
    }

    public GeoFenceDataModel getGeoFenceModel() {
        if (geoFenceModel == null) {
            populateGeoFenceModel();
        }
        return geoFenceModel;
    }

    public void setGeoFenceModel(GeoFenceDataModel geoFenceModel) {
        this.geoFenceModel = geoFenceModel;
    }

    public void populateGeoFenceModel() {
        if (geoFences == null) {
            geoFenceModel = new GeoFenceDataModel(geoFenceMgr.getGeoFencesByCompany(this.getCompanyFromSession()));
        } else {
            if (getSelectedCountry().getName() == null || getSelectedCountry().getName().isEmpty()) {
                getSelectedCountry().setName("United States");
            }
            Country realCountry = countryMgr.findByName(getSelectedCountry().getName());
            setSelectedCountry(realCountry);
            geoFenceModel = new GeoFenceDataModel(geoFenceMgr.getGeoFencesByCompanyAndCountry(this.getCompanyFromSession(), realCountry));
        }
    }

    public GeoFence getSelectedGeoFence() {
        if (selectedGeoFence == null) {
            selectedGeoFence = new GeoFence();
        }
        setLocationOfSelectedGeoFence(selectedGeoFence);
        return selectedGeoFence;
    }

    public void setSelectedGeoFence(GeoFence selectedGeoFence) {
        this.selectedGeoFence = selectedGeoFence;
    }

    private void setLocationOfSelectedGeoFence(GeoFence geofence) {
        if (propertyLocation == null) {
            if (geofence.getProperty() != null) {
                String propertyAddress = geofence.getProperty().getFormattedAddress();
                GeocodeResponse geocodeAddress = GeoUtil.GeocodeAddress(propertyAddress);
                if (geocodeAddress != null) {
                    List<Result> results = geocodeAddress.getResults();
                    if (results != null && results.size() > 0) {
                        Geometry geom = results.get(0).getGeometry();
                        if (geom != null) {
                            setPropertyLocation(new LatLng(geom.getLocation().getLat(), geom.getLocation().getLng()));
                        }
                    }
                }
            }
        }
    }

    public List<GeoFence> getGeoFences() {
        return geoFences;
    }

    public void setGeoFences(List<GeoFence> geoFences) {
        this.geoFences = geoFences;
    }

    public MapModel getFenceModel() {

        return fenceModel;
    }

    public void setCenter(LatLng center) {
        this.center = center;
    }

    public String getCenter() {
        if (this.center == null) {
            /*
             * Center on this property or the center of the United States
             */
            if (this.propertyLocation != null) {
                this.center = propertyLocation;
            } else {
                this.center = new LatLng(37.09024, -95.71289100000001);
            }
        }
        return "" + this.center.getLat() + "," + this.center.getLng();
    }

    public Marker getSelectedMarker() {
        return selectedMarker;
    }

    public void setSelectedMarker(Marker selectedMarker) {

        this.selectedMarker = selectedMarker;
    }

    public Marker getNewMarker() {
        return newMarker;
    }

    public void setNewMarker(Marker newMarker) {
        if (this.newMarker != null) {
            this.lastMarker = this.newMarker;
            this.lastMarker.setIcon(ICON_GREEN);
        }
        this.newMarker = newMarker;
        GeocodeResponse locationResponse = GeoUtil.GeocodeLocation(this.newMarker.getLatlng());
        if (locationResponse != null && locationResponse.getStatus().equalsIgnoreCase("OK")) {
            List<Result> results = locationResponse.getResults();
            if (results != null && results.size() > 0) {
                String markerAddress = results.get(0).getFormattedAddress();
                //System.err.println("GOT: " + address);
                this.location = markerAddress;
                this.newMarker.setTitle(markerAddress);
                this.newMarker.setData(markerAddress);

            }
        }
    }

    public int getActiveTab() {
        return activeTab;
    }

    public void setActiveTab(int activeTab) {
        this.activeTab = activeTab;
    }

    public GeoPointMarker getSelectedGeoPointMarker() {
        return selectedGeoPointMarker;
    }

    public void setSelectedGeoPointMarker(GeoPointMarker selectedGeoPointMarker) {
        this.selectedGeoPointMarker = selectedGeoPointMarker;
    }

    public String getSelectedMarkerAddress() {
        return getMarkerAddress(this.selectedMarker);
    }

    public void onDMARowSelect(SelectEvent event) {
        DMA dma = (DMA) event.getObject();
        if (dma != null) {
            setSelectedDma(dma);
            populatePropertyDataModel();
            this.activeTab = 1;
        }
    }

    public void onPropertyRowSelect(SelectEvent event) {
        fenceModel = new GeoFenceModel();
        this.selectedGeoPointMarker.setCenterMarker(null);
        Property property = (Property) event.getObject();
        if (property != null) {
            setSelectedProperty(property);
            setSelectedDma(property.getDma());
            shape = "CIRCLE";
            this.activeTab = 2;
            this.propertyLocation = null;
            populatePropertyMap();
            setCenter(null);
        }
        updateMap();
    }

    public void onStateChange(StateChangeEvent event) {
        this.center = event.getCenter();
    }

    public void onPointSelect(PointSelectEvent event) {
        LatLng pos = event.getLatLng();
        Marker nMarker = createMarker(pos, "", "", ICON_GREEN_DOT);
        setNewMarker(nMarker);
        this.selectedGeoPointMarker.setMarker(this.newMarker);
        this.selectedGeoPointMarker.setAddress(getMarkerAddress(this.newMarker));
        updateMap();
    }

    private void populatePropertyDataModel() {
        List<Property> findAll = this.propertyMgr.getPropertiesByCompanyDMA(this.getCompanyFromSession(), getSelectedDma());
        if (findAll == null) {
            this.propertyDataModel = new PropertyDataModel();
            filteredProperties = new ArrayList<Property>();
        } else {
            this.propertyDataModel = new PropertyDataModel(findAll);
            filteredProperties = new ArrayList<Property>(findAll);
        }
    }

    private void populateDMADataModel() {
        List<Long> dmaIds = propertyMgr.getDMAIdsByCompany(companyMgr.getCompanybyId(getCompanyIdFromSession()));
        DMA tempDMA;
        List<DMA> dmaList = new ArrayList<DMA>();
        if (dmaIds == null) {
            this.dmaDataModel = new DMADataModel();
        } else {
            for (Long value : dmaIds) {
                tempDMA = dmaMgr.find(value);
                dmaList.add(tempDMA);
            }
            Collections.sort(dmaList, new Comparator<DMA>() {
                @Override
                public int compare(DMA dma1, DMA dma2) {
                    return dma1.getName().compareTo(dma2.getName());
                }
            });
            this.dmaDataModel = new DMADataModel(dmaList);
            filteredDMAs = new ArrayList<DMA>(dmaList);
        }
    }

    private void populatePropertyMap() {
        if (selectedProperty != null) {
            List<GeoFence> propertyGeoFences = geoFenceMgr.findAllByProperty(selectedProperty);
            if (propertyGeoFences != null && !propertyGeoFences.isEmpty()) {
                GeoFence propertyGeoFence = propertyGeoFences.get(0);
                setPropertyLocation(new LatLng(propertyGeoFence.getGeoPoints().get(0).getLat(), propertyGeoFence.getGeoPoints().get(0).getLng()));
            }
        } else {
            logger.fatal("Property was null for some reason. ");
        }
    }

    /**
     * Create GeoFences drawn on the map. TODO: Support multiple shapes etc.
     *
     * @return
     */
    public void createGeoFence(String address) {
        Property property = getPropertyGeoPoint().getGeoFence().getProperty();
        setSelectedDma(property.getDma());
        setSelectedProperty(property);
        if (address != null) {
            setAddress(address);
        }
        if (address != null && selectedShape != null && enteredRadius != null) {
            double realRadius;
            try {
                realRadius = Double.parseDouble(enteredRadius);
            } catch (NumberFormatException nfe) {
                realRadius = 3.0;
                JsfUtil.addErrorMessage(message.getString("geofenceRadiusError"));
            }
            GeocodeResponse geocodeAddress = GeoUtil.GeocodeAddress(address);
            if (geocodeAddress != null) {
                List<Result> results = geocodeAddress.getResults();
                if (results != null && results.size() > 0) {
                    Geometry geom = results.get(0).getGeometry();
                    if (geom != null) {
                        LatLng locationData = new LatLng(geom.getLocation().getLat(), geom.getLocation().getLng());
                        GeoPoint locationGeoPoint = geoPointMgr.findGeoPointByLocation(locationData.getLat(), locationData.getLng());
                        if (locationGeoPoint == null) {
                            GeoFence createdGeoFence = new GeoFence(this.name, property);
                            createdGeoFence.setType(selectedShape);
                            createdGeoFence.setCompany(companyMgr.find(this.getCompanyIdFromSession()));
                            geoFenceMgr.create(createdGeoFence);
                            createdGeoFence.setPriority(createdGeoFence.getId());
                            geoFenceMgr.update(createdGeoFence);
                            if (selectedShape.equals(GeoFence.CIRCLE)) {
                                GeoPoint circularPoint = getCircularGeoPoint(createdGeoFence, locationData, realRadius);
                                circularPoint.setPropertyPoint(true);
                                geoPointMgr.create(circularPoint);
                                updateMap();
                                this.address = null;
                                this.name = null;
                                this.enteredRadius = null;
                                this.selectedShape = null;
                            } else if (selectedShape.equals(GeoFence.RECTANGLE)) {
                                List<GeoPoint> rectangularPoints = getRectangularGeoPoints(createdGeoFence, locationData, realRadius);
                                for (GeoPoint geoPoint : rectangularPoints) {
                                    geoPointMgr.create(geoPoint);
                                }
                                updateMap();
                                this.address = null;
                                this.name = null;
                                this.enteredRadius = null;
                                this.selectedShape = null;
                            }
                        }

                    }
                }
            }
        }
    }

    private GeoPoint getCircularGeoPoint(GeoFence geoFence, LatLng coordinates, double radius) {
        GeoPoint geoPoint = new GeoPoint(coordinates.getLat(), coordinates.getLng(), radius, geoFence);
        return geoPoint;
    }

    private List<GeoPoint> getRectangularGeoPoints(GeoFence geoFence, LatLng coordinates, double radius) {
        List<GeoPoint> geoPoints = new ArrayList<GeoPoint>();
        GeoPoint centerGeoPoint = new GeoPoint(coordinates.getLat(), coordinates.getLng(), radius, geoFence);
        centerGeoPoint.setPropertyPoint(true);
        geoPoints.add(0, centerGeoPoint);
        double distance = GeoUtil.MiTom(centerGeoPoint.getRadius()) / 1000;
        LatLng latLng = new LatLng(centerGeoPoint.getLat(), centerGeoPoint.getLng());
        LatLng ne = GeoUtil.Destination(latLng, distance, BEARING_NORTH_EAST);
        GeoPoint northEast = new GeoPoint(ne.getLat(), ne.getLng(), radius, geoFence);
        geoPoints.add(1, northEast);
        LatLng sw = GeoUtil.Destination(latLng, distance, BEARING_SOUTH_WEST);
        GeoPoint southWest = new GeoPoint(sw.getLat(), sw.getLng(), radius, geoFence);
        geoPoints.add(2, southWest);
        return geoPoints;
    }

    public void deleteGeoFence() {
        if (this.selectedGeoPointMarker.getMarker() != null) {
            setPropertyLocation(selectedGeoPointMarker.getMarker().getLatlng());
            Property property = getPropertyGeoPoint().getGeoFence().getProperty();
            setSelectedDma(property.getDma());
            setSelectedProperty(property);
            GeoPoint geoPoint = geoPointMgr.findGeoPointByLocation(selectedGeoPointMarker.getMarker().getLatlng().getLat(),
                    selectedGeoPointMarker.getMarker().getLatlng().getLng());
            GeoFence gf = geoPoint.getGeoFence();
            if (gf.getType().equals(GeoFence.RECTANGLE)) {
                for (GeoPoint point : gf.getGeoPoints()) {
                    geoPointMgr.delete(point);
                }
            } else {
                geoPointMgr.delete(geoPoint);
            }
            geoFenceMgr.delete(gf);
            if (geoFences == null) {
                this.fenceModel.clearMarkers();
                this.fenceModel.clearCircles();
            }

            List<GeoFence> propertyGeofences = geoFenceMgr.findAllByProperty(property);
            if (propertyGeofences != null && propertyGeofences.size() > 0) {
                GeoFence geoFence = propertyGeofences.get(0);
                LatLng geoPointLocation = new LatLng(geoFence.getGeoPoints().get(0).getLat(), geoFence.getGeoPoints().get(0).getLng());
                setPropertyLocation(geoPointLocation);
            }
            if (geoFences == null) {
                updateMap();
            } else {
                fenceModel.getMarkers().remove(selectedGeoPointMarker.getMarker());
                LatLng latLng = new LatLng(selectedGeoPointMarker.getMarker().getLatlng().getLat(), selectedGeoPointMarker.getMarker().getLatlng().getLng());
                Circle cir = new Circle(latLng, GeoUtil.MiTom(selectedGeoPointMarker.getRadius()));
                Circle oldCircle = null;
                for (Circle circle : fenceModel.getCircles()) {
                    if (cir.getCenter().equals(circle.getCenter())) {
                        oldCircle = circle;
                    }
                }
                if (oldCircle != null) {
                    fenceModel.getCircles().remove(oldCircle);
                }
                populateGeoFenceModel();
                setCenter(null);
                setZoom(4);
            }
        }
    }

    public Marker createMarker(LatLng pos, String title, Object data, String icon) {

        Marker new_marker = new Marker(pos, title, data, icon);
        new_marker.setDraggable(true);
        return new_marker;
    }

    public void centerMap(ActionEvent actionEvent) {
        this.center = this.selectedMarker.getLatlng();
    }

    public void updateRadius(Property property) {
        if (property != null) {
            setSelectedProperty(property);
        }
        updateMap();
    }

    private Marker getPropertyMarker() {
        if (this.selectedProperty != null) {
            GeoPoint gp = geoPointMgr.findGeoPointByLocation(propertyLocation.getLat(), propertyLocation.getLng());
            if (gp != null) {
                LatLng ll = new LatLng(gp.getLat(), gp.getLng());
                Marker home = new Marker(ll, this.selectedProperty.getName());
                home.setDraggable(false);
                home.setClickable(true);
                home.setIcon(ICON_YELLOW);
                return home;
            }
        }
        return null;
    }

    public void updateGeoFence(Property property) {
        if (property != null) {
            setSelectedProperty(property);
            setSelectedDma(property.getDma());
        }

        if (this.selectedGeoPointMarker.getMarker() != null) {
            GeoPoint geoPoint = geoPointMgr.findGeoPointByLocation(selectedGeoPointMarker.getMarker().getLatlng().getLat(),
                    selectedGeoPointMarker.getMarker().getLatlng().getLng());
            GeoFence gf = geoPoint.getGeoFence();
            gf.setName(this.selectedGeoPointMarker.getName());
            geoFenceMgr.update(gf);
            populateGeoFenceModel();
            GeoPoint centerGeoPoint = gf.getGeoPoints().get(0);
            LatLng centerPoint = new LatLng(centerGeoPoint.getLat(), centerGeoPoint.getLng());
            if (gf.getType().equals(GeoFence.RECTANGLE)) {
                List<GeoPoint> rectangularPoints = getRectangularGeoPoints(gf, centerPoint, this.selectedGeoPointMarker.getRadius());
                geoPointMgr.delete(gf.getGeoPoints().get(1));
                geoPointMgr.delete(gf.getGeoPoints().get(2));
                centerGeoPoint.setRadius(this.selectedGeoPointMarker.getRadius());
                geoPointMgr.update(centerGeoPoint);
                geoPointMgr.create(rectangularPoints.get(2));
                geoPointMgr.create(rectangularPoints.get(1));
            } else {
                geoPoint.setRadius(this.selectedGeoPointMarker.getRadius());
                geoPointMgr.update(geoPoint);
            }
        }
        if (geoFences != null) {
            fenceModel.getMarkers().remove(selectedGeoPointMarker.getMarker());
            fenceModel.addOverlay(selectedGeoPointMarker.getMarker());
            LatLng latLng = new LatLng(selectedGeoPointMarker.getMarker().getLatlng().getLat(), selectedGeoPointMarker.getMarker().getLatlng().getLng());
            Circle cir = new Circle(latLng, GeoUtil.MiTom(selectedGeoPointMarker.getRadius()));
            Circle oldCircle = null;
            for (Circle circle : fenceModel.getCircles()) {
                if (cir.getCenter().equals(circle.getCenter())) {
                    oldCircle = circle;
                }
            }
            if (oldCircle != null) {
                fenceModel.getCircles().remove(oldCircle);
            }
            cir.setFillColor(GREEN);
            cir.setStrokeColor(WHITE);
            cir.setFillOpacity(0.25);
            getCenter();
            fenceModel.addOverlay(cir);
        } else {
            updateMap();
        }
    }

    public int numberOfGeofencesInProperty() {
        Property property = getPropertyGeoPoint().getGeoFence().getProperty();
        setSelectedDma(property.getDma());
        setSelectedProperty(property);
        List<GeoFence> geofences = geoFenceMgr.findAllByProperty(property);
        int numberOfGeofences = geofences.size();
        return numberOfGeofences;
    }

    private void updateMap() {
        this.fenceModel.clearMarkers();
        this.fenceModel.clearCircles();
        this.fenceModel.clearRectangles();
        this.fenceModel.clearPolygons();
        this.fenceModel.clearPolylines();
        for (Overlay overlay : getGeoFencesOverlay()) {
            this.fenceModel.addOverlay(overlay);
        }
    }

    public String prepareCreate() {
        fenceModel = new GeoFenceModel();
        this.radius = MAX_RADIUS / 2;
        this.center = null;
        this.newMarker = null;
        this.lastMarker = null;
        this.shape = GeoFence.CIRCLE;
        this.selectedRadius = 0;
        this.location = "";
        this.name = "";
        this.activeTab = 0;
        this.selectedDma = null;
        this.propertyDataModel = null;
        this.dmaDataModel = null;
        propertyLocation = null;
        geoFences = null;
        this.selectedGeoPointMarker = new GeoPointMarker();
        populateDMADataModel();
        populatePropertyDataModel();
        return "/geofence/Create?faces-redirect=true";
    }

    public String prepareList() {
        fenceModel = new GeoFenceModel();
        this.selectedGeoPointMarker = new GeoPointMarker();
        geoFences = null;
        populateGeoFenceList();
        selectedGeoFence = null;
        selectedCountry = null;
        return "/geofence/List?faces-redirect=true";
    }

    public void populateGeoFenceList() {
        propertyLocation = null;
        zoom = 4;
        setCenter(null);
        fenceModel = new GeoFenceModel();
        this.selectedGeoPointMarker = new GeoPointMarker();
        if (getSelectedCountry().getName() == null || getSelectedCountry().getName().isEmpty()) {
            getSelectedCountry().setName("United States");
        }
        Country realCountry = countryMgr.findByName(getSelectedCountry().getName());

        List<GeoFence> fences = geoFenceMgr.getGeoFencesByCompanyAndCountry(this.getCompanyFromSession(), realCountry);

        if (fences != null && !fences.isEmpty()) {
            this.geoFences = fences;
            populateGeoFenceModel();
            if (!selectedCountry.getCode().equals("US")) {
                LatLng centerPoint = new LatLng(geoFences.get(0).getGeoPoints().get(0).getLat(), geoFences.get(0).getGeoPoints().get(0).getLng());
                setCenter(centerPoint);
                zoom = 6;
            }
            for (GeoFence geoFence : geoFences) {
                if (geoFence.getType().equals(GeoFence.CIRCLE)) {
                    for (GeoPoint geoPoint : geoFence.getGeoPoints()) {
                        LatLng latLng = new LatLng(geoPoint.getLat(), geoPoint.getLng());
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
            }
        }
    }

    private void markerToSelectedGeoPoint(Marker marker) {
        GeoPoint geoPoint = geoPointMgr.findGeoPointByLocation(marker.getLatlng().getLat(), marker.getLatlng().getLng());
        if (propertyLocation != null) {
            GeoFence geoFence = geoFenceMgr.findByName(marker.getTitle(), getPropertyGeoPoint().getGeoFence().getProperty());
            LatLng latLng = new LatLng(marker.getLatlng().getLat(), marker.getLatlng().getLng());
            if (geoPoint == null && !geoFence.getType().equals(GeoFence.RECTANGLE)) {
                List<GeoPoint> geoPointList = geoFence.getGeoPoints();
                for (GeoPoint oldGeoPoint : geoPointList) {
                    geoPointMgr.delete(oldGeoPoint);
                }
                GeoPoint newGeoPoint = new GeoPoint(latLng.getLat(), latLng.getLng(), 3, geoFence);
                geoPointMgr.create(newGeoPoint);
                geoPoint = newGeoPoint;
                if (geoFences == null) {
                    this.fenceModel.clearMarkers();
                    this.fenceModel.clearCircles();
                }
            } else if (geoPoint == null && geoFence.getType().equals(GeoFence.RECTANGLE)) {
                geoPointMgr.delete(geoFence.getGeoPoints().get(1));
                GeoPoint newGeoPoint = new GeoPoint(latLng.getLat(), latLng.getLng(), 3, geoFence);
                geoPointMgr.create(newGeoPoint);
                com.javadocmd.simplelatlng.LatLng southWest = new com.javadocmd.simplelatlng.LatLng(latLng.getLat(), latLng.getLng());
                com.javadocmd.simplelatlng.LatLng centerPoint = new com.javadocmd.simplelatlng.LatLng(geoFence.getGeoPoints().get(0).getLat(),
                        geoFence.getGeoPoints().get(0).getLng());
                double rectangleRadius = LatLngTool.distance(southWest, centerPoint, LengthUnit.MILE);
                newGeoPoint.setRadius(Math.round(rectangleRadius));
                geoPointMgr.update(newGeoPoint);
                geoPoint = newGeoPoint;
                geoFence.getGeoPoints().set(1, newGeoPoint);
                if (geoFences == null) {
                    this.fenceModel.clearMarkers();
                    this.fenceModel.clearCircles();
                }
            }
        }
        GeocodeResponse geocodeAddress = GeoUtil.GeocodeLocation(marker.getLatlng());
        if (geocodeAddress != null) {
            List<Result> results = geocodeAddress.getResults();
            if (results != null && results.size() > 0) {
                String markerAddress = results.get(0).getFormattedAddress();
                if (markerAddress != null) {
                    marker.setTitle(geoPoint.getGeoFence().getName());
                    marker.setDraggable(false);
                    this.selectedGeoPointMarker.setMarker(marker);
                    this.selectedGeoPointMarker.setAddress(markerAddress);
                    this.selectedGeoPointMarker.setRadius(geoPoint.getRadius());
                }
            }
        }
    }

    public void onRowSelect(SelectEvent event) {
        if (selectedGeoFence.getType().equals(GeoFence.CIRCLE)) {
            for (GeoPoint geoPoint : selectedGeoFence.getGeoPoints()) {
                LatLng latLng = new LatLng(geoPoint.getLat(), geoPoint.getLng());
                setCenter(latLng);
                setPropertyLocation(latLng);
                Marker marker = new Marker(latLng);
                marker.setTitle(geoPoint.getGeoFence().getName());
                marker.setDraggable(false);
                marker.setClickable(true);
                marker.setIcon(ICON_BLUE);
                this.selectedGeoPointMarker.setMarker(marker);
                this.selectedGeoPointMarker.setAddress(getMarkerAddress(marker));
                this.selectedGeoPointMarker.setRadius(geoPoint.getRadius());
                fenceModel.getMarkers().remove(selectedGeoPointMarker.getMarker());
                fenceModel.addOverlay(marker);
                setZoom(12);
            }
        } else if (selectedGeoFence.getType().equals(GeoFence.RECTANGLE)) {
            LatLng centerPoint = new LatLng(selectedGeoFence.getGeoPoints().get(0).getLat(), selectedGeoFence.getGeoPoints().get(0).getLng());
            LatLng ne = new LatLng(selectedGeoFence.getGeoPoints().get(1).getLat(), selectedGeoFence.getGeoPoints().get(1).getLng());
            LatLng sw = new LatLng(selectedGeoFence.getGeoPoints().get(2).getLat(), selectedGeoFence.getGeoPoints().get(2).getLng());
            LatLngBounds bounds = GeoUtil.SmallestBounds(ne, sw);

            setCenter(centerPoint);
            setPropertyLocation(centerPoint);
            Marker centerMarker = new Marker(centerPoint);
            centerMarker.setTitle(selectedGeoFence.getGeoPoints().get(0).getGeoFence().getName());
            centerMarker.setDraggable(false);
            centerMarker.setClickable(true);
            centerMarker.setIcon(ICON_YELLOW);
            this.selectedGeoPointMarker.setMarker(centerMarker);
            this.selectedGeoPointMarker.setAddress(getMarkerAddress(centerMarker));
            this.selectedGeoPointMarker.setRadius(selectedGeoFence.getGeoPoints().get(0).getRadius());
            fenceModel.addOverlay(centerMarker);

            Marker neMarker = new Marker(ne);
            neMarker.setTitle(selectedGeoFence.getGeoPoints().get(1).getGeoFence().getName());
            neMarker.setDraggable(false);
            neMarker.setClickable(false);
            neMarker.setIcon(ICON_GREEN);
            this.selectedGeoPointMarker.setMarker(neMarker);
            this.selectedGeoPointMarker.setAddress(getMarkerAddress(neMarker));
            this.selectedGeoPointMarker.setRadius(selectedGeoFence.getGeoPoints().get(1).getRadius());
            fenceModel.addOverlay(neMarker);

            Marker swMarker = new Marker(sw);
            swMarker.setTitle(selectedGeoFence.getGeoPoints().get(2).getGeoFence().getName());
            swMarker.setDraggable(false);
            swMarker.setClickable(false);
            swMarker.setIcon(ICON_RED);
            this.selectedGeoPointMarker.setMarker(swMarker);
            this.selectedGeoPointMarker.setAddress(getMarkerAddress(swMarker));
            this.selectedGeoPointMarker.setRadius(selectedGeoFence.getGeoPoints().get(2).getRadius());
            fenceModel.addOverlay(swMarker);
            Rectangle rect = new Rectangle(bounds);
            rect.setFillColor(RED);
            rect.setStrokeColor(WHITE);
            rect.setFillOpacity(0.5);
            fenceModel.addOverlay(rect);
            setZoom(8);
        }
    }

    public void onMarkerSelect(OverlaySelectEvent event) {
        if (event.getOverlay() instanceof Marker) {
            Marker marker = (Marker) event.getOverlay();
            if (marker.isClickable() != false) {
                if (geoFences != null) {
                    setZoom(12);
                    setCenter(marker.getLatlng());
                    setPropertyLocation(marker.getLatlng());
                }
                markerToSelectedGeoPoint(marker);
                if (geoFences == null) {
                    updateMap();
                }
            }
        }
    }

    public void onMarkerDrag(MarkerDragEvent event) {
        Marker marker = (Marker) event.getMarker();
        markerToSelectedGeoPoint(marker);
        updateMap();
    }

    public String getMarkerAddress(Marker marker) {
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

    private List<Overlay> getGeoFencesOverlay() {
        List<Overlay> overlays = new ArrayList<Overlay>();
        List<GeoFence> geoFencesByProperty = geoFenceMgr.findAllByProperty(getSelectedProperty());
        if (geoFencesByProperty != null && geoFencesByProperty.size() > 0) {

            for (GeoFence geoFence : geoFencesByProperty) {
                List<GeoPoint> geoPoints = geoFence.getGeoPoints();
                if (geoFence.getType().equalsIgnoreCase(GeoFence.CIRCLE)) {
                    for (GeoPoint geoPoint : geoPoints) {
                        LatLng ll = new LatLng(geoPoint.getLat(), geoPoint.getLng());
                        Marker otherMarker = new Marker(ll, geoFence.getName());
                        otherMarker.setClickable(true);
                        if (getPropertyMarker().getLatlng().equals(otherMarker.getLatlng())) {
                            otherMarker.setIcon(GeoFenceController.ICON_YELLOW);
                            otherMarker.setDraggable(false);
                        } else {
                            otherMarker.setIcon(GeoFenceController.ICON_BLUE);
                            otherMarker.setDraggable(true);
                        }

                        this.fenceModel.addOverlay(otherMarker);

                        Circle cir = new Circle(ll, GeoUtil.MiTom(geoPoint.getRadius()));
                        cir.setFillColor(RED);
                        cir.setStrokeColor(WHITE);
                        cir.setFillOpacity(0.5);
                        overlays.add(cir);
                    }
                }
                if (geoFence.getType().equalsIgnoreCase(GeoFence.RECTANGLE)) {
                    if (geoPoints.size() > 2) {
                        LatLng centerPoint = new LatLng(geoPoints.get(0).getLat(), geoPoints.get(0).getLng());
                        LatLng ll1 = new LatLng(geoPoints.get(1).getLat(), geoPoints.get(1).getLng());
                        LatLng ll2 = new LatLng(geoPoints.get(2).getLat(), geoPoints.get(2).getLng());
                        LatLngBounds bounds = GeoUtil.SmallestBounds(ll1, ll2);
                        Marker firstMarker = new Marker(centerPoint, geoFence.getName());
                        firstMarker.setClickable(true);
                        firstMarker.setIcon(GeoFenceController.ICON_BLUE);
                        firstMarker.setDraggable(false);
                        Marker secondMarker = new Marker(ll1, geoFence.getName());
                        secondMarker.setClickable(false);
                        secondMarker.setIcon(GeoFenceController.ICON_GREEN);
                        secondMarker.setDraggable(true);
                        Marker thirdMarker = new Marker(ll2, geoFence.getName());
                        thirdMarker.setClickable(false);
                        thirdMarker.setIcon(GeoFenceController.ICON_RED);
                        thirdMarker.setDraggable(false);
                        this.fenceModel.addOverlay(firstMarker);
                        this.fenceModel.addOverlay(secondMarker);
                        this.fenceModel.addOverlay(thirdMarker);
                        Rectangle rect = new Rectangle(bounds);
                        rect.setFillColor(RED);
                        rect.setStrokeColor(WHITE);
                        rect.setFillOpacity(0.5);
                        overlays.add(rect);
                    }
                }
            }
        }
        return overlays;
    }
}
