/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.sms.report;

import com.proximus.data.Company;
import com.proximus.data.sms.*;
import com.proximus.data.util.DateUtil;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 *
 * @author ronald
 */
@Entity
@Table(name = "mobile_offer_send_log_summary")
public class MobileOfferSendLogSummary implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;
    @ManyToOne
    private Company company;
    @ManyToOne
    private DMA dma;
    @ManyToOne
    private Property property;
    @ManyToOne
    private Retailer retailer;
    @ManyToOne
    private MobileOffer mobileOffer;
    @ManyToOne
    private GeoFence geoFence;
    @Column(name = "event_date")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date eventDate;
    @Column(name = "total_messages_sent")
    private Long totalMessagesSent;
    @Column(name = "click_through_rate")
    private Double clickThroughRate;
    @Transient
    private String formattedDate;

    public MobileOfferSendLogSummary() {
        this.id = 0L;
        this.clickThroughRate = 0.0;
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

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public Retailer getRetailer() {
        return retailer;
    }

    public void setRetailer(Retailer retailer) {
        this.retailer = retailer;
    }

    public MobileOffer getMobileOffer() {
        return mobileOffer;
    }

    public void setMobileOffer(MobileOffer mobileOffer) {
        this.mobileOffer = mobileOffer;
    }

    public Long getTotalMessagesSent() {
        return totalMessagesSent;
    }

    public void setTotalMessagesSent(Long totalMessagesSent) {
        this.totalMessagesSent = totalMessagesSent;
    }

    public GeoFence getGeoFence() {
        return geoFence;
    }

    public void setGeoFence(GeoFence geoFence) {
        this.geoFence = geoFence;
    }

    public Double getClickThroughRate() {
        return clickThroughRate;
    }

    public void setClickThroughRate(Double clickThroughRate) {
        this.clickThroughRate = clickThroughRate;
    }

    public String getFormattedDate() {
        formattedDate = DateUtil.formatShortDateForWeb(eventDate);
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }

    private String escapeDoubleQuotes(String s) {
        final String q = "\"";
        return q + s.replaceAll("\"", "\'") + q;
    }

    /**
     * Return this summary in CSV format
     *
     * @return
     */
    public String toCSV() {
        final String sep = ",";
        StringBuilder sb = new StringBuilder();
        sb.append(escapeDoubleQuotes(mobileOffer.getName()));
        sb.append(sep);
        sb.append(escapeDoubleQuotes(this.getRetailer().getName()));
        sb.append(sep);
        if (this.getProperty() != null) {
            sb.append(escapeDoubleQuotes(getProperty().getName()));
        } else {
            sb.append("null1");
        }
        sb.append(sep);
        sb.append(escapeDoubleQuotes(getDma().getName()));
        sb.append(sep);
        sb.append(getEventDate());
        sb.append(sep);
        if (this.getProperty() != null) {
            sb.append(escapeDoubleQuotes(getProperty().getStateProvince()));
        } else {
            sb.append("null2");
        }
        sb.append(sep);
        sb.append(getTotalMessagesSent());
        sb.append(sep);
        sb.append(mobileOffer.isRetailOnly());

        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MobileOfferSendLogSummary other = (MobileOfferSendLogSummary) obj;
        if (this.company != other.company && (this.company == null || !this.company.equals(other.company))) {
            return false;
        }
        if (this.mobileOffer != other.mobileOffer && (this.mobileOffer == null || !this.mobileOffer.equals(other.mobileOffer))) {
            return false;
        }
        if (this.eventDate != other.eventDate && (this.eventDate == null || !this.eventDate.equals(other.eventDate))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (this.company != null ? this.company.hashCode() : 0);
        hash = 79 * hash + (this.mobileOffer != null ? this.mobileOffer.hashCode() : 0);
        hash = 79 * hash + (this.eventDate != null ? this.eventDate.hashCode() : 0);
        return hash;
    }

    public static void main(String[] args) {
        MobileOfferSendLogSummary s = new MobileOfferSendLogSummary();
        s.setCompany(new Company("test co"));
        MobileOffer mo = new MobileOffer();
        DMA dma = new DMA();
        dma.setName("some \"dma");
        Retailer r = new Retailer();
        Property prop = new Property();
        prop.setName("some prop");
        prop.setStateProvince("GA");
        r.setName("test retailer");
        mo.setName("test offer");
        mo.setRetailer(r);

        s.setMobileOffer(mo);
        s.setDma(dma);
        s.setProperty(prop);
        s.setRetailer(r);
        System.out.println(s.toCSV());

        s.setProperty(null);
        System.out.println(s.toCSV());
    }
}
