/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.sms;

import com.proximus.data.Brand;
import com.proximus.data.DayParts;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Gilberto Gaxiola
 */
@Entity
@Table(name = "geo_location_settings")
public class GeoLocationSettings implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    protected Long id = 0L;
    @Basic(optional = false)
    @Column(name = "last_modified")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date lastModified;
    @ManyToOne
    protected Brand brand;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "geo_location_settings")
    List<DayParts> dayParts;

    public GeoLocationSettings() {
        id = 0L;
        dayParts = new ArrayList<DayParts>();

    }
    
     public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
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
}
