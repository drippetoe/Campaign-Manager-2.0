/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kazemobile.types;

/**
 *
 * @author rwalker
 */
public enum CampaignPrompts {
    OptIn("opt-in"),
    OptOut("opt-out");
    
    private final String value;

    CampaignPrompts(String v) { value=v; }

    public String value() { return value; }

    public static CampaignPrompts fromValue(String v) {
        for (CampaignPrompts c: CampaignPrompts.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }

        throw new IllegalArgumentException(v);
    }
}