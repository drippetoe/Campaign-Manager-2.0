/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.data.config;

import com.proximus.data.BluetoothConfig;
import com.proximus.data.ConfigProperty;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * 
 * @author ejohansson
 */
@XmlRootElement(name = "config")
@XmlAccessorType(XmlAccessType.FIELD)
public final class Config
{
    private Connection connection;
    private Authentication authentication;
    private Logging logging;
    private Software software;
    @XmlElementWrapper(name = "campaigns")
    @XmlElement(name = "campaign")
    private List<CampaignShort> campaigns;
    @XmlElementWrapper(name = "configProperties")
    @XmlElement(name = "property")
    private List<ConfigProperty> configProperties;
    private BluetoothConfig bluetooth;
    @XmlAttribute(name = "macAddr")
    private String macAddress;
    @XmlTransient
    private Date lastSeen;
    @XmlAttribute(name = "channel")
    private Integer channel;

    public Config()
    {
        this.connection = new Connection();
        this.authentication = new Authentication();
        this.logging = new Logging();
        this.software = new Software();
        this.campaigns = new ArrayList<CampaignShort>();
        this.configProperties = new ArrayList<ConfigProperty>();
        this.bluetooth = new BluetoothConfig();
    }

    private Config(Config client)
    {
        this.connection = client.connection;
        this.authentication = client.authentication;
        this.logging = client.logging;
        this.software = client.software;
        this.campaigns = client.campaigns;
        this.bluetooth = client.bluetooth;
        this.lastSeen = client.lastSeen;
        this.channel = client.channel;
        this.configProperties = client.configProperties;
    }

    public String getMacAddress()
    {
        return macAddress;
    }

    //Normalizing macAddress to be without semicolon and Upercase
    public void setMacAddress(String macAddress)
    {
        this.macAddress = macAddress.toUpperCase().replaceAll(":", "");
    }

    public String getAuthenticationToken()
    {
        return this.authentication.token;
    }

    public void setAuthenticationToken(String token)
    {
        this.authentication.token = token;
    }

    public long getReconnectInterval()
    {
        return this.connection.reconnectInterval;
    }

    public void setReconnectInterval(long interval)
    {
        //Never less than 30 sec
        if (interval > 30) {
            this.connection.reconnectInterval = interval;
        }
    }

    public long getKeepAlive()
    {
        return this.connection.keepAlive;
    }

    public void setKeepAlive(long keepAlive)
    {
        this.connection.keepAlive = keepAlive;
    }

    public long getLoggingRotation()
    {
        return this.logging.rotation;
    }

    public void setLoggingRotation(long rotation)
    {
        this.logging.rotation = rotation;
    }

    public String getLoggingExpression()
    {
        return this.logging.getCronExpression();
    }

    public long getSoftwareMajor()
    {
        return this.software.major;
    }

    public void setSoftwareMajor(long major)
    {
        this.software.major = major;
    }

    public long getSoftwareMinor()
    {
        return this.software.minor;
    }

    public void setSoftwareMinor(long minor)
    {
        this.software.minor = minor;
    }

    public long getSoftwareBuild()
    {
        return this.software.build;
    }

    public void setSoftwareBuild(long build)
    {
        this.software.build = build;
    }

    public String getSoftwareVersion()
    {
        return this.software.major + "." + this.software.minor + "." + this.software.build;
    }

    public String getSoftwareKernel()
    {
        return this.software.kernel;
    }

    public void setSoftwareKernel(String kernel)
    {
        this.software.kernel = kernel;
    }

    public String getSoftwareLicense()
    {
        return this.software.license;
    }

    public void setSoftwareLicense(String license)
    {
        this.software.license = license;
    }

    public List<CampaignShort> getCampaignsList()
    {
        return this.campaigns;
    }

    public void addCampaign(long id, String name, Date lastModified)
    {
        this.campaigns.add(new CampaignShort(id, name, lastModified));
    }

    public void addCampaign(CampaignShort campShort)
    {
        this.campaigns.add(campShort);
    }

    public void setCampaignsList(List<CampaignShort> campaigns)
    {
        this.campaigns = campaigns;
    }

    public List<ConfigProperty> getConfigProperties()
    {
        return configProperties;
    }

    public void setConfigProperties(List<ConfigProperty> configProperties)
    {
        this.configProperties = configProperties;
    }

    public void addProperty(ConfigProperty property)
    {
        this.configProperties.add(property);
    }

    public BluetoothConfig getBluetooth()
    {
        return bluetooth;
    }

    public void setBluetooth(BluetoothConfig bluetooth)
    {
        this.bluetooth = bluetooth;
    }

    public Date getLastSeen()
    {
        return lastSeen;
    }

    public void setLastSeen(Date lastSeen)
    {
        this.lastSeen = lastSeen;
    }

    public Integer getChannel()
    {
        return channel;
    }

    public void setChannel(Integer channel)
    {
        this.channel = channel;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException
    {
        return new Config(this);
    }
}
