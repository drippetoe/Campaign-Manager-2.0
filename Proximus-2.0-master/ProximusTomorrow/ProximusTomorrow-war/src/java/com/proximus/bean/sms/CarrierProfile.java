/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.bean.sms;

/**
 *
 * @author ronald
 */
public class CarrierProfile {

    private String carrier;
    private Long uniqueSubscribers;

    public CarrierProfile() {
        this.uniqueSubscribers = 0L;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public Long getUniqueSubscribers() {
        return uniqueSubscribers;
    }

    public void setUniqueSubscribers(Long uniqueSubscribers) {
        this.uniqueSubscribers = uniqueSubscribers;
    }
}
