/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.sms;

import com.proximus.data.Company;
import com.proximus.data.sms.*;
import com.proximus.data.xml.googleapi.GeocodeResponse;
import com.proximus.data.xml.googleapi.Geometry;
import com.proximus.data.xml.googleapi.Result;
import com.proximus.helper.util.JsfUtil;
import com.proximus.jsf.AbstractController;
import com.proximus.jsf.datamodel.sms.PropertyDataModel;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.sms.*;
import com.proximus.util.ListChoosers;
import com.proximus.util.server.GeoUtil;
import java.io.Serializable;
import java.util.*;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;

/**
 *
 * @author ronald
 */
@ManagedBean(name = "propertyController")
@SessionScoped
public class PropertyController extends AbstractController implements Serializable {

    private static final long serialVersionUID = 1;
    static final Logger logger = Logger.getLogger(PropertyController.class.getName());
    @EJB
    CompanyManagerLocal companyMgr;
    @EJB
    PropertyManagerLocal propertyMgr;
    @EJB
    DMAManagerLocal DMAMgr;
    @EJB
    MobileOfferSettingsManagerLocal mosMgr;
    @EJB
    RetailerManagerLocal retailerMgr;
    @EJB
    CountryManagerLocal countryMgr;
    @EJB
    GeoFenceManagerLocal geoFenceMgr;
    @EJB
    GeoPointManagerLocal geoPointMgr;
    private Property newProperty;
    private PropertyDataModel propertyModel;
    private List<Property> filteredProperties;
    private Property selectedProperty;
    private List<Property> propertyList;
    private List<DMA> listDmas;
    private DMA selectedDma;
    private List<String> selectedCountries;
    private List<String> selectedStates = ListChoosers.getStates();
    private DualListModel<String> picklistModel;
    private List<String> picklistSource;
    private List<String> picklistTarget;
    private boolean editUI = false;
    private ResourceBundle message;

    public PropertyController() {
        message = this.getHttpSession().getMessages();
    }

    public Property getNewProperty() {
        if (newProperty == null) {
            newProperty = new Property();
        }
        return newProperty;
    }

    public void setNewProperty(Property newProperty) {
        this.newProperty = newProperty;
    }

    public PropertyDataModel getPropertyModel() {
        if (propertyModel == null) {
            populatePropertyModel();
        }
        return propertyModel;
    }

    public void setPropertyModel(PropertyDataModel propertyModel) {
        this.propertyModel = propertyModel;
    }

    public List<Property> getFilteredProperties() {
        return filteredProperties;
    }

    public void setFilteredProperties(List<Property> filteredProperties) {
        this.filteredProperties = filteredProperties;
    }

    public Property getSelectedProperty() {
        if (selectedProperty == null) {
            selectedProperty = new Property();
        }
        return selectedProperty;
    }

    public void setSelectedProperty(Property selectedProperty) {
        this.selectedProperty = selectedProperty;
    }

    public String getSelectedPropertyMobileSite() {
        if (this.selectedProperty != null && this.selectedProperty.getId() > 0) {
            Company company = this.selectedProperty.getCompany();
            MobileOfferSettings mos = mosMgr.findByBrand(company.getBrand());
            if (company != null && mos != null) {
                return mos.getOfferUrl() + "/" + this.selectedProperty.getWebHash();
            }
        }
        return "";
    }

    public List<DMA> getListDmas() {
        if (listDmas == null) {
            this.populateListDMA();
        }
        return listDmas;
    }

    public void setListDmas(List<DMA> listDmas) {
        this.listDmas = listDmas;
    }

    public DMA getSelectedDma() {
        if (this.selectedDma == null) {
            this.selectedDma = new DMA();
        }
        return selectedDma;
    }

    public void setSelectedDma(DMA selectedDma) {
        this.selectedDma = selectedDma;
    }

    public List<String> getSelectedCountries() {
        if (selectedCountries == null) {
            populateSelectedCountries();
        }
        return selectedCountries;
    }

