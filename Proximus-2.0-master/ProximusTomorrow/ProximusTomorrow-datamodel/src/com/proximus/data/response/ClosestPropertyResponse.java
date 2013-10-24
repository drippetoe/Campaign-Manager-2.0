/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ronald
 */
@XmlRootElement(name = "closestPropertyResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ClosestPropertyResponse extends BaseResponse {

    private String closestPropertyWebHash;
    private Long closestPropertyId;

    public String getClosestPropertyWebHash() {
        return closestPropertyWebHash;
    }

    public void setClosestPropertyWebHash(String closestPropertyWebHash) {
        this.closestPropertyWebHash = closestPropertyWebHash;
    }

    public Long getClosestPropertyId() {
        return closestPropertyId;
    }

    public void setClosestPropertyId(Long closestPropertyId) {
        this.closestPropertyId = closestPropertyId;
    }
    
}
