/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.Campaign;
import com.proximus.data.CampaignFile;
import com.proximus.data.CampaignType;
import com.proximus.data.Company;
import java.io.File;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;

/**
 *
 * @author Gilberto Gaxiola
 */
@Stateless
public class CampaignManager extends AbstractManager<Campaign> implements CampaignManagerLocal {

    private static final Logger logger = Logger.getLogger(CampaignManager.class.getName());
    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public CampaignManager() {

        super(Campaign.class);
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
    public void calculateChecksum(Long id) {
        Campaign c = (Campaign) this.find(id);
        if (c != null) {
            c.setChecksum(c.calculateChecksum());
            em.persist(c);
        }
    }

    @Override
    public File getFile(Long id, String type) {
        File file;
        Campaign c = (Campaign) this.find(id);
        em.flush();
        if (c != null) {
            CampaignType ct = c.getCampaignType(type);
            Query q = em.createQuery("SELECT cf FROM CampaignFile cf WHERE cf.campaignType = ?1");
            q.setParameter(1, ct);
            List<CampaignFile> results = (List<CampaignFile>) q.getResultList();
            if (results.size() > 0) {
                CampaignFile cf = results.get(0);
                if (cf != null) {
                    String sp = cf.getServerPath();
                    file = new File(sp);
                    if (file.exists()) {
                        return file;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public List<Campaign> findCampaignLike(String keyword) {
        Query q = em.createQuery("SELECT camp FROM Campaign camp WHERE UPPER(camp.name) LIKE ?1 AND camp.deleted = 0 ORDER BY camp.name");
        q.setParameter(1, "%" + keyword.toUpperCase() + "%");
        List<Campaign> results = (List<Campaign>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public List<Campaign> findAllByCompanyActive(Company company) {
        Query q = em.createQuery("SELECT camp FROM Campaign camp WHERE camp.company = ?1 AND camp.deleted = 0 ORDER BY camp.active, camp.name");
        q.setParameter(1, company);
        List<Campaign> results = (List<Campaign>) q.getResultList();

        return results;
    }

    @Override
    public List<Campaign> findCampaignsByCompanyAndModifiedDateLike(Company company, String keyword) {
        Query q = em.createQuery("SELECT camp FROM Campaign camp WHERE camp.company = ?1 AND camp.name LIKE ?2 AND camp.deleted = 0 ORDER BY camp.lastModified desc");
        q.setParameter(1, company);
        q.setParameter(2, "%" + keyword.toUpperCase() + "%");
        List<Campaign> results = (List<Campaign>) q.getResultList();

        return results;
    }

    @Override
    public void deleteCampaignType(CampaignType ct) {
        em.remove(em.merge(ct));
    }
//    @Override
//    public Campaign getCampaignbyName(String name) {
//        Query q = em.createQuery("SELECT c FROM Campaign c WHERE c.name = ?1");
//        q.setParameter(1, name);
//        List<Campaign> results = (List<Campaign>) q.getResultList();
//        if (results.size() > 0) {
//            return results.get(0);
//        }
//        return null; // none
//    }

    @Override
    public List<Campaign> findAll(Company company) {
        Query q = em.createQuery("SELECT camp FROM Campaign camp WHERE camp.company = ?1 order by camp.name");
        q.setParameter(1, company);
        List<Campaign> results = (List<Campaign>) q.getResultList();

        return results;
    }
}
