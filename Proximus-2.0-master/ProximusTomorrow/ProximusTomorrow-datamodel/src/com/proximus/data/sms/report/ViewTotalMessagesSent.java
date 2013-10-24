/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.sms.report;

import com.proximus.data.Company;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Gilberto Gaxiola
 */
@Entity
@Table(name = "view_total_messages_sent")
public class ViewTotalMessagesSent {
    
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private String id;
    @Column(name = "event_date")
    @Temporal(TemporalType.DATE)
    private Date eventDate;
    @Column(name = "total_messages")
    private Long totalMessages;
    @ManyToOne
    private Company company;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getTotalMessages() {
        return totalMessages;
    }

    public void setTotalMessages(Long totalMessages) {
        this.totalMessages = totalMessages;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ViewTotalMessagesSent other = (ViewTotalMessagesSent) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        if (this.eventDate != other.eventDate && (this.eventDate == null || !this.eventDate.equals(other.eventDate))) {
            return false;
        }
        if (this.totalMessages != other.totalMessages && (this.totalMessages == null || !this.totalMessages.equals(other.totalMessages))) {
            return false;
        }
        if (this.company != other.company && (this.company == null || !this.company.equals(other.company))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 37 * hash + (this.eventDate != null ? this.eventDate.hashCode() : 0);
        hash = 37 * hash + (this.totalMessages != null ? this.totalMessages.hashCode() : 0);
        hash = 37 * hash + (this.company != null ? this.company.hashCode() : 0);
        return hash;
    }
    
    

}
