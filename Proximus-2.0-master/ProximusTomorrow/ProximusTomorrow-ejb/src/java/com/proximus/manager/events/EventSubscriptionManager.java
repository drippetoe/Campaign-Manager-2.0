/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.events;

import com.proximus.data.User;
import com.proximus.data.events.EventSubscription;
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
public class EventSubscriptionManager extends AbstractManager<EventSubscription> implements EventSubscriptionManagerLocal {

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

    public EventSubscriptionManager() {
        super(EventSubscription.class);
    }

    @Override
    public List<EventSubscription> getEventSubscriptionByUser(User user) {
        Query q = em.createQuery("SELECT DISTINCT(es) FROM EventSubscription es WHERE es.user = :user");
        q.setParameter("user", user);
        List<EventSubscription> results = (List<EventSubscription>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }
}
