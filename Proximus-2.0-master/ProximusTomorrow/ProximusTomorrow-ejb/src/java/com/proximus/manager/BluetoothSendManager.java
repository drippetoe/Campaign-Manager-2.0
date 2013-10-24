/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.BluetoothSend;
import com.proximus.data.Campaign;
import com.proximus.data.Company;
import com.proximus.data.Device;
import com.proximus.data.report.ViewBluetoothDaySummary;
import com.proximus.data.util.DateUtil;
import java.math.BigDecimal;
import java.util.*;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Gilberto Gaxiola
 */
@Stateless
public class BluetoothSendManager extends AbstractManager<BluetoothSend> implements BluetoothSendManagerLocal {

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

    public BluetoothSendManager() {
        super(BluetoothSend.class);
    }

    /**
     * Since all the methods are going to query on the same parameters of
     * event_date, company, device and campaign we may use this helper method
     *
     * Parameter order:
     *
     * 1. Start Date 2. End Date 3. Company 4. Campaign 5. Device
     *
     * @return
     */
    private String getWhereClause(Company company, Campaign campaign, Device device) {
        String queryString = " WHERE b.event_date BETWEEN ?1 AND ?2";
        if (company != null) {
            queryString += " AND b.company_id = ?3";
        }
        if (campaign != null) {
            queryString += " AND b.campaign_id = ?4";
        }
        if (device != null) {

            queryString += " AND b.device_id = ?5";
        }
        return queryString;
    }

    @Override
    public Long getTotalDevicesSeen(Company company, Campaign campaign, Device device, Date startDate, Date endDate) {
        String queryString = "SELECT COUNT(b.id) FROM bluetooth_send b";
        queryString += getWhereClause(company, campaign, device);
        Query q = em.createNativeQuery(queryString);
        q.setParameter(1, DateUtil.getStartOfDay(startDate));
        q.setParameter(2, DateUtil.getEndOfDay(endDate));
        if (company != null) {
            q.setParameter(3, company.getId());
        }
        if (campaign != null) {
            q.setParameter(4, campaign.getId());
        }
        if (device != null) {
            q.setParameter(5, device.getId());
        }
        Object result = q.getSingleResult();
        if (result == null) {
            return 0L;
        }
        return (Long) result;

    }

    @Override
    public Long getUniqueDevicesSeen(Company company, Campaign campaign, Device device, Date startDate, Date endDate) {
        String queryString = "SELECT COUNT(DISTINCT b.mac_address) FROM bluetooth_send b";
        queryString += getWhereClause(company, campaign, device);
        Query q = em.createNativeQuery(queryString);
        q.setParameter(1, DateUtil.getStartOfDay(startDate));
        q.setParameter(2, DateUtil.getEndOfDay(endDate));
        if (company != null) {
            q.setParameter(3, company.getId());
        }
        if (campaign != null) {
            q.setParameter(4, campaign.getId());
        }
        if (device != null) {
            q.setParameter(5, device.getId());
        }
        Object result = q.getSingleResult();
        if (result == null) {
            return 0L;
        }
        return (Long) result;
    }

    @Override
    public Long getUniqueDevicesSupportingBluetooth(Company company, Campaign campaign, Device device, Date startDate, Date endDate) {
        String queryString = "SELECT COUNT(DISTINCT b.mac_address) FROM bluetooth_send b";
        queryString += getWhereClause(company, campaign, device);
        queryString += " AND b.send_status IN (1,2,3,4,5,7)";
        Query q = em.createNativeQuery(queryString);
        q.setParameter(1, DateUtil.getStartOfDay(startDate));
        q.setParameter(2, DateUtil.getEndOfDay(endDate));
        if (company != null) {
            q.setParameter(3, company.getId());
        }
        if (campaign != null) {
            q.setParameter(4, campaign.getId());
        }
        if (device != null) {
            q.setParameter(5, device.getId());
        }
        Object result = q.getSingleResult();
        if (result == null) {
            return 0L;
        }
        return (Long) result;
    }

    @Override
    public Long getUniqueDevicesAcceptingPush(Company company, Campaign campaign, Device device, Date startDate, Date endDate) {
        String queryString = "SELECT COUNT(DISTINCT b.mac_address) FROM bluetooth_send b";
        queryString += getWhereClause(company, campaign, device);
        queryString += " AND (b.accept_status = 1 OR b.send_status = 7)";
        Query q = em.createNativeQuery(queryString);
        q.setParameter(1, DateUtil.getStartOfDay(startDate));
        q.setParameter(2, DateUtil.getEndOfDay(endDate));
        if (company != null) {
            q.setParameter(3, company.getId());
        }
        if (campaign != null) {
            q.setParameter(4, campaign.getId());
        }
        if (device != null) {
            q.setParameter(5, device.getId());
        }
        Object result = q.getSingleResult();
        if (result == null) {
            return 0L;
        }
        return (Long) result;
    }

    @Override
    public Long getUniqueDevicesDownloadingContent(Company company, Campaign campaign, Device device, Date startDate, Date endDate) {
        String queryString = "SELECT COUNT(DISTINCT b.mac_address) FROM bluetooth_send b";
        queryString += getWhereClause(company, campaign, device);
        queryString += " AND b.send_status = 1";
        Query q = em.createNativeQuery(queryString);
        q.setParameter(1, DateUtil.getStartOfDay(startDate));
        q.setParameter(2, DateUtil.getEndOfDay(endDate));
        if (company != null) {
            q.setParameter(3, company.getId());
        }
        if (campaign != null) {
            q.setParameter(4, campaign.getId());
        }
        if (device != null) {
            q.setParameter(5, device.getId());
        }
        Object result = q.getSingleResult();
        if (result == null) {
            return 0L;
        }
        return (Long) result;
    }

