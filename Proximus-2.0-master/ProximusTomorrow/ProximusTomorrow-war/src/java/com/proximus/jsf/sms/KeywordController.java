/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.sms;

import com.proximus.data.Company;
import com.proximus.data.sms.*;
import com.proximus.helper.util.JsfUtil;
import com.proximus.jsf.AbstractController;
import com.proximus.jsf.datamodel.sms.KeywordDataModel;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.sms.*;
import com.proximus.rest.sms.SMSPlatformRESTClient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.apache.log4j.Logger;

@ManagedBean(name = "keywordController")
@SessionScoped
/**
 *
 * @author Gilberto Gaxiola
 */
public class KeywordController extends AbstractController implements Serializable {

    private static final Logger logger = Logger.getLogger(KeywordController.class.getName());
    private static final long serialVersionUID = 1;
    @EJB
    KeywordManagerLocal keywordMgr;
    @EJB
    PropertyManagerLocal propertyMgr;
    @EJB
    CompanyManagerLocal companyMgr;
    @EJB
    MobileOfferSettingsManagerLocal mosMgr;
    @EJB
    SourceTypeManagerLocal sourceTypeMgr;
    @EJB
    ShortCodeManagerLocal shortCodeMgr;
    @EJB
    LocaleManagerLocal localeMgr;
    @EJB
    MasterCampaignManagerLocal masterCampaignMgr;
    private Keyword newKeyword;
    private Keyword selectedKeyword;
    private List<Locale> localeList;
    private List<SourceType> sourceTypeList;
    private List<Property> propertyList;
    private SourceType selectedSourceType;
    private Property selectedProperty;
    private Locale selectedLocale;
    private KeywordDataModel keywordDataModel;
    private List<Keyword> filteredKeywords;
    private SMSPlatformRESTClient smsPlatformRestClient;
    private MobileOfferSettings mos;
    private Company company;
    private ResourceBundle message;

    public KeywordController() {
        smsPlatformRestClient = new SMSPlatformRESTClient();
        message = this.getHttpSession().getMessages();
    }

