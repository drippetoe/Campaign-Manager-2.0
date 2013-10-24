/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.data.config;

import javax.xml.bind.annotation.XmlAttribute;

/**
 *
 * @author ejohansson
 */
public final class Authentication
{
    @XmlAttribute(name = "token")
    public String token = "";
}
