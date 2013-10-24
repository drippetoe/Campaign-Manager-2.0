/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class RatingService {

    private String title;
    private String subline;
    private String url;

    public RatingService(String title, String subline, String url) {
        this.title = title;
        this.subline = subline;
        this.url = url;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    
}
