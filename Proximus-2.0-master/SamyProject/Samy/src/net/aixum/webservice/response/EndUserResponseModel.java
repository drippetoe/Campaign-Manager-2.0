/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice.response;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class EndUserResponseModel extends BaseResponseModel {

    @SerializedName("Id")    
    private String id;//Guid
    @SerializedName("Name")
    private String name;
    @SerializedName("Mobile")
    private String mobile;
    @SerializedName("Email")
    private String email;
    @SerializedName("PushToken")
    private String pushToken;
    @SerializedName("LanguageCode")
    private String languageCode;
    @SerializedName("RegionId")
    private int regionId;

    public EndUserResponseModel(String id, String name, String mobile, String email, String pushToken, String languageCode, int regionId, boolean success, String message) {
        super(success, message);
        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.pushToken = pushToken;
        this.languageCode = languageCode;
        this.regionId = regionId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    @Override
    public String toString() {
        return "EndUserResponseModel{" + "id=" + id + ", name=" + name + ", mobile=" + mobile + ", email=" + email + ", pushToken=" + pushToken + ", languageCode=" + languageCode + ", regionId=" + regionId + '}';
    }
    
    
}
