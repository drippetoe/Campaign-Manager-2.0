/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice.response;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class AddCardMemberResponseModel extends BaseResponseModel {

    public AddCardMemberResponseModel() {
        super();
    }

    public AddCardMemberResponseModel(boolean success, String message) {
        super(success, message);
    }
}
