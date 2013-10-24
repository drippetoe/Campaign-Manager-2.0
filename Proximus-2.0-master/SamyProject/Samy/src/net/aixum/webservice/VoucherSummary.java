/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class VoucherSummary {

    private int id;
    private int revision;

    public VoucherSummary() {
    }

    public VoucherSummary(int id, int revision) {
        this.id = id;
        this.revision = revision;
    }

    public Number getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Number getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }
}
