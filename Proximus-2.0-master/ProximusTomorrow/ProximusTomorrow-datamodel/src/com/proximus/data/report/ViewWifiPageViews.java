/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.report;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Gilberto Gaxiola
 */
public class ViewWifiPageViews implements Serializable {

    private static final long serialVersionUID = 1L;


    private Date eventDate;
    private String requestUrl;
    private Long successfulPageViews;

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

   

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public Long getSuccessfulPageViews() {
        return successfulPageViews;
    }

    public void setSuccessfulPageViews(Long successfulPageViews) {
        this.successfulPageViews = successfulPageViews;
    }    
}
