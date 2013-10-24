/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.sms;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Gilberto Gaxiola
 */
@Entity
@Table(name = "country")
public class Country implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    protected Long id;
    @Size(max = 2)
    @Column(name = "code")
    protected String code;
    @Size(max = 255)
    @Column(name = "name")
    protected String name;
  
    
    public Country() {
        id = 0L;
        
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    
    @Override
    public boolean equals(Object o) {
        if(o instanceof Country) {
            Country c = (Country)o;
            if(this.id.equals(c.getId()) && this.id != 0) {
                return true;
            }  
        }
        return false;
    }
    
    @Override
    public String toString() {
        return name + " ("+code+")";
    }
    
    

   
    
}

