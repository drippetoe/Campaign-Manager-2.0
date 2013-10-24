/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.report;

import java.util.Date;

/**
 *
 * @author Gilberto Gaxiola
 */
public class ViewBluetoothDaySummary implements Comparable<ViewBluetoothDaySummary> {

    private Date eventDate;
    private Long downloads;
    private Long devicesSeen;

    public ViewBluetoothDaySummary(Date eventDate, Long devicesSeen, Long downloads) {
        this.eventDate = eventDate;
        this.downloads = downloads;
        this.devicesSeen = devicesSeen;
    }

    public Long getDownloads() {
        return downloads;
    }

    public void setDownloads(Long downloads) {
        this.downloads = downloads;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Long getDevicesSeen() {
        return devicesSeen;
    }

    public void setDevicesSeen(Long devicesSeen) {
        this.devicesSeen = devicesSeen;
    }

    @Override
    public int compareTo(ViewBluetoothDaySummary o) {
        if(o == null || o.getEventDate() == null) {
            return -1;
        }
        if(this.eventDate == null ) {
            return 1;
        }
        return this.eventDate.compareTo(o.getEventDate());
        
    }
}
