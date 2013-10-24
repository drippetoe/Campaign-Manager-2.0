/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.parsers;

import com.proximus.data.sms.Keyword;
import com.proximus.data.sms.MobileOfferSettings;
import com.proximus.data.sms.Subscriber;
import com.proximus.data.util.DateUtil;
import com.proximus.location.client.FakeLocationFinder;
import com.proximus.location.client.LocationFinder;
import com.proximus.location.client.LocationFinderLocal;
import com.proximus.manager.sms.*;
import com.proximus.registration.client.Registrar;
import com.proximus.registration.client.RegistrarResponse;
import com.proximus.rest.sms.SMSPlatformRESTClient;
import com.proximus.util.ServerURISettings;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Used for importing subscribers 1. registration and opt in to location
 * services 2. insert into subscriber table 3. look up new subscribers
 *
 * @author Angela Mercer
 */
public class SubscriberImportParser
{
    static final Logger logger = Logger.getLogger(SubscriberImportParser.class.getName());

    public int parse(File f, SubscriberManagerLocal subscriberMgr, KeywordManagerLocal keywordMgr,
            MobileOfferSettingsManagerLocal settingsMgr, LocationDataManagerLocal locationMgr, Keyword keyword)
    {
        BufferedReader csvFile = null;
        BufferedWriter bw = null;

        int parseCount = 0;
        int noMsisdnCount = 0;
        int errorCount = 0;
        int optOutCount = 0;
        int existCount = 0;
        int importedCount = 0;
        String kazeOptinStatus = "";
        String msisdn = "";
        String registrationDateString = "";
        Date registrationDate;
        Registrar r = new Registrar();
        try {
            csvFile = new BufferedReader(new FileReader(f));
            String file_separator = System.getProperty("file.separator");
            String csvFileLine;
            SimpleDateFormat formatter = new SimpleDateFormat(ServerURISettings.FILE_DATEFORMAT);
            File resultsfile = new File(ServerURISettings.SMS_USER_IMPORT_DIR + file_separator + "pr_kiosk_" + formatter.format(new Date()) + ".txt");
            if (!resultsfile.exists()) {
                resultsfile.createNewFile();
            }
            FileWriter fw = new FileWriter(resultsfile.getAbsoluteFile());
            bw = new BufferedWriter(fw);

            while ((csvFileLine = csvFile.readLine()) != null) {
                String[] details = csvFileLine.split(",");

                if (details.length < 4) {
                    logger.warn("import line did not have enough data: " + csvFileLine);
                    bw.write("import line did not have enough data: " + csvFileLine);
                    bw.newLine();
                    errorCount++;
                    parseCount++;
                    continue;
                }
                String emailAddress = details[0];
                registrationDateString = details[1];
                if (registrationDateString.isEmpty()) {
                    registrationDate = new Date();
                } else {
                     registrationDate = DateUtil.formatStringToDateForImport(registrationDateString);
                     if (registrationDate == null) {
                        registrationDate = new Date();
                    }
                }     
                msisdn = details[2];
                if (!msisdn.isEmpty()) {
                    msisdn = msisdn.replaceAll("\\D", "");
                    if (!msisdn.startsWith("1") && msisdn.length() == 10) {
                        msisdn = "1" + msisdn;
                    }
                } else {
                    logger.warn("skipping import -- no msisdn");
                    noMsisdnCount++;
                    parseCount++;
                    continue;
                }
                bw.write("importing msisdn | " + msisdn + "|");

                //1. check to see if we have this subscriber already
                Subscriber subscriber = subscriberMgr.findByMsisdn(msisdn);
                if (subscriber != null) {

                    //check to see if subscriber has already opted-out
                    if (subscriber.getStatus().equals(Subscriber.STATUS_OPT_OUT)) {
                        bw.write("msisdn: " + msisdn + " has status: " + Subscriber.STATUS_OPT_OUT);
                        optOutCount++;
                        parseCount++;
                        continue;

                        //update the subscriber with locator service information
                    } else {
                        String msisdnStatus = r.getSingleMsisdnStatus(msisdn);

                        //opt-in complete in locator service
                        if (msisdnStatus.equals(Registrar.STATUS_OPTIN_COMPLETE)) {
                            subscriber.setStatus(Subscriber.STATUS_OPT_IN_COMPLETE);
                        }

                        //opt-in pending in locator service -- attempt opt-in
                        if (msisdnStatus.equals(Registrar.STATUS_OPTIN_PENDING)) {
                            RegistrarResponse existResponse = r.singleOptIn(msisdn);
                            if (existResponse.getRegistrationCode().equals(RegistrarResponse.OPT_IN_SUCCESS_CODE)) {
                                subscriber.setStatus(Subscriber.STATUS_OPT_IN_COMPLETE);
                            } else {
                                subscriber.setStatus(Subscriber.STATUS_TRACKING_FAILED);
                            }
                        }
                        //no locator service status -- attempt registration
                        if (msisdnStatus.equals(Registrar.STATUS_NONE)) {
                            RegistrarResponse registrarResponse = r.singleMsisdnRegistration(msisdn);

                            //carrier unsupported
                            if (registrarResponse.getRegistrationCode().equals(RegistrarResponse.CARRIER_UNSUPPORTED_CODE)) {
                                subscriber.setStatus(Subscriber.STATUS_CARRIER_UNSUPPORTED);
                            }

                            //already subscribed
                            if (registrarResponse.getRegistrationCode().equals(RegistrarResponse.MSISDN_ALREADY_SUBSCRIBED_CODE)) {
                                subscriber.setStatus(Subscriber.STATUS_OPT_IN_COMPLETE);
                            }

                            //if registration is successful complete opt-in
                            if (registrarResponse.getRegistrationCode().equals(RegistrarResponse.REGISTRATION_SUCCESS_CODE)) {
                                RegistrarResponse optinResponse = r.singleOptIn(msisdn);
                                if (optinResponse.getRegistrationCode().equals(RegistrarResponse.OPT_IN_SUCCESS_CODE)) {
                                    subscriber.setStatus(Subscriber.STATUS_OPT_IN_COMPLETE);
                                } else {
                                    subscriber.setStatus(Subscriber.STATUS_TRACKING_FAILED);
                                }
                            }

                        }

                        //cancelled status in locator service
                        if (msisdnStatus.equals(Registrar.STATUS_SUBSCRIPTION_CANCELLED)) {
                           subscriber.setStatus(Subscriber.STATUS_OPT_OUT);
                        }
                        subscriberMgr.update(subscriber);
                        bw.write("location service status for existing subscriber: " + subscriber.getStatus() + "|");
                        existCount++;
                        parseCount++;
                    }
                }
                
                //subscriber doesn't exist -- new subscriber
                if (subscriber == null) {
                    subscriber = new Subscriber();
                    RegistrarResponse registrarResponse = r.singleMsisdnRegistration(msisdn);
//
                    //carrier unsupported
                    if (registrarResponse.getRegistrationCode().equals(RegistrarResponse.CARRIER_UNSUPPORTED_CODE)) {
                        subscriber.setStatus(Subscriber.STATUS_CARRIER_UNSUPPORTED);
                    }

                    //already subscribed
                    if (registrarResponse.getRegistrationCode().equals(RegistrarResponse.MSISDN_ALREADY_SUBSCRIBED_CODE)) {
                        subscriber.setStatus(Subscriber.STATUS_OPT_IN_COMPLETE);
                    }

                    //successful complete opt-in
                    if (registrarResponse.getRegistrationCode().equals(RegistrarResponse.REGISTRATION_SUCCESS_CODE)) {
                        RegistrarResponse optinResponse = r.singleOptIn(msisdn);
                        if (optinResponse.getRegistrationCode().equals(RegistrarResponse.OPT_IN_SUCCESS_CODE)) {
                            subscriber.setStatus(Subscriber.STATUS_OPT_IN_COMPLETE);
                        } else {
                            subscriber.setStatus(Subscriber.STATUS_TRACKING_FAILED);
                        }
                    }
//
                    bw.write("location service status for new subscriber: " + subscriber.getStatus() + "|");

                    //4. optin to Kaze
                    SMSPlatformRESTClient smsClient = new SMSPlatformRESTClient();
                    kazeOptinStatus = smsClient.optInMsisdn(msisdn, keyword.getKeyword(), null);
                    if (kazeOptinStatus.equals("0")) {
                        logger.warn("Error opting into Kaze!");
                    }
                    bw.write("Kaze optin msg id | " + kazeOptinStatus + "|");


                    //4b. insert into subscriber and look subscriber up
                    subscriber.setRegistrationDate(registrationDate);
                    subscriber.setMsisdn(msisdn);
                    subscriber.setBrand(keyword.getCompany().getBrand());
                    subscriber.setCompany(keyword.getCompany());
                    subscriber.setKeyword(keyword);
                    subscriber.setEmail(emailAddress);
                    subscriber.setSmsOptInDate(new Date());
                    if (subscriber.getStatus().equals(Subscriber.STATUS_OPT_IN_COMPLETE)) {
                        subscriber.setTrackingOptInDate(new Date());
                    }
//                        //lookup
//                          MobileOfferSettings settings = settingsMgr.findByBrand(subscriber.getCompany().getBrand());
//                          boolean liveLocation = settings.isLiveGeoLocation();
//                          try {
//                            logger.debug("Locating new subscriber " + subscriber.getMsisdn());
//                            List<String> msisdnList = new ArrayList<String>();
//                            msisdnList.add(subscriber.getMsisdn());
//                            LocationFinderLocal finder;
//                            if (liveLocation) {
//                                finder = new LocationFinder();
//                            } else {
//                                finder = new FakeLocationFinder();
//                            }
//                            finder.synchFind(msisdnList, 0, 0, locationMgr, subscriberMgr, subscriber.getBrand(),settings);
//                            
//                        } catch (Exception err) {
//                            logger.error(err);
//                        }
                    subscriberMgr.create(subscriber);
                    bw.write("subscriber status | imported");
                    bw.newLine();
                    importedCount++;
                    parseCount++;



                }
            }
           
        } catch (Exception ex) {
            logger.fatal(ex);
        } finally {
            try {
                csvFile.close();
                bw.write("error count: " + errorCount);
                bw.write("opt-out count " + optOutCount);
                bw.write("no msisdn count: " + noMsisdnCount);
                bw.write("already exists count: " + existCount);
                bw.write("imported count: " + importedCount);
                bw.write("total lines: " + parseCount);
                bw.close();
                
                logger.info("****import completed******"
                        + "\n" + "error count: " + errorCount
                        + "\n" + "opt-out count " + optOutCount
                        + "\n" + "no msisdn count: " + noMsisdnCount
                        + "\n" + "already exists count: " + existCount
                        + "\n" + "imported count: " + importedCount
                        + "\n" + "total lines: " + parseCount);
                return parseCount;
            } catch (Exception ex) {
                logger.fatal(ex);
                return parseCount;
            }
        }
    }
}
