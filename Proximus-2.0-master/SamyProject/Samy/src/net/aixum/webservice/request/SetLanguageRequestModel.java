/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice.request;

import java.util.Date;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class SetLanguageRequestModel extends BaseRequestModel {

    private String code;

    public SetLanguageRequestModel(String code, String userId, int clientVersion, Date lastSync, double latitude, double longitude, double locationAccuracy, Date locationTimestamp) {
        super(userId, clientVersion, lastSync, latitude, longitude, locationAccuracy, locationTimestamp);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
