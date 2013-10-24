/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.response.example;

import com.proximus.data.response.BaseResponse;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ronald
 */
@XmlRootElement(name = "exampleResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExampleResponseObject  {

    @XmlAttribute(name = "id")
    private Long id;
    @XmlAttribute(name = "name")
    private String name;
    @XmlAttribute(name = "lastModified")
    private Date lastModified;
    @XmlElementWrapper(name = "cars")
    @XmlElement(name = "car")
    private List<String> elements = new LinkedList<String>();

    public ExampleResponseObject(Long id, String name) {
        this.id = id;
        this.name = name;
        this.lastModified = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public List<String> getElements() {
        return elements;
    }

    public void setElements(List<String> elements) {
        this.elements = elements;
    }
    
}
