/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.data.config;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

    /**
    *
    * @author Gaxiola
    */
    @XmlRootElement(name = "campaign")
    @XmlAccessorType(XmlAccessType.FIELD)
    public class CampaignShort
    {
        @XmlAttribute(name = "id")
        private long id;
        @XmlAttribute(name = "name")
        private String name;
        @XmlAttribute(name = "lastModified")
        private Date lastModified;
        
        public CampaignShort()
        {
        }
        
        public CampaignShort(long id, String name, Date lastModified)
        {
            this.id = id;
            this.name = name;
            this.lastModified = lastModified;
        }
    }
    