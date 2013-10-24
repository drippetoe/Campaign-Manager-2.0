package com.proximus.data.events;

import com.proximus.data.User;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 *
 * @author eric
 */
@Entity
@Table(name = "event_subscription")
public class EventSubscription implements Serializable {

    private static final long serialVersionUID = 1L;
    /*
     * Using bitwise flags to store subscrition in an int
     */
    public static final int ALERT_RAW = 1; //1
    public static final int ALERT_LIVE = 1 << 1; //2
    public static final int ALERT_DAILY = 1 << 2; //4
    public static final int ALERT_WEEKLY = 1 << 3; //8
    public static final int ALERT_MONTHLY = 1 << 4; //16
    public static final int ALERTS[] = new int[]{ALERT_RAW, ALERT_LIVE, ALERT_DAILY, ALERT_WEEKLY, ALERT_MONTHLY};
    public static final String ALERT_FRIENDLY_NAMES[] = new String[]{"raw", "live", "daily", "weekly", "monthly"};
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;
    @ManyToOne
    private EventType eventType;
    @ManyToOne
    private User user;
    @Column(name = "subscription")
    private Long subscription;

    public EventSubscription() {
        this.id = 0L;
        this.subscription = 0L;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getSubscription() {
        return subscription;
    }

    public void setSubscription(Long subscription) {
        this.subscription = subscription;
    }

    public void addSubscription(int alert) {
        this.subscription |= alert;
    }

    public void removeSubscription(int alert) {
        this.subscription = this.subscription & ~alert;
    }

    public void clearSubscriptions() {
        this.subscription = 0L;
    }

    public String friendlySubscriptions() {
        String prefix = "";
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ALERTS.length; i++) {
            if (hasSubscription(ALERTS[i])) {
                    sb.append(prefix);
                    prefix=",";
                    sb.append(ALERT_FRIENDLY_NAMES[i]);
            }
        }
        return sb.toString();
    }

    public boolean hasSubscription(int alert) {
        return ((this.subscription & alert) != 0);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + (this.eventType != null ? this.eventType.hashCode() : 0);
        hash = 61 * hash + (this.user != null ? this.user.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EventSubscription other = (EventSubscription) obj;
        if (this.eventType != other.eventType && (this.eventType == null || !this.eventType.equals(other.eventType))) {
            return false;
        }
        if (this.user != other.user && (this.user == null || !this.user.equals(other.user))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "UserEventSubscription{" + "id=" + id + ", eventType=" + eventType + ", user=" + user + ", subscription=" + subscription + '}' + friendlySubscriptions();
    }
}
