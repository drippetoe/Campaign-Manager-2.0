/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.*;
import com.proximus.data.report.BluetoothDaySummary;
import com.proximus.data.report.BluetoothFileSendSummary;
import com.proximus.data.util.DateUtil;
import java.util.*;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.validation.ConstraintViolationException;
import org.apache.log4j.Logger;

@Stateless
public class BluetoothReportManager implements BluetoothReportManagerLocal {

    private static final Logger logger = Logger.getLogger(BluetoothReportManager.class.getName());
    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public EntityManager getEntityManager() {
        return em;
    }

    public void setEntityManager(EntityManager em) {
        this.em = em;
    }
    
    

    @Override
    public void createBluetoothSend(BluetoothSend entry) {
        try {
            em.persist(entry);
        } catch (javax.validation.ConstraintViolationException err) {
            logger.error(err.getConstraintViolations().toString());
        } catch (javax.ejb.EJBException err) {
            Throwable cause = err.getCause();
            if (cause instanceof ConstraintViolationException) {
                logger.error(((ConstraintViolationException) cause).getConstraintViolations().toString());
            }
        } catch (Exception err) {
            logger.error(null, err);
        }
    }

    @Override
    public void deleteBluetoothSend(BluetoothSend report) {
        em.remove(report);
    }

    @Override
    public void createBluetoothDwell(BluetoothDwell entry) {
        try {
            em.persist(entry);
        } catch (javax.validation.ConstraintViolationException err) {
            logger.error(err.getConstraintViolations().toString());
        } catch (javax.ejb.EJBException err) {
            Throwable cause = err.getCause();
            if (cause instanceof ConstraintViolationException) {
                logger.error(((ConstraintViolationException) cause).getConstraintViolations().toString());
            }
        } catch (Exception err) {
            logger.error(null, err);
        }
    }

    @Override
    public Long getTotalDevicesSeen(Company company, Date eventDate, Campaign campaign, Device device) {
        Query q = em.createQuery("SELECT COUNT(b.id) FROM BluetoothSend b WHERE b.eventDate BETWEEN ?1 AND ?2 AND b.campaign = ?3 AND b.company = ?4 AND b.device=?5");
        q.setParameter(1, eventDate);
        q.setParameter(2, DateUtil.getEndOfDay(eventDate));
        q.setParameter(3, campaign);
        q.setParameter(4, company);
        q.setParameter(5, device);
        Long countResult = (Long) q.getSingleResult();
        return countResult;
    }

