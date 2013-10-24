/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kazemobile.types;

/**
 *
 * @author rwalker
 */
public enum CampaignTimeZones {
    Eastern("America/New_York"),
    Central("America/Chicago"),
    Mountain("America/Denver"),
    Pacific("America/Los_Angeles"),
    Alaska("America/Anchorage"),
    HawaiiAleutian("America/Adak");

    private final String value;

    CampaignTimeZones(String v) { value=v; }

    public String value() { return value; }

    public static CampaignTimeZones fromValue(String v) {
        for (CampaignTimeZones c: CampaignTimeZones.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }

        throw new IllegalArgumentException(v);
    }
}