    public void setSelectedCountries(List<String> selectedCountries) {
        this.selectedCountries = selectedCountries;

    }

    public List<String> getSelectedStates() {
        return selectedStates;
    }

    public void populateSelectedCountries() {
        List<Country> list = countryMgr.findAllSorted();
        selectedCountries = new ArrayList<String>();
        for (Country c : list) {
            selectedCountries.add(c.getName());
        }
    }

    public void setSelectedStates(List<String> selectedStates) {
        this.selectedStates = selectedStates;
    }

    private void populateListDMA() {
        listDmas = DMAMgr.getAllSorted();
    }

    private void populatePropertyModel() {
        List<Property> props = propertyMgr.getPropertiesByCompany(companyMgr.find(getCompanyIdFromSession()));
        propertyModel = new PropertyDataModel(props);
        filteredProperties = new ArrayList<Property>(props);
    }

    private void prepareVars() {
        propertyModel = null;
        newProperty = null;
        selectedProperty = null;
        selectedCountries = null;
        propertyList = null;
        selectedDma = null;
        listDmas = null;
        picklistModel = null;
        picklistSource = null;
        picklistTarget = null;
    }

    public String prepareList() {
        prepareVars();
        return "/property/List?faces-redirect=true";
    }

    public String preparePropertyRetailer() {
        prepareVars();
        return "/property/PropertyRetailer?faces-redirect=true";
    }

    public void createNewProperty(Property property) {

        if (property == null || property.getName() == null || property.getName().isEmpty()) {
            JsfUtil.addErrorMessage(message.getString("propertyNameError"));
            return;
        }
        if (selectedDma == null || selectedDma.getName() == null || selectedDma.getName().isEmpty()) {
            JsfUtil.addErrorMessage(message.getString("propertyDmaError"));
            return;
        }
        if (property == null || property.getAddress() == null || property.getAddress().isEmpty()) {
            JsfUtil.addErrorMessage(message.getString("propertyAddressError"));
            return;
        }

        if (property.getAddress().length() > Property.PROPERTY_ADDRESS_LIMIT) {
            JsfUtil.addErrorMessage(message.getString("propertyAddressLengthError"));
            return;
        }
        if (property == null || property.getCity() == null || property.getCity().isEmpty()) {
            JsfUtil.addErrorMessage(message.getString("propertyCityError"));
            JsfUtil.addErrorMessage("Please enter a city.");
            return;
        }
        if (property == null || property.getZipcode() == null || property.getZipcode().isEmpty()) {
            JsfUtil.addErrorMessage(message.getString("propertyZipcodeError"));
            return;
        }
        List<String> propertyNames = new ArrayList<String>();
        if (propertyModel.getPropertyData() != null && !propertyModel.getPropertyData().isEmpty()) {
            for (Property p : propertyModel.getPropertyData()) {
                propertyNames.add(p.getName().toLowerCase());
            }


            if (propertyNames.contains(property.getName().toLowerCase())) {
                JsfUtil.addErrorMessage(message.getString("propertyDuplicatedError"));
                return;
            }

        }

        Country realCountry = countryMgr.findByName(property.getCountry().getName());
        property.setCountry(realCountry);

        Company company = companyMgr.find(this.getCompanyIdFromSession());
        property.setCompany(company);
        if (selectedDma != null || !selectedDma.getName().isEmpty()) {
            DMA realDMA = DMAMgr.getDMAByName(selectedDma.getName());
            property.setDma(realDMA);
        }
        property.setDateCreated(new Date());
        propertyMgr.create(property);
        createPropertyGeoFence(property);
        property.createWebHash();
        propertyMgr.update(property);
        JsfUtil.addSuccessMessage(message.getString("propertyCreated"));
        prepareList();

    }

