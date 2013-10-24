/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice.request;

import java.util.Date;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class MembershipCardDetailsRequestModel extends BaseRequestModel {

    private int membershipCardId;

    public MembershipCardDetailsRequestModel(int membershipCardId, String userId, int clientVersion, Date lastSync, double latitude, double longitude, double locationAccuracy, Date locationTimestamp) {
        super(userId, clientVersion, lastSync, latitude, longitude, locationAccuracy, locationTimestamp);
        this.membershipCardId = membershipCardId;
    }

    public int getMembershipCardId() {
        return membershipCardId;
    }

    public void setMembershipCardId(int membershipCardId) {
        this.membershipCardId = membershipCardId;
    }
}
