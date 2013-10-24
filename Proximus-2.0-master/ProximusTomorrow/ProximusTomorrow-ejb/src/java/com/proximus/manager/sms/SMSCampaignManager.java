/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms;

import com.proximus.data.Company;
import com.proximus.manager.AbstractManager;
import com.proximus.data.sms.SMSCampaign;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author ronald
 */
@Stateless
public class SMSCampaignManager extends AbstractManager<SMSCampaign> implements SMSCampaignManagerLocal {

    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public SMSCampaignManager() {
        super(SMSCampaign.class);
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
    public List<SMSCampaign> findAllByCompany(Company company) {
        Query q = em.createQuery("SELECT c FROM SMSCampaign c WHERE c.company = ?1 ORDER BY c.id");
        q.setParameter(1, company);
        List<SMSCampaign> results = (List<SMSCampaign>) q.getResultList();

        return results;
    }

    @Override
    public SMSCampaign getSMSCampaignbyName(Company company, String name) {
        Query q = em.createQuery("SELECT c FROM SMSCampaign c WHERE c.company = ?1 AND c.name = ?2");
        q.setParameter(1, company);
        q.setParameter(2, name);
        List<SMSCampaign> results = (List<SMSCampaign>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null; // none
    }
    
    @Deprecated
    @Override
    /** DO NOT USE, DOES NOT MAINTAIN TENTETED SYSTEM **/
    public SMSCampaign getSMSCampaignById(Long id) {
        Query q = em.createQuery("SELECT c FROM SMSCampaign c WHERE c.id = ?1");
        q.setParameter(1, id);
        List<SMSCampaign> results = (List<SMSCampaign>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null; // none
    }
    
    @Override
    public SMSCampaign getSMSCampaignById(Company company, Long id) {
        Query q = em.createQuery("SELECT c FROM SMSCampaign c WHERE c.company = ?1 AND c.id = ?2");
        q.setParameter(1, company);
        q.setParameter(2, id);
        List<SMSCampaign> results = (List<SMSCampaign>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null; // none
    }
}
