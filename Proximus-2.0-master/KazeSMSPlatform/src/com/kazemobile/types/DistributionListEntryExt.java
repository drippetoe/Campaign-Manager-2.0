/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kazemobile.types;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rwalker
 */
@XmlRootElement(name="distributionListEntryExt")
@XmlAccessorType(XmlAccessType.FIELD)
public class DistributionListEntryExt implements Serializable {
    
    private Long distributionListEntryId;
    private int distributionListId;
    private String listStatus;
    private String firstname;
    private String lastname;
    private String mobile;
    private int carrierId;
    private String carrier;
    private String email;
    private String twitterid;
    
    public DistributionListEntryExt() {
        distributionListEntryId = new Long(0L);
    }
    
    public Long getDistributionListEntryId() {
        return distributionListEntryId;
    }

    public void setDistributionListEntryId(Long distributionListEntryId) {
        this.distributionListEntryId = distributionListEntryId;
    }

    public int getDistributionListId() {
        return distributionListId;
    }

    public void setDistributionListId(int distributionListId) {
        this.distributionListId = distributionListId;
    }

    public String getListStatus() {
        return listStatus;
    }

    public void setListStatus(String listStatus) {
        this.listStatus = listStatus;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(int carrierId) {
        this.carrierId = carrierId;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTwitterid() {
        return twitterid;
    }

    public void setTwitterid(String twitterid) {
        this.twitterid = twitterid;
    }
}

