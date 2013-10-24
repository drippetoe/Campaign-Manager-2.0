/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.sms;

import com.kazemobile.types.CampaignExt;
import com.kazemobile.types.CampaignSteps;
import com.proximus.data.Brand;
import com.proximus.data.Company;
import com.proximus.data.DayParts;
import com.proximus.data.User;
import com.proximus.data.events.MobileOfferEvent;
import com.proximus.data.sms.*;
import com.proximus.data.sms.Locale;
import com.proximus.data.web.WebOffer;
import com.proximus.helper.util.JsfUtil;
import com.proximus.jsf.AbstractController;
import com.proximus.jsf.LoginController;
import com.proximus.jsf.datamodel.sms.MobileOfferDataModel;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.DayPartsManagerLocal;
import com.proximus.manager.UserManagerLocal;
import com.proximus.manager.events.EventManagerLocal;
import com.proximus.manager.sms.*;
import com.proximus.manager.web.WebOfferManagerLocal;
import com.proximus.rest.sms.SMSPlatformRESTClient;
import com.proximus.util.LanguageSelector;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DualListModel;

/**
 *
 * @author Gilberto Gaxiola
 */
@ManagedBean(name = "mobileOfferAbstractController")
@SessionScoped
public class MobileOfferAbstractController extends AbstractController {

    @EJB
    protected CompanyManagerLocal companyMgr;
    @EJB
    protected UserManagerLocal userMgr;
    @EJB
    protected RetailerManagerLocal retailerMgr;
    @EJB
    protected MobileOfferManagerLocal mobileOfferMgr;
    @EJB
    protected WebOfferManagerLocal webOfferMgr;
    @EJB
    protected BannedWordManagerLocal bannedWordMgr;
    @EJB
    protected PropertyManagerLocal propertyMgr;
    @EJB
    protected CategoryManagerLocal categoryMgr;
    @EJB
    protected DMAManagerLocal dmaMgr;
    @EJB
    protected SubscriberManagerLocal subscriberMgr;
    @EJB
    protected MobileOfferSettingsManagerLocal mosMgr;
    @EJB
    protected DayPartsManagerLocal dayPartsMgr;
    @EJB
    protected EventManagerLocal eventMgr;
    @EJB
    LocaleManagerLocal localeMgr;
    protected MobileOffer createdMobileOffer;
    protected MobileOffer selectedMobileOffer;
    protected MobileOfferSettings mos;
    protected MobileOfferDataModel mobileOfferModel;
    protected List<MobileOffer> filteredMobileOffers;
    protected static final String NO_HTML_REGEX = "\\<.*?\\>";
    protected static final Pattern pattern = Pattern.compile("(<input value=\".+?\" macro)");
    protected static final Pattern patternReplace = Pattern.compile("(input value=\".+?\")\\s+(macro=\".+?\")\\s+(size=\".+?\")");
    protected final int CHAR_LIMIT = 150; //Allowing for CR and LF and the 7 characters for the Property Encoding ( /es0000 )
    protected Map<String, String> macrosMap;
    protected ShortCode shortCode;
    protected boolean overwriteDayParts = false;
    protected boolean isRetailOnly = false;
    protected List<DayParts> visibleParts;
    protected List<DayParts> removedParts;
    protected String retailerWideOrStoreSpecificCheck;
    protected DualListModel<String> picklistModel;
    protected List<String> picklistSource;
    protected List<String> picklistTarget;
    protected String viewMode;
    protected Company selectedCompany;
    protected Company selectedCompanyToAddOffer;
    protected List<Company> brandCompanies;
    protected String msisdn;
    protected CampaignExt testOfferCampaign;
    protected CampaignSteps testOfferSteps;
    protected String testCampaignMessage;
    protected static final String VIEW_MODE_ACTIVE = "Active";
    protected static final String VIEW_MODE_EXPIRED = "Expired";
    protected static final String VIEW_MODE_ALL = "All";
    protected static final String VIEW_MODE_PENDING_APPROVAL = "Pending Approval";
    protected static final String VIEW_MODE_NOT_EXPIRED = "Not Expired";
    protected LanguageSelector selectedLanguage;
    protected boolean inListMode;
    protected ResourceBundle message;
    protected String mode;
    protected Retailer selectedRetailer;
    protected Property selectedProperty;
    protected List<Property> propertyList;
    protected final String RETAIL_WIDE = "Retail Wide";
    protected final String MULTIPLE_STORES = "Multiple Stores";
    protected final String SINGLE_STORE = "Single Store";
    private String footerMsg;

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public CampaignExt getTestOfferCampaign() {
        return testOfferCampaign;
    }

    public void setTestOfferCampaign(CampaignExt testOfferCampaign) {
        this.testOfferCampaign = testOfferCampaign;
    }

    public CampaignSteps getTestOfferSteps() {
        return testOfferSteps;
    }

    public void setTestOfferSteps(CampaignSteps testOfferSteps) {
        this.testOfferSteps = testOfferSteps;
    }

    public String getTestCampaignMessage() {
        return testCampaignMessage;
    }

