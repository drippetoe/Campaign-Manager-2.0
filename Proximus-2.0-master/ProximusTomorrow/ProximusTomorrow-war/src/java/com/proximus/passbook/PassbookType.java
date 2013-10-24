/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.passbook;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dshaw
 */
public class PassbookType {

    // a field is the smallest and most basic element information that's visible on the pass, each one has a key, a label, and a value
    // optionally: text alignment, change message, date and time + style (WC3 format, PKDateStyleNone, PKDateStyleShort)
    // headerFields in the top if the pass, visible when pass is in the stack
    private List<PassbookField> headerFields;
    // primaryFields location varies based on style, most visible and largest fields
    private List<PassbookField> primaryFields;
    // secondaryFields of secondary importance
    private List<PassbookField> secondaryFields;
    // optional auxiliaryFields
    private List<PassbookField> auxiliaryFields;
    // fields on the back of the pass, unlimited in number like terms & conditions
    private List<PassbookField> backFields;

    public void addHeaderField(PassbookField field) {
        if (headerFields == null) {
            headerFields = new ArrayList<PassbookField>();
        }
        headerFields.add(field);
    }

    public void addPrimaryField(PassbookField field) {
        if (primaryFields == null) {
            primaryFields = new ArrayList<PassbookField>();
        }
        primaryFields.add(field);
    }

    public void addAuxiliaryField(PassbookField field) {
        if (auxiliaryFields == null) {
            auxiliaryFields = new ArrayList<PassbookField>();
        }
        auxiliaryFields.add(field);
    }

    public void addSecondaryField(PassbookField field) {
        if (secondaryFields == null) {
            secondaryFields = new ArrayList<PassbookField>();
        }
        secondaryFields.add(field);
    }

    public void addBackField(PassbookField field) {
        if (backFields == null) {
            backFields = new ArrayList<PassbookField>();
        }
        backFields.add(field);
    }

    public List<PassbookField> getHeaderFields() {
        return headerFields;
    }

    public List<PassbookField> getPrimaryFields() {
        return primaryFields;
    }

    public List<PassbookField> getSecondaryFields() {
        return secondaryFields;
    }

    public List<PassbookField> getAuxiliaryFields() {
        return auxiliaryFields;
    }

    public List<PassbookField> getBackFields() {
        return backFields;
    }

    
}
