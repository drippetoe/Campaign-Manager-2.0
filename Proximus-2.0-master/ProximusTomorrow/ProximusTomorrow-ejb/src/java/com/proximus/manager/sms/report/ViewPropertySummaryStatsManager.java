/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms.report;

import com.proximus.data.Brand;
import com.proximus.data.Company;
import com.proximus.data.sms.Country;
import com.proximus.data.sms.MobileOffer;
import com.proximus.data.sms.Property;
import com.proximus.data.sms.report.ViewPropertySummaryStats;
import com.proximus.manager.AbstractManager;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Gilberto Gaxiola
 */
@Stateless
public class ViewPropertySummaryStatsManager extends AbstractManager<ViewPropertySummaryStats> implements ViewPropertySummaryStatsManagerLocal {

    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public ViewPropertySummaryStatsManager() {
        super(ViewPropertySummaryStats.class);
    }

    @Override
    public List<ViewPropertySummaryStats> getByBrand(Brand brand) {
        Query query = em.createQuery("SELECT stats FROM ViewPropertySummaryStats stats, Property p WHERE p.id = stats.propertyId and p.company.brand = ?1");
        query.setParameter(1, brand);
        List<ViewPropertySummaryStats> result = (List<ViewPropertySummaryStats>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    @Override
    public List<ViewPropertySummaryStats> getByCompany(Company company) {
        Query query = em.createQuery("SELECT stats FROM ViewPropertySummaryStats stats, Property p WHERE p.id = stats.propertyId and p.company = ?1");
        query.setParameter(1, company);
        List<ViewPropertySummaryStats> result = (List<ViewPropertySummaryStats>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    @Override
    public List<ViewPropertySummaryStats> getByProperties(List<Property> properties) {
        if (properties == null || properties.isEmpty()) {
            return null;
        }
        Query query = em.createQuery("SELECT s FROM ViewPropertySummaryStats s WHERE s.propertyId IN :propsIds");
        List<Long> propertiesIds = new ArrayList<Long>();

        for (Property p : properties) {
            propertiesIds.add(p.getId());
        }
        query.setParameter("propsIds", propertiesIds);
        List<ViewPropertySummaryStats> result = (List<ViewPropertySummaryStats>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    @Override
    public List<ViewPropertySummaryStats> getByBrandAndCountry(Brand brand, Country country) {
        String q = "SELECT stats FROM ViewPropertySummaryStats stats, Property p WHERE p.id = stats.propertyId AND p.company.brand = :brand";
        if (country != null) {
            q += " AND p.country = :country";
        }
        Query query = em.createQuery(q);

        query.setParameter("brand", brand);
        if (country != null) {
            query.setParameter("country", country);
        }
        List<ViewPropertySummaryStats> result = (List<ViewPropertySummaryStats>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    @Override
    public List<ViewPropertySummaryStats> getByCompanyAndCountry(Company company, Country country) {
        String q = "SELECT stats FROM ViewPropertySummaryStats stats, Property p WHERE p.id = stats.propertyId AND p.company = :company";
        if (country != null) {
            q += " AND p.country = :country";
        }
        Query query = em.createQuery(q);
        query.setParameter("company", company);
        if (country != null) {
            query.setParameter("country", country);
        }
        List<ViewPropertySummaryStats> result = (List<ViewPropertySummaryStats>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }
}
