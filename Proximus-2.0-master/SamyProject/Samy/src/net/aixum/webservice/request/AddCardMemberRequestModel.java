/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice.request;

import java.util.Date;
import java.util.List;
import net.aixum.webservice.FieldSubmission;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class AddCardMemberRequestModel extends BaseRequestModel {

    private int cardId;
    private int revision;
    private boolean installed;
    private List<FieldSubmission> submittedFields;

    public AddCardMemberRequestModel(int cardId, int revision, boolean installed, List<FieldSubmission> submittedFields, String userId, int clientVersion, Date lastSync, double latitude, double longitude, double locationAccuracy, Date locationTimestamp) {
        super(userId, clientVersion, lastSync, latitude, longitude, locationAccuracy, locationTimestamp);
        this.cardId = cardId;
        this.revision = revision;
        this.installed = installed;
        this.submittedFields = submittedFields;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public boolean isInstalled() {
        return installed;
    }

    public void setInstalled(boolean installed) {
        this.installed = installed;
    }

    public List<FieldSubmission> getSubmittedFields() {
        return submittedFields;
    }

    public void setSubmittedFields(List<FieldSubmission> submittedFields) {
        this.submittedFields = submittedFields;
    }
}
