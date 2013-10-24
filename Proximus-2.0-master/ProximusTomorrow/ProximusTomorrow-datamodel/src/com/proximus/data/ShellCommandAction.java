/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Gilberto Gaxiola
 */
@Entity
@DiscriminatorValue("GENERIC")
@Table(name = "shell_command_action")
@XmlRootElement(name = "shellcommand")
@XmlAccessorType(XmlAccessType.FIELD)
public class ShellCommandAction
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    @XmlAttribute(name = "id")
    private Long id;
    @Size(max = 255)
    @Column(name = "command")
    @XmlAttribute(name = "command")
    private String command;
    @Size(max = 255)
    @Column(name = "parameters")
    @XmlAttribute(name = "parameters")
    private String parameters;
    @Basic(optional = false)
    @Column(name = "completed")
    private boolean completed;
    @Column(name = "device_id")
    private Long device_id;
    

    public ShellCommandAction()
    {
        id = 0L;
        completed = false;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getCommand()
    {
        return command;
    }

    public void setCommand(String command)
    {
        this.command = command;
    }

    public String getParameters()
    {
        return parameters;
    }

    public void setParameters(String parameters)
    {
        this.parameters = parameters;
    }

    public boolean isCompleted()
    {
        return completed;
    }

    public void setCompleted(boolean completed)
    {
        this.completed = completed;
    }

    public Long getDevice_id()
    {
        return device_id;
    }

    public void setDevice_id(Long device_id)
    {
        this.device_id = device_id;
    }
    
    
    
    
}
