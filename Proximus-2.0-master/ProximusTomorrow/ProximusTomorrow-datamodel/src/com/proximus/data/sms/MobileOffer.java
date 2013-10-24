/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.sms;

import com.proximus.data.Brand;
import com.proximus.data.Company;
import com.proximus.data.DayParts;
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
@XmlRootElement(name = "mobile_offer")
@XmlAccessorType(XmlAccessType.FIELD)
@DiscriminatorValue("GENERIC")
@Table(name = "mobile_offer")
public class MobileOffer implements Serializable {

    public static final String PENDING = "Pending Approval";
    public static final String APPROVED = "Approved";
    public static final String DELETED = "Deleted";
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id = 0L;
    @Size(max = 255)
    @Column(name = "name")
    private String name;
    @Size(max = 2500)
    @Column(name = "offer_text", length = 1024)
    private String offerText;
    @Size(max = 255)
    @Column(name = "clean_offer_text", length = 255)
    private String cleanOfferText;
    @Basic(optional = false)
    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    private Date startDate;
    @Basic(optional = false)
    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private Date endDate;
    @Column(name = "retail_only")
    private boolean retailOnly;
    @Column(name = "status")
    private String status;
    @XmlTransient
    @ManyToOne
    private Company company;
    @XmlTransient
    @ManyToOne
    private Brand brand;
    @XmlTransient
    @ManyToMany
    @JoinTable(name = "mobile_offer_property", joinColumns =
    @JoinColumn(name = "mobile_offer_id", referencedColumnName = "id"), inverseJoinColumns =
    @JoinColumn(name = "property_id", referencedColumnName = "id"))
    private List<Property> properties;
    @XmlTransient
    @ManyToOne
    private Retailer retailer;
    @XmlTransient
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "mobileOffer")
    List<DayParts> dayParts;
    @Transient
    long sendCount;
    @XmlTransient
    @ManyToMany
    @JoinTable(name = "mobile_offer_category", joinColumns =
    @JoinColumn(name = "mobile_offer_id", referencedColumnName = "id"), inverseJoinColumns =
    @JoinColumn(name = "category_id", referencedColumnName = "id"))
    private List<Category> categories;
    @Column(name = "deleted")
    private boolean deleted;
    @Basic(optional = false)
    @Column(name = "last_modified")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date lastModified;
    @Basic(optional = false)
    @Column(name = "event_created")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date eventCreated;
    @Basic(optional = false)
    @Column(name = "event_approved")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date eventApproved;
    @XmlTransient
    @OneToOne
    Locale locale;
    @Column(name = "offer_type")
    private String offerType;
    @Size(max = 255)
    @Column(name = "passbook_barcode")
    private String passbookBarcode;
    @Size(max = 7)
    @Column(name = "passbook_header")
    private String passbookHeader;
    @Size(max = 46)
    @Column(name = "passbook_subheader")
    private String passbookSubheader;
    @Size(max = 36)
    @Column(name = "passbook_details")
    private String passbookDetails;

    public MobileOffer() {
        id = 0L;
        deleted = false;
        startDate = new Date();
        // 4 weeks ahead by default
        endDate = new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 28));
        dayParts = new ArrayList<DayParts>();
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

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public List<DayParts> getDayParts() {
        return dayParts;
    }

    public void setDayParts(List<DayParts> dayParts) {
        this.dayParts = dayParts;
    }

    public void addDayPart() {
        if (this.dayParts == null) {
            this.dayParts = new ArrayList<DayParts>();
        }
        this.dayParts.add(new DayParts());

    }

    public void removeDayPart(DayParts daypart) {
        if (this.dayParts != null) {
            dayParts.remove(daypart);
        }

    }

    public long getSendCount() {
        return sendCount;
    }

    public void setSendCount(long sendCount) {
        this.sendCount = sendCount;
    }

    public List<Category> getCategories() {
        if (this.categories == null) {
            this.categories = new ArrayList<Category>();
        }
        return this.categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public void addCategory(Category category) {
        getCategories().add(category);
    }

    public void removeCategory(Category category) {
        if (getCategories().contains(category)) {
            getCategories().remove(category);
        }
    }

    public void clearCategories() {
        this.categories = new ArrayList<Category>();
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
        value.append("[(MobileOffer) ");
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

    public String getPassbookBarcode() {
        return passbookBarcode;
    }

    public void setPassbookBarcode(String passbookBarcode) {
        this.passbookBarcode = passbookBarcode;
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

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
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
        final MobileOffer other = (MobileOffer) obj;
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

    public String getOfferWithWebHash(String url, String webHash) {
        String result = null;
        if (this.cleanOfferText != null && !this.cleanOfferText.isEmpty() && cleanOfferText.contains(url)) {
            String[] cleanText = this.cleanOfferText.split(url);

            result = cleanText[0] + url + "/" + this.getLocale().getLanguageCode() + webHash;
            if (cleanText.length > 1) {
                //URL was in the middle
                String secondPart = cleanText[1];
                if (secondPart.startsWith("/")) {
                    secondPart = secondPart.replaceFirst("/", "");
                }
                result += secondPart;
            }
        }
        return result;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public void clearProperties() {
        this.properties = new ArrayList<Property>();
    }

    public String getOfferType() {
        return offerType;
    }

    public void setOfferType(String offerType) {
        this.offerType = offerType;
    }

    public Date getEventApproved() {
        return eventApproved;
    }

    public void setEventApproved(Date eventApproved) {
        this.eventApproved = eventApproved;
    }

    public Date getEventCreated() {
        return eventCreated;
    }

    public void setEventCreated(Date eventCreated) {
        this.eventCreated = eventCreated;
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
    
    
    
    

    public MobileOffer cloneMyData() {
        MobileOffer offer = new MobileOffer();
        offer.setBrand(brand);
        offer.setCategories(categories);
        offer.setCleanOfferText(cleanOfferText);
        offer.setCompany(company);
        offer.setDayParts(dayParts);
        offer.setDeleted(deleted);
        offer.setEndDate(endDate);
        offer.setId(0L);
        offer.setLastModified(new Date());
        offer.setLocale(locale);
        offer.setName("");
        offer.setOfferText(offerText);
        offer.setOfferType(offerType);
        offer.setProperties(properties);
        offer.setRetailOnly(retailOnly);
        offer.setRetailer(retailer);
        offer.setSendCount(0L);
        offer.setStartDate(startDate);
        offer.setStatus(MobileOffer.PENDING);
        offer.setPassbookBarcode(passbookBarcode);
        offer.setPassbookDetails(passbookDetails);
        offer.setPassbookHeader(passbookHeader);
        offer.setPassbookSubheader(passbookSubheader);
        return offer;
    }
}
