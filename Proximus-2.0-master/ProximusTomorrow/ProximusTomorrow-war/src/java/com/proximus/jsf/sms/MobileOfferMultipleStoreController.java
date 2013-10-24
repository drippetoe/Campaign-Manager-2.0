/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.sms;

import com.proximus.data.Company;
import com.proximus.data.DayParts;
import com.proximus.data.User;
import com.proximus.data.events.MobileOfferEvent;
import com.proximus.data.sms.*;
import com.proximus.data.web.WebOffer;
import com.proximus.helper.util.JsfUtil;
import com.proximus.util.LanguageSelector;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.*;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;

/**
 *
 * @author Gilberto Gaxiola
 */
@ManagedBean(name = "mobileOfferMultipleStoreController")
@SessionScoped
public class MobileOfferMultipleStoreController extends MobileOfferAbstractController implements Serializable {

    private static final Logger logger = Logger.getLogger(MobileOfferController.class.getName());
    private static final long serialVersionUID = 1;
    private List<Retailer> retailerList;
    private MobileOfferAbstractController parentController;
    private DualListModel<String> picklistPropertyModel;
    private List<String> picklistPropertySource;
    private List<String> picklistPropertyTarget;

    public MobileOfferMultipleStoreController() {
        viewMode = VIEW_MODE_ACTIVE;
        message = this.getHttpSession().getMessages();
    }

    public List<Retailer> getRetailerList() {
        if (retailerList == null) {
            this.populateRetailerList();
        }
        return retailerList;
    }

    public void setRetailerList(List<Retailer> retailerList) {
        this.retailerList = retailerList;
    }

    public String prepareEdit(MobileOffer mobileOffer) {
        this.selectedMobileOffer = mobileOffer;
        if (selectedMobileOffer == null || selectedMobileOffer.getId() == 0) {
            System.err.println("Weird behavior on Edit The SelectedMobileOffer was Null or its Id was zero.. check it out");
            return "";
        }
        if (this.selectedMobileOffer.isRetailOnly()) {
            System.err.println("Can't Edit Retail Only Store on Multiple Store Controller...");
            return "";
        } else {
            this.mode = this.MULTIPLE_STORES;
        }
        inListMode = false;
        mos = null;
        mos = getMos();
        if (mos != null && mos.getShortCode() != null) {
            shortCode = mos.getShortCode();
        }

        this.createdMobileOffer = mobileOfferMgr.getMobileOfferbyId(selectedMobileOffer.getId());
        isRetailOnly = this.createdMobileOffer.isRetailOnly();
        retailerWideOrStoreSpecificCheck = "0";
        visibleParts = new ArrayList<DayParts>();
        removedParts = new ArrayList<DayParts>();

        if (this.createdMobileOffer.getDayParts() != null && !this.createdMobileOffer.getDayParts().isEmpty()) {
            this.overwriteDayParts = true;
            for (DayParts r : this.createdMobileOffer.getDayParts()) {
                r.initialize();
                visibleParts.add(r);
            }
        } else {
            this.overwriteDayParts = false;
        }

        this.selectedRetailer = this.createdMobileOffer.getRetailer();
        //Get Properties Picklist
        this.picklistPropertyModel = null;
        this.picklistPropertySource = null;
        this.picklistPropertyTarget = new ArrayList<String>();
        List<Property> properties = this.createdMobileOffer.getProperties();
        if (properties != null) {
            for (Property p : properties) {
                this.picklistPropertyTarget.add(p.getName());
            }
        }
        this.populatePicklistProperty();
        propertyList = null;

        try {
            selectedLanguage = LanguageSelector.getSelectorForLanguage(this.createdMobileOffer.getLocale().getLanguageCode());
        } catch (UnsupportedEncodingException ex) {
            logger.fatal(ex);
        }
        populateRetailerList();
        this.setupMacros(createdMobileOffer);

        /**
         * Get Categories
         */
        this.picklistSource = null;
        this.picklistTarget = new ArrayList<String>();
        List<Category> categories = this.createdMobileOffer.getCategories();
        if (categories != null) {
            for (Category category : categories) {
                this.picklistTarget.add(category.getName());
            }
        }
        populatePicklist();
        return "/mobile-offer/OfferMult?faces-redirect=true";

    }

