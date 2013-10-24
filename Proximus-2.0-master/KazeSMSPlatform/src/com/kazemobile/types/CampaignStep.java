package com.kazemobile.types;

import java.io.Serializable;
import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rwalker
 */
@XmlRootElement
public class CampaignStep implements Serializable {
    private Long campaignStepId;
    private Date launchOn;
    private Date expiresOn;
    private long campaignId;
    private int stepIndex;
    private CampaignTimeZones timeZoneId;
    private String campaignMsg;
    private int campaignMsgLimit;
    private String queueStatus;
    
    public CampaignStep() {
        campaignStepId = new Long(0);
        stepIndex = 0;
        timeZoneId = CampaignTimeZones.Eastern;
        campaignMsgLimit = 160;
    }
    
    public Long getCampaignStepId() {
        return campaignStepId;
    }

    public void setCampaignStepId(Long campaignStepId) {
        this.campaignStepId = campaignStepId;
    }

    public long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(long campaignId) {
        this.campaignId = campaignId;
    }

    public int getStepIndex() {
        return stepIndex;
    }

    public void setStepIndex(int stepIndex) {
        this.stepIndex = stepIndex;
    }

    public CampaignTimeZones getTimeZoneId() {
        return timeZoneId;
    }

    public void setTimeZoneId(CampaignTimeZones timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    public String getCampaignMsg() {
        return campaignMsg;
    }

    public void setCampaignMsg(String campaignMsg) {
        this.campaignMsg = campaignMsg;
    }

    public int getCampaignMsgLimit() {
        return campaignMsgLimit;
    }

    public void setCampaignMsgLimit(int campaignMsgLimit) {
        this.campaignMsgLimit = campaignMsgLimit;
    }

    public String getQueueStatus() {
        return queueStatus;
    }

    public void setQueueStatus(String queueStatus) {
        this.queueStatus = queueStatus;
    }
    
    public Date getLaunchOn() {
        return launchOn;
    }

    public void setLaunchOn(Date launchOn) {
        this.launchOn = launchOn;
    }

    public Date getExpiresOn() {
        return expiresOn;
    }

    public void setExpiresOn(Date expiresOn) {
        this.expiresOn = expiresOn;
    }
}

