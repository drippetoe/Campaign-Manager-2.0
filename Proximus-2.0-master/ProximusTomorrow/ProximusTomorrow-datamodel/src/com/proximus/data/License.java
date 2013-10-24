/*
 * Copyright 2010-2012, Proximus Mobility, LLC.
 *
 */
package com.proximus.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * License is a text field containing single-character license attributes Adding
 * a new licensable item requires new code, so having it exist in code does not
 * add complexity. This class's sole purpose is to contain and decode the
 * license
 *
 * @author dshaw
 */
@Entity
@Table(name = "license")
public class License implements Serializable {

    /**
     * Define new licensable features here. These are not privileges, but
     * individual products that we sell.
     */
    public static final String LICENSE_PROXIMITY = "P";
    public static final String LICENSE_GEOFENCE = "G";
    public static final String LICENSE_CONTENTGENERATOR = "C";
    public static final String LICENSE_NOTIFICATION = "N";
    public static final String LICENSE_ALERTS = "A";
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;
    @Column(name = "license_text", length = 2048, nullable = false)
    private String licenseText;
    @ManyToMany(mappedBy = "licenses")
    private List<Role> roles;

    public License() {
        id = 0L;
        if (licenseText == null) {
            licenseText = "";
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLicenseText() {
        return licenseText;
    }

    public void setLicenseText(String licenseText) {
        this.licenseText = licenseText;
    }

    /**
     * @return true if this license contains Proximity
     */
    public boolean hasProximity() {
        return this.hasLicense(LICENSE_PROXIMITY);
    }

    /**
     * @return true if this license contains Geofencing
     */
    public boolean hasGeofence() {
        return this.hasLicense(LICENSE_GEOFENCE);
    }

    public boolean hasContentGenerator() {
        return this.hasLicense(LICENSE_CONTENTGENERATOR);
    }

    public boolean hasPubNubNotification() {
        return this.hasLicense(LICENSE_NOTIFICATION);
    }
    
    
    public boolean hasAlerts(){
        return this.hasLicense(LICENSE_ALERTS);
    }

    /**
     *
     * @param license license feature to check. Licenses defined in License.java
     * @return true if this license contains the license name passed
     */
    public boolean hasLicense(String licenseName) {
        return this.licenseText.contains(licenseName);
    }

    public void addLicense(String license) {
        ArrayList<String> licenseList = new ArrayList<String>(Arrays.asList(this.licenseText.split("")));
        if (!licenseList.contains(license)) {
            licenseList.add(license);
        }
        Collections.sort(licenseList);

        StringBuilder sb = new StringBuilder();
        for (String lItem : licenseList) {
            sb.append(lItem);
        }

        this.licenseText = sb.toString();
    }

    public static void main(String[] args) {
        License l = new License();

        System.out.println("\nGEO: " + l.hasGeofence());
        System.out.println("PRX: " + l.hasProximity());

        l.addLicense(LICENSE_PROXIMITY);

        System.out.println("\nGEO: " + l.hasGeofence());
        System.out.println("PRX: " + l.hasProximity());

        l.addLicense(LICENSE_GEOFENCE);

        System.out.println("\nGEO: " + l.hasGeofence());
        System.out.println("PRX: " + l.hasProximity());
    }

    public List<Role> getRoles() {
        if (roles == null) {
            roles = new ArrayList<Role>();
        }
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Role role) {
        getRoles().add(role);
    }

    public void deleteRole(Role role) {
        if (getRoles().contains(role)) {
            getRoles().remove(role);
        }
    }

    @Override
    public String toString() {
        return this.licenseText;
    }
}
