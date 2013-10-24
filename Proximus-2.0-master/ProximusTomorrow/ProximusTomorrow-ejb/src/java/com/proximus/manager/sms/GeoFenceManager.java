/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager.sms;

import com.proximus.data.Brand;
import com.proximus.data.Company;
import com.proximus.data.sms.Country;
import com.proximus.data.sms.GeoFence;
import com.proximus.data.sms.Property;
import com.proximus.manager.AbstractManager;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;

/**
 *
 * @author eric
 */
@Stateless
public class GeoFenceManager extends AbstractManager<GeoFence> implements GeoFenceManagerLocal {

    private static final Logger logger = Logger.getLogger(GeoFenceManager.class.getName());
    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    public GeoFenceManager() {
        super(GeoFence.class);
    }

    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    public GeoFence findByName(String name, Property p) {
        Query q = em.createQuery("SELECT g FROM GeoFence g WHERE g.name = ?1 and g.property = ?2");
        q.setParameter(1, name);
        q.setParameter(2, p);
        List<GeoFence> results = (List<GeoFence>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public List<String> getGeoFenceNamesByProperty(Property p) {
        Query q = em.createQuery("SELECT g FROM GeoFence g WHERE g.property = ?1 order by g.name");
        q.setParameter(1, p);
        List<GeoFence> results = (List<GeoFence>) q.getResultList();
        if (results.size() > 0) {
            List<String> names = new ArrayList<String>();
            for (GeoFence g : results) {
                names.add(g.getName());
            }
            return names;
        }
        return null;
    }

    @Override
    public List<GeoFence> findAllByProperty(Property property) {
        Query q = em.createQuery("SELECT g FROM GeoFence g WHERE g.property = ?1 ORDER BY g.id");
        q.setParameter(1, property);
        List<GeoFence> results = (List<GeoFence>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public List<GeoFence> getGeoFencesByCompanyAndPriority(Company company, Long priority) {
        Query q = em.createQuery("SELECT g FROM GeoFence g WHERE g.company = ?1 AND g.priority = ?2 order by g.priority");
        q.setParameter(1, company);
        q.setParameter(2, priority);
        List<GeoFence> results = (List<GeoFence>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null; // none
    }

    @Override
    public List<GeoFence> getGeoFencesByCompany(Company company) {
        Query q = em.createQuery("SELECT g FROM GeoFence g WHERE g.company = ?1 order by g.name");
        q.setParameter(1, company);
        List<GeoFence> results = (List<GeoFence>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null; // none    
    }

    @Override
    public Property getPropertyFromGeoFence(Long geoFenceId) {
        Query query = em.createQuery("SELECT g.property FROM GeoFence g WHERE g.id = ?1");
        query.setParameter(1, geoFenceId);
        Property closestProperty;
        try {
            closestProperty = (Property) query.getSingleResult();
        } catch (Exception e) {
            closestProperty = null;
        }
        return closestProperty;
    }

    @Override
    public List<GeoFence> getGeoFencesByCompanyAndCountry(Company company, Country country) {
        Query q = em.createQuery("SELECT g FROM GeoFence g WHERE g.company = ?1 AND g.property.country = ?2 order by g.name");
        q.setParameter(1, company);
        q.setParameter(2, country);
        List<GeoFence> results = (List<GeoFence>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null; // none
    }

    @Override
    public List<GeoFence> getGeoFencesByBrand(Brand brand) {
        Query q = em.createQuery("SELECT g FROM GeoFence g WHERE g.company.brand = :brand order by g.name");
        q.setParameter("brand", brand);
        List<GeoFence> results = (List<GeoFence>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null; // none    
    }
}
