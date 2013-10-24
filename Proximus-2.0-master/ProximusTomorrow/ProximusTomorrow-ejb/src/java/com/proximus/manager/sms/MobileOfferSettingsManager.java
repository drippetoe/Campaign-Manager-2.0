/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms;

import com.proximus.data.Brand;
import com.proximus.data.sms.MobileOffer;
import com.proximus.data.sms.MobileOfferSettings;
import com.proximus.manager.AbstractManager;
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
public class MobileOfferSettingsManager extends AbstractManager<MobileOfferSettings> implements MobileOfferSettingsManagerLocal {

    private static final Logger logger = Logger.getLogger(MobileOffer.class.getName());
    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public MobileOfferSettingsManager() {

        super(MobileOfferSettings.class);
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
    public MobileOfferSettings findByBrand(Brand brand) {
        Query q = em.createQuery("SELECT mos FROM MobileOfferSettings mos WHERE mos.brand = ?1");
        q.setParameter(1, brand);
        List<MobileOfferSettings> results = (List<MobileOfferSettings>) q.getResultList();
        if (results.size() > 0) {
            MobileOfferSettings mos = results.get(0);
            if (mos != null) {
                return mos;
            }
        }
        return null;
    }
}
