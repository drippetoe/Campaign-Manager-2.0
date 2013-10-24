/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.converter;

import com.proximus.data.Campaign;
import com.proximus.manager.CampaignManagerLocal;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Named;

/**
 *
 * @author Gilberto Gaxiola
 */
@Named("campaignConverter")
@RequestScoped
public class CampaignConverter implements Converter,Serializable {

    @EJB
    CampaignManagerLocal campMgr;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if(value == null || value.isEmpty()) {
            return null;
        }
        if(campMgr == null) {
            return null;
        }
        return campMgr.find(Long.valueOf(value));

    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value instanceof Campaign) {
            return "" + ((Campaign) value).getId();
        }
        return "";
    }
}