    //This needs to be fixed to return objects with unique mac addresses
    @Override
    public List<BluetoothSend> getAllUniqueDevicesSeen(Date start, Date end, Company company, Campaign campaign, Device device) {
//        Query q = em.createQuery("SELECT b FROM BluetoothSend b where b.macAddress in (select distinct b.macAddress from BluetoothSend b where b.company=?1) GROUP BY b.macAddress");
        String query = ("SELECT b FROM BluetoothSend b WHERE b.eventDate BETWEEN ?1 AND ?2 AND b.company = ?3");
//        Query q = em.createQuery("SELECT b FROM BluetoothSend b WHERE b.id in (SELECT min(b.id) FROM BluetoothSend bu WHERE b.company=?1 GROUP BY b.macAddress)");
        int paramCount = 3;
        HashMap<Integer, Object> params = new HashMap<Integer, Object>();
        if (campaign != null) {
            paramCount++;
            query += " AND b.campaign = ?" + paramCount + " ";
            params.put(paramCount, campaign);
        }
        if (device != null) {
            paramCount++;
            query += " AND b.device = ?" + paramCount + " ";
            params.put(paramCount, device);
        }
//        query +="GROUP BY b.macAddress ORDER BY b.friendlyName";
        Query q = em.createQuery(query);
        q.setParameter(1, start);
        q.setParameter(2, DateUtil.getEndOfDay(end));
        q.setParameter(3, company);

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

    @Override
    public Long getUniqueDevicesSeen(Company company, Date eventDate, Campaign campaign, Device device) {
        String query = "SELECT COUNT(DISTINCT b.macAddress) FROM BluetoothSend b WHERE b.eventDate BETWEEN ?1 AND ?2 AND b.company = ?3 ";

        int paramCount = 3;
        HashMap<Integer, Object> params = new HashMap<Integer, Object>();
        if (campaign != null) {
            paramCount++;
            query += " AND b.campaign = ?" + paramCount + " ";
            params.put(paramCount, campaign);
        }
        if (device != null) {
            paramCount++;
            query += " AND b.device = ?" + paramCount + " ";
            params.put(paramCount, device);
        }

        Query q = em.createQuery(query);
        q.setParameter(1, eventDate);
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
    public Long getUniqueDevicesSupportingBluetooth(Company company, Date eventDate, Campaign campaign, Device device) {
        String query = "SELECT COUNT(DISTINCT b.macAddress) FROM BluetoothSend b WHERE b.eventDate BETWEEN ?1 AND ?2 AND b.company = ?3 AND b.sendStatus in (1, 2, 3, 4, 5, 7) ";
        int paramCount = 3;
        HashMap<Integer, Object> params = new HashMap<Integer, Object>();
        if (campaign != null) {
            paramCount++;
            query += " AND b.campaign = ?" + paramCount + " ";
            params.put(paramCount, campaign);
        }
        if (device != null) {
            paramCount++;
            query += " AND b.device = ?" + paramCount + " ";
            params.put(paramCount, device);
        }
        Query q = em.createQuery(query);

        q.setParameter(1, eventDate);
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
    public Long getUniqueDevicesAcceptingPush(Company company, Date eventDate, Campaign campaign, Device device) {
        String query = "SELECT COUNT(DISTINCT b.macAddress) FROM BluetoothSend b WHERE (b.acceptStatus = ?1 OR b.sendStatus = ?2) AND b.eventDate BETWEEN ?3 AND ?4 AND b.company = ?5 ";
        int paramCount = 5;
        HashMap<Integer, Object> params = new HashMap<Integer, Object>();
        if (campaign != null) {
            paramCount++;
            query += " AND b.campaign = ?" + paramCount + " ";
            params.put(paramCount, campaign);
        }
        if (device != null) {
            paramCount++;
            query += " AND b.device = ?" + paramCount + " ";
            params.put(paramCount, device);
        }
        Query q = em.createQuery(query);

        q.setParameter(1, 1);
        q.setParameter(2, BluetoothSend.STATUS_ACCEPTED);
        q.setParameter(3, eventDate);
        q.setParameter(4, DateUtil.getEndOfDay(eventDate));
        q.setParameter(5, company);

        for (Map.Entry<Integer, Object> entry : params.entrySet()) {
            Integer paramId = entry.getKey();
            Object object = entry.getValue();
            q.setParameter(paramId, object);
        }

        Long countResult = (Long) q.getSingleResult();
        return countResult;
    }

    @Override
    public Long getUniqueDevicesDownloadingContent(Company company, Date eventDate, Campaign campaign, Device device) {
        String query = "SELECT COUNT(DISTINCT b.macAddress) FROM BluetoothSend b WHERE b.sendStatus = ?1 AND b.eventDate BETWEEN ?2 AND ?3 AND b.company = ?4 ";
        int paramCount = 4;
        HashMap<Integer, Object> params = new HashMap<Integer, Object>();
        if (campaign != null) {
            paramCount++;
            query += " AND b.campaign = ?" + paramCount + " ";
            params.put(paramCount, campaign);
        }
        if (device != null) {
            paramCount++;
            query += " AND b.device = ?" + paramCount + " ";
            params.put(paramCount, device);
        }

        Query q = em.createQuery(query);
        q.setParameter(1, BluetoothSend.STATUS_SENT);
        q.setParameter(2, eventDate);
        q.setParameter(3, DateUtil.getEndOfDay(eventDate));
        q.setParameter(4, company);

        for (Map.Entry<Integer, Object> entry : params.entrySet()) {
            Integer paramId = entry.getKey();
            Object object = entry.getValue();
            q.setParameter(paramId, object);
        }

        Long countResult = (Long) q.getSingleResult();
        return countResult;
    }

    @Override
    public Long getTotalContentDownloads(Company company, Date eventDate, Campaign campaign, Device device) {
        String query = "SELECT COUNT(b.id) FROM BluetoothSend b WHERE b.sendStatus = ?1 AND b.eventDate BETWEEN ?2 AND ?3 AND b.company = ?4 ";
        int paramCount = 4;
        HashMap<Integer, Object> params = new HashMap<Integer, Object>();
        if (campaign != null) {
            paramCount++;
            query += " AND b.campaign = ?" + paramCount + " ";
            params.put(paramCount, campaign);
        }
        if (device != null) {
            paramCount++;
            query += " AND b.device = ?" + paramCount + " ";
            params.put(paramCount, device);
        }

        Query q = em.createQuery(query);
        q.setParameter(1, BluetoothSend.STATUS_SENT);
        q.setParameter(2, eventDate);
        q.setParameter(3, DateUtil.getEndOfDay(eventDate));
        q.setParameter(4, company);

        for (Map.Entry<Integer, Object> entry : params.entrySet()) {
            Integer paramId = entry.getKey();
            Object object = entry.getValue();
            q.setParameter(paramId, object);
        }

        Long countResult = (Long) q.getSingleResult();
        return countResult;
    }

    @Override
    public void createBluetoothDaySummary(BluetoothDaySummary entry) {
        try {
            em.persist(entry);
        } catch (javax.validation.ConstraintViolationException err) {
            logger.error(err.getConstraintViolations().toString());
        } catch (javax.ejb.EJBException err) {
            Throwable cause = err.getCause();
            if (cause instanceof ConstraintViolationException) {
                logger.error(((ConstraintViolationException) cause).getConstraintViolations().toString());
            } else {
                logger.error(null, err);
            }
        } catch (Exception err) {
            logger.error(null, err);
        }
    }

    @Override
    public void updateBluetoothDaySummary(BluetoothDaySummary entry) {
        try {
            em.merge(entry);
        } catch (javax.validation.ConstraintViolationException err) {
            logger.error(err.getConstraintViolations().toString());
        } catch (javax.ejb.EJBException err) {
            Throwable cause = err.getCause();
            if (cause instanceof ConstraintViolationException) {
                logger.error(((ConstraintViolationException) cause).getConstraintViolations().toString());
            } else {
                logger.error(null, err);
            }
        } catch (Exception err) {
            logger.error(null, err);
        }
    }

    @Override
    public void createBluetoothFileSendSummary(BluetoothFileSendSummary entry) {
        try {
            em.persist(entry);
        } catch (javax.validation.ConstraintViolationException err) {
            logger.error(err.getConstraintViolations().toString());
        } catch (javax.ejb.EJBException err) {
            Throwable cause = err.getCause();
            if (cause instanceof ConstraintViolationException) {
                logger.error(((ConstraintViolationException) cause).getConstraintViolations().toString());
            } else {
                logger.error(null, err);
            }
        } catch (Exception err) {
            logger.error(null, err);
        }
    }

    @Override
    public void updateBluetoothFileSendSummary(BluetoothFileSendSummary entry) {
        try {
            em.merge(entry);
        } catch (javax.validation.ConstraintViolationException err) {
            logger.error(err.getConstraintViolations().toString());
        } catch (javax.ejb.EJBException err) {
            Throwable cause = err.getCause();
            if (cause instanceof ConstraintViolationException) {
                logger.error(((ConstraintViolationException) cause).getConstraintViolations().toString());
            } else {
                logger.error(null, err);
            }
        } catch (Exception err) {
            logger.error(null, err);
        }
    }

    @Override
    public BluetoothDaySummary fetchBluetoothDaySummary(Date eventDate, Company company, Campaign campaign, Device device) {
        String query = "SELECT b FROM BluetoothDaySummary b WHERE b.eventDate BETWEEN ?1 AND ?2 AND b.company = ?3 ";
        int paramCount = 3;
        HashMap<Integer, Object> params = new HashMap<Integer, Object>();
        if (campaign != null) {
            paramCount++;
            query += " AND b.campaign = ?" + paramCount + " ";
            params.put(paramCount, campaign);
        }
        if (device != null) {
            paramCount++;
            query += " AND b.device = ?" + paramCount + " ";
            params.put(paramCount, device);
        }

        Query q = em.createQuery(query);
        q.setParameter(1, eventDate);
        q.setParameter(2, DateUtil.getEndOfDay(eventDate));
        q.setParameter(3, company);

        for (Map.Entry<Integer, Object> entry : params.entrySet()) {
            Integer paramId = entry.getKey();
            Object object = entry.getValue();
            q.setParameter(paramId, object);
        }

        List<BluetoothDaySummary> results = (List<BluetoothDaySummary>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public List<BluetoothFileSendSummary> fetchBluetoothFileSendSummaries(Date eventDate, Company company, Campaign campaign, Device device) {
        String query = "SELECT b FROM BluetoothFileSendSummary b WHERE b.eventDate BETWEEN ?1 AND ?2 AND b.company = ?3 ";
        int paramCount = 3;
        HashMap<Integer, Object> params = new HashMap<Integer, Object>();
        if (campaign != null) {
            paramCount++;
            query += " AND b.campaign = ?" + paramCount + " ";
            params.put(paramCount, campaign);
        }
        if (device != null) {
            paramCount++;
            query += " AND b.device = ?" + paramCount + " ";
            params.put(paramCount, device);
        }

        Query q = em.createQuery(query);
        q.setParameter(1, eventDate);
        q.setParameter(2, DateUtil.getEndOfDay(eventDate));
        q.setParameter(3, company);

        for (Map.Entry<Integer, Object> entry : params.entrySet()) {
            Integer paramId = entry.getKey();
            Object object = entry.getValue();
            q.setParameter(paramId, object);
        }

        List<BluetoothFileSendSummary> results = (List<BluetoothFileSendSummary>) q.getResultList();
        return results;
    }

    @Override
    public List<BluetoothFileSendSummary> fetchBluetoothSendSummaries(Date start, Date end, Company company, Campaign campaign, Device device) {

        if (end == null || start.getTime() == end.getTime()) {
            end = start;
        }

        String query = "SELECT b FROM BluetoothFileSendSummary b WHERE b.eventDate BETWEEN ?1 AND ?2 AND b.company = ?3 ";
        int paramCount = 3;
        HashMap<Integer, Object> params = new HashMap<Integer, Object>();
        if (campaign != null) {
            paramCount++;
            query += " AND b.campaign = ?" + paramCount + " ";
            params.put(paramCount, campaign);
        }
        if (device != null) {
            paramCount++;
            query += " AND b.device = ?" + paramCount + " ";
            params.put(paramCount, device);
        }

        Query q = em.createQuery(query);

        q.setParameter(1, start);
        q.setParameter(2, DateUtil.getEndOfDay(end));
        q.setParameter(3, company);

        for (Map.Entry<Integer, Object> entry : params.entrySet()) {
            Integer paramId = entry.getKey();
            Object object = entry.getValue();
            q.setParameter(paramId, object);
        }

        List<BluetoothFileSendSummary> results = (List<BluetoothFileSendSummary>) q.getResultList();
        return results;
    }

    @Override
    public List<BluetoothDaySummary> fetchBluetoothDaySummaries(Date start, Date end, Company company, Campaign campaign, Device device) {

        if (end == null || start.getTime() == end.getTime()) {
            end = start;
        }

        String query = "SELECT b FROM BluetoothDaySummary b WHERE b.eventDate BETWEEN ?1 AND ?2 AND b.company = ?3 ";
        int paramCount = 3;
        HashMap<Integer, Object> params = new HashMap<Integer, Object>();
        if (campaign != null) {
            paramCount++;
            query += " AND b.campaign = ?" + paramCount + " ";
            params.put(paramCount, campaign);
        }
        if (device != null) {
            paramCount++;
            query += " AND b.device = ?" + paramCount + " ";
            params.put(paramCount, device);
        }

        Query q = em.createQuery(query);

        q.setParameter(1, start);
        q.setParameter(2, DateUtil.getEndOfDay(end));
        q.setParameter(3, company);

        for (Map.Entry<Integer, Object> entry : params.entrySet()) {
            Integer paramId = entry.getKey();
            Object object = entry.getValue();
            q.setParameter(paramId, object);
        }

        List<BluetoothDaySummary> results = (List<BluetoothDaySummary>) q.getResultList();
        return results;
    }

    @Override
    public void createOrUpdateBluetoothDaySummary(BluetoothDaySummary entry) {
        if (entry.getId() != null) {
            logger.debug("updating existing BluetoothDaySummary entry");
            updateBluetoothDaySummary(entry);
        } else {
            logger.debug("creating new BluetoothDaySummary entry");
            createBluetoothDaySummary(entry);
        }
    }

    @Override
    public void createOrUpdateBluetoothFileSendSummary(BluetoothFileSendSummary entry) {
        if (entry.getId() != null) {
            logger.debug("updating existing BluetoothFileSendSummary entry");
            updateBluetoothFileSendSummary(entry);
        } else {
            logger.debug("creating new BluetoothFileSendSummary entry");
            createBluetoothFileSendSummary(entry);
        }
    }

    @Override
    public List<BluetoothSend> getBluetoothSendInRange(Date start, Date end, Company c) {
        Query q;
        if (start == null || end == null) {
            q = em.createQuery("SELECT s FROM BluetoothSend s WHERE s.company = ?1");
            q.setParameter(1, c);
        } else {
            q = em.createQuery("SELECT s FROM BluetoothSend s WHERE s.company = ?1 AND s.eventDate BETWEEN ?2 AND ?3");
            q.setParameter(1, c);
            q.setParameter(2, start, TemporalType.TIMESTAMP);
            q.setParameter(3, end, TemporalType.TIMESTAMP);
        }
        List<BluetoothSend> results = (List<BluetoothSend>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public List<BluetoothSend> getBluetoothSendInRangeByCampaignAndDevice(Date start, Date end, Company company, Campaign campaign, Device device) {
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
        if (start != null ) {
            paramCount++;
            query += " AND s.eventDate >= ?" + paramCount + " ";
            params.put(paramCount, start);
        }
        if (end != null ) {
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
        
        List<BluetoothSend> results = (List<BluetoothSend>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public List<BluetoothDwell> getBluetoothDwellInRange(Date start, Date end, Company c) {
        Query q;
        if (start == null || end == null) {
            q = em.createQuery("SELECT bdwell FROM BluetoothDwell bdwell WHERE bdwell.company = ?1");
            q.setParameter(1, c);
        } else {
            q = em.createQuery("SELECT bdwell FROM BluetoothDwell bdwell WHERE bdwell.company = ?1 AND bdwell.eventDate BETWEEN ?2 AND ?3");
            q.setParameter(1, c);
            q.setParameter(2, start, TemporalType.TIMESTAMP);
            q.setParameter(3, end, TemporalType.TIMESTAMP);
        }
        List<BluetoothDwell> results = (List<BluetoothDwell>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public List<BluetoothDwell> getBluetoothDwellInRangeByCampaign(Date start, Date end, Company c, Campaign camp) {
        Query q;
        if (start == null || end == null) {
            q = em.createQuery("SELECT bdwell FROM BluetoothDwell bdwell WHERE bdwell.company = ?1 and bdwell.campaign = ?2");
            q.setParameter(1, c);
            q.setParameter(2, camp);
        } else {
            q = em.createQuery("SELECT bdwell FROM BluetoothDwell bdwell WHERE bdwell.company = ?1 AND bdwell.eventDate BETWEEN ?2 AND ?3 AND bdwell.campaign = ?4");
            q.setParameter(1, c);
            q.setParameter(2, start, TemporalType.TIMESTAMP);
            q.setParameter(3, end, TemporalType.TIMESTAMP);
            q.setParameter(4, camp);
        }
        List<BluetoothDwell> results = (List<BluetoothDwell>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    /**
     *
     * This uses a native query because you can't use the DATE() function in
     * MySQL in a JPA query
     *
     * @param start startDate
     * @param end endDate end date ( uses the whole day via
     * DateUtil.getNextDay() )
     * @param c Company
     * @param campaign Campaign (optional)
     * @param device Device (optional)
     * @return
     */
    @Override
    public List<BluetoothFileSendSummary> getBluetoothFileSendSummaryFromBluetoothSend(Date start, Date end, Company company, Campaign campaign, Device device) {

        int count = 4;
        HashMap<Integer, Object> parameters = new HashMap<Integer, Object>();

        String query = "SELECT DATE(event_date), file, count(*) "
                + "FROM bluetooth_send "
                + "WHERE send_status = 1 "
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

        query += "GROUP BY file, DATE(event_date) ORDER BY DATE(event_date)";

        Query q = em.createNativeQuery(query);
        q.setParameter(1, company.getId());
        q.setParameter(2, start);
        q.setParameter(3, DateUtil.getNextDay(end));

        for (Map.Entry<Integer, Object> entry : parameters.entrySet()) {
            Integer position = entry.getKey();
            Object param = entry.getValue();
            q.setParameter(position, param);
        }

        List<BluetoothFileSendSummary> results = new ArrayList<BluetoothFileSendSummary>();

        List<Object[]> result = q.getResultList();
        for (Object[] object : result) {
            Date eventDate = (Date) object[0];
            String file = (String) object[1];
            Long sendCount = (Long) object[2];

            BluetoothFileSendSummary item = new BluetoothFileSendSummary();
            item.setEventDate(eventDate);
            item.setFile(file);
            item.setSendCount(sendCount);
            item.setCompany(company);
            item.setCampaign(campaign);
            item.setDevice(device);

            results.add(item);
        }
        return results;
    }
}
