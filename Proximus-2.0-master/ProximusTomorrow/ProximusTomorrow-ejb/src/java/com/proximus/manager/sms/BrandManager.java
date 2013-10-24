/*
 * Copyright 2010-2012, Proximus Mobility, LLC.
 * 
 */
package com.proximus.manager.sms;

import com.proximus.data.Brand;
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
public class BrandManager extends AbstractManager<Brand> implements BrandManagerLocal {

    private static final Logger logger = Logger.getLogger(BrandManager.class);
    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public BrandManager() {
        super(Brand.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    public Brand getBrandByName(String name) {
        Query q = em.createQuery("SELECT b FROM Brand b WHERE b.name = ?1");
        q.setParameter(1, name);
        List<Brand> results = (List<Brand>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null; // none
    }

    @Override
    public List<Brand> getAllSorted() {
        Query q = em.createQuery("SELECT b FROM Brand b order by b.name");
        List<Brand> results = (List<Brand>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null; // none
    }
}
