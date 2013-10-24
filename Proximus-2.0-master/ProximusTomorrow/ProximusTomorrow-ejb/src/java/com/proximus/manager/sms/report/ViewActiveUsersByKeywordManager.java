/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms.report;

import com.proximus.data.Brand;
import com.proximus.data.sms.Keyword;
import com.proximus.data.sms.report.ViewActiveUsersByKeyword;
import com.proximus.data.util.DateUtil;
import com.proximus.manager.AbstractManager;
import java.util.Date;
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
public class ViewActiveUsersByKeywordManager extends AbstractManager<ViewActiveUsersByKeyword> implements ViewActiveUsersByKeywordManagerLocal {

    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public ViewActiveUsersByKeywordManager() {
        super(ViewActiveUsersByKeyword.class);
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
    public List<ViewActiveUsersByKeyword> getActiveUsersByKeyword(Brand brand, Date date, Keyword keyword) {
        String queryString = "SELECT au FROM ViewActiveUsersByKeyword au WHERE au.brand = :brand AND au.keyword = :keyword";
        if (date != null) {
            queryString += " AND au.eventDate <= :day";
        }
        queryString += " ORDER BY au.eventDate";
        Query q = em.createQuery(queryString);
        q.setParameter("brand", brand);
        q.setParameter("keyword", keyword);
        if (date != null) {
            q.setParameter("day", DateUtil.getEndOfDay(date));
        }
        List<ViewActiveUsersByKeyword> results = (List<ViewActiveUsersByKeyword>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null; // none
    }

    @Override
    public Long getCountActiveUsersByKeyword(Brand brand, Date date, Keyword keyword) {
        String queryString = "SELECT SUM(au.activeUsers) FROM ViewActiveUsersByKeyword au WHERE au.brand = :brand AND au.keyword = :keyword";
        if (date != null) {
            queryString += " AND au.eventDate <= :day";
        }
        Query q = em.createQuery(queryString, Long.class);
        q.setParameter("brand", brand);
        q.setParameter("keyword", keyword);
        if (date != null) {
            q.setParameter("day", DateUtil.getEndOfDay(date));
        }
        Long result = (Long) q.getResultList().get(0);
        if (result == null) {
            return 0L;
        }
        return result;

    }

    @Override
    public List<Keyword> getTopKeywordsByActiveUsers(Brand brand, Date date, int topX) {
        String queryString = "SELECT au.keyword FROM ViewActiveUsersByKeyword au WHERE au.brand = :brand";
        if (date != null) {
            queryString += " AND au.eventDate <= :day";
        }
        //IGNORING IMPORTED KEYWORD
        queryString += " AND au.keyword.keyword != 'IMPORTED'";
        queryString += " GROUP BY au.keyword ORDER BY SUM(au.activeUsers) DESC";
        Query q = em.createQuery(queryString).setMaxResults(topX);
        q.setParameter("brand", brand);
        if (date != null) {
            q.setParameter("day", DateUtil.getEndOfDay(date));
        }

        List<Keyword> result = (List<Keyword>) q.getResultList();
        return result;
    }

    @Override
    public List<Keyword> getTopKeywordsByActiveUsersAndLocale(Brand brand, com.proximus.data.sms.Locale locale, Date date, int topX) {
        String queryString = "SELECT au.keyword FROM ViewActiveUsersByKeyword au WHERE au.brand = :brand AND au.keyword.locale = :locale";
        if (date != null) {
            queryString += " AND au.eventDate <= :day";
        }
        //IGNORING IMPORTED KEYWORD
        queryString += " AND au.keyword.keyword != 'IMPORTED'";
        queryString += " GROUP BY au.keyword ORDER BY SUM(au.activeUsers) DESC";
        Query q = em.createQuery(queryString).setMaxResults(topX);
        q.setParameter("brand", brand);
        q.setParameter("locale", locale);
        if (date != null) {
            q.setParameter("day", DateUtil.getEndOfDay(date));
        }

        List<Keyword> result = (List<Keyword>) q.getResultList();
        return result;
    }
}
