/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf;

import com.proximus.data.Company;
import com.proximus.data.User;
import com.proximus.data.events.EventSubscription;
import com.proximus.data.events.EventType;
import com.proximus.helper.util.JsfUtil;
import com.proximus.jsf.datamodel.EventSubscriptionDataModel;
import com.proximus.jsf.datamodel.EventTypeDataModel;
import com.proximus.manager.UserManagerLocal;
import com.proximus.manager.events.EventSubscriptionManagerLocal;
import com.proximus.manager.events.EventTypeManagerLocal;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author ronald
 */
@ManagedBean(name = "eventSubscriptionController")
@SessionScoped
public class EventSubscriptionController extends AbstractController implements Serializable {

    private static final long serialVersionUID = 1;
    @EJB
    EventTypeManagerLocal eventTypeMgr;
    @EJB
    EventSubscriptionManagerLocal eventSubscriptionMgr;
    @EJB
    UserManagerLocal userMgr;
    private EventType[] selectedEventTypes;
    private List<EventType> eventTypeList;
    private EventTypeDataModel eventTypeModel;
    private EventSubscriptionDataModel eventSubscriptionModel;
    private List<String> selectedAlerts;
    private Map<String, Integer> alerts;
    private Company company;
    private User user;
    private List<EventSubscription> subscriptions;
    private EventSubscription selectedSubscription;
    private ResourceBundle message;

    public EventSubscriptionController() {
        message = this.getHttpSession().getMessages();
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public EventSubscription getSelectedSubscription() {
        if (selectedSubscription == null) {
            selectedSubscription = new EventSubscription();
        }
        return selectedSubscription;
    }

    public void setSelectedSubscription(EventSubscription selectedSubscription) {
        this.selectedSubscription = selectedSubscription;
    }

    public User getUser() {
        if (user == null) {
            user = userMgr.find(this.getUserIdFromSession());
        }
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<EventSubscription> getSubscriptions() {
        if (this.subscriptions == null) {
            populateEventSubscriptions();
        }
        return subscriptions;
    }

    public void setSubscriptions(List<EventSubscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    private void populateEventSubscriptions() {
        subscriptions = eventSubscriptionMgr.getEventSubscriptionByUser(getUser());
    }

    public List<EventType> getEventTypeList() {
        if (eventTypeList == null) {
            populateEventTypeList();
        }
        return eventTypeList;
    }

    public void setEventTypeList(List<EventType> eventTypeList) {
        this.eventTypeList = eventTypeList;
    }

    private void populateEventTypeList() {
        eventTypeList = eventTypeMgr.getAvailibleEventTypes(company.getLicense(), getUser());
        //eventTypeList = eventTypeMgr.getEventTypesFromCompanyAndAccessLevel(company, getUser().getRole().getPriority());
        if (eventTypeList != null) {
            List<EventSubscription> eventSubscriptions = getSubscriptions();
            if (eventSubscriptions != null) {
                for (EventSubscription eventSubscription : eventSubscriptions) {
                    if (eventTypeList.contains(eventSubscription.getEventType())) {
                        eventTypeList.remove(eventSubscription.getEventType());
                    }
                }
            }
        }
    }

    public EventType[] getSelectedEventTypes() {
        return selectedEventTypes;
    }

    public void setSelectedEventTypes(EventType[] selectedEventTypes) {
        this.selectedEventTypes = selectedEventTypes;
    }

    public EventSubscriptionDataModel getEventSubscriptionModel() {
        if (this.eventSubscriptionModel == null) {
            populateEventSubscriptionModel();
        }
        return eventSubscriptionModel;
    }

    public void setEventSubscriptionModel(EventSubscriptionDataModel eventSubscriptionModel) {
        this.eventSubscriptionModel = eventSubscriptionModel;
    }

    private void populateEventSubscriptionModel() {
        eventSubscriptionModel = new EventSubscriptionDataModel(getSubscriptions());
    }

    public EventTypeDataModel getEventTypeModel() {
        if (this.eventTypeModel == null) {
            populateEventTypeModel();
        }
        return eventTypeModel;
    }

    public void setEventTypeModel(EventTypeDataModel eventTypeModel) {
        this.eventTypeModel = eventTypeModel;
    }

    private void populateEventTypeModel() {
        eventTypeModel = new EventTypeDataModel(getEventTypeList());
    }

    public List<String> getSelectedAlerts() {
        return selectedAlerts;
    }

    public void setSelectedAlerts(List<String> selectedAlerts) {
        this.selectedAlerts = selectedAlerts;
    }

    public Map<String, Integer> getAlerts() {
        if (alerts.isEmpty()) {
            populateAlerts();
        }
        return alerts;
    }

    public void setAlerts(Map<String, Integer> alerts) {
        this.alerts = alerts;
    }

    private void populateAlerts() {
        for (int i = 0; i < EventSubscription.ALERT_FRIENDLY_NAMES.length; i++) {
            alerts.put(message.getString(EventSubscription.ALERT_FRIENDLY_NAMES[i]), EventSubscription.ALERTS[i]);
        }
    }

    public void subscribeToEventType() {
        if (selectedEventTypes == null || selectedEventTypes.length == 0) {
            JsfUtil.addErrorMessage(message.getString("selectedEventError"));
            return;
        }

        if (selectedAlerts == null || selectedAlerts.isEmpty()) {
            JsfUtil.addErrorMessage(message.getString("selectedAlertError"));
            return;
        }

        for (EventType eventType : selectedEventTypes) {
            EventSubscription es = new EventSubscription();
            es.setUser(user);
            es.setEventType(eventType);
            for (String alert : selectedAlerts) {
                Integer value = Integer.parseInt(alert);
                es.addSubscription(value);
            }
            eventSubscriptionMgr.create(es);
        }
        JsfUtil.addSuccessMessage(message.getString("subscriptionCreated"));
        prepareEventSubscription();
    }

    public String unsubscribeFromEventType() {
        if (selectedSubscription != null) {
            try {
                eventSubscriptionMgr.delete(selectedSubscription);
            } catch (Exception ex) {
                JsfUtil.addErrorMessage(message.getString("deleteSubscriptionError"));
            }
        }
        return prepareEventSubscription();
    }

    private void prepareVars() {
        this.selectedEventTypes = null;
        this.eventTypeModel = null;
        this.selectedAlerts = null;
        this.alerts = new LinkedHashMap<String, Integer>();
        company = this.getCompanyFromSession();
        user = null;
        subscriptions = null;
        eventSubscriptionModel = null;
        selectedSubscription = null;
        eventTypeList = null;
    }

    public String prepareEventSubscription() {
        prepareVars();
        return "/event/EventSubscription?faces-redirect=true";
    }
}
