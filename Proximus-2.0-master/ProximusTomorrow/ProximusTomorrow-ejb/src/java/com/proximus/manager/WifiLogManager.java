/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.Campaign;
import com.proximus.data.Company;
import com.proximus.data.Device;
import com.proximus.data.WifiLog;
import com.proximus.data.report.WifiDaySummary;
import com.proximus.data.util.DateUtil;
import java.util.*;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolationException;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 *
 * @author Gilberto Gaxiola
 */
@Stateless
public class WifiLogManager extends AbstractManager<WifiLog> implements WifiLogManagerLocal {

    private static final Logger logger = Logger.getLogger(WifiLogManager.class.getName());
    
    
    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public WifiLogManager() {
        super(WifiLog.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    private static final String pageviewSubquery = " ( e.requestUrl LIKE '%.html'"
            + "   OR e.requestUrl LIKE '%.php'"
            + "   OR e.requestUrl LIKE '%/' ) ";
    private static final String successfulRequestSubquery = " e.httpStatus = '200' ";
    private static final String realBrowserSubquery = " e.operatingSystemGroup != 'Unknown' AND e.browserGroup != 'CFNetwork' ";

    @Override
    public void createWifiLog(WifiLog log) {
        try {
            em.persist(log);
            em.flush();
        } catch (Exception e) {
            logger.log(Priority.ERROR, "Couldn't persist data");
        }
    }

    @Override
    public void createListWifiLogs(List<WifiLog> logs) {
        try {

            for (int i = 0; i < logs.size(); i++) {
                WifiLog r = logs.get(i);
                em.persist(logs.get(i));
                if ((i % 500) == 0) {
                    em.flush();
                    em.clear();
                }
            }
            em.flush();

        } catch (Exception e) {
            logger.log(Priority.ERROR, "Couldn't persist list of data: " + e);
        }
    }

    @Override
    public void deleteWifiLog(WifiLog log) {
        try {
            em.remove(log);
            em.flush();
        } catch (Exception e) {
            logger.log(Priority.ERROR, "Couldn't delete data");
        }



    }

    @Override
    public void deleteListWifiLogs(List<WifiLog> logs) {
        try {
            em.getTransaction().begin();
            for (int i = 0; i < logs.size(); i++) {
                em.remove(logs.get(i));
                if ((i % 5000) == 0) {
                    em.flush();
                    em.clear();
                }
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.log(Priority.ERROR, "Couldn't delete list of data");
        }
    }

    @Override
    public Long getUniqueUserCount(Company company, Date eventDate, Campaign campaign, Device device) {
        String query = "SELECT COUNT(DISTINCT e.macAddress) FROM WifiLog e WHERE e.eventDate BETWEEN ?1 AND ?2 AND e.company = ?3";

        int paramCount = 3;
        HashMap<Integer, Object> params = new HashMap<Integer, Object>();
        if (campaign != null) {
            paramCount++;
            query += " AND e.campaign = ?" + paramCount + " ";
            params.put(paramCount, campaign);
        }
        if (device != null) {
            paramCount++;
            query += " AND e.device = ?" + paramCount + " ";
            params.put(paramCount, device);
        }

        Query q = em.createQuery(query);
        q.setParameter(1, DateUtil.getStartOfDay(eventDate));
        q.setParameter(2, DateUtil.getEndOfDay(eventDate));
        q.setParameter(3, company);

        for (Map.Entry<Integer, Object> entry : params.entrySet()) {
            Integer paramId = entry.getKey();
            Object object = entry.getValue();
            q.setParameter(paramId, object);
        }

        Long countResult = (Long) q.getSingleResult();
        return countResult;
    }

    @Override
    public Long getTotalRequests(Company company, Date eventDate, Campaign campaign, Device device) {
        String query = "SELECT COUNT(e.id) FROM WifiLog e WHERE e.eventDate BETWEEN ?1 AND ?2 AND e.company = ?3";

        int paramCount = 3;
        HashMap<Integer, Object> params = new HashMap<Integer, Object>();
        if (campaign != null) {
            paramCount++;
            query += " AND e.campaign = ?" + paramCount + " ";
            params.put(paramCount, campaign);
        }
        if (device != null) {
            paramCount++;
            query += " AND e.device = ?" + paramCount + " ";
            params.put(paramCount, device);
        }

        Query q = em.createQuery(query);

        q.setParameter(1, DateUtil.getStartOfDay(eventDate));
        q.setParameter(2, DateUtil.getEndOfDay(eventDate));
        q.setParameter(3, company);

        for (Map.Entry<Integer, Object> entry : params.entrySet()) {
            Integer paramId = entry.getKey();
            Object object = entry.getValue();
            q.setParameter(paramId, object);
        }

        Long countResult = (Long) q.getSingleResult();
        return countResult;
    }

    @Override
    public Long getTotalPageViews(Company company, Date eventDate, Campaign campaign, Device device) {
        String query = "SELECT COUNT(e.id) FROM WifiLog e WHERE e.eventDate BETWEEN ?1 AND ?2 AND e.company = ?3 "
                + " AND " + pageviewSubquery
                + " AND " + realBrowserSubquery;

        int paramCount = 3;
        HashMap<Integer, Object> params = new HashMap<Integer, Object>();
        if (campaign != null) {
            paramCount++;
            query += " AND e.campaign = ?" + paramCount + " ";
            params.put(paramCount, campaign);
        }
        if (device != null) {
            paramCount++;
            query += " AND e.device = ?" + paramCount + " ";
            params.put(paramCount, device);
        }

        Query q = em.createQuery(query);

        q.setParameter(1, DateUtil.getStartOfDay(eventDate));
        q.setParameter(2, DateUtil.getEndOfDay(eventDate));
        q.setParameter(3, company);

        for (Map.Entry<Integer, Object> entry : params.entrySet()) {
            Integer paramId = entry.getKey();
            Object object = entry.getValue();
            q.setParameter(paramId, object);
        }

        Long countResult = (Long) q.getSingleResult();
        return countResult;
    }

    @Override
    public Long getSuccessfulPageViews(Company company, Date eventDate, Campaign campaign, Device device) {
        String query = "SELECT COUNT(e.id) FROM WifiLog e WHERE e.eventDate >= ?1 AND e.eventDate <= ?2 AND e.company = ?3 "
                + " AND " + pageviewSubquery
                + " AND " + successfulRequestSubquery
                + " AND " + realBrowserSubquery;

        
        int paramCount = 3;
        HashMap<Integer, Object> params = new HashMap<Integer, Object>();
        if (campaign != null) {
            paramCount++;
            query += " AND e.campaign = ?" + paramCount + " ";
            params.put(paramCount, campaign);
        }
        if (device != null) {
            paramCount++;
            query += " AND e.device = ?" + paramCount + " ";
            params.put(paramCount, device);
        }

        Query q = em.createQuery(query);

        q.setParameter(1, DateUtil.getStartOfDay(eventDate));
        q.setParameter(2, DateUtil.getEndOfDay(eventDate));
        q.setParameter(3, company);

        for (Map.Entry<Integer, Object> entry : params.entrySet()) {
            Integer paramId = entry.getKey();
            Object object = entry.getValue();
            q.setParameter(paramId, object);
        }

        Long countResult = (Long) q.getSingleResult();
        return countResult;
    }

    @Override
    public WifiDaySummary fetchWifiDaySummary(Date eventDate, Company company, Campaign campaign, Device device) {
        String query = "SELECT e FROM WifiDaySummary e WHERE e.eventDate BETWEEN ?1 AND ?2 AND e.company = ?3 ";

        int paramCount = 3;
        HashMap<Integer, Object> params = new HashMap<Integer, Object>();
        if (campaign != null) {
            paramCount++;
            query += " AND e.campaign = ?" + paramCount + " ";
            params.put(paramCount, campaign);
        }
        if (device != null) {
            paramCount++;
            query += " AND e.device = ?" + paramCount + " ";
            params.put(paramCount, device);
        }

        Query q = em.createQuery(query);

        q.setParameter(1, DateUtil.getStartOfDay(eventDate));
        q.setParameter(2, DateUtil.getEndOfDay(eventDate));
        q.setParameter(3, company);

        for (Map.Entry<Integer, Object> entry : params.entrySet()) {
            Integer paramId = entry.getKey();
            Object object = entry.getValue();
            q.setParameter(paramId, object);
        }

        List<WifiDaySummary> results = (List<WifiDaySummary>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public List<WifiDaySummary> fetchWifiDaySummaries(Date start, Date end, Company company, Campaign campaign, Device device) {
        String query = "SELECT e FROM WifiDaySummary e WHERE e.eventDate BETWEEN ?1 AND ?2 AND e.company = ?3";

        int paramCount = 3;
        HashMap<Integer, Object> params = new HashMap<Integer, Object>();
        if (campaign != null) {
            paramCount++;
            query += " AND e.campaign = ?" + paramCount + " ";
            params.put(paramCount, campaign);
        }
        if (device != null) {
            paramCount++;
            query += " AND e.device = ?" + paramCount + " ";
            params.put(paramCount, device);
        }

        if (end == null || start.getTime() == end.getTime()) {
            end = start;
        }

        Query q = em.createQuery(query);

        q.setParameter(1, DateUtil.getStartOfDay(start));
        q.setParameter(2, DateUtil.getEndOfDay(end));
        q.setParameter(3, company);

        for (Map.Entry<Integer, Object> entry : params.entrySet()) {
            Integer paramId = entry.getKey();
            Object object = entry.getValue();
            q.setParameter(paramId, object);
        }

        List<WifiDaySummary> results = (List<WifiDaySummary>) q.getResultList();
        return results;
    }

    @Override
    public List<WifiLog> getWifiLogInRangeByCampaignAndDevice(Date start, Date end, Company company, Campaign campaign, Device device) {

        String query = "SELECT s FROM WifiLog s WHERE s.company = ?1 ";

        int paramCount = 1;
        HashMap<Integer, Object> params = new HashMap<Integer, Object>();
        if (campaign != null) {
            paramCount++;
            query += " AND s.campaign = ?" + paramCount + " ";
            params.put(paramCount, campaign);
        }
        if (device != null) {
            paramCount++;
            query += " AND s.device = ?" + paramCount + " ";
            params.put(paramCount, device);
        }
        if (start != null) {
            paramCount++;
            query += " AND s.eventDate >= ?" + paramCount + " ";
            params.put(paramCount, DateUtil.getStartOfDay(start));
        }
        if (end != null) {
            paramCount++;
            query += " AND s.eventDate <= ?" + paramCount + " ";
            params.put(paramCount, DateUtil.getEndOfDay(end));
        }

        Query q = em.createQuery(query);

        q.setParameter(1, company);

        for (Map.Entry<Integer, Object> entry : params.entrySet()) {
            Integer paramId = entry.getKey();
            Object object = entry.getValue();
            q.setParameter(paramId, object);
        }

        List<WifiLog> results = (List<WifiLog>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public void createWifiDaySummary(WifiDaySummary entry) {
        try {
            em.persist(entry);
        } catch (javax.validation.ConstraintViolationException err) {
            logger.error(err.getConstraintViolations());
        } catch (javax.ejb.EJBException err) {
            Throwable cause = err.getCause();
            if (cause instanceof ConstraintViolationException) {
                logger.error(((ConstraintViolationException) cause).getConstraintViolations());
            } else {
                logger.error(err);
            }
        } catch (Exception err) {
            logger.error(err);
        }
    }

    @Override
    public void updateWifiDaySummary(WifiDaySummary entry) {
        try {
            em.merge(entry);
        } catch (javax.validation.ConstraintViolationException err) {
            logger.error(err.getConstraintViolations());
        } catch (javax.ejb.EJBException err) {
            Throwable cause = err.getCause();
            if (cause instanceof ConstraintViolationException) {
                logger.error(((ConstraintViolationException) cause).getConstraintViolations());
            } else {
                logger.error(err);
            }
        } catch (Exception err) {
            logger.error(err);
        }
    }

    @Override
    public void createOrUpdateWifiDaySummary(WifiDaySummary entry) {
        if (entry.getId() != null && entry.getId() > 0) {
            logger.log(Priority.INFO, "updating existing WifiDaySummary entry");
            updateWifiDaySummary(entry);
        } else {
            logger.log(Priority.INFO, "creating new WifiDaySummary entry");
            createWifiDaySummary(entry);
        }
    }

    @Override
    public List<WifiLog> getMostPopularServersFromWifiLog(Date start, Date end, Company company, Campaign campaign, Device device) {

        int count = 4;
        HashMap<Integer, Object> parameters = new HashMap<Integer, Object>();
        String query = "SELECT server_name, count(*) "
                //        String query = "SELECT DATE(event_date), server_name, count(*) "
                + "FROM wifi_log "
                + "WHERE http_status = '200' "
                + "AND server_name != '192.168.45.3' "
                + "AND server_name != '192.168.3.1' "
                + "AND company_id = ?1 "
                + "AND event_date BETWEEN ?2 AND ?3 ";
        if (campaign != null) {
            query += "AND campaign_id = ?" + count + " ";
            parameters.put(count, campaign.getId());
            count++;

        }
        if (device != null) {
            query += "AND device_id = ?" + count + " ";
            parameters.put(count, device.getId());
            count++;
        }

        query += "GROUP BY server_name ORDER BY COUNT(*) DESC LIMIT 15";

        Query q = em.createNativeQuery(query);
        q.setParameter(1, company.getId());
        q.setParameter(2, DateUtil.getStartOfDay(start));
        q.setParameter(3, DateUtil.getNextDay(end));

        for (Map.Entry<Integer, Object> entry : parameters.entrySet()) {
            Integer position = entry.getKey();
            Object param = entry.getValue();
            q.setParameter(position, param);
        }

        List<WifiLog> results = new ArrayList<WifiLog>();

        List<Object[]> result = q.getResultList();
        for (Object[] object : result) {
            String serverName = (String) object[0];
            //Date eventDate = (Date) object[1];
            Long records = (Long) object[1];

            WifiLog item = new WifiLog();
            //item.setEventDate(eventDate);
            item.setServerName(serverName);
            item.setRecords(records);
            item.setCompany(company);
            item.setCampaign(campaign);
            item.setDevice(device);
            results.add(item);
        }
        return results;
    }
}
