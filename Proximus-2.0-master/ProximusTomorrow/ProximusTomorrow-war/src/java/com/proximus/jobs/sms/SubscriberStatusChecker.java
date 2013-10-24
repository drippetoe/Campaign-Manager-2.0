/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jobs.sms;

import com.proximus.data.sms.Subscriber;
import com.proximus.data.util.DateUtil;
import com.proximus.manager.sms.MobileOfferSettingsManagerLocal;
import com.proximus.manager.sms.SubscriberManagerLocal;
import com.proximus.registration.client.Registrar;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author ronald
 */
public class SubscriberStatusChecker extends Thread {

    private static final Logger logger = Logger.getLogger(SubscriberStatusChecker.class.getName());
    SubscriberManagerLocal subscriberMgr;
    MobileOfferSettingsManagerLocal mosMgr;

    public SubscriberStatusChecker(SubscriberManagerLocal subscriberMgr) {
        this.subscriberMgr = subscriberMgr;
    }

    private void getOutOfSynchSubscribers(List<Subscriber> allSubscribers) {
        if (allSubscribers != null) {
            Registrar registrar = new Registrar();
            for (Subscriber subscriber : allSubscribers) {
                String locaidStatus = registrar.getSingleMsisdnStatus(subscriber.getMsisdn());
                subscriber.setLocaidStatus(locaidStatus);
                subscriber.setSynchedWithLocaid(false);
                logger.warn("Locaid status: " + locaidStatus + "\nSubscriber status: " + subscriber.getStatus());
                subscriberMgr.update(subscriber);
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                try {
                    /*Do this instead of .equals because we do not know 
                     * precisely what time the thread will run and getLastDayOfMonth 
                     * sets the time to the ending of the day
                     */

                    Date date = new Date();
                    String dateString = date.toString().substring(0, 10);
                    Date lastDayOfMonth = DateUtil.getLastDayOfMonth(date);
                    String lastDayOfMonthString = lastDayOfMonth.toString().substring(0, 10);

                    //TODO:Maybe get this in a better way but this works pretty effectively, all the work is done in the manager.
                    Date lateLookupRequest = DateUtil.getOneWeekAgo(new Date());

                    if (dateString.equals(lastDayOfMonthString)) {
                        getOutOfSynchSubscribers(subscriberMgr.findActiveSubscribersWithLateLookups(lateLookupRequest));
                    }
                } catch (Exception err) {
                    logger.fatal("SubscriberStatusChecker Exception", err);
                }
                Thread.sleep(1000 * 60 * 60 * 24); // run once a day
            } catch (InterruptedException ex) {
                logger.fatal(ex);
                return; // if sleep fails, quit the thread
            }
        }
    }
}
