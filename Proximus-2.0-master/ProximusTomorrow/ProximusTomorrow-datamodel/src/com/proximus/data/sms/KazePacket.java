package com.proximus.data.sms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
@XmlRootElement(name = "kazePacket")
@XmlAccessorType(XmlAccessType.FIELD)
public final class KazePacket {

    @XmlTransient
    private String fileName;
    @XmlElement(name = "eventDate")
    private Date eventDate;
    @XmlElement(name = "campaignId")
    private Long campaignId;
    @XmlElementWrapper(name = "mobileOfferSendLogs")
    @XmlElement(name = "mobileOfferSendLog")
    private List<MobileOfferSendLog> mobileOfferSendLogs;

    public KazePacket() {
        this.mobileOfferSendLogs = new ArrayList<MobileOfferSendLog>();
        this.campaignId = -1L;
    }

    public KazePacket(String fileName) {
        this.fileName = fileName;
        this.mobileOfferSendLogs = new ArrayList<MobileOfferSendLog>();
        this.campaignId = -1L;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public List<MobileOfferSendLog> getMobileOfferSendLogs() {
        return this.mobileOfferSendLogs;
    }

    public void setMobileOfferSendLogs(List<MobileOfferSendLog> mobileOfferSendLogs) {
        this.mobileOfferSendLogs = mobileOfferSendLogs;
    }

    public void addMobileOfferSendLog(MobileOfferSendLog mobileOfferSendLogs) {
        this.mobileOfferSendLogs.add(mobileOfferSendLogs);
    }

    public void clearMobileOfferSendLogs() {
        this.mobileOfferSendLogs = new ArrayList<MobileOfferSendLog>();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String filename) {
        this.fileName = filename;
    }
}
