/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.response.example;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ronald
 */
@XmlRootElement(name = "exampleErrorResponseObject")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExampleErrorResponseObject {

    @XmlAttribute(name = "errorCode")
    private Long errorCode;
    @XmlAttribute(name = "reason")
    private String reason;

    public ExampleErrorResponseObject(Long errorCode, String reason) {
        this.errorCode = errorCode;
        this.reason = reason;
    }

    public Long getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Long errorCode) {
        this.errorCode = errorCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
