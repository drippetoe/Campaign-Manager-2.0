/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Eric Johansson
 */
@XmlRootElement(name = "BluetoothFiles")
@XmlAccessorType(XmlAccessType.FIELD)
public class BluetoothFiles {

    @XmlElement(name = "file")
    private List<BluetoothFile> files = new ArrayList<BluetoothFile>(); 

    public BluetoothFiles() {
    }

    public List<BluetoothFile> getFileList() {
        return files;
    }

    public void setFileList(List<BluetoothFile> files) {
        this.files = files;
    }
    
    public void addFile(BluetoothFile file){
        this.files.add(file);
    }
    
    public void removeFile(BluetoothFile file){
        this.files.remove(file);
    }
    
}