    @Override
    public String prepareCreate() {
        this.mode = this.MULTIPLE_STORES;
        MobileOfferSettings settings = mosMgr.findByBrand(getCompany().getBrand());
        if (settings != null && settings.getShortCode() != null) {
            shortCode = settings.getShortCode();
        }
        inListMode = false;

        createdMobileOffer = new MobileOffer();
        createdMobileOffer.setRetailOnly(false);
        createdMobileOffer.setOfferType(this.mode);
        retailerWideOrStoreSpecificCheck = "0";
        isRetailOnly = false;

        if (!mode.equalsIgnoreCase(this.MULTIPLE_STORES)) {
            return "";
        }
        selectedProperty = new Property();
        propertyList = null;
        retailerList = null;
        selectedRetailer = null;
        mos = null;
        overwriteDayParts = false;
        macrosMap = new HashMap<String, String>();
        //Setting the macros
        macrosMap.put("store", "no_store");
        macrosMap.put("property", "no_property");
        macrosMap.put("propertyAddress", "no_property_address");
        macrosMap.put("dma", "no_dma");
        macrosMap.put("shortCode", "no_shortCode");
        macrosMap.put("city", "no_city");
        macrosMap.put("state", "no_state");
        macrosMap.put("country", "no_country");

        picklistModel = null;
        picklistSource = null;
        picklistTarget = null;

        picklistPropertyModel = null;
        picklistPropertySource = null;
        picklistPropertyTarget = null;

        visibleParts = new ArrayList<DayParts>();
        removedParts = new ArrayList<DayParts>();

        try {
            selectedLanguage = LanguageSelector.getSelectorForLanguage(this.getHttpSession().getLocale().getLanguage());
        } catch (UnsupportedEncodingException ex) {
            logger.fatal(ex);
        }
        return "/mobile-offer/OfferMult?faces-redirect=true";
    }

    private void createMacro(RequestContext context, String macro, String macroKeyword) {
        if (macro.contains("&")) {
            macro = macro.replaceAll("&", "&amp;");
        }
        String textMacro = "&lt;input value=\"" + macro + "\" macro=\"" + macroKeyword + "\" size=\"" + macro.length() + "\" disabled=\"true\" style=\"display:inline;\"/&gt;";
        textMacro = textMacro.trim();
        context.addCallbackParam("macro", textMacro);
    }

    public void macroStore(ActionEvent actionEvent) {
        RequestContext context = RequestContext.getCurrentInstance();
        if (macrosMap.containsKey("store")) {
            createMacro(context, macrosMap.get("store"), "store");
        }
    }

    public void macroProperty(ActionEvent actionEvent) {
        RequestContext context = RequestContext.getCurrentInstance();
        if (macrosMap.containsKey("property")) {
            createMacro(context, macrosMap.get("property"), "property");
        }

    }

    public void macroPropertyAddress(ActionEvent actionEvent) {
        RequestContext context = RequestContext.getCurrentInstance();
        if (macrosMap.containsKey("propertyAddress")) {
            createMacro(context, macrosMap.get("propertyAddress"), "propertyAddress");
        }
    }

    public void macroShortcode(ActionEvent actionEvent) {
        RequestContext context = RequestContext.getCurrentInstance();
        if (macrosMap.containsKey("shortCode")) {
            createMacro(context, macrosMap.get("shortCode"), "shortCode");
        }
    }

