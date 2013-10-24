/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms;

import com.proximus.data.Company;
import com.proximus.data.sms.MobileSystemMessage;
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

/**
 *
 * @author ronald
 */
@Stateless
public class MobileSystemMessageManager extends AbstractManager<MobileSystemMessage> implements MobileSystemMessageManagerLocal {

    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public MobileSystemMessageManager() {
        super(MobileSystemMessage.class);
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
    public Long getTotalMessageCountByCompanyAndDate(Company company, Date startDate, Date endDate) {
        String query = "SELECT COUNT(m) FROM MobileSystemMessage m WHERE m.company = ?1 AND m.eventDate BETWEEN ?2 AND ?3";
        Query q = em.createQuery(query);
        q.setParameter(1, company);
        q.setParameter(2, DateUtil.getStartOfDay(startDate));
        q.setParameter(3, DateUtil.getNextDay(endDate));
        Long result;
        try {
            result = (Long) q.getSingleResult();
        } catch (Exception e) {
            result = 0L;
        }
        return result;
    }

    @Override
    public Map<String, Number> getSystemMessageMap(Company company, Date startDate, Date endDate) {
        Query query = this.em.createQuery("SELECT m.message, count(m) FROM MobileSystemMessage m WHERE  m.company = ?1 AND m.eventDate BETWEEN ?2 AND ?3 "
                + "GROUP BY m.message ORDER BY m.message DESC");
        query.setParameter(1, company);
        query.setParameter(2, DateUtil.getStartOfDay(startDate));
        query.setParameter(3, DateUtil.getNextDay(endDate));
        Map<String, Number> systemMessageMap = new HashMap<String, Number>();
        List<Object[]> results = query.getResultList();
        for (Object[] object : results) {
            String message = (String) object[0];
            Long numberOfMessages = (Long) object[1];
            systemMessageMap.put(message, numberOfMessages);
        }
        if (systemMessageMap.size() > 0) {
            return systemMessageMap;
        }
        return null;
    }
}
