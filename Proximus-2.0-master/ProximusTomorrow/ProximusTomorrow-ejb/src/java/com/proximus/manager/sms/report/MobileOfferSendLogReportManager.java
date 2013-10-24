/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms.report;

import com.proximus.data.Company;
import com.proximus.data.sms.*;
import com.proximus.data.sms.report.MobileOfferSendLogSummary;
import com.proximus.data.util.DateUtil;
import com.proximus.manager.AbstractManager;
import java.util.*;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import org.apache.log4j.Logger;

/**
 *
 * @author ronald
 */
@Stateless
public class MobileOfferSendLogReportManager extends AbstractManager<MobileOfferSendLogSummary> implements MobileOfferSendLogReportManagerLocal {

    private static final Logger logger = Logger.getLogger(MobileOfferSendLogReportManager.class.getName());
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

    public MobileOfferSendLogReportManager() {
        super(MobileOfferSendLogSummary.class);
    }

    @Override
    public List<DMA> findAllDmas(Company company) {
        String query = "SELECT DISTINCT(m.dma) FROM MobileOfferSendLogSummary m WHERE m.company = ?1 AND m.dma IS NOT NULL ORDER BY m.dma.name";
        Query q = em.createQuery(query);
        q.setParameter(1, company);
        List<DMA> results = (List<DMA>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public List<Retailer> findAllRetailers(Company company) {
        String query = "SELECT DISTINCT(m.retailer) FROM MobileOfferSendLogSummary m WHERE m.company = ?1 AND m.retailer IS NOT NULL ORDER BY m.retailer.name";
        Query q = em.createQuery(query);
        q.setParameter(1, company);
        List<Retailer> results = (List<Retailer>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public MobileOfferSendLogSummary fetchMobileOfferSendLogSummaryByCompanyAndSendLogSummary(Company company, MobileOfferSendLogSummary sendLogSummary) {
        String query = "SELECT m FROM MobileOfferSendLogSummary m WHERE m.company = ?1 AND m.mobileOffer = ?2 AND m.eventDate = ?3";
        Query q = em.createQuery(query);
        q.setParameter(1, company);
        q.setParameter(2, sendLogSummary.getMobileOffer());
        q.setParameter(3, sendLogSummary.getEventDate());
        List<MobileOfferSendLogSummary> results = (List<MobileOfferSendLogSummary>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public MobileOfferSendLogSummary fetchMobileOfferSendLogSummary(Company company, DMA dma, Property property, Retailer retailer, MobileOffer offer) {
        String query = "SELECT COUNT(m.id) FROM MobileOfferSendLog m WHERE m.company = ?1";
        int paramCount = 1;
        HashMap<Integer, Object> params = new HashMap<Integer, Object>();
        if (dma != null) {
            paramCount++;
            query += " " + "AND m.property.dma = ?" + paramCount;
            params.put(paramCount, dma);
        }

        if (property != null) {
            paramCount++;
            query += " " + "AND m.property = ?" + paramCount;
            params.put(paramCount, property);
        }
        if (retailer != null) {
            paramCount++;
            query += " " + "AND m.mobileOffer.retailer = ?" + paramCount;
            params.put(paramCount, retailer);
        }

        if (offer != null) {
            paramCount++;
            query += " " + "AND m.mobileOffer = ?" + paramCount;
            params.put(paramCount, offer);
        }
        Query q = em.createQuery(query);
        q.setParameter(1, company);
        for (Map.Entry<Integer, Object> entry : params.entrySet()) {
            Integer paramId = entry.getKey();
            Object object = entry.getValue();
            q.setParameter(paramId, object);
        }
        return null;
    }

    @Override
    public List<MobileOfferSendLogSummary> fetchMobileOfferSendLogSummaries(Date start, Date end, Company company, DMA dma, Property property, Retailer retailer) {
        if (end == null || start.getTime() == end.getTime()) {
            end = start;
        }
        String query = "SELECT m FROM MobileOfferSendLogSummary m WHERE m.eventDate BETWEEN ?1 AND ?2 AND m.company = ?3";
        int paramCount = 3;
        HashMap<Integer, Object> params = new HashMap<Integer, Object>();
        if (dma != null) {
            paramCount++;
            query += " " + "AND m.property.dma = ?" + paramCount;
            params.put(paramCount, dma);
        }

        if (property != null) {
            paramCount++;
            query += " " + "AND m.property = ?" + paramCount;
            params.put(paramCount, property);
        }
        if (retailer != null) {
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
        List<MobileOfferSendLogSummary> results = (List<MobileOfferSendLogSummary>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public List<MobileOfferSendLogSummary> getSummaryFromSendLog(Company company, Date deliveryDate) {

        String query = "SELECT DATE(event_date), count(*), mobileoffer_id, geo_fence_id, property_id "
                + "FROM mobile_offer_send_log "
                + "WHERE status = 'DELIVERED' "
                + "AND company_id = ?1 "
                + "AND event_date BETWEEN ?2 AND ?3 ";

        query += "GROUP BY DATE(event_date), mobileoffer_id ORDER BY DATE(event_date) DESC";

        Query q = em.createNativeQuery(query);
        q.setParameter(1, company.getId());
        q.setParameter(2, DateUtil.getStartOfDay(deliveryDate), TemporalType.DATE);
        q.setParameter(3, DateUtil.getNextDay(deliveryDate), TemporalType.DATE);

        List<MobileOfferSendLogSummary> results = new ArrayList<MobileOfferSendLogSummary>();

        List<Object[]> result = q.getResultList();
        for (Object[] object : result) {
            Date eventDate = (Date) object[0];
            Long messagesSent = (Long) object[1];
            Long offerId = (Long) object[2];
            Long geoFenceId = (Long) object[3];
            Long propertyId = (Long) object[4];

            String query2 = "SELECT m, m.retailer FROM MobileOffer m WHERE m.id =?1";
            Query q2 = em.createQuery(query2);
            q2.setParameter(1, offerId);
            Object[] anotherResult;

            try {
                q2.getSingleResult();
            } catch (Exception e) {
                continue;
            }
            anotherResult = (Object[]) q2.getSingleResult();
            MobileOffer mobileOffer = (MobileOffer) anotherResult[0];
            Retailer retailer = (Retailer) anotherResult[1];


            String query3 = "SELECT p, p.dma FROM Property p WHERE p.id =?1";
            Query q3 = em.createQuery(query3);
            q3.setParameter(1, propertyId);
            Object[] propResult;
            try {
                q3.getSingleResult();
            } catch (Exception e) {
                continue;
            }
            propResult = (Object[]) q3.getSingleResult();

            Property property = (Property) propResult[0];
            DMA dma = (DMA) propResult[1];


            if (dma == null && property == null && retailer == null) {
                continue;
            }
            String query4 = "SELECT g FROM GeoFence g WHERE g.id =?1";
            Query q4 = em.createQuery(query4);
            q4.setParameter(1, geoFenceId);
            GeoFence geoFence;
            try {
                geoFence = (GeoFence) q4.getSingleResult();
            } catch (Exception ex) {
                continue;
            }
            MobileOfferSendLogSummary item = new MobileOfferSendLogSummary();
            item.setEventDate(eventDate);
            item.setTotalMessagesSent(messagesSent);
            item.setCompany(company);
            item.setMobileOffer(mobileOffer);
            item.setDma(dma);
            item.setGeoFence(geoFence);
            item.setProperty(property);
            item.setRetailer(retailer);
            results.add(item);
        }
        if (results != null && results.size() > 0) {
            return results;
        }
        return null;
    }
}
