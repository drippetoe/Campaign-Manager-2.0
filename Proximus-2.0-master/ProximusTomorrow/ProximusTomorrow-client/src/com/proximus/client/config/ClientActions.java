/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.client.config;

import com.proximus.client.modules.Module;
import com.proximus.data.Actions;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * @author Gilberto Gaxiola
 */
public class ClientActions extends Observable
{
    private Actions actions;
    List<Module> modules;

    public ClientActions()
    {
        modules = new ArrayList<Module>();
        actions = new Actions();
    }

    public ClientActions(Actions actions)
    {
        modules = new ArrayList<Module>();
        this.actions = actions;
    }

    public void addModule(Module module)
    {
        this.addObserver(module);
        modules.add(module);
    }

    public void addModules(List<Module> modsList)
    {
        for (Module module : modsList) {
            this.addObserver(module);
            modules.add(module);
        }
    }

    public void initModules()
    {
        for (Module module : modules) {
            module.init();

        }
    }

    public List<Object> getActions() {
        return actions.getActions();
    }

    public void setActions(Actions actions) {
        this.actions = actions;
        setChanged();
        notifyObservers(this.clone());
    }
    
    

    @Override
    protected Object clone()
    {
        ClientActions result = new ClientActions(this.actions);
        return result;
    }
}
