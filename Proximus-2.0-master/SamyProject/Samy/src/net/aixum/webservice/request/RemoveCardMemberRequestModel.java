/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice.request;

import java.util.Date;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class RemoveCardMemberRequestModel extends BaseRequestModel {

    private int cardId;

    public RemoveCardMemberRequestModel(int cardId, String userId, int clientVersion, Date lastSync, double latitude, double longitude, double locationAccuracy, Date locationTimestamp) {
        super(userId, clientVersion, lastSync, latitude, longitude, locationAccuracy, locationTimestamp);
        this.cardId = cardId;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }
}
