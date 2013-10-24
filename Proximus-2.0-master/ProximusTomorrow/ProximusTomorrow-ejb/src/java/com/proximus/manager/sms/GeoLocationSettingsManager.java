/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms;

import com.proximus.data.Brand;
import com.proximus.data.sms.GeoLocationSettings;
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
public class GeoLocationSettingsManager extends AbstractManager<GeoLocationSettings> implements GeoLocationSettingsManagerLocal {

    private static final Logger logger = Logger.getLogger(GeoLocationSettingsManager.class.getName());
    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public GeoLocationSettingsManager() {

        super(GeoLocationSettings.class);
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
    public GeoLocationSettings findByBrand(Brand brand) {
        Query q = em.createQuery("SELECT gls FROM GeoLocationSettings gls WHERE gls.brand = ?1");
        q.setParameter(1, brand);
        List<GeoLocationSettings> results = (List<GeoLocationSettings>) q.getResultList();
        if (results.size() > 0) {
            GeoLocationSettings gls = results.get(0);
            if (gls != null) {
                return gls;
            }
        }
        return null;
    }
}
