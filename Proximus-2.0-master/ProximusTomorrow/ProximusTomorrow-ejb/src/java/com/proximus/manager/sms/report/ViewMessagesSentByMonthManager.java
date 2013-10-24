/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms.report;

import com.proximus.data.Company;
import com.proximus.data.sms.Property;
import com.proximus.data.sms.report.ViewKeywordTotalOptOuts;
import com.proximus.data.sms.report.ViewMessagesSentByMonth;
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
public class ViewMessagesSentByMonthManager extends AbstractManager<ViewMessagesSentByMonth> implements ViewMessagesSentByMonthManagerLocal {

    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public ViewMessagesSentByMonthManager() {
        super(ViewMessagesSentByMonth.class);
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
    public List<ViewMessagesSentByMonth> getSentInYearAndMonth(Company company, int year, int month) {

        String queryString = "SELECT vm FROM ViewMessagesSentByMonth vm WHERE vm.company = :company AND vm.theMonth = :month AND vm.theYear = :year";
        Query q = em.createQuery(queryString);
        q.setParameter("company", company);
        q.setParameter("month", month);
        q.setParameter("year", year);
        List<ViewMessagesSentByMonth> results = (List<ViewMessagesSentByMonth>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null; // none
    }

    @Override
    public Long getSentInYearAndMonthByProperty(Company company, int year, int month, Property property) {
        String queryString = "SELECT vm FROM ViewMessagesSentByMonth vm WHERE vm.company = :company AND vm.theMonth = :month AND vm.theYear = :year AND vm.property = :property";
        Query q = em.createQuery(queryString);
        q.setParameter("company", company);
        q.setParameter("month", month);
        q.setParameter("year", year);
        q.setParameter("property", property);
        List<ViewMessagesSentByMonth> results = (List<ViewMessagesSentByMonth>) q.getResultList();
        Long actualResult = 0L;
        if (results.size() > 0) {
            for (ViewMessagesSentByMonth r : results) {
                actualResult += r.getMessagesSent();

            }
        }
        return actualResult;

    }
}