    public String save() {
        boolean newOffer = false;
        RequestContext context = RequestContext.getCurrentInstance();
        if (this.createdMobileOffer.getName() == null || this.createdMobileOffer.getName().isEmpty()) {

            JsfUtil.addErrorMessage(message.getString("noNameMobileOfferErrorMessage"));
            if (context != null) {
                context.addCallbackParam("error", true);
            }
            return null;
        }

        if (!bannedWordMgr.approve(this.getCompany(), createdMobileOffer)) {
            JsfUtil.addErrorMessage(message.getString("bannedWordErrorMessage"));
            if (context != null) {
                context.addCallbackParam("error", true);
            }
            return null;
        }

        //Overwrite or not We removed the Removed Day Parts from Database

        //Deleting from DB dayparts removed on UI (that were already persisted)
        Iterator<DayParts> iter = this.getRemovedParts().iterator();
        while (iter.hasNext()) {
            DayParts r = iter.next();
            r.setMobile_offer_settings(null);
            r.setMobileOffer(null);
            iter.remove();
            dayPartsMgr.delete(r);
        }

        //Only if overwriteDayParts we add the new DayParts
        if (this.overwriteDayParts) {
            //Updating the Day Parts chosen from UI
            iter = this.getVisibleParts().iterator();
            while (iter.hasNext()) {
                DayParts r = iter.next();
                r.update();
                r.setMobile_offer_settings(null);
                r.setMobileOffer(this.createdMobileOffer);
                if (r.getSelectedDaysOfWeek().isEmpty()) {
                    r.setDaysOfWeek("M,T,W,R,F,S,U"); //if nothing selected default to All days
                }
            }
            this.createdMobileOffer.setDayParts(visibleParts);
        } else {

            iter = this.createdMobileOffer.getDayParts().iterator();
            while (iter.hasNext()) {
                DayParts r = iter.next();
                r.setMobile_offer_settings(null);
                r.setMobileOffer(null);
                iter.remove();
                dayPartsMgr.delete(r);
            }
            this.createdMobileOffer.setDayParts(null);
        }

        /**
         * Persisting mobileoffer
         */
        this.createdMobileOffer.setRetailOnly(false);

        Retailer realRetailer = retailerMgr.getRetailerByName(getCompany().getBrand(), this.selectedRetailer.getName());
        this.createdMobileOffer.setRetailer(realRetailer);
        this.createdMobileOffer.setCleanOfferText(StringEscapeUtils.unescapeHtml4(this.getCleanOfferFrom(this.createdMobileOffer)));


        if (this.createdMobileOffer.getId() == 0) {
            this.createdMobileOffer.setBrand(selectedCompanyToAddOffer.getBrand());
            this.createdMobileOffer.setCompany(selectedCompanyToAddOffer);

            mobileOfferMgr.create(this.createdMobileOffer);
            /*
             * MobileOfferEvent Trigger
             */
            newOffer = true;//for tirgger later
            User currentUser = userMgr.find(getUserIdFromSession());
            MobileOfferEvent moe = new MobileOfferEvent(MobileOfferEvent.TYPE_CREATED, this.createdMobileOffer, getCompany(), currentUser);
            eventMgr.update(moe);
            /*END TRIGGER*/
        }
        this.createdMobileOffer.setStatus(MobileOffer.PENDING);
        this.createdMobileOffer.setLastModified(new Date());
        this.createdMobileOffer.setLocale(localeMgr.getLocaleByLanguageCode(selectedLanguage.getValue()));
        mobileOfferMgr.update(this.createdMobileOffer);

        /**
         * Persist Properties
         */
        List<Property> targetProperties = getTargetProperties();
        this.createdMobileOffer.clearProperties();
        mobileOfferMgr.update(this.createdMobileOffer);
        if (targetProperties != null) {
            for (Property p : targetProperties) {
                p.removeMobileOffer(createdMobileOffer);
                this.createdMobileOffer.addProperty(p);
                mobileOfferMgr.update(this.createdMobileOffer);
                propertyMgr.update(p);
            }
        }

        //Cleaning the Offer
        this.createdMobileOffer.setCleanOfferText(StringEscapeUtils.unescapeHtml4(this.getCleanOfferFrom(this.createdMobileOffer)));

        /**
         * Persist Categories
         */
        List<Category> targetCategories = getTargetCategories();
        this.createdMobileOffer.clearCategories();
        mobileOfferMgr.update(this.createdMobileOffer);
        if (targetCategories != null) {
            for (Category category : targetCategories) {
                category.removeMobileOffer(this.createdMobileOffer);
                this.createdMobileOffer.addCategory(category);
                category.addMobileOffer(this.createdMobileOffer);
                mobileOfferMgr.update(this.createdMobileOffer);
                categoryMgr.update(category);
            }
        }

        this.createdMobileOffer.setEventCreated(new Date());
        mobileOfferMgr.update(this.createdMobileOffer);
        /*
         * MobileOfferEvent Trigger
         */
        if (!newOffer) {
            User currentUser = userMgr.find(getUserIdFromSession());
            MobileOfferEvent moe = new MobileOfferEvent(MobileOfferEvent.TYPE_UPDATED, this.createdMobileOffer, getCompany(), currentUser);
            eventMgr.update(moe);
        }
        /*END TRIGGER*/

        WebOffer webOffer = this.getSiblingWebOffer(this.createdMobileOffer);
        if (webOffer == null) {
            webOffer = new WebOffer();

        }
        webOffer.createWebOfferFromMobileOffer(createdMobileOffer);
        webOfferMgr.update(webOffer);

        if (getParentController() != null) {
            return getParentController().prepareList("");
        } else {
            JsfUtil.addErrorMessage("Invalid Session Scope Managed Bean AbstractController couldn't be injected");
            return null;
        }

    }

