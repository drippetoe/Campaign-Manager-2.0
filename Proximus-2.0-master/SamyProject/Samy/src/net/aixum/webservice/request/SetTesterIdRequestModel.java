/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice.request;

import java.util.Date;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class SetTesterIdRequestModel extends BaseRequestModel {

    private String testerId;

    public SetTesterIdRequestModel(String testerId, String userId, int clientVersion, Date lastSync, double latitude, double longitude, double locationAccuracy, Date locationTimestamp) {
        super(userId, clientVersion, lastSync, latitude, longitude, locationAccuracy, locationTimestamp);
        this.testerId = testerId;
    }

    public String getTesterId() {
        return testerId;
    }

    public void setTesterId(String testerId) {
        this.testerId = testerId;
    }
}
