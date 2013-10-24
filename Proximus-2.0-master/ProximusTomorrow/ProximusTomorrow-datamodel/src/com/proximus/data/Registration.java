package com.proximus.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ejohansson
 */
@XmlRootElement(name = "registration")
@XmlAccessorType(XmlAccessType.FIELD)
public class Registration
{
    private String token;

    public Registration()
    {
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }
}
