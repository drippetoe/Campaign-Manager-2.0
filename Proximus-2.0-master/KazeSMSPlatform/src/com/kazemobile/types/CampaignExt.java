/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kazemobile.types;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rwalker
 */
@XmlRootElement
public class CampaignExt implements Serializable {
    private Long campaignId;
    private int campaignTemplateId;
    private int keywordId;
    private String keyword;
    private int shortCodeId;
    private String shortCode;
    private int distributionListId;
    private int accountId;
    private BigInteger contactUserId;
    private String name;
    private String description;
    private Date launchOn;
    private Date expiresOn;
    private String queueStatus;
    
    public CampaignExt() {
        campaignId = new Long(0);
    }
    
    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }
    
    public int getCampaignTemplateId() {
        return campaignTemplateId;
    }

    public void setCampaignTemplateId(int campaignTemplateId) {
        this.campaignTemplateId = campaignTemplateId;
    }

    public int getKeywordId() {
        return keywordId;
    }

    public void setKeywordId(int keywordId) {
        this.keywordId = keywordId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getShortCodeId() {
        return shortCodeId;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public void setShortCodeId(int shortCodeId) {
        this.shortCodeId = shortCodeId;
    }

    public int getDistributionListId() {
        return distributionListId;
    }

    public void setDistributionListId(int distributionListId) {
        this.distributionListId = distributionListId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public BigInteger getContactUserId() {
        return contactUserId;
    }

    public void setContactUserId(BigInteger contactUserId) {
        this.contactUserId = contactUserId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getQueueStatus() {
        return queueStatus;
    }

    public void setQueueStatus(String status) {
        this.queueStatus = status;
    }
}

