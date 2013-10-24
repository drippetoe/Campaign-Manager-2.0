/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.util;

import com.proximus.data.Company;
import com.proximus.manager.CompanyManagerLocal;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Named;
import org.apache.log4j.Logger;

/**
 *
 * @author Gilberto Gaxiola
 */
@Named("companySelectorConverter")
@RequestScoped
public class CompanySelectorConverter implements Converter, Serializable {

    private static final Logger logger = Logger.getLogger(CompanySelectorConverter.class.getName());
    @EJB
    CompanyManagerLocal companyMgr;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return companyMgr.getCompanybyName(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value instanceof Company) {
            Company comp = (Company) value;
            return "" + ((Company) value).getName();
        }
        return "";
    }
}
