/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kazemobile.types;

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
@XmlRootElement(name="distributionListEntryExts")
@XmlAccessorType(XmlAccessType.FIELD)
public class DistributionListEntryExts
{
   @XmlElement(name="distributionListEntryExt")
    private List<DistributionListEntryExt> entries;

    public DistributionListEntryExts()
    {
       entries = new ArrayList<DistributionListEntryExt>();
    }
   
    public List<DistributionListEntryExt> getEntries()
    {
        return entries;
    }

    public void setEntries(List<DistributionListEntryExt> entries)
    {
        this.entries = entries;
    }
   
   
   
   
}
