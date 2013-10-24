/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice.response;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class SetEndUserDetailsResponseModel extends BaseResponseModel {

    public SetEndUserDetailsResponseModel() {
    }

    public SetEndUserDetailsResponseModel(boolean success, String message) {
        super(success, message);
    }
}
