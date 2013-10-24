/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms.report;

import com.proximus.data.Company;
import com.proximus.data.sms.report.ViewActiveUsers;
import com.proximus.data.sms.report.ViewTotalMessagesSent;
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
public class ViewTotalMessagesSentManager extends AbstractManager<ViewTotalMessagesSent> implements ViewTotalMessagesSentManagerLocal {

    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public ViewTotalMessagesSentManager() {
        super(ViewTotalMessagesSent.class);
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
    public List<ViewTotalMessagesSent> getTotalMessagesSent(Company company, Date startDate, Date endDate) {
        String queryString = "SELECT ms FROM ViewTotalMessagesSent ms WHERE ms.company = :company";

        if (startDate != null && endDate != null) {
            queryString += " AND ms.eventDate BETWEEN :startDate AND :endDate";
        }
        if (startDate != null && endDate == null) {
            queryString += " AND ms.eventDate >= :startDate";
        }
        if (startDate == null && endDate != null) {
            queryString += " AND ms.eventDate <= :endDate";
        }

        queryString += " ORDER BY ms.eventDate";



        Query q = em.createQuery(queryString);
        q.setParameter("company", company);
        if (startDate != null) {
            q.setParameter("startDate", DateUtil.getStartOfDay(startDate));
        }
        if (endDate != null) {
            q.setParameter("endDate", DateUtil.getEndOfDay(endDate));
        }


        List<ViewTotalMessagesSent> results = (List<ViewTotalMessagesSent>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null; // none
    }

    @Override
    public Long getTotalMessagesSentBefore(Company company, Date date) {
        String queryString = "SELECT SUM(ms.totalMessages) FROM ViewTotalMessagesSent ms WHERE ms.company = :company";
        if (date != null) {
            queryString += " AND ms.eventDate < :date";
        }
        queryString += " ORDER BY ms.eventDate";
        Query q = em.createQuery(queryString,Long.class);
        q.setParameter("company", company);
        if (date != null) {
            q.setParameter("date", DateUtil.getEndOfDay(date));
        }
         
        Long result = (Long) q.getResultList().get(0);
        if(result == null) {
            return 0L;
        }
        return result;

    }
}
