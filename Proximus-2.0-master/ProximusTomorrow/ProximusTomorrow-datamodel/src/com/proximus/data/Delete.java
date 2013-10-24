/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Gaxiola
 */
@XmlRootElement(name = "delete")
@XmlAccessorType(XmlAccessType.FIELD)
class Delete
{
    @XmlAttribute(name = "all")
    private boolean all = false;
    @XmlElement(name = "log")
    private List<Log> logs;

    public boolean isAll()
    {
        return all;
    }

    public void setAll(boolean all)
    {
        this.all = all;
    }

    public List<Log> getLogs()
    {
        return logs;
    }

    public void setLogs(List<Log> logs)
    {
        this.logs = logs;
    }
    
    
    
}
