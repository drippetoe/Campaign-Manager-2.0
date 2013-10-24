/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class Website extends ContactDetailBaseModel {

    private String website;

    public Website(String website, int sort, String value) {
        super(sort, value);
        this.website = website;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
