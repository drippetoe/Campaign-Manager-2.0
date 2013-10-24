/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.startup.sms;

import com.proximus.jobs.sms.SubscriberStatusChecker;
import com.proximus.manager.sms.SubscriberManagerLocal;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.apache.log4j.Logger;

/**
 *
 * @author ronald
 */
@Singleton
@Startup
public class SubscriberStatusCheckerStartup implements SubscriberStatusCheckerEJBRemote {

    private static final Logger logger = Logger.getLogger(SubscriberStatusCheckerStartup.class.getName());
    @EJB
    SubscriberManagerLocal subscriberMgr;

    @PostConstruct
    @Override
    public void start() {
        logger.info("Starting SubscriberStatusCheckerStartup");
        Thread factoryThread = new Thread(new SubscriberStatusChecker(subscriberMgr), "Subscriber Status Checker");
        factoryThread.start();
        logger.info("SubscriberStatusCheckerStartup started on " + new Date());
    }
}
