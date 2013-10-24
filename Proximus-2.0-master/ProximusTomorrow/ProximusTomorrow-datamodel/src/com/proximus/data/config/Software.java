package com.proximus.data.config;

import javax.xml.bind.annotation.XmlAttribute;

/**
 *
 * @author ejohansson
 */
public final class Software
{

    @XmlAttribute(name = "major")
    public long major = 0;
    @XmlAttribute(name = "minor")
    public long minor = 0;
    @XmlAttribute(name = "build")
    public long build = 0;
    @XmlAttribute(name = "kernel")
    public String kernel = "unknown";
    @XmlAttribute(name = "license")
    public String license = "unknown-license";
}
