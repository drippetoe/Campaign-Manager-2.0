/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.sms.report;

import com.proximus.data.sms.Property;
import com.proximus.data.sms.Retailer;
import com.proximus.data.web.WebOffer;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Gilberto Gaxiola
 */
@XmlRootElement(name = "viewActiveOffers")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "view_active_offers")
public class ViewActiveOffers implements Serializable {

    private static final long serialVersionUID = 1L;
    @XmlTransient
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private String id;
    @ManyToOne
    @JoinColumn(name = "web_offer_id")
    private WebOffer webOffer;
    @XmlTransient
    @Column(name = "web_hash")
    private String webHash;
    @XmlTransient
    @Column(name = "locale")
    private String locale;
    @XmlTransient
    @Column(name = "clean_offer_text")
    private String cleanOfferText;
    @XmlTransient
    @Column(name = "name")
    private String name;
    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;
    @ManyToOne
    @JoinColumn(name = "retailer_id")
    private Retailer retailer;
    @XmlTransient
    @Basic(optional = false)
    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    private Date startDate;
    @Basic(optional = false)
    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private Date endDate;
    @XmlTransient
    @Transient
    private Long messagesSent;
    @XmlTransient
    @Transient
    private Long currMonthMessagesSent;
    @XmlTransient
    @Transient
    private Long prevMonthMessagesSent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCleanOfferText() {
        return cleanOfferText;
    }

    public void setCleanOfferText(String cleanOfferText) {
        this.cleanOfferText = cleanOfferText;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public WebOffer getWebOffer() {
        return webOffer;
    }

    public void setWebOffer(WebOffer webOffer) {
        this.webOffer = webOffer;
    }

    public String getWebHash() {
        return webHash;
    }

    public void setWebHash(String webHash) {
        this.webHash = webHash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Long getMessagesSent() {
        return messagesSent;
    }

    public void setMessagesSent(Long messagesSent) {
        this.messagesSent = messagesSent;
    }

    public Retailer getRetailer() {
        return retailer;
    }

    public void setRetailer(Retailer retailer) {
        this.retailer = retailer;
    }

    public Long getCurrMonthMessagesSent() {
        return currMonthMessagesSent;
    }

    public void setCurrMonthMessagesSent(Long currMonthMessagesSent) {
        this.currMonthMessagesSent = currMonthMessagesSent;
    }

    public Long getPrevMonthMessagesSent() {
        return prevMonthMessagesSent;
    }

    public void setPrevMonthMessagesSent(Long prevMonthMessagesSent) {
        this.prevMonthMessagesSent = prevMonthMessagesSent;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ViewActiveOffers other = (ViewActiveOffers) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        if (this.webOffer != other.webOffer && (this.webOffer == null || !this.webOffer.equals(other.webOffer))) {
            return false;
        }
        if ((this.webHash == null) ? (other.webHash != null) : !this.webHash.equals(other.webHash)) {
            return false;
        }
        if ((this.locale == null) ? (other.locale != null) : !this.locale.equals(other.locale)) {
            return false;
        }
        if ((this.cleanOfferText == null) ? (other.cleanOfferText != null) : !this.cleanOfferText.equals(other.cleanOfferText)) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.property != other.property && (this.property == null || !this.property.equals(other.property))) {
            return false;
        }
        if (this.startDate != other.startDate && (this.startDate == null || !this.startDate.equals(other.startDate))) {
            return false;
        }
        if (this.endDate != other.endDate && (this.endDate == null || !this.endDate.equals(other.endDate))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 23 * hash + (this.webOffer != null ? this.webOffer.hashCode() : 0);
        hash = 23 * hash + (this.webHash != null ? this.webHash.hashCode() : 0);
        hash = 23 * hash + (this.locale != null ? this.locale.hashCode() : 0);
        hash = 23 * hash + (this.cleanOfferText != null ? this.cleanOfferText.hashCode() : 0);
        hash = 23 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 23 * hash + (this.property != null ? this.property.hashCode() : 0);
        hash = 23 * hash + (this.startDate != null ? this.startDate.hashCode() : 0);
        hash = 23 * hash + (this.endDate != null ? this.endDate.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return id + " -> " + name;
    }
}
