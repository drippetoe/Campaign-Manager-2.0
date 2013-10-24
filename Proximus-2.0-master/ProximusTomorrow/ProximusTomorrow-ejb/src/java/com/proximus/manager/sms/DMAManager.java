/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms;

import com.proximus.data.sms.DMA;
import com.proximus.data.sms.Property;
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
public class DMAManager extends AbstractManager<DMA> implements DMAManagerLocal {

    private static final Logger logger = Logger.getLogger(DMA.class.getName());
    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public DMAManager() {
        super(DMA.class);
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
    public DMA getDMAByName(String name) {
        Query q = em.createQuery("SELECT d FROM DMA d WHERE d.name = ?1");
        q.setParameter(1, name);
        List<DMA> results = (List<DMA>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null; // none
    }

    @Override
    public List<DMA> getAllSorted() {
        Query q = em.createQuery("SELECT d FROM DMA d order by d.name");
        List<DMA> results = (List<DMA>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null; // none
    }

    @Override
    public DMA getDMAsByProperty(Property property) {
        Query q = em.createQuery("SELECT p.dma FROM Property p WHERE p = ?1");
        q.setParameter(1, property);
        List<DMA> results = (List<DMA>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null; // none
    }
        @Override
    public List<DMA> getDmaFromKeyword() {
        Query q = em.createQuery("SELECT DISTINCT(k.dma) FROM KeywordRegistrationSummary k WHERE k.dma IS NOT NULL ORDER BY k.dma.name");
        List<DMA> results = (List<DMA>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null; // none
    }
}
