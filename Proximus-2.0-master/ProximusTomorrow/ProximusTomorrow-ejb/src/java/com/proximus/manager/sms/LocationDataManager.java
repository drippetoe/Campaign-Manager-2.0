/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms;

import com.proximus.data.Brand;
import com.proximus.data.Company;
import com.proximus.data.sms.LocationData;
import com.proximus.data.sms.Subscriber;
import com.proximus.data.util.DateUtil;
import com.proximus.manager.AbstractManager;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import org.apache.log4j.Logger;

/**
 *
 * @author Angela Mercer
 */
@Stateless
public class LocationDataManager extends AbstractManager<LocationData> implements LocationDataManagerLocal {

    private static final Logger logger = Logger.getLogger(LocationDataManager.class.getName());
    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public LocationDataManager() {
        super(LocationData.class);
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
    public Long getTotalLookupsInDateRangeByBrand(Date startDate, Date endDate, Brand brand, String status) {
        Query query;
        if (status == null) {
            query = em.createQuery("SELECT COUNT(l.id) FROM LocationData l WHERE l.brand = ?1 AND l.eventDate BETWEEN ?2 AND ?3");
            query.setParameter(1, brand);
            query.setParameter(2, DateUtil.getStartOfDay(startDate));
            query.setParameter(3, DateUtil.getEndOfDay(endDate));
        } else {
            query = em.createQuery("SELECT COUNT(l.id) FROM LocationData l WHERE l.brand = ?1 AND l.eventDate BETWEEN ?2 AND ?3 AND l.status =?4");
            query.setParameter(1, brand);
            query.setParameter(2, DateUtil.getStartOfDay(startDate));
            query.setParameter(3, DateUtil.getEndOfDay(endDate));
            query.setParameter(4, status.toUpperCase());
        }
        Long countResult = (Long) query.getSingleResult();
        return countResult;
    }
    
   
    
    


    @Override
    public Long getTotalLookupsInDateRangeByMsisdn(Date startDate, Date endDate, String msisdn) {
        Query query;
        query = em.createQuery("SELECT COUNT(l.id) FROM LocationData l WHERE l.msisdn = ?1 AND l.eventDate BETWEEN ?2 AND ?3");
        query.setParameter(1, msisdn);
        query.setParameter(2, DateUtil.getStartOfDay(startDate));
        query.setParameter(3, DateUtil.getEndOfDay(endDate));

        Long countResult = (Long) query.getSingleResult();
        return countResult;
    }

    @Override
    public Long getTotalSuccessfulLookupsInDateRangeByMsisdn(Date startDate, Date endDate, String msisdn) {
        Query query;
        query = em.createQuery("SELECT COUNT(l.id) FROM LocationData l WHERE l.msisdn = ?1 AND l.eventDate BETWEEN ?2 AND ?3 AND (l.status = ?4 OR l.status = ?5)");
        query.setParameter(1, msisdn);
        query.setParameter(2, DateUtil.getStartOfDay(startDate));
        query.setParameter(3, DateUtil.getEndOfDay(endDate));
        query.setParameter(4, LocationData.STATUS_FOUND);
        query.setParameter(5, LocationData.STATUS_FOUND_FAKE);

        Long countResult = (Long) query.getSingleResult();
        return countResult;
    }


    @Override
    public Long getTotalLookupsByMsisdn(String msisdn) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Long getTotalFoundInDateRangeByBrand(Date startDate, Date endDate, Brand brand) {
        Query query;
        query = em.createQuery("SELECT COUNT(l.id) FROM LocationData l WHERE l.brand =  ?1 l.eventDate BETWEEN ?2 AND ?3 AND status = ?4");
        query.setParameter(1,brand);
        query.setParameter(2, DateUtil.getStartOfDay(startDate), TemporalType.TIMESTAMP);
        query.setParameter(3, DateUtil.getEndOfDay(endDate), TemporalType.TIMESTAMP);
        query.setParameter(4, LocationData.STATUS_FOUND);
        Long result = (Long) query.getSingleResult();
        return result;

    }

    @Override
    public List<LocationData> getAllLocationDataInDateRange(Date startDate, Date endDate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<LocationData> findAllByBrand(Brand brand) {
        Query q = em.createQuery("SELECT l FROM LocationData l WHERE l.brand = ?1 ORDER BY l.id");
        q.setParameter(1, brand);
        List<LocationData> results = (List<LocationData>) q.getResultList();

        return results;
    }

  
    
     @Override
    public LocationData findLastBySubscriber(Subscriber subscriber) {
        Query q = em.createQuery("SELECT l FROM LocationData l WHERE l.msisdn = ?1 and (l.status = ?2 or l.status = ?3) ORDER BY l.eventDate DESC");
        q.setParameter(1, subscriber.getMsisdn());
        q.setParameter(2, LocationData.STATUS_FOUND);
        q.setParameter(3, LocationData.STATUS_FOUND_FAKE);
        List<LocationData> results = (List<LocationData>) q.getResultList();
        if (results != null && results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public List<LocationData> findAllByBrandAndMsisdn(Brand brand, String msisdn) {
        Query q = em.createQuery("SELECT l FROM LocationData l WHERE l.brand = ?1 AND l.msisdn = ?2 and l.status = ?3 ORDER BY l.eventDate DESC");
        q.setParameter(1, brand);
        q.setParameter(2, msisdn);
        q.setParameter(3, LocationData.STATUS_FOUND);
        List<LocationData> results = (List<LocationData>) q.getResultList();

        return results;
    }

    @Override
    public List<LocationData> getLocationDataByBrandAndMsisdnInRange(Brand brand, String msisdn, Date startDate, Date endDate) {
        Query q = em.createQuery("SELECT l FROM LocationData l WHERE l.brand = ?1 AND l.msisdn = ?2 AND l.status = ?3 AND l.eventDate BETWEEN ?4 AND ?5 ORDER BY l.eventDate DESC");
        q.setParameter(1, brand);
        q.setParameter(2, msisdn);
        q.setParameter(3, LocationData.STATUS_FOUND);
        q.setParameter(4, DateUtil.getStartOfDay(startDate));
        q.setParameter(5, DateUtil.getEndOfDay(endDate));
        List<LocationData> results = (List<LocationData>) q.getResultList();
        if (results != null) {
            return results;
        }
        return null;
    }

    @Override
    public List<LocationData> findByBrandAndStatus(Brand brand, String status) {
        Query q = em.createQuery("SELECT l FROM LocationData l WHERE l.brand = ?1 AND l.status = ?2 ORDER BY l.eventDate DESC");
        q.setParameter(1, brand);
        q.setParameter(2, status);
        List<LocationData> results = (List<LocationData>) q.getResultList();
        return results;
    }

  

    @Override
    public Long getGeoFenceId(String msisdn) {
        Query query = em.createQuery("SELECT l.currentClosestGeoFenceId FROM LocationData l WHERE l.msisdn = ?1 AND ORDER BY l.eventDate DESC");
        query.setParameter(1, msisdn);
        List<Long> results = (List<Long>) query.getResultList();
        if (results != null && results.size() > 0) {
            return results.get(0);
        }
        return null;
    }
    
    @Override
     public Double getAverageDistanceFromGeoFence(Brand brand, Date startDate, Date endDate, String milesLimit){
        Query query;
        double averageDistance;
        if(milesLimit != null){
            query = em.createNativeQuery("SELECT AVG(distance_away_from_geo_fence) " +
                    "FROM location_data WHERE distance_away_from_geo_fence " + 
                    "<= ?1 AND event_date BETWEEN ?2 AND ?3 AND " +
                    "brand_id = ?4 AND " +
                    "distance_away_from_geo_fence IS NOT NULL");
            query.setParameter(1, milesLimit);
            query.setParameter(2, DateUtil.getStartOfDay(startDate));
            query.setParameter(3, DateUtil.getEndOfDay(endDate));
            query.setParameter(4, brand.getId());
        }else{
            query = em.createNativeQuery("SELECT AVG(distance_away_from_geo_fence) " +
                    "FROM location_data WHERE event_date BETWEEN ?1 AND ?2 " +
                    "AND brand_id = ?3 " +
                    "AND distance_away_from_geo_fence IS NOT NULL");
            query.setParameter(1, DateUtil.getStartOfDay(startDate));
            query.setParameter(2, DateUtil.getEndOfDay(endDate));
            query.setParameter(3, brand.getId());
        }
        try{
            averageDistance = (Double)query.getSingleResult();
            return averageDistance;
        }catch(Exception e){
            return 0.0;
        }    
    }
}
