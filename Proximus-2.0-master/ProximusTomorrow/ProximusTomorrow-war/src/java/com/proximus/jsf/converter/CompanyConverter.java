/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.converter;

import com.proximus.data.Company;
import com.proximus.manager.CompanyManagerLocal;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.inject.Named;

/**
 *
 * @author ronald
 */
@Named("companyConverter")
@RequestScoped
public class CompanyConverter implements Converter, Serializable {

    @EJB
    CompanyManagerLocal companyManager;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Company company = null;
        try {
            if ((value != null) && (!value.isEmpty())) {
                company = companyManager.find(Long.valueOf(value));
            }
            return company;
        } catch (Exception e) {
            throw new ConverterException(new FacesMessage(String.format("Cannot convert id %s to Company", value, e)), e);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if ((value != null)) {
            Company company = (Company) value;
            Long id = company.getId();
            return id.toString();
        } else {
            return null;
        }
    }
}
