/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.sms;

import com.proximus.data.Brand;
import java.io.Serializable;
import java.util.Date;
import javax.jdo.annotations.Index;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


/**
 *
 * @author Angela Mercer
 */
@Entity
@Index(members = {"mobile", "eventDate", "status"}, unique = "true")
@Table(name = "location_data")
public class LocationData implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;
    @Size(max = 11)
    @Column(name = "msisdn")
    @NotNull
    private String msisdn;
    @Column(name = "x_coor")
    private Double longitude;
    @Column(name = "y_coor")
    private Double latitude;
    @Basic(optional = false)
    @Index
    @Column(name = "event_date")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date eventDate;
    @Size(max = 255)
    @Column(name = "status")
    private String status;
    @OneToOne
    private Brand brand;
    @Column(name = "distance_away_from_geo_fence")
    private Double distanceAwayFromGeoFence;
    @Column(name = "current_closest_geo_fence_id")
    private Long currentClosestGeoFenceId;

    public static final String STATUS_FOUND = "FOUND";
    public static final String STATUS_NOT_FOUND = "NOT_FOUND";
    public static final String STATUS_FOUND_FAKE = "FOUND_FAKE";
    public static final String STATUS_ERROR = "ERROR";
    public static final String GREATER_THAN = "GREATER_THAN";
    public static final String LESS_THAN = "LESS_THAN";
    
    public LocationData() {
        this.id = 0L;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

   

    @Override
    public String toString() {
        return getMsisdn() + "|" + getLatitude() + "|" + getLongitude() + "|" + getEventDate() + "|"
                + getStatus();
    }

    public Long getCurrentClosestGeoFenceId() {
        return currentClosestGeoFenceId;
    }

    public void setCurrentClosestGeoFenceId(Long currentClosestGeoFenceId) {
        this.currentClosestGeoFenceId = currentClosestGeoFenceId;
    }

    public Double getDistanceAwayFromGeoFence() {
        return distanceAwayFromGeoFence;
    }

    public void setDistanceAwayFromGeoFence(Double distanceAwayFromGeoFence) {
        this.distanceAwayFromGeoFence = distanceAwayFromGeoFence;
    }
    
    
}
