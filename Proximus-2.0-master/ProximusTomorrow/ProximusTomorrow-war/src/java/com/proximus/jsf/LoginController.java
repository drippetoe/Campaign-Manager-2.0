/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf;

import com.proximus.data.Company;
import com.proximus.data.CompanyLogo;
import com.proximus.data.Privilege;
import com.proximus.data.User;
import com.proximus.jsf.util.JsfUtil;
import com.proximus.manager.CompanyLogoManagerLocal;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.UserManagerLocal;
import com.proximus.util.HashUtil;
import com.proximus.util.LanguageSelector;
import com.proximus.util.ListChoosers;
import com.proximus.util.ServerURISettings;
import java.io.*;
import java.util.*;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author Angela Mercer
 */
@ManagedBean(name = "loginController")
@SessionScoped
public class LoginController implements Serializable {

    private String username;
    private String password;
    private Long company_id;
    private Long user_id;
    private String company_name;
    private String first_name;
    public static final String test = "Create Users";
    private String last_name;
    private List<String> privileges;
    private boolean loggedIn = false;
    private boolean hasGeoFenceLicense;
    private boolean hasProximityLicense;
    private boolean hasContentGeneratorLicense;
    private boolean hasNotificationLicense;
    private CompanyLogo companyLogo;
    private String userUUIDTmp;
    public User currUser;
    ResourceBundle bundle = ResourceBundle.getBundle("resources.Bundle");
    ResourceBundle messages = ResourceBundle.getBundle("resources.Messages");
    //Locale related
    private LanguageSelector selectedLanguage;
    private List<LanguageSelector> languageSelectors;
    private Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
    @EJB
    private UserManagerLocal userManager;
    @EJB
    private CompanyManagerLocal companyManager;
    @EJB
    private CompanyLogoManagerLocal companyLogoMgr;
    private static final Logger logger = Logger.getLogger(LoginController.class.getName());

    public Company getCompanyFromSession() {
        return companyManager.find(this.getCompany_id());
    }

