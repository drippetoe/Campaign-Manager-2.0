/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms;

import com.proximus.data.sms.Country;
import com.proximus.manager.AbstractManager;
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
public class CountryManager extends AbstractManager<Country> implements CountryManagerLocal {

    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public CountryManager() {
        super(Country.class);
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
    public Country findByName(String name) {
        Query q = em.createQuery("SELECT c FROM Country c WHERE c.name=?1");
        q.setParameter(1, name);
        List<Country> results = (List<Country>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null; // none
    }

    @Override
    public Country findByCode(String code) {
        Query q = em.createQuery("SELECT c FROM Country c WHERE c.code=?1");
        q.setParameter(1, code);
        List<Country> results = (List<Country>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null; // none
    }

    @Override
    public List<Country> findAllSorted() {
        Query q = em.createQuery("SELECT c FROM Country c ORDER BY c.name");
        List<Country> results = (List<Country>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null; // none
    }

    @Override
    public List<Country> findAllSortedCountriesByProperty() {
        Query q = em.createQuery("SELECT DISTINCT(p.country) FROM Property p ORDER BY p.country.name");
        List<Country> results = (List<Country>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null; // none
    }
}
