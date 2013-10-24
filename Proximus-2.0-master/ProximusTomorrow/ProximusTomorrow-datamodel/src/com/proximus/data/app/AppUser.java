/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.data.app;

import com.proximus.data.sms.Category;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.jdo.annotations.Index;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 *
 * @author ronald
 */
@Entity
@Index(members = {"uniqueIdentifier", "msisdn", "macAddress"}, unique = "true")
@Table(name = "app_user")
public class AppUser implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;
    @NotNull
    @Column(name = "uuid")
    private String uniqueIdentifier;
    @Column(name = "msisdn")
    private String msisdn;
    @Column(name = "mac_address")
    private String macAddress;
    @Column(name = "mobile_country_code")
    private String mobileCountryCode;
    @Column(name = "mobile_network_code")
    private String mobileNetworkCode;
    @Column(name = "user_agent")
    private String userAgent;
    @Column(name = "device_platform")
    private String devicePlatform;
    @Column(name = "device_version")
    private String deviceVersion;
    @ManyToMany
    @JoinTable(name = "app_user_category", joinColumns =
            @JoinColumn(name = "app_user_id", referencedColumnName = "id"), inverseJoinColumns =
            @JoinColumn(name = "category_id", referencedColumnName = "id"))
    private List<Category> categories;

    public AppUser() {
        this.id = 0L;
    }

    public AppUser(String uniqueIdentifier, String msisdn, String macAddress, String mobileCountryCode, String mobileNetworkCode,
            String userAgent, String devicePlatform, String deviceVersion, List<Category> categories) {
        this.id = 0L;
        this.uniqueIdentifier = uniqueIdentifier;
        this.msisdn = msisdn;
        this.macAddress = macAddress;
        this.mobileCountryCode = mobileCountryCode;
        this.mobileNetworkCode = mobileNetworkCode;
        this.userAgent = userAgent;
        this.devicePlatform = devicePlatform;
        this.deviceVersion = deviceVersion;
        this.categories = categories;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    public void setUniqueIdentifier(String uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getMobileCountryCode() {
        return mobileCountryCode;
    }

    public void setMobileCountryCode(String mobileCountryCode) {
        this.mobileCountryCode = mobileCountryCode;
    }

    public String getMobileNetworkCode() {
        return mobileNetworkCode;
    }

    public void setMobileNetworkCode(String mobileNetworkCode) {
        this.mobileNetworkCode = mobileNetworkCode;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getDevicePlatform() {
        return devicePlatform;
    }

    public void setDevicePlatform(String devicePlatform) {
        this.devicePlatform = devicePlatform;
    }

    public String getDeviceVersion() {
        return deviceVersion;
    }

    public void setDeviceVersion(String deviceVersion) {
        this.deviceVersion = deviceVersion;
    }

    public List<Category> getCategories() {
        if (this.categories == null) {
            this.categories = new ArrayList<Category>();
        }
        return this.categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public void addCategory(Category category) {
        getCategories().add(category);
    }

    public void removeCategory(Category category) {
        if (getCategories().contains(category)) {
            getCategories().remove(category);
        }
    }

    /*Only do comparison on msisdn because some situations 
     * can occur that can make mac address or UUID be different or fail validation
     * Eg. Phone connected before with wifi and now phone is just on 3/4G
     * Eg. User rooted device to delete file or did a system restore
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.msisdn != null ? this.msisdn.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AppUser other = (AppUser) obj;
        if ((this.msisdn == null) ? (other.msisdn != null) : !this.msisdn.equals(other.msisdn)) {
            return false;
        }
        return true;
    }
}
