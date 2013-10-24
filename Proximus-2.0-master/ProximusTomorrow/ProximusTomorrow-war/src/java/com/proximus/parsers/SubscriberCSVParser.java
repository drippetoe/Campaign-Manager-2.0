/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.parsers;

import com.proximus.data.Company;
import com.proximus.data.sms.Carrier;
import com.proximus.data.sms.Subscriber;
import com.proximus.data.sms.SubscriberCampaign;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.sms.SMSCampaignManagerLocal;
import com.proximus.manager.sms.SubscriberCampaignManagerLocal;
import com.proximus.manager.sms.SubscriberManagerLocal;
import com.proximus.manager.sms.CarrierManagerLocal;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author ronald
 */
public class SubscriberCSVParser {

    static final Logger logger = Logger.getLogger(SubscriberCSVParser.class.getName());

    public int parse(File f, SubscriberManagerLocal subscriberMgr, SubscriberCampaignManagerLocal subscriberCampaignMgr, CompanyManagerLocal companyMgr,
            SMSCampaignManagerLocal smsCampaignMgr, CarrierManagerLocal carrierMgr, Company company) {
        BufferedReader csvFile = null;
        int parseCount = 0;
        try {
            csvFile = new BufferedReader(new FileReader(f));
            String csvFileLine;

            DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            while ((csvFileLine = csvFile.readLine()) != null) {
                String[] details = csvFileLine.split(",");

                if (details.length < 6) {
                    logger.debug("Line was too short: " + csvFileLine);
                    continue;
                }

                String phoneNumber = details[0];
                String carrier = details[1];
                Carrier myCarrier = new Carrier();
                if(carrier != null) {
                   myCarrier = carrierMgr.find(carrier);    
                }
                boolean smsOptIn = Boolean.valueOf(details[2]);
                boolean trackingOptIn = Boolean.valueOf(details[3]);


                Date registrationDate = formatter.parse(details[4]);
                Long smsCampaign = Long.parseLong(details[5]);
                

                Subscriber subscriber = new Subscriber(registrationDate, phoneNumber, smsOptIn, trackingOptIn, myCarrier);

                if (company != null) {
                    subscriber.setCompany(company);
                    subscriber.setBrand(company.getBrand());
                }

                subscriberMgr.create(subscriber);

                SubscriberCampaign subscriberCampaign = new SubscriberCampaign();
                subscriberCampaign.setSubscriber(subscriber);
                subscriberCampaign.setSmsOptIn(smsOptIn);
                subscriberCampaign.setTrackingOptIn(trackingOptIn);
                subscriberCampaign.setSmscampaign(smsCampaignMgr.find(smsCampaign));
                subscriberCampaignMgr.create(subscriberCampaign);

                parseCount++;
            }
        } catch (IOException ex) {
            logger.fatal(ex);
        } finally {
            try {
                csvFile.close();

                return parseCount;
            } catch (IOException ex) {
                logger.fatal(ex);
                return parseCount;
            }
        }
    }
}
