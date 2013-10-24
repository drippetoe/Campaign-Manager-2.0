/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Gilberto Gaxiola
 */
@Entity
@Table(name = "zipcode_timezone", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "zipcode"
    })
})
public class ZipcodeToTimezone implements Serializable {

    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "zipcode", length = 5)
    private String zipcode;
    @Basic(optional = false)
    @Column(name = "state_province", length = 2)
    private String stateProvince;
    @Basic(optional = false)
    @Column(name = "timezone")
    private int timezone;
    @Column(name = "uses_daylight_savings")
    private boolean usesDaylightSavings;

    public ZipcodeToTimezone() {
    }

    public ZipcodeToTimezone(String zipcode, String stateProvince, int timezone, boolean usesDaylightSavings) {
        this.zipcode = zipcode;
        this.stateProvince = stateProvince;
        this.timezone = timezone;
        this.usesDaylightSavings = usesDaylightSavings;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    public int getTimezone() {
        return timezone;
    }

    public void setTimezone(int timezone) {
        this.timezone = timezone;
    }

    public boolean isUsesDaylightSavings() {
        return usesDaylightSavings;
    }

    public void setUsesDaylightSavings(boolean usesDaylightSavings) {
        this.usesDaylightSavings = usesDaylightSavings;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }
}
