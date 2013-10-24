/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.DefaultValue;

/**
 *
 * @author Gilberto Gaxiola
 */
@Entity
@DiscriminatorValue("GENERIC")
@Table(name = "device_in_limbo", uniqueConstraints =
{
    @UniqueConstraint(columnNames =
    {
        "mac_address", "serial_number"
    })
})
public class DeviceInLimbo implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;
    @Size(max = 255)
    @Column(name = "serial_number")
    private String serialNumber;
    @Basic(optional = false)
    @Size(min = 1, max = 17)
    @Column(name = "mac_address")
    private String macAddress;
    @Size(max = 46)
    @Column(name = "last_ip_address")
    private String lastIpAddress;
    @Size(max = 255)
    @Column(name = "token")
    private String token;
    @Column(name = "major")
    @DefaultValue("0")
    private long major = 0;
    @Column(name = "minor")
    @DefaultValue("0")
    private long minor = 0;
    @Column(name = "build")
    @DefaultValue("0")
    private long build = 0;
    @Size(max = 255)
    @Column(name = "kernel")
    private String kernel;
    @Column(name = "last_seen")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date lastSeen;
    
    
    public DeviceInLimbo() {
        this.id = 0L;
        
    }
    
    public DeviceInLimbo(String serialNumber, String macAddress)
    {
        this();
        this.serialNumber = serialNumber;
        this.macAddress = macAddress.toUpperCase().replaceAll(":", "");    
    }
    
    public DeviceInLimbo(String macAddress) {
        this();
        this.macAddress = macAddress.toUpperCase().replaceAll(":", "");    
        
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getLastIpAddress()
    {
        return lastIpAddress;
    }

    public void setLastIpAddress(String lastIpAddress)
    {
        this.lastIpAddress = lastIpAddress;
    }

    public Date getLastSeen()
    {
        return lastSeen;
    }

    public void setLastSeen(Date lastSeen)
    {
        this.lastSeen = lastSeen;
    }

    public String getMacAddress()
    {
        return macAddress;
    }

    public void setMacAddress(String macAddress)
    {
        if(macAddress != null) {
            this.macAddress = macAddress.toUpperCase().replaceAll(":", "");
        }
    }

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public long getBuild() {
        return build;
    }

    public void setBuild(long build) {
        this.build = build;
    }

    public String getKernel() {
        return kernel;
    }

    public void setKernel(String kernel) {
        this.kernel = kernel;
    }

    public long getMajor() {
        return major;
    }

    public void setMajor(long major) {
        this.major = major;
    }

    public long getMinor() {
        return minor;
    }

    public void setMinor(long minor) {
        this.minor = minor;
    }
}