    public SourceType getSelectedSourceType() {
        if (this.selectedSourceType == null) {
            this.selectedSourceType = new SourceType();
        }
        return selectedSourceType;
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

    public Locale getSelectedLocale() {
        if (this.selectedLocale == null) {
            this.selectedLocale = new Locale();
        }
        return selectedLocale;
    }

    public void setSelectedLocale(Locale selectedLocale) {
        this.selectedLocale = selectedLocale;
    }

    public void setSelectedSourceType(SourceType selectedSourceType) {
        this.selectedSourceType = selectedSourceType;
    }

    public Keyword getNewKeyword() {
        if (newKeyword == null) {
            this.newKeyword = new Keyword();
            this.newKeyword.setProperty(new Property());
            this.newKeyword.setSourceType(new SourceType());
            this.newKeyword.setLocale(new Locale());
        }
        return newKeyword;
    }

    public void setNewKeyword(Keyword newKeyword) {
        this.newKeyword = newKeyword;
    }

    public Keyword getSelectedKeyword() {
        if (this.selectedKeyword == null) {
            this.selectedKeyword = new Keyword();
        }
        if (this.selectedKeyword.getProperty() == null) {
            this.selectedKeyword.setProperty(new Property());
        }
        if (this.selectedKeyword.getSourceType() == null) {
            this.selectedKeyword.setSourceType(new SourceType());
        }
        return selectedKeyword;
    }

    public void setSelectedKeyword(Keyword selectedKeyword) {
        this.selectedKeyword = selectedKeyword;
    }

    public KeywordDataModel getKeywordDataModel() {
        if (keywordDataModel == null) {
            populateKeywordDataModel();
        }
        return keywordDataModel;
    }

    public void setKeywordDataModel(KeywordDataModel keywordDataModel) {
        this.keywordDataModel = keywordDataModel;
    }

    public List<Keyword> getFilteredKeywords() {
        return filteredKeywords;
    }

    public void setFilteredKeywords(List<Keyword> filteredKeywords) {
        this.filteredKeywords = filteredKeywords;
    }

    public Company getCompany() {
        if (company == null) {
            company = companyMgr.getCompanybyId(this.getCompanyIdFromSession());
        }
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public List<Locale> getLocaleList() {
        if (localeList == null) {
            this.populateLocaleList();
        }
        return localeList;
    }

    public void setLocaleList(List<Locale> localeList) {
        this.localeList = localeList;
    }

    private void populateLocaleList() {
        localeList = localeMgr.getAllSorted();
    }

    public List<SourceType> getSourceTypeList() {
        if (sourceTypeList == null) {
            this.populateSourceTypeList();
        }
        return sourceTypeList;
    }

    public void setSourceTypeList(List<SourceType> sourceTypeList) {
        this.sourceTypeList = sourceTypeList;
    }

    private void populateSourceTypeList() {
        sourceTypeList = sourceTypeMgr.getAllSorted();
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

    private void populatePropertyList() {
        propertyList = propertyMgr.getPropertiesByCompany(getCompany());
    }

    private void populateKeywordDataModel() {
        List<Keyword> keywords = keywordMgr.findAllNotDeletedByCompany(getCompany());
        this.keywordDataModel = new KeywordDataModel(keywords);
        filteredKeywords = new ArrayList<Keyword>(keywords);
    }

    public void createNewKeyWord() {
        if (newKeyword == null || newKeyword.getKeyword() == null || newKeyword.getKeyword().isEmpty()) {
            JsfUtil.addErrorMessage(message.getString("keywordNameError"));
            return;
        }

        selectedProperty = newKeyword.getProperty();
        selectedSourceType = newKeyword.getSourceType();
        Locale realLocale = localeMgr.getLocaleByName(newKeyword.getLocale().getName());
        if(realLocale != null){
            newKeyword.setLocale(realLocale);
        }
        selectedLocale = realLocale;

        List<String> listOfUniqueKeywords = new ArrayList<String>();

        if (getMos() == null) {
            JsfUtil.addErrorMessage("mobileOfferSettingsNotSetup");
            return;
        }
        List<Keyword> listOfAllKeywords = keywordMgr.findAllByShortCode(getMos().getShortCode());



        if (listOfAllKeywords != null && !listOfAllKeywords.isEmpty()) {
            for (Keyword k : listOfAllKeywords) {
                listOfUniqueKeywords.add(k.getKeyword().toUpperCase());
            }


            if (listOfUniqueKeywords.contains(newKeyword.getKeyword().toUpperCase())) {
                JsfUtil.addErrorMessage(message.getString("keywordAlreadyReserved"));
                return;
            }
        }
        if (getMos() != null) {

            String response = smsPlatformRestClient.createKeywordAlias(newKeyword, getMos(), masterCampaignMgr);
            if (response != null) {

                newKeyword.setCompany(companyMgr.find(this.getCompanyIdFromSession()));

                if (getMos() != null && getMos().getShortCode() != null) {
                    newKeyword.setShortCode(getMos().getShortCode());
                }
                newKeyword.setKazeKeywordId(response);

                if (selectedProperty != null || !selectedProperty.getName().isEmpty()) {
                    Property realProperty = propertyMgr.getPropertyByCompanyAndName(newKeyword.getCompany(), selectedProperty.getName());
                    newKeyword.setProperty(realProperty);
                } else {
                    newKeyword.setProperty(null);
                }

                if (selectedLocale != null || !selectedLocale.getName().isEmpty()) {
                    realLocale = localeMgr.getLocaleByName(selectedLocale.getName());
                    newKeyword.setLocale(realLocale);
                } else {
                    newKeyword.setLocale(null);
                }

                if (selectedSourceType != null || !selectedSourceType.getSourceType().isEmpty()) {
                    SourceType realSourceType = sourceTypeMgr.getSourceTypeByName(selectedSourceType.getSourceType());
                    newKeyword.setSourceType(realSourceType);
                } else {
                    newKeyword.setSourceType(null);
                }

                keywordMgr.create(newKeyword);
                JsfUtil.addSuccessMessage(message.getString("keywordCreated"));
                

                prepareList();
            } else {
                JsfUtil.addErrorMessage(message.getString("keywordAlreadyReserved"));
            }
        } else {
            JsfUtil.addErrorMessage("mobileOfferSettingsNotSetup");
        }
    }

    public String prepareList() {
        this.newKeyword = null;
        this.company = null;
        this.selectedSourceType = null;
        this.sourceTypeList = null;
        this.selectedProperty = null;
        this.selectedKeyword = null;
        this.keywordDataModel = null;
        this.localeList = null;
        this.selectedLocale = null;
        return "/keyword/List?faces-redirect=true";
    }

    public boolean editKeyword() {
        try {
            if (selectedKeyword.getKeyword() == null || selectedKeyword.getKeyword().isEmpty()) {
                JsfUtil.addErrorMessage(message.getString("keywordNameError"));
                prepareList();
                return false;
            }

            selectedProperty = selectedKeyword.getProperty();
            selectedSourceType = selectedKeyword.getSourceType();
            selectedLocale = selectedKeyword.getLocale();

            List<String> listOfUniqueKeywords = new ArrayList<String>();


            if (getKeywordDataModel().getKeywordData() != null && !getKeywordDataModel().getKeywordData().isEmpty()) {
                for (Keyword k : getKeywordDataModel().getKeywordData()) {
                    if (k != selectedKeyword) {
                        listOfUniqueKeywords.add(k.getKeyword().toUpperCase());
                    }
                }

                if (listOfUniqueKeywords.contains(selectedKeyword.getKeyword().toUpperCase())) {
                    JsfUtil.addErrorMessage(message.getString("keywordDuplicatedError"));
                    return false;
                }
            }

            if (selectedSourceType != null || !selectedSourceType.getSourceType().isEmpty()) {
                SourceType realSourceType = sourceTypeMgr.getSourceTypeByName(selectedSourceType.getSourceType());
                selectedKeyword.setSourceType(realSourceType);
            }

            if (selectedProperty != null || !selectedProperty.getName().isEmpty()) {
                Property realProperty = propertyMgr.getPropertyByCompanyAndName(getCompany(), selectedProperty.getName());
                selectedKeyword.setProperty(realProperty);
            }

            keywordMgr.update(selectedKeyword);
            prepareList();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(message.getString("keywordPersistError"));
            prepareList();
            return false;
        }
        return true;
    }

    public void deleteKeyword() {
        if (selectedKeyword == null) {
            return;
        }
        try {
            selectedKeyword.setDeleted(true);
            keywordMgr.update(selectedKeyword);
            
            /*
             * uncomment and test when releasing an alias works on the Kaze platform.  Right now we are 
             * flagging the alias as deleted in our database. -- ARM
             */
            MobileOfferSettings mos = mosMgr.findByBrand(this.getCompany().getBrand());
            if(mos.isLiveGeoLocation()) {
                SMSPlatformRESTClient client = new SMSPlatformRESTClient();
                if(client.releaseKeywordAlias(selectedKeyword, masterCampaignMgr)){
                    JsfUtil.addSuccessMessage(message.getString("keywordDeleted"));
                }else{
                     JsfUtil.addErrorMessage(message.getString("keywordDeleteError") + ": " + selectedKeyword.getKeyword());
                }
            }
        } catch (Exception e) {
            JsfUtil.addErrorMessage(message.getString("keywordDeleteError") + ": " + selectedKeyword.getKeyword());
        }
        finally{
            prepareList();
        }    
    }

    public MobileOfferSettings getMos() {
        if (mos == null) {
            mos = mosMgr.findByBrand(getCompany().getBrand());
        }
        return mos;
    }

    public void setMos(MobileOfferSettings mos) {
        this.mos = mos;
    }
}
