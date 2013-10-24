/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.bean.sms;

/**
 *
 * @author ronald
 */
public class MobileSystemMessageProfile {

    private String systemMessage;
    private Long messageCount;
    private String cost;

    public MobileSystemMessageProfile() {
        this.messageCount = 0L;
    }

    public String getSystemMessage() {
        return systemMessage;
    }

    public void setSystemMessage(String systemMessage) {
        this.systemMessage = systemMessage;
    }

    public Long getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(Long messageCount) {
        this.messageCount = messageCount;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (this.systemMessage != null ? this.systemMessage.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MobileSystemMessageProfile other = (MobileSystemMessageProfile) obj;
        if ((this.systemMessage == null) ? (other.systemMessage != null) : !this.systemMessage.equals(other.systemMessage)) {
            return false;
        }
        return true;
    }
}
