package com.kazemobile.types;

//

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CustomizedSystemMessage implements Serializable {
    private long customizedSystemMessageId;
    private int shortCodeId;
    private ReservedKeywordType keyword;
    private String alias;
    private String message;
    private long campaignId;
    
    public CustomizedSystemMessage() { 
        customizedSystemMessageId = 0L;
        keyword = ReservedKeywordType.Stop;
        shortCodeId = 0;
        campaignId = 0L;
    }
    
    public long getCustomizedSystemMessageId() { return customizedSystemMessageId; }
    public void setCustomizedSystemMessageId(long value) { customizedSystemMessageId = value; }
    
    public int getShortCodeId() { return shortCodeId; }
    public void setShortCodeId(int value) { shortCodeId = value; }
    
    public long getCampaignId() { return campaignId; }
    public void setCampaignId(long value) { campaignId = value; }
    
    public ReservedKeywordType getKeyword() { return keyword; }
    public void setKeyword(ReservedKeywordType value) { keyword = value; }
    
    public String getAlias() { return alias; }
    public void setAlias(String value) { alias = value; }
    
    public String getMessage() { return message; }
    public void setMessage(String value) { message = value; }
    
    @Override
    public int hashCode() {
        return hashCode(customizedSystemMessageId);
    }

    /**
    * Return the same value as <code>{@link Long#hashCode()}</code>.
    * @see Long#hashCode()
    */
    private static int hashCode(long lng) {
        return (int) (lng ^ (lng >>> 32));
    }
    
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the campaignId fields are not set
        if (!(object instanceof CustomizedSystemMessage)) {
            return false;
        }
        CustomizedSystemMessage other = (CustomizedSystemMessage) object;
        if (this.customizedSystemMessageId != other.customizedSystemMessageId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "services.CustomizedSystemMessage[ id=" + customizedSystemMessageId + " ]";
    }
    
}
