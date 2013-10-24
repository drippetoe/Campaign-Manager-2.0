/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.Barcode;
import com.proximus.data.Campaign;
import com.proximus.data.Company;
import com.proximus.data.Device;
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
public class BarcodeManager extends AbstractManager<Barcode> implements BarcodeManagerLocal {

    private static final Logger logger = Logger.getLogger(Barcode.class.getName());
    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public BarcodeManager() {
        super(Barcode.class);
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
    public List<Barcode> findAllByCompany(Company company) {
        Query q = em.createQuery("SELECT b FROM Barcode b WHERE b.company = ?1 ORDER BY b.id");
        q.setParameter(1, company);
        List<Barcode> results = (List<Barcode>) q.getResultList();

        return results;
    }

    @Override
    public void createListBarcodeLogs(List<Barcode> logs) {
        try {

            for (int i = 0; i < logs.size(); i++) {
                Barcode b = logs.get(i);
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
    public List<Barcode> fetchBarcodeOffers(Date start, Date end, Company company, Campaign campaign, Device device) {
        if (end == null || start.getTime() == end.getTime()) {
            end = start;
        }

        String query = "SELECT b FROM Barcode b WHERE b.eventDate between ?1 AND ?2 AND b.company = ?3 ";
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

        List<Barcode> results = (List<Barcode>) q.getResultList();
        if (results.isEmpty()) {
            return null;
        }
        return results;
    }
}
