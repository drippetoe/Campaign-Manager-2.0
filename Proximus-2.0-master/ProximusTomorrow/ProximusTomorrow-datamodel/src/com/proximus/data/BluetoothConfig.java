/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Eric Johansson
 */
@Entity
@DiscriminatorValue("GENERIC")
@Table(name = "bluetooth_config")
@XmlRootElement(name = "bluetooth")
@XmlAccessorType(XmlAccessType.FIELD)
public class BluetoothConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    @XmlAttribute(name = "id")
    private Long id;

  
    @Column(name = "resend_interval")
    @XmlAttribute(name = "resend-interval")
    private long resendInterval;

    public BluetoothConfig() {
        id = 0L;
    }

    public BluetoothConfig(long resendInterval) {
        this.resendInterval = resendInterval;
    }

    public long getResendInterval() {
        return resendInterval;
    }

    public void setResendInterval(long resendInterval) {
        this.resendInterval = resendInterval;
    }
    
      public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }
    
    
    
}
