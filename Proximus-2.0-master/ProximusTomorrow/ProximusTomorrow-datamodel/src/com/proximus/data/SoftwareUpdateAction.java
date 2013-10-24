/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.data;

import java.io.File;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Represents an XML response software update action sent to the Client
 * 
 * @author dshaw
 */
@XmlRootElement(name = "softwareupdate")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class SoftwareUpdateAction {
    
    @XmlTransient
    private SoftwareRelease softwareRelease;

    @XmlAttribute(name="path")
    public String getPath() {   
        File f = new File(this.softwareRelease.getPath());
        String path = "/"+this.softwareRelease.getPlatform()+"/"+this.softwareRelease.getMajor()+"/"+this.softwareRelease.getMinor()+"/"+this.softwareRelease.getBuild()+"/"+f.getName();
        return path;
    }
    
    public void setSoftwareReleasePath(SoftwareRelease software) {
        this.softwareRelease = software;
    }
}
