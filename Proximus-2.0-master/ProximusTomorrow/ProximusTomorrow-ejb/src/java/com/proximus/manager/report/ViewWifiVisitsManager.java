/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.report;

import com.proximus.data.Campaign;
import com.proximus.data.Company;
import com.proximus.data.Device;
import com.proximus.data.report.ViewWifiPageViews;
import com.proximus.data.report.ViewWifiSuccessfulPages;
import com.proximus.data.report.ViewWifiVisits;
import com.proximus.data.report.WifiVisits;
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
    public class ViewWifiVisitsManager extends AbstractManager<ViewWifiVisits> implements ViewWifiVisitsManagerLocal {
    
    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public ViewWifiVisitsManager() {
        super(ViewWifiVisits.class);
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
    public List<ViewWifiVisits> getRawWifiVisits(Company company, Campaign campaign, Device device, Date startDate, Date endDate) {
        String queryString = "SELECT wv FROM ViewWifiVisits wv WHERE wv.company = :company";

        if (startDate != null && endDate != null) {
            queryString += " AND wv.eventDate BETWEEN :startDate AND :endDate";
        }
        if (startDate != null && endDate == null) {
            queryString += " AND wv.eventDate >= :startDate";
        }
        if (startDate == null && endDate != null) {
            queryString += " AND wv.eventDate <= :endDate";
        }

        if (campaign != null) {
            queryString += " AND wv.campaign = :campaign";
        }
        if (device != null) {
            queryString += " AND wv.device = :device";
        }

        Query q = em.createQuery(queryString);
        q.setParameter("company", company);
        if (startDate != null) {
            q.setParameter("startDate", DateUtil.getStartOfDay(startDate));
        }
        if (endDate != null) {
            q.setParameter("endDate", DateUtil.getEndOfDay(endDate));
        }
        if (campaign != null) {
            q.setParameter("campaign", campaign);
        }
        if (device != null) {
            q.setParameter("device", device);
        }

        List<ViewWifiVisits> results = (List<ViewWifiVisits>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null; // none
    }

    @Override
    public List<WifiVisits> getWifiVisits(Company company, Campaign campaign, Device device, Date startDate, Date endDate) {
        
        String queryString = "SELECT mac_address, count(*) FROM wifi_log WHERE is_page_view = 1 AND company_id = ?1";
        
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

        queryString += " GROUP BY mac_address ORDER BY count(*) DESC";


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
            List<WifiVisits> results = new ArrayList<WifiVisits>(rows.size());
            for (Object[] row : rows) {
                String  macAddress = (String) row[0];
                long actualCount = (Long) row[1];
                results.add(new WifiVisits(macAddress, actualCount));
            }
            return results;

        }


        return null; // none
    }

    

}
