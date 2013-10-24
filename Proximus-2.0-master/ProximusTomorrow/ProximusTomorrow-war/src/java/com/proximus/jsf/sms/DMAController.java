/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.sms;

import com.proximus.data.sms.Country;
import com.proximus.data.sms.DMA;
import com.proximus.helper.util.JsfUtil;
import com.proximus.jsf.AbstractController;
import com.proximus.jsf.datamodel.sms.DMADataModel;
import com.proximus.manager.sms.CountryManagerLocal;
import com.proximus.manager.sms.DMAManagerLocal;
import com.proximus.manager.sms.PropertyManagerLocal;
import com.proximus.util.ListChoosers;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import org.primefaces.context.RequestContext;

/**
 *
 * @author ronald
 */
@ManagedBean(name = "dmaController")
@SessionScoped
public class DMAController extends AbstractController implements Serializable {

    private static final long serialVersionUID = 1;
    @EJB
    DMAManagerLocal dmaMgr;
    @EJB
    CountryManagerLocal countryMgr;
    @EJB
    PropertyManagerLocal propertyMgr;
    private DMA newDMA;
    private DMADataModel dmaModel;
    private List<DMA> filteredDMAs;
    private DMA selectedDMA;
    private List<String> selectedCountries;
    private List<String> selectedStates = ListChoosers.getStates();
    private ResourceBundle message;

    public DMAController() {
        message = this.getHttpSession().getMessages();
    }

    public DMADataModel getDmaModel() {
        if (dmaModel == null) {
            populateDMAModel();
        }
        return dmaModel;
    }

    public void setDmaModel(DMADataModel dmaModel) {
        this.dmaModel = dmaModel;
    }

    public List<DMA> getFilteredDMAs() {
        return filteredDMAs;
    }

    public void setFilteredDMAs(List<DMA> filteredDMAs) {
        this.filteredDMAs = filteredDMAs;
    }

    public DMA getNewDMA() {
        if (newDMA == null) {
            newDMA = new DMA();
        }
        return newDMA;
    }

    public void setNewDMA(DMA newDMA) {
        this.newDMA = newDMA;
    }

    public DMA getSelectedDMA() {
        if (selectedDMA == null) {
            selectedDMA = new DMA();
        }
        return selectedDMA;
    }

    public void setSelectedDMA(DMA selectedDMA) {
        this.selectedDMA = selectedDMA;
    }

    public List<String> getSelectedCountries() {
        if (selectedCountries == null) {
            List<Country> list = countryMgr.findAllSorted();
            selectedCountries = new ArrayList<String>();
            for (Country c : list) {
                selectedCountries.add(c.getName());
            }
        }
        return selectedCountries;
    }

    public void setSelectedCountries(List<String> selectedCountries) {
        this.selectedCountries = selectedCountries;
    }

    public List<String> getSelectedStates() {
        return selectedStates;
    }

    public void setSelectedStates(List<String> selectedStates) {
        this.selectedStates = selectedStates;
    }

    private void populateDMAModel() {
        List<DMA> dma = dmaMgr.getAllSorted();
        dmaModel = new DMADataModel(dma);
        filteredDMAs = new ArrayList<DMA>(dma);
    }

    public String prepareList() {
        dmaModel = null;
        newDMA = null;
        selectedDMA = null;
        return "/dma/List?faces-redirect=true";
    }

    public void addDma(DMA dma) {

        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg = null;
        boolean validation = false;
        if (dma == null || dma.getName() == null || dma.getName().isEmpty()) {
            validation = false;
            context.addCallbackParam("validation", validation);
            JsfUtil.addErrorMessage(message.getString("dmaNameNotEmptyError"));
            return;
        }
        List<String> dmaNames = new ArrayList<String>();
        if (dmaModel.getDMAData() != null && !dmaModel.getDMAData().isEmpty()) {
            for (DMA d : dmaModel.getDMAData()) {
                dmaNames.add(d.getName().toLowerCase());
            }
            if (dmaNames.contains(dma.getName().toLowerCase())) {
                validation = false;
                context.addCallbackParam("validation", validation);
                JsfUtil.addErrorMessage(message.getString("dmaDuplicatedError"));
                return;
            }
        }
        dmaMgr.create(dma);
        validation = true;
        context.addCallbackParam("validation", validation);
        JsfUtil.addSuccessMessage(message.getString("DmaCreated"));
        prepareList();
    }

    public void editDMA(ActionEvent event) {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg = null;
        boolean validation = false;
        try {
            if (selectedDMA.getName() == null || selectedDMA.getName().isEmpty()) {
                validation = false;
                context.addCallbackParam("validation", validation);
                JsfUtil.addErrorMessage(message.getString("dmaNameNotEmptyError"));
                return;
            }
            dmaMgr.update(selectedDMA);
            prepareList();
        } catch (Exception e) {
            context.addCallbackParam("validation", false);
            JsfUtil.addErrorMessage(message.getString("dmaPersistError"));

            prepareList();
            return;
        }
        context.addCallbackParam("validation", true);
    }

    public void deleteDMA() {
        if (selectedDMA == null) {
            return;
        }
        try {
            if (propertyMgr.propertiesAssociatedToDma(selectedDMA)) {
                JsfUtil.addErrorMessage("DMA is associated to a Property can't be deleted");
                return;
            } else {
                dmaMgr.delete(selectedDMA);
            }

        } catch (Exception e) {
        }
        prepareList();
    }
}
