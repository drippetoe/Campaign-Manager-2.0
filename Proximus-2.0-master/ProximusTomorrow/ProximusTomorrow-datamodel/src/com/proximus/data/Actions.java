/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;

/**
 *
 * @author Gaxiola
 */
@XmlRootElement(name = "actions")
@XmlAccessorType(XmlAccessType.FIELD)
public class Actions  {

    @XmlElementRefs({
        @XmlElementRef(name = "upload", type = LogUploadAction.class),
        @XmlElementRef(name = "softwareupdate", type = SoftwareUpdateAction.class),
        @XmlElementRef(name = "shellcommand", type = ShellCommandAction.class)
    })
            
        
    List<Object> actions;

    public Actions() {
        actions = new ArrayList<Object>();
    }

    public Actions(List<Object> actions) {
        this.actions = actions;
    }

    public void addAction(Object action) {
        actions.add(action);
    }


    public void removeAction(Object action) {
        actions.remove(action);
    }

    public List<Object> getActions() {
        return actions;
    }

    public void setActions(List<Object> actions) {
        this.actions = actions;
    }
    
}