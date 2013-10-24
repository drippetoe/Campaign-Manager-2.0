/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.passbook;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author dshaw
 */
@XmlRootElement
public class PassbookField {
    private String key;
    private String label;
    private String value;
    private String changeMessage;
    private String textAlignment;  // PKTextAlignmentRight

    public PassbookField(String key, String label, String value) {
        this.key = key;
        this.label = label;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getChangeMessage() {
        return changeMessage;
    }

    public void setChangeMessage(String changeMessage) {
        this.changeMessage = changeMessage;
    }

    public String getTextAlignment() {
        return textAlignment;
    }

    public void setTextAlignment(String textAlignment) {
        this.textAlignment = textAlignment;
    }

    

}
