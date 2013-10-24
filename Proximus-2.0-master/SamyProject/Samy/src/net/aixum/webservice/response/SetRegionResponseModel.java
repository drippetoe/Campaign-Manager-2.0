/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice.response;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class SetRegionResponseModel extends BaseResponseModel {

    public SetRegionResponseModel() {
    }

    public SetRegionResponseModel(boolean success, String message) {
        super(success, message);
    }

}
