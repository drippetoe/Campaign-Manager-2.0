/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice.response;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class AlertDetailsResponseModel extends BaseResponseModel {
    private int id;
    private int revision;
    private String folder;
    private String title;
    private boolean read;
    private String type;
    private int referenceId;
    private String contentHtml;

    public AlertDetailsResponseModel() {
        super();
    }

    public AlertDetailsResponseModel(int id, int revision, String folder, String title, boolean read, String type, int referenceId, String contentHtml, boolean success, String message) {
        super(success, message);
        this.id = id;
        this.revision = revision;
        this.folder = folder;
        this.title = title;
        this.read = read;
        this.type = type;
        this.referenceId = referenceId;
        this.contentHtml = contentHtml;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(int referenceId) {
        this.referenceId = referenceId;
    }

    public String getContentHtml() {
        return contentHtml;
    }

    public void setContentHtml(String contentHtml) {
        this.contentHtml = contentHtml;
    }
}
