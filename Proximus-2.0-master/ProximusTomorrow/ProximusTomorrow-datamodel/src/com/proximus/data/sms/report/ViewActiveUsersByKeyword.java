/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.sms.report;

import com.proximus.data.Brand;
import com.proximus.data.sms.Keyword;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Gilberto Gaxiola
 */
@Entity
@Table(name = "view_active_users_by_keyword")
public class ViewActiveUsersByKeyword implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private String id;
    @Column(name = "event_date")
    @Temporal(TemporalType.DATE)
    private Date eventDate;
    @Column(name = "active_users")
    private Long activeUsers;
    @ManyToOne
    private Brand brand;
    @OneToOne
    private Keyword keyword;

    public Long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(Long activeUsers) {
        this.activeUsers = activeUsers;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public Keyword getKeyword() {
        return keyword;
    }

    public void setKeyword(Keyword keyword) {
        this.keyword = keyword;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ViewActiveUsersByKeyword other = (ViewActiveUsersByKeyword) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        if (this.eventDate != other.eventDate && (this.eventDate == null || !this.eventDate.equals(other.eventDate))) {
            return false;
        }
        if (this.activeUsers != other.activeUsers && (this.activeUsers == null || !this.activeUsers.equals(other.activeUsers))) {
            return false;
        }
        if (this.brand != other.brand && (this.brand == null || !this.brand.equals(other.brand))) {
            return false;
        }
        if (this.keyword != other.keyword && (this.keyword == null || !this.keyword.equals(other.keyword))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 61 * hash + (this.eventDate != null ? this.eventDate.hashCode() : 0);
        hash = 61 * hash + (this.activeUsers != null ? this.activeUsers.hashCode() : 0);
        hash = 61 * hash + (this.brand != null ? this.brand.hashCode() : 0);
        hash = 61 * hash + (this.keyword != null ? this.keyword.hashCode() : 0);
        return hash;
    }

    
}
