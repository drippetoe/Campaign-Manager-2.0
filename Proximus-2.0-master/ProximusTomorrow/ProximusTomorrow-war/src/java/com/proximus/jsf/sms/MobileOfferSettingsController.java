/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.sms;

import com.proximus.data.DayParts;
import com.proximus.data.sms.MobileOfferSettings;
import com.proximus.helper.util.JsfUtil;
import com.proximus.jsf.AbstractController;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.DayPartsManagerLocal;
import com.proximus.manager.sms.MobileOfferSettingsManagerLocal;
import com.proximus.util.TimeConstants;
import java.io.Serializable;
import java.util.*;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.LineChartSeries;

/**
 *
 * @author Gilberto Gaxiola
 */
@ManagedBean(name = "mobileOfferSettingsController")
@SessionScoped
public class MobileOfferSettingsController extends AbstractController implements Serializable {

    @EJB
    MobileOfferSettingsManagerLocal mosMgr;
    @EJB
    DayPartsManagerLocal dayPartsMgr;
    @EJB
    CompanyManagerLocal companyMgr;
    MobileOfferSettings selectedMobileOfferSettings;
    private List<DayParts> visibleParts;
    private List<DayParts> removedParts;
    private final int CHAR_LIMIT = 158; //Allowing for CR and LF
    private ResourceBundle message;

    public MobileOfferSettingsController() {
        selectedMobileOfferSettings = new MobileOfferSettings();
        message = this.getHttpSession().getMessages();
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
        MobileOfferSettings mos = mosMgr.findByBrand(getCompanyFromSession().getBrand());
        visibleParts = new ArrayList<DayParts>();
        removedParts = new ArrayList<DayParts>();
        if (mos == null) {
            selectedMobileOfferSettings = new MobileOfferSettings();
            visibleParts.add(new DayParts());
        } else {
            selectedMobileOfferSettings = mos;
            for (DayParts r : selectedMobileOfferSettings.getDayParts()) {
                r.initialize();
                visibleParts.add(r);
            }
        }
        return "/mobile-offer/Settings?faces-redirect=true";
    }

    public MobileOfferSettings getSelectedMobileOfferSettings() {
        if (selectedMobileOfferSettings == null) {
            selectedMobileOfferSettings = new MobileOfferSettings();
        }
        return selectedMobileOfferSettings;
    }

    public void setSelectedMobileOfferSettings(MobileOfferSettings selectedMobileOfferSettings) {
        this.selectedMobileOfferSettings = selectedMobileOfferSettings;
    }

    public String save() {
        //Checking if we have dayparts

        if (this.getVisibleParts() == null || this.getVisibleParts().isEmpty()) {
            JsfUtil.addErrorMessage(message.getString("noDayPartErrorMessage"));
            return "";
        }
        if (this.selectedMobileOfferSettings.getBrand() == null) {
            this.selectedMobileOfferSettings.setBrand(getCompanyFromSession().getBrand());
        }
        
        
        if(this.selectedMobileOfferSettings.getHelpCustomMessage() != null) {
            this.selectedMobileOfferSettings.setHelpCustomMessage(MobileOfferController.cleanContent(this.selectedMobileOfferSettings.getHelpCustomMessage()));
            
        }
        if(this.selectedMobileOfferSettings.getStopCustomMessage() != null) {
            this.selectedMobileOfferSettings.setStopCustomMessage(MobileOfferController.cleanContent(this.selectedMobileOfferSettings.getStopCustomMessage()));
            
        }
        if(this.helpCharRemaining() < 0) {
            JsfUtil.addErrorMessage(message.getString("helpMessageLimitError"));
            return "";
        }
        if(this.stopCharRemaining() < 0) {
            JsfUtil.addErrorMessage(message.getString("stopMessageLimitError"));
            return "";
        }
        this.selectedMobileOfferSettings.setLastModified(new Date());

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
            r.setMobile_offer_settings(this.selectedMobileOfferSettings);
            if (r.getSelectedDaysOfWeek().isEmpty()) {
                r.setDaysOfWeek("M,T,W,R,F,S,U"); //if nothing selected default to All days
            }
        }
        this.selectedMobileOfferSettings.setDayParts(visibleParts);
        mosMgr.update(this.selectedMobileOfferSettings);
        return "/home?faces-redirect=true";
    }

    public int helpCharRemaining() {
        if (this.selectedMobileOfferSettings != null) {
            int helpMessageLength = (this.selectedMobileOfferSettings.getHelpCustomMessage() != null) ? this.selectedMobileOfferSettings.getHelpCustomMessage().length() : 0;
            return CHAR_LIMIT - helpMessageLength;
        }
        return CHAR_LIMIT;
    }

    public int stopCharRemaining() {
        if (this.selectedMobileOfferSettings != null) {
            int stopMessageLength = (this.selectedMobileOfferSettings.getStopCustomMessage() != null) ? this.selectedMobileOfferSettings.getStopCustomMessage().length() : 0;
            return CHAR_LIMIT - stopMessageLength;
        }
        return CHAR_LIMIT;
    }
    
    public CartesianChartModel getSlopeModel() {  
        CartesianChartModel linearModel = new CartesianChartModel();  
  
        LineChartSeries series1 = new LineChartSeries();  
        series1.setLabel(message.getString("lookupsPerTime"));
        //series1.setLabel("Lookups / Time");  
  
        // first point is x = min_time, y = 0, 
        // second point is x = max_time, y = max_distance
        // third point is x = max_time + 10%, y = max_distance
        MobileOfferSettings mos = this.getSelectedMobileOfferSettings();
        series1.set(Math.round(mos.getMinLookupWaitTime()/TimeConstants.ONE_MINUTE), 0);
        series1.set(Math.round(mos.getMaxLookupWaitTime()/TimeConstants.ONE_MINUTE), mos.getMaxDistance());
        series1.set(getMaxX(), mos.getMaxDistance());
        
        linearModel.addSeries(series1);  
        return linearModel;
    }
    
    public Long getMaxX()
    {
        MobileOfferSettings mos = this.getSelectedMobileOfferSettings();
        return mos.getMaxLookupWaitTime()/TimeConstants.ONE_MINUTE + Math.round(Math.ceil((mos.getMaxLookupWaitTime()/TimeConstants.ONE_MINUTE)* 1.1));
    }
    
    public Long getMaxY()
    {
        MobileOfferSettings mos = this.getSelectedMobileOfferSettings();
        return Math.round(Math.ceil(mos.getMaxDistance() * 1.1));
    }
}