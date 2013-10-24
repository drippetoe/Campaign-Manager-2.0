/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.report;

import com.proximus.data.Campaign;
import com.proximus.data.Company;
import com.proximus.data.Device;
import com.proximus.data.report.ViewWifiDaySummary;
import com.proximus.data.report.ViewWifiPageViews;
import com.proximus.data.report.ViewWifiSuccessfulPages;
import com.proximus.data.util.DateUtil;
import com.proximus.manager.AbstractManager;
import java.math.BigDecimal;
import java.util.ArrayList;
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
public class ViewWifiPageManager extends AbstractManager<ViewWifiPageViews> implements ViewWifiPageManagerLocal {

    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public ViewWifiPageManager() {
        super(ViewWifiPageViews.class);
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
    public List<ViewWifiPageViews> getPageViews(Company company, Campaign campaign, Device device, Date startDate, Date endDate) {

        String queryString = "SELECT DATE(event_date), request_url, count(request_url), company_id, campaign_id, device_id FROM wifi_log";
        queryString += " WHERE is_page_view = 1 and company_id = ?1";

        if (startDate != null && endDate != null) {
            queryString += " AND DATE(event_date) BETWEEN ?2 AND ?3";
        }
        if (startDate != null && endDate == null) {
            queryString += " AND DATE(event_date) >= ?2";
        }
        if (startDate == null && endDate != null) {
            queryString += " AND DATE(event_date) <= ?3";
        }

        if (campaign != null) {
            queryString += " AND campaign_id = ?4";
        }

        if (device != null) {
            queryString += " AND device_id = ?5";
        }

        queryString += " GROUP BY request_url, device_id, campaign_id, DATE(event_date)";

        Query q = em.createNativeQuery(queryString);

        q.setParameter(1, company.getId());
        if (startDate != null) {
            q.setParameter(2, DateUtil.getStartOfDay(startDate));
        }
        if (endDate != null) {
            q.setParameter(3, DateUtil.getEndOfDay(endDate));
        }

        if (campaign != null) {
            q.setParameter(4, campaign.getId());
        }

        if (device != null) {
            q.setParameter(5, device.getId());
        }


        List<Object[]> rows = (List<Object[]>) q.getResultList();
        if (rows.size() > 0) {
            List<ViewWifiPageViews> results = new ArrayList<ViewWifiPageViews>(rows.size());
            for (Object[] row : rows) {
                Date event_date = (Date) row[0];
                String request = (String) row[1];
                long actualCount = (Long) row[2];
                ViewWifiPageViews view = new ViewWifiPageViews();
                view.setEventDate(event_date);
                view.setRequestUrl(request);
                view.setSuccessfulPageViews(actualCount);
                results.add(view);
            }
            return results;

        }


        return null; // none



//        String queryString = "SELECT wiv FROM ViewWifiPageViews wiv WHERE wiv.company = :company";
//
//        if (startDate != null && endDate != null) {
//            queryString += " AND wiv.eventDate BETWEEN :startDate AND :endDate";
//        }
//        if (startDate != null && endDate == null) {
//            queryString += " AND wiv.eventDate >= :startDate";
//        }
//        if (startDate == null && endDate != null) {
//            queryString += " AND wiv.eventDate <= :endDate";
//        }
//
//        if (campaign != null) {
//            queryString += " AND wiv.campaign = :campaign";
//        }
//        if (device != null) {
//            queryString += " AND wiv.device = :device";
//        }
//
//        Query q = em.createQuery(queryString);
//        q.setParameter("company", company);
//        if (startDate != null) {
//            q.setParameter("startDate", DateUtil.getStartOfDay(startDate));
//        }
//        if (endDate != null) {
//            q.setParameter("endDate", DateUtil.getEndOfDay(endDate));
//        }
//        if (campaign != null) {
//            q.setParameter("campaign", campaign);
//        }
//        if (device != null) {
//            q.setParameter("device", device);
//        }
//
//        long start = System.currentTimeMillis();
//        
//        List<ViewWifiPageViews> results = (List<ViewWifiPageViews>) q.getResultList();
//        long end = System.currentTimeMillis() - start;
//        System.out.println("get page views timing (in seconds):" + (end/100));
//        if (results.size() > 0) {
//            return results;
//        }
//        return null; // none
    }

    @Override
    public List<ViewWifiSuccessfulPages> getSuccessfulPageViews(Company company, Campaign campaign, Device device, Date startDate, Date endDate) {

        String queryString = "SELECT request_url, count(request_url) FROM wifi_log WHERE is_page_view = 1 AND company_id = ?1";

        if (startDate != null && endDate != null) {
            queryString += " AND event_date BETWEEN ?2 AND ?3";
        }
        if (startDate != null && endDate == null) {
            queryString += " AND event_date >= ?2";
        }
        if (startDate == null && endDate != null) {
            queryString += " AND event_date <= ?3";
        }

        if (campaign != null) {
            queryString += " AND campaign_id = ?4";
        }

        if (device != null) {
            queryString += " AND device_id = ?5";
        }

        queryString += " GROUP BY request_url";


        Query q = em.createNativeQuery(queryString);

        q.setParameter(1, company.getId());
        if (startDate != null) {
            q.setParameter(2, DateUtil.getStartOfDay(startDate));
        }
        if (endDate != null) {
            q.setParameter(3, DateUtil.getEndOfDay(endDate));
        }

        if (campaign != null) {
            q.setParameter(4, campaign.getId());
        }

        if (device != null) {
            q.setParameter(5, device.getId());
        }


        List<Object[]> rows = (List<Object[]>) q.getResultList();

        if (rows.size() > 0) {
            List<ViewWifiSuccessfulPages> results = new ArrayList<ViewWifiSuccessfulPages>(rows.size());
            for (Object[] row : rows) {
                String request = (String) row[0];
                long actualCount = (Long)row[1];
                results.add(new ViewWifiSuccessfulPages(request, actualCount));
            }
            return results;

        }


        return null; // none
    }

    @Override
    public List<String> getUniqueUsers(Company company, Campaign campaign, Device device, Date startDate, Date endDate) {
        String queryString = "SELECT DISTINCT mac_address FROM wifi_log WHERE company_id = ?1";

        if (startDate != null && endDate != null) {
            queryString += " AND DATE(event_date) BETWEEN ?2 AND ?3";
        }
        if (startDate != null && endDate == null) {
            queryString += " AND DATE(event_date >= ?2";
        }
        if (startDate == null && endDate != null) {
            queryString += " AND DATE(event_date <= ?3";
        }

        if (campaign != null) {
            queryString += " AND campaign_id = ?4";
        }

        if (device != null) {
            queryString += " AND device_id = ?5";
        }

        Query q = em.createNativeQuery(queryString);
        q.setParameter(1, company.getId());
        if (startDate != null) {
            q.setParameter(2, DateUtil.getStartOfDay(startDate));
        }
        if (endDate != null) {
            q.setParameter(3, DateUtil.getEndOfDay(endDate));
        }

        if (campaign != null) {
            q.setParameter(4, campaign.getId());
        }

        if (device != null) {
            q.setParameter(5, device.getId());
        }

        List<String> results = (List<String>) q.getResultList();

        if (results.size() > 0) {
            return results;
        }
        return null; // none


    }

    @Override
    public List<ViewWifiDaySummary> getWifiDaySummary(Company company, Campaign campaign, Device device, Date startDate, Date endDate) {

        String queryString = "SELECT DATE(event_date), count(request_url) FROM wifi_log WHERE is_page_view = 1 AND company_id = ?1";


        if (startDate != null && endDate != null) {
            queryString += " AND DATE(event_date) BETWEEN ?2 AND ?3";
        }
        if (startDate != null && endDate == null) {
            queryString += " AND DATE(event_date) >= ?2";
        }
        if (startDate == null && endDate != null) {
            queryString += " AND DATE(event_date) <= ?3";
        }

        if (campaign != null) {
            queryString += " AND campaign_id = ?4";
        }

        if (device != null) {
            queryString += " AND device_id = ?5";
        }

        queryString += " GROUP BY DATE(event_date)";


        Query q = em.createNativeQuery(queryString);

        q.setParameter(1, company.getId());
        if (startDate != null) {
            q.setParameter(2, DateUtil.getStartOfDay(startDate));
        }
        if (endDate != null) {
            q.setParameter(3, DateUtil.getEndOfDay(endDate));
        }

        if (campaign != null) {
            q.setParameter(4, campaign.getId());
        }

        if (device != null) {
            q.setParameter(5, device.getId());
        }


        List<Object[]> rows = (List<Object[]>) q.getResultList();

        if (rows.size() > 0) {
            List<ViewWifiDaySummary> results = new ArrayList<ViewWifiDaySummary>(rows.size());
            for (Object[] row : rows) {
                Date eventDate = (Date) row[0];
                long actualCount = (Long)row[1];
                results.add(new ViewWifiDaySummary(eventDate, actualCount));
            }
            return results;

        }


        return null; // none
    }
}