    private MobileOfferAbstractController getParentController() {
        if (parentController == null) {
            parentController = FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{mobileOfferAbstractController}", MobileOfferAbstractController.class);
        }
        return parentController;
    }

    public void recreateRetailers() {
        populateRetailerList();
        recreatePropertyPickList();
    }

    public void populateRetailerList() {
        retailerList = retailerMgr.getAllRetailersByBrand(selectedCompanyToAddOffer.getBrand());
        Collections.sort(retailerList);
    }

    public String deleteMobileOffer() {
        if (this.selectedMobileOffer != null) {
            this.selectedMobileOffer.setDeleted(true);
            this.selectedMobileOffer.setStatus(MobileOffer.DELETED);
            this.mobileOfferMgr.update(this.selectedMobileOffer);
        }
        this.selectedMobileOffer = null;
        return prepareList("");
    }

    public String cloneMobileOffer(MobileOffer offerToClone) {
        this.createdMobileOffer = offerToClone.cloneMyData();
        this.mode = this.MULTIPLE_STORES;
        inListMode = false;
        mos = null;
        mos = getMos();
        if (mos != null && mos.getShortCode() != null) {
            shortCode = mos.getShortCode();
        }
        isRetailOnly = this.createdMobileOffer.isRetailOnly();
        retailerWideOrStoreSpecificCheck = "0";
        visibleParts = new ArrayList<DayParts>();
        removedParts = new ArrayList<DayParts>();

        if (this.createdMobileOffer.getDayParts() != null && !this.createdMobileOffer.getDayParts().isEmpty()) {
            this.overwriteDayParts = true;
            for (DayParts r : this.createdMobileOffer.getDayParts()) {
                r.initialize();
                visibleParts.add(r);
            }
        } else {
            this.overwriteDayParts = false;
        }

        this.selectedRetailer = this.createdMobileOffer.getRetailer();
        //Get Properties Picklist
        this.picklistPropertyModel = null;
        this.picklistPropertySource = null;
        this.picklistPropertyTarget = new ArrayList<String>();
        List<Property> properties = this.createdMobileOffer.getProperties();
        if (properties != null) {
            for (Property p : properties) {
                this.picklistPropertyTarget.add(p.getName());
            }
        }
        this.populatePicklistProperty();
        propertyList = null;

        try {
            selectedLanguage = LanguageSelector.getSelectorForLanguage(this.createdMobileOffer.getLocale().getLanguageCode());
        } catch (UnsupportedEncodingException ex) {
            logger.fatal(ex);
        }
        populateRetailerList();
        this.setupMacros(createdMobileOffer);

        /**
         * Get Categories
         */
        this.picklistSource = null;
        this.picklistTarget = new ArrayList<String>();
        List<Category> categories = this.createdMobileOffer.getCategories();
        if (categories != null) {
            for (Category category : categories) {
                this.picklistTarget.add(category.getName());
            }
        }
        populatePicklist();
        return "/mobile-offer/OfferMult?faces-redirect=true";

    }

