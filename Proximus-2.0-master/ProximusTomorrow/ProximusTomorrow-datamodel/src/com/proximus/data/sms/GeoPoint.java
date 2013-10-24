/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.data.sms;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author eric
 */
@Entity
@XmlRootElement(name = "geo_point")
@XmlAccessorType(XmlAccessType.FIELD)
@DiscriminatorValue("GENERIC")
@Table(name = "geo_point")
public class GeoPoint {

    private static final long serialVersionUID = 1L;
    @XmlTransient
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;
    @Column(name = "lat")
    private Double lat;
    @Column(name = "lng")
    private Double lng;
    @XmlTransient
    @Column(name = "radius")
    private double radius;
    @XmlTransient
    @Column(name = "property_point")
    private boolean propertyPoint;
    @XmlTransient
    @ManyToOne
    private GeoFence geoFence;

    public GeoPoint() {
        this.id = 0L;
        this.propertyPoint = false;
    }

    public GeoPoint(Double lat, Double lng, double radius) {
        this.id = 0L;
        this.lat = lat;
        this.lng = lng;
        this.radius = radius;
    }

    public GeoPoint(Double lat, Double lng, double radius, GeoFence geoFence) {
        this.id = 0L;
        this.lat = lat;
        this.lng = lng;
        this.radius = radius;
        this.geoFence = geoFence;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public boolean isPropertyPoint() {
        return propertyPoint;
    }

    public void setPropertyPoint(boolean propertyPoint) {
        this.propertyPoint = propertyPoint;
    }

    public GeoFence getGeoFence() {
        return geoFence;
    }

    public void setGeoFence(GeoFence geoFence) {
        this.geoFence = geoFence;
    }

    public void setLatLng(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public String getLatLng() {
        return "" + this.lat + "," + this.lng;
    }

    @Override
    public String toString() {
        return this.getLatLng();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GeoPoint other = (GeoPoint) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if (this.lat != other.lat && (this.lat == null || !this.lat.equals(other.lat))) {
            return false;
        }
        if (this.lng != other.lng && (this.lng == null || !this.lng.equals(other.lng))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 67 * hash + (this.lat != null ? this.lat.hashCode() : 0);
        hash = 67 * hash + (this.lng != null ? this.lng.hashCode() : 0);
        return hash;
    }
}
