/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms;

import com.proximus.data.Company;
import com.proximus.data.sms.ShortCode;
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
public class ShortCodeManager extends AbstractManager<ShortCode> implements ShortCodeManagerLocal {

    private static final Logger logger = Logger.getLogger(ShortCode.class.getName());
    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public ShortCodeManager() {
        super(ShortCode.class);
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
    public ShortCode findByCompany(Company company) {
        Query q = em.createQuery("SELECT s FROM ShortCode s WHERE s.company = ?1");
        q.setParameter(1, company);
        List<ShortCode> results = (List<ShortCode>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public ShortCode findByShortCodeNumber(String codeNumber) {
        Query q = em.createQuery("SELECT s FROM ShortCode s WHERE s.shortCode = ?1");
        q.setParameter(1, codeNumber);
        List<ShortCode> results = (List<ShortCode>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }
}
