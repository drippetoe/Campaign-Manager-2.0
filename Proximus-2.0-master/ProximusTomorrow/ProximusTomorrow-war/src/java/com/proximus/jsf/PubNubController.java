/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf;

import com.proximus.data.PubNubKey;
import com.proximus.helper.util.JsfUtil;
import com.proximus.jsf.datamodel.NotificationDataModel;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.PubNubKeyManagerLocal;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;



/**
 *
 * @author Gilberto Gaxiola
 */
@ManagedBean(name = "pubNubController")
@SessionScoped
public class PubNubController extends AbstractController implements Serializable {
    
    private static final long serialVersionUID = 1;
    @EJB
    PubNubKeyManagerLocal pubNubKeyMgr;
    @EJB
    CompanyManagerLocal companyMgr;
    private PubNubKey newNotification;
    private PubNubKey selectedNotification;    
    private NotificationDataModel notificationDataModel;
    private List<PubNubKey> filteredPubNubKeys;
    
    public PubNubController() {
        
    }
    
    public PubNubKey getNewNotification() {
        if (newNotification == null) {
            newNotification = new PubNubKey();
        }
        return newNotification;
    }
    
    public void setNewNotification(PubNubKey newNotification) {
        this.newNotification = newNotification;
    }
    
    public PubNubKey getSelectedNotification() {
        if (selectedNotification == null) {
            selectedNotification = new PubNubKey();
        }
        return selectedNotification;
    }
    
    public void setSelectedNotification(PubNubKey selectedNotification) {
        this.selectedNotification = selectedNotification;
    }
    
    public NotificationDataModel getNotificationDataModel() {
        if (notificationDataModel == null) {
            populateNotificationDataModel();
        }
        return notificationDataModel;
    }
    
    public void setNotificationDataModel(NotificationDataModel notificationDataModel) {
        this.notificationDataModel = notificationDataModel;
    }

    public List<PubNubKey> getFilteredPubNubKeys() {
        return filteredPubNubKeys;
    }

    public void setFilteredPubNubKeys(List<PubNubKey> filteredPubNubKeys) {
        this.filteredPubNubKeys = filteredPubNubKeys;
    }
    
    private void populateNotificationDataModel() {
        List<PubNubKey> keys = pubNubKeyMgr.findAllByCompany(companyMgr.getCompanybyId(this.getCompanyIdFromSession()));
        this.notificationDataModel = new NotificationDataModel(keys);
        filteredPubNubKeys = new ArrayList<PubNubKey>(keys);
    }
    
    public void createNewNotification() {
        
        if (newNotification == null) {
            JsfUtil.addErrorMessage("Please Fill out your PubNub Settings.");
            return;
        }
        boolean noPublish = (newNotification.getPublishKey() == null || newNotification.getPublishKey().isEmpty());
        boolean noSubcribe = (newNotification.getSubscribeKey() == null || newNotification.getSubscribeKey().isEmpty());
        
        if (noPublish && noSubcribe) {
            JsfUtil.addErrorMessage("Either the Publish Key or the Subscribe Key must be set before saving");
            return;
        }
        
        newNotification.setCompany(companyMgr.find(this.getCompanyIdFromSession()));
        if(pubNubKeyMgr.doesPubNubKeyChannelExistsByCompany(newNotification.getChannel(),newNotification.getCompany())) {
            JsfUtil.addErrorMessage("You already have a PubNub Key with channel: " + newNotification.getChannel() +".\nPlease choose another name");
            return;
        }
        pubNubKeyMgr.create(newNotification);
        JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("NotificationCreated"));
        prepareList();
        
    }
    
    public String prepareList() {
        this.newNotification = null;
        
        this.selectedNotification = null;
        this.notificationDataModel = null;
        return "/notifications/List?faces-redirect=true";
    }
    
    public boolean editNotification() {        
        if (selectedNotification == null) {
            JsfUtil.addErrorMessage("Please Fill out your PubNub Settings.");
            return false;
        }
        boolean noPublish = (selectedNotification.getPublishKey() == null || selectedNotification.getPublishKey().isEmpty());
        boolean noSubcribe = (selectedNotification.getSubscribeKey() == null || selectedNotification.getSubscribeKey().isEmpty());
        
        if (noPublish && noSubcribe) {
            JsfUtil.addErrorMessage("Either the Publish Key or the Subscribe Key must be set before saving the edits");
            return false;
        }
        
        if(pubNubKeyMgr.doesPubNubKeyChannelExistsByCompany(selectedNotification.getChannel(),selectedNotification.getCompany())) {
            JsfUtil.addErrorMessage("You already have a PubNub Key with name: " + selectedNotification.getChannel() +".\nPlease choose another name");
            return false;
        }
        
        pubNubKeyMgr.update(selectedNotification);
        prepareList();
        return true;
        
    }
    
    public void deleteNotification() {
        if (selectedNotification == null) {
            return;
        }
        try {
            pubNubKeyMgr.delete(selectedNotification);
        } catch (Exception e) {
            JsfUtil.addErrorMessage("Can't Delete Notification");
        }
        prepareList();
    }
}

