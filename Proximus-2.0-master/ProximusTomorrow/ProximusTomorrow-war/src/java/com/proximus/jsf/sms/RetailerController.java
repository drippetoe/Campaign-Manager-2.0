/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.sms;

import com.proximus.data.Company;
import com.proximus.data.sms.Retailer;
import com.proximus.helper.util.JsfUtil;
import com.proximus.jsf.AbstractController;
import com.proximus.jsf.datamodel.sms.RetailerDataModel;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.sms.PropertyManagerLocal;
import com.proximus.manager.sms.RetailerManagerLocal;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author ronald
 */
@ManagedBean(name = "retailerController")
@SessionScoped
public class RetailerController extends AbstractController implements Serializable {

    private static final long serialVersionUID = 1;
    @EJB
    CompanyManagerLocal companyMgr;
    @EJB
    RetailerManagerLocal retailerMgr;
    @EJB
    PropertyManagerLocal propertyMgr;
    private Retailer newRetailer;
    private RetailerDataModel retailerModel;
    private List<Retailer> filteredRetailers;
    private Retailer selectedRetailer;
    private ResourceBundle message;

    public RetailerController() {
        message = this.getHttpSession().getMessages();
    }

    public Retailer getNewRetailer() {
        if (newRetailer == null) {
            newRetailer = new Retailer();
        }
        return newRetailer;
    }

    public void setNewRetailer(Retailer newRetailer) {
        this.newRetailer = newRetailer;
    }

    public RetailerDataModel getRetailerModel() {
        if (retailerModel == null) {
            populateRetailerModel();
        }
        return retailerModel;
    }

    public void setRetailerModel(RetailerDataModel retailerModel) {
        this.retailerModel = retailerModel;
    }

    public List<Retailer> getFilteredRetailers() {
        return filteredRetailers;
    }

    public void setFilteredRetailers(List<Retailer> filteredRetailers) {
        this.filteredRetailers = filteredRetailers;
    }

    public Retailer getSelectedRetailer() {
        if (selectedRetailer == null) {
            selectedRetailer = new Retailer();
        }
        return selectedRetailer;
    }

    public void setSelectedRetailer(Retailer selectedRetailer) {
        this.selectedRetailer = selectedRetailer;
    }

    public void populateRetailerModel() {
        List<Retailer> rets = retailerMgr.getAllRetailersByBrand(this.getBrandFromSession());
        retailerModel = new RetailerDataModel(rets);
        filteredRetailers = new ArrayList<Retailer>(rets);
    }

    public String prepareList() {
        retailerModel = null;
        newRetailer = null;
        selectedRetailer = null;

        return "/retailer/List?faces-redirect=true";
    }

    private boolean isRetailerNew(String retailerName) {
        Company c = companyMgr.find(this.getCompanyIdFromSession());
        Retailer temp = retailerMgr.getRetailerByName(c.getBrand(), retailerName);
        if (temp != null) {
            JsfUtil.addErrorMessage("Duplicated! This " + temp.getClass().getSimpleName() + " already exists");
            return false;
        } else {
            return true;
        }
    }

    public void createNewRetailer(Retailer retailer) {
        if (retailer == null || retailer.getName() == null || retailer.getName().isEmpty()) {
            JsfUtil.addErrorMessage("Please add a name for your " + retailer.getClass().getSimpleName() + ".");
            retailer = new Retailer();
            return;
        }

        if (isRetailerNew(retailer.getName()) != false) {
            Company c = companyMgr.find(this.getCompanyIdFromSession());
            retailer.setBrand(c.getBrand());
            retailerMgr.update(retailer);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("RetailerCreated"));
            prepareList();
        }
    }

    public boolean editRetailer() {
        try {
            if (selectedRetailer.getName() == null || selectedRetailer.getName().isEmpty()) {
                JsfUtil.addErrorMessage("Please enter a name.");
                prepareList();
                return false;
            }
            retailerMgr.update(selectedRetailer);
            prepareList();
        } catch (Exception e) {
            JsfUtil.addErrorMessage("Couldn't Persist " + selectedRetailer.getClass().getSimpleName() + ".");
            prepareList();
            return false;
        }
        return true;
    }

    public void deleteRetailer() {
        if (selectedRetailer == null) {
            return;
        }
        if (selectedRetailer.getProperties() != null && !selectedRetailer.getProperties().isEmpty()) {
            String prop = selectedRetailer.getProperties().size() == 1 ? message.getString("property"):message.getString("properties");  
            String msg = message.getString("ErrorDelete") +" " + selectedRetailer.getName() + " " + message.getString("ErrorDeleteTwo") + " " +  selectedRetailer.getProperties().size() + " " + prop;
            JsfUtil.addErrorMessage(msg);
            
            prepareList();
            return;

        }
        try {
            retailerMgr.delete(selectedRetailer);
        } catch (Exception e) {
            JsfUtil.addErrorMessage("Can't delete " + selectedRetailer.getName() + ".");
            prepareList();
            return;
        }

        JsfUtil.addSuccessMessage(message.getString("retailerDeleteSuccess"));
        prepareList();

    }
}
