package com.proximus.data;


import java.io.Serializable;
import java.util.Date;
//import javax.jdo.annotations.Index;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.DefaultValue;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author dshaw
 */
@Entity
@Table(name = "software_release")
@XmlRootElement
public class SoftwareRelease implements Serializable
{

    public static final String PLATFORM_DREAMPLUG_8 = "DP8";
    public static final String PLATFORM_DREAMPLUG_9 = "DP9";
    public static final String PLATFORM_DREAMPLUG_10 = "DP10";
    public static final String PLATFORM_BLUEGIGA_AX4 = "BGAX4";
    public static final String [] PLATFORMS = {PLATFORM_DREAMPLUG_8,PLATFORM_DREAMPLUG_9,PLATFORM_DREAMPLUG_10,PLATFORM_BLUEGIGA_AX4};
    //@Index
    @ManyToOne
    private Device device;
    @Column(name = "major")
    @DefaultValue("0")
    private Long major = 0L;
    @Column(name = "minor")
    @DefaultValue("0")
    private Long minor = 0L;
    @Column(name = "build")
    @DefaultValue("0")
    private Long build = 0L;
    @Size(max = 255)
    @Column(name = "kernel")
    private String kernel;
    @Size(max = 1024)
    @Column(name = "path")
    private String path;
    @Size(max = 1024)
    @Column(name = "description")
    private String description;
    @Size(max = 255)
    //@Index
    @Column(name = "platform")
    private String platform;
    @Column(name = "release_date")
    @Temporal(TemporalType.DATE)
    private Date releaseDate;

    @Basic(optional = false)
    @Column(name = "single")
    protected Boolean single = true;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id = 0L;

    public SoftwareRelease()
    {
        releaseDate = new Date();
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public Long getBuild()
    {
        return build;
    }

    public void setBuild(Long build)
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

    public Long getMajor()
    {
        return major;
    }

    public void setMajor(Long major)
    {
        this.major = major;
    }

    public Long getMinor()
    {
        return minor;
    }

    public void setMinor(Long minor)
    {
        this.minor = minor;
    }

    public String getPlatform()
    {
        return platform;
    }

    public void setPlatform(String platform)
    {
        this.platform = platform;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Date getReleaseDate()
    {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate)
    {
        this.releaseDate = releaseDate;
    }

    public Device getDevice()
    {
        return device;
    }

    public void setDevice(Device device)
    {
        this.device = device;
    }

    public Boolean getSingle()
    {
        return single;
    }

    public void setSingle(Boolean single)
    {
        this.single = single;
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SoftwareRelease))
        {
            return false;
        }
        SoftwareRelease other = (SoftwareRelease) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "com.proximus.data.SoftwareVersion[ id=" + id + " ]";
    }
}
