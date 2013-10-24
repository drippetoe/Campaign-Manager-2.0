/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.jdo.annotations.Index;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author ronald
 */
@Entity
@Table(name = "wifi_registration")
@DiscriminatorValue("wifi")
public class WifiRegistration implements Serializable {

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM/dd/yyyy HH:mm:ss z");
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    protected Long id;
    @Basic(optional = false)
    @NotNull
    @Index
    @Column(name = "event_date")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date eventDate;
    @Size(max = 1024)
    @Column(name = "field1")
    protected String field1;
    @Size(max = 1024)
    @Column(name = "field2")
    protected String field2;
    @Size(max = 1024)
    @Column(name = "field3")
    protected String field3;
    @Size(max = 1024)
    @Column(name = "field4")
    protected String field4;
    @Size(max = 1024)
    @Column(name = "field5")
    protected String field5;
    @Size(max = 1024)
    @Column(name = "field6")
    protected String field6;
    @Size(max = 1024)
    @Column(name = "field7")
    protected String field7;
    @Size(max = 1024)
    @Column(name = "field8")
    protected String field8;
    @Size(max = 1024)
    @Column(name = "field9")
    protected String field9;
    @Size(max = 1024)
    @Column(name = "field10")
    protected String field10;
    @Size(max = 1024)
    @Column(name = "field11")
    protected String field11;
    @Size(max = 1024)
    @Column(name = "field12")
    protected String field12;
    @Size(max = 1024)
    @Column(name = "field13")
    protected String field13;
    @Size(max = 1024)
    @Column(name = "field14")
    protected String field14;
    @Size(max = 1024)
    @Column(name = "field15")
    protected String field15;
    @Index
    @Column(name = "mac_address")
    protected String macAddress;
    @ManyToOne
    private Company company;
    @Index
    @ManyToOne
    private Campaign campaign;
    @ManyToOne
    private Device device;

    public WifiRegistration() {
        id = 0L;
    }

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }

    public String getField3() {
        return field3;
    }

    public void setField3(String field3) {
        this.field3 = field3;
    }

    public String getField4() {
        return field4;
    }

    public void setField4(String field4) {
        this.field4 = field4;
    }

    public String getField5() {
        return field5;
    }

    public void setField5(String field5) {
        this.field5 = field5;
    }

    public String getField6() {
        return field6;
    }

    public void setField6(String field6) {
        this.field6 = field6;
    }

    public String getField7() {
        return field7;
    }

    public void setField7(String field7) {
        this.field7 = field7;
    }

    public String getField8() {
        return field8;
    }

    public void setField8(String field8) {
        this.field8 = field8;
    }

    public String getField9() {
        return field9;
    }

    public void setField9(String field9) {
        this.field9 = field9;
    }

    public String getField10() {
        return field10;
    }

    public void setField10(String field10) {
        this.field10 = field10;
    }

    public String getField11() {
        return field11;
    }

    public void setField11(String field11) {
        this.field11 = field11;
    }

    public String getField12() {
        return field12;
    }

    public void setField12(String field12) {
        this.field12 = field12;
    }

    public String getField13() {
        return field13;
    }

    public void setField13(String field13) {
        this.field13 = field13;
    }

    public String getField14() {
        return field14;
    }

    public void setField14(String field14) {
        this.field14 = field14;
    }

    public String getField15() {
        return field15;
    }

    public void setField15(String field15) {
        this.field15 = field15;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String toCVS() {
        String cvs = "";
        cvs += "\"" + WifiRegistration.dateFormat.format(this.eventDate) + "\",";
        cvs += "\"" + this.field1 + "\",";
        cvs += "\"" + this.field2 + "\",";
        cvs += "\"" + this.field3 + "\",";
        cvs += "\"" + this.field4 + "\",";
        cvs += "\"" + this.field5 + "\",";
        cvs += "\"" + this.field6 + "\",";
        cvs += "\"" + this.field7 + "\",";
        cvs += "\"" + this.field8 + "\",";
        cvs += "\"" + this.field9 + "\",";
        cvs += "\"" + this.field10 + "\",";
        cvs += "\"" + this.field11 + "\",";
        cvs += "\"" + this.field12 + "\",";
        cvs += "\"" + this.field13 + "\",";
        cvs += "\"" + this.field14 + "\",";
        cvs += "\"" + this.field15 + "\",";
        cvs += "\"" + this.macAddress + "\",";
        cvs += "\"" + (this.device != null?this.device.getName():"No Device") + "\",";
        cvs += "\"" + (this.campaign != null?this.campaign.getName():"No Campaign") + "\",";
        cvs += "\"" + (this.company != null?this.company.getName():"No Company") + "\"";
        
        return cvs;
    }

    public static String getCVSHeader() {
        return "event_date, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, mac_address, device,campaign, company";
    }
}