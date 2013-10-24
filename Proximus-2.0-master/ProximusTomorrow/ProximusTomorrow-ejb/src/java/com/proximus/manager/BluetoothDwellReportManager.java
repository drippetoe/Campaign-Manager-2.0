/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.BluetoothDwell;
import com.proximus.data.Campaign;
import com.proximus.data.Company;
import com.proximus.data.Device;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Gilberto Gaxiola
 */
@Stateless
public class BluetoothDwellReportManager implements BluetoothDwellReportManagerLocal {

    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public EntityManager getEntityManager() {
        return em;
    }

    public void setEntityManager(EntityManager em) {
        this.em = em;
    }
    
    

    @Override
    public Long totalDwellSessions(Date startDate, Date endDate, Company company, Campaign campaign, Device device) {
        String query = "SELECT COUNT(b.id) FROM BluetoothDwell b WHERE b.eventDate BETWEEN ?1 and ?2 AND b.company = ?3";
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
        q.setParameter(1, startDate);
        q.setParameter(2, endDate);
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
    public Long totalDwellTime(Date startDate, Date endDate, Company company, Campaign campaign, Device device) {

        String query = "SELECT SUM(b.dwellTimeMS) FROM BluetoothDwell b WHERE b.eventDate BETWEEN ?1 and ?2 AND b.company = ?3";
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
        q.setParameter(1, startDate);
        q.setParameter(2, endDate);
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
    public Double averageDwellTime(Date startDate, Date endDate, Company company, Campaign campaign, Device device) {
        String query = "SELECT AVG(b.dwellTimeMS) FROM BluetoothDwell b WHERE b.eventDate BETWEEN ?1 and ?2 AND b.company = ?3";
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
        q.setParameter(1, startDate);
        q.setParameter(2, endDate);
        q.setParameter(3, company);
        for (Map.Entry<Integer, Object> entry : params.entrySet()) {
            Integer paramId = entry.getKey();
            Object object = entry.getValue();
            q.setParameter(paramId, object);
        }

        Object obj = q.getSingleResult();
        if (obj == null) {
            return 0.00;
        } else {
            Double countResult = (Double) q.getSingleResult();
            return countResult;
        }
    }

    @Override
    public List<Device> getDevicesWithBluetoothDwell(Company company) {

        String query = "SELECT bd.device FROM BluetoothDwell bd WHERE bd.company = ?1 GROUP BY bd.device";
        Query q = em.createQuery(query);
        q.setParameter(1, company);
        List<Device> result = (List<Device>) q.getResultList();
        return result;
    }

    @Override
    public List<String> getDeviceMacAddresses(Company company) {
        String query = "(SELECT DISTINCT(d.mac_address) FROM bluetooth_dwell AS d WHERE d.friendly_name"
                + " IS NOT NULL AND company_id = ?1";
        query += ") ORDER BY mac_address";
        Query q = em.createNativeQuery(query);
        q.setParameter(1, company.getId());
        List<String> results = q.getResultList();
        return results;
    }

    @Override
    public String getDeviceFriendlyNames(String macAddress, Company company) {
        String query = "(SELECT b.friendly_name FROM bluetooth_dwell AS b WHERE b.mac_address =?1 AND b.company_id =?2)";
        Query q = em.createNativeQuery(query);
        q.setParameter(1, macAddress);
        q.setParameter(2, company.getId());
        List<String> results = (List<String>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public Long getMacAddressCount(String macAddress, Company company) {
        String query = "(SELECT COUNT(*) FROM bluetooth_dwell AS b WHERE b.mac_address =?1 AND b.company_id =?2)";
        Query q = em.createNativeQuery(query);
        q.setParameter(1, macAddress);
        q.setParameter(2, company.getId());
        List<Long> results = (List<Long>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public List<BluetoothDwell> getBluetoothDwellRecordsFrom(Date startDate, Date endDate, String macAddress, Company company, Device device) {
        String query = "SELECT b FROM BluetoothDwell b WHERE b.eventDate BETWEEN ?1 and ?2 AND b.macAddress = ?3 AND b.company = ?4 AND b.device = ?5 ORDER BY b.eventDate";

        Query q = em.createQuery(query);
        q.setParameter(1, startDate);
        q.setParameter(2, endDate);
        q.setParameter(3, macAddress);
        q.setParameter(4, company);
        q.setParameter(5, device);

        List<BluetoothDwell> result = (List<BluetoothDwell>) q.getResultList();
        if(result.isEmpty()) {
            return null;
        }
        return result;

    }

    @Override
    public List<BluetoothDwell> getAllBluetoothDwellRecordsFrom(String macAddress, Company company, Device device) {
        String query = "SELECT b FROM BluetoothDwell b WHERE b.macAddress = ?1 AND b.company = ?2 AND b.device = ?3 ORDER BY b.eventDate";

        Query q = em.createQuery(query);
        q.setParameter(1, macAddress);
        q.setParameter(2, company);
        q.setParameter(3, device);

        List<BluetoothDwell> result = (List<BluetoothDwell>) q.getResultList();
        if(result.isEmpty()) {
            return null;
        }
        return result;

    }
}
