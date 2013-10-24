/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice.request;

import java.util.Date;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class SetEndUserDetailsRequestModel extends BaseRequestModel {

    private String name;
    private String mobile;
    private String email;
    private String pushtoken;
    private String pringoId;

    public SetEndUserDetailsRequestModel(String name, String mobile, String email, String pushtoken, String pringoId, String userId, int clientVersion, Date lastSync, double latitude, double longitude, double locationAccuracy, Date locationTimestamp) {
        super(userId, clientVersion, lastSync, latitude, longitude, locationAccuracy, locationTimestamp);
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.pushtoken = pushtoken;
        this.pringoId = pringoId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPushtoken() {
        return pushtoken;
    }

    public void setPushtoken(String pushtoken) {
        this.pushtoken = pushtoken;
    }

    public String getPringoId() {
        return pringoId;
    }

    public void setPringoId(String pringoId) {
        this.pringoId = pringoId;
    }
}
