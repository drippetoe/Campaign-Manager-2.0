/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.sms;

import com.proximus.data.Company;
import com.proximus.data.User;
import com.proximus.data.web.WebOffer;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
@XmlRootElement(name = "property")
@XmlAccessorType(XmlAccessType.FIELD)
@Table(name = "property")
public class Property implements Serializable {

    private static final long serialVersionUID = 1L;
    @XmlTransient
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    protected Long id;
    @Size(max = 255)
    @Column(name = "name")
    protected String name;
    @XmlTransient
    @Size(max = 10)
    @Column(name = "web_hash")
    protected String webHash;
    @Size(max = 255)
    @Column(name = "address")
    protected String address;
    @Size(max = 255)
    @Column(name = "city")
    protected String city;
    @Size(max = 255)
    @Column(name = "state_province")
    protected String stateProvince;
    @XmlTransient
    @ManyToOne
    protected Country country;
    @Size(max = 255)
    @Column(name = "zipcode")
    protected String zipcode;
    @XmlTransient
    @Column(name = "date_created")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date dateCreated;
    @XmlTransient
    @ManyToOne
    protected DMA dma;
    @XmlTransient
    @ManyToOne
    protected Company company;
    @XmlTransient
    @ManyToMany
    @JoinTable(name = "property_retailer", joinColumns =
    @JoinColumn(name = "property_id", referencedColumnName = "id"), inverseJoinColumns =
    @JoinColumn(name = "retailer_id", referencedColumnName = "id"))
    protected List<Retailer> retailers;
    @XmlTransient
    @ManyToMany(mappedBy = "properties")
    protected List<User> users;
    @XmlTransient
    @ManyToMany(mappedBy = "properties")
    protected List<MobileOffer> mobileOffers;
    @XmlTransient
    @ManyToMany(mappedBy = "properties")
    protected List<WebOffer> webOffers;
    @XmlTransient
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "property")
    protected List<Keyword> keywords;
    @XmlTransient
    protected static final String baseDigits = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    @XmlTransient
    protected static final int base = 62;
    @XmlTransient
    public static final int PROPERTY_ADDRESS_LIMIT = 35;
    @XmlTransient
    public static final String ADDRESS_REGEX = "#PROX#ADDRESS#PROX#";
    @XmlTransient
    public static final String PROPERTY_REGEX = "#PROX#PROPERTY#PROX#";
    @XmlTransient
    public static final String ADDRESS_WEB = "{ADDRESS}";
    @XmlTransient
    public static final String PROPERTY_WEB = "{PROPERTY}";

    public Property() {
        id = 0L;
        dateCreated = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        createWebHash();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Country getCountry() {
        if (country == null) {
            country = new Country();
        }
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public DMA getDma() {
        return dma;
    }

    public void setDma(DMA dma) {
        this.dma = dma;
    }

    public String getFormattedAddress() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.address);
        stringBuilder.append(",");
        stringBuilder.append(this.city);
        stringBuilder.append(",");
        stringBuilder.append(this.stateProvince);
        stringBuilder.append(" ");
        stringBuilder.append(this.zipcode);
        return stringBuilder.toString();
    }

    /**
     *
     * @return the property as a log-friendly string
     */
    public String getLogString() {
        StringBuilder value = new StringBuilder();
        value.append("[(Property) ");
        value.append(this.getName());
        value.append(" - ");
        value.append(getStateProvince());
        value.append(" (").append(this.getId()).append(")");
        value.append("]");

        return value.toString();
    }

    public List<Retailer> getRetailers() {
        return retailers;
    }

    public void setRetailers(List<Retailer> retailers) {
        this.retailers = retailers;
    }

    public void addRetailers(Retailer retailer) {
        if (this.retailers == null) {
            this.retailers = new ArrayList<Retailer>();
        }
        this.retailers.add(retailer);
    }

    public void clearRetailers() {
        this.retailers = new ArrayList<Retailer>();
    }

    public String getWebHash() {
        return webHash;

    }

    public void setWebHash(String webHash) {
        this.webHash = webHash;

    }

    /**
     * Give a Base 62 representation of the Property's id up to 4 decimal places
     * Up to 14,776,336 different ids
     *
     * @return
     */
    public void createWebHash() {
        long val = this.getId();
        int number = (int) val;
        String hash = number == 0 ? "0" : "";
        int mod = 0;
        while (number != 0) {
            mod = number % base;
            hash = baseDigits.substring(mod, mod + 1) + hash;
            number = number / base;
        }
        webHash = String.format("%4s", hash).replace(' ', '0');

    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Property other = (Property) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public List<Keyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
    }

    public List<User> getUsers() {
        if (users == null) {
            users = new ArrayList<User>();
        }
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void addUser(User u) {
        getUsers().add(u);
    }

    public void removeUser(User u) {
        if (getUsers().contains(u)) {
            getUsers().remove(u);
        }
    }

    public void clearUsers() {
        this.setUsers(new ArrayList<User>());
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

    public void addMobileOffer(MobileOffer mo) {
        getMobileOffers().add(mo);
    }

    public void removeMobileOffer(MobileOffer mo) {
        if (getMobileOffers().contains(mo)) {
            getMobileOffers().remove(mo);
        }
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

    public void addWebOffer(WebOffer wo) {
        getWebOffers().add(wo);
    }

    public void removeWebOffer(WebOffer wo) {
        if (getWebOffers().contains(wo)) {
            getWebOffers().remove(wo);
        }
    }
}
