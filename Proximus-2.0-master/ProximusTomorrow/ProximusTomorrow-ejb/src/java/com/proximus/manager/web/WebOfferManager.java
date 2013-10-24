/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.web;

import com.proximus.data.web.WebOffer;
import com.proximus.manager.AbstractManager;
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
public class WebOfferManager extends AbstractManager<WebOffer> implements WebOfferManagerLocal {

    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public WebOfferManager() {
        super(WebOffer.class);
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
    public WebOffer getWebOfferByItsMobileOfferSibling(Long mobileOfferId) {
        Query q = em.createQuery("SELECT wo FROM WebOffer wo WHERE wo.mobileOffer.id = ?1");
        q.setParameter(1, mobileOfferId);
        List<WebOffer> results = (List<WebOffer>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null; // none
    }

    @Override
    public List<Long> getWebOffersByProperty(String languageCode, String propertyWebHash) {
        Query q = em.createNativeQuery("SELECT w.id FROM web_offer w WHERE w.locale =?1  AND((w.retail_only = 1 AND "
                + "w.retailer_id in (SELECT ret.id FROM retailer ret WHERE ret.id in(SELECT pr.retailer_id FROM property_retailer pr "
                + "WHERE pr.property_id = (SELECT id FROM property WHERE web_hash COLLATE latin1_general_cs = ?2)))) "
                + "OR w.id in(SELECT pw.web_offer_id FROM property_web_offer pw WHERE pw.property_id = (SELECT id FROM property "
                + "WHERE web_hash COLLATE latin1_general_cs = ?2))) AND w.status='Approved' AND w.deleted=0 AND "
                + "DATE(NOW()) BETWEEN DATE(w.start_date) AND DATE(w.end_date) ORDER BY w.retailer_id");
        q.setParameter(1, languageCode);
        q.setParameter(2, propertyWebHash);
        List<Long> results = (List<Long>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }
}
