/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.Campaign;
import com.proximus.data.Company;
import com.proximus.data.Device;
import com.proximus.data.WifiRegistration;
import com.proximus.data.util.DateUtil;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 *
 * @author ronald
 */
@Stateless
public class WifiRegistrationManager extends AbstractManager<WifiRegistration> implements WifiRegistrationManagerLocal {

    private static final Logger logger = Logger.getLogger(WifiRegistrationManager.class.getName());
    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    @Override
    public void createRegistration(WifiRegistration registration) {
        try {
            em.persist(registration);
            em.flush();
        } catch (Exception e) {
            logger.log(Priority.ERROR, "Couldn't persist data");
        }
    }
    
    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    public void createRegistrationList(List<WifiRegistration> registrations) {
        try {

            for (int i = 0; i < registrations.size(); i++) {
                WifiRegistration r = registrations.get(i);
                System.out.println("ID: " + i + "--" + r);

                em.persist(registrations.get(i));
                if ((i % 500) == 0) {
                    em.flush();
                    em.clear();
                }
            }
            em.flush();

        } catch (Exception e) {
            logger.log(Priority.ERROR, "Couldn't persist list of data");
            e.printStackTrace();
        }
    }

    @Override
    public void deleteRegistration(WifiRegistration registration) {
        try {
            em.remove(registration);
            em.flush();
        } catch (Exception e) {
            logger.log(Priority.ERROR, "Couldn't delete data");
        }



    }

    @Override
    public void deleteRegistrationList(List<WifiRegistration> registrations) {
        try {
            em.getTransaction().begin();
            for (int i = 0; i < registrations.size(); i++) {
                em.remove(registrations.get(i));
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

    public WifiRegistrationManager() {
        super(WifiRegistration.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Long getRegistrationCount(Date start, Date end, Company company, Campaign campaign, Device device) {
        String query = "SELECT count(s) FROM WifiRegistration s WHERE s.company = ?1 ";
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
            params.put(paramCount, start);
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
        Long result = (Long) q.getSingleResult();
        return result;
    }

    @Override
    public List<WifiRegistration> getWifiRegistrationInRangeByCampaignAndDevice(Date start, Date end, Company company, Campaign campaign, Device device) {
        String query = "SELECT s FROM WifiRegistration s WHERE s.company = ?1";

        int paramCount = 1;
        HashMap<Integer, Object> params = new HashMap<Integer, Object>();
        if (campaign != null) {
            paramCount++;
            query += " " + "AND s.campaign = ?" + paramCount;
            params.put(paramCount, campaign);
        }
        if (device != null) {
            paramCount++;
            query += " " + "AND s.device = ?" + paramCount;
            params.put(paramCount, device);
        }
        if (start != null) {
            paramCount++;
            query += " " + "AND s.eventDate >= ?" + paramCount;
            params.put(paramCount, start);
        }
        if (end != null) {
            paramCount++;
            query += " " + "AND s.eventDate <= ?" + paramCount;
            params.put(paramCount, DateUtil.getEndOfDay(end));
        }
        Query q = em.createQuery(query);

        q.setParameter(1, company);

        for (Map.Entry<Integer, Object> entry : params.entrySet()) {
            Integer paramId = entry.getKey();
            Object object = entry.getValue();
            q.setParameter(paramId, object);
        }

        List<WifiRegistration> results = (List<WifiRegistration>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;

    }
}