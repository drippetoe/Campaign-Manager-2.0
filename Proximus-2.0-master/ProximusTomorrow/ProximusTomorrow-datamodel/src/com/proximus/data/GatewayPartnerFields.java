/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author dshaw
 */
@Entity
@Table(name = "gateway_partner_fields")
public class GatewayPartnerFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;
    @ManyToOne
    private Company company;
    @ManyToOne
    private Campaign campaign;
    @Column(name = "partner")
    private String partnerName;
    @Column(name = "partner_login")
    private String partnerLogin;
    @Column(name = "partner_password")
    private String partnerPassword;
    @Size(max = 1024)
    @Column(name = "url", length=1024)
    protected String url;
    @Size(max = 1024)
    @Column(name = "key1", length=1024)
    protected String key1;
    @Size(max = 1024)
    @Column(name = "key2", length=1024)
    protected String key2;
    @Size(max = 1024)
    @Column(name = "key3", length=1024)
    protected String key3;
    @Size(max = 1024)
    @Column(name = "key4", length=1024)
    protected String key4;
    @Size(max = 1024)
    @Column(name = "key5", length=1024)
    protected String key5;

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getKey5() {
        return key5;
    }

    public void setKey5(String key5) {
        this.key5 = key5;
    }

    public String getKey1() {
        return key1;
    }

    public void setKey1(String key1) {
        this.key1 = key1;
    }

    public String getKey2() {
        return key2;
    }

    public void setKey2(String key2) {
        this.key2 = key2;
    }

    public String getKey3() {
        return key3;
    }

    public void setKey3(String key3) {
        this.key3 = key3;
    }

    public String getKey4() {
        return key4;
    }

    public void setKey4(String key4) {
        this.key4 = key4;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getPartnerLogin() {
        return partnerLogin;
    }

    public void setPartnerLogin(String partnerLogin) {
        this.partnerLogin = partnerLogin;
    }

    public String getPartnerPassword() {
        return partnerPassword;
    }

    public void setPartnerPassword(String partnerPassword) {
        this.partnerPassword = partnerPassword;
    }
}