    public boolean editProperty() {
        try {
            if (selectedProperty.getName() == null || selectedProperty.getName().isEmpty()) {
                JsfUtil.addErrorMessage(message.getString("propertyNameError"));
                prepareList();
                return false;
            }
            if (selectedProperty.getAddress().length() > Property.PROPERTY_ADDRESS_LIMIT) {
                JsfUtil.addErrorMessage(message.getString("propertyAddressLengthError"));
                prepareList();
                return false;
            }
            propertyMgr.update(selectedProperty);
            createPropertyGeoFence(selectedProperty);
            prepareList();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(message.getString("propertyPersistError"));
            prepareList();
            return false;
        }
        return true;
    }

    public void deleteProperty() {
        if (selectedProperty == null) {
            return;
        }

        if (selectedProperty.getRetailers() != null && !selectedProperty.getRetailers().isEmpty()) {
            String ret = selectedProperty.getRetailers().size() == 1 ? message.getString("retailer") : message.getString("retailers");
            String msg = message.getString("ErrorDelete") + " " + selectedProperty.getName() + " " + message.getString("ErrorDeleteTwo") + " " + selectedProperty.getRetailers().size() + " " + ret;
            JsfUtil.addErrorMessage(msg);
            prepareList();
            return;

        }
        try {
            deletePropertyGeoFence(selectedProperty);
            propertyMgr.delete(selectedProperty);
        } catch (Exception e) {
            JsfUtil.addErrorMessage(message.getString("propertyDeleteError") + ": " + selectedProperty.getName() + ".\n" + message.getString("propertyDmaAssociatedDeleteError"));
        }
        JsfUtil.addSuccessMessage(message.getString("propertyDeleteSuccess"));
        prepareList();
    }

    public List<String> listOfRetailers() {
        List<String> result = getPicklistModel().getTarget();
        Collections.sort(result);
        return result;
    }

    public DualListModel<String> getPicklistModel() {
        if (this.picklistModel == null) {
            populatePicklist();
        }
        return picklistModel;
    }

    public void setPicklistModel(DualListModel<String> picklistModel) {
        this.picklistModel = picklistModel;
    }

    public List<String> getPicklistSource() {
        if (this.picklistSource == null) {
            List<Retailer> allByCompany = retailerMgr.getAllRetailersByBrand(getCompany().getBrand());
            if (allByCompany != null) {
                this.picklistSource = new ArrayList<String>();
                for (Retailer r : allByCompany) {
                    this.picklistSource.add(r.getName());
                }
                Iterator<String> it = this.picklistSource.iterator();
                while (it.hasNext()) {
                    String value = it.next();
                    if (getPicklistTarget().contains(value)) {
                        it.remove();
                    }
                }

            }
        }
        if (picklistSource != null) {
            Collections.sort(picklistSource);
        }
        return picklistSource;
    }

    public void setPicklistSource(List<String> picklistSource) {
        this.picklistSource = picklistSource;
    }

    public List<String> getPicklistTarget() {
        if (picklistTarget == null) {
            picklistTarget = new ArrayList<String>();
        }
        return picklistTarget;
    }

    public void setPicklistTarget(List<String> picklistTarget) {
        this.picklistTarget = picklistTarget;
    }

    private void populatePicklist() {
        setPicklistSource(null);
        this.picklistModel = new DualListModel<String>(getPicklistSource(), getPicklistTarget());
    }

    public Company getCompany() {
        return this.companyMgr.find(getCompanyIdFromSession());
    }

    public List<Property> getPropertyList() {
        if (propertyList == null) {
            this.populatePropertyList();
        }
        return propertyList;
    }

    public void setPropertyList(List<Property> propertyList) {
        this.propertyList = propertyList;
    }

    public void populatePropertyList() {
        selectedProperty = null;
        propertyList = propertyMgr.getPropertiesByCompany(getCompany());
    }

    public void recreatePickList() {
        /**
         * Get Categories
         */
        Property p = propertyMgr.getPropertyByCompanyAndName(getCompany(), selectedProperty.getName());
        if (p != null) {
            this.picklistSource = null;
            this.picklistTarget = new ArrayList<String>();
            List<Retailer> retailers = p.getRetailers();
            if (retailers != null) {
                for (Retailer r : retailers) {
                    this.picklistTarget.add(r.getName());
                }
            }
        }
        populatePicklist();

    }

