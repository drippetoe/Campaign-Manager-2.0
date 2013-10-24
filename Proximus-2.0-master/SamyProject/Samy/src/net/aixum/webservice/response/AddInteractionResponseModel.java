/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice.response;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class AddInteractionResponseModel extends BaseResponseModel {

    public AddInteractionResponseModel() {
    }

    public AddInteractionResponseModel(boolean success, String message) {
        super(success, message);
    }
}
