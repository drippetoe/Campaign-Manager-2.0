/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms;

import com.proximus.data.Brand;
import com.proximus.data.sms.Property;
import com.proximus.data.sms.Retailer;
import com.proximus.manager.AbstractManager;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;

/**
 *
 * @author ronald
 */
@Stateless
public class RetailerManager extends AbstractManager<Retailer> implements RetailerManagerLocal {

    private static final Logger logger = Logger.getLogger(Retailer.class.getName());
    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public RetailerManager() {
        super(Retailer.class);
    }
    
    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Retailer getRetailerByName(Brand brand, String name) {
        Query q = em.createQuery("SELECT r FROM Retailer r WHERE r.name = ?1 AND r.brand= ?2 order by r.name");
        q.setParameter(1, name);
        q.setParameter(2, brand);
        List<Retailer> results = (List<Retailer>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null; // none
    }

    @Override
    public List<Retailer> getAllRetailersByBrand(Brand b) {
        Query q = em.createQuery("SELECT r FROM Retailer r WHERE r.brand= ?1 order by r.name");
        q.setParameter(1, b);
        List<Retailer> results = (List<Retailer>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null; // none
    }
    
        @Override
    public List<Retailer> getRetailersByProperty(Property property) {
        Query q = em.createQuery("SELECT p.retailers FROM Property p WHERE p= :property");
        q.setParameter("property", property);
        List<Retailer> results = (List<Retailer>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null; // none
    }
}
