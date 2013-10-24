/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf;

import com.proximus.data.CompanyLogo;
import com.proximus.manager.CompanyLogoManagerLocal;
import com.proximus.manager.CompanyManagerLocal;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Gilberto Gaxiola
 */
@ManagedBean(name = "logoLoginController")
@RequestScoped
public class LogoLoginController {

    @EJB
    private CompanyLogoManagerLocal companyLogoMgr;
    @EJB
    private CompanyManagerLocal companyMgr;
    private String loginId;
    private CompanyLogo companyLogo;
    private String companyName;
    private boolean customLogoExists = false;

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        Long id = 0L;
        try {
            id = Long.valueOf(loginId);
        } catch (NumberFormatException e) {
            this.loginId = "";
            return;
        }
        this.loginId = loginId;
        companyLogo = companyLogoMgr.getByCompany(companyMgr.getCompanybyId(id));
        companyName = companyMgr.getCompanybyId(id).getName();
        
        if(companyLogo != null) {
            customLogoExists = true;
        }
    }

    public String getLogoUrl() {
        String path = null;
        if (companyLogo != null) {
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            String s = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/logos/";
            path = s + this.loginId + "/" + companyLogo.getFilename();
        }
        return path;
    }
    
    public String getCompanyName() {
        return companyName;
    }

    public boolean isCustomLogoExists() {
        return customLogoExists;
    }

    public void setCustomLogoExists(boolean customLogoExists) {
        this.customLogoExists = customLogoExists;
    }
    
    
}
