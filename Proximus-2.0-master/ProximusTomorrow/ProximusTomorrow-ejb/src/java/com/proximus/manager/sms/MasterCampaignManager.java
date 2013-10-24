/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager.sms;

import com.proximus.data.sms.Locale;
import com.proximus.data.sms.MasterCampaign;
import com.proximus.manager.AbstractManager;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Angela Mercer
 */
@Stateless
public class MasterCampaignManager extends AbstractManager<MasterCampaign> implements MasterCampaignManagerLocal 
{
    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public MasterCampaignManager() {
        super(MasterCampaign.class);
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
    public MasterCampaign getMasterCampaignByKazeId(String kazeId){
        Query query = em.createQuery("SELECT m from MasterCampaign m WHERE m.kaze_id = ?1");
        query.setParameter(1, kazeId);
        List<MasterCampaign> results = (List<MasterCampaign>) query.getResultList();
        if(results.size() > 0){
            return results.get(0);
        }
        return null;
     }
     
    @Override
    public MasterCampaign getMasterCampaignByLocale(Locale locale){
        Query query = em.createQuery("SELECT m from MasterCampaign m WHERE m.locale = ?1");
        query.setParameter(1, locale);
        List<MasterCampaign> results = (List<MasterCampaign>) query.getResultList();
        if(results.size() > 0){
            return results.get(0);
        }
        return null;
    } 
      
}