    public void savePropertyRetailer() {
        /**
         * Persist Categories
         */
        Property realProperty = propertyMgr.getPropertyByCompanyAndName(getCompany(), selectedProperty.getName());
        List<Retailer> targetRetailers = getTargetRetailers();
        realProperty.clearRetailers();
        propertyMgr.update(realProperty);

        if (targetRetailers != null) {
            for (Retailer r : targetRetailers) {
                r.clearProperties();
                realProperty.addRetailers(r);
                r.addProperty(realProperty);
                propertyMgr.update(realProperty);
                retailerMgr.update(r);
            }
        }
        JsfUtil.addSuccessMessage(message.getString("propertyAssignedSuccess") + ": " + realProperty.getName());
    }

    private List<Retailer> getTargetRetailers() {
        return getRetailersFromNames(getPicklistTarget());
    }

    private List<Retailer> getRetailersFromNames(List<String> names) {
        List<Retailer> result = new ArrayList<Retailer>();
        Company c = getCompany();
        for (String retailerName : names) {
            Retailer retailer = retailerMgr.getRetailerByName(c.getBrand(), retailerName);
            if (retailer != null) {
                result.add(retailer);
            }
        }
        return result;
    }

    public boolean isEditUI() {
        return editUI;
    }

    public void setEditUI(boolean editUI) {
        this.editUI = editUI;
    }

    public void toggleEditUI() {
        this.editUI = !this.editUI;
    }

    public String toggleLabel() {
        if (this.editUI) {
            return message.getString("propertyReadOnlyLabel");

        } else {
            return message.getString("propertyAddRemoveLabel");
        }

    }

    public void fixWebHashes() {
        List<Property> props = propertyMgr.findAll();
        for (Property p : props) {
            p.createWebHash();
            propertyMgr.update(p);
        }
    }

    private void createPropertyGeoFence(Property property) {
        GeoPoint tempGeoPoint;
        String propertyAddress = property.getFormattedAddress();
        GeocodeResponse geocodeAddress = GeoUtil.GeocodeAddress(propertyAddress);
        if (geocodeAddress != null) {
            List<Result> results = geocodeAddress.getResults();
            if (results != null && results.size() > 0) {
                Geometry geom = results.get(0).getGeometry();
                if (geom != null) {
                    GeoPoint newGeoPoint = geoPointMgr.findGeoPointByLocation(geom.getLocation().getLat(), geom.getLocation().getLng());
                    List<GeoFence> propertyGeoFences = geoFenceMgr.findAllByProperty(property);
                    if (newGeoPoint == null || propertyGeoFences == null) {
                        GeoFence geoFence = new GeoFence(property.getName(), property);
                        geoFence.setType(GeoFence.CIRCLE);
                        geoFence.setCompany(companyMgr.find(this.getCompanyIdFromSession()));
                        geoFenceMgr.create(geoFence);
                        geoFence.setPriority(geoFence.getId());
                        geoFenceMgr.update(geoFence);
                        tempGeoPoint = new GeoPoint();
                        tempGeoPoint.setLatLng(geom.getLocation().getLat(), geom.getLocation().getLng());
                        tempGeoPoint.setGeoFence(geoFence);
                        tempGeoPoint.setRadius(3.0);
                        tempGeoPoint.setPropertyPoint(true);
                        geoPointMgr.create(tempGeoPoint);
                    }
                }
            } else {
                logger.fatal("No geocode response were found for this address: " + propertyAddress);
            }
        }
    }

    private void deletePropertyGeoFence(Property property) {
        List<GeoFence> propertyGeofences = geoFenceMgr.findAllByProperty(property);
        if (propertyGeofences != null && propertyGeofences.size() > 0) {
            for (GeoFence geoFence : propertyGeofences) {
                for (GeoPoint point : geoFence.getGeoPoints()) {
                    geoPointMgr.delete(point);
                }
                geoFenceMgr.delete(geoFence);
            }
        }
    }
}
