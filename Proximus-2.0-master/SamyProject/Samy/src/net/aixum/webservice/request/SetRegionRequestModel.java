/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice.request;

import java.util.Date;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class SetRegionRequestModel extends BaseRequestModel {

    private int regionId;

    public SetRegionRequestModel(int regionId, String userId, int clientVersion, Date lastSync, double latitude, double longitude, double locationAccuracy, Date locationTimestamp) {
        super(userId, clientVersion, lastSync, latitude, longitude, locationAccuracy, locationTimestamp);
        this.regionId = regionId;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }
}
