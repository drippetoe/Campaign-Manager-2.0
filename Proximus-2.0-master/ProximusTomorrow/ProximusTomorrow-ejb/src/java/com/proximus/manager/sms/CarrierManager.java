/*
 * Copyright 2010-2012, Proximus Mobility, LLC.
 * 
 */
package com.proximus.manager.sms;

import com.proximus.data.sms.BannedWord;
import com.proximus.data.sms.Carrier;
import com.proximus.manager.AbstractManager;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;

/**
 *
 * @author dshaw
 */
@Stateless
public class CarrierManager extends AbstractManager<Carrier> implements CarrierManagerLocal {

    private static final Logger logger = Logger.getLogger(CarrierManager.class);
    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public CarrierManager() {
        super(Carrier.class);
    }
    
    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    public Carrier findByName(String name) {
        Query query = this.em.createQuery("SELECT c FROM Carrier c WHERE c.name = ?1");
        query.setParameter(1, name);
        List<Carrier> results = (List<Carrier>) query.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public Carrier findCarrierById(Long id) {
        Query query = this.em.createQuery("SELECT c FROM Carrier c WHERE c.id = ?1");
        query.setParameter(1, id);
        List<Carrier> results = (List<Carrier>) query.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    @Override
    public List<Carrier> findAllSupported() {
        Query query = this.em.createQuery("SELECT c FROM Carrier c WHERE c.supported = ?1 ORDER BY c.name");
        query.setParameter(1, true);
        List<Carrier> results = (List<Carrier>) query.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public List<Carrier> getAllSorted() {
        Query q = em.createQuery("SELECT c FROM Carrier c ORDER BY c.name");
        List<Carrier> results = (List<Carrier>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null; // none
    }
}