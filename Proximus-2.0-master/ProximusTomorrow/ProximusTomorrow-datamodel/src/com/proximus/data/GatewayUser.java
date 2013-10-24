package com.proximus.data;

import java.io.Serializable;
import java.util.Date;
import javax.jdo.annotations.Index;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author dshaw
 */
@Entity
@Table(name = "gateway_user",uniqueConstraints =
{
    @UniqueConstraint(columnNames =
    {
        "mac_address", "company_id"
    })
})
public class GatewayUser implements Serializable {
    @Basic(optional = false)
    @Size(min = 1, max = 17)
    @Index(unique = "true")
    @Column(name = "mac_address")
    private String macAddress;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;
    
    @ManyToOne
    private Company company;
    
    @ManyToOne
    private WifiRegistration wifiRegistration;
    
    @Column(name = "registration_time")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date registrationTime = new Date();
    
    @Size(max = 1024)
    @Column(name = "value1", length=1024)
    protected String value1;
    @Size(max = 1024)
    @Column(name = "value2", length=1024)
    protected String value2;
    @Size(max = 1024)
    @Column(name = "value3", length=1024)
    protected String value3;
    @Size(max = 1024)
    @Column(name = "value4", length=1024)
    protected String value4;
    @Size(max = 1024)
    @Column(name = "value5", length=1024)
    protected String value5;

    public GatewayUser() {
        this.id = 0L;
    }    
    
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
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
        this.macAddress = macAddress.toUpperCase().replaceAll(":", "");
    }

    public Date getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(Date registrationTime) {
        this.registrationTime = registrationTime;
    }

    public WifiRegistration getWifiRegistration() {
        return wifiRegistration;
    }

    public void setWifiRegistration(WifiRegistration wifiRegistration) {
        this.wifiRegistration = wifiRegistration;
    }

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    public String getValue3() {
        return value3;
    }

    public void setValue3(String value3) {
        this.value3 = value3;
    }

    public String getValue4() {
        return value4;
    }

    public void setValue4(String value4) {
        this.value4 = value4;
    }

    public String getValue5() {
        return value5;
    }

    public void setValue5(String value5) {
        this.value5 = value5;
    }
}
