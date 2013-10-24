/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.PricingModel;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author ronald
 */
@Stateless
public class PricingModelManager extends AbstractManager<PricingModel> implements PricingModelManagerLocal {

    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public PricingModelManager() {
        super(PricingModel.class);
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
    public PricingModel getPricingModelByName(String name) {
        Query q = em.createQuery("SELECT p FROM PricingModel p WHERE p.name = ?1");
        q.setParameter(1, name);
        List<PricingModel> results = (List<PricingModel>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null; // none
    }

    @Override
    public List<PricingModel> findAllSortedPricingModels() {
        Query q = em.createQuery("SELECT p FROM PricingModel p ORDER BY p.name");
        List<PricingModel> results = (List<PricingModel>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null; // none
    }
}