    @Override
    public Long getTotalContentDownloads(Company company, Campaign campaign, Device device, Date startDate, Date endDate) {
        String queryString = "SELECT COUNT(b.id) FROM bluetooth_send b";
        queryString += getWhereClause(company, campaign, device);
        queryString += " AND b.send_status = 1";
        Query q = em.createNativeQuery(queryString);
        q.setParameter(1, DateUtil.getStartOfDay(startDate));
        q.setParameter(2, DateUtil.getEndOfDay(endDate));
        if (company != null) {
            q.setParameter(3, company.getId());
        }
        if (campaign != null) {
            q.setParameter(4, campaign.getId());
        }
        if (device != null) {
            q.setParameter(5, device.getId());
        }
        Object result = q.getSingleResult();
        if (result == null) {
            return 0L;
        }
        return (Long) result;
    }

    @Override
    public List<ViewBluetoothDaySummary> getBluetoothDaySummaryForDevicesSeen(Company company, Campaign campaign, Device device, Date startDate, Date endDate) {
        String queryString = "SELECT DATE(b.event_date), COUNT(b.id) FROM bluetooth_send b";
        queryString += getWhereClause(company, campaign, device);
        queryString += " GROUP BY DATE(b.event_date)";
        Query q = em.createNativeQuery(queryString);
        q.setParameter(1, DateUtil.getStartOfDay(startDate));
        q.setParameter(2, DateUtil.getEndOfDay(endDate));
        if (company != null) {
            q.setParameter(3, company.getId());
        }
        if (campaign != null) {
            q.setParameter(4, campaign.getId());
        }
        if (device != null) {
            q.setParameter(5, device.getId());
        }
        
        List<Object[]> rows = (List<Object[]>) q.getResultList();

        if (rows.size() > 0) {
            List<ViewBluetoothDaySummary> results = new ArrayList<ViewBluetoothDaySummary>(rows.size());
            for (Object[] row : rows) {
                Date eventDate = (Date) row[0];
                Long count = (Long) row[1];
                long actualCount = count.longValue();
                results.add(new ViewBluetoothDaySummary(eventDate, actualCount, 0L));
            }
            return results;
        }
        return null;
    }
    
    
    @Override
    public List<ViewBluetoothDaySummary> getBluetoothDaySummaryForContentDownload(Company company, Campaign campaign, Device device, Date startDate, Date endDate) {
        String queryString = "SELECT DATE(b.event_date), COUNT(b.id) FROM bluetooth_send b";
        queryString += getWhereClause(company, campaign, device);
        queryString += " AND b.send_status = 1";
        queryString += " GROUP BY DATE(b.event_date)";
        Query q = em.createNativeQuery(queryString);
        q.setParameter(1, DateUtil.getStartOfDay(startDate));
        q.setParameter(2, DateUtil.getEndOfDay(endDate));
        if (company != null) {
            q.setParameter(3, company.getId());
        }
        if (campaign != null) {
            q.setParameter(4, campaign.getId());
        }
        if (device != null) {
            q.setParameter(5, device.getId());
        }
        
        List<Object[]> rows = (List<Object[]>) q.getResultList();

        if (rows.size() > 0) {
            List<ViewBluetoothDaySummary> results = new ArrayList<ViewBluetoothDaySummary>(rows.size());
            for (Object[] row : rows) {
                Date eventDate = (Date) row[0];
                Long count = (Long) row[1];
                long actualCount = count.longValue();
                results.add(new ViewBluetoothDaySummary(eventDate, 0L, actualCount));
            }
            return results;
        }
        return null;
    }

    @Override
    public List<BluetoothSend> getBluetoothSendInRangeByCampaignAndDevice(Company company, Campaign campaign , Device device, Date startDate, Date endDate) {
         String query = "SELECT s FROM BluetoothSend s WHERE s.company = ?1 ";
        
        int paramCount = 1;
        HashMap<Integer, Object> params = new HashMap<Integer, Object>();
        if (campaign != null && campaign.getName() != null && !campaign.getName().isEmpty()) {
            paramCount++;
            query += " AND s.campaign = ?" + paramCount + " ";
            params.put(paramCount, campaign);
        }
        if (device != null) {
            paramCount++;
            query += " AND s.device = ?" + paramCount + " ";
            params.put(paramCount, device);
        }
        if (startDate != null ) {
            paramCount++;
            query += " AND s.eventDate >= ?" + paramCount + " ";
            params.put(paramCount, startDate);
        }
        if (endDate != null ) {
            paramCount++;
            query += " AND s.eventDate <= ?" + paramCount + " ";
            params.put(paramCount, DateUtil.getEndOfDay(endDate));
        }
        
        Query q = em.createQuery(query);

        q.setParameter(1, company);

        for (Map.Entry<Integer, Object> entry : params.entrySet()) {
            Integer paramId = entry.getKey();
            Object object = entry.getValue();
            q.setParameter(paramId, object);
        }
        
        List<BluetoothSend> results = (List<BluetoothSend>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

  
}
