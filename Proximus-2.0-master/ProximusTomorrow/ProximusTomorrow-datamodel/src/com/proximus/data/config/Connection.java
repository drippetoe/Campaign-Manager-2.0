/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.data.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ejohansson
 */
@XmlRootElement(name = "connection")
@XmlAccessorType(XmlAccessType.FIELD)
public final class Connection
{
    @XmlAttribute(name = "reconnect-interval")
    public Long reconnectInterval = 30000L;
    @XmlAttribute(name = "keep-alive")
    public Long keepAlive = 20000L;
}