    public void setTestCampaignMessage(String testCampaignMessage) {
        this.testCampaignMessage = testCampaignMessage;
    }

    public boolean showSMS() {
        if (selectedRetailer == null || selectedRetailer.getName() == null || selectedRetailer.getName().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean hasCategories() {
        if (getPicklistSource() != null && !getPicklistSource().isEmpty()) {
            return true;
        } else if (getTargetCategories() != null && !getTargetCategories().isEmpty()) {
            return true;
        }
        return false;
    }

    public MobileOffer getCreatedMobileOffer() {
        if (createdMobileOffer == null) {
            createdMobileOffer = new MobileOffer();
        }
        return createdMobileOffer;
    }

    public void setCreatedMobileOffer(MobileOffer createdMobileOffer) {
        this.createdMobileOffer = createdMobileOffer;
    }

    public Retailer getSelectedRetailer() {
        if (this.selectedRetailer == null) {
            this.selectedRetailer = new Retailer();
        }
        return selectedRetailer;
    }

    public void setSelectedRetailer(Retailer selectedRetailer) {
        this.selectedRetailer = selectedRetailer;
    }

    public Property getSelectedProperty() {
        if (this.selectedProperty == null) {
            this.selectedProperty = new Property();
        }
        return selectedProperty;
    }

    public void setSelectedProperty(Property selectedProperty) {
        this.selectedProperty = selectedProperty;
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

        LoginController lc = this.getHttpSession();
        ResourceBundle bundle = lc.getBundle();
        if (lc.getPrivileges().contains(bundle.getString("BrandMobileOffer")) || lc.getPrivileges().contains(bundle.getString("CompanyMobileOffer"))) {
            propertyList = propertyMgr.getPropertiesByCompany(this.getCompanyFromSession());
        } else if (!lc.getCurrUser().getProperties().isEmpty()) {
            propertyList = lc.getCurrUser().getProperties();
        }



    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void clearText() {
        if (this.createdMobileOffer != null && this.createdMobileOffer.getOfferText() != null) {
            this.createdMobileOffer.setOfferText("");
        }
    }

    public String cleanContentForApproval() {

        if (this.selectedMobileOffer == null) {
            return "";
        }


        MobileOfferSettings settings = getMos();

        String header = (settings == null) ? "" : settings.getOfferHeader();

        String footer = this.makeTheFooterMsg(this.selectedMobileOffer.getLocale().getLanguageCode());
        String url = (settings == null) ? "" : settings.getOfferUrl();

        String cleanContent = MobileOfferController.cleanContent(this.selectedMobileOffer.getOfferText());

        header = (header != null && !header.isEmpty()) ? header : "";
        footer = (footer != null && !footer.isEmpty()) ? footer : "";
        url = (url != null && !url.isEmpty()) ? url : "";
        cleanContent = (cleanContent != null && !cleanContent.isEmpty()) ? cleanContent : "";

        header = header.trim();
        footer = footer.trim();
        url = url.trim();
        cleanContent = cleanContent.trim();
        String result = header + " " + cleanContent + " " + footer + " " + url;
        result = StringEscapeUtils.unescapeHtml4(result);
        return result;
    }

    public static String cleanContent(String rawContent) {
        Matcher matcher = pattern.matcher(rawContent);
        StringBuffer sb = new StringBuffer(rawContent.length());
        while (matcher.find()) {
            String text = matcher.group(1);
            text = " " + text.split("\"")[1] + " <";
            matcher.appendReplacement(sb, Matcher.quoteReplacement(text));
        }
        matcher.appendTail(sb);
        String result = sb.toString();
        result = result.replaceAll("&nbsp;", " ");
        result = result.replaceAll("<br/>", " ");
        result = result.replaceAll("<br />", " ");
        result = result.replaceAll(MobileOfferController.NO_HTML_REGEX, "");
        result = result.replaceAll("[ \\t]+", " ");
        result = result.trim();
        return result;
    }

    public void approveMobileOffer() {
        if (this.selectedMobileOffer != null && !this.selectedMobileOffer.getStatus().equalsIgnoreCase(MobileOffer.APPROVED)) {
            this.selectedMobileOffer.setStatus(MobileOffer.APPROVED);
            this.selectedMobileOffer.setEventApproved(new Date());
            /*
             * MobileOfferEvent Trigger
             */
            User currentUser = userMgr.find(getUserIdFromSession());
            MobileOfferEvent moe = new MobileOfferEvent(MobileOfferEvent.TYPE_APPROVED, this.selectedMobileOffer, getCompany(), currentUser);
            eventMgr.update(moe);
            /*END TRIGGER*/
            
            WebOffer webOffer = this.getSiblingWebOffer(selectedMobileOffer);
            if (webOffer == null) {
                webOffer = new WebOffer();
            }
            webOffer.createWebOfferFromMobileOffer(selectedMobileOffer);
            webOfferMgr.update(webOffer);
            
            
            
            
            webOffer.setStatus(this.selectedMobileOffer.getStatus());
            mobileOfferMgr.update(this.selectedMobileOffer);
            webOfferMgr.update(webOffer);
        }
    }

    public MobileOfferSettings getMos() {
        if (mos == null) {
            mos = mosMgr.findByBrand(getCompany().getBrand());
        }
        return mos;
    }

    public int charRemainings() {
        int headerFooterLength = 0;
        MobileOfferSettings settings = getMos();
        if (settings != null) {

            if (settings.getOfferHeader() != null) {
                headerFooterLength = settings.getOfferHeader().length();
            }

            headerFooterLength += getFooterMsg().length();

            if (settings.getOfferUrl() != null) {
                headerFooterLength += settings.getOfferUrl().length();

            }
        }

        if (this.createdMobileOffer != null && this.createdMobileOffer.getOfferText() != null) {
            if (this.createdMobileOffer.getOfferText().isEmpty()) {
                return CHAR_LIMIT - headerFooterLength;
            }
            String clean = cleanContent(this.createdMobileOffer.getOfferText());
            int count = StringUtils.countMatches(clean, Property.ADDRESS_REGEX);
            if (count > 0) {
                int contentLength = clean.length() - (Property.ADDRESS_REGEX.length() * count) + (Property.PROPERTY_ADDRESS_LIMIT * count);
                return CHAR_LIMIT - contentLength - headerFooterLength;

            }
            return CHAR_LIMIT - clean.length() - headerFooterLength;
        }
        return CHAR_LIMIT;
    }

    public Map getMacrosMap() {
        if (macrosMap == null) {
            macrosMap = new HashMap<String, String>();
        }
        return macrosMap;
    }

    public void setMacrosMap(Map macrosMap) {
        this.macrosMap = macrosMap;
    }

    public ShortCode getShortCode() {
        return shortCode;
    }

    public void setShortCode(ShortCode shortCode) {
        this.shortCode = shortCode;
    }

    public MobileOfferDataModel getMobileOfferModel() {
        if (mobileOfferModel == null) {
            populateMobileOfferModel();
        }
        return mobileOfferModel;
    }

    public void setMobileOfferModel(MobileOfferDataModel mobileOfferModel) {
        this.mobileOfferModel = mobileOfferModel;
    }

    public List<MobileOffer> getFilteredMobileOffers() {
        return filteredMobileOffers;
    }

    public void setFilteredMobileOffers(List<MobileOffer> filteredMobileOffers) {
        this.filteredMobileOffers = filteredMobileOffers;
    }

    public boolean populateMobileOfferModel() {
        return changeViewMode();
    }

    /**
     * Add all the companyBased Offers and retailWide Offers to the
     * mobileOfferModel
     *
     * @param companyBased
     * @param retailWide
     * @return
     */
    protected List<MobileOffer> mergeOffers(List<MobileOffer> companyBased, List<MobileOffer> retailWide) {
        List<MobileOffer> result = new ArrayList<MobileOffer>();
        if (companyBased != null) {
            result.addAll(companyBased);
        }
        if (retailWide != null) {
            result.addAll(retailWide);
        }
        return result;

    }

    protected void changeViewModeBrand() {
        if (selectedCompany == null || selectedCompany.getName() == null || selectedCompany.getName().isEmpty()) {
            Brand b = this.getCompany().getBrand();
            List<Company> companies = companyMgr.findAllCompaniesOfBrand(b);
            if (VIEW_MODE_ACTIVE.equalsIgnoreCase(viewMode)) {
                mobileOfferModel = new MobileOfferDataModel(mergeOffers(mobileOfferMgr.findActiveByCompanies(companies), mobileOfferMgr.findRetailWideActiveByBrand(b)));
            } else if (VIEW_MODE_EXPIRED.equalsIgnoreCase(viewMode)) {
                mobileOfferModel = new MobileOfferDataModel(mergeOffers(mobileOfferMgr.findExpiredByCompanies(companies), mobileOfferMgr.findRetailWideExpiredByBrand(b)));
            } else if (VIEW_MODE_PENDING_APPROVAL.equalsIgnoreCase(viewMode)) {
                mobileOfferModel = new MobileOfferDataModel(mergeOffers(mobileOfferMgr.findPendingApprovalByCompanies(companies), mobileOfferMgr.findRetailWidePendingApprovalByBrand(b)));
            } else if (VIEW_MODE_NOT_EXPIRED.equalsIgnoreCase(viewMode)) {
                mobileOfferModel = new MobileOfferDataModel(mergeOffers(mobileOfferMgr.findNotExpiredByCompanies(companies), mobileOfferMgr.findRetailWideNotExpiredByBrand(b)));
            } else {
                mobileOfferModel = new MobileOfferDataModel(mergeOffers(mobileOfferMgr.findNotDeletedByCompanies(companies), mobileOfferMgr.findRetailWideNotDeletedByBrand(b)));
            }
            
            List<MobileOffer> offers = (List<MobileOffer>)mobileOfferModel.getWrappedData();
            filteredMobileOffers = new ArrayList<MobileOffer>(offers);

        } else {
            changeViewMode(selectedCompany);
        }
    }

    public boolean changeViewMode() {

        LoginController lc = this.getHttpSession();
        if (lc.getPrivileges().contains(lc.getBundle().getString("BrandMobileOffer"))) {
            changeViewModeBrand();
        } else if (lc.getPrivileges().contains(lc.getBundle().getString("CompanyMobileOffer"))) {
            changeViewMode(this.getCompanyFromSession());
        } else if (lc.getCurrUser().getProperties().isEmpty()) {
            return false;
        } else {
            changeViewModeProperty();
        }

        return true;
    }

    public void changeViewModeProperty() {
        List<Property> properties = new ArrayList<Property>();

        if (selectedProperty == null || selectedProperty.getName() == null || selectedProperty.getName().isEmpty()) {
            if (getHttpSession().getPrivileges().contains(getHttpSession().getBundle().getString("CompanyMobileOffer"))) {
                changeViewMode(); //If no property selected do the default Change View Mode according to the current user's role
                return;
            }
            properties = this.getHttpSession().getCurrUser().getProperties();
            selectedProperty = null;

        } else {
            selectedProperty = propertyMgr.getPropertyByCompanyAndName(this.getCompany(), selectedProperty.getName());
            properties.add(selectedProperty);
        }
        if (VIEW_MODE_ACTIVE.equalsIgnoreCase(viewMode)) {
            mobileOfferModel = new MobileOfferDataModel(mergeOffers(mobileOfferMgr.findActiveByProperties(properties), mobileOfferMgr.findRetailWideActiveInProperties(properties)));
        } else if (VIEW_MODE_EXPIRED.equalsIgnoreCase(viewMode)) {
            mobileOfferModel = new MobileOfferDataModel(mergeOffers(mobileOfferMgr.findExpiredByProperties(properties), mobileOfferMgr.findRetailWideExpiredInProperties(properties)));
        } else if (VIEW_MODE_PENDING_APPROVAL.equalsIgnoreCase(viewMode)) {
            mobileOfferModel = new MobileOfferDataModel(mergeOffers(mobileOfferMgr.findPendingApprovalByProperties(properties), mobileOfferMgr.findRetailWidePendingApprovalInProperties(properties)));
        } else if (VIEW_MODE_NOT_EXPIRED.equalsIgnoreCase(viewMode)) {
            mobileOfferModel = new MobileOfferDataModel(mergeOffers(mobileOfferMgr.findNotExpiredByProperties(properties), mobileOfferMgr.findRetailWideNotExpiredInProperties(properties)));
        } else {
            mobileOfferModel = new MobileOfferDataModel(mergeOffers(mobileOfferMgr.findNotDeletedByProperties(properties), mobileOfferMgr.findRetailWideNotDeletedInProperties(properties)));
        }
        
        List<MobileOffer> offers = (List<MobileOffer>)mobileOfferModel.getWrappedData();
        filteredMobileOffers = new ArrayList<MobileOffer>(offers);
        
    }

    protected void changeViewMode(Company c) {
        if (VIEW_MODE_ACTIVE.equalsIgnoreCase(viewMode)) {
            mobileOfferModel = new MobileOfferDataModel(mergeOffers(mobileOfferMgr.findActiveByCompany(c), mobileOfferMgr.findRetailWideActiveByBrand(c.getBrand())));
        } else if (VIEW_MODE_EXPIRED.equalsIgnoreCase(viewMode)) {
            mobileOfferModel = new MobileOfferDataModel(mergeOffers(mobileOfferMgr.findByCompanyExpired(c), mobileOfferMgr.findRetailWideExpiredByBrand(c.getBrand())));
        } else if (VIEW_MODE_PENDING_APPROVAL.equalsIgnoreCase(viewMode)) {
            mobileOfferModel = new MobileOfferDataModel(mergeOffers(mobileOfferMgr.findByCompanyPendingApproval(c), mobileOfferMgr.findRetailWidePendingApprovalByBrand(c.getBrand())));
        } else if (VIEW_MODE_NOT_EXPIRED.equalsIgnoreCase(viewMode)) {
            mobileOfferModel = new MobileOfferDataModel(mergeOffers(mobileOfferMgr.findByCompanyNotExpired(c), mobileOfferMgr.findRetailWideNotExpiredByBrand(c.getBrand())));
        } else {
            mobileOfferModel = new MobileOfferDataModel(mergeOffers(mobileOfferMgr.findByCompanyNotDeleted(c), mobileOfferMgr.findRetailWideNotDeletedByBrand(c.getBrand())));
        }
        
        List<MobileOffer> offers = (List<MobileOffer>)mobileOfferModel.getWrappedData();
        filteredMobileOffers = new ArrayList<MobileOffer>(offers);
    }

    public MobileOffer getSelectedMobileOffer() {
        return selectedMobileOffer;
    }

    public void setSelectedMobileOffer(MobileOffer selectedMobileOffer) {
        this.selectedMobileOffer = selectedMobileOffer;
    }

    public String sendOffer() {
        try {
            SMSPlatformRESTClient rest = new SMSPlatformRESTClient();
            List<Subscriber> subscriberList = new ArrayList<Subscriber>();
            //create clean offer
            MobileOffer offer = selectedMobileOffer;
            String offerText;
            if (offer.isRetailOnly()) {
                offerText = offer.getOfferWithWebHash(mos.getOfferUrl(), "0001");
            } else {
                offerText = offer.getOfferWithWebHash(mos.getOfferUrl(), offer.getProperties().get(0).getWebHash());
            }
            if (!selectedMobileOffer.isRetailOnly()) {
                offerText = offerText.replaceAll(Property.ADDRESS_REGEX, selectedMobileOffer.getProperties().get(0).getAddress());
                offerText = offerText.replaceAll(Property.PROPERTY_REGEX, selectedMobileOffer.getProperties().get(0).getName());
            }
            if (selectedMobileOffer.isRetailOnly() && selectedMobileOffer.getRetailer().getProperties() != null && !selectedMobileOffer.getRetailer().getProperties().isEmpty()) {
                offerText = offerText.replaceAll(Property.ADDRESS_REGEX, selectedMobileOffer.getRetailer().getProperties().get(0).getAddress());
                offerText = offerText.replaceAll(Property.PROPERTY_REGEX, selectedMobileOffer.getRetailer().getProperties().get(0).getName());
            }

            offer.setOfferText(offerText);
            offer.setName(selectedMobileOffer.getName());

            //create subscriber list from msisdn
            Subscriber subscriber = subscriberMgr.findByMsisdn(msisdn);
            if (subscriber == null) {
                JsfUtil.addErrorMessage(message.getString("notOptInErrorMessage"));
                return prepareList("");
            }
            subscriberList.add(subscriber);

            //create subscriber list
            int dlist = rest.createSubscriberList(subscriberList, offer);

            //create campaign and send offer
            testOfferCampaign = rest.createTestMobileOffer(offer, dlist);
            if (testOfferCampaign != null) {
                testOfferSteps = rest.findCampaignSteps(testOfferCampaign.getCampaignId().toString());
                if (testOfferSteps != null) {
                    testCampaignMessage = testOfferSteps.getSteps().get(0).getCampaignMsg();
                }
            }
            return prepareList("");
        } catch (Exception ex) {
            if (message == null) {
                message = this.getHttpSession().getMessages();
            }
            JsfUtil.addErrorMessage(message.getString("mobileOfferTransactionErrorMessage"));
            return prepareList("");
        }
    }

    public boolean isOverwriteDayParts() {
        return overwriteDayParts;
    }

    public void setOverwriteDayParts(boolean overwriteDayParts) {
        this.overwriteDayParts = overwriteDayParts;
    }

    public void dayPartsOn() {
        this.overwriteDayParts = true;
    }

    public void dayPartsOff() {
        this.overwriteDayParts = false;
    }

    public List<DayParts> getRemovedParts() {
        if (removedParts == null) {
            removedParts = new ArrayList<DayParts>();
        }
        return removedParts;
    }

    public void setRemovedParts(List<DayParts> removedParts) {
        this.removedParts = removedParts;
    }

    public List<DayParts> getVisibleParts() {
        if (visibleParts == null) {
            visibleParts = new ArrayList<DayParts>();
        }
        return visibleParts;
    }

    public void setVisibleParts(List<DayParts> visibleParts) {
        this.visibleParts = visibleParts;
    }

    public void addToVisibleParts() {
        if (visibleParts == null) {
            visibleParts = new ArrayList<DayParts>();
        }
        visibleParts.add(new DayParts());
    }

    public void removeFromVisibleParts(DayParts daypart) {
        if (visibleParts != null) {
            visibleParts.remove(daypart);
            if (daypart.getId() > 0) {
                if (this.removedParts == null) {
                    this.removedParts = new ArrayList<DayParts>();
                }
                this.removedParts.add(daypart);
            }
        }
    }

    public boolean isIsRetailOnly() {
        return isRetailOnly;
    }

    public void setIsRetailOnly(boolean isRetailOnly) {
        this.isRetailOnly = isRetailOnly;
    }

    public String getRetailerWideOrStoreSpecificCheck() {
        return retailerWideOrStoreSpecificCheck;
    }

    public void setRetailerWideOrStoreSpecificCheck(String retailWideOrStoreSpecificCheck) {
        this.retailerWideOrStoreSpecificCheck = retailWideOrStoreSpecificCheck;
        if (this.retailerWideOrStoreSpecificCheck != null) {
            if (this.retailerWideOrStoreSpecificCheck.equalsIgnoreCase("0")) {
                this.isRetailOnly = false;
            } else {
                this.selectedRetailer = null;
                this.isRetailOnly = true;

            }
        }
        clearText();
        picklistSource = null;
        picklistTarget = null;
        populatePicklist();
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
        if (this.picklistSource == null || this.picklistSource.isEmpty()) {
            List<Category> categories;
            categories = categoryMgr.getAllByBrand(getCompany().getBrand());
            if (categories != null) {
                this.picklistSource = new ArrayList<String>();
                for (Category category : categories) {
                    this.picklistSource.add(category.getName());
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

    protected void populatePicklist() {
        setPicklistSource(null);
        this.picklistModel = new DualListModel<String>(getPicklistSource(), getPicklistTarget());
    }

    public Company getCompany() {
        if (selectedCompanyToAddOffer == null) {
            selectedCompanyToAddOffer = this.companyMgr.find(getCompanyIdFromSession());
        }
        MobileOfferSettings settings = mosMgr.findByBrand(selectedCompanyToAddOffer.getBrand());
        if (settings != null && settings.getShortCode() != null) {
            shortCode = settings.getShortCode();
        }
        return this.companyMgr.find(selectedCompanyToAddOffer.getId());
    }

    public void setCompany(Company company) {
        this.selectedCompanyToAddOffer = company;
    }

    protected List<Category> getTargetCategories() {
        return getCategoriesFromNames(getPicklistTarget());
    }

    protected List<Category> getCategoriesFromNames(List<String> names) {
        List<Category> result = new ArrayList<Category>();
        Company c = getCompany();
        for (String categoryName : names) {
            Category category = categoryMgr.getCategoryByBrandAndName(c.getBrand(), categoryName);
            if (category != null) {
                result.add(category);
            }
        }
        return result;
    }

    public String getViewMode() {

        if (viewMode == null) {
            viewMode = VIEW_MODE_ALL;
        }
        return viewMode;
    }

    public void setViewMode(String viewMode) {
        this.viewMode = viewMode;
    }

    public List<Company> getBrandCompanies() {
        if (brandCompanies == null || brandCompanies.isEmpty()) {
            brandCompanies = companyMgr.findAllCompaniesOfBrand(this.getCompany().getBrand());
        }
        return brandCompanies;
    }

    public void setBrandCompanies(List<Company> brandCompanies) {
        this.brandCompanies = brandCompanies;
    }

    public Company getSelectedCompany() {
        return selectedCompany;
    }

    public void setSelectedCompany(Company selectedCompany) {
        this.selectedCompany = selectedCompany;
    }

    public Company getSelectedCompanyToAddOffer() {
        return selectedCompanyToAddOffer;
    }

    public void setSelectedCompanyToAddOffer(Company selectedCompanyToAddOffer) {
        this.selectedCompanyToAddOffer = selectedCompanyToAddOffer;
    }

    public LanguageSelector getSelectedLanguage() {
        return selectedLanguage;
    }

    public void setSelectedLanguage(LanguageSelector selectedLanguage) {
        this.selectedLanguage = selectedLanguage;
        resetFooterMsg();
        if (this.selectedLanguage == null) {
            try {
                this.selectedLanguage = LanguageSelector.getSelectorForLanguage("en");
            } catch (UnsupportedEncodingException ex) {
            }
        }

        Locale locale = localeMgr.getLocaleByLanguageCode(this.selectedLanguage.getValue());
        createdMobileOffer.setLocale(locale);
    }

    public boolean isInListMode() {
        return inListMode;
    }

    public void setInListMode(boolean inListMode) {
        this.inListMode = inListMode;
    }

    public String prepareList(String defaultMode) {
        msisdn = null;
        inListMode = true;
        selectedProperty = new Property();
        selectedRetailer = null;
        selectedCompany = null;
        isRetailOnly = false;

        if (defaultMode == null || defaultMode.isEmpty()) {
            this.viewMode = VIEW_MODE_NOT_EXPIRED;
        } else {
            this.viewMode = defaultMode;
        }

        boolean success = this.populateMobileOfferModel();
        if (success) {
            return "/mobile-offer/List?faces-redirect=true";
        } else {
            if (message == null) {
                message = this.getHttpSession().getMessages();
            }
            JsfUtil.addErrorMessage(message.getString("noPropertyAssigned"));
            return null;
        }
    }

    public String prepareList() {
        msisdn = null;
        inListMode = true;
        selectedProperty = new Property();
        selectedRetailer = null;
        selectedCompany = null;
        isRetailOnly = false;
        this.viewMode = VIEW_MODE_NOT_EXPIRED;
        boolean success = this.populateMobileOfferModel();
        if (success) {
            return "/mobile-offer/List?faces-redirect=true";
        } else {
            if (message == null) {
                message = this.getHttpSession().getMessages();
            }
            JsfUtil.addErrorMessage(message.getString("noPropertyAssigned"));
            return null;
        }
    }

    public String prepareEdit() {

        FacesContext context = FacesContext.getCurrentInstance();
        if (this.selectedMobileOffer.isRetailOnly() || this.SINGLE_STORE.equals(this.selectedMobileOffer.getOfferType())) {
            MobileOfferController mobileOfferController = context.getApplication().evaluateExpressionGet(context, "#{mobileOfferController}", MobileOfferController.class);
            if (mobileOfferController != null) {
                return mobileOfferController.prepareEdit(this.selectedMobileOffer);
            }
            System.err.println("Couldn't inject mobileOfferController");
            return "";

        } else if (this.MULTIPLE_STORES.equals(this.selectedMobileOffer.getOfferType())) {
            MobileOfferMultipleStoreController mobileOfferMultipleStoreController = context.getApplication().evaluateExpressionGet(context, "#{mobileOfferMultipleStoreController}", MobileOfferMultipleStoreController.class);
            if (mobileOfferMultipleStoreController != null) {
                return mobileOfferMultipleStoreController.prepareEdit(this.selectedMobileOffer);
            }
        }
        return "";
    }

    public String prepareCreate() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (this.mode.equalsIgnoreCase(this.RETAIL_WIDE) || this.mode.equalsIgnoreCase(this.SINGLE_STORE)) {
            MobileOfferController mobileOfferController = context.getApplication().evaluateExpressionGet(context, "#{mobileOfferController}", MobileOfferController.class);
            if (mobileOfferController != null) {
                return mobileOfferController.prepareCreate(this.mode);
            }
            System.err.println("Couldn't inject mobileOfferController");
            return "";

        } else if (this.mode.equalsIgnoreCase(this.MULTIPLE_STORES)) {
            MobileOfferMultipleStoreController mobileOfferMultipleStoreController = context.getApplication().evaluateExpressionGet(context, "#{mobileOfferMultipleStoreController}", MobileOfferMultipleStoreController.class);
            if (mobileOfferMultipleStoreController != null) {
                return mobileOfferMultipleStoreController.prepareCreate();
            }
        }
        return "";
    }

    public String cloneMobileOffer() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (this.selectedMobileOffer.isRetailOnly() || this.SINGLE_STORE.equals(this.selectedMobileOffer.getOfferType())) {
            MobileOfferController mobileOfferController = context.getApplication().evaluateExpressionGet(context, "#{mobileOfferController}", MobileOfferController.class);
            if (mobileOfferController != null) {
                return mobileOfferController.cloneMobileOffer(this.selectedMobileOffer);
            }
            System.err.println("Couldn't inject mobileOfferController");
            return "";

        } else if (this.MULTIPLE_STORES.equals(this.selectedMobileOffer.getOfferType())) {
            MobileOfferMultipleStoreController mobileOfferMultipleStoreController = context.getApplication().evaluateExpressionGet(context, "#{mobileOfferMultipleStoreController}", MobileOfferMultipleStoreController.class);
            if (mobileOfferMultipleStoreController != null) {
                return mobileOfferMultipleStoreController.cloneMobileOffer(this.selectedMobileOffer);
            }
            System.err.println("Couldn't inject mobileOfferMultipleController");
            return "";
        }
        return "";
    }

    public String cancelToList() {
        createdMobileOffer = null;
        selectedProperty = new Property();
        macrosMap = null;
        shortCode = null;
        overwriteDayParts = false;
        propertyList = null;
        return prepareList("");
    }

    public void setupMacros(MobileOffer mobileOffer) {
        if (this.isRetailOnly) {
            macrosMap = new HashMap<String, String>();
            if (selectedRetailer == null || selectedRetailer.getName() == null || selectedRetailer.getName().isEmpty()) {
                //no macros to modify
                return;
            }
            mos = mosMgr.findByBrand(getCompanyFromSession().getBrand());
            shortCode = mos.getShortCode();
            macrosMap.put("store", selectedRetailer.getName());
            macrosMap.put("shortCode", shortCode != null ? shortCode.getShortCode() + "" : "no_shortCode");
            macrosMap.put("propertyAddress", Property.ADDRESS_WEB);

            return;
        } else if (this.mode.equalsIgnoreCase(this.MULTIPLE_STORES)) {
            macrosMap = new HashMap<String, String>();
            if (selectedRetailer == null || selectedRetailer.getName() == null || selectedRetailer.getName().isEmpty()) {
                //no macros to modify
                return;
            }
            mos = mosMgr.findByBrand(getCompanyFromSession().getBrand());
            shortCode = mos.getShortCode();
            macrosMap.put("store", selectedRetailer.getName());
            macrosMap.put("shortCode", shortCode != null ? shortCode.getShortCode() + "" : "no_shortCode");
            macrosMap.put("propertyAddress", Property.ADDRESS_WEB);

        } else {
            Property prop = propertyMgr.getPropertyByCompanyAndName(getCompany(), selectedProperty.getName());
            macrosMap = new HashMap<String, String>();
            macrosMap.put("store", selectedRetailer.getName());
            macrosMap.put("property", prop.getName());
            macrosMap.put("propertyAddress", prop.getAddress() != null ? prop.getAddress() : "");
            macrosMap.put("shortCode", shortCode != null ? shortCode.getShortCode() + "" : "no_shortCode");

        }

        String offer = mobileOffer.getOfferText();
        if (offer == null || offer.isEmpty()) {
            return;
        }

        //Replace Macros in the offer text
        Matcher matcher = patternReplace.matcher(offer);
        StringBuffer sb = new StringBuffer(offer.length());
        while (matcher.find()) {
            if (matcher.groupCount() == 3) {
                String mac = matcher.group(2);
                if (mac != null) {
                    mac = mac.split("=")[1].replace("\"", "");
                    if (macrosMap.containsKey(mac)) {
                        String text = "input value=\"" + macrosMap.get(mac) + "\" " + matcher.group(2) + " size=\"" + (macrosMap.get(mac).length() + 1) + "\"";
                        matcher.appendReplacement(sb, Matcher.quoteReplacement(text));
                    }
                }
            }
        }
        matcher.appendTail(sb);
        String result = sb.toString().trim();
        mobileOffer.setOfferText(result);

    }

    public String getCleanOfferFrom(MobileOffer offer) {

        Map<String, String> myMacrosMap = new HashMap<String, String>();
        Retailer retailer = offer.getRetailer();
        myMacrosMap.put("store", retailer.getName());
        myMacrosMap.put("shortCode", getMos().getShortCode() != null ? getMos().getShortCode().getShortCode() + "" : "no_shortCode");
        //Taking in consideration Retail Wide, Multiple Stores or Specific Store;
        if (offer.getProperties() == null || offer.getProperties().isEmpty() || offer.getProperties().size() > 1) {
            //No Specific Property
            myMacrosMap.put("property", Property.PROPERTY_REGEX);
            myMacrosMap.put("propertyAddress", Property.ADDRESS_REGEX);
        } else {
            Property property = offer.getProperties().get(0);
            myMacrosMap.put("property", property.getName());
            myMacrosMap.put("propertyAddress", property.getAddress() != null ? property.getAddress() : "");
        }


        String offerText = offer.getOfferText();
        if (offerText == null || offerText.isEmpty()) {
            return offerText;
        }

        Matcher matcher = patternReplace.matcher(offerText);
        StringBuffer sb = new StringBuffer(offerText.length());
        while (matcher.find()) {
            if (matcher.groupCount() == 3) {
                String mac = matcher.group(2);
                if (mac != null) {
                    mac = mac.split("=")[1].replace("\"", "");
                    if (myMacrosMap.containsKey(mac)) {
                        String text = "input value=\"" + myMacrosMap.get(mac) + "\" " + matcher.group(2) + " size=\"" + myMacrosMap.get(mac).length() + "\"";
                        matcher.appendReplacement(sb, Matcher.quoteReplacement(text));
                    }
                }
            }
        }
        matcher.appendTail(sb);
        String result = sb.toString().trim();
        String header = (getMos() == null) ? "" : getMos().getOfferHeader();
        String footer = getFooterMsg();
        String url = (getMos() == null) ? "" : getMos().getOfferUrl();

        String cleanContent = MobileOfferController.cleanContent(result);

        header = (header != null && !header.isEmpty()) ? header : "";
        footer = (footer != null && !footer.isEmpty()) ? footer : "";
        url = (url != null && !url.isEmpty()) ? url : "";
        cleanContent = (cleanContent != null && !cleanContent.isEmpty()) ? cleanContent : "";

        header = header.trim();
        footer = footer.trim();
        url = url.trim();
        cleanContent = cleanContent.trim();
        result = header + " " + cleanContent + " " + footer + " " + url;
        result = result.replaceAll("[ \\t]+", " ");
        result = result.trim();
        return result;
    }

    public String deleteMobileOffer() {
        if (this.selectedMobileOffer != null) {
            this.selectedMobileOffer.setDeleted(true);
            WebOffer webOffer = this.getSiblingWebOffer(this.selectedMobileOffer);
            mobileOfferMgr.update(this.selectedMobileOffer);
            User currentUser = userMgr.find(getUserIdFromSession());
            MobileOfferEvent moe = new MobileOfferEvent(MobileOfferEvent.TYPE_DELETED, this.selectedMobileOffer, getCompany(), currentUser);
            eventMgr.update(moe);
            if (webOffer != null) {
                webOffer.setDeleted(this.selectedMobileOffer.isDeleted());
                webOfferMgr.update(webOffer);
            }
        }
        this.selectedMobileOffer = null;
        return prepareList(this.viewMode);
    }

    protected WebOffer getSiblingWebOffer(MobileOffer mo) {
        return webOfferMgr.getWebOfferByItsMobileOfferSibling(mo.getId());
    }

    public void resetFooterMsg() {
        footerMsg = null;
        getFooterMsg();
    }

    public String makeTheFooterMsg(String locale) {
        ResourceBundle localMessages = ResourceBundle.getBundle("resources.Messages", new java.util.Locale(locale));
        return localMessages.getString("mobileOfferFooterMsg");
    }

    public String getFooterMsg() {
        if (footerMsg == null || footerMsg.isEmpty()) {
            ResourceBundle localMessages;
            if (selectedLanguage == null) {
                localMessages = ResourceBundle.getBundle("resources.Messages", new java.util.Locale("en"));
            } else {
                localMessages = ResourceBundle.getBundle("resources.Messages", new java.util.Locale(selectedLanguage.getValue()));
            }
            footerMsg = localMessages.getString("mobileOfferFooterMsg");
        }
        return footerMsg;

    }

    public void setFooterMsg(String footerMsg) {
        this.footerMsg = footerMsg;
    }
}
