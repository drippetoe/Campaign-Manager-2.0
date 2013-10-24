/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice;

import java.util.List;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class Form {

    private String title;
    private String text;
    private String validateUrl;
    private List<Field> fields;

    public Form(String title, String text, String validateUrl, List<Field> fields) {
        this.title = title;
        this.text = text;
        this.validateUrl = validateUrl;
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

    public String getValidateUrl() {
        return validateUrl;
    }

    public void setValidateUrl(String validateUrl) {
        this.validateUrl = validateUrl;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }
}
