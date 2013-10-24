/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms.report;

import com.proximus.data.Company;
import com.proximus.data.sms.Keyword;
import com.proximus.data.sms.report.RegistrationSummary;
import com.proximus.data.util.DateUtil;
import com.proximus.manager.AbstractManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author ronald
 */
@Stateless
public class RegistrationReportManager extends AbstractManager<RegistrationSummary> implements RegistrationReportManagerLocal {

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

    public RegistrationReportManager() {
        super(RegistrationSummary.class);
    }

    @Override
    public RegistrationSummary fetchRegistrationSummaryByCompanyAndRegistrationSummary(Company company, RegistrationSummary summary) {
        String query = "SELECT r FROM RegistrationSummary r WHERE r.company = ?1 AND r.totalActiveSubscribers = ?2 AND r.totalOptOuts = ?3 AND r.totalRegistrations = ?4";
        Query q = em.createQuery(query);
        q.setParameter(1, company);
        q.setParameter(2, summary.getTotalActiveSubscribers());
        q.setParameter(3, summary.getTotalOptOuts());
        q.setParameter(4, summary.getTotalRegistrations());
        List<RegistrationSummary> results = (List<RegistrationSummary>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public RegistrationSummary fetchRegistrationSummaryForDma(Company company, RegistrationSummary summary) {
        String query = "SELECT r FROM RegistrationSummary r "
                + "WHERE r.company = ?1 "
                + "AND r.totalActiveSubscribers = ?2 "
                + "AND r.totalOptOuts = ?3 "
                + "AND r.totalRegistrations = ?4 "
                + "AND r.dma =?5";
        Query q = em.createQuery(query);
        q.setParameter(1, company);
        q.setParameter(2, summary.getTotalActiveSubscribers());
        q.setParameter(3, summary.getTotalOptOuts());
        q.setParameter(4, summary.getTotalRegistrations());
        q.setParameter(5, summary.getDma());
        List<RegistrationSummary> results = (List<RegistrationSummary>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public RegistrationSummary fetchRegistrationSummary(Date start, Date end, Company company) {
        if (end == null || start.getTime() == end.getTime()) {
            end = start;
        }
        String query = "SELECT r FROM RegistrationSummary r WHERE r.eventDate BETWEEN ?1 AND ?2 AND r.company = ?3 ORDER BY r.eventDate DESC";
        Query q = em.createQuery(query);
        q.setParameter(1, start);
        q.setParameter(2, DateUtil.getEndOfDay(end));
        q.setParameter(3, company);
        List<RegistrationSummary> results = (List<RegistrationSummary>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public List<RegistrationSummary> fetchRegistrationSummaries(Date start, Date end, Company company) {
        if (end == null || start.getTime() == end.getTime()) {
            end = start;
        }
        String query = "SELECT r FROM RegistrationSummary r WHERE r.eventDate BETWEEN ?1 AND ?2 AND r.company = ?3";
        Query q = em.createQuery(query);
        q.setParameter(1, start);
        q.setParameter(2, DateUtil.getEndOfDay(end));
        q.setParameter(3, company);

        List<RegistrationSummary> results = (List<RegistrationSummary>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public List<RegistrationSummary> getRegistrationsFromSubscriber(Company company) {
        Long optIns = 0L;
        Long optOuts = 0L;
        String query = "SELECT count(*) "
                + "FROM subscriber "
                + "WHERE status = 'OPT_IN_COMPLETE' "
                + "AND company_id = ?1 ";

        query += "GROUP BY company_id";

        Query q = em.createNativeQuery(query);
        q.setParameter(1, company.getId());

        List<RegistrationSummary> results = new ArrayList<RegistrationSummary>();
        try {
            optIns = (Long) q.getSingleResult();
        } catch (NoResultException e) {
            optIns = 0L;
        }
        String query2 = "SELECT count(*) "
                + "FROM subscriber "
                + "WHERE status = 'OPT_OUT' "
                + "AND company_id = ?1 ";

        query2 += "GROUP BY company_id";

        Query q2 = em.createNativeQuery(query2);
        q2.setParameter(1, company.getId());
        try {
            optOuts = (Long) q2.getSingleResult();
        } catch (NoResultException e) {
            optOuts = 0L;
        }

        Long totalRegistrations = optIns + optOuts;

        if ((optIns == null && optOuts == null && totalRegistrations == null) || (optIns == 0L && optOuts == 0L && totalRegistrations == 0L)) {
            return null;
        }

        RegistrationSummary item = new RegistrationSummary();
        item.setEventDate(new Date());
        item.setTotalActiveSubscribers(optIns);
        item.setTotalOptOuts(optOuts);
        item.setTotalRegistrations(totalRegistrations);
        item.setCompany(company);
        results.add(item);

        return results;

    }

    @Override
    public List<RegistrationSummary> getRegistrationsFromSubscriberByKeyword(Company company, Keyword keyword) {
        Long optinsByKeyword = 0L;
        Long optoutsByKeyword = 0L;


        String query = "SELECT count(*) "
                + "FROM subscriber s "
                + "WHERE s.status = 'OPT_IN_COMPLETE' "
                + "AND s.company_id = ?1 "
                + "AND s.id IN ";

        query += "(SELECT subscriber_id "
                + "FROM subscriber_keyword "
                + "WHERE keyword_id = ?2)";


        Query q = em.createNativeQuery(query);
        q.setParameter(1, company.getId());
        q.setParameter(2, keyword.getId());
        List<RegistrationSummary> results = new ArrayList<RegistrationSummary>();
        try {
            optinsByKeyword = (Long) q.getSingleResult();
        } catch (NoResultException e) {
            optinsByKeyword = 0L;
        }

        String query2 = "SELECT count(*) "
                + "FROM subscriber s "
                + "WHERE s.status = 'OPT_OUT' "
                + "AND s.company_id = ?1 "
                + "AND s.id IN ";

        query2 += "(SELECT subscriber_id "
                + "FROM subscriber_keyword "
                + "WHERE keyword_id = ?2)";

        Query q2 = em.createNativeQuery(query2);
        q2.setParameter(1, company.getId());
        q2.setParameter(2, keyword.getId());
        try {
            optoutsByKeyword = (Long) q2.getSingleResult();
        } catch (NoResultException e) {
            optoutsByKeyword = 0L;
        }
        Long totalRegistrationsByKeyword = optinsByKeyword + optoutsByKeyword;

        RegistrationSummary summary = new RegistrationSummary();
        summary.setEventDate(new Date());
        summary.setTotalActiveSubscribers(optinsByKeyword);
        summary.setTotalOptOuts(optoutsByKeyword);
        summary.setTotalRegistrations(totalRegistrationsByKeyword);
        summary.setCompany(company);
        summary.setDma(keyword.getProperty().getDma());

        results.add(summary);

        return results;

    }

    @Override
    public List<RegistrationSummary> fetchRegistrationSummariesByDMA(Date startDate, Date endDate, Company company) {
        if (endDate == null || startDate.getTime() == endDate.getTime()) {
            endDate = startDate;
        }
        String query = "SELECT r "
                + "FROM RegistrationSummary r "
                + "WHERE r.eventDate "
                + "BETWEEN ?1 AND ?2 "
                + "AND r.company = ?3 "
                + "AND r.dma IS NOT NULL ";

        query += "ORDER BY r.totalRegistrations DESC";

        Query q = em.createQuery(query);
        q.setParameter(1, startDate);
        q.setParameter(2, DateUtil.getEndOfDay(endDate));
        q.setParameter(3, company);

        List<RegistrationSummary> results = (List<RegistrationSummary>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }
}
