/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.sms;

import com.proximus.data.sms.Subscriber;
import com.proximus.jsf.AbstractController;
import com.proximus.jsf.datamodel.sms.SubscriberDataModel;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.sms.SubscriberManagerLocal;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author ronald
 */
@ManagedBean(name = "smsUserController")
@SessionScoped
public class SubscriberController extends AbstractController implements Serializable {

    private static final long serialVersionUID = 1;
    //GENERAL
    private SubscriberDataModel subscriberModel;
    private Subscriber newSubscriber;
    private Subscriber selectedSubscriber;
    @EJB
    SubscriberManagerLocal subscriberMgr;
    @EJB
    CompanyManagerLocal companyMgr;

    public Subscriber getNewUser() {
        return newSubscriber;
    }

    public void setNewUser(Subscriber newUser) {
        this.newSubscriber = newUser;
    }

    public SubscriberDataModel getSubscriberModel() {
        if (subscriberModel == null) {
            populateSubscriberModel();
        }
        return subscriberModel;
    }

    public void setSubscriberModel(SubscriberDataModel subscriberModel) {
        this.subscriberModel = subscriberModel;
    }

    private void populateSubscriberModel() {
        subscriberModel = new SubscriberDataModel(subscriberMgr.findAllByCompany(companyMgr.find(getCompanyIdFromSession())));
    }

    public Subscriber getSelectedSubscriber() {
        return selectedSubscriber;
    }

    public void setSelectedSubscriber(Subscriber selectedUser) {
        this.selectedSubscriber = selectedUser;
    }

    public String prepareList() {
        populateSubscriberModel();
        return "/sms-user/List";
    }
}
