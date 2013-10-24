/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Gilberto Gaxiola
 */
@Entity
@Table(name = "bluetooth_campaign")
@DiscriminatorValue("bluetooth")
@XmlRootElement(name = "bluetooth_campaign")
@XmlAccessorType(XmlAccessType.FIELD)
public class BluetoothCampaign extends CampaignType
{
    @Column(name = "bluetooth_mode")
    @XmlAttribute(name = "bluetooth_mode")
    private Integer bluetoothMode;
    @Size(max = 255)
    @Column(name = "friendly_name")
    @XmlAttribute(name = "friendly_name")
    private String friendlyName;

    public BluetoothCampaign()
    {
        this.id = 0L;
        this.bluetoothMode = 1;
    }

    public int getBluetoothMode()
    {
        return bluetoothMode;
    }

    public void setBluetoothMode(int bluetoothMode)
    {
        this.bluetoothMode = bluetoothMode;
    }

    public String getFriendlyName()
    {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName)
    {
        this.friendlyName = friendlyName;
    }
    
    @Override
    public boolean equals(Object campObj)
    {
        BluetoothCampaign camp;

        if (campObj instanceof BluetoothCampaign) {
            camp = (BluetoothCampaign) campObj;
        } else {
            return false;
        }

        if (this.id != camp.id) {
            return false;
        }
        if (this.bluetoothMode != camp.bluetoothMode) {
            return false;
        }
        if (!this.friendlyName.equals(camp.friendlyName)) {
            return false;
        }
        
        if (!this.checksum.equals(camp.checksum)) {
            return false;
        }
        return true;
    }

    @Override
    public String getType()
    {
        return "bluetooth";
    }

    @Override
    public Long getId()
    {
        return this.id;
    }

    @Override
    public void setId(Long id)
    {
        this.id = id;
    }
    
    
}
