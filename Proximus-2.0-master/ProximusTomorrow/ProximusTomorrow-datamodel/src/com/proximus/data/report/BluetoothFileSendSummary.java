package com.proximus.data.report;

import com.proximus.data.Campaign;
import com.proximus.data.Company;
import com.proximus.data.Device;
import java.io.Serializable;
import java.util.Date;
import javax.jdo.annotations.Index;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 *
 * Represents the send count of a file or tag for a given day, company,
 * campaign, file
 *
 * @author dshaw
 */
@Entity
@Index(members = {"eventDate", "company", "campaign", "device", "file"}, unique = "true")
@Table(name = "summary_filesend_daily")
public class BluetoothFileSendSummary implements Serializable {

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
    @ManyToOne
    protected Device device;
    @Basic(optional = false)
    @Index
    @Column(name = "event_date")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date eventDate;
    @Column(name = "send_count")
    private Long sendCount;
    @Index
    @Column(name = "file")
    String file;
    @Transient
    Long bluetoothDetections;
    @Transient
    Long wifiSuccessfulPageViews;

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

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Long getSendCount() {
        return sendCount;
    }

    public void setSendCount(Long sendCount) {
        this.sendCount = sendCount;
    }

    public Long getBluetoothDetections() {
        return bluetoothDetections;
    }

    public void setBluetoothDetections(Long bluetoothDetections) {
        this.bluetoothDetections = bluetoothDetections;
    }

    public Long getWifiSuccessfulPageViews() {
        return wifiSuccessfulPageViews;
    }

    public void setWifiSuccessfulPageViews(Long wifiSuccessfulPageViews) {
        this.wifiSuccessfulPageViews = wifiSuccessfulPageViews;
    }

    @Override
    public String toString() {
        return getEventDate() + " | " + getFile() + " | " + getSendCount();
    }
}
