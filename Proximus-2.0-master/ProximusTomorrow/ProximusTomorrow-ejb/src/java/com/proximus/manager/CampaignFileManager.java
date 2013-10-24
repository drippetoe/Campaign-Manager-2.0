/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.Campaign;
import com.proximus.data.CampaignFile;
import com.proximus.data.CampaignType;
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
public class CampaignFileManager extends AbstractManager<CampaignFile> implements CampaignFileManagerLocal
{

    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager()
    {
        return em;
    }
    
    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public CampaignFileManager()
    {
        super(CampaignFile.class);
    }

    public CampaignFile getFileByType(CampaignType ct)
    {
        Query q = em.createQuery("SELECT cfile FROM CampaignFile cfile WHERE cfile.campaignType=?1");
        q.setParameter(1, ct);
        List<CampaignFile> results = (List<CampaignFile>) q.getResultList();
        if (results.size() > 0)
        {
            return results.get(0);
        }
        return null;
    }
    
}
