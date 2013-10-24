/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class Field {

    private FieldType type;
    private String variable;
    private String label;

    public Field(FieldType type, String variable, String label) {
        this.type = type;
        this.variable = variable;
        this.label = label;
    }

    public FieldType getType() {
        return type;
    }

    public void setType(FieldType type) {
        this.type = type;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
