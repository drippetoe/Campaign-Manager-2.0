/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.contentgenerator.bean;

/**
 *
 * @author Gilberto Gaxiola
 */
public abstract class HtmlBlock {

    public static final String MACRO_TAG = "#PROX#";
    protected String id;
    protected String tag;
    protected String value;

    public HtmlBlock(String id, Class clazz) {
        this.id = id;
        this.tag = MACRO_TAG + clazz.getSimpleName() + "_" + this.id + MACRO_TAG;
        this.value = "";

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        this.tag = MACRO_TAG + this.getClass().getSimpleName() + "_" + this.id + MACRO_TAG;
    }

    public String getTag() {
        return tag;
    }
    
    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public abstract String toString();

    public abstract String getEditor();

}
