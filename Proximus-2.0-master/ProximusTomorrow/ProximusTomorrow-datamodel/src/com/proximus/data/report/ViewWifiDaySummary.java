/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.report;

import java.util.Date;

/**
 *
 * @author Gilberto Gaxiola
 */
public class ViewWifiDaySummary {
    
    private Date eventDate;
    private Long pageViews;
    
    public ViewWifiDaySummary(Date eventDate, Long pageViews) {
        this.eventDate = eventDate;
        this.pageViews = pageViews;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Long getPageViews() {
        return pageViews;
    }

    public void setPageViews(Long pageViews) {
        this.pageViews = pageViews;
    }
    
    
}
