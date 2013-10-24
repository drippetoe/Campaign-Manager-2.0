/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.FacebookUserLog;
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
public class FacebookUserLogManager extends AbstractManager<FacebookUserLog> implements FacebookUserLogManagerLocal {

    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public FacebookUserLogManager() {
        super(FacebookUserLog.class);
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
    public FacebookUserLog getFacebookUserLogByFacebookId(Long facebookId) {
        Query q = em.createQuery("SELECT f FROM FacebookUserLog f WHERE f.id = ?1");
        q.setParameter(1, facebookId);
        List<FacebookUserLog> results = (List<FacebookUserLog>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null; // none
    }
}
