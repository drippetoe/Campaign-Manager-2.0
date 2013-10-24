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
 * @author Gilberto Gaxiola
 */
@Entity
@DiscriminatorValue("GENERIC")
@Table(name = "contact")
public class Contact implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;
    @Size(max = 255)
    @Column(name = "description")
    private String description;
    @Size(max = 255)
    @Basic(optional = false)
    @NotNull
    @Column(name = "email", unique = true)
    private String email;
    @Size(max = 255)
    @Column(name = "address_one")
    private String address1;
    @Size(max = 255)
    @Column(name = "address_two")
    private String address2;
    @Size(max = 255)
    @Column(name = "city")
    private String city;
    @Size(max = 255)
    @Column(name = "state_province")
    private String stateProvince;
    @Size(max = 255)
    @Column(name = "country")
    private String country;
    @Size(max = 255)
    @Column(name = "zipcode")
    private String zipcode;
    @Size(max = 255)
    @Column(name = "phone")
    private String phone;
    @Size(max = 255)
    @Column(name = "lat")
    private String lat;
    @Size(max = 255)
    @Column(name = "lon")
    private String lon;
    @OneToMany(mappedBy = "contact")
    private List<Device> devices;
    @OneToOne
    private Company company;

    public Contact() {
        this.id = 0L;
        devices = new ArrayList<Device>();
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public void addDevice(Device d) {
        this.devices.add(d);
    }

    public void removeDevice(Device d) {
        this.devices.remove(d);
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        if (address1 != null && address1.length() > 254) {
            this.address1 = address1.substring(0, 254);
        } else {
            this.address1 = address1;
        }
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        if (address2 != null && address2.length() > 254) {
            this.address2 = address2.substring(0, 254);
        } else {
            this.address2 = address2;
        }
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        if (country != null && country.length() > 254) {
            this.country = country.substring(0, 254);
        } else {
            this.country = country;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email != null && email.length() > 254) {
            this.email = email.substring(0, 254);
        } else {
            this.email = email;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        if (lat != null && lat.length() > 254) {
            this.lat = lat.substring(0, 254);
        } else {
            this.lat = lat;
        }
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        if (lon != null && lon.length() > 254) {
            this.lon = lon.substring(0, 254);
        } else {
            this.lon = lon;
        }
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (phone != null && phone.length() > 254) {
            this.phone = phone.substring(0, 254);
        } else {
            this.phone = phone;
        }
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(String stateProvince) {
        if (stateProvince != null && stateProvince.length() > 254) {
            this.stateProvince = stateProvince.substring(0, 254);
        } else {
            this.stateProvince = stateProvince;
        }
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        if (zipcode != null && zipcode.length() > 254) {
            this.zipcode = zipcode.substring(0, 254);
        } else {
            this.zipcode = zipcode;
        }
    }

    @Override
    public String toString() {
        String result = "";
        result += "Contact Email: " + this.email + "\n";
        return result;
    }
    
    @Override
    public boolean equals(Object c) {
        if(c instanceof Contact) {
            Contact cont = (Contact)c;
            if(this.id != 0) {
                return this.id == cont.getId();
            } else {
                if(this.email != null && this.email.equals(cont.getEmail())) {
                    if(this.phone != null && this.phone.equals(cont.getPhone())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
}
