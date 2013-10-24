package com.proximus.jsf;

import com.proximus.data.Brand;
import com.proximus.data.Company;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 *
 * @author dshaw
 */
public class AbstractController {

    protected static final long ONE_WEEK = 1000 * 60 * 60 * 24 * 7;

    public Long getUserIdFromSession() {
        return getHttpSession().getUser_id();
    }

    public Long getCompanyIdFromSession() {
        return getHttpSession().getCompany_id();
    }
    
    public Company getCompanyFromSession() {
        return getHttpSession().getCompanyFromSession();
    }
    
    public Brand getBrandFromSession() {
        return getCompanyFromSession().getBrand();
    }

    public String getUsernameFromSession() {
        return getHttpSession().getUsername();
    }

    public String getSeriesColors() {
        return "aa0000,0000aa,767676,00aa7a,000000";
    }

    public LoginController getHttpSession() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        session.getAttribute("loginController");
        LoginController lc = (LoginController) session.getAttribute("loginController");
        return lc;
    }

    
    
    
}