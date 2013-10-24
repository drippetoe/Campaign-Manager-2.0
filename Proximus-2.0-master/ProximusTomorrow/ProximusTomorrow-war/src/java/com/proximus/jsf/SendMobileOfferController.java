/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf;

import com.kazemobile.types.CampaignExt;
import com.kazemobile.types.CampaignSteps;
import com.proximus.data.sms.MobileOffer;
import com.proximus.data.sms.Subscriber;
import com.proximus.helper.util.JsfUtil;
import com.proximus.rest.sms.SMSPlatformRESTClient;
import java.io.Serializable;
import java.util.*;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.apache.log4j.Logger;

/**
 *
 * @author Angela Mercer
 */
@ManagedBean(name = "sendMobileOfferController")
@SessionScoped
public class SendMobileOfferController extends AbstractController implements Serializable {

    private static final long serialVersionUID = 1;
    //GENERAL
    private String msisdn;
    private CampaignExt testOfferCampaign;
    private CampaignSteps testOfferSteps;
    private String testOfferText;
    private String testOfferName;
    private String testCampaignMessage;
    private static final Logger logger = Logger.getLogger(SendMobileOfferController.class.getName());
    private ResourceBundle message;

    public SendMobileOfferController() {
        message = this.getHttpSession().getMessages();
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public CampaignExt getTestOfferCampaign() {
        return testOfferCampaign;
    }

    public void setTestOfferCampaign(CampaignExt testOfferCampaign) {
        this.testOfferCampaign = testOfferCampaign;
    }

    public String getTestOfferName() {
        return testOfferName;
    }

    public void setTestOfferName(String testOfferName) {
        this.testOfferName = testOfferName;
    }

    public CampaignSteps getTestOfferSteps() {
        return testOfferSteps;
    }

    public void setTestOfferSteps(CampaignSteps testOfferSteps) {
        this.testOfferSteps = testOfferSteps;
    }

    public String getTestOfferText() {
        return testOfferText;
    }

    public void setTestOfferText(String testOfferText) {
        this.testOfferText = testOfferText;
    }

    public String getTestCampaignMessage() {
        return testCampaignMessage;
    }

    public void setTestCampaignMessage(String testCampaignMessage) {
        this.testCampaignMessage = testCampaignMessage;
    }

    public String prepareSend() {
        msisdn = null;
        testOfferCampaign = null;
        testOfferName = null;
        testOfferSteps = null;
        testOfferText = null;
        testCampaignMessage = null;
        return "/sms-campaign/sendOffer?faces-redirect=true";
    }

    public String sendOffer() {
        if (msisdn == null || msisdn.isEmpty()) {
            JsfUtil.addErrorMessage(message.getString("sendMobileOfferMobileRequired"));
            return "/sms-campaign/sendOffer";
        }
        if (testOfferText == null || testOfferText.isEmpty()) {

            JsfUtil.addErrorMessage(message.getString("sendMobileOfferTextRequired"));
            return "/sms-campaign/sendOffer";
        }
        if (testOfferName == null || testOfferName.isEmpty()) {
            JsfUtil.addErrorMessage(message.getString("sendMobileOfferNameRequired"));
            return "/sms-campaign/sendOffer";
        }
        try {
            SMSPlatformRESTClient rest = new SMSPlatformRESTClient();
            List<Subscriber> subscriberList = new ArrayList<Subscriber>();

            //create demo offer
            MobileOffer offer = new MobileOffer();
            offer.setOfferText(testOfferText);
            offer.setName(testOfferName);

            Calendar now = Calendar.getInstance();
            Date launch = now.getTime();

            offer.setStartDate(launch);

            now.add(Calendar.DATE, 1);
            Date expire = now.getTime();

            offer.setEndDate(expire);

            //create subscriber list from msisdn
            Subscriber s = new Subscriber();
            s.setMsisdn(msisdn);
            subscriberList.add(s);

            //create subscriber list
            int dlist = rest.createSubscriberList(subscriberList, offer);

            //create campaign and send offer
            testOfferCampaign = rest.createTestMobileOffer(offer, dlist);
            if (testOfferCampaign != null) {
                testOfferSteps = rest.findCampaignSteps(testOfferCampaign.getCampaignId().toString());
                if (testOfferSteps != null) {
                    testCampaignMessage = testOfferSteps.getSteps().get(0).getCampaignMsg();
                }

            }
            return "/sms-campaign/sendOffer?faces-redirect=true";

        } catch (Exception ex) {
            JsfUtil.addErrorMessage(message.getString("sendMobileOfferExceptionError"));
            return "/sms-campaign/sendOffer";
        }
    }
}
