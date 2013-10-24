/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.web;

import com.proximus.data.sms.*;
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
 * @author Gilberto Gaxiola
 */
@XmlRootElement(name = "webOffer")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@DiscriminatorValue("GENERIC")
@Table(name = "web_offer")
public class WebOffer implements Serializable {

    public static final String PENDING = "Pending Approval";
    public static final String APPROVED = "Approved";
    public static final String DELETED = "Deleted";
    private static final long serialVersionUID = 1L;
    @XmlTransient
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id = 0L;
    @Size(max = 255)
    @Column(name = "name")
    private String name;
    @XmlTransient
    @Size(max = 2500)
    @Column(name = "offer_text", length = 1024)
    private String offerText;
    @Size(max = 255)
    @Column(name = "clean_offer_text")
    private String cleanOfferText;
    @XmlTransient
    @Basic(optional = false)
    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    private Date startDate;
    @XmlTransient
    @Basic(optional = false)
    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private Date endDate;
    @XmlTransient
    @Column(name = "retail_only")
    private boolean retailOnly;
    @XmlTransient
    @Column(name = "status")
    private String status;
    @XmlTransient
    @ManyToMany
    @JoinTable(name = "property_web_offer", joinColumns =
    @JoinColumn(name = "web_offer_id", referencedColumnName = "id"), inverseJoinColumns =
    @JoinColumn(name = "property_id", referencedColumnName = "id"))
    private List<Property> properties;
    @XmlTransient
    @OneToOne
    private Retailer retailer;
    @ManyToMany
    @JoinTable(name = "web_offer_category", joinColumns =
    @JoinColumn(name = "web_offer_id", referencedColumnName = "id"), inverseJoinColumns =
    @JoinColumn(name = "category_id", referencedColumnName = "id"))
    private List<Category> categories;
    @XmlTransient
    @Column(name = "deleted")
    private boolean deleted;
    @XmlTransient
    @Basic(optional = false)
    @Column(name = "last_modified")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date lastModified;
    @XmlTransient
    @Size(max = 2)
    @Column(name = "locale")
    String locale;
    @XmlTransient
    @OneToOne
    @JoinColumn(name = "MOBILEOFFERID")
    protected MobileOffer mobileOffer;
    @XmlTransient
    @Size(max = 255)
    @Column(name = "passbook_barcode")
    private String passbookBarcode;
    @XmlTransient
    @Size(max = 7)
    @Column(name = "passbook_header")
    private String passbookHeader;
    @XmlTransient
    @Size(max = 46)
    @Column(name = "passbook_subheader")
    private String passbookSubheader;
    @XmlTransient
    @Size(max = 36)
    @Column(name = "passbook_details")
    private String passbookDetails;
    @Size(max = 255)
    @Column(name = "offer_image_path")
    private String offerImage;

    public WebOffer() {
        id = 0L;
        deleted = false;
        startDate = new Date();
        // 4 weeks ahead by default
        endDate = new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 28));
        properties = new ArrayList<Property>();
        retailOnly = false;
        retailer = null;
        lastModified = new Date();
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOfferText() {
        return offerText;
    }

    public void setOfferText(String offerText) {
        this.offerText = offerText;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public boolean isRetailOnly() {
        return retailOnly;
    }

    public void setRetailOnly(boolean retailOnly) {
        this.retailOnly = retailOnly;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOfferImage() {
        return offerImage;
    }

    public void setOfferImage(String offerImage) {
        this.offerImage = offerImage;
    }

    @Override
    public String toString() {
        return this.offerText;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Category> getCategories() {
        if (categories == null) {
            categories = new ArrayList<Category>();
        }
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public void addCategory(Category category) {
        getCategories().add(category);
    }

    public void clearCategories() {
        this.categories = new ArrayList<Category>();
    }

    public void removeCategory(Category category) {
        if (getCategories().contains(category)) {
            getCategories().remove(category);
        }
    }

    public String getFormattedCategories() {
        String result = "";
        for (Category category : getCategories()) {
            result += category + ", ";
        }
        result = result.trim();
        result = result.substring(0, result.length() - 1);
        return result;
    }

    public Retailer getRetailer() {
        return retailer;
    }

    public void setRetailer(Retailer retailer) {
        this.retailer = retailer;
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

    public String getLogName() {
        return "[" + getName() + " (" + getId() + ")|" + getLocale() + "]";
    }

    /**
     *
     * @return the offer in a log friendly format
     */
    public String getLogString() {
        StringBuilder value = new StringBuilder();
        value.append("[(WebOffer) ");
        value.append(this.getName());
        value.append(" (").append(this.getId()).append(")");
        value.append(" (type:");
        if (this.isRetailOnly()) {
            value.append("R");
        } else {
            value.append("P (");
            for (Property p : properties) {
                value.append(p.getLogString()).append(" ");
            }
            value.append(")");
        }

        value.append("]");

        return value.toString();
    }

    public String getCleanOfferText() {
        return cleanOfferText;
    }

    public void setCleanOfferText(String cleanOfferText) {
        this.cleanOfferText = cleanOfferText;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WebOffer other = (WebOffer) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public void clearProperties() {
        this.properties = new ArrayList<Property>();
    }

    public String getPassbookBarcode() {
        return passbookBarcode;
    }

    public void setPassbookBarcode(String passbookBarcode) {
        this.passbookBarcode = passbookBarcode;
    }

    public void createWebOfferFromMobileOffer(MobileOffer mo) {
        this.mobileOffer = mo;
        this.name = mo.getName();
        this.offerText = mo.getOfferText();
        this.cleanOfferText = mo.getCleanOfferText();
        this.startDate = mo.getStartDate();
        this.endDate = mo.getEndDate();
        this.retailOnly = mo.isRetailOnly();
        this.status = mo.getStatus();
        this.properties = mo.getProperties();
        this.retailer = mo.getRetailer();
        this.categories = mo.getCategories();
        this.deleted = mo.isDeleted();
        this.lastModified = mo.getLastModified();
        this.locale = mo.getLocale().getLanguageCode();
        this.passbookBarcode = mo.getPassbookBarcode();
        this.passbookDetails = mo.getPassbookDetails();
        this.passbookHeader = mo.getPassbookHeader();
        this.passbookSubheader = mo.getPassbookSubheader();
    }

    public MobileOffer getMobileOffer() {
        return mobileOffer;
    }

    public void setMobileOffer(MobileOffer mobileOffer) {
        this.mobileOffer = mobileOffer;
    }

    public String getPassbookDetails() {
        return passbookDetails;
    }

    public void setPassbookDetails(String passbookDetails) {
        this.passbookDetails = passbookDetails;
    }

    public String getPassbookHeader() {
        return passbookHeader;
    }

    public void setPassbookHeader(String passbookHeader) {
        this.passbookHeader = passbookHeader;
    }

    public String getPassbookSubheader() {
        return passbookSubheader;
    }

    public void setPassbookSubheader(String passbookSubheader) {
        this.passbookSubheader = passbookSubheader;
    }
}
