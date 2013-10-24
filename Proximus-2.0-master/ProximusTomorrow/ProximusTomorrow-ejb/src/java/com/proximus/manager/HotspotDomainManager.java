/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.HotspotDomain;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.log4j.Logger;

/**
 *
 * @author ronald
 */
@Stateless
public class HotspotDomainManager extends AbstractManager<HotspotDomain> implements HotspotDomainManagerLocal {

    private static final Logger logger = Logger.getLogger(HotspotDomainManager.class.getName());
    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }
    
    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public HotspotDomainManager() {
        super(HotspotDomain.class);
    }
}
