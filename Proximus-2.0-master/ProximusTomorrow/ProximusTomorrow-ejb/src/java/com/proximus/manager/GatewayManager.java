/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager;

import com.proximus.data.Campaign;
import com.proximus.data.Company;
import com.proximus.data.GatewayPartnerFields;
import com.proximus.data.GatewayUser;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;

/**
 *
 * @author dshaw
 */
@Stateless
public class GatewayManager extends AbstractManager<GatewayUser> implements GatewayManagerLocal {

    private static final Logger logger = Logger.getLogger(GatewayManager.class.getName());
    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;
    
    public GatewayManager() {
        super(GatewayUser.class);
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
    public GatewayUser getGatewayUserByMacAddress(Company company, String macAddress) {
        macAddress=macAddress.replaceAll(":", "").toUpperCase();
        Query q = em.createQuery("SELECT e FROM GatewayUser e WHERE e.company =?1 AND e.macAddress = ?2");
        q.setParameter(1, company);
        q.setParameter(2, macAddress);
        List<GatewayUser> results = (List<GatewayUser>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public GatewayPartnerFields createGatewayPartnerFields(GatewayPartnerFields entity) {
        em.persist(entity);
        return entity;
    }

    @Override
    public void deleteGatewayPartnerFields(GatewayPartnerFields entity) {
        em.remove(entity);
    }

    @Override
    public GatewayPartnerFields updateGatewayPartnerFields(GatewayPartnerFields entity) {
        em.merge(entity);
        return entity;
    }

    @Override
    public GatewayPartnerFields getGatewayPartnerFieldsByCampaign(Campaign campaign) {
        Query q = em.createQuery("SELECT e FROM GatewayPartnerFields e WHERE e.campaign = ?1");
        q.setParameter(1, campaign);
        List<GatewayPartnerFields> results = (List<GatewayPartnerFields>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

}
