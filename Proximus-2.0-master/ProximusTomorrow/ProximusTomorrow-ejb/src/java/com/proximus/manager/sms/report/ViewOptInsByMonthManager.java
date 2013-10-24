/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms.report;

import com.proximus.data.Company;
import com.proximus.data.sms.Property;
import com.proximus.data.sms.report.ViewOptInsByMonth;
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
public class ViewOptInsByMonthManager extends AbstractManager<ViewOptInsByMonth> implements ViewOptInsByMonthManagerLocal {

    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public ViewOptInsByMonthManager() {
        super(ViewOptInsByMonth.class);
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
    public List<ViewOptInsByMonth> getOptInsInYearAndMonth(Company company, int year, int month) {

        
        String queryString = "SELECT vo FROM ViewOptInsByMonth vo WHERE vo.company = :company AND vo.theMonth = :month AND vo.theYear = :year";
        Query q = em.createQuery(queryString);
        q.setParameter("company", company);
        q.setParameter("month", month);
        q.setParameter("year", year);
        List<ViewOptInsByMonth> results = (List<ViewOptInsByMonth>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null; // none
    }

    @Override
    public Long getOptInsInYearAndMonthByProperty(Company company, int year, int month, Property property) {
        String queryString = "SELECT vo FROM ViewOptInsByMonth vo WHERE vo.company = :company AND vo.theMonth = :month AND vo.theYear = :year AND vo.property = :property";
        Query q = em.createQuery(queryString);
        q.setParameter("company", company);
        q.setParameter("month", month);
        q.setParameter("year", year);
        q.setParameter("property", property);
        List<ViewOptInsByMonth> results = (List<ViewOptInsByMonth>) q.getResultList();
        Long actualResult = 0L;
        if (results.size() > 0) {
            for (ViewOptInsByMonth r : results) {
                actualResult += r.getOptIns();

            }
        }
        return actualResult;

    }
}
