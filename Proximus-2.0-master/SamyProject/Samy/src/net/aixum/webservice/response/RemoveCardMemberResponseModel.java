/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice.response;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class RemoveCardMemberResponseModel extends BaseResponseModel {

    public RemoveCardMemberResponseModel() {
    }

    public RemoveCardMemberResponseModel(boolean success, String message) {
        super(success, message);
    }
}
