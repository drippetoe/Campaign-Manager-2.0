/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.response;

import com.proximus.data.sms.Carrier;
import com.proximus.data.sms.Category;
import com.proximus.data.sms.Locale;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ronald
 */
@XmlRootElement(name = "preferenceResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class PreferenceReponse extends BaseResponse {

    private List<Carrier> carriers;
    private List<Category> categories;
    private List<Locale> locales;

    public PreferenceReponse() {
    }

    public List<Carrier> getCarriers() {
        return carriers;
    }

    public void setCarriers(List<Carrier> carriers) {
        this.carriers = carriers;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Locale> getLocales() {
        return locales;
    }

    public void setLocales(List<Locale> locales) {
        this.locales = locales;
    }
}
