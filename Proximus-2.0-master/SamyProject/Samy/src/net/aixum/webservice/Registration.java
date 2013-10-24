/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice;

import java.util.List;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class Registration {

    private String title;
    private String text;
    private List<Field> fields;

    public Registration(String title, String text, List<Field> fields) {
        this.title = title;
        this.text = text;
        this.fields = fields;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }
}
