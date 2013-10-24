/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms.report;

import com.proximus.data.sms.Category;
import com.proximus.data.sms.Property;
import com.proximus.data.sms.report.ViewActiveOffers;
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
public class ViewActiveOffersManager extends AbstractManager<ViewActiveOffers> implements ViewActiveOffersManagerLocal {

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

    public ViewActiveOffersManager() {
        super(ViewActiveOffers.class);
    }

    @Override
    public List<ViewActiveOffers> getByPropertyAndLanguageCode(Property p, String languageCode) {
        Query query = em.createQuery("SELECT offer FROM ViewActiveOffers offer where offer.property = ?1 AND offer.locale = ?2");
        query.setParameter(1, p);
        query.setParameter(2, languageCode);
        List<ViewActiveOffers> result = (List<ViewActiveOffers>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    @Override
    public List<ViewActiveOffers> getByPropertyAndCategoryAndLanguageCode(Property p, Category c, String languageCode) {
        Query query = em.createQuery("SELECT offer FROM ViewActiveOffers offer where offer.property = ?1 AND offer.locale = ?2");
        query.setParameter(1, p);
        query.setParameter(2, languageCode);
        List<ViewActiveOffers> subresult = (List<ViewActiveOffers>) query.getResultList();
        List<ViewActiveOffers> result = new ArrayList<ViewActiveOffers>();
        for (ViewActiveOffers vao : subresult) {
            if (vao.getWebOffer().getCategories().contains(c)) {
                result.add(vao);
            }
        }
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    @Override
    public List<ViewActiveOffers> getByProperty(Property p) {
        Query query = em.createQuery("SELECT offer FROM ViewActiveOffers offer where offer.property = ?1");
        query.setParameter(1, p);
        List<ViewActiveOffers> result = (List<ViewActiveOffers>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    @Override
    public List<ViewActiveOffers> getByPropertyAndLocaleAndCategoryList(Property p, String locale, List<Category> categories) {
        String q = "SELECT offer FROM ViewActiveOffers offer where offer.property = :property";
        if (locale != null && !locale.isEmpty()) {
            q += " AND offer.locale = :locale";
        }
        Query query = em.createQuery(q);
        query.setParameter("property", p);
        if (locale != null && !locale.isEmpty()) {
            query.setParameter("locale", locale);
        }

        List<ViewActiveOffers> subresult = (List<ViewActiveOffers>) query.getResultList();
        List<ViewActiveOffers> result;
        if (categories != null && !categories.isEmpty()) {
            result = new ArrayList<ViewActiveOffers>();
            for (ViewActiveOffers vao : subresult) {
                for (Category category : categories) {
                    if (result.contains(vao)) {
                        continue;
                    }
                    if (vao.getWebOffer().getCategories().contains(category)) {
                        result.add(vao);
                    }
                }
            }
        } else {
            result = subresult;
        }
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }
}
