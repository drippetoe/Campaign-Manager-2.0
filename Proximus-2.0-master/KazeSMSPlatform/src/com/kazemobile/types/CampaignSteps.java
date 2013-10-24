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
@XmlRootElement(name="campaignSteps")
@XmlAccessorType(XmlAccessType.FIELD)
public class CampaignSteps
{
   @XmlElement(name="campaignStep")
    private List<CampaignStep> steps;

    public CampaignSteps()
    {
       steps = new ArrayList<CampaignStep>();
    }
   
    public List<CampaignStep> getSteps()
    {
        return steps;
    }

    public void setEntries(List<CampaignStep> steps)
    {
        this.steps = steps;
    }
   
   
   
   
}
