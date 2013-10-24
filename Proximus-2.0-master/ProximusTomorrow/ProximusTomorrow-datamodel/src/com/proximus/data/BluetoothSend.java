/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.jdo.annotations.Index;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 *
 * @author dshaw
 */
@Entity
@Index(members = {"eventDate", "macAddress", "file"}, unique = "true")
@Table(name = "bluetooth_send")
public class BluetoothSend implements Serializable {

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM/dd/yyyy HH:mm:ss z");
    private static final long serialVersionUID = 1L;
    // indicates the file was sent successfully
    public static final Integer STATUS_SENT = 1;
    // indicates the file was sent, and that more files will be sent
    public static final Integer STATUS_SENT_MORE = 2;
    // indicates the user ignored the request, we will not retry again
    public static final Integer STATUS_IGNORED = 3;
    // indicates the user ignored the request, we will retry again
    public static final Integer STATUS_IGNORED_RETRY = 4;
    // indicates that the user rejected the send request
    public static final Integer STATUS_REJECTED = 5;
    // indicates that the device did not support push
    public static final Integer STATUS_UNSUPPORTED = 6;
    // indicates "accepted" but not sent yet -- Dreamplug specific
    public static final Integer STATUS_ACCEPTED = 7;
    // something else - unknown
    public static final Integer STATUS_UNKNOWN = 99;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id = 0L;
    @ManyToOne
    @Index
    private Company company;
    @ManyToOne
    @Index
    private Campaign campaign;
    @Basic(optional = false)
    @Index
    @Column(name = "event_date")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date eventDate;
    @ManyToOne
    private Device device;
    @Column(name = "mac_address")
    @Index
    private String macAddress;
    @Column(name = "friendly_name")
    private String friendlyName;
    @Column(name = "file")
    private String file;
    @Column(name = "transfer_time")
    private Long transferTimeMS;
    @Column(name = "signal_strength")
    private Integer signalStrength;
    @Column(name = "accept_status")
    private Integer acceptStatus;
    @Column(name = "send_status")
    private Integer sendStatus;

    public static String formatMAC(String mac) {
        return mac.replace(":", "").toUpperCase();
    }

    public Integer getAcceptStatus() {
        return acceptStatus;
    }

    public void setAcceptStatus(Integer acceptStatus) {
        this.acceptStatus = acceptStatus;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = BluetoothSend.formatMAC(macAddress);
    }

    public Integer getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(Integer sendStatus) {
        this.sendStatus = sendStatus;
    }

    public Integer getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(Integer signalStrength) {
        this.signalStrength = signalStrength;
    }

    public Long getTransferTimeMS() {
        return transferTimeMS;
    }

    public void setTransferTimeMS(Long transferTimeMS) {
        this.transferTimeMS = transferTimeMS;
    }

    public static String getCVSHeader() {
        return "event_date, mac_address, friendly_name, signal_strength, transfer_time, send_status, accept_status, file, device, campaign, company";
    }

    public String toCVS() {
        String cvs = "";
        cvs += "\"" + BluetoothSend.dateFormat.format(this.eventDate) + "\",";
        cvs += "\"" + this.macAddress + "\",";
        cvs += "\"" + this.friendlyName + "\",";
        cvs += "\"" + this.signalStrength + "\",";
        cvs += "\"" + this.transferTimeMS + "\",";
        cvs += "\"" + this.sendStatus + "\",";
        cvs += "\"" + this.acceptStatus + "\",";
        cvs += "\"" + this.file + "\",";
        cvs += "\"" + (this.device != null?this.device.getName():"No Device") + "\",";
        cvs += "\"" + (this.campaign != null?this.campaign.getName():"No Campaign") + "\",";
        cvs += "\"" + (this.company != null?this.company.getName():"No Company") + "\"";
        return cvs;
    }

    @Override
    public String toString() {
        return this.macAddress;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BluetoothSend other = (BluetoothSend) obj;
        if ((this.macAddress == null) ? (other.macAddress != null) : !this.macAddress.equals(other.macAddress)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.macAddress != null ? this.macAddress.hashCode() : 0);
        return hash;
    }
    
}
