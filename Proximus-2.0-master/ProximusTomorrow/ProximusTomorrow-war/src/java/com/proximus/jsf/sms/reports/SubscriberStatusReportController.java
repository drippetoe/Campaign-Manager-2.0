/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.sms.reports;

import com.proximus.data.sms.Subscriber;
import com.proximus.manager.sms.SubscriberManagerLocal;
import com.proximus.registration.client.Registrar;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author ronald
 */
@ManagedBean(name = "subscriberStatusReportController")
@SessionScoped
public class SubscriberStatusReportController implements Serializable {

    private static final long serialVersionUID = 1;
    @EJB
    SubscriberManagerLocal subscriberMgr;
    private List<Subscriber> subscriberList;
    private Subscriber selectedSubscriber;

    public Subscriber getSelectedSubscriber() {
        if (selectedSubscriber == null) {
            selectedSubscriber = new Subscriber();
        }
        return selectedSubscriber;
    }

    public void setSelectedSubscriber(Subscriber selectedSubscriber) {
        this.selectedSubscriber = selectedSubscriber;
    }

    public List<Subscriber> getSubscriberList() {
        if (subscriberList == null) {
            populateSubscriberList();
        }
        return subscriberList;
    }

    public void setSubscriberList(List<Subscriber> subscriberList) {
        this.subscriberList = subscriberList;
    }

    private void populateSubscriberList() {
        subscriberList = subscriberMgr.findAllUnsynchedSubscribers();
    }

    public void obtainSubscriberStatus() {
        Subscriber subscriber = subscriberMgr.findByMsisdn(selectedSubscriber.getMsisdn());
        Registrar registrar = new Registrar();
        String locaidStatus;
        try {
            locaidStatus = registrar.getSingleMsisdnStatus(selectedSubscriber.getMsisdn());

        } catch (Exception e) {
            locaidStatus = null;
        }
        subscriber.setLocaidStatus(locaidStatus);
        subscriberMgr.update(subscriber);
        prepareList();
    }

    private void prepareVars() {
        subscriberList = null;
        selectedSubscriber = null;
    }

    public String prepareList() {
        prepareVars();
        return "/geo-reports/SubscriberStatusChecker?faces-redirect=true";
    }
}
