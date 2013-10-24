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
@XmlRootElement(name = "webInteractionResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class WebInteractionResponse extends BaseResponse {

    public WebInteractionResponse() {
    }
}
