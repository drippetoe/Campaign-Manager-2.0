package com.proximus.startup.event;

import com.proximus.manager.events.EventManagerLocal;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.apache.log4j.Logger;

/**
 *
 * @author eric
 */
@Singleton
@Startup
public class EventFilterStartup implements EventFilterRemote {

    private static final Logger logger = Logger.getLogger(EventFilterStartup.class.getName());
    @EJB
    private EventManagerLocal eventMgr;

    @PostConstruct
    @Override
    public void start() {
        System.out.println("EventFilterStartup");
        try {
            Thread.sleep(1000 * 15);
            EventFilterThread eft = EventFilterThread.getInstance();
            eft.setEventMgr(eventMgr);
            logger.fatal("Starting " + EventFilterStartup.class.getName());
            eft.start();
        } catch (Exception ex) {
            logger.fatal("Event Filter Startup FAILED!");
            logger.fatal(ex.getMessage());
        }
    }
}
