/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms;

import com.proximus.data.sms.SourceType;
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
public class SourceTypeManager extends AbstractManager<SourceType> implements SourceTypeManagerLocal {

    private static final Logger logger = Logger.getLogger(SourceTypeManager.class.getName());
    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public SourceTypeManager() {
        super(SourceType.class);
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
    public SourceType getSourceTypeByName(String source) {
        Query q = em.createQuery("SELECT s FROM SourceType s WHERE s.sourceType = ?1");
        q.setParameter(1, source);
        List<SourceType> results = (List<SourceType>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null; // none
    }

    @Override
    public List<SourceType> getAllSorted() {
        Query q = em.createQuery("SELECT s FROM SourceType s order by s.sourceType");
        List<SourceType> results = (List<SourceType>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null; // none
    }
}
