/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.data.sms;

import com.proximus.data.Company;
import java.util.ArrayList;
import java.util.List;
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
@XmlRootElement(name = "geoFence")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@DiscriminatorValue("GENERIC")
@Table(name = "geo_fence")
public class GeoFence {

    public static final String CIRCLE = "CIRCLE";
    public static final String RECTANGLE = "RECTANGLE";
    public static final String CONCENTRIC = "CONCENTRIC";
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "type")
    private String type;
    @Column(name = "priority")
    private Long priority;
    @Transient
    double distanceAway;
    @XmlTransient
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "geoFence")
    private List<GeoPoint> geoPoints;
    @XmlTransient
    @ManyToOne
    private Property property;
    @XmlTransient
    @ManyToOne
    private Company company;

    public GeoFence() {
        this.id = 0L;
        this.priority = -1L;
    }

    public GeoFence(String name, Property property) {
        this.id = 0L;
        this.priority = -1L;
        this.name = name;
        this.geoPoints = new ArrayList<GeoPoint>();
        this.property = property;
    }

    public void addGeoPoint(GeoPoint geoPoint) {
        if (this.geoPoints == null) {
            this.geoPoints = new ArrayList<GeoPoint>();
        }
        this.geoPoints.add(geoPoint);
    }

    public void clearGeoPoints() {
        if (this.geoPoints == null) {
            this.geoPoints = new ArrayList<GeoPoint>();
        } else {
            this.geoPoints.clear();
        }
    }

    public List<GeoPoint> getGeoPoints() {
        return geoPoints;
    }

    public void setGeoPoints(List<GeoPoint> geoPoints) {
        this.geoPoints = geoPoints;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public double getDistanceAway() {
        return distanceAway;
    }

    public void setDistanceAway(double distanceAway) {
        this.distanceAway = distanceAway;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GeoFence other = (GeoFence) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if (this.priority != other.priority && (this.priority == null || !this.priority.equals(other.priority))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 79 * hash + (this.priority != null ? this.priority.hashCode() : 0);
        return hash;
    }
}