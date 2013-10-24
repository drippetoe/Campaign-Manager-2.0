package com.kazemobile.types;

/**
 *
 * @author rwalker
 */
public enum CampaignNotificationType {
    AutoNotifyEmail("AUTO_NOTIFY_EMAIL"),
    AutoNotifyHttpGet("AUTO_NOTIFY_HTTP_GET"),
    AutoNotifyHttpPost("AUTO_NOTIFY_HTTP_POST"),
    AutoNotifySMS("AUTO_NOTIFY_SMS"),
    ManualNotifyEmail("MANUAL_NOTIFY_EMAIL"),
    ManualNotifySMS("MANUAL_NOTIFY_SMS"),
    NoOp("NO_OP");

    private final String value;

    CampaignNotificationType(String v) { value=v; }

    public String value() { return value; }

    public static CampaignNotificationType fromValue(String v) {
        for (CampaignNotificationType c: CampaignNotificationType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }

        throw new IllegalArgumentException(v);
    }
}
