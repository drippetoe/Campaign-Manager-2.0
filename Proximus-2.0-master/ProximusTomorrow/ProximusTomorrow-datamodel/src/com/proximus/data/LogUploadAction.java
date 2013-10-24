/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Gaxiola
 */
@XmlRootElement(name = "upload")
@XmlAccessorType(XmlAccessType.FIELD)
public class LogUploadAction
{
    @XmlAttribute(name = "all")
    private boolean all = false;
    @XmlAttribute(name = "count")
    private int count = 0;

    public boolean isAll()
    {
        return all;
    }

    public void setAll(boolean all)
    {
        this.all = all;
    }

    public int getCount()
    {
        return count;
    }

    public void setCount(int count)
    {
        this.count = count;
    }    
}
