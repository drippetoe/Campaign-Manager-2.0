/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.sms;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Gilberto Gaxiola
 */
@XmlRootElement(name = "keyword")
@XmlAccessorType(XmlAccessType.FIELD)
public class KeywordRequest {

    private String ShortCodeId;
    private String keyword;

    public String getShortCodeId() {
        return ShortCodeId;
    }

    public void setShortCodeId(String ShortCodeId) {
        this.ShortCodeId = ShortCodeId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
