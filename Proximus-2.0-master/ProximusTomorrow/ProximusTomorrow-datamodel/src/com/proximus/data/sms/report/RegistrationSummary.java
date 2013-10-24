/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.sms.report;

import com.proximus.data.Company;
import com.proximus.data.sms.DMA;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 *
 * @author ronald
 */
@Entity
@Table(name = "registration_summary")
public class RegistrationSummary implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;
    @ManyToOne
    private Company company;
    @Column(name = "event_date")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date eventDate;
    @Column(name = "total_opt_outs")
    private Long totalOptOuts;
    @Column(name = "total_active_subscribers")
    private Long totalActiveSubscribers;
    @Column(name = "total_registrations")
    private Long totalRegistrations;
    @Column(name = "opt_in")
    private Long optIn;
    @Column(name = "opt_out")
    private Long optOut;
    @ManyToOne
    private DMA dma;

    public RegistrationSummary() {
        this.id = 0L;
        this.totalActiveSubscribers = 0L;
        this.totalOptOuts = 0L;
        this.totalRegistrations = 0L;
        this.optIn = 0L;
        this.optOut = 0L;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
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

    public Long getTotalActiveSubscribers() {
        return totalActiveSubscribers;
    }

    public void setTotalActiveSubscribers(Long totalActiveSubscribers) {
        this.totalActiveSubscribers = totalActiveSubscribers;
    }

    public Long getTotalOptOuts() {
        return totalOptOuts;
    }

    public void setTotalOptOuts(Long totalOptOuts) {
        this.totalOptOuts = totalOptOuts;
    }

    public Long getOptIn() {
        return optIn;
    }

    public void setOptIn(Long optIn) {
        this.optIn = optIn;
    }

    public Long getOptOut() {
        return optOut;
    }

    public void setOptOut(Long optOut) {
        this.optOut = optOut;
    }

    public Long getTotalRegistrations() {
        return totalRegistrations;
    }

    public void setTotalRegistrations(Long totalRegistrations) {
        this.totalRegistrations = totalRegistrations;
    }

    public DMA getDma() {
        return dma;
    }

    public void setDma(DMA dma) {
        this.dma = dma;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Event date: ").append(eventDate);
        sb.append("\n");
        if (company != null) {
            sb.append("Company: ").append(company.getName());
            sb.append(" (").append(company.getId()).append(")");
            sb.append("\n");
        }
        sb.append("Get Total Registrations: ").append(getTotalRegistrations());
        sb.append("\n");
        sb.append("Get Active Subscribers: ").append(getTotalActiveSubscribers());
        sb.append("\n");
        sb.append("Get Opt-outs: ").append(getTotalOptOuts());
        sb.append("\n");
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
        final RegistrationSummary other = (RegistrationSummary) obj;
        if (this.company != other.company && (this.company == null || !this.company.equals(other.company))) {
            return false;
        }
        if (this.totalOptOuts != other.totalOptOuts && (this.totalOptOuts == null || !this.totalOptOuts.equals(other.totalOptOuts))) {
            return false;
        }
        if (this.totalActiveSubscribers != other.totalActiveSubscribers && (this.totalActiveSubscribers == null || !this.totalActiveSubscribers.equals(other.totalActiveSubscribers))) {
            return false;
        }
        if (this.totalRegistrations != other.totalRegistrations && (this.totalRegistrations == null || !this.totalRegistrations.equals(other.totalRegistrations))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.company != null ? this.company.hashCode() : 0);
        hash = 37 * hash + (this.totalOptOuts != null ? this.totalOptOuts.hashCode() : 0);
        hash = 37 * hash + (this.totalActiveSubscribers != null ? this.totalActiveSubscribers.hashCode() : 0);
        hash = 37 * hash + (this.totalRegistrations != null ? this.totalRegistrations.hashCode() : 0);
        return hash;
    }
}
