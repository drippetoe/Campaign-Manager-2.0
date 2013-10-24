/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.sms.report;

import com.proximus.data.Company;
import com.proximus.data.sms.Keyword;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 *
 * @author angela mercer
 */
@Entity
@Table(name = "registration_source_summary")
public class RegistrationSourceSummary implements Serializable {

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
    @Column(name = "total_registrations")
    private Long totalRegistrations;
    @ManyToOne
    private Keyword keyword;
    
    
    

    public RegistrationSourceSummary() {
        this.id = 0L;
        this.totalRegistrations = 0L;
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

    public Keyword getKeyword()
    {
        return keyword;
    }

    public void setKeyword(Keyword keyword)
    {
        this.keyword = keyword;
    }

    

    public Long getTotalRegistrations() {
        return totalRegistrations;
    }

    public void setTotalRegistrations(Long totalRegistrations) {
        this.totalRegistrations = totalRegistrations;
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
        final RegistrationSourceSummary other = (RegistrationSourceSummary) obj;
        if (this.company != other.company && (this.company == null || !this.company.equals(other.company))) {
            return false;
        }
        if (this.totalRegistrations != other.totalRegistrations && (this.totalRegistrations == null || !this.totalRegistrations.equals(other.totalRegistrations))) {
            return false;
        }
        if ((this.keyword != other.keyword && (this.keyword == null || !this.keyword.equals(other.keyword)))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.company != null ? this.company.hashCode() : 0);
        hash = 37 * hash + (this.keyword != null ? this.keyword.hashCode() : 0);
        hash = 37 * hash + (this.totalRegistrations != null ? this.totalRegistrations.hashCode() : 0);
        return hash;
    }
}
