/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.sms;

import com.proximus.data.Brand;
import com.proximus.data.app.AppUser;
import com.proximus.data.web.WebOffer;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author ronald
 */
@Entity
@DiscriminatorValue("GENERIC")
@Table(name = "category", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "name", "COMPANY_id"
    })
})
@XmlRootElement(name = "category")
@XmlAccessorType(XmlAccessType.FIELD)
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    protected Long id;
    @Size(max = 255)
    @Column(name = "name")
    protected String name;
    @Size(max = 255)
    @Column(name = "web_safe_name")
    protected String webSafeName;
    @XmlTransient
    @ManyToMany(mappedBy = "categories")
    private List<MobileOffer> mobileOffers;
    @XmlTransient
    @ManyToMany(mappedBy = "categories")
    private List<WebOffer> webOffers;
    @XmlTransient
    @ManyToMany(mappedBy = "categories")
    private List<Subscriber> subscribers;
    @XmlTransient
    @ManyToMany(mappedBy = "categories")
    private List<AppUser> appUsers;
    @XmlTransient
    @ManyToOne
    private Brand brand;
    @XmlTransient
    @ManyToOne
    Locale locale;

    public Category() {
        id = 0L;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebSafeName() {
        return webSafeName;
    }

    public void setWebSafeName(String webSafeName) {
        this.webSafeName = webSafeName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Category other = (Category) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    public List<MobileOffer> getMobileOffers() {
        if (mobileOffers == null) {
            mobileOffers = new ArrayList<MobileOffer>();
        }
        return mobileOffers;
    }

    public void setMobileOffers(List<MobileOffer> mobileOffers) {
        this.mobileOffers = mobileOffers;
    }

    public void addMobileOffer(MobileOffer offer) {
        getMobileOffers().add(offer);
    }

    public void removeMobileOffer(MobileOffer mo) {
        if (getMobileOffers().contains(mo)) {
            getMobileOffers().remove(mo);
        }
    }

    public void clearMobileOffers() {
        this.mobileOffers = new ArrayList<MobileOffer>();
    }

    public List<WebOffer> getWebOffers() {
        if (webOffers == null) {
            webOffers = new ArrayList<WebOffer>();
        }
        return webOffers;
    }

    public void setWebOffers(List<WebOffer> webOffers) {
        this.webOffers = webOffers;
    }

    public void addWebOffer(WebOffer offer) {
        getWebOffers().add(offer);
    }

    public void removeWebOffer(WebOffer wo) {
        if (getWebOffers().contains(wo)) {
            getWebOffers().remove(wo);
        }
    }

    public void clearWebOffers() {
        this.webOffers = new ArrayList<WebOffer>();
    }

    public List<Subscriber> getSubscribers() {
        if (subscribers == null) {
            subscribers = new ArrayList<Subscriber>();
        }
        return subscribers;
    }

    public void setSubscribers(List<Subscriber> subscribers) {
        this.subscribers = subscribers;
    }

    public void addSubscriber(Subscriber sub) {
        getSubscribers().add(sub);
    }

    public void removeSubscriber(Subscriber sub) {
        if (getSubscribers().contains(sub)) {
            getSubscribers().remove(sub);
        }
    }

    public void clearSubscribers() {
        this.subscribers = new ArrayList<Subscriber>();
    }

    public List<AppUser> getAppUsers() {
        return appUsers;
    }

    public void setAppUsers(List<AppUser> appUsers) {
        this.appUsers = appUsers;
    }

    public void addAppUser(AppUser appUser) {
        getAppUsers().add(appUser);
    }

    public void removeAppUser(AppUser appUser) {
        if (getAppUsers().contains(appUser)) {
            getAppUsers().remove(appUser);
        }
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
