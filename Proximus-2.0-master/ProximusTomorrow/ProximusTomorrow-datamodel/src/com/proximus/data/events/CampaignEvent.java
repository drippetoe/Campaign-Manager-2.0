package com.proximus.data.events;

import com.proximus.data.Campaign;
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
@Table(name = "event_campaign")
@DiscriminatorValue("CAMPAIGN")
public class CampaignEvent extends Event implements Serializable {

    private static final long serialVersionUID = 1L;
    public static EventType TYPE = new EventType("CAMPAIGN");
    public static EventType TYPE_CREATED = new EventType("CAMPAIGN_CREATED");
    public static EventType TYPE_DELETED = new EventType("CAMPAIGN_DELETED");
    public static EventType TYPE_EXPIRED = new EventType("CAMPAIGN_EXPIRED");
    public static EventType TYPE_UPDATED = new EventType("CAMPAIGN_UPDATED");
    public static EventType[] EVENT_TYPES = new EventType[]{TYPE_CREATED, TYPE_DELETED, TYPE_EXPIRED, TYPE_UPDATED};
    @ManyToOne
    private Campaign campaign;

    public CampaignEvent() {
        super();
        this.eventType = CampaignEvent.TYPE;
        this.campaign = null;
    }

    public CampaignEvent(EventType eventType, Campaign campaign) {
        super();
        this.eventType = eventType;
        this.campaign = campaign;
    }

    public CampaignEvent(EventType eventType, Campaign campaign, String message) {
        super();
        this.eventType = eventType;
        this.campaign = campaign;
        this.message = message;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    @Override
    public boolean filter(List<Event> events) {
        if (this.campaign == null) {
            return false;
        }
        if (this.eventType.equals(CampaignEvent.TYPE)) {
            return false;
        } else if (this.eventType.equals(CampaignEvent.TYPE_CREATED)) {
            return true;
        } else if (this.eventType.equals(CampaignEvent.TYPE_UPDATED)) {
            for (Event event : events) {
                if (event.getEventType().equals(CampaignEvent.TYPE_UPDATED)) {
                    CampaignEvent cev = (CampaignEvent) event;
                    if (cev.getCampaign().equals(this.campaign)) {
                        DateTime startDate = new DateTime(cev.getEventDate());
                        DateTime endDate = new DateTime(this.eventDate);
                        int difference = Hours.hoursBetween(startDate, endDate).getHours();
                        if (difference < 1 && cev.getUser().equals(this.getUser())) {
                            System.err.println("DISCARD MATCH: "+this.campaign.getName()+" = "+cev.getCampaign().getName());
                            return false;
                        }
                    }
                }
            }
            return true;
        } else if (this.eventType.equals(CampaignEvent.TYPE_DELETED)) {
            return true;
        } else if (this.eventType.equals(CampaignEvent.TYPE_EXPIRED)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return super.toString() + ", " + campaign.getName();
    }
}
