/**
 * Copyright (c) 2010-2013 Proximus Mobility LLC
 */
package com.proximus.manager.report;

import com.proximus.data.Campaign;
import com.proximus.data.Company;
import com.proximus.data.Device;
import com.proximus.data.report.ViewWifiDaySummary;
import com.proximus.data.report.ViewWifiPageViews;
import com.proximus.data.report.WifiDaySummary;
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
public class WifiDaySummaryManager extends AbstractManager<WifiDaySummary> implements WifiDaySummaryManagerLocal {

    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public WifiDaySummaryManager() {
        super(WifiDaySummary.class);
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
    public List<WifiDaySummary> getInDateRange(Company company, Campaign campaign, Device device, Date startDate, Date endDate) {
        String queryString = "SELECT w FROM WifiDaySummary w WHERE w.company = :company";

        if (startDate != null && endDate != null) {
            queryString += " AND DATE(w.eventDate) BETWEEN :startDate AND :endDate";
        }
        if (startDate != null && endDate == null) {
            queryString += " AND DATE(w.eventDate) >= :startDate";
        }
        if (startDate == null && endDate != null) {
            queryString += " AND DATE(w.eventDate) <= :endDate";
        }

        if (campaign != null) {
            queryString += " AND w.campaign = :campaign";
        }

        if (device != null) {
            queryString += " AND w.device = :device";
        }

        Query q = em.createQuery(queryString);
        q.setParameter("company", company);

        if (startDate != null) {
            q.setParameter("startDate", startDate);
        }
        if (endDate != null) {
            q.setParameter("endDate", endDate);
        }
        if (campaign != null) {
            q.setParameter("campaign", campaign);
        }
        if (device != null) {
            q.setParameter("device", device);
        }

        List<WifiDaySummary> results = (List<WifiDaySummary>) q.getResultList();

        if (results.size() > 0) {
            return results;
        } else {
            return null;
        }
    }

    @Override
    public List<WifiDaySummary> getAllByCompany(Company company) {
        String queryString = "SELECT w FROM WifiDaySummary w WHERE w.company = :company";
        Query q = em.createQuery(queryString);
        q.setParameter("company", company);
        List<WifiDaySummary> results = (List<WifiDaySummary>) q.getResultList();

        if (results.size() > 0) {
            return results;
        } else {
            return null;
        }
    }

    private Long selectSumTemplate(Company company, Campaign campaign, Device device, Date startDate, Date endDate, String fieldName) {
        String queryString = "SELECT sum(" + fieldName + ") FROM wifi_day_summary WHERE company_id = ?1";

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

        BigDecimal result = (BigDecimal) q.getSingleResult();

        if (result == null) {
            return 0L;
        }
        return result.longValue();
    }

    @Override
    public Long getPageViews(Company company, Campaign campaign, Device device, Date startDate, Date endDate) {
        return selectSumTemplate(company, campaign, device, startDate, endDate, "total_page_views");

    }

    @Override
    public Long getSuccessfulPageViews(Company company, Campaign campaign, Device device, Date startDate, Date endDate) {
        return selectSumTemplate(company, campaign, device, startDate, endDate, "successful_page_views");
    }

    @Override
    public Long getUniqueUsers(Company company, Campaign campaign, Device device, Date startDate, Date endDate) {
        return selectSumTemplate(company, campaign, device, startDate, endDate, "unique_users");
    }

    @Override
    public List<ViewWifiDaySummary> getViewSummary(Company company, Campaign campaign, Device device, Date startDate, Date endDate) {
        
        String queryString = "SELECT event_date, sum(successful_page_views) FROM wifi_day_summary WHERE company_id = ?1";

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
                Date event_date = (Date) row[0];
                BigDecimal sum = (BigDecimal) row[1];
                if(sum == null) {
                    sum = new BigDecimal(0L);
                }
                if(event_date == null) {
                    return null;
                }
                Long actualSum = sum.longValue();
                ViewWifiDaySummary view = new ViewWifiDaySummary(event_date, actualSum);
                results.add(view);
            }
            return results;

        } 
        return null;
        
        
    }
}
