/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice.request;


import java.util.Date;
import net.aixum.webservice.InteractionType;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class AddInteractionRequestModel extends BaseRequestModel {

    private InteractionType type;
    private int detailId;
    private int detailRevision;
    private Date date;
    private String additionalDetails;
    private boolean voucherUsedAgain;
    private double voucherUsedAtLatitude;
    private double voucherUserAtLongitude;
    private Date voucherUsedAtTimestamp;
    private double voucherUsedAtAccuracy;

    public AddInteractionRequestModel(InteractionType type, int detailId, int detailRevision, Date date, String additionalDetails, boolean voucherUsedAgain, double voucherUsedAtLatitude, double voucherUserAtLongitude, Date voucherUsedAtTimestamp, double voucherUsedAtAccuracy, String userId, int clientVersion, Date lastSync, double latitude, double longitude, double locationAccuracy, Date locationTimestamp) {
        super(userId, clientVersion, lastSync, latitude, longitude, locationAccuracy, locationTimestamp);
        this.type = type;
        this.detailId = detailId;
        this.detailRevision = detailRevision;
        this.date = date;
        this.additionalDetails = additionalDetails;
        this.voucherUsedAgain = voucherUsedAgain;
        this.voucherUsedAtLatitude = voucherUsedAtLatitude;
        this.voucherUserAtLongitude = voucherUserAtLongitude;
        this.voucherUsedAtTimestamp = voucherUsedAtTimestamp;
        this.voucherUsedAtAccuracy = voucherUsedAtAccuracy;
    }

    public InteractionType getType() {
        return type;
    }

    public void setType(InteractionType type) {
        this.type = type;
    }

    public int getDetailId() {
        return detailId;
    }

    public void setDetailId(int detailId) {
        this.detailId = detailId;
    }

    public int getDetailRevision() {
        return detailRevision;
    }

    public void setDetailRevision(int detailRevision) {
        this.detailRevision = detailRevision;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAdditionalDetails() {
        return additionalDetails;
    }

    public void setAdditionalDetails(String additionalDetails) {
        this.additionalDetails = additionalDetails;
    }

    public boolean isVoucherUsedAgain() {
        return voucherUsedAgain;
    }

    public void setVoucherUsedAgain(boolean voucherUsedAgain) {
        this.voucherUsedAgain = voucherUsedAgain;
    }

    public double getVoucherUsedAtLatitude() {
        return voucherUsedAtLatitude;
    }

    public void setVoucherUsedAtLatitude(double voucherUsedAtLatitude) {
        this.voucherUsedAtLatitude = voucherUsedAtLatitude;
    }

    public double getVoucherUserAtLongitude() {
        return voucherUserAtLongitude;
    }

    public void setVoucherUserAtLongitude(double voucherUserAtLongitude) {
        this.voucherUserAtLongitude = voucherUserAtLongitude;
    }

    public Date getVoucherUsedAtTimestamp() {
        return voucherUsedAtTimestamp;
    }

    public void setVoucherUsedAtTimestamp(Date voucherUsedAtTimestamp) {
        this.voucherUsedAtTimestamp = voucherUsedAtTimestamp;
    }

    public double getVoucherUsedAtAccuracy() {
        return voucherUsedAtAccuracy;
    }

    public void setVoucherUsedAtAccuracy(double voucherUsedAtAccuracy) {
        this.voucherUsedAtAccuracy = voucherUsedAtAccuracy;
    }
}
