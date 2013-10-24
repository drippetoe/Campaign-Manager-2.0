/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 *
 * @author ronald
 */
@Entity
@Table(name = "barcode")
public class Barcode implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @Column(name = "event_date")
    @Temporal(TemporalType.TIMESTAMP)
    Date eventDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "mac_address")
    String macAddress;
    @Basic(optional = false)
    @NotNull
    @Column(name = "type")
    String type;
    @Basic(optional = false)
    @NotNull
    @Column(name = "barcode")
    String barcode;
    @ManyToOne
    private Company company;
    @ManyToOne
    protected Device device;
    @ManyToOne
    protected Campaign campaign;

    public Barcode() {
        id = 0L;
    }

    public Barcode(Date eventDate, String macAddress, String type, String barcode) {
        id = 0L;
        this.eventDate = eventDate;
        this.macAddress = macAddress;
        this.type = type;
        this.barcode = barcode;
    }
    
     public static String formatTimestampForWeb(Date date) {
        final String TIMESTAMP_FMT = "dd-MMMM-yyyy HH:mm:ss";
        return new SimpleDateFormat(TIMESTAMP_FMT).format(date);
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
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
        this.macAddress = macAddress;
    }

    @Override
    public String toString() {
        return "Barcode{" + "barcode=" + barcode + '}';
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
    
    public String getEventDateFmt() {
        if (this.eventDate == null) {
            return "Never";
        } else {
            return Barcode.formatTimestampForWeb(eventDate);
        }

    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }
    
}
