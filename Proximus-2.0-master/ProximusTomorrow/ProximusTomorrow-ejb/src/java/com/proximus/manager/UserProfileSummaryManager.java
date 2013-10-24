/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.Company;
import com.proximus.data.Device;
import com.proximus.data.report.UserProfileSummary;
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

/**
 *
 * @author ronald
 */
@Stateless
public class UserProfileSummaryManager extends AbstractManager<UserProfileSummary> implements UserProfileSummaryManagerLocal {

    private static final Logger logger = Logger.getLogger(UserProfileSummaryManager.class);
    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public UserProfileSummaryManager() {
        super(UserProfileSummary.class);
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
    public UserProfileSummary fetchUserProfileSummary(String macAddress, Company company, Device device) {
        String query = "SELECT b FROM UserProfileSummary b WHERE b.macAddress = ?1 AND b.company =?2 AND b.device = ?3";
        Query q = em.createQuery(query);
        q.setParameter(1, macAddress);
        q.setParameter(2, company);
        q.setParameter(3, device);

        List<UserProfileSummary> results = (List<UserProfileSummary>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public List<UserProfileSummary> fetchUserProfileSummaries(Date start, Date end, Company company, Device device) {
        if (end == null || start.getTime() == end.getTime()) {
            end = start;
        }

        String query = "SELECT s FROM UserProfileSummary s WHERE s.lastSeen between ?1 AND ?2 AND s.company = ?3 ";
        int paramCount = 3;
        HashMap<Integer, Object> params = new HashMap<Integer, Object>();
        if (device != null) {
            paramCount++;
            query += " AND s.device = ?" + paramCount + " ";
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

        List<UserProfileSummary> results = (List<UserProfileSummary>) q.getResultList();
        if (results.isEmpty()) {
            return null;
        }
        return results;
    }
}