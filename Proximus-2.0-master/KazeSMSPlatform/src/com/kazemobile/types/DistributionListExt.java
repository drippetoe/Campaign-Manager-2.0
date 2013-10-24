/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kazemobile.types;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rwalker
 */
@XmlRootElement
public class DistributionListExt implements Serializable {
    private Integer distributionListId;
    private String name;
    private String notes;
    
    public DistributionListExt() {
        distributionListId = new Integer(0);
    }
    
    public Integer getDistributionListId() {
        return distributionListId;
    }

    public void setDistributionListId(Integer distributionListId) {
        this.distributionListId = distributionListId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

