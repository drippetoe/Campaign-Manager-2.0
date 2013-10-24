package com.proximus.data;

import com.proximus.data.config.Config;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.jdo.annotations.Index;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.DefaultValue;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Gilberto Gaxiola
 */
@Entity
@DiscriminatorValue("GENERIC")
@Table(name = "device", uniqueConstraints =
{
    @UniqueConstraint(columnNames =
    {
        "mac_address"
    }),
    @UniqueConstraint(columnNames =
    {
        "mac_address", "token", "serial_number"
    })
})
public class Device implements Serializable
{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;
    @ManyToOne
    private Company company;
    @ManyToOne
    private Contact contact;
    @Size(max = 255)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @Size(min = 1, max = 255)
    @Column(name = "serial_number")
    private String serialNumber;
    @Basic(optional = false)
    @Size(min = 1, max = 17)
    @Index(unique = "true")
    @Column(name = "mac_address")
    private String macAddress;
    @Column(name = "wifi_channel")
    private Integer wifiChannel;
    @XmlTransient
    @Column(name="allow_wifi_override")
    private Boolean allowWifiClientOverride = false;
    @Size(max = 46)
    @Column(name = "last_ip_address")
    private String lastIpAddress;
    @Size(max = 255)
    @Column(name = "token")
    private String token;
    @Column(name = "reconnect_interval")
    @DefaultValue("20000")
    private Long reconnectInterval;
    @Column(name = "keep_alive")
    private Long keepAlive;
    @Column(name = "rotation")
    private Long rotation;
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
    @Size(max = 255)
    @Column(name = "license")
    private String license;
    @Size(max = 255)
    @Column(name = "platform")
    private String platform;
    @XmlTransient
    @Column(name = "active")
    private Boolean active;
    @Transient
    private List<Campaign> campaigns;
    @Column(name = "last_seen")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date lastSeen;
    @Column(name = "registration_date")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date registrationDate;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "device")
    private List<ConfigProperty> configProperties;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "device")
    private List<WifiLog> reports;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "device")
    private List<SoftwareRelease> softwareRelease;
    @ManyToOne
    @XmlTransient
    private Tag tag;
    @OneToMany(mappedBy = "device")
    private List<BluetoothSend> bluetoothDataPoints;

    public Device()
    {
        id = 0L;
        keepAlive = 20000L;
        reconnectInterval = 300000L;
        build = 0;
        major = 0;
        minor = 0;
        rotation = 3600000L;
        lastSeen = null;
        registrationDate = null;
        token = null;
        wifiChannel = 9;
        active = true;
    }

    public Tag getTag()
    {
        return tag;
    }

    public void setTag(Tag tag)
    {
        this.tag = tag;
    }

    public Device(String serialNumber, String macAddress)
    {
        this();
        this.serialNumber = serialNumber;
        this.macAddress = macAddress.toUpperCase().replaceAll(":", "");

    }

    public List<Campaign> getCampaigns()
    {
        if ( campaigns == null )
        {
            campaigns = new ArrayList<Campaign>();
        }
        if ( this.tag != null && this.campaigns.isEmpty() )
        {
            campaigns = this.tag.getCampaigns();
        }
        return campaigns;
    }
    
    /**
     * The tags returned here map to CSS styles, so bear that in mind if you change them
     * @return 
     */
    public String getConnectStatus()
    {
        long lastOnlineMS = 0L;
        if ( this.lastSeen != null )
            lastOnlineMS = this.lastSeen.getTime();
                    
        
        long msSinceSeen = System.currentTimeMillis() - lastOnlineMS;
        // say this is 2 mins
        
        long intervalMS = this.getReconnectInterval();
        // say this is 1 min
        
        // we want to flag it as green if it's been seen within 4x the interval
        // we flag as yellow if it's been 4x to 12x the interval
        // we flag as red if greater
        if ( (intervalMS * 4 ) >= msSinceSeen )
        {
            return "Current";
        }
        else if ( (intervalMS * 12 ) >= msSinceSeen )
        {
            return "Late";
        }
        else
        {
            return "Offline";
        }
    }
    
    public static String formatTimestampForWeb(Date date)
    {
        final String TIMESTAMP_FMT = "dd-MMMM-yyyy HH:mm:ss";
        return new SimpleDateFormat(TIMESTAMP_FMT).format(date);
    }
    
    public String getLastSeenFmt()
    {
        if ( lastSeen == null )
            return "Never";
        return formatTimestampForWeb(lastSeen);
    }

    public void setCampaigns(List<Campaign> campaigns)
    {
        this.campaigns = campaigns;
    }

    public void addCampaign(Campaign camp)
    {
        this.campaigns.add(camp);
    }

    public void dropCampaign(Campaign toDrop)
    {
        this.campaigns.remove(toDrop);
    }

    public String getToken()
    {
        return token;
    }
    
    public Boolean hasToken()
    {
        return (( token != null ) && !token.isEmpty());
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPlatform()
    {
        return platform;
    }

    public void setPlatform(String platform)
    {
        this.platform = platform;
    }

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
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

    public Company getCompany()
    {
        return company;
    }

    public void setCompany(Company company)
    {
        this.company = company;
    }

    public Contact getContact()
    {
        return contact;
    }

    public void setContact(Contact contact)
    {
        this.contact = contact;
    }

    public Integer getWifiChannel()
    {
        return wifiChannel;
    }

    public void setWifiChannel(Integer wifiChannel)
    {
        this.wifiChannel = wifiChannel;
    }

    public Boolean getAllowWifiClientOverride() {
        return allowWifiClientOverride;
    }

    public void setAllowWifiClientOverride(Boolean allowWifiClientOverride) {
        this.allowWifiClientOverride = allowWifiClientOverride;
    }

    public String getLastIpAddress()
    {
        return lastIpAddress;
    }

    public void setLastIpAddress(String lastIpAddress)
    {
        this.lastIpAddress = lastIpAddress;
    }

    public Long getKeepAlive()
    {
        return keepAlive;
    }

    public void setKeepAlive(Long keepAlive)
    {
        this.keepAlive = keepAlive;
    }

    public Long getReconnectInterval()
    {
        return reconnectInterval;
    }

    public void setReconnectInterval(Long reconnectInterval)
    {
        this.reconnectInterval = reconnectInterval;
    }

    public Long getRotation()
    {
        return rotation;
    }

    public void setRotation(Long rotation)
    {
        this.rotation = rotation;
    }

    public long getBuild()
    {
        return build;
    }

    public void setBuild(long build)
    {
        this.build = build;
    }

    public String getKernel()
    {
        return kernel;
    }

    public void setKernel(String kernel)
    {
        this.kernel = kernel;
    }

    public String getLicense()
    {
        return license;
    }

    public void setLicense(String license)
    {
        this.license = license;
    }

    public long getMajor()
    {
        return major;
    }

    public void setMajor(long major)
    {
        this.major = major;
    }

    public long getMinor()
    {
        return minor;
    }

    public void setMinor(long minor)
    {
        this.minor = minor;
    }

    public Date getLastSeen()
    {
        return lastSeen;
    }

    public void setLastSeen(Date lastSeen)
    {
        this.lastSeen = lastSeen;
    }

    public Boolean getActive()
    {
        return active;
    }

    public void setActive(Boolean active)
    {
        this.active = active;
    }

    public List<ConfigProperty> getConfigProperties()
    {
        return configProperties;
    }

    public void setConfigProperties(List<ConfigProperty> configProperties)
    {
        this.configProperties = configProperties;
    }

    public Config createConfig()
    {
        Config result = new Config();
        result.setAuthenticationToken(this.token);
        result.setKeepAlive(this.keepAlive);
        result.setLoggingRotation(this.rotation);
        result.setReconnectInterval(this.reconnectInterval);
        result.setSoftwareBuild(this.build);
        result.setSoftwareKernel(this.kernel);
        result.setSoftwareLicense(this.license);
        result.setSoftwareMajor(this.major);
        result.setSoftwareMinor(this.minor);
        result.setBluetooth(null);
        result.setCampaignsList(null);
        result.setLastSeen(this.lastSeen);
        result.setChannel(this.wifiChannel);
        result.setConfigProperties(this.configProperties);
        return result;
    }

    public void setFromConfig(Config c)
    {

        this.token = c.getAuthenticationToken();
        this.keepAlive = c.getKeepAlive();
        this.rotation = c.getLoggingRotation();
        this.reconnectInterval = c.getReconnectInterval();
        this.build = c.getSoftwareBuild();
        this.kernel = c.getSoftwareKernel();
        this.license = c.getSoftwareLicense();
        this.major = c.getSoftwareMajor();
        this.minor = c.getSoftwareMinor();
        this.lastSeen = c.getLastSeen();
        this.configProperties = c.getConfigProperties();
    }

    public Date getRegistrationDate()
    {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate)
    {
        this.registrationDate = registrationDate;
    }

    public List<WifiLog> getReports()
    {
        return reports;
    }

    public void setReports(List<WifiLog> reports)
    {
        this.reports = reports;
    }

//    @Override
//    public int hashCode()
//    {
//        int hash = 0;
//        hash += (id != null ? id.hashCode() : 0);
//        return hash;
//    }
//
//    @Override
//    public boolean equals(Object object)
//    {
//        if (!(object instanceof Device)) {
//            return false;
//        }
//        Device other = (Device) object;
//        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
//            return false;
//        }
//        return true;
//    }
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final Device other = (Device) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id)))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 37 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString()
    {
        String result = "" + macAddress + " | " + serialNumber;
        return result;
    }
}
