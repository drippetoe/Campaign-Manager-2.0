/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.sms;

import com.proximus.data.DayParts;
import com.proximus.data.sms.GeoLocationSettings;
import com.proximus.helper.util.JsfUtil;
import com.proximus.jsf.AbstractController;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.DayPartsManagerLocal;
import com.proximus.manager.sms.GeoLocationSettingsManagerLocal;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Gilberto Gaxiola
 */
@ManagedBean(name = "geoLocationSettingsController")
@SessionScoped
public class GeoLocationSettingsController extends AbstractController implements Serializable {

    @EJB
    GeoLocationSettingsManagerLocal geoLocMgr;
    @EJB
    DayPartsManagerLocal dayPartsMgr;
    @EJB
    CompanyManagerLocal companyMgr;
    GeoLocationSettings selectedGeoLocationSettings;
    private List<DayParts> visibleParts;
    private List<DayParts> removedParts;

    public GeoLocationSettingsController() {
        selectedGeoLocationSettings = new GeoLocationSettings();
    }

    public List<DayParts> getRemovedParts() {
        if (removedParts == null) {
            removedParts = new ArrayList<DayParts>();
        }
        return removedParts;
    }

    public void setRemovedParts(List<DayParts> removedParts) {
        this.removedParts = removedParts;
    }

    public List<DayParts> getVisibleParts() {
        if (visibleParts == null) {
            visibleParts = new ArrayList<DayParts>();
        }

        return visibleParts;
    }

    public void setVisibleParts(List<DayParts> visibleParts) {
        this.visibleParts = visibleParts;
    }

    public void addToVisibleParts() {
        if (visibleParts == null) {
            visibleParts = new ArrayList<DayParts>();
        }
        visibleParts.add(new DayParts());
    }

    public void removeFromVisibleParts(DayParts daypart) {
        if (visibleParts != null) {
            visibleParts.remove(daypart);
            if (daypart.getId() > 0) {
                if (this.removedParts == null) {
                    this.removedParts = new ArrayList<DayParts>();
                }
                this.removedParts.add(daypart);
            }
        }
    }

    public String prepareSettings() {
        GeoLocationSettings glos = geoLocMgr.findByBrand(getCompanyFromSession().getBrand());
        visibleParts = new ArrayList<DayParts>();
        removedParts = new ArrayList<DayParts>();
        if (glos == null) {
            selectedGeoLocationSettings = new GeoLocationSettings();
        } else {
            selectedGeoLocationSettings = glos;
            for (DayParts r : selectedGeoLocationSettings.getDayParts()) {
                r.initialize();
                visibleParts.add(r);
            }
        }
        return "/mobile-offer/GeoSettings?faces-redirect=true";
    }

    public GeoLocationSettings getSelectedGeoLocationSettings() {
        if (selectedGeoLocationSettings == null) {
            selectedGeoLocationSettings = new GeoLocationSettings();
        }
        return selectedGeoLocationSettings;
    }

    public void setSelectedGeoLocationSettings(GeoLocationSettings selectedGeoLocationSettings) {
        this.selectedGeoLocationSettings = selectedGeoLocationSettings;
    }
    
    
     public String save() {
        //Checking if we have dayparts
        if (this.getVisibleParts() == null || this.getVisibleParts().isEmpty()) {
            JsfUtil.addErrorMessage("Your Settings do not have any Day Part");
            return "";
        }
        if (this.selectedGeoLocationSettings.getBrand() == null) {
            this.selectedGeoLocationSettings.setBrand(getCompanyFromSession().getBrand());
        }
        
       
        this.selectedGeoLocationSettings.setLastModified(new Date());

        //Deleting from DB dayparts removed on UI (that were already persisted)
        Iterator<DayParts> iter = this.getRemovedParts().iterator();
        while (iter.hasNext()) {
            DayParts r = iter.next();
            r.setMobile_offer_settings(null);
            iter.remove();
            dayPartsMgr.delete(r);
        }

        //Updating the Day Parts chosen from UI
        iter = this.getVisibleParts().iterator();
        while (iter.hasNext()) {
            DayParts r = iter.next();
            r.update();
            r.setGeo_location_settings(this.selectedGeoLocationSettings);
            if (r.getSelectedDaysOfWeek().isEmpty()) {
                r.setDaysOfWeek("M,T,W,R,F,S,U"); //if nothing selected default to All days
            }
        }
        this.selectedGeoLocationSettings.setDayParts(visibleParts);
        geoLocMgr.update(this.selectedGeoLocationSettings);
        return "/home?faces-redirect=true";
    }
}
