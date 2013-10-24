/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.data.sms;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Angela Mercer
 */
/**<distributionListExts>
    * <distributionListExt>
        * <distributionListId>75</distributionListId>
        * <name>Proximus Demo Group</name>
        * <notes>Proximus Demo Group</notes>
    * </distributionListExt>
 * </distributionListExts>
 */ 
@XmlRootElement(name = "distributionListExt")
@XmlAccessorType(XmlAccessType.FIELD)
public class DistributionListResponse
{
    @XmlElement(name="distributionListId")
    private String distributionListId;
    
    @XmlElement (name = "name")
    private String name;
    
    @XmlElement(name="notes")
    private String notes;

    public String getDistributionListId()
    {
        return distributionListId;
    }

    public void setDistributionListId(String distributionListId)
    {
        this.distributionListId = distributionListId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getNotes()
    {
        return notes;
    }

    public void setNotes(String notes)
    {
        this.notes = notes;
    }
    
    
    
    
}
