package com.proximus.manager.sms;

import com.proximus.data.sms.GeoFence;
import com.proximus.data.sms.GeoPoint;
import com.proximus.data.sms.Property;
import com.proximus.manager.AbstractManager;
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
public class GeoPointManager extends AbstractManager<GeoPoint> implements GeoPointManagerLocal {

    private static final Logger logger = Logger.getLogger(GeoPointManager.class.getName());
    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public GeoPointManager() {
        super(GeoPoint.class);
    }

    @Override
    public void createGeoPoints(List<GeoPoint> geoPoints) {
        for (GeoPoint geoPoint : geoPoints) {
            this.em.persist(geoPoint);
        }
    }

    /**
     *
     *
     * @param geoPoints
     */
    @Override
    public void updateGeoPoints(List<GeoPoint> geoPoints) {
        for (GeoPoint geoPoint : geoPoints) {
            this.em.merge(geoPoint);
        }
    }

    @Override
    public void deleteGeoPoints(List<GeoPoint> geoPoints) {
        for (GeoPoint geoPoint : geoPoints) {
            this.em.remove(geoPoint);
        }
    }

    @Override
    public Double getLatitude(GeoFence geoFence) {
        Query q = em.createQuery("SELECT g.lat FROM GeoPoint g WHERE g.geoFence = ?1");
        q.setParameter(1, geoFence);
        List<Double> results = (List<Double>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public Double getLongitude(GeoFence geoFence) {
        Query q = em.createQuery("SELECT g.lng FROM GeoPoint g WHERE g.geoFence = ?1");
        q.setParameter(1, geoFence);
        List<Double> results = (List<Double>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public Integer getRadius(GeoFence geoFence) {
        Query q = em.createQuery("SELECT g.radius FROM GeoPoint g WHERE g.geoFence = ?1");
        q.setParameter(1, geoFence);
        Integer radius = (Integer) q.getSingleResult();
        return radius;
    }

    @Override
    public GeoPoint findGeoPointByLocation(Double latitude, Double longitude) {
        Query q = em.createQuery("SELECT g FROM GeoPoint g WHERE g.lat = ?1 AND g.lng =?2");
        q.setParameter(1, latitude);
        q.setParameter(2, longitude);
        List<GeoPoint> results = (List<GeoPoint>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public GeoPoint findGeoPointByGeoFenceAndProperty(GeoFence geoFence, Property property) {
        Query q = em.createQuery("SELECT g FROM GeoPoint g WHERE g.geoFence = :geoFence "
                + "AND g.geoFence.property = :property AND g.propertyPoint = :true");
        q.setParameter("geoFence", geoFence);
        q.setParameter("property", property);
        q.setParameter("true", Boolean.TRUE);
        List<GeoPoint> results = (List<GeoPoint>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }
}