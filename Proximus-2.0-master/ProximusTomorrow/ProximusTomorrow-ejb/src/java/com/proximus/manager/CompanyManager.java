/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.*;
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
public class CompanyManager extends AbstractManager<Company> implements CompanyManagerLocal {

    private static final Logger logger = Logger.getLogger(CompanyManager.class.getName());
    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public CompanyManager() {
        super(Company.class);
    }
    
     public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    public Company getCompanybyName(String name) {
        Query q = em.createQuery("SELECT co FROM Company co WHERE co.name = ?1");
        q.setParameter(1, name);
        List<Company> results = (List<Company>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null; // none
    }

    @Override
    public Company getCompanybyId(Long id) {
        Query q = em.createQuery("SELECT co FROM Company co WHERE co.id = ?1");
        q.setParameter(1, id);
        List<Company> results = (List<Company>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null; // none
    }

    @Override
    public List<Company> findCompanyLike(String keyword) {
        Query q = em.createQuery("SELECT co FROM Company co WHERE UPPER(co.name) LIKE ?1 ORDER BY co.name");
        q.setParameter(1, "%" + keyword.toUpperCase() + "%");
        List<Company> results = (List<Company>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public List<Company> findCompaniesWithLicense(String license) {
        Query q = em.createQuery("SELECT co FROM Company co WHERE co.license.licenseText LIKE ?1 ORDER BY co.name");
        q.setParameter(1, "%" + license + "%");
        List<Company> results = (List<Company>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }
    
     @Override
    public List<Company> findCompaniesWithLicenseInBrand(String license,Brand b) {
        Query q = em.createQuery("SELECT co FROM Company co WHERE co.license.licenseText LIKE ?1 AND co.brand = ?2 ORDER BY co.name");
        q.setParameter(1, "%" + license + "%");
        q.setParameter(2,b);
        List<Company> results = (List<Company>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public List<Company> findAllCompanies() {
        Query q = em.createQuery("SELECT co FROM Company ORDER BY co.name");
        List<Company> results = (List<Company>) q.getResultList();
        return results;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
  
    @Override
    public List<User> getUsersFromCompany(Long id) {
        Company c = this.find(id);
        return c.getUser();
    }

    @Override
    public List<License> getAllLicenses() {
        Query q = em.createQuery("SELECT l FROM License l order by l.licenseText");
        List<License> results = (List<License>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null; // none
    }

    @Override
    public License getLicenseByText(String licenseText) {
        Query q = em.createQuery("SELECT l FROM License l where l.licenseText = ?1");
        q.setParameter(1, licenseText);
        List<License> results = (List<License>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null; // none
    }

    @Override
    public List<Company> findAllCompaniesOfBrand(Brand b) {
        Query q = em.createQuery("SELECT co FROM Company co where co.brand = ?1 ORDER BY co.name");
        q.setParameter(1,b);
        List<Company> results = (List<Company>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }
    
    @Override
    public PricingModel getPricingModelByCompany(Company c){
        Query q = em.createQuery("SELECT pm from PricingModel pm where pm = ?1");
        q.setParameter(1, c.getPricingModel());
        List<PricingModel> results = (List<PricingModel>)q.getResultList();
        if(results.size() > 0){
            return results.get(0);
        }
        return null;
    }
}
