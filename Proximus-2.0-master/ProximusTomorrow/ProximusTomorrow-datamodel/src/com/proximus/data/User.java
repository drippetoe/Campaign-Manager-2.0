/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data;

import com.proximus.data.events.EventType;
import com.proximus.data.events.EventSubscription;
import com.proximus.data.sms.Property;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author ronald
 */
@Entity
@DiscriminatorValue("GENERIC")
@Table(name = "user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;
    @ManyToOne
    private Role role;
    @Size(max = 255)
    @Basic(optional = false)
    @NotNull
    @Column(name = "username", unique = true)
    private String userName;
    @Size(min = 8)
    @NotNull
    @Basic(optional = false)
    @Column(name = "password")
    String password;
    @ManyToMany
    @JoinTable(name = "company_user", joinColumns =
    @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns =
    @JoinColumn(name = "company_id", referencedColumnName = "id"))
    private List<Company> companies;
    @ManyToMany
    @JoinTable(name = "user_property", joinColumns =
    @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns =
    @JoinColumn(name = "property_id", referencedColumnName = "id"))
    private List<Property> properties;
//    @ManyToMany
//    @JoinTable(name = "user_event_type", joinColumns =
//    @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns =
//    @JoinColumn(name = "event_id", referencedColumnName = "id"))
//    private List<EventType> eventTypes;
    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<EventSubscription> eventSubscription;
    @Size(max = 45)
    @NotNull
    @Column(name = "first_name")
    private String firstName;
    @Size(max = 5)
    @Column(name = "locale")
    private String locale = "en";
    @Size(max = 45)
    @NotNull
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "time_zone")
    private String timezone;
    @Column(name = "api_password")
    private String apiPassword;

    public User() {
        id = 0L;
        companies = new ArrayList<Company>();
        eventSubscription = new ArrayList<EventSubscription>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Company> getCompanies() {
        Collections.sort(companies, new Comparator<Company>() {
            @Override
            public int compare(Company t, Company t1) {
                return (t.getName() + t.getId()).compareTo(t1.getName() + t1.getId());
            }
        });
        return companies;
    }

    public void addCompany(Company c) {
        this.companies.add(c);
    }

    public void removeCompany(Company c) {
        this.companies.remove(c);
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

    public List<EventSubscription> getEventSubscriptions() {
        return eventSubscription;
    }

    public void setEventSubscriptions(List<EventSubscription> eventSubscription) {
        this.eventSubscription = eventSubscription;
    }

    public void addEventSubscriptions(EventSubscription eventSubscription) {
        if (!this.eventSubscription.contains(eventSubscription)) {
            this.eventSubscription.add(eventSubscription);
        }
    }

    public EventSubscription getEventSubscriptionByType(EventType eventType) {
        for (EventSubscription userEventSubscription : eventSubscription) {
            if (userEventSubscription.getEventType().equals(eventType)) {
                return userEventSubscription;
            }

        }
        return null;
    }

    public void removeEventSubscriptions(EventSubscription eventSubscription) {
        if (this.eventSubscription.contains(eventSubscription)) {
            this.eventSubscription.remove(eventSubscription);
        }
    }

    /*
     public void addEventType(EventType et) {
     this.eventTypes.add(et);
     }

     public void removeEventType(EventType et) {
     this.eventTypes.remove(et);
     }

     public List<EventType> getEventTypes() {
     return eventTypes;
     }

     public void setEventTypes(List<EventType> eventTypes) {
     this.eventTypes = eventTypes;
     }
     */
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password == null || password.isEmpty() || password.length() < 8) {
            return;
        }
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getApiPassword() {
        return apiPassword;
    }

    public void setApiPassword(String apiPassword) {
        this.apiPassword = apiPassword;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        final User other = (User) obj;
        
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        
        if(this.id == 0L) {
            if(!this.userName.equalsIgnoreCase(other.getPassword()) || !this.password.equalsIgnoreCase(other.getPassword())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public List<Property> getProperties() {
        if (properties == null) {
            properties = new ArrayList<Property>();
        }
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public void addProperty(Property p) {
        getProperties().add(p);
    }

    public void removeProperty(Property p) {
        if (getProperties().contains(p)) {
            getProperties().remove(p);
        }
    }

    public void clearProperties() {
        this.setProperties(new ArrayList<Property>());
    }

    @Override
    public String toString() {
        return this.firstName + " " + this.lastName;
    }

    public boolean hasHigherRankThan(User u) {
        if (u.getRole() == null) {
            return true;
        }

        if (this.getRole().getPriority() < u.getRole().getPriority()) {
            return true;
        }
        return false;
    }
}