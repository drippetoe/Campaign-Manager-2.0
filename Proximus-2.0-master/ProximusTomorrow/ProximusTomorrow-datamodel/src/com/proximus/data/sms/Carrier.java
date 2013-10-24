/*
 * Copyright 2010-2012, Proximus Mobility, LLC.
 */
package com.proximus.data.sms;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Wireless Carrier List from Kaze
 * @author dshaw
 */
@Entity
@Table(name = "carrier")
public class Carrier implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    protected Long id = 0L;
    @Size(max = 255)
    @Column(name = "name")
    protected String name;
    @Column(name = "supported")
    private boolean supported;

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

    public boolean isSupported()
    {
        return supported;
    }

    public void setSupported(boolean supported)
    {
        this.supported = supported;
    }

    @Override
    public String toString()
    {
        return this.name + "(" + this.id + ")";
    }
    
}
