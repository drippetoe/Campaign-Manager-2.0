/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author ronald
 */
@Entity
@DiscriminatorValue("GENERIC")
@Table(name = "role")
public class Role implements Serializable {

    public static final String SUPER_USER = "Super User";
    
    //Proximity
    public static final String COMPANY_ADMIN = "Company Admin";
    public static final String COMPANY_VIEWER = "Company Viewer";
    
    //GeoFence
    public static final String BRAND_MANAGER = "Brand Manager";
    public static final String GEO_COMPANY_ADMIN = "Geo Company Admin";
    public static final String PROPERTY_MANAGER = "Property Manager";
    public static final String GEO_COMPANY_VIEWR = "Geo Company Viewer";
    public static final String GEO_COMPANY_ASSISTANT = "Geo Company Assistant";
    
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "role")
    private List<User> users;
    @Size(max = 255)
    @NotNull
    @Basic(optional = false)
    @Column(name = "role_name", unique = true)
    private String name;
    @ManyToMany(mappedBy = "roles")
    private List<Privilege> privileges;
    @Column(name = "priority")
    private Long priority;
    @ManyToMany
    @JoinTable(name = "role_license", joinColumns =
    @JoinColumn(name = "role_id", referencedColumnName = "id"), inverseJoinColumns =
    @JoinColumn(name = "license_id", referencedColumnName = "id"))
    private List<License> licenses;

    public Role() {
        id = 0L;
        privileges = new ArrayList<Privilege>();
        users = new ArrayList<User>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUsers() {
        return users;
    }

    public void addUser(User u) {
        this.users.add(u);
    }

    public void removeUser(User u) {
        this.users.remove(u);
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Privilege> getPrivileges() {
        return privileges;
    }

    public void addPrivilege(Privilege p) {
        this.privileges.add(p);
    }

    public void removePrivilege(Privilege p) {
        this.privileges.remove(p);
    }

    public void setPrivileges(List<Privilege> privileges) {
        this.privileges = privileges;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Role other = (Role) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public List<License> getLicenses() {
        if (licenses == null) {
            licenses = new ArrayList<License>();
        }
        return licenses;
    }

    public void setLicenses(List<License> licenses) {
        this.licenses = licenses;
    }

    public void addLicense(License l) {
        getLicenses().add(l);
    }

    public void removeLicense(License l) {
        if (getLicenses().contains(l)) {
            getLicenses().remove(l);
        }
    }
}
