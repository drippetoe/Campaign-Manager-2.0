/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.data.events;

import com.proximus.data.Company;
import com.proximus.data.User;
import com.proximus.data.sms.MobileOffer;
import java.io.Serializable;
import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.joda.time.DateTime;
import org.joda.time.Hours;

/**
 *
 * @author Eric and Ron
 */
@Entity
@Table(name = "event_mobile_offer")
@DiscriminatorValue("MOBILE_OFFER")
public class MobileOfferEvent extends Event implements Serializable {

    private static final long serialVersionUID = 1L;
    public static EventType TYPE = new EventType("MOBILE_OFFER");
    public static EventType TYPE_CREATED = new EventType("MOBILE_OFFER_CREATED");
    public static EventType TYPE_APPROVED = new EventType("MOBILE_OFFER_APPROVED");
    public static EventType TYPE_DELETED = new EventType("MOBILE_OFFER_DELETED");
    public static EventType TYPE_UPDATED = new EventType("MOBILE_OFFER_UPDATED");
    public static EventType[] EVENT_TYPES = new EventType[]{MobileOfferEvent.TYPE_CREATED, MobileOfferEvent.TYPE_DELETED, MobileOfferEvent.TYPE_UPDATED, MobileOfferEvent.TYPE_APPROVED};
    @ManyToOne
    private MobileOffer mobileOffer;

    public MobileOfferEvent() {
        super();
        this.eventType = MobileOfferEvent.TYPE;
        this.queue = Event.QUEUE_DAILY;
    }

    public MobileOfferEvent(EventType eventType, MobileOffer mobileOffer, Company company, User user) {
        super();
        this.eventType = eventType;
        this.mobileOffer = mobileOffer;
        this.company = company;
        this.user = user;
        this.queue = Event.QUEUE_DAILY;
    }

    public MobileOfferEvent(EventType eventType, MobileOffer mobileOffer, String message) {
        super();
        this.eventType = eventType;
        this.mobileOffer = mobileOffer;
        this.message = message;
        this.queue = Event.QUEUE_DAILY;
    }

    public MobileOffer getMobileOffer() {
        return mobileOffer;
    }

    public void setMobileOffer(MobileOffer mobileOffer) {
        this.mobileOffer = mobileOffer;
    }

    @Override
    public boolean filter(List<Event> events) {
        if (this.mobileOffer == null) {
            return false;
        }
        if (this.eventType.equals(MobileOfferEvent.TYPE)) {
            return false;
        } else if (this.eventType.equals(MobileOfferEvent.TYPE_CREATED)) {
            return true;
        } else if (this.eventType.equals(MobileOfferEvent.TYPE_UPDATED)) {
            for (Event event : events) {
                if (event.getEventType().equals(CampaignEvent.TYPE_UPDATED)) {
                    MobileOfferEvent moe = (MobileOfferEvent) event;
                    if (moe.getMobileOffer().equals(this.mobileOffer)) {
                        DateTime startDate = new DateTime(moe.getEventDate());
                        DateTime endDate = new DateTime(this.eventDate);
                        int difference = Hours.hoursBetween(startDate, endDate).getHours();
                        if (difference < 1 && moe.getUser().equals(this.getUser())) {
                            return false;
                        }
                    }
                }
            }
            return true;
        } else if (this.eventType.equals(MobileOfferEvent.TYPE_APPROVED)) {
            return true;
        } else if (this.eventType.equals(MobileOfferEvent.TYPE_DELETED)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return super.toString() + ", " + mobileOffer.getName();
    }
}
