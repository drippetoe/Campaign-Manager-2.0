/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.Company;
import com.proximus.data.CompanyLogo;
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
public class CompanyLogoManager extends AbstractManager<CompanyLogo> implements CompanyLogoManagerLocal {

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

    public CompanyLogoManager() {
        super(CompanyLogo.class);
    }

    @Override
    public CompanyLogo getByCompany(Company c) {
        Query q = em.createQuery("SELECT clogo FROM CompanyLogo clogo WHERE clogo.company=?1");
        q.setParameter(1, c);
        List<CompanyLogo> results = (List<CompanyLogo>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }
}
