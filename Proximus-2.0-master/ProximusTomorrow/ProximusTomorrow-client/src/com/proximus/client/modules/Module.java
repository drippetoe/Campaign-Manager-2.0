package com.proximus.client.modules;

import com.proximus.client.config.ClientCampaign;
import com.proximus.client.config.ClientConfig;
import com.proximus.client.config.ClientActions;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author ejohansson
 */
public abstract class Module implements Observer
{
    protected ClientConfig config;
    protected ClientCampaign clientCampaign;
    protected ClientActions actions;

    @Override
    public void update(Observable o, Object arg)
    {
        config = null;
        clientCampaign = null;
        actions = null;
        if (arg instanceof ClientConfig) {
            this.config = ((ClientConfig) arg); 
        } else if (arg instanceof ClientCampaign) {
            this.clientCampaign = ((ClientCampaign) arg);
        } else if (arg instanceof ClientActions) {
            this.actions = ((ClientActions) arg);
        }
        
        runTask();
    }

    public abstract void runTask(Object ... params);

    /**
     * override me if you need to do something prior to running the main loop
     */
    public abstract void init();
}
