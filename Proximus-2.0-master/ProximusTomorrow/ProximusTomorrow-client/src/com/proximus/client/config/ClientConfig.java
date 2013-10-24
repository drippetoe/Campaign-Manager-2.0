/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.client.config;

import com.proximus.client.modules.Module;
import com.proximus.data.config.Config;
import com.proximus.util.JAXBUtil;
import com.proximus.util.client.ClientURISettings;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * ClientConfig keeps the config in memory as well as writes to file.
 * @author ejohansson
 */
public class ClientConfig extends Observable
{
    
    private static final Logger logger = Logger.getLogger(ClientConfig.class.getName());
    private Config config;
    private List<Module> modules;
    

    public ClientConfig()
    {
        modules = new ArrayList<Module>();
        new File(ClientURISettings.CONFIG_ROOT).mkdirs();
        this.config = new Config();
    }

    private ClientConfig(Config config)
    {
        modules = new ArrayList<Module>();
        new File(ClientURISettings.CONFIG_ROOT).mkdirs();
        this.config = config;
    }

    public Config getConfig()
    {
        return config;
    }

    public void setConfig(Config config)
    {
        this.config = config;
    }

    public void addModule(Module mod)
    {
        this.addObserver(mod);
        modules.add(mod);
    }

    public void addModules(List<Module> mods)
    {
        for (Module module : mods) {
            this.addObserver(module);
            modules.add(module);
        }
    }

    public void loadConfiguration()
    {
        try {
            File clientConfig = new File(ClientURISettings.CONFIG_FILE);
            if (clientConfig.exists() && clientConfig.isFile()) {
                this.config = JAXBUtil.loadFromFile(Config.class, ClientURISettings.CONFIG_FILE);
                setChanged();
                notifyObservers(this.clone());
            } else {
                JAXBUtil.saveToFile(Config.class, this.config, ClientURISettings.CONFIG_FILE);
            }
        } catch (Exception ex) {
            logger.log(Priority.ERROR, ex);
        }
    }

    public void saveConfiguration()
    {
        try {
            JAXBUtil.saveToFile(Config.class, this.config, ClientURISettings.CONFIG_FILE);
            setChanged();
            notifyObservers(this.clone());
        } catch (Exception ex) {
            logger.log(Priority.ERROR, ex);
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException
    {
        return new ClientConfig(this.config);
    }

    public void initModules()
    {
        for (Module module : modules) {
            module.init();
        }
    }
}
