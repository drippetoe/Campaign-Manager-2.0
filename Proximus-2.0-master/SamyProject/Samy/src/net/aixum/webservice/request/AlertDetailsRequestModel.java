/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice.request;

import java.util.Date;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class AlertDetailsRequestModel extends BaseRequestModel {

    private int alertIds;

    public AlertDetailsRequestModel(int alertIds, String userId, int clientVersion, Date lastSync, double latitude, double longitude, double locationAccuracy, Date locationTimestamp) {
        super(userId, clientVersion, lastSync, latitude, longitude, locationAccuracy, locationTimestamp);
        this.alertIds = alertIds;
    }

    public int getAlertIds() {
        return alertIds;
    }

    public void setAlertIds(int alertIds) {
        this.alertIds = alertIds;
    }
    
}