    public DualListModel<String> getPicklistPropertyModel() {
        if (this.picklistPropertyModel == null) {
            populatePicklistProperty();
        }
        return picklistPropertyModel;
    }

    public void setPicklistPropertyModel(DualListModel<String> picklistPropertyModel) {
        this.picklistPropertyModel = picklistPropertyModel;
    }

    protected void populatePicklistProperty() {
        setPicklistPropertySource(null);
        this.picklistPropertyModel = new DualListModel<String>(getPicklistPropertySource(), getPicklistPropertyTarget());
    }

    public List<String> getPicklistPropertySource() {
        if (this.picklistPropertySource == null || this.picklistPropertySource.isEmpty()) {
            List<Property> properties;
            properties = propertyMgr.getPropertiesByCompanyAndRetailer(selectedCompanyToAddOffer, selectedRetailer);
            if (properties != null) {
                this.picklistPropertySource = new ArrayList<String>();
                for (Property p : properties) {
                    this.picklistPropertySource.add(p.getName());
                }
                Iterator<String> it = this.picklistPropertySource.iterator();
                while (it.hasNext()) {
                    String value = it.next();
                    if (getPicklistPropertyTarget().contains(value)) {
                        it.remove();
                    }
                }

            } else {
                picklistPropertySource = new ArrayList<String>();
            }
        }

        return picklistPropertySource;
    }

    public void setPicklistPropertySource(List<String> picklistPropertySource) {
        this.picklistPropertySource = picklistPropertySource;
    }

    public List<String> getPicklistPropertyTarget() {
        if (picklistPropertyTarget == null) {
            picklistPropertyTarget = new ArrayList<String>();
        }
        return picklistPropertyTarget;
    }

    public void setPicklistPropertyTarget(List<String> picklistPropertyTarget) {
        this.picklistPropertyTarget = picklistPropertyTarget;
    }

    protected List<Property> getTargetProperties() {
        return getPropertiesFromNames(getPicklistPropertyTarget());
    }

    protected List<Property> getPropertiesFromNames(List<String> names) {
        List<Property> result = new ArrayList<Property>();
        Company c = getCompany();
        for (String propertyName : names) {
            Property property = propertyMgr.getPropertyByCompanyAndName(c, propertyName);
            if (property != null) {
                result.add(property);
            }
        }
        return result;
    }

    public void recreatePropertyPickList() {

        if (selectedRetailer != null) {
            selectedRetailer = retailerMgr.getRetailerByName(getCompany().getBrand(), selectedRetailer.getName());
            this.picklistPropertySource = null;
            this.picklistPropertyTarget = new ArrayList<String>();
        }
        populatePicklistProperty();


    }

    @Override
    public boolean showSMS() {
        if (selectedRetailer == null || selectedRetailer.getName() == null || selectedRetailer.getName().isEmpty()) {
            return false;
        }
        if ((this.picklistPropertyTarget == null || this.picklistPropertyTarget.isEmpty())) {
            return false;
        }
        return true;

    }
}
