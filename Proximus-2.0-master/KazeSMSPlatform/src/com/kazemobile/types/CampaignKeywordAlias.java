/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kazemobile.types;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rwalker
 */
@XmlRootElement
public class CampaignKeywordAlias implements Serializable {
    private Long campaignKeywordAliasId;
    private long campaignId;
    private String keyword;
    
    public CampaignKeywordAlias() {
        campaignKeywordAliasId = 0L;
        campaignId = 0L;
        keyword = "";
    }

    public Long getCampaignKeywordAliasId() {
        return campaignKeywordAliasId;
    }

    public void setCampaignKeywordAliasId(Long id) {
        this.campaignKeywordAliasId = id;
    }

    public long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(long campaignId) {
        this.campaignId = campaignId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}

