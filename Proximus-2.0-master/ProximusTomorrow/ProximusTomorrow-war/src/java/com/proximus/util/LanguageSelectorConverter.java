/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.util;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Named;
import org.apache.log4j.Logger;

/**
 *
 * @author Gilberto Gaxiola
 */
@Named("languageSelectorConverter")
@RequestScoped
public class LanguageSelectorConverter implements Converter, Serializable {

    private static final Logger logger = Logger.getLogger(LanguageSelectorConverter.class.getName());

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        LanguageSelector selectedLanguge = null;
        try {
            selectedLanguge = LanguageSelector.getSelectorForLanguage(value);
        } catch (UnsupportedEncodingException ex) {
            logger.fatal(ex);
        }
        return selectedLanguge;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value instanceof LanguageSelector) {
            LanguageSelector sel = (LanguageSelector) value;
            return "" + ((LanguageSelector) value).getValue();
        }
        return "";
    }
}
