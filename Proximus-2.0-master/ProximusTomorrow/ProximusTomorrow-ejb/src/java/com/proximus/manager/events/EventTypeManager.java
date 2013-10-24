/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.events;

import com.proximus.data.Company;
import com.proximus.data.License;
import com.proximus.data.User;
import com.proximus.data.events.EventType;
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
public class EventTypeManager extends AbstractManager<EventType> implements EventTypeManagerLocal {

    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EventTypeManager() {
        super(EventType.class);
    }

    @Override
    public List<EventType> getEventTypesFromCompanyAndAccessLevel(Company company, Long accessLevel) {
        Query q = em.createQuery("SELECT DISTINCT(e.eventType) FROM Event e WHERE e.company =:company AND e.eventType.accessLevel >= :accessLevel");
        q.setParameter("company", company);
        q.setParameter("accessLevel", accessLevel);
        List<EventType> results = (List<EventType>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public List<EventType> getAvailibleEventTypes(License license, User user) {
        Query q = em.createQuery("SELECT e FROM EventType e WHERE e.accessLevel >= :accessLevel AND ( LOCATE(e.license,:license)>0 OR  e.license IS NULL )");
        q.setParameter("license", license.getLicenseText());
        q.setParameter("accessLevel", user.getRole().getPriority());
        List<EventType> results = (List<EventType>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }
}
