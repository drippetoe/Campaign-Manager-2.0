/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.Company;
import com.proximus.data.PubNubKey;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Gilberto Gaxiola
 */
@Stateless
public class PubNubKeyManager extends AbstractManager<PubNubKey> implements PubNubKeyManagerLocal {

    public PubNubKeyManager() {
        super(PubNubKey.class);
    }
    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    public PubNubKey getPubNubKeyByChannel(String channel, Company c) {
        Query q = em.createQuery("SELECT n FROM PubNubKey n WHERE n.channel = ?1 and n.company = ?2");
        q.setParameter(1, channel);
        q.setParameter(2, c);
        List<PubNubKey> results = (List<PubNubKey>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public List<PubNubKey> findAllByCompany(Company c) {
        Query q = em.createQuery("SELECT n FROM PubNubKey n WHERE n.company = ?1 order by n.channel");
        q.setParameter(1, c);
        List<PubNubKey> results = (List<PubNubKey>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public boolean doesPubNubKeyChannelExistsByCompany(String channel, Company c) {
        Query q = em.createQuery("SELECT n FROM PubNubKey n WHERE n.channel = ?1 and n.company = ?2");
        q.setParameter(1, channel);
        q.setParameter(2, c);
        List<PubNubKey> results = (List<PubNubKey>) q.getResultList();
        if (results.size() > 0) {
            return true;
        }
        return false;
    }
}
