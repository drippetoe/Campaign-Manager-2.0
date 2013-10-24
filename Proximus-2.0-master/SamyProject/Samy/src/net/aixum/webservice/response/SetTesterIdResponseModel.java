/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice.response;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class SetTesterIdResponseModel extends BaseResponseModel {

    public SetTesterIdResponseModel() {
    }

    public SetTesterIdResponseModel(boolean success, String message) {
        super(success, message);
    }
}
