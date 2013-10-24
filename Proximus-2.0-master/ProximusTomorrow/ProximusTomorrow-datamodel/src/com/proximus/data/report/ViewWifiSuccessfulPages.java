/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.report;

/**
 *
 * @author Gilberto Gaxiola
 */
public class ViewWifiSuccessfulPages {
    
    private String requestUrl;
    private Long successfulPageViews;
    
    
    public ViewWifiSuccessfulPages(String requestUrl, Long successfulPageViews) {
        this.requestUrl = requestUrl;
        this.successfulPageViews = successfulPageViews;
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
