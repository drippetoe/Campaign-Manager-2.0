/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms;

import com.proximus.data.Brand;
import com.proximus.data.Company;
import com.proximus.data.sms.Keyword;
import com.proximus.data.sms.Subscriber;
import com.proximus.data.util.DateUtil;
import com.proximus.manager.AbstractManager;
import java.math.BigDecimal;
import java.util.*;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import org.joda.time.DateTime;

/**
 *
 * @author ronald
 */
@Stateless
public class SubscriberManager extends AbstractManager<Subscriber> implements SubscriberManagerLocal {

    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public SubscriberManager() {
        super(Subscriber.class);
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
    public List<Subscriber> findAllByCompany(Company company) {
        Query q = em.createQuery("SELECT u FROM Subscriber u WHERE u.company = ?1 ORDER BY u.id");
        q.setParameter(1, company);
        List<Subscriber> results = (List<Subscriber>) q.getResultList();

        return results;
    }

    @Override
    public List<Subscriber> findAllByCompanyStatusNotFailed(Company company) {
        Query q = em.createQuery("SELECT u FROM Subscriber u WHERE u.company = ?1 AND (u.status = ?2 OR u.status = ?3) ORDER BY u.id");
        q.setParameter(1, company);
        List<Subscriber> results = (List<Subscriber>) q.getResultList();
        q.setParameter(2, Subscriber.STATUS_OPT_IN_COMPLETE);
        q.setParameter(3, Subscriber.STATUS_OPT_OUT);
        if (results == null) {
            return new ArrayList<Subscriber>();
        }
        return results;
    }

    @Override
    public Long countAllByCompany(Company company) {
        Query q = em.createQuery("SELECT count(s) FROM Subscriber s WHERE s.company = :company");
        q.setParameter("company", company);
        Long countResult = 0L;
        try {
            countResult = (Long) q.getSingleResult();
        } catch (Exception ex) {
            countResult = 0L;
        }
        return countResult;
    }

    @Override
    public List<Subscriber> findAllByBrandAndOptIn(Brand brand) {
        Query q = em.createQuery("SELECT u FROM Subscriber u WHERE u.brand = ?1 AND u.status = ?2 ORDER BY u.id");
        q.setParameter(1, brand);
        q.setParameter(2, Subscriber.STATUS_OPT_IN_COMPLETE);
        List<Subscriber> results = (List<Subscriber>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public List<Subscriber> findAllByCompanyAndOptIn(Company company) {
        Query q = em.createQuery("SELECT u FROM Subscriber u WHERE u.company = ?1 AND u.status = ?2 ORDER BY u.id");
        q.setParameter(1, company);
        q.setParameter(2, Subscriber.STATUS_OPT_IN_COMPLETE);
        List<Subscriber> results = (List<Subscriber>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public Subscriber findByMsisdn(String msisdn) {
        Query q = em.createQuery("SELECT s FROM Subscriber s WHERE s.msisdn = ?1");
        q.setParameter(1, msisdn);
        List<Subscriber> results = (List<Subscriber>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    /**
     * Find all opted-in subscribers for a company needing lookup based on time
     *
     * @param company company to check
     * @return list of subscribers, ordered by least-recently looked-up first
     */
    @Override
    public List<Subscriber> findSubscribersNeedingLocation(Brand brand) {
        Query q = em.createQuery("SELECT s FROM Subscriber s WHERE s.brand = ?1 AND (s.nextLookupRequest IS NULL OR s.nextLookupRequest <= ?2) and s.status = ?3 ORDER BY s.nextLookupRequest");
        q.setParameter(1, brand);
        q.setParameter(2, new Date());
        q.setParameter(3, Subscriber.STATUS_OPT_IN_COMPLETE);
        List<Subscriber> results = (List<Subscriber>) q.getResultList();
        return results;
    }

    @Override
    public Long findOptedInCountByCompany(Company company) {
        Query q = em.createQuery("SELECT COUNT(s.id) FROM Subscriber s WHERE s.company = ?1 AND s.status = ?2");
        q.setParameter(1, company);
        q.setParameter(2, Subscriber.STATUS_OPT_IN_COMPLETE);
        Long countResult = 0L;
        try {
            countResult = (Long) q.getSingleResult();
        } catch (Exception ex) {
            countResult = 0L;
        }
        return countResult;
    }

    @Override
    public Long findOptedInCountByBrand(Brand brand) {
        Query q = em.createQuery("SELECT COUNT(s.id) FROM Subscriber s WHERE s.brand = ?1 AND s.status = ?2");
        q.setParameter(1, brand);
        q.setParameter(2, Subscriber.STATUS_OPT_IN_COMPLETE);
        Long countResult = 0L;
        try {
            countResult = (Long) q.getSingleResult();
        } catch (Exception ex) {
            countResult = 0L;
        }
        return countResult;
    }

    @Override
    public Long findOptedInCountByCompanyAndDate(Company company, Date eventDate, Date endOfDay) {
        Query q = em.createQuery("SELECT COUNT(s.id) FROM Subscriber s WHERE s.company = ?1 AND s.status = ?2 AND s.smsOptInDate BETWEEN ?3 AND ?4");
        q.setParameter(1, company);
        q.setParameter(2, Subscriber.STATUS_OPT_IN_COMPLETE);
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
    public Long findOptedInCountByBrandAndDate(Brand brand, Date eventDate, Date endOfDay) {
        Query q = em.createQuery("SELECT COUNT(s.id) FROM Subscriber s WHERE s.brand = ?1 AND s.status = ?2 AND s.smsOptInDate BETWEEN ?3 AND ?4");
        q.setParameter(1, brand);
        q.setParameter(2, Subscriber.STATUS_OPT_IN_COMPLETE);
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
    public Long findTotalOptedInCountByCompanyAndDate(Company company, Date eventDate) {
        Query q = em.createQuery("SELECT COUNT(s.id) FROM Subscriber s WHERE s.company = ?1 AND s.status = ?2 AND s.smsOptInDate < ?3");
        q.setParameter(1, company);
        q.setParameter(2, Subscriber.STATUS_OPT_IN_COMPLETE);
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
    public Long findTotalOptedInCountByBrandAndDate(Brand brand, Date eventDate) {
        Query q = em.createQuery("SELECT COUNT(s.id) FROM Subscriber s WHERE s.brand = ?1 AND s.status = ?2 AND s.smsOptInDate < ?3");
        q.setParameter(1, brand);
        q.setParameter(2, Subscriber.STATUS_OPT_IN_COMPLETE);
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
    public List<Subscriber> findAllByCompanyAndStatus(Company company, String status) {
        Query q = em.createQuery("SELECT u FROM Subscriber u WHERE u.company = ?1 AND u.status = ?2 ORDER BY u.id");
        q.setParameter(1, company);
        q.setParameter(2, status);
        List<Subscriber> results = (List<Subscriber>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return results;
    }

    @Override
    public List<Subscriber> findAllSubscribersByKeywordAndStatus(Company company, Keyword keyword, String status) {
        Query q = em.createQuery("SELECT s FROM Subscriber s WHERE s.company = ?1 AND s.keyword = ?2 AND s.status = ?3");
        q.setParameter(1, company);
        q.setParameter(2, keyword);
        q.setParameter(3, status);
        List<Subscriber> results = (List<Subscriber>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;

    }

    @Override
    public List<Subscriber> findAllSubscribersByKeywordAndStatusAndOptInDate(Company company, Keyword keyword, Date smsOptInDate, String status) {
        Query q = em.createQuery("SELECT s FROM Subscriber s WHERE s.company = ?1 AND s.keyword = ?2 AND s.status = ?3 AND s.smsOptInDate BETWEEN ?4 AND ?5");
        q.setParameter(1, company);
        q.setParameter(2, keyword);
        q.setParameter(3, status);
        q.setParameter(4, DateUtil.getStartOfDay(smsOptInDate), TemporalType.DATE);
        q.setParameter(5, DateUtil.getNextDay(smsOptInDate), TemporalType.DATE);
        List<Subscriber> results = (List<Subscriber>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public List<Subscriber> findAllSubscribersByKeywordAndOptOutDate(Company company, Keyword keyword, Date smsOptOutDate) {
        Query q = em.createQuery("SELECT s FROM Subscriber s WHERE s.company = ?1 AND s.keyword = ?2 AND s.status = ?3 AND s.smsOptOutDate BETWEEN ?4 AND ?5");
        q.setParameter(1, company);
        q.setParameter(2, keyword);
        q.setParameter(3, Subscriber.STATUS_OPT_OUT);
        q.setParameter(4, DateUtil.getStartOfDay(smsOptOutDate), TemporalType.DATE);
        q.setParameter(5, DateUtil.getNextDay(smsOptOutDate), TemporalType.DATE);
        List<Subscriber> results = (List<Subscriber>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public Map<String, Number> getSupportedCarrierSubscriberMap(Company company) {
        Query query = this.em.createQuery("SELECT s.carrier.name, count(s) FROM Subscriber s WHERE s.carrier IS NOT NULL AND s.status != ?1 AND "
                + "s.status != ?2 AND s.company = ?3 GROUP BY s.carrier.name ORDER BY s.carrier.name DESC");
        query.setParameter(1, Subscriber.STATUS_CARRIER_UNSUPPORTED);
        query.setParameter(2, Subscriber.STATUS_OPT_OUT);
        query.setParameter(3, company);
        Map<String, Number> carrierSubscriberMap = new HashMap<String, Number>();
        List<Object[]> results = query.getResultList();
        for (Object[] object : results) {
            String carrier = (String) object[0];
            Long numberOfSubscribers = (Long) object[1];
            carrierSubscriberMap.put(carrier, numberOfSubscribers);
        }
        if (carrierSubscriberMap.size() > 0) {
            return carrierSubscriberMap;
        }
        return null;
    }

    @Override
    public Map<String, Number> getUnSupportedCarrierSubscriberMap(Company company) {
        Query query = this.em.createQuery("SELECT s.carrier.name, count(s) FROM Subscriber s WHERE s.carrier IS NOT NULL AND s.status = ?1 AND "
                + "s.company = ?2 GROUP BY s.carrier.name ORDER BY s.carrier.name DESC");
        query.setParameter(1, Subscriber.STATUS_CARRIER_UNSUPPORTED);
        query.setParameter(2, company);
        Map<String, Number> carrierSubscriberMap = new HashMap<String, Number>();
        List<Object[]> results = query.getResultList();
        for (Object[] object : results) {
            String carrier = (String) object[0];
            Long numberOfSubscribers = (Long) object[1];
            carrierSubscriberMap.put(carrier, numberOfSubscribers);
        }
        if (carrierSubscriberMap.size() > 0) {
            return carrierSubscriberMap;
        }
        return null;
    }

    @Override
    public Map<String, Number> getSubscriberStatuses(Company company) {
        Query query = this.em.createQuery("SELECT s.status, count(s) FROM Subscriber s WHERE s.company = :company GROUP BY s.status");
        query.setParameter("company", company);
        Map<String, Number> carrierSubscriberMap = new HashMap<String, Number>();
        List<Object[]> results = query.getResultList();
        for (Object[] object : results) {
            String status = (String) object[0];
            Long numberOfSubscribers = (Long) object[1];
            carrierSubscriberMap.put(status, numberOfSubscribers);
        }
        if (carrierSubscriberMap.size() > 0) {
            return carrierSubscriberMap;
        }
        return null;
    }

    @Override
    public long countedActiveSubscribersBetweenDatesByCompany(Date startDate, Date endDate, Company c) {
        long totalOptedIn = 0L;
        Query queryOne = em.createQuery("SELECT COUNT(s.id) FROM Subscriber s WHERE s.smsOptOutDate "
                + "BETWEEN ?1 AND ?2 "
                + "AND s.company = ?3 "
                + "AND s.status = ?4");
        queryOne.setParameter(1, DateUtil.getStartOfDay(startDate));
        queryOne.setParameter(2, DateUtil.getEndOfDay(endDate));
        queryOne.setParameter(3, c);
        queryOne.setParameter(4, Subscriber.STATUS_OPT_OUT);
        Query queryTwo = em.createQuery("SELECT COUNT(s.id) FROM Subscriber s "
                + "WHERE s.status = ?1 "
                + "AND s.company = ?2 "
                + "AND s.smsOptInDate "
                + "<= ?3");
        queryTwo.setParameter(1, Subscriber.STATUS_OPT_IN_COMPLETE);
        queryTwo.setParameter(2, c);
        queryTwo.setParameter(3, endDate);
        long optedOut = (Long) queryOne.getSingleResult();
        long optedIn = (Long) queryTwo.getSingleResult();
        totalOptedIn = optedOut + optedIn;

        return totalOptedIn;
    }

    @Override
    public long countOptedOutByCompanyBetweenDates(Company c, Date startDate, Date endDate) {
        long totalCount = 0L;
        Query q = em.createQuery("SELECT COUNT(s.id) from Subscriber s "
                + "WHERE s.company = ?1 "
                + "AND s.smsOptOutDate "
                + "BETWEEN ?2 AND ?3 "
                + "AND s.status = ?4");
        q.setParameter(1, c);
        q.setParameter(2, startDate);
        q.setParameter(3, endDate);
        q.setParameter(4, Subscriber.STATUS_OPT_OUT);
        totalCount = (Long) q.getSingleResult();
        return totalCount;
    }

    @Override
    public List<Subscriber> findAllUnsynchedSubscribers() {
        Query q = em.createQuery("SELECT s FROM Subscriber s WHERE s.synchedWithLocaid = ?1 ORDER BY s.registrationDate DESC");
        q.setParameter(1, false);
        List<Subscriber> results = (List<Subscriber>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public List<Subscriber> findActiveSubscribersWithLateLookups(Date minimumLookup) {
        Query q = em.createQuery("SELECT s FROM Subscriber s WHERE (s.nextLookupRequest IS NULL OR s.nextLookupRequest <= ?1) AND s.status = ?2 ORDER BY s.registrationDate DESC");
        q.setParameter(1, minimumLookup);
        q.setParameter(2, Subscriber.STATUS_OPT_IN_COMPLETE);
        List<Subscriber> results = (List<Subscriber>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public Long getNrOfSupportedCarriers(Company company) {
        Query query = this.em.createQuery("SELECT count(s) FROM Subscriber s WHERE s.carrier IS NOT NULL AND s.status != ?1 AND "
                + "s.status != ?2 AND s.company = ?3");
        query.setParameter(1, Subscriber.STATUS_CARRIER_UNSUPPORTED);
        query.setParameter(2, Subscriber.STATUS_OPT_OUT);
        query.setParameter(3, company);
        Long result;
        try {
            result = (Long) query.getSingleResult();
        } catch (Exception e) {
            result = 0L;
        }
        return result;
    }

    @Override
    public Long getNrOfUnSupportedCarriers(Company company) {
        Query query = this.em.createQuery("SELECT count(s) FROM Subscriber s WHERE s.carrier IS NOT NULL AND s.status = ?1 AND "
                + "s.company = ?2 ");
        query.setParameter(1, Subscriber.STATUS_CARRIER_UNSUPPORTED);
        query.setParameter(2, company);
        Long result;
        try {
            result = (Long) query.getSingleResult();
        } catch (Exception e) {
            result = 0L;
        }
        return result;
    }

    @Override
    public Double getAverageMessagesSentByBrandAndDate(Brand brand, Date startDate, Date endDate) {
        String queryString = "SELECT AVG(a.rcount) FROM (";
        queryString += " SELECT count(subscriber_id) as rcount";
        queryString += " FROM mobile_offer_send_log ";
        queryString += " WHERE brand_id = ?1";

        if (startDate != null && endDate != null) {
            queryString += " AND event_date BETWEEN ?2 AND ?3";
        }
        if (startDate != null && endDate == null) {
            queryString += " AND event_date >= ?2";
        }
        if (startDate == null && endDate != null) {
            queryString += " AND event_date <= ?3";
        }

        queryString += " GROUP BY subscriber_id) a";
        Query q = em.createNativeQuery(queryString);
        q.setParameter("1", brand.getId());
        if (startDate != null) {
            q.setParameter("2", DateUtil.getStartOfDay(startDate));
        }
        if (endDate != null) {
            q.setParameter("3", DateUtil.getEndOfDay(endDate));
        }

        Object result = q.getSingleResult();
        if (result == null) {
            return 0.0;
        }
        BigDecimal avgMsg = (BigDecimal) result;
        return avgMsg.doubleValue();

    }

    @Override
    public Double getAverageDaysBetweenMessagesByBrandAndDate(Brand brand, Date startDate, Date endDate) {
        String queryString = "SELECT AVG(a.average_days) FROM (";
        queryString += " SELECT IFNULL(TIMESTAMPDIFF(DAY, MIN(event_date), MAX(event_date)) / (COUNT(DISTINCT(event_date)) - 1),0) AS average_days";
        queryString += " FROM mobile_offer_send_log ";
        queryString += " WHERE brand_id = ?1";

        if (startDate != null && endDate != null) {
            queryString += " AND event_date BETWEEN ?2 AND ?3";
        }
        if (startDate != null && endDate == null) {
            queryString += " AND event_date >= ?2";
        }
        if (startDate == null && endDate != null) {
            queryString += " AND event_date <= ?3";
        }

        queryString += " GROUP BY subscriber_id) a";
        Query q = em.createNativeQuery(queryString);
        q.setParameter("1", brand.getId());
        if (startDate != null) {
            q.setParameter("2", DateUtil.getStartOfDay(startDate));
        }
        if (endDate != null) {
            q.setParameter("3", DateUtil.getEndOfDay(endDate));
        }

        Object result = q.getSingleResult();
        if (result == null) {
            return 0.0;
        }
        BigDecimal avgDays = (BigDecimal) result;
        return avgDays.doubleValue();

    }
    
    @Override
    public long countOptInsForOneDay(Company company, Date day){        
        //SELECT count(id) FROM subscriber where status = 'OPT_IN_COMPLETE' AND DATE(registration_date) = DATE(DATE_SUB(NOW(), INTERVAL 1 DAY)) AND company_id = 126;
        Query query = this.em.createQuery("SELECT s FROM Subscriber s WHERE s.carrier IS NOT NULL AND s.status = :status AND s.company = :company AND s.smsOptInDate BETWEEN :startOfDay AND :endOfDay");
        query.setParameter("status", Subscriber.STATUS_OPT_IN_COMPLETE);
        query.setParameter("company", company);
        query.setParameter("startOfDay", DateUtil.getStartOfDay(day));
        query.setParameter("endOfDay", DateUtil.getEndOfDay(day));
        List resultList = query.getResultList();
        if(resultList!=null){
            return resultList.size();
        }
        return 0;
    }
    
    @Override
    public long countOptInsForMonth(Company company, Date month){        
        //SELECT count(id) FROM subscriber where status = 'OPT_IN_COMPLETE' AND DATE(registration_date) = DATE(DATE_SUB(NOW(), INTERVAL 1 DAY)) AND company_id = 126;
        Query query = this.em.createQuery("SELECT s FROM Subscriber s WHERE s.carrier IS NOT NULL AND s.status = :status AND s.company = :company AND s.smsOptInDate BETWEEN :startOfDay AND :endOfDay");
        query.setParameter("status", Subscriber.STATUS_OPT_IN_COMPLETE);
        query.setParameter("company", company);
        query.setParameter("startOfDay", DateUtil.getFirstDayOfMonth(month));
        query.setParameter("endOfDay", DateUtil.getLastDayOfMonth(month));
        List resultList = query.getResultList();
        if(resultList!=null){
            return resultList.size();
        }
        return 0;
    }
}
