/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class Setting {

    private String type;
    private String variable;
    private String label;
    private String byDefault; //default is a reserved keyword

    public Setting(String type, String variable, String label, String byDefault) {
        this.type = type;
        this.variable = variable;
        this.label = label;
        this.byDefault = byDefault;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
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

    public String getByDefault() {
        return byDefault;
    }

    public void setByDefault(String byDefault) {
        this.byDefault = byDefault;
    }
}
