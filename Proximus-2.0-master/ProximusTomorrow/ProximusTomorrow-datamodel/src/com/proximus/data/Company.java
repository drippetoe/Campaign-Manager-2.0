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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ronald
 */
@Entity
@DiscriminatorValue("GENERIC")
@Table(name = "company", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "name"
    })
})
@XmlRootElement
public class Company implements Serializable {

    public static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;
    @Size(max = 255)
    @Column(name = "name")
    private String name;
    @Size(max = 255)
    @Column(name = "salt")
    private String salt;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "company")
    private List<Device> devices;
    @ManyToMany(mappedBy = "companies")
    private List<User> users;
    @OneToOne
    private License license;
    @OneToOne
    private PricingModel pricingModel;
    @ManyToOne
    private Brand brand;

    public Company() {
        id = 0L;
        devices = new ArrayList<Device>();
        users = new ArrayList<User>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Company(String name) {
        id = 0L;
        this.name = name;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void addDevice(Device d) {
        devices.add(d);
    }

    public License getLicense() {
        return this.license;
    }

    public void setLicense(License license) {
        this.license = license;
    }

    public List<User> getUser() {
        return users;
    }

    public void addUser(User u) {
        users.add(u);
    }

    public void removeUser(User u) {
        users.remove(u);
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public PricingModel getPricingModel()
    {
        return pricingModel;
    }

    public void setPricingModel(PricingModel pricingModel)
    {
        this.pricingModel = pricingModel;
    }
    
    

    /**
     * FYI do not change this, it is used to select thee company in the UI
     *
     * @return
     */
    @Override
    public String toString() {
        return this.name;
    }

    public String logName() {
        return "[" + this.name + " (" + this.id + ")]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Company other = (Company) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}
