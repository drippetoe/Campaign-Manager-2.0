package com.proximus.startup.rulesengine;

import com.proximus.data.Brand;
import com.proximus.data.DayParts;
import com.proximus.data.ZipcodeToTimezone;
import com.proximus.data.sms.*;
import com.proximus.manager.ZipcodeToTimezoneManagerLocal;
import com.proximus.manager.sms.RetailerManagerLocal;
import com.proximus.util.TimeConstants;
import com.proximus.util.TimeZoneUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * This thread, when run, finds all active day parts across all companies It can
 * be used to determine whether any Mobile Offers can be sent at any given time
 *
 * @author dshaw
 */
public class ActiveDayPartLocatorThread extends AbstractRulesEngineThread {

    static final Logger logger = Logger.getLogger(ActiveDayPartLocatorThread.class);
    MobileOfferSettings settings;
    HashMap<String, List<MobileOffer>> potentialMobileOffers;
    HashMap<String, List<MobileOffer>> activeMobileOffers;
    private ZipcodeToTimezoneManagerLocal zipcodeToTimezoneMgr;
    private RetailerManagerLocal retailerMgr;

    public ActiveDayPartLocatorThread(Brand brand, MobileOfferSettings settings, HashMap<String, List<MobileOffer>> potentialMobileOffers, ZipcodeToTimezoneManagerLocal zipcodeToTimezoneMgr, RetailerManagerLocal retailerMgr) {
        super(brand);
        this.settings = settings;
        this.potentialMobileOffers = potentialMobileOffers;
        this.zipcodeToTimezoneMgr = zipcodeToTimezoneMgr;
        this.retailerMgr = retailerMgr;
        activeMobileOffers = new HashMap<String, List<MobileOffer>>();

    }

    @Override
    public void run() {

        if (potentialMobileOffers != null && !potentialMobileOffers.isEmpty()) {
            for (List<MobileOffer> localeOffers : potentialMobileOffers.values()) {
                for (MobileOffer offer : localeOffers) {
                    Locale offerLocale = offer.getLocale();
                    List<DayParts> dayParts = offer.getDayParts();
                    //Property Specific Offers we can get the actual timezone
                    ZipcodeToTimezone tz;
                    if (offer.getProperties() != null && !offer.getProperties().isEmpty()) {
                        tz = zipcodeToTimezoneMgr.getByZipcode(offer.getProperties().get(0).getZipcode());
                        //Checking if Mobile Offer has specific Day Parts or we need to use the Mobile Offer Settings DayPart
                        if (dayParts == null || dayParts.isEmpty()) {
                            dayParts = settings.getDayParts();
                        }
                        for (DayParts dayPart : dayParts) {
                            dayPart.initialize();
                            String localeStartTime = (tz != null) ? TimeZoneUtil.dayPartFromLocaleToUTC(dayPart.getStartTime(), tz.getTimezone(), tz.isUsesDaylightSavings()) : dayPart.getStartTime();
                            String localeEndTime = (tz != null) ? TimeZoneUtil.dayPartFromLocaleToUTC(dayPart.getEndTime(), tz.getTimezone(), tz.isUsesDaylightSavings()) : dayPart.getEndTime();
                            boolean active = TimeConstants.isWithinTimeAndRange(runStart, localeStartTime, localeEndTime, dayPart.getDaysOfWeek());

                            if (active) {
                                logger.debug(offer.getLogString() + ", ACTIVE in " + dayPart.getLogString());

                                if (!activeMobileOffers.containsKey(offerLocale.getLanguageCode())) {
                                    activeMobileOffers.put(offerLocale.getLanguageCode(), new ArrayList<MobileOffer>());
                                }

                                activeMobileOffers.get(offerLocale.getLanguageCode()).add(offer);
                                break;
                            } else {
                                logger.debug(offer.getLogString() + ", INACTIVE in " + dayPart.getLogString());
                            }
                        }
                    } else {
                        //RETAIL WIDE MOBILE OFFERS THAT DO NOT HAVE SPECIFIC PROPERTY
                        //Need to check for each Property that has this mobileOffer if it is possible to use it according to the DayPart
                        boolean foundAPropertyActive = false;
                        Retailer ret = retailerMgr.getRetailerByName(brand, offer.getRetailer().getName());
                        List<Property> properties = ret.getProperties();
                        for (Property property : properties) {
                            tz = zipcodeToTimezoneMgr.getByZipcode(property.getZipcode());
                            if (dayParts == null || dayParts.isEmpty()) {
                                dayParts = settings.getDayParts();
                            }
                            for (DayParts dayPart : dayParts) {
                                dayPart.initialize();
                                String localeStartTime = (tz != null) ? TimeZoneUtil.dayPartFromLocaleToUTC(dayPart.getStartTime(), tz.getTimezone(), tz.isUsesDaylightSavings()) : dayPart.getStartTime();
                                String localeEndTime = (tz != null) ? TimeZoneUtil.dayPartFromLocaleToUTC(dayPart.getEndTime(), tz.getTimezone(), tz.isUsesDaylightSavings()) : dayPart.getEndTime();
                                boolean active = TimeConstants.isWithinTimeAndRange(runStart, localeStartTime, localeEndTime, dayPart.getDaysOfWeek());

                                if (active) {
                                    logger.debug(offer.getLogString() + ", ACTIVE in " + dayPart.getLogString());
                                    if (!activeMobileOffers.containsKey(offerLocale.getLanguageCode())) {
                                        activeMobileOffers.put(offerLocale.getLanguageCode(), new ArrayList<MobileOffer>());
                                    }

                                    activeMobileOffers.get(offerLocale.getLanguageCode()).add(offer);
                                    foundAPropertyActive = true;
                                    break;
                                } else {
                                    logger.debug(offer.getLogString() + ", INACTIVE in " + dayPart.getLogString());
                                }
                            }
                            if (foundAPropertyActive) {
                                break;
                            }
                        }
                    }

                }

            }
        }

        setComplete(true);
    }

    /**
     *
     * @return True if thread has completed and result is one or more active day
     * parts
     */
    public boolean hasActiveOffers() {
        if (!isComplete()) {
            throw new RuntimeException("Daypart calculation not complete yet");
        }

        return (activeMobileOffers.size() > 0);
    }

    public HashMap<String, List<MobileOffer>> getActiveOffers() {
        if (!isComplete()) {
            throw new RuntimeException("Daypart calculation not complete yet");
        }

        return (activeMobileOffers);
    }

    /**
     *
     * @return all active offers for all locales in a single list
     */
    public List<MobileOffer> getAllActiveOffersList()
    {
        ArrayList<MobileOffer> allActive = new ArrayList<MobileOffer>();
        for (List<MobileOffer> offerList : activeMobileOffers.values()) {
            allActive.addAll(offerList);
        }
        return allActive;
    }

    public ZipcodeToTimezoneManagerLocal getZipcodeToTimezoneMgr() {
        return zipcodeToTimezoneMgr;
    }

    public void setZipcodeToTimezoneMgr(ZipcodeToTimezoneManagerLocal zipcodeToTimezoneMgr) {
        this.zipcodeToTimezoneMgr = zipcodeToTimezoneMgr;
    }
}
