/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.report;

/**
 *
 * @author Gilberto Gaxiola
 */
public class WifiVisits {
    
    private String macAddress;
    private Long count;
    
    public WifiVisits(String macAddress, Long count) {
        this.macAddress = macAddress;
        this.count = count;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
    
    
    @Override
    public String toString() {
        return this.macAddress + " " + this.count;
    }
    

}
