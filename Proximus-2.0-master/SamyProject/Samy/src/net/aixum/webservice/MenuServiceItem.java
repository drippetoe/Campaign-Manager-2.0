/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class MenuServiceItem {

    private String type;
    private String url;
    private String urlschema;
    private String title;
    private String subline;

    public MenuServiceItem(String type, String url, String urlschema, String title, String subline) {
        this.type = type;
        this.url = url;
        this.urlschema = urlschema;
        this.title = title;
        this.subline = subline;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlschema() {
        return urlschema;
    }

    public void setUrlschema(String urlschema) {
        this.urlschema = urlschema;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubline() {
        return subline;
    }

    public void setSubline(String subline) {
        this.subline = subline;
    }
    
    
}
