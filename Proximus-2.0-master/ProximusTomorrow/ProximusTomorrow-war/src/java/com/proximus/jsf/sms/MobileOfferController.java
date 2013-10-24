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
import com.proximus.jsf.LoginController;
import com.proximus.util.LanguageSelector;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.Collator;
import java.util.*;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

/**
 * This specific implementation deals with National Retailer and with Specific
 * Store
 *
 * @author Gilberto Gaxiola
 */
@ManagedBean(name = "mobileOfferController")
@SessionScoped
public class MobileOfferController extends MobileOfferAbstractController implements Serializable {

    private static final Logger logger = Logger.getLogger(MobileOfferController.class.getName());
    private static final long serialVersionUID = 1;
    private List<DMA> dmaList;
    private DMA selectedDma;
    private List<Retailer> retailerList;
    private MobileOfferAbstractController parentController;

    public MobileOfferController() {
        viewMode = VIEW_MODE_ACTIVE;
        message = this.getHttpSession().getMessages();
    }

    public List<DMA> getDmaList() {
        if (dmaList == null) {
            this.populateDMAList();
        }
        return dmaList;
    }

    public void setDmaList(List<DMA> dmaList) {
        this.dmaList = dmaList;
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

    public DMA getSelectedDma() {
        if (this.selectedDma == null) {
            this.selectedDma = new DMA();
        }
        return selectedDma;
    }

    public void setSelectedDma(DMA selectedDma) {
        this.selectedDma = selectedDma;
    }

    public String prepareEdit(MobileOffer mobileOffer) {
        this.selectedMobileOffer = mobileOffer;
        if (selectedMobileOffer == null || selectedMobileOffer.getId() == 0) {
            System.err.println("Weird behavior on Edit The SelectedMobileOffer was Null or its Id was zero.. check it out");
        }
        //Making sure Mobile Offer Settings is the most recent prior to editing
        if (this.selectedMobileOffer.isRetailOnly()) {
            this.mode = this.RETAIL_WIDE;
        } else {
            this.mode = this.SINGLE_STORE;
        }
        inListMode = false;
        mos = null;
        mos = getMos();
        if (mos != null && mos.getShortCode() != null) {
            shortCode = mos.getShortCode();
        }

        this.createdMobileOffer = mobileOfferMgr.getMobileOfferbyId(selectedMobileOffer.getId());
        isRetailOnly = this.createdMobileOffer.isRetailOnly();
        if (isRetailOnly) {
            retailerWideOrStoreSpecificCheck = "1";
        }
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
        // Is Reatail Only
        if (isRetailOnly) {
            selectedProperty = new Property();
            selectedDma = null;
            dmaList = null;
            propertyList = null;
            selectedRetailer = this.createdMobileOffer.getRetailer();
            //Has a Specific Retailer
        } else {
            if (this.createdMobileOffer.getProperties().isEmpty()) {
                selectedProperty = null;
            } else if (this.createdMobileOffer.getProperties().size() > 1) {
                JsfUtil.addErrorMessage("Can't Edit a Mobile Offer with Multiple Properties on Retail Wide or Single Store Mode");
                return "";
            } else {
                selectedProperty = this.createdMobileOffer.getProperties().get(0);
            }

            selectedDma = selectedProperty.getDma();
            dmaList = dmaMgr.getAllSorted();
            propertyList = propertyMgr.getPropertiesByCompanyDMA(getCompany(), selectedDma);
            selectedRetailer = this.createdMobileOffer.getRetailer();
        }
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
        return "/mobile-offer/Offer?faces-redirect=true";

    }

    public String prepareCreate(String mode) {
        this.mode = mode;
        MobileOfferSettings settings = mosMgr.findByBrand(getCompany().getBrand());
        if (settings != null && settings.getShortCode() != null) {
            shortCode = settings.getShortCode();
        }
        inListMode = false;

        createdMobileOffer = new MobileOffer();
        createdMobileOffer.setOfferType(this.mode);


        if (mode.equalsIgnoreCase(this.RETAIL_WIDE)) {
            retailerWideOrStoreSpecificCheck = "1";
            isRetailOnly = true;
            createdMobileOffer.setRetailOnly(true);
        } else {
            retailerWideOrStoreSpecificCheck = "0";
            isRetailOnly = false;
            createdMobileOffer.setRetailOnly(false);
        }


        selectedProperty = new Property();
        propertyList = null;

        selectedDma = null;
        dmaList = null;
        retailerList = null;
        selectedRetailer = null;
        mos = null;
        overwriteDayParts = false;
        macrosMap = new HashMap<String, String>();
        //Setting the macros
        macrosMap.put("store", "no_store");
        macrosMap.put("property", "no_property");
        macrosMap.put("propertyAddress", "no_property_address");
        macrosMap.put("shortCode", "no_shortCode");


        picklistModel = null;
        visibleParts = new ArrayList<DayParts>();
        removedParts = new ArrayList<DayParts>();
        picklistSource = null;
        picklistTarget = null;
        try {
            selectedLanguage = LanguageSelector.getSelectorForLanguage(this.getHttpSession().getLocale().getLanguage());
        } catch (UnsupportedEncodingException ex) {
            logger.fatal(ex);
        }
        return "/mobile-offer/Offer?faces-redirect=true";
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

        if (this.createdMobileOffer.getPassbookSubheader() != null && !this.createdMobileOffer.getPassbookSubheader().isEmpty() && this.createdMobileOffer.getPassbookSubheader().length() > 46) {
            JsfUtil.addErrorMessage(message.getString("passbookSubheaderErrorMessage"));
            if (context != null) {
                context.addCallbackParam("error", true);
            }
            return null;

        }

        if (this.createdMobileOffer.getPassbookDetails() != null && !this.createdMobileOffer.getPassbookDetails().isEmpty() && this.createdMobileOffer.getPassbookDetails().length() > 36) {
            JsfUtil.addErrorMessage(message.getString("passbookDetailsErrorMessage"));
            if (context != null) {
                context.addCallbackParam("error", true);
            }
            return null;

        }

        if (this.createdMobileOffer.getPassbookHeader() != null && !this.createdMobileOffer.getPassbookHeader().isEmpty() && this.createdMobileOffer.getPassbookHeader().length() > 7) {
            JsfUtil.addErrorMessage(message.getString("passbookHeaderErrorMessage"));
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
        this.createdMobileOffer.setRetailOnly(isRetailOnly);

        Retailer realRetailer = retailerMgr.getRetailerByName(getCompany().getBrand(), this.selectedRetailer.getName());
        this.createdMobileOffer.setRetailer(realRetailer);

        if (this.createdMobileOffer.getId() == 0) {
            this.createdMobileOffer.setBrand(getCompany().getBrand());
            this.createdMobileOffer.setCompany(isRetailOnly ? null : getCompany());

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
         * Persist Property
         */
        Property realProperty = null;
        if (!isRetailOnly) {
            realProperty = propertyMgr.getPropertyByCompanyAndName(getCompany(), selectedProperty.getName());
            this.createdMobileOffer.setProperties(new ArrayList<Property>());
            this.createdMobileOffer.addProperty(realProperty);
            realProperty.removeMobileOffer(this.createdMobileOffer);
            realProperty.addMobileOffer(this.createdMobileOffer);
            mobileOfferMgr.update(this.createdMobileOffer);
            propertyMgr.update(realProperty);
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
                category.clearMobileOffers();
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

    public void populateDMAList() {
        Company c = getCompany();
        this.selectedDma = null;
        this.selectedProperty = null;
        this.selectedRetailer = null;
        picklistSource = null;
        picklistTarget = null;
        populatePicklist();
        if (c == null) {
            this.dmaList = null;
        } else {

            List<Long> dmaIds = propertyMgr.getDMAIdsByCompany(c);
            if (dmaIds == null) {
                this.dmaList = null;
                return;
            }
            DMA tempDMA;
            List<DMA> tempDmaList = new ArrayList<DMA>();
            for (Long value : dmaIds) {
                tempDMA = dmaMgr.find(value);
                tempDmaList.add(tempDMA);
            }
            Collections.sort(tempDmaList, new Comparator<DMA>() {
                @Override
                public int compare(DMA dma1, DMA dma2) {
                    return dma1.getName().compareTo(dma2.getName());
                }
            });
            this.dmaList = tempDmaList;
        }
    }

    public void populateRetailerList() {
        if (selectedMobileOffer != null) {
            Company company = selectedMobileOffer.getCompany();
            setCompany(company);
        }
        if (isRetailOnly) {
            selectedDma = null;
            selectedProperty = null;
            retailerList = retailerMgr.getAllRetailersByBrand(getCompany().getBrand());

        } else {
            retailerList = new ArrayList<Retailer>();
            if (selectedProperty != null && selectedProperty.getName() != null && !selectedProperty.getName().isEmpty()) {
                Property p = propertyMgr.getPropertyByCompanyAndName(getCompany(), selectedProperty.getName());
                //Had to do this long process because Collections.sort did not work for some reason
                Collection<String> retailerNames = new TreeSet<String>(Collator.getInstance());
                List<Retailer> retailers = retailerMgr.getRetailersByProperty(p);
                if (retailers != null && !retailers.isEmpty()) {
                    for (Retailer retailer : retailers) {
                        retailerNames.add(retailer.getName());
                    }
                    for (String name : retailerNames) {
                        retailerList.add(retailerMgr.getRetailerByName(getCompany().getBrand(), name));
                    }
                }
            } else {
                selectedRetailer = null;
            }


        }
        if (retailerList == null) {
            retailerList = new ArrayList<Retailer>();
        }
    }

    public String cloneMobileOffer(MobileOffer offerToClone) {
        //Making sure Mobile Offer Settings is the most recent prior to editing
        Company company = offerToClone.getCompany();
        setCompany(company);

        this.createdMobileOffer = offerToClone.cloneMyData();

        if (this.createdMobileOffer.isRetailOnly()) {
            this.mode = this.RETAIL_WIDE;
        } else {
            this.mode = this.SINGLE_STORE;
        }
        inListMode = false;
        mos = null;
        mos = getMos();
        if (mos != null && mos.getShortCode() != null) {
            shortCode = mos.getShortCode();
        }

        isRetailOnly = this.createdMobileOffer.isRetailOnly();
        if (isRetailOnly) {
            retailerWideOrStoreSpecificCheck = "1";
        }
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
        // Is Reatail Only
        if (isRetailOnly) {
            selectedProperty = new Property();
            selectedDma = null;
            dmaList = null;
            propertyList = null;
            selectedRetailer = this.createdMobileOffer.getRetailer();
            //Has a Specific Retailer
        } else {
            if (this.createdMobileOffer.getProperties().isEmpty()) {
                selectedProperty = null;
            } else if (this.createdMobileOffer.getProperties().size() > 1) {
                JsfUtil.addErrorMessage("Can't Edit a Mobile Offer with Multiple Properties on Retail Wide or Single Store Mode");
                return "";
            } else {
                selectedProperty = this.createdMobileOffer.getProperties().get(0);
            }

            selectedDma = selectedProperty.getDma();
            dmaList = dmaMgr.getAllSorted();
            propertyList = propertyMgr.getPropertiesByCompanyDMA(getCompany(), selectedDma);
            selectedRetailer = this.createdMobileOffer.getRetailer();
        }
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
        return "/mobile-offer/Offer?faces-redirect=true";
    }

    @Override
    public void populatePropertyList() {

        LoginController lc = this.getHttpSession();
        ResourceBundle bundle = lc.getBundle();
        if (lc.getPrivileges().contains(bundle.getString("BrandMobileOffer")) || lc.getPrivileges().contains(bundle.getString("CompanyMobileOffer"))) {
            if (inListMode) {
                propertyList = propertyMgr.getPropertiesByCompany(this.getCompanyFromSession());
            } else {
                selectedProperty = null;
                if (selectedDma == null || selectedDma.getName() == null || selectedDma.getName().isEmpty()) {
                    propertyList = null;
                } else {
                    DMA realDma = dmaMgr.getDMAByName(selectedDma.getName());
                    propertyList = propertyMgr.getPropertiesByCompanyDMA(getCompany(), realDma);

                }
            }
        } else if (!lc.getCurrUser().getProperties().isEmpty()) {
            propertyList = lc.getCurrUser().getProperties();
        }



    }
}
