/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice.response;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class SetLanguageResponseModel extends BaseResponseModel {

    public SetLanguageResponseModel() {
    }

    public SetLanguageResponseModel(boolean success, String message) {
        super(success, message);
    }
}
