/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.contentgenerator.bean;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 *
 * @author Gilberto Gaxiola
 */
public class TextEditor extends HtmlBlock {

    public TextEditor(String id) {

        super(id, TextEditor.class);

    }

    public String convert(String html) {
        /*
         * 1. Search for pattern "#"+this.getClass().getSimpleName()
         */
        return html.replace(this.tag, this.toString());
    }

    @Override
    public String toString() {
        return StringEscapeUtils.escapeHtml4(this.value);  
    }
    
    
    

    @Override
    public String getEditor() {
        StringBuilder sb = new StringBuilder();
        sb.append("<!--").append(this.tag).append("-->\n");
        sb.append("<div id=\"").append(this.id).append("_container\" class=\"").append(this.getClass().getSimpleName()).append("\">");
        sb.append("\n\t").append("<input type=\"hidden\" id=\"").append(this.id).append("\" name=\"").append(this.id).append("\" value=\"").append(this.toString()).append("\"/>");
        sb.append("\n\t").append("<div>").append(this.toString()).append("</div>");
        sb.append("\n\t").append("<textarea class=\"editMe\"></textarea>");
        sb.append("\n").append("</div>");
        return sb.toString();
    }
    
    
    public static void main(String[] args) {
        String value = "<h1>This is just an example \"yes\" &copy 2012</h1>";
        System.out.println("value: " + value);
        System.out.println(StringEscapeUtils.escapeHtml4(value));
    }
}
