/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms.report;

import com.proximus.data.Brand;
import com.proximus.data.sms.report.ViewActiveUsers;
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
public class ViewActiveUsersManager extends AbstractManager<ViewActiveUsers> implements ViewActiveUsersManagerLocal {

    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public ViewActiveUsersManager() {
        super(ViewActiveUsers.class);
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
    public List<ViewActiveUsers> getActiveUsers(Brand brand, Date date) {
        String queryString = "SELECT au FROM ViewActiveUsers au WHERE au.brand = :brand";
        if (date != null) {
            queryString += " AND au.eventDate <= :day";
        }
        queryString += " ORDER BY au.eventDate";
        Query q = em.createQuery(queryString);
        q.setParameter("brand", brand);
        if (date != null) {
            q.setParameter("day", DateUtil.getEndOfDay(date));
        }
        List<ViewActiveUsers> results = (List<ViewActiveUsers>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null; // none
    }

    @Override
    public Long getCountActiveUsers(Brand brand, Date date) {
        String queryString = "SELECT SUM(au.activeUsers) FROM ViewActiveUsers au WHERE au.brand = :brand";
        if (date != null) {
            queryString += " AND au.eventDate <= :day";
        }
        Query q = em.createQuery(queryString, Long.class);
        q.setParameter("brand", brand);
        if (date != null) {
            q.setParameter("day", DateUtil.getEndOfDay(date));
        }
        Long result = (Long) q.getResultList().get(0);
        if(result == null) {
            return 0L;
        }
        return result;

    }
}
