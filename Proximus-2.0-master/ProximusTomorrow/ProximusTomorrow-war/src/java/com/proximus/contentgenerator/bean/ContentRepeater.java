/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.contentgenerator.bean;

/**
 *
 * @author Gilberto Gaxiola
 */
public class ContentRepeater extends HtmlBlock {

    public static final String START_COMMAND = "start";
    public static final String END_COMMAND = "end";

    public ContentRepeater(String id) {
        super(id, ContentRepeater.class);
    }
    
    public ContentRepeater(String id, String macro) {
        this(id);
        this.tag = macro;
    }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public String getEditor() {
        return "";
    }

    
}
