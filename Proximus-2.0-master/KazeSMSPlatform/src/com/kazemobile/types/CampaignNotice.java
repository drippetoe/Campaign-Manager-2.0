/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kazemobile.types;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rwalker
 */
@XmlRootElement
public class CampaignNotice {
    private Integer campaignNoticeId;
    private long campaignId;
    private long campaignStepId;
    private CampaignNotificationType notificationTrigger;
    private String notificationEndpoint;
    private String notificationMessage;
    
    public CampaignNotice() {
        campaignNoticeId = new Integer(0);
        campaignStepId = 0L;
        notificationTrigger = CampaignNotificationType.AutoNotifyHttpGet;
    }
    
    public Integer getCampaignNoticeId() {
        return campaignNoticeId;
    }

    public void setCampaignNoticeId(Integer campaignNoticeId) {
        this.campaignNoticeId = campaignNoticeId;
    }

    public long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(long campaignId) {
        this.campaignId = campaignId;
    }

    public long getCampaignStepId() {
        return campaignStepId;
    }

    public void setCampaignStepId(long campaignStepId) {
        this.campaignStepId = campaignStepId;
    }

    public CampaignNotificationType getNotificationTrigger() {
        return notificationTrigger;
    }

    public void setNotificationTrigger(CampaignNotificationType notificationTrigger) {
        this.notificationTrigger = notificationTrigger;
    }

    public String getNotificationEndpoint() {
        return notificationEndpoint;
    }

    public void setNotificationEndpoint(String notificationEndpoint) {
        this.notificationEndpoint = notificationEndpoint;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }
}
