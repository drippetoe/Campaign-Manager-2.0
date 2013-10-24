/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager.app;

import com.proximus.data.app.AppUserLocation;
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
public class AppUserLocationManager extends AbstractManager<AppUserLocation> implements AppUserLocationManagerLocal {

    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public AppUserLocationManager() {
        super(AppUserLocation.class);
    }

    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    @Override
    public AppUserLocation findAppUserLocation(Double latitude, Double longitude) {
        Query q = em.createQuery("SELECT l FROM AppUserLocation l WHERE l.latitude = :latitude AND l.longitude = :longitude");
        q.setParameter("latitude", latitude);
        q.setParameter("longitude", longitude);
        List<AppUserLocation> results = (List<AppUserLocation>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }
}
