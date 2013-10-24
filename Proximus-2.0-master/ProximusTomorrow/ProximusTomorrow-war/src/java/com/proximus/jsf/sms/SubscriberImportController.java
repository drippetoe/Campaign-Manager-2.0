/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.sms;

import com.proximus.data.Company;
import com.proximus.data.License;
import com.proximus.data.sms.Keyword;
import com.proximus.helper.util.JsfUtil;
import com.proximus.jsf.AbstractController;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.sms.*;
import com.proximus.parsers.SubscriberImportParser;
import com.proximus.util.ServerURISettings;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;

/**
 *
 * @author ronald modified by angela mercer
 */
@ManagedBean(name = "subscriberImportController")
@SessionScoped
public class SubscriberImportController extends AbstractController implements Serializable
{
    @EJB
    private SubscriberManagerLocal subscriberMgr;
    @EJB
    private CompanyManagerLocal companyMgr;
    @EJB
    private KeywordManagerLocal keywordMgr;
    @EJB
    private MobileOfferSettingsManagerLocal settingsMgr;
    @EJB
    private LocationDataManagerLocal locationDataMgr;
    private String file;
    private Keyword selectedKeyword;
    private String selectedKeywordString;
    private Company selectedGeoCompany;
    private List<Company> geoFenceCompanies;
    private List<Keyword> keywordsList;
    static final Logger logger = Logger.getLogger(SubscriberImportController.class.getName());

    public SubscriberImportController()
    {
    }

    public String getSelectedKeywordString()
    {
        return selectedKeywordString;
    }

    public void setSelectedKeywordString(String selectedKeywordString)
    {
        this.selectedKeywordString = selectedKeywordString;
    }

    public Company getSelectedGeoCompany()
    {
        if (this.selectedGeoCompany == null) {
            this.selectedGeoCompany = companyMgr.getCompanybyId(this.getCompanyIdFromSession());
        }
        return selectedGeoCompany;
    }

    public void setSelectedGeoCompany(Company selectedGeoCompany)
    {
        this.selectedGeoCompany = selectedGeoCompany;
    }

    public String getFile()
    {
        return file;
    }

    public void setFile(String file)
    {
        this.file = file;
    }

    public Keyword getSelectedKeyword()
    {
        if (selectedKeyword == null) {
            this.selectedKeyword = keywordMgr.getKeywordByKeywordString("VALU");
        }
        return selectedKeyword;
    }

    public void setSelectedKeyword(Keyword selectedKeyword)
    {
        this.selectedKeyword = selectedKeyword;
    }

    public String prepareImport()
    {

        this.selectedGeoCompany = null;
        this.selectedKeyword = null;
        return "/subscriber-import/Import?faces-redirect=true";
    }

    public List<Company> getGeoFenceCompanies()
    {
        if (geoFenceCompanies == null) {
            retrieveGeoFenceCompanies();
        }
        return geoFenceCompanies;
    }

    public void setGeoFenceCompanies(List<Company> geoFenceCompanies)
    {
        this.geoFenceCompanies = geoFenceCompanies;
    }

    public List<Keyword> getKeywordsList()
    {
        if (keywordsList == null) {

            retrieveKeywordsList();
        }
        return keywordsList;
    }

    public void setKeywordsList(List<Keyword> keywordsList)
    {
        this.keywordsList = keywordsList;
    }

    private void retrieveGeoFenceCompanies()
    {
        geoFenceCompanies = companyMgr.findCompaniesWithLicense(License.LICENSE_GEOFENCE);
    }

    /**
     * Takes a CSV file and uploads and parses it
     *
     * @param event
     */
    public void handleFileUpload(FileUploadEvent event)
    {
        logger.debug("in handleFileUpload");
        
        if (keywordMgr.getKeywordByKeywordString(selectedKeywordString) == null) {
            logger.debug("selected keyword is null");
            JsfUtil.addErrorMessage("Please select a keyword.");
        } else {
            Keyword myKeyword = keywordMgr.getKeywordByKeywordString(selectedKeywordString);
            SimpleDateFormat sdf = new SimpleDateFormat(ServerURISettings.FILE_DATEFORMAT);
            this.file = event.getFile().getFileName();
            InputStream inputStream;
            try {
                File dir = new File(ServerURISettings.SMS_USER_IMPORT_DIR);
                dir.mkdirs();
                File f = new File(dir, "sub_upload" + sdf.format(new Date()) + "_" + this.file);
                inputStream = event.getFile().getInputstream();
                FileUtils.copyInputStreamToFile(inputStream, f);
                JsfUtil.addSuccessMessage("Successfully Uploaded File " + this.file);
//            SubscriberCSVParser parser = new SubscriberCSVParser();
                SubscriberImportParser parser = new SubscriberImportParser();
                int count = parser.parse(f, subscriberMgr, keywordMgr, settingsMgr, locationDataMgr, myKeyword);
                JsfUtil.addSuccessMessage("Read " + count + " lines from file.");

            } catch (IOException ex) {
                JsfUtil.addErrorMessage("Unable to upload file: " + this.file + "\n" + ex.getMessage());
            }
        }
    }

    private void retrieveKeywordsList()
    {
        List<Keyword> myList = keywordMgr.findAllByCompany(this.getCompanyFromSession());
        Collections.sort(myList, new Comparator<Keyword>()
        {
            @Override
            public int compare(Keyword keywordOne, Keyword keywordTwo)
            {
                return keywordOne.getKeyword().compareTo(keywordTwo.getKeyword());
            }
        });
        this.keywordsList = myList;
    }

    @FacesConverter(forClass = Keyword.class, value = "keywordConverter")
    public static class KeywordConverter extends AbstractController implements Converter, Serializable
    {
        private static final Logger logger = Logger.getLogger(com.proximus.jsf.sms.SubscriberImportController.KeywordConverter.class.getName());
        private static final ResourceBundle MESSAGE = ResourceBundle.getBundle("resources.Messages");

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value)
        {
            System.out.println("value of keyword selected: " + value);
            if (value == null || value.length() == 0 || value.equalsIgnoreCase(this.getHttpSession().getMessages().getString("selectKeyword"))) {
                return null;
            }
            try {
                Long id = Long.parseLong(value);
                Keyword controller = ((KeywordController) facesContext.getApplication().getELResolver().getValue(facesContext.getELContext(), null, "SubscriberImportController")).keywordMgr.find(id);
                return controller;
            } catch (NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", MESSAGE.getString("keywordValueNotFound")));
            }
        }

        java.lang.Long getKey(String value)
        {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value)
        {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object value)
        {
            if (value == null || value.toString().isEmpty() || !(value instanceof Keyword)) {
                return null;
            }
            return String.valueOf(((Keyword) value).getId());
        }
    }
}
