/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.util;

import java.io.UnsupportedEncodingException;

/**
 *
 * @author Gilberto Gaxiola
 */
public class LanguageSelector {

    private String label;
    private String value;
    private String countryCode;

    public LanguageSelector() {
    }

    public LanguageSelector(String label, String value, String countryCode) {
        this.label = label;
        this.value = value;
        this.countryCode = countryCode;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Override
    public boolean equals(Object o) { 
        if (o == null) {
            return false;
        }
        LanguageSelector selector = (LanguageSelector) o;
        if (this.value.equals(selector.value) && this.label.equals(selector.label) && this.countryCode.equals(selector.countryCode)) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.label != null ? this.label.hashCode() : 0);
        hash = 37 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 37 * hash + (this.countryCode != null ? this.countryCode.hashCode() : 0);
        return hash;
    }
    
     public static LanguageSelector getSelectorForLanguage(String language) throws UnsupportedEncodingException {
        if (language.equals("en")) {
            return new LanguageSelector("English", "en", "us");
        }
        if (language.equals("es")) {
            return new LanguageSelector("Español", "es", "es");
       
        } else {
            return null;
        }
    }
}
