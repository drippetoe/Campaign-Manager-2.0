package com.proximus.startup.rulesengine;

import com.proximus.data.Brand;
import com.proximus.data.Company;
import com.proximus.data.sms.*;
import com.proximus.manager.sms.GeoFenceManagerLocal;
import com.proximus.manager.sms.LocaleManagerLocal;
import java.util.Map.Entry;
import java.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author dshaw, ggaxiola
 */
public class SubscriberOfferPairingThread extends AbstractRulesEngineThread {

    private static final Logger logger = Logger.getLogger(SubscriberOfferPairingThread.class.getName());
    GeoFenceManagerLocal geoFenceMgr;
    LocaleManagerLocal localeMgr;
    List<Subscriber> allSubscribers;
    List<MobileOffer> mobileOffers;
    Map<MobileOffer, List<Subscriber>> offerMap = new HashMap<MobileOffer, List<Subscriber>>();
    Map<Subscriber, Property> subscriberToPropertyMap = new HashMap<Subscriber, Property>();
    //List Of Mobile Offers within its GeoFence
    Map<Property, List<MobileOffer>> mapOfMobileOffersByProperty = new HashMap<Property, List<MobileOffer>>();
    //Saving us DB queries on the inner loop of Setting up all the data structure mappings
    Map<Property, List<Subscriber>> propertyToSubscriberMap = new HashMap<Property, List<Subscriber>>();

    public SubscriberOfferPairingThread(Brand brand, List<Subscriber> allSubscribers, List<MobileOffer> mobileOffers, GeoFenceManagerLocal geoFenceMgr, LocaleManagerLocal localeMgr) {
        super(brand);
        this.mobileOffers = mobileOffers;
        this.allSubscribers = allSubscribers;
        this.geoFenceMgr = geoFenceMgr;
        this.localeMgr = localeMgr;
    }

    @Override
    public void run() {
        Random random = new Random(System.currentTimeMillis());

        try {
            if (allSubscribers == null || allSubscribers.isEmpty()) {
                logger.info("No Subscribers available for Pairing with Mobile Offers");
                return;
            }

            // create mapping of subscribers to the properties in which they reside
            for (Subscriber s : allSubscribers) {
                GeoFence g = geoFenceMgr.find(s.getCurrentClosestGeoFenceId());
                Property p = g.getProperty();

                subscriberToPropertyMap.put(s, p);

                if (propertyToSubscriberMap.containsKey(p)) {
                    propertyToSubscriberMap.get(p).add(s);
                } else {
                    List<Subscriber> propertySub = new ArrayList<Subscriber>();
                    propertySub.add(s);
                    propertyToSubscriberMap.put(p, propertySub);
                }

                if (s.getLocales() == null || s.getLocales().isEmpty()) {

                    /**
                     * @TODO Make the default locale dependent on the Company *
                     */
                    com.proximus.data.sms.Locale defaultLocale = localeMgr.getDefaultLocale();
                    s.addLocale(defaultLocale);
                }
            }



            // create mapping of mobile offers to the properties in which they reside
            for (MobileOffer offer : mobileOffers) {
                List<Property> activeProperties = new ArrayList(propertyToSubscriberMap.keySet());

                List<Property> offerProperties;
                //Is the Offer Retail-Wide?
                if (offer.getProperties() == null || offer.getProperties().isEmpty()) {
                    offerProperties = offer.getRetailer().getProperties();
                } else { //Is the Offer Property-Specific?
                    offerProperties = offer.getProperties();
                }

                //ActiveProperties  Are Properties where Subscribers are Located
                //OfferProperties Are Properties where offers are available
                List<Property> intersection = new ArrayList<Property>(activeProperties);
                intersection.retainAll(offerProperties);

                for (Property property : intersection) {
                    if (mapOfMobileOffersByProperty.containsKey(property)) {
                        mapOfMobileOffersByProperty.get(property).add(offer);
                    } else {
                        List<MobileOffer> propOffer = new ArrayList<MobileOffer>();
                        propOffer.add(offer);
                        mapOfMobileOffersByProperty.put(property, propOffer);
                    }
                }
            }

            for (Subscriber subscriber : allSubscribers) {
                MobileOffer finalMatchedOffer = null;

                Property subProp = subscriberToPropertyMap.get(subscriber);
                List<MobileOffer> listOfAvailableOffers = mapOfMobileOffersByProperty.get(subProp);
                List<MobileOffer> localeOfferList = new ArrayList<MobileOffer>();
                if (listOfAvailableOffers == null || listOfAvailableOffers.isEmpty()) {
                    logger.debug("No available offers for subscriber " + subscriber.getMsisdn());
                    continue;
                }

                //Step 1 check if a MobileOffer's locale matches one of the subscriber's locale
                Collections.shuffle(listOfAvailableOffers);
                for (MobileOffer mobileOffer : listOfAvailableOffers) {
                    com.proximus.data.sms.Locale offerLocale = mobileOffer.getLocale();
                    List<com.proximus.data.sms.Locale> subscriberLocales = subscriber.getLocales();
                    if (subscriberLocales.contains(offerLocale)) {
                        localeOfferList.add(mobileOffer);
                        logger.debug("Locale " + offerLocale.getName() + " matched for Offer " + mobileOffer.getId() + " and Subscriber " + subscriber.getMsisdn());
                    }
                }

                if (localeOfferList.isEmpty()) {
                    logger.debug("No locale-compatible offers for subscriber " + subscriber.getMsisdn());
                    continue;
                }

                //Step 2 check if a MobileOffer's category matches one of the subscriber's categories
                if (subscriber.getCategories() != null && !subscriber.getCategories().isEmpty()) {
                    for (MobileOffer mobileOffer : localeOfferList) {
                        List<Category> offerCategories = mobileOffer.getCategories();
                        List<Category> subscriberCategories = subscriber.getCategories();
                        if (intersection(subscriberCategories, offerCategories)) {
                            finalMatchedOffer = mobileOffer;
                            logger.info("Category matched for Offer " + mobileOffer.getId() + " and Subscriber " + subscriber.getMsisdn());
                            //No need to loop over we found a match!
                            break;
                        }
                    }
                }

                //Step 3: No Categories matched, use randomization to determine a MobileOffer to send
                if (finalMatchedOffer == null) {
                    Integer randomSubIndex = random.nextInt(localeOfferList.size());
                    finalMatchedOffer = localeOfferList.get(randomSubIndex);
                    logger.info("Matched Offer: " + finalMatchedOffer.getId() + " with Subscriber: " + subscriber.getMsisdn() + ", couldn't match by Category (using random)");
                }

                //Step 4: Add Offer to offerMap
                if (offerMap.containsKey(finalMatchedOffer)) {
                    offerMap.get(finalMatchedOffer).add(subscriber);
                } else {
                    List<Subscriber> newSubList = new ArrayList<Subscriber>();
                    newSubList.add(subscriber);
                    offerMap.put(finalMatchedOffer, newSubList);
                }

            }

        } catch (Exception err) {
            logger.fatal(err);
        }


        // at this point every subscriber should have an offer and the offerMap represents the ones to be sent
        setComplete(true);

    }

