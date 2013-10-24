/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.startup.rulesengine;

import com.proximus.data.Brand;
import com.proximus.data.Company;
import com.proximus.data.sms.Locale;
import com.proximus.data.sms.MobileOffer;
import com.proximus.data.sms.MobileOfferMonthlyBalance;
import com.proximus.data.sms.MobileOfferSettings;
import com.proximus.manager.sms.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author dshaw
 */
public class ActiveMobileOfferLocatorThread extends AbstractRulesEngineThread {

    static final Logger logger = Logger.getLogger(ActiveMobileOfferLocatorThread.class.getName());
    final double SEND_THRESHOLD = 1.0 - .20;  // 20% or more
    MobileOfferManagerLocal mobileOfferMgr;
    MobileOfferSendLogManagerLocal mobileOfferSendLogMgr;
    MobileOfferBalanceManagerLocal mobileOfferBalanceMgr;
    MobileOfferSettingsManagerLocal mobileOfferSettingsMgr;
    LocaleManagerLocal localeMgr;
    HashMap<String, List<MobileOffer>> localeOffers = new HashMap<String, List<MobileOffer>>();
    List<Company> companies;

    public ActiveMobileOfferLocatorThread(Brand brand, List<Company> companies, MobileOfferManagerLocal mobileOfferMgr, MobileOfferSendLogManagerLocal mobileOfferSendLogMgr, MobileOfferBalanceManagerLocal mobileOfferBalanceMgr, MobileOfferSettingsManagerLocal mobileOfferSettingsMgr, LocaleManagerLocal localeMgr) {
        super(brand);
        this.companies = companies;
        this.mobileOfferMgr = mobileOfferMgr;
        this.mobileOfferSendLogMgr = mobileOfferSendLogMgr;
        this.mobileOfferBalanceMgr = mobileOfferBalanceMgr;
        this.localeMgr = localeMgr;
        this.mobileOfferSettingsMgr = mobileOfferSettingsMgr;
    }

    @Override
    public void run() {
        long maxSent = 0L;
        long minSent = 0L;
        List<MobileOffer> potentialActiveMobileOffers;
        MobileOfferSettings mos = mobileOfferSettingsMgr.findByBrand(brand);

        //Setting up HashMap of LocaleOffers
        // initialize with all locales
        if (localeMgr == null) {
            logger.error("Injection to ActiveMobileOfferLocatorThread incomplete, exiting.");
            setComplete(true);
            return;
        }

        for (Locale locale : localeMgr.findAll()) {
            localeOffers.put(locale.getLanguageCode(), new ArrayList<MobileOffer>());
        }

        //Getting all Offers Active and Approved by Brand
        try {
            potentialActiveMobileOffers = mobileOfferMgr.findActiveAndApprovedByBrand(brand);
            if (potentialActiveMobileOffers == null || potentialActiveMobileOffers.isEmpty()) {
                setComplete(true);
                return;
            }

            for (MobileOffer mobileOffer : potentialActiveMobileOffers) {
                // we use balance here instead of send count because send count doesn't allow for offers created mid month
                MobileOfferMonthlyBalance balance = mobileOfferBalanceMgr.findByMobileOfferAndMonth(mobileOffer, runStart);

                maxSent = Math.max(balance.getBalance(), maxSent);
                minSent = Math.min(balance.getBalance(), minSent);
                mobileOffer.setSendCount(balance.getBalance());
            }

            /**
             * If any offer was sent 20% or fewer times than the others, then we
             * need to "catch up" so only send those offers
             */
            long threshold = Math.round(maxSent * SEND_THRESHOLD);  // 80% of the highest send # of all offers

            for (MobileOffer mobileOffer : potentialActiveMobileOffers) {
                Locale offerLocale = mobileOffer.getLocale();
                if ((minSent < threshold) && (maxSent - minSent > mos.getMobileOfferBalance())) {
                    logger.debug("Brand " + brand.getName() + ": Some offers below the threshhold of " + threshold + ", minSent=" + minSent + ", masSent=" + maxSent);

                    if (mobileOffer.getSendCount() < threshold) {
                        localeOffers.get(offerLocale.getLanguageCode()).add(mobileOffer);

                        logger.debug("Brand " + brand.getName() + ": Adding offer " + mobileOffer.getLogName() + " to balanced send list");
                    }
                } else {
                    localeOffers.get(offerLocale.getLanguageCode()).add(mobileOffer);
                    logger.debug("Brand " + brand.getName() + ": Adding offer " + mobileOffer.getLogName() + ") to ALL send list");
                }
            }

        } catch (Exception err) {
            logger.fatal("Uncaught error in ActiveMobileOfferLocatorThread", err);
        }


        this.setComplete(true);
    }

    public List<MobileOffer> getActiveMobileOffers(Locale locale) {
        return localeOffers.get(locale.getLanguageCode());
    }

    public HashMap<String, List<MobileOffer>> getActiveOffers() {
        return localeOffers;
    }

    public Long getActiveMobileOfferCount() {
        long count = 0;
        for (List<MobileOffer> offers : localeOffers.values()) {
            count += offers.size();
        }

        return count;
    }
}
