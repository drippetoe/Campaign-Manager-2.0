/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.events;

import com.proximus.data.License;
import com.proximus.data.User;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 *
 * @author ronald
 */
@Entity
@Table(name = "event_type")
public class EventType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private String id;
    @Column(name = "access_level")
    private Long accessLevel;
    @OneToMany(mappedBy = "eventType", cascade = {CascadeType.MERGE})
    private List<EventSubscription> eventSubsciptions;
    @Column(name = "license")
    private String license;

    public EventType() {
        this.id = "UNDEFINED";
        this.accessLevel = 1L;
    }

    public EventType(String id) {
        this.id = id;
        this.accessLevel = 1L;
        this.license = null;
    }

    public EventType(String id, long accessLevel) {
        this.id = id;
        this.accessLevel = accessLevel;
        this.license = null;
    }

    public EventType(String id, String license) {
        this.id = id;
        this.accessLevel = 1L;
        this.license = license;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final EventType other = (EventType) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    public Long getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(Long accessLevel) {
        this.accessLevel = accessLevel;
    }

    public List<EventSubscription> getEventSubsciptions() {
        return eventSubsciptions;
    }

    public void setEventSubsciptions(List<EventSubscription> eventSubsciptions) {
        this.eventSubsciptions = eventSubsciptions;
    }

    public void addEventSubsciptions(EventSubscription eventSubsciption) {
        this.eventSubsciptions.add(eventSubsciption);
    }

    public void removeEventSubsciptions(EventSubscription eventSubsciption) {
        this.eventSubsciptions.remove(eventSubsciption);
    }

    public boolean hasAccess(User u) {
        if (u.getRole().getPriority() <= this.accessLevel) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return this.id;
    }
}
