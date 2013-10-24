/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Eric Johansson
 */
@Entity
@DiscriminatorValue("GENERIC")
@Table(name = "bluetooth_file")
@XmlRootElement(name = "file")
@XmlAccessorType(XmlAccessType.FIELD)
public class BluetoothFile implements Comparable<BluetoothFile>, Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    @XmlAttribute(name = "id")
    private Long id;
    
    @Size(max = 255)
    @Column(name = "name")
    @XmlAttribute(name = "name")
    private String name;
    
    @Size(max = 255)
    @Column(name = "title")
    @XmlAttribute(name = "title")
    private String title;
    
    @Column(name = "bt_order")
    @XmlAttribute(name = "bt_order")
    private int btOrder;
    
    @Size(max = 15)
    @Column(name = "days_of_week")
    @XmlAttribute(name = "days_of_week")
    private String daysOfWeek;
    
    @Size(max = 10)
    @Column(name = "start_time")
    @XmlAttribute(name = "start_time")
    private String startTime;
    
    @Size(max = 10)
    @Column(name = "end_time")
    @XmlAttribute(name = "end_time")
    private String endTime;
    
    @Size(max = 255)
    @Column(name = "mime")
    @XmlAttribute(name = "mime")
    private String mime;
    
    @Column(name = "campaign_id")
    @XmlAttribute(name = "campaign_id")
    private long campId;

    public BluetoothFile()
    {
        id = 0L;
    }

    public String getDaysOfWeek()
    {
        return daysOfWeek;
    }

    public void setDaysOfWeek(String daysOfWeek)
    {
        this.daysOfWeek = daysOfWeek;
    }

    public String getEndTime()
    {
        return endTime;
    }

    public void setEndTime(String endTime)
    {
        this.endTime = endTime;
    }

    public String getMime()
    {
        return mime;
    }

    public void setMime(String mime)
    {
        this.mime = mime;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getBtOrder()
    {
        return btOrder;
    }

    public void setBtOrder(int order)
    {
        this.btOrder = order;
    }

    public String getStartTime()
    {
        return startTime;
    }

    public void setStartTime(String startTime)
    {
        this.startTime = startTime;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setCampId(long campId)
    {
        this.campId = campId;
    }

    public Long getCampId()
    {
        return campId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof BluetoothFile) {
            BluetoothFile file = (BluetoothFile) o;
            if (!this.name.equals(file.getName())) {
                return false;
            }
            if (!this.title.equals(file.getTitle())) {
                return false;
            }
            if (this.btOrder != file.getBtOrder()) {
                return false;
            }
            if (!this.startTime.equals(file.getStartTime())) {
                return false;
            }
            if (!this.endTime.equals(file.getEndTime())) {
                return false;
            }
            if (!this.mime.equals(file.getMime())) {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 37 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 37 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 37 * hash + this.btOrder;
        hash = 37 * hash + (this.daysOfWeek != null ? this.daysOfWeek.hashCode() : 0);
        hash = 37 * hash + (this.startTime != null ? this.startTime.hashCode() : 0);
        hash = 37 * hash + (this.endTime != null ? this.endTime.hashCode() : 0);
        hash = 37 * hash + (this.mime != null ? this.mime.hashCode() : 0);
        return hash;
    }

    @Override
    public int compareTo(BluetoothFile t)
    {
        return t.getBtOrder() - this.btOrder;
    }
}
