/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms;

import com.proximus.data.Brand;
import com.proximus.data.Company;
import com.proximus.data.sms.*;
import com.proximus.data.util.DateUtil;
import com.proximus.manager.AbstractManager;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;

/**
 *
 * @author ronald
 */
@Stateless
public class MobileOfferSendLogManager extends AbstractManager<MobileOfferSendLog> implements MobileOfferSendLogManagerLocal {

    private static final Logger logger = Logger.getLogger(MobileOfferSendLog.class.getName());
    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public MobileOfferSendLogManager() {
        super(MobileOfferSendLog.class);
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
    public List<MobileOfferSendLog> findByMonthAndCompanyAndSubscriber(Company company, Date date, Subscriber subscriber) {
        Query query = em.createQuery("SELECT l FROM MobileOfferSendLog l WHERE l.company = ?1 AND l.subscriber = ?2 AND l.eventDate BETWEEN ?3 AND ?4 ORDER BY l.eventDate DESC");
        query.setParameter(1, company);
        query.setParameter(2, subscriber);
        query.setParameter(3, DateUtil.getFirstDayOfMonth(date));
        query.setParameter(4, DateUtil.getEndOfDay(date));

        List<MobileOfferSendLog> results = (List<MobileOfferSendLog>) query.getResultList();
        return results;
    }

    @Override
    public MobileOfferSendLog findMostRecentByCompanyAndSubscriber(Company company, Subscriber subscriber) {
        Query query = em.createQuery("SELECT l FROM MobileOfferSendLog l WHERE l.company = ?1 AND l.subscriber = ?2 ORDER BY l.eventDate DESC");
        query.setParameter(1, company);
        query.setParameter(2, subscriber);

        List<MobileOfferSendLog> results = (List<MobileOfferSendLog>) query.getResultList();
        if (results != null && results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public MobileOfferSendLog findMostRecentDeliveredByCompany(Company company) {
        Query query = em.createQuery("SELECT l FROM MobileOfferSendLog l WHERE l.company = :company AND l.status = :status ORDER BY l.eventDate DESC");
        query.setParameter("company", company);
        query.setParameter("status", MobileOfferSendLog.STATUS_DELIVERED);
        query.setMaxResults(1);

        List<MobileOfferSendLog> results = (List<MobileOfferSendLog>) query.getResultList();
        if (results != null && results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public List<MobileOfferSendLog> findByMonthAndSubscriber(Date date, Subscriber subscriber) {
        Query query = em.createQuery("SELECT l FROM MobileOfferSendLog l WHERE l.subscriber = ?1 AND l.eventDate BETWEEN ?2 AND ?3 ORDER BY l.eventDate DESC");
        query.setParameter(1, subscriber);
        query.setParameter(2, DateUtil.getFirstDayOfMonth(date));
        query.setParameter(3, DateUtil.getEndOfDay(date));

        List<MobileOfferSendLog> results = (List<MobileOfferSendLog>) query.getResultList();
        return results;
    }

    @Override
    public MobileOfferSendLog findMostRecentBySubscriber(Subscriber subscriber) {
        Query query = em.createQuery("SELECT l FROM MobileOfferSendLog l WHERE l.subscriber = ?1 ORDER BY l.eventDate DESC");
        query.setParameter(1, subscriber);

        List<MobileOfferSendLog> results = (List<MobileOfferSendLog>) query.getResultList();
        if (results != null && results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public Long getTotalMessagesSentByCompanyAndDateRange(Company company, Date start, Date end) {
        Query query = em.createQuery("SELECT COUNT(l.id) FROM MobileOfferSendLog l WHERE l.company = ?1 AND l.eventDate BETWEEN ?2 AND ?3");
        query.setParameter(1, company);
        query.setParameter(2, start);
        query.setParameter(3, end);
        Long countResult;
        try {
            countResult = (Long) query.getSingleResult();
        } catch (Exception ex) {
            countResult = 0L;
        }
        return countResult;
    }

    @Override
    public Long getTotalMessagesSentByCompanyAndSubscriber(Company company, Date date, Subscriber subscriber) {
        Query query = em.createQuery("SELECT COUNT(l.id) FROM MobileOfferSendLog l WHERE l.company = ?1 AND l.subscriber = ?2 AND l.eventDate BETWEEN ?3 AND ?4");
        query.setParameter(1, company);
        query.setParameter(2, subscriber);
        query.setParameter(3, DateUtil.getFirstDayOfMonth(date));
        query.setParameter(4, DateUtil.getEndOfDay(date));
        Long countResult = (Long) query.getSingleResult();
        return countResult;
    }

    @Override
    public Long getTotalMessagesSentBySubscriber(Date date, Subscriber subscriber) {
        Query query = em.createQuery("SELECT COUNT(l.id) FROM MobileOfferSendLog l WHERE l.subscriber = ?1 AND l.eventDate BETWEEN ?2 AND ?3");
        query.setParameter(1, subscriber);
        query.setParameter(2, DateUtil.getFirstDayOfMonth(date));
        query.setParameter(3, DateUtil.getEndOfDay(date));
        Long countResult = (Long) query.getSingleResult();
        return countResult;
    }

    @Override
    public Long getTotalMessagesSentByDateRangeAndStatusAndCompany(Date startDate, Date endDate, Company company, String status) {
        Query query;
        if (status == null) {
            query = em.createQuery("SELECT COUNT(m.id) FROM MobileOfferSendLog m WHERE m.eventDate BETWEEN ?1 AND ?2 AND m.company = ?3");
            query.setParameter(1, DateUtil.getStartOfDay(startDate));
            query.setParameter(2, DateUtil.getEndOfDay(endDate));
            query.setParameter(3, company);
        } else {
            query = em.createQuery("SELECT COUNT(m.id) FROM MobileOfferSendLog m WHERE m.eventDate BETWEEN ?1 AND ?2 AND m.company = ?3 AND m.status =?4");
            query.setParameter(1, DateUtil.getStartOfDay(startDate));
            query.setParameter(2, DateUtil.getEndOfDay(endDate));
            query.setParameter(3, company);
            query.setParameter(4, status.toUpperCase());
        }
        Long countResult = (Long) query.getSingleResult();
        return countResult;
    }

    @Override
    public Long getTotalMessagesSentByDateRangeAndStatusAndBrand(Date startDate, Date endDate, Brand brand, String status) {
        Query query;
        if (status == null) {
            query = em.createQuery("SELECT COUNT(m.id) FROM MobileOfferSendLog m WHERE m.eventDate BETWEEN ?1 AND ?2 AND m.brand = ?3");
            query.setParameter(1, DateUtil.getStartOfDay(startDate));
            query.setParameter(2, DateUtil.getEndOfDay(endDate));
            query.setParameter(3, brand);
        } else {
            query = em.createQuery("SELECT COUNT(m.id) FROM MobileOfferSendLog m WHERE m.eventDate BETWEEN ?1 AND ?2 AND m.brand = ?3 AND m.status =?4");
            query.setParameter(1, DateUtil.getStartOfDay(startDate));
            query.setParameter(2, DateUtil.getEndOfDay(endDate));
            query.setParameter(3, brand);
            query.setParameter(4, status.toUpperCase());
        }
        Long countResult = (Long) query.getSingleResult();
        return countResult;
    }

    @Override
    public List<MobileOfferSendLog> getMobileOffersInRange(Company company, Subscriber subscriber, Date startDate, Date endDate) {
        Query query = em.createQuery("SELECT m FROM MobileOfferSendLog m WHERE m.company = ?1 AND m.subscriber = ?2 AND m.status = ?3 AND m.eventDate BETWEEN ?4 AND ?5ORDER BY m.eventDate DESC");
        query.setParameter(1, company);
        query.setParameter(2, subscriber);
        query.setParameter(3, MobileOfferSendLog.STATUS_DELIVERED);
        query.setParameter(4, DateUtil.getStartOfDay(startDate));
        query.setParameter(5, DateUtil.getEndOfDay(endDate));
        List<MobileOfferSendLog> results = (List<MobileOfferSendLog>) query.getResultList();
        if (results != null) {
            return results;
        }
        return null;
    }

    @Override
    public List<MobileOfferSendLog> fetchMobileOfferSendLog(Date start, Date end, Company company, DMA dma, Property property, Retailer retailer) {
        if (end == null || start.getTime() == end.getTime()) {
            end = start;
        }
        String query = "SELECT m FROM MobileOfferSendLog m WHERE m.eventDate BETWEEN ?1 AND ?2 AND m.company = ?3";
        int paramCount = 3;
        HashMap<Integer, Object> params = new HashMap<Integer, Object>();
        if (dma != null && dma.getName() != null) {
            paramCount++;
            query += " " + "AND m.property.dma = ?" + paramCount;
            params.put(paramCount, dma);
        }

        if (property != null && property.getName() != null) {
            paramCount++;
            query += " " + "AND m.property =  ?" + paramCount;
            params.put(paramCount, property);
        }
        if (retailer != null && retailer.getName() != null) {
            paramCount++;
            query += " " + "AND m.mobileOffer.retailer = ?" + paramCount;
            params.put(paramCount, retailer);
        }

        Query q = em.createQuery(query);
        for (Map.Entry<Integer, Object> entry : params.entrySet()) {
            Integer paramId = entry.getKey();
            Object object = entry.getValue();
            q.setParameter(paramId, object);
        }
        q.setParameter(1, start);
        q.setParameter(2, DateUtil.getEndOfDay(end));
        q.setParameter(3, company);
        List<MobileOfferSendLog> results = (List<MobileOfferSendLog>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public Long findSendLogsByCompanyAndDate(Company company, Date eventDate, Date endOfDay) {
        Query q = em.createQuery("SELECT COUNT(m.id) FROM MobileOfferSendLog m WHERE m.company = ?1 AND m.status = ?2 AND m.eventDate BETWEEN ?3 AND ?4");
        q.setParameter(1, company);
        q.setParameter(2, MobileOfferSendLog.STATUS_DELIVERED);
        q.setParameter(3, eventDate);
        q.setParameter(4, endOfDay);
        Long countResult;
        try {
            countResult = (Long) q.getSingleResult();
        } catch (Exception ex) {
            countResult = 0L;
        }
        return countResult;
    }

    @Override
    public List<Date> findSendLogDatesByCompanyAndDate(Company company, Date eventDate, Date endOfDay) {
        Query q = em.createNativeQuery("SELECT DISTINCT(DATE(event_date)) FROM mobile_offer_send_log WHERE company_id = ?1 AND status = ?2 AND event_date "
                + "BETWEEN ?3 AND ?4");
        q.setParameter(1, company.getId());
        q.setParameter(2, MobileOfferSendLog.STATUS_DELIVERED);
        q.setParameter(3, eventDate);
        q.setParameter(4, endOfDay);
        List<Date> results = (List<Date>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public Long findTotalSendLogsByCompanyAndDate(Company company, Date eventDate) {
        Query q = em.createQuery("SELECT COUNT(m.id) FROM MobileOfferSendLog m WHERE m.company = ?1 AND m.status = ?2 AND m.eventDate < ?3");
        q.setParameter(1, company);
        q.setParameter(2, MobileOfferSendLog.STATUS_DELIVERED);
        q.setParameter(3, eventDate);
        Long countResult;
        try {
            countResult = (Long) q.getSingleResult();
        } catch (Exception ex) {
            countResult = 0L;
        }
        return countResult;
    }

    @Override
    public Long findSendLogsByCompany(Company company) {
        Query q = em.createQuery("SELECT COUNT(m.id) FROM MobileOfferSendLog m WHERE m.company = ?1 AND m.status = ?2");
        q.setParameter(1, company);
        q.setParameter(2, MobileOfferSendLog.STATUS_DELIVERED);
        Long countResult;
        try {
            countResult = (Long) q.getSingleResult();
        } catch (Exception ex) {
            countResult = 0L;
        }
        return countResult;
    }

    @Override
    public Map<String, Number> getSendLogMap(Company company, Date startDate, Date endDate) {
        Query query = this.em.createQuery("SELECT m.mobileOffer.name, count(m) FROM MobileOfferSendLog m WHERE  m.company = ?1 AND m.eventDate BETWEEN ?2 AND ?3 "
                + "GROUP BY m.mobileOffer.name ORDER BY m.mobileOffer.name DESC");
        query.setParameter(1, company);
        query.setParameter(2, DateUtil.getStartOfDay(startDate));
        query.setParameter(3, DateUtil.getNextDay(endDate));
        Map<String, Number> systemMessageMap = new HashMap<String, Number>();
        List<Object[]> results = query.getResultList();
        for (Object[] object : results) {
            String offer = (String) object[0];
            Long numberOfOffersSent = (Long) object[1];
            systemMessageMap.put(offer, numberOfOffersSent);
        }
        if (systemMessageMap.size() > 0) {
            return systemMessageMap;
        }
        return null;
    }

    @Override
    public Long getDistinctSubscriberCount(Company company, Date startDate, Date endDate) {
        Long count;
        Query q = em.createQuery("SELECT COUNT(DISTINCT l.msisdn) "
                + "FROM MobileOfferSendLog l "
                + "WHERE l.company = ?1 "
                + "AND l.eventDate BETWEEN ?2 AND ?3");
        q.setParameter(1, company);
        q.setParameter(2, DateUtil.getStartOfDay(startDate));
        q.setParameter(3, DateUtil.getEndOfDay(endDate));
        try {
            count = (Long) q.getSingleResult();
        } catch (Exception ex) {
            count = 0L;
        }
        return count;
    }

    @Override
    public Long countMessagesSentByOffer(MobileOffer offer, Date startDate, Date endDate, Property property) {
        String queryString = "SELECT l FROM MobileOfferSendLog l WHERE l.mobileOffer = :mobileOffer and l.status = :status";

        if (startDate != null && endDate != null) {
            queryString += " AND l.eventDate BETWEEN ?2 AND ?3";
        }
        if (startDate != null && endDate == null) {
            queryString += " AND l.eventDate >= ?2";
        }
        if (startDate == null && endDate != null) {
            queryString += " AND l.eventDate <= ?3";
        }

        if (property != null) {
            queryString += " AND l.property = :property";
        }

        Query query = em.createQuery(queryString);

        query.setParameter("mobileOffer", offer);
        query.setParameter("status", MobileOfferSendLog.STATUS_DELIVERED);
        if (startDate != null) {
            query.setParameter(2, DateUtil.getFirstDayOfMonth(startDate));

        }
        if (endDate != null) {
            query.setParameter(3, DateUtil.getEndOfDay(endDate));
        }

        if (property != null) {
            query.setParameter("property", property);
        }

        List<MobileOfferSendLog> results = (List<MobileOfferSendLog>) query.getResultList();
        if (results == null) {
            return 0L;
        }
        long count = results.size();
        return count;
    }
}
