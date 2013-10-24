/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.events;

import com.proximus.data.events.Event;
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
public class EventManager extends AbstractManager<Event> implements EventManagerLocal {

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

    public EventManager() {
        super(Event.class);
    }

    /**
     *
     * @param queue ex Event.QUEUE_RAW
     * @param maxResults -1 equals all results
     * @return List of events
     */
    @Override
    public List<Event> getQueue(long queue, int maxResults) {
        Query q = this.em.createQuery("SELECT e FROM Event e WHERE e.queue=:queue ORDER BY e.eventDate ASC");
        q.setParameter("queue", queue);
        if (maxResults > 0) {
            q.setMaxResults(maxResults);
        }
        List<Event> eventQueue = (List<Event>) q.getResultList();
        if (eventQueue != null && !eventQueue.isEmpty()) {
            return eventQueue;
        }
        return null;
    }
}
