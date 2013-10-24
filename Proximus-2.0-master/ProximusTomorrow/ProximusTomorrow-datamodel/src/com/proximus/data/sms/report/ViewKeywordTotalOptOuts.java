/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.sms.report;

import com.proximus.data.Company;
import com.proximus.data.sms.Keyword;
import com.proximus.data.sms.SourceType;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Gilberto Gaxiola
 */
@Entity
@Table(name = "view_keyword_total_opt_outs")
public class ViewKeywordTotalOptOuts implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private String id;
    @ManyToOne
    @JoinColumn(name = "keyword_id")
    private Keyword keyword;
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
    @ManyToOne
    @JoinColumn(name = "sourcetype_id")
    private SourceType sourceType;
    @Column(name = "total_opt_outs")
    private Long totalOptOuts;
    @Basic(optional = false)
    @Column(name = "event_date")
    @Temporal(TemporalType.DATE)
    private Date eventDate;

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

    public Keyword getKeyword() {
        return keyword;
    }

    public void setKeyword(Keyword keyword) {
        this.keyword = keyword;
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }

    public Long getTotalOptOuts() {
        return totalOptOuts;
    }

    public void setTotalOptOuts(Long totalOptOuts) {
        this.totalOptOuts = totalOptOuts;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ViewKeywordTotalOptOuts other = (ViewKeywordTotalOptOuts) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        if (this.keyword != other.keyword && (this.keyword == null || !this.keyword.equals(other.keyword))) {
            return false;
        }
        if (this.company != other.company && (this.company == null || !this.company.equals(other.company))) {
            return false;
        }
        if (this.sourceType != other.sourceType && (this.sourceType == null || !this.sourceType.equals(other.sourceType))) {
            return false;
        }
        if (this.totalOptOuts != other.totalOptOuts && (this.totalOptOuts == null || !this.totalOptOuts.equals(other.totalOptOuts))) {
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
        hash = 53 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 53 * hash + (this.keyword != null ? this.keyword.hashCode() : 0);
        hash = 53 * hash + (this.company != null ? this.company.hashCode() : 0);
        hash = 53 * hash + (this.sourceType != null ? this.sourceType.hashCode() : 0);
        hash = 53 * hash + (this.totalOptOuts != null ? this.totalOptOuts.hashCode() : 0);
        hash = 53 * hash + (this.eventDate != null ? this.eventDate.hashCode() : 0);
        return hash;
    }
    
    
    


}
