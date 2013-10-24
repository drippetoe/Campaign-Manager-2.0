/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms;

import com.proximus.data.sms.Locale;
import com.proximus.manager.AbstractManager;
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
public class LocaleManager extends AbstractManager<Locale> implements LocaleManagerLocal {

    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public LocaleManager() {
        super(Locale.class);
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
    public Locale getLocaleByName(String name) {
        Query q = em.createQuery("SELECT l FROM Locale l WHERE l.name = ?1");
        q.setParameter(1, name);
        List<Locale> results = (List<Locale>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null; // none
    }

    @Override
    public Locale getLocaleByLanguageCode(String languageCode) {
        Query q = em.createQuery("SELECT l FROM Locale l WHERE l.languageCode = ?1");
        q.setParameter(1, languageCode);
        List<Locale> results = (List<Locale>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null; // none
    }

    @Override
    public List<Locale> getAllSorted() {
        Query q = em.createQuery("SELECT l FROM Locale l order by l.name");
        List<Locale> results = (List<Locale>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null; // none
    }

    @Override
    public List<Locale> getAllSortedByKeyword() {
        Query q = em.createQuery("SELECT DISTINCT(k.locale) FROM Keyword k ORDER BY k.locale.name");
        List<Locale> results = (List<Locale>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null; // none
    }

    @Override
    public Locale getDefaultLocale() {
        return getLocaleByLanguageCode("en");
    }
}