    public Map<MobileOffer, List<Subscriber>> getOfferMap() {
        return offerMap;
    }

    public List<MobileOffer> getOffersWithSubscribers() {
        List<MobileOffer> keys = new ArrayList<MobileOffer>();
        for (Entry<MobileOffer, List<Subscriber>> entry : offerMap.entrySet()) {
            MobileOffer mobileOffer = entry.getKey();
            keys.add(mobileOffer);
        }
        return keys;
    }

    public List<Subscriber> getSubscribersForOffer(MobileOffer offer) {
        return offerMap.get(offer);
    }

    /**
     * Check if there is an intersection between subscriber categoriess and
     * offer categories
     *
     * @param subscriberCat subscriber category list
     * @param offerCat offers category list
     * @return the index of Subscriber Category; @reutnr -1 if no matches
     */
    private boolean intersection(List<Category> subscriberCat, List<Category> offerCat) {
        List<Category> newList = new ArrayList<Category>(subscriberCat);
        newList.retainAll(offerCat);
        if (!newList.isEmpty()) {
            return true;
        }
        return false;
    }

//    /**
//     * Get the first intersection affinity between a Subscriber and Mobile Offer
//     * Categories First we shuffle around the list to disallow getting the same
//     * result over and over
//     *
//     * @param subscriberCat subscriber category list
//     * @param offerCat offers category list
//     * @return the index of Subscriber Category; @reutnr -1 if no matches
//     */
//    private int intersection(List<Category> subscriberCat, List<Category> offerCat) {
//        if (subscriberCat == null || offerCat == null || subscriberCat.isEmpty() || offerCat.isEmpty()) {
//            return -1;
//        }
//        Collections.shuffle(subscriberCat);
//        Collections.shuffle(offerCat);
//        for (int i = 0; i < subscriberCat.size(); i++) {
//            for (int j = 0; j < subscriberCat.size(); j++) {
//                if (subscriberCat.get(i).equals(offerCat.get(j))) {
//                    return i;
//                }
//            }
//        }
//        return -1;
//    }
    public Map<Subscriber, Property> getSubscriberToPropertyMap() {
        return subscriberToPropertyMap;
    }

    public Map<Property, List<MobileOffer>> getMapOfMobileOffersByProperty() {
        return mapOfMobileOffersByProperty;
    }

    public Map<Property, List<Subscriber>> getPropertyToSubscriberMap() {
        return propertyToSubscriberMap;
    }

    public void setPropertyToSubscriberMap(Map<Property, List<Subscriber>> propertyToSubscriberMap) {
        this.propertyToSubscriberMap = propertyToSubscriberMap;
    }
}
