/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.sms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Angela Mercer
 */
/**
 * <keyword>
    * <accountId>20</accountId>
    * <createdBy>37</createdBy>
    * <createdOn>2012-06-06T17:50:05.623Z</createdOn>
    * <entityId>20</entityId>
    * <keyword>elephant</keyword>
    * <keywordId>261</keywordId>
    * <modifiedBy>37</modifiedBy>
    * <modifiedOn>2012-06-06T17:50:05.623Z</modifiedOn>
    * <recordStateId>true</recordStateId>
    * <shortCodeId>2</shortCodeId>
 * </keyword>
*/
@XmlRootElement(name = "keyword")
@XmlAccessorType(XmlAccessType.FIELD)
public class KeywordResponse
{
    @XmlElement(name="accountId")
    private String accountId;
    
    @XmlElement (name="createdBy")
    private String createdBy;
    
    @XmlElement(name = "createdOn")
    private String dateCreated;
    
    @XmlElement(name="entityId")
    private String entityId;
    
    @XmlElement(name="keyword")
    private String keyword;
    
    @XmlElement(name="keywordId")
    private String keywordId;
    
    @XmlElement(name="modifiedBy")
    private String modifiedBy;
    
    @XmlElement(name="modifiedOn")
    private String modifiedOn;
    
    @XmlElement(name="recordStateId")
    private String recordStateId;
    
    @XmlElement(name="shortCodeId")
    private String ShortCodeId;

    public String getShortCodeId()
    {
        return ShortCodeId;
    }

    public void setShortCodeId(String ShortCodeId)
    {
        this.ShortCodeId = ShortCodeId;
    }

    public String getAccountId()
    {
        return accountId;
    }

    public void setAccountId(String accountId)
    {
        this.accountId = accountId;
    }

    public String getCreatedBy()
    {
        return createdBy;
    }

    public void setCreatedBy(String createdBy)
    {
        this.createdBy = createdBy;
    }

    public String getDateCreated()
    {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated)
    {
        this.dateCreated = dateCreated;
    }

    public String getEntityId()
    {
        return entityId;
    }

    public void setEntityId(String entityId)
    {
        this.entityId = entityId;
    }

    public String getKeyword()
    {
        return keyword;
    }

    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }

    public String getKeywordId()
    {
        return keywordId;
    }

    public void setKeywordId(String keywordId)
    {
        this.keywordId = keywordId;
    }

    public String getModifiedBy()
    {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy)
    {
        this.modifiedBy = modifiedBy;
    }

    public String getModifiedOn()
    {
        return modifiedOn;
    }

    public void setModifiedOn(String modifiedOn)
    {
        this.modifiedOn = modifiedOn;
    }

    public String getRecordStateId()
    {
        return recordStateId;
    }

    public void setRecordStateId(String recordStateId)
    {
        this.recordStateId = recordStateId;
    }
    
    
    
    
    
    
    
    
    
    
    
}
