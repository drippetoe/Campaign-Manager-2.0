/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms;

import com.proximus.data.sms.MobileOfferClickthrough;
import com.proximus.manager.AbstractManager;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author ronald
 */
@Stateless
public class MobileOfferClickthroughManager extends AbstractManager<MobileOfferClickthrough> implements MobileOfferClickthroughManagerLocal {

    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public MobileOfferClickthroughManager() {
        super(MobileOfferClickthrough.class);
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
