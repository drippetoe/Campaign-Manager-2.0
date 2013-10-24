/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms;

import com.proximus.data.Brand;
import com.proximus.data.Company;
import com.proximus.data.sms.Category;
import com.proximus.data.sms.Locale;
import com.proximus.manager.AbstractManager;
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
public class CategoryManager extends AbstractManager<Category> implements CategoryManagerLocal {

    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public CategoryManager() {
        super(Category.class);
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
    public Category getCategoryByBrandAndName(Brand b, String name) {
        Query q = em.createQuery("SELECT c FROM Category c WHERE c.brand=?1 AND  c.name = ?2");
        q.setParameter(1, b);
        q.setParameter(2, name);
        List<Category> results = (List<Category>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null; // none
    }

    @Override
    public Category getCategoryByBrandAndWebSafeName(Brand b, String webSafeName) {
        Query q = em.createQuery("SELECT c FROM Category c WHERE c.brand=?1 AND  c.webSafeName = ?2");
        q.setParameter(1, b);
        q.setParameter(2, webSafeName);
        List<Category> results = (List<Category>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null; // none
    }

    @Override
    public List<Category> getAllByBrandFilterLike(Brand b, String like) {
        Query q = em.createQuery("SELECT c FROM Category c WHERE c.brand=?1 AND c.name LIKE ?2 ORDER BY c.name");
        q.setParameter(1, b);
        q.setParameter(2, "%" + like + "%");
        List<Category> results = (List<Category>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null; // none
    }

    @Override
    public List<Category> getAllByBrand(Brand b) {
        Query q = em.createQuery("SELECT c FROM Category c WHERE c.brand=?1 ORDER BY c.name");
        q.setParameter(1, b);
        List<Category> results = (List<Category>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null; // none
    }

    @Override
    public List<Category> getAllByBrandAndLocale(Brand b, Locale locale) {
        String q = "SELECT c FROM Category c WHERE c.brand = :brand";
        if (locale != null && !locale.getLanguageCode().isEmpty()) {
            q += " AND c.locale = :locale";
        }
        q += " ORDER BY c.name";
        Query query = em.createQuery(q);
        query.setParameter("brand", b);
        if (locale != null && !locale.getLanguageCode().isEmpty()) {
            query.setParameter("locale", locale);
        }
        List<Category> results = (List<Category>) query.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null; // none
    }
}
