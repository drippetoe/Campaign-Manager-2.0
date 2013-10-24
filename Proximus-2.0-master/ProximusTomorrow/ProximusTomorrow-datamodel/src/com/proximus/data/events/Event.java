/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.events;

import com.proximus.data.Company;
import com.proximus.data.User;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 *
 * @author ronald and eric
 */
@Entity
@Table(name = "event")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING, length = 50)
public abstract class Event implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final long QUEUE_DISCARDED = -1L;
    public static final long QUEUE_RAW = 0L;
    public static final long QUEUE_LIVE = 1L;
    public static final long QUEUE_DAILY = 2L;
    public static final long QUEUE_WEEKLY = 3L;
    public static final long QUEUE_MONTHLY = 4L;
    public static final long QUEUE_DONE = 5L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    protected Long id;
    @Column(name = "queue")
    protected Long queue;
    @Column(name = "event_date")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date eventDate;
    @Column(name = "message")
    protected String message;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    protected EventType eventType;
    @ManyToOne
    protected Company company;
    @ManyToOne
    protected User user;

    public Event() {
        this.id = 0L;
        this.eventDate = new Date();
        this.queue = Event.QUEUE_RAW;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Long getQueue() {
        return queue;
    }

    public void setQueue(Long queue) {
        this.queue = queue;
    }

    public void advanceQueue() {
        if (this.queue < Event.QUEUE_DONE) {
            this.queue++;
        }
    }

    /*
     * Reverse
     */
    public void discard() {
        if (this.queue == 0) {
            this.queue = Event.QUEUE_DISCARDED;
        } else {
            this.queue = -1 * Math.abs(this.queue);
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final Event other = (Event) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }

        return true;
    }

    public abstract boolean filter(List<Event> events);
    /*
     * public abstract String toEMail();
     * public abstract String toSMS();
     */

    @Override
    public String toString() {
        return eventType + " [" + queue + "] " + eventDate + ": " + message;
    }
}
