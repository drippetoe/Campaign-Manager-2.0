/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package com.proximus.startup.event;

import com.proximus.data.events.Event;
import com.proximus.manager.events.EventManagerLocal;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author eric
 */
public class EventFilterThread extends Thread {

    private static final Logger logger = Logger.getLogger(EventFilterThread.class.getName());
    private static final int maxProcessed = 1000;
    private static final int processInterval = 1000 * 15;
    /*
     * EJB 
     */
    private EventManagerLocal eventMgr;

    private EventFilterThread() {
        super("EventFilterThread");
    }

    private static class EventFilterThreadHolder {

        public static final EventFilterThread instance = new EventFilterThread();
    }

    public static EventFilterThread getInstance() {
        return EventFilterThreadHolder.instance;
    }

    public EventManagerLocal getEventMgr() {
        return eventMgr;
    }

    public void setEventMgr(EventManagerLocal eventMgr) {
        this.eventMgr = eventMgr;
    }

    @Override
    public void run() {
        logger.info("Starting Event Filter");
        while (true) {
            /*
             * 0. Get all raw events
             */
            List<Event> events = eventMgr.getQueue(Event.QUEUE_RAW, maxProcessed);
            /*
             * 1. If we have raw events process them.
             */
            if (events != null && !events.isEmpty()) {
                logger.debug("Processing Raw Events (" + events.size() + ")");
                /*
                 * 2. Get ALL the Live queue.
                 */
                List<Event> liveQueue = eventMgr.getQueue(Event.QUEUE_LIVE, -1);
                /*
                 * 3. Start a new transaction.
                 */
                /*if (!em.getTransaction().isActive()) {
                 em.getTransaction().begin();*/
                try {
                    for (Event event : events) {
                        /*
                         * 4. If the events pass the filter advance the event to the live queue.
                         *  or discard it.
                         */
                        if (event.filter(liveQueue)) {
                            liveQueue.add(event);
                            event.advanceQueue();
                            logger.debug("Advancing event: " + event);
                        } else {
                            event.discard();
                            logger.debug("Discarding event: " + event);
                        };
                        /*
                         * 5. Update the event.
                         */
                        //em.merge(event);
                        eventMgr.update(event);
                    }
                    /*
                     * 6. Commit transaction
                     */
                } catch (Exception ex) {
                    /*
                     * 7. If something goes wrong rollback the transaction
                     */
                    //em.getTransaction().rollback();
                    logger.debug("Failed filtering events: " + ex.getMessage());
                }
                /*
                 * 8. If all went right commit the changes.
                 */
                /*if (em.getTransaction().isActive()) {
                 em.getTransaction().commit();
                 }*/
                //}
            }
            /*
             * 9. Sleep
             */
            try {
                Thread.sleep(processInterval);
            } catch (InterruptedException ex) {
                logger.info("Sleep interupted!");
            }
            /*
             * 10. End Loop
             */

        }
    }
}
