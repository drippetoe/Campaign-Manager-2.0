/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms.report;

import com.proximus.data.Brand;
import com.proximus.data.Company;
import com.proximus.data.sms.Keyword;
import com.proximus.data.sms.Locale;
import com.proximus.data.sms.report.ViewKeywordTotalOptIns;
import com.proximus.data.sms.report.ViewKeywordTotalOptInsPending;
import com.proximus.data.util.DateUtil;
import com.proximus.manager.AbstractManager;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Gilberto Gaxiola
 */
@Stateless
public class ViewKeywordTotalOptInsPendingManager extends AbstractManager<ViewKeywordTotalOptInsPending> implements ViewKeywordTotalOptInsPendingManagerLocal {

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

    public ViewKeywordTotalOptInsPendingManager() {
        super(ViewKeywordTotalOptInsPending.class);

    }

    @Override
    public List<ViewKeywordTotalOptInsPending> getAllByBrand(Brand brand) {
        String queryString = "SELECT ks FROM ViewKeywordTotalOptInsPending ks WHERE ks.company.brand = :brand";
        Query q = em.createQuery(queryString);
        q.setParameter("brand", brand);
        List<ViewKeywordTotalOptInsPending> results = (List<ViewKeywordTotalOptInsPending>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null; // none
    }

    @Override
    public List<ViewKeywordTotalOptInsPending> getAllByCompany(Company company) {
        String queryString = "SELECT ks FROM ViewKeywordTotalOptInsPending ks WHERE ks.company = :company";
        Query q = em.createQuery(queryString);
        q.setParameter("company", company);
        List<ViewKeywordTotalOptInsPending> results = (List<ViewKeywordTotalOptInsPending>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null; // none
    }

    @Override
    public List<ViewKeywordTotalOptInsPending> getAllByKeyword(Keyword keyword) {
        String queryString = "SELECT ks FROM ViewKeywordTotalOptInsPending ks WHERE ks.keyword = :keyword";
        Query q = em.createQuery(queryString);
        q.setParameter("keyword", keyword);
        List<ViewKeywordTotalOptInsPending> results = (List<ViewKeywordTotalOptInsPending>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null; // none
    }

    @Override
    public long getTotalByKeywordAndDate(Keyword keyword, Date startDate, Date endDate) {
        String queryString = "SELECT sum(ks.totalOptInsPending) FROM ViewKeywordTotalOptInsPending ks WHERE ks.keyword = :keyword";
        if (startDate != null && endDate != null) {
            queryString += " AND ks.eventDate BETWEEN :startDate AND :endDate";
        }
        if (startDate != null && endDate == null) {
            queryString += " AND ks.eventDate >= :startDate";
        }
        if (startDate == null && endDate != null) {
            queryString += " AND ks.eventDate <= :endDate";
        }
        Query q = em.createQuery(queryString, Long.class);
        q.setParameter("keyword", keyword);
        if (startDate != null) {
            q.setParameter("startDate", DateUtil.getStartOfDay(startDate));
        }
        if (endDate != null) {
            q.setParameter("endDate", DateUtil.getEndOfDay(endDate));
        }
        Long result = (Long) q.getSingleResult();
        if (result == null) {
            return 0L;
        } else {
            return result;
        }
    }

    @Override
    public Map<Keyword, Long> getTotalPerKeywordByCompanyAndDate(Company company, Date startDate, Date endDate) {
        String queryString = "SELECT SUM(total_opt_ins_pending), keyword_id FROM view_keyword_total_opt_ins_pending WHERE company_id = ?1";
        if (startDate != null && endDate != null) {
            queryString += " AND event_date BETWEEN ?2 AND ?3";
        }
        if (startDate != null && endDate == null) {
            queryString += " AND event_date >= ?2";
        }
        if (startDate == null && endDate != null) {
            queryString += " AND event_date <= ?3";
        }

        queryString += " GROUP BY keyword_id";
        Query q = em.createNativeQuery(queryString);
        q.setParameter(1, company.getId());
        if (startDate != null) {
            q.setParameter(2, DateUtil.getStartOfDay(startDate));
        }
        if (endDate != null) {
            q.setParameter(3, DateUtil.getEndOfDay(endDate));
        }

        Map<Keyword, Long> resultMap = new HashMap<Keyword, Long>();
        List<Object[]> result = q.getResultList();
        for (Object[] object : result) {
            BigDecimal decimalVal = (BigDecimal) object[0];
            Long value = decimalVal.longValue();
            Long key = (Long) object[1];


            Query q1 = em.createQuery("SELECT k FROM Keyword k where k.id = ?1");
            q1.setParameter(1, key);
            Keyword keyword = (Keyword) q1.getSingleResult();
            resultMap.put(keyword, value);
        }

        return resultMap;
    }

    @Override
    public Map<Keyword, Long> getTotalPerKeywordByCompanyAndLocaleAndDate(Company company, Locale locale, Date startDate, Date endDate) {
        String queryString = "SELECT SUM(total_opt_ins_pending), keyword_id FROM view_keyword_total_opt_ins_pending WHERE company_id = ?1";
        if (startDate != null && endDate != null) {
            queryString += " AND event_date BETWEEN ?2 AND ?3";
        }
        if (startDate != null && endDate == null) {
            queryString += " AND event_date >= ?2";
        }
        if (startDate == null && endDate != null) {
            queryString += " AND event_date <= ?3";
        }

        queryString += " GROUP BY keyword_id";
        Query q = em.createNativeQuery(queryString);
        q.setParameter(1, company.getId());
        if (startDate != null) {
            q.setParameter(2, DateUtil.getStartOfDay(startDate));
        }
        if (endDate != null) {
            q.setParameter(3, DateUtil.getEndOfDay(endDate));
        }

        Map<Keyword, Long> resultMap = new HashMap<Keyword, Long>();
        List<Object[]> result = q.getResultList();
        for (Object[] object : result) {
            BigDecimal decimalVal = (BigDecimal) object[0];
            Long value = decimalVal.longValue();
            Long key = (Long) object[1];


            Query q1 = em.createQuery("SELECT k FROM Keyword k where k.id = ?1");
            q1.setParameter(1, key);
            Keyword keyword = (Keyword) q1.getSingleResult();
            if (keyword != null && keyword.getLocale().equals(locale)) {
                resultMap.put(keyword, value);
            }
        }
        return resultMap;
    }
}
