/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.data;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Angela Mercer
 */
@Entity
@DiscriminatorValue("GENERIC")
@Table(name = "pricing_model", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "name"
    })
})
public class PricingModel implements Serializable {

    public static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;
    @Size(max = 255)
    @Column(name = "name")
    private String name;
    @Column(name = "monthly_property_fee")
    private String monthlyPropertyFee;
    @Column(name = "geo_license_fee")
    private String geoLicenseFee;
    @Column(name = "new_property_fee")
    private String newPropertyFee;
    @Column(name = "subscriber_fee")
    private String subscriberFee;
    @Column(name = "device_fee")
    private String deviceFee;

    public PricingModel() {
        this.id = 0L;
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

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PricingModel other = (PricingModel) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public String getDeviceFee() {
        return deviceFee;
    }

    public void setDeviceFee(String deviceFee) {
        this.deviceFee = deviceFee;
    }

    public String getGeoLicenseFee() {
        return geoLicenseFee;
    }

    public void setGeoLicenseFee(String geoLicenseFee) {
        this.geoLicenseFee = geoLicenseFee;
    }

    public String getMonthlyPropertyFee() {
        return monthlyPropertyFee;
    }

    public void setMonthlyPropertyFee(String monthlyPropertyFee) {
        this.monthlyPropertyFee = monthlyPropertyFee;
    }

    public String getNewPropertyFee() {
        return newPropertyFee;
    }

    public void setNewPropertyFee(String newPropertyFee) {
        this.newPropertyFee = newPropertyFee;
    }

    public String getSubscriberFee() {
        return subscriberFee;
    }

    public void setSubscriberFee(String subscriberFee) {
        this.subscriberFee = subscriberFee;
    }
}
