/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.sms;

import com.proximus.data.Brand;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author ronald
 */
@Entity
@XmlRootElement(name = "retailer")
@XmlAccessorType(XmlAccessType.FIELD)
@Table(name = "retailer")
public class Retailer implements Serializable, Comparable {

    private static final long serialVersionUID = 1L;
    @XmlTransient
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    protected Long id;
    @Size(max = 255)
    @Column(name = "name")
    protected String name;
    @XmlTransient
    @ManyToOne
    private Brand brand;
    @XmlTransient
    @ManyToMany(mappedBy = "retailers")
    private List<Property> properties;

    public Retailer() {
        this.id = 0L;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public void addProperty(Property property) {
        if (this.properties == null) {
            this.properties = new ArrayList<Property>();
        }
        this.properties.add(property);
    }

    public void clearProperties() {
        this.properties = new ArrayList<Property>();
    }

    @Override
    public String toString() {
        return this.name;
    }

    /**
     * * return zero if id for both is same return negative if current id is
     * less than specified one return positive if current id is greater than
     * specified one
     *
     * @param t
     * @return
     */
    @Override
    public int compareTo(Object t) {
        if (t instanceof Retailer) {
            Retailer comp = (Retailer) t;
            return this.getId().compareTo(comp.getId());
        } else {
            return 1;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        final Retailer other = (Retailer) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) {

        // test Comparable
        Retailer one = new Retailer();
        one.setId(1L);

        Retailer two = new Retailer();
        two.setId(2L);

        Retailer three = new Retailer();
        three.setId(3L);

        System.out.println(two.compareTo(one));
        System.out.println(two.compareTo(three));
        System.out.println(two.compareTo(two));
        System.out.println(two.compareTo(null));
    }
}
