/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms;

import com.proximus.data.Company;
import com.proximus.data.sms.DMA;
import com.proximus.data.sms.Property;
import com.proximus.data.sms.Retailer;
import com.proximus.data.util.DateUtil;
import com.proximus.manager.AbstractManager;
import java.util.Date;
import java.util.List;
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
public class PropertyManager extends AbstractManager<Property> implements PropertyManagerLocal {

    private static final Logger logger = Logger.getLogger(Property.class.getName());
    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public PropertyManager() {
        super(Property.class);
    }
    
    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Property> getPropertiesByCompany(Company company) {
        Query q = em.createQuery("SELECT p FROM Property p WHERE p.company = ?1 order by p.name");
        q.setParameter(1, company);
        List<Property> results = (List<Property>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null; // none
    }

    @Override
    public Property getPropertyByCompanyAndName(Company c, String name) {
        Query q = em.createQuery("SELECT p FROM Property p WHERE p.company = ?1 AND p.name = ?2");
        q.setParameter(1, c);
        q.setParameter(2, name);
        List<Property> results = (List<Property>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null; // none
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public List<Property> getPropertiesByCompanyDMA(Company company, DMA dma) {
        Query q = em.createQuery("SELECT p FROM Property p WHERE p.dma = ?1 AND p.company = ?2 order by p.name");
        q.setParameter(1, dma);
        q.setParameter(2, company);
        List<Property> results = (List<Property>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null; // none
    }

    @Override
    public List<Long> getDMAIdsByCompany(Company company) {
        String query = "(SELECT DISTINCT DMA_id FROM property WHERE COMPANY_id =?1 AND DMA_id IS NOT NULL)";
        Query q = em.createNativeQuery(query);
        q.setParameter(1, company.getId());
        List<Long> results = (List<Long>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public List<Property> getPropertiesByCompanyAndRetailer(Company company, Retailer retailer) {
        Query q = em.createQuery("SELECT p FROM Property p WHERE p.company = :company AND :retailer MEMBER OF p.retailers ORDER BY p.name");

        q.setParameter("company", company);
        q.setParameter("retailer", retailer);
        List<Property> results = (List<Property>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null; // none
    }

    @Override
    public long countPropertiesByCompany(Company company) {
        Query q = em.createQuery("SELECT COUNT(p.id) FROM Property p WHERE p.company = ?1");
        q.setParameter(1, company);
        long result = (Long) q.getSingleResult();
        return result;
    }

    @Override
    public long countNewPropertiesByCompanyBetweenDates(Date startDate, Date endDate, Company company) {
        Query q = em.createQuery("SELECT COUNT(p.id) FROM Property p WHERE p.company = ?1 "
                + "AND p.dateCreated BETWEEN ?2 and ?3");
        q.setParameter(1, company);
        q.setParameter(2, DateUtil.getStartOfDay(startDate));
        q.setParameter(3, DateUtil.getEndOfDay(endDate));
        long result = (Long) q.getSingleResult();
        return result;
    }

    @Override
    public long countPropertiesByCompanyBeforeDate(Company company, Date stopDate) {
        Query q = em.createQuery("SELECT COUNT(p.id) FROM Property p WHERE p.company = ?1 "
                + "AND p.dateCreated < ?2");
        q.setParameter(1, company);
        q.setParameter(2, DateUtil.getEndOfDay(stopDate));
        long result = (Long) q.getSingleResult();
        return result;
    }

    @Override
    public Property getPropertyByWebHash(String webHash) {
        Query q = em.createQuery("SELECT p FROM Property p WHERE p.webHash=?1");
        q.setParameter(1, webHash);
        List<Property> results = (List<Property>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null; // none
    }

    @Override
    public boolean propertiesAssociatedToDma(DMA dma) {
        Query q = em.createQuery("SELECT p FROM Property p WHERE p.dma = :dma");
        q.setParameter("dma", dma);
        List<Property> results = (List<Property>)q.getResultList();
        if(results.size() > 0) {
            return true;
        }
        return false;
    }
}
