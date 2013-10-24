/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice.response;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class LatestCategoryRevisionResponseModel extends BaseResponseModel {

    private int revision;

    public LatestCategoryRevisionResponseModel(int revision, boolean success, String message) {
        super(success, message);
        this.revision = revision;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }
}
