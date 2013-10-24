/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms;

import com.proximus.data.Company;
import com.proximus.data.sms.*;
import com.proximus.manager.AbstractManager;
import java.util.ArrayList;
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
public class KeywordManager extends AbstractManager<Keyword> implements KeywordManagerLocal {

    private static final Logger logger = Logger.getLogger(Keyword.class.getName());
    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public KeywordManager() {
        super(Keyword.class);
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
    public Keyword getKeywordByShortCode(String keyword, ShortCode shortCode) {
        Query q = em.createQuery("SELECT k FROM Keyword k WHERE k.keyword = ?1 AND k.ShortCode = ?2");
        q.setParameter(1, keyword);
        q.setParameter(2, shortCode);
        List<Keyword> results = (List<Keyword>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public List<Keyword> findAllByCompany(Company c) {
        Query q = em.createQuery("SELECT k FROM Keyword k WHERE k.company = ?1");
        q.setParameter(1, c);
        List<Keyword> results = (List<Keyword>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public List<Keyword> findAllNotDeletedByCompany(Company c) {
        Query query = em.createQuery("SELECT k FROM Keyword k "
                + "WHERE k.company = ?1 "
                + "AND k.deleted = :deleted");
        query.setParameter(1, c);
        query.setParameter("deleted", false);
        List<Keyword> results = (List<Keyword>) query.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public List<Keyword> findAllByShortCode(ShortCode shortcode) {
        Query q = em.createQuery("SELECT k FROM Keyword k WHERE k.shortCode = ?1");
        q.setParameter(1, shortcode);
        List<Keyword> results = (List<Keyword>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public Company getCompanyByKeyword(String keyword) {
        Query q = em.createQuery("SELECT k.company FROM Keyword k WHERE k.keyword = ?1");
        q.setParameter(1, keyword);
        List<Company> results = (List<Company>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public Keyword getKeywordByKeywordString(String keyword) {
        Query q = em.createQuery("SELECT k FROM Keyword k WHERE k.keyword = ?1");
        q.setParameter(1, keyword);
        List<Keyword> results = (List<Keyword>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public List<Keyword> getKeywordByDMA(Company company, DMA dma) {
        Query q = em.createQuery("SELECT k FROM Keyword k WHERE k.company = ?1 AND k.property.dma =?2");
        q.setParameter(1, company);
        q.setParameter(2, dma);
        List<Keyword> results = (List<Keyword>) q.getResultList();

        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public List<Keyword> getKeywordByCompanyNoDMA(Company company) {
        Query q = em.createQuery("SELECT k FROM Keyword k WHERE k.company = ?1 AND k.property IS NULL");
        q.setParameter(1, company);
        List<Keyword> results = (List<Keyword>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public List<Keyword> findAllByCompanyWithProperty(Company c) {
        Query q = em.createQuery("SELECT k FROM Keyword k WHERE k.company = ?1 AND k.property IS NOT NULL");
        q.setParameter(1, c);
        List<Keyword> results = (List<Keyword>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public List<Keyword> getKeywordsBySourceType(SourceType sourceType) {
        Query q = em.createQuery("SELECT k FROM Keyword k WHERE k.sourceType = :sourceType");
        q.setParameter("sourceType", sourceType);
        List<Keyword> results = (List<Keyword>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }
}
