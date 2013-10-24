/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms;

import com.proximus.manager.AbstractManager;
import com.proximus.data.sms.SubscriberCampaign;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author ronald
 */
@Stateless
public class SubscriberCampaignManager extends AbstractManager<SubscriberCampaign> implements SubscriberCampaignManagerLocal {

    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public SubscriberCampaignManager() {
        super(SubscriberCampaign.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }
}