    private UserManagerLocal getFacade() {
        return userManager;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getCompany_id() {
        return company_id;
    }

    public String getCompanyName() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public void setCompany_id(Long company_id) {
        this.company_id = company_id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public List<String> getPrivileges() {
        return privileges;
    }

    public User getCurrUser() {
        return currUser;
    }

    public void setCurrUser(User currUser) {
        this.currUser = currUser;
    }

    public void setPrivileges(List<String> privileges) {
        this.privileges = privileges;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public String login() {


        try {
            if (username == null || username.isEmpty()) {
                JsfUtil.addErrorMessage("Please enter username");
                return "login";
            }
            if (password == null || password.isEmpty()) {
                JsfUtil.addErrorMessage("Please enter password");
                return "login";
            }
            User myUser = getFacade().getUserByUsername(username);
            if (myUser != null) {
                currUser = myUser;
                String myName = myUser.getUserName();
                String aPass = myUser.getPassword();
                if (!HashUtil.isEncodedPassword(aPass)) {
                    myUser.setPassword(HashUtil.getEncodedPassword(myName, aPass));
                    userManager.update(myUser);
                    aPass = myUser.getPassword();
                }

                String hashPass = HashUtil.getEncodedPassword(myName, password);
                if (username.equalsIgnoreCase(myName) && hashPass.equals(aPass)) {
                    loggedIn = true;
                    user_id = myUser.getId();
                    deleteTempDirectories();
                    userUUIDTmp = user_id + "_" + UUID.randomUUID().toString();
                    if (!myUser.getCompanies().isEmpty()) {
                        boolean companyExists = false;
                        if (company_id != null) {
                            for (Company c : myUser.getCompanies()) {
                                if (c.getId().equals(company_id)) {
                                    companyExists = true;
                                    break;
                                }
                            }
                        }
                        if (!companyExists) {
                            company_id = myUser.getCompanies().get(0).getId();
                        }
                        if (company_id == null) {
                            JsfUtil.addErrorMessage("Company : " + ResourceBundle.getBundle("/resources/Bundle").getString("UserNotFound"));
                            return "login";
                        }
                        Company c = companyManager.getCompanybyId(company_id);
                        this.hasGeoFenceLicense = c.getLicense().hasGeofence();
                        this.hasProximityLicense = c.getLicense().hasProximity();
                        this.hasContentGeneratorLicense = c.getLicense().hasContentGenerator();
                        this.hasNotificationLicense = c.getLicense().hasPubNubNotification();
                        if (myUser.getLocale() != null && !myUser.getLocale().isEmpty()) {
                            this.locale = new Locale(myUser.getLocale());
                            messages = ResourceBundle.getBundle("resources.Messages", this.locale);
                        }
                        company_name = c.getName();
                    }

                    companyLogo = companyLogoMgr.getByCompany(companyManager.getCompanybyId(company_id));
                    first_name = myUser.getFirstName();
                    last_name = myUser.getLastName();
                    privileges = new ArrayList<String>();
                    if (myUser.getRole() != null) {
                        List<Privilege> privs = myUser.getRole().getPrivileges();
                        for (Privilege p : privs) {
                            if (!privileges.contains(p.toString())) {
                                privileges.add(p.toString());
                            }
                        }
                    }
                    cleanUserTempDir();
                    return "home?faces-redirect=true";
                } else {
                    JsfUtil.addErrorMessage(ResourceBundle.getBundle("/resources/Bundle").getString("InvalidCredentials"));
                    return "login";
                }

            } else {
                JsfUtil.addErrorMessage(ResourceBundle.getBundle("/resources/Bundle").getString("UserNotFound"));
                return "login";
            }
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
            return "login";
        }
    }

    private void deleteTempDirectories() {
        String basePath = ServerURISettings.CONTENT_GENERATOR_TMP_DIR;
        File f = new File(basePath);
        if (f != null && f.isDirectory()) {
            File[] list = f.listFiles(new TempFilter(user_id + "_"));
            for (File fi : list) {
                FileUtils.deleteQuietly(fi);
            }
        }
        basePath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("//tmp");
        f = new File(basePath);
        if (f != null && f.isDirectory()) {
            File[] list = f.listFiles(new TempFilter(user_id + "_"));
            for (File fi : list) {
                FileUtils.deleteQuietly(fi);
            }
        }

    }

    public void checkLogin() {
        if (!loggedIn) {
            final FacesContext fc = FacesContext.getCurrentInstance();
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect(fc.getExternalContext().getRequestContextPath() + "/faces/login.xhtml");
            } catch (IOException ex) {
                logger.fatal(ex);

            }
        }

    }

    public String getGoogleMapsApi() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String protocol = "http";
        if (request.getServerName().contains("secure")) {
            protocol = "https";
        }
        return protocol + "://maps.google.com/maps/api/js?sensor=false";
    }

    public String logout() {
        boolean customLogo = this.hasCustomCompanyLogo();
        String loginId = company_id + "";
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        loggedIn = false;
        company_id = null;
        privileges = null;
        hasGeoFenceLicense = false;
        hasProximityLicense = false;
        hasContentGeneratorLicense = false;
        hasNotificationLicense = false;
        companyLogo = null;
        userUUIDTmp = null;
        session.invalidate();
        String logoutPage = "/login?faces-redirect=true" + (customLogo ? "login=" + loginId : "");
        return logoutPage;

    }

    private void cleanUserTempDir() {
        String userTempPath = ServerURISettings.SERVER_TMP + ServerURISettings.OS_SEP + this.getUser_id() + ServerURISettings.OS_SEP;
        File tempDir = new File(userTempPath);
        if (tempDir.exists()) {
            try {
                FileUtils.deleteDirectory(tempDir);
            } catch (IOException ex) {
            }
        }
    }

    public boolean isHasGeoFenceLicense() {
        return hasGeoFenceLicense;
    }

    public void setHasGeoFenceLicense(boolean hasGeoFenceLicense) {
        this.hasGeoFenceLicense = hasGeoFenceLicense;
    }

    public boolean isHasProximityLicense() {
        return hasProximityLicense;
    }

    public void setHasProximityLicense(boolean hasProximityLicense) {
        this.hasProximityLicense = hasProximityLicense;
    }

    public boolean isHasContentGeneratorLicense() {
        return hasContentGeneratorLicense;
    }

    public void setHasContentGeneratorLicense(boolean hasContentGeneratorLicense) {
        this.hasContentGeneratorLicense = hasContentGeneratorLicense;
    }

    public boolean isHasNotificationLicense() {
        return hasNotificationLicense;
    }

    public void setHasNotificationLicense(boolean hasNotificationLicense) {
        this.hasNotificationLicense = hasNotificationLicense;
    }

    public boolean hasCustomCompanyLogo() {
        return companyLogo != null;
    }

    public String getCompanyLogoFilename() {
        if (companyLogo != null) {
            return companyLogo.getFilename();
        }
        return null;
    }

    public CompanyLogo getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(CompanyLogo companyLogo) {
        this.companyLogo = companyLogo;
    }

    public void populateCompanyLogo() {
        companyLogo = companyLogoMgr.getByCompany(companyManager.getCompanybyId(company_id));
    }

    public String getLogoUrl() {


        String path = null;
        if (companyLogo != null) {

            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            String protocol = "http";
            String s = "";
            if (request.getServerName().contains("secure")) {
                protocol = "https";
                s = protocol + "://" + request.getServerName() + "/logos/";
            } else {
                s = protocol + "://" + request.getServerName() + ":" + request.getServerPort() + "/logos/";
            }
            path = s + this.company_id + "/" + companyLogo.getFilename();
        }

        return path;
    }

    public String getUserUUIDTmp() {
        return userUUIDTmp;
    }

    public void setUserUUIDTmp(String userUUIDTmp) {
        this.userUUIDTmp = userUUIDTmp;
    }

    public Locale getLocale() {
        return locale;
    }

    public List<LanguageSelector> getLanguageSelectors() {
        try {
            languageSelectors = ListChoosers.getLanguages();
        } catch (UnsupportedEncodingException ex) {
            logger.fatal(ex);
        }
        return languageSelectors;
    }

    public void setLanguageSelectors(List<LanguageSelector> languageSelectors) {
        this.languageSelectors = languageSelectors;
    }

    public LanguageSelector getSelectedLanguage() {
        if (currUser != null && this.selectedLanguage == null) {
            try {
                this.selectedLanguage = LanguageSelector.getSelectorForLanguage(currUser.getLocale());
            } catch (UnsupportedEncodingException ex) {
                logger.fatal(ex);
                this.selectedLanguage = null;
            }

        }
        return selectedLanguage;
    }

    public void setSelectedLanguage(LanguageSelector selectedLanguage) {
        this.selectedLanguage = selectedLanguage;
        if (this.selectedLanguage == null) {
            try {
                this.selectedLanguage = LanguageSelector.getSelectorForLanguage("en");
            } catch (UnsupportedEncodingException ex) {
                logger.fatal(ex);
            }
        }
        locale = new Locale(this.selectedLanguage.getValue());
        messages = ResourceBundle.getBundle("resources.Messages", locale);

    }

    public void reloadLocale() {
        FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public ResourceBundle getMessages() {
        return messages;
    }

    public void setMessages(ResourceBundle messages) {
        this.messages = messages;
    }

    class TempFilter implements FilenameFilter {

        String prefix;

        public TempFilter(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public boolean accept(File dir, String name) {
            return name.startsWith(prefix);
        }
    }
}
