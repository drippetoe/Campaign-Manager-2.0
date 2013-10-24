/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kazemobile.types;

/**
 *
 * @author rwalker
 */
public enum ReservedKeywordType {
    Stop("STOP"),
    Help("HELP");

    private final String value;

    ReservedKeywordType(String v) { value=v; }

    public String value() { return value; }

    public static ReservedKeywordType fromValue(String v) {
        for (ReservedKeywordType c: ReservedKeywordType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }

        throw new IllegalArgumentException(v);
    }
}
