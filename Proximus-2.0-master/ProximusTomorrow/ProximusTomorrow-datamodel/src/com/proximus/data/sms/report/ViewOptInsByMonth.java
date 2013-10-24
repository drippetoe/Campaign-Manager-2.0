/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.sms.report;

import com.proximus.data.Company;
import com.proximus.data.sms.Property;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Gilberto Gaxiola
 */
@Entity
@Table(name = "view_opt_ins_by_month")
public class ViewOptInsByMonth implements Serializable{
    
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private String id;
    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
    @Column(name = "opt_ins")
    private Long optIns;
    @Column(name = "the_year")
    private Integer theYear;
    @Column(name = "the_month")
    private Integer theMonth;

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getOptIns() {
        return optIns;
    }

    public void setOptIns(Long optIns) {
        this.optIns = optIns;
    }

   

    public Integer getTheMonth() {
        return theMonth;
    }

    public void setTheMonth(Integer theMonth) {
        this.theMonth = theMonth;
    }

    public Integer getTheYear() {
        return theYear;
    }

    public void setTheYear(Integer theYear) {
        this.theYear = theYear;
    }

   

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

}
