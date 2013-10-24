/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.rest.sms;

import com.kazemobile.helper.CampaignHelper;
import com.kazemobile.helper.DistributionHelper;
import com.kazemobile.helper.KeywordHelper;
import com.kazemobile.types.Keyword;
import com.kazemobile.types.CampaignPromptParam;
import com.kazemobile.types.CampaignPrompts;
import com.kazemobile.types.CampaignExt;
import com.kazemobile.types.DistributionListEntryExts;
import com.kazemobile.types.DistributionListEntryExt;
import com.kazemobile.types.DistributionListExt;
import com.kazemobile.types.CampaignStep;
import com.kazemobile.types.CampaignSteps;
import com.kazemobile.types.CampaignKeywordAlias;
import com.kazemobile.types.CampaignTimeZones;
import com.kazemobile.types.CampaignNotice;
import com.kazemobile.types.CampaignNotificationType;
import com.kazemobile.types.CustomizedSystemMessage;
import com.kazemobile.types.ReservedKeywordType;
import com.proximus.data.Company;
import com.proximus.data.sms.*;
import com.proximus.manager.sms.MasterCampaignManagerLocal;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.sms.KeywordManagerLocal;
import com.proximus.util.RSAUtils;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.LoggingFilter;
import java.io.*;
import java.math.BigInteger;
import java.util.*;
import javax.ejb.EJB;
import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;

/**
 * this client: reserves a keyword (checks availability) creates a distribution
 * list to send text messages creates a campaign
 *
 * @author Angela Mercer
 */
public class SMSPlatformRESTClient
{
    private static final Logger logger = Logger.getLogger(SMSPlatformRESTClient.class.getName());
    private Client client;
    private static final String HTTP_BASE_URI = "http://sms.kazemobile.com:41919/partners/api/";
    private String accountSignature = "";
    private static final int VALU_KEYWORD_ID = 267;
    private static final String SHORT_CODE_247365 = "247365";
    private static final String SHORT_CODE_247365_ID = "2";
    public static final String DIR = System.getProperty("file.separator");
    public static final String PROXIMUS_ROOT = DIR + "home" + DIR + "proximus";
    public static final String PROXIMUS_KEYS = PROXIMUS_ROOT + DIR + "server" + DIR + "keys";
    private static final int PROXIMUS_ACCT_ID = 20;
    private static final int PROXIMUS_USER_ID = 37;
    private static final int EN_MASTER_CAMPAIGN_ID = 492;
    private static final int ES_MASTER_CAMPAIGN_ID = 1203;

    public Client getClient()
    {
        if (client == null) {
            client = generateClient();
        }
        return client;
    }

    public void setClient(Client client)
    {
        this.client = client;
    }

    public String getAccountSignature()
    {
        if (accountSignature == null || accountSignature.isEmpty()) {
            accountSignature = buildAuthenticationSignature();
        }
        return accountSignature;
    }

    public void setAccountSignature(String accountSignature)
    {
        this.accountSignature = accountSignature;
    }

    private Client generateClient()
    {
        client = Client.create();
        client.addFilter(new LoggingFilter());
        return client;
    }

    private String buildAuthenticationSignature()
    {
        RSAUtils rsaUtils = new RSAUtils();
        try {//C:\keys
            BigInteger[] privKeyComps = readKeyComponentsFromFile(PROXIMUS_KEYS + "/proximus_private.key");
            BigInteger[] pubKeyComps = readKeyComponentsFromFile(PROXIMUS_KEYS + "/kmi_server_public.key");
            Integer proximusAccountId = PROXIMUS_ACCT_ID;
            Integer proximusUserId = PROXIMUS_USER_ID;
            String proximusApiToken = "52842040-90f1-4b73-9af3-f883008e49fb";
            String accountCredentials = proximusAccountId.toString() + ":"
                    + proximusApiToken + ":"
                    + proximusUserId;
            rsaUtils.setDecryptComponents(privKeyComps[0], privKeyComps[1]);
            rsaUtils.setEcryptComponents(pubKeyComps[0], pubKeyComps[1]);
            String signature = RSAUtils.toHexString(rsaUtils.encrypt(accountCredentials.getBytes()));
            return signature;
        } catch (Exception exp) {
            exp.printStackTrace();
            return null;
        }

    }

    /**
     * reserve a keyword on the KAZE platform
     *
     * @param keyword
     * @return String Kaze keyword id TODO: Need a way to specify which
     * shortCode to use (currently is hard coded to 247365)
     */
    public String reserveKeyword(String keyword, MobileOfferSettings mos)
    {
        String myResponse = null;

        /*
         * check for availability
         */
        if (keywordAvailable(keyword, mos.getShortCode())) {
//        if (keywordAvailable(keyword, "247365")) {    
            System.out.println("mos short code: " + mos.getShortCode().getKazeShortCodeId());

            /*
             * reserve keyword
             */
            KeywordHelper helper = new KeywordHelper();
            Keyword keywordRequest = new Keyword();
            keywordRequest.setKeyword(keyword);
            keywordRequest.setShortCodeId(Integer.parseInt(mos.getShortCode().getKazeShortCodeId()));
            try {
                KeywordResponse response = helper.create_XML(getAccountSignature(), KeywordResponse.class, keywordRequest);
                myResponse = response.getKeywordId();
                logger.info("returned keyword id: " + myResponse);
                helper.close();
            } catch (Exception e) {
                logger.fatal(e);
                return null;
            }
        }
        return myResponse;
    }

    /**
     * check to see if keyword/short code combo is available
     *
     * @param keyword
     * @param shortCode
     * @return boolean
     */
    public boolean keywordAvailable(String keyword, ShortCode shortCode)
    {
        boolean available = false;
        KeywordHelper keywords = new KeywordHelper();
        String clientResponse = keywords.keywordMatchCount(getAccountSignature(), keyword, (shortCode.getShortCode() + ""));
        Integer keywordCount = Integer.parseInt(clientResponse);
        if (keywordCount > 0) {
            System.out.println(keyword + " is not available.");
            return available;
        } else {
            System.out.println(keyword + " is available");
            available = true;
        }
        return available;
    }

    /**
     * check to see if keyword/short code combo is available
     *
     * @param String keyword
     * @param String shortcode
     * @return boolean
     */
    public boolean keywordAvailable(String keyword, String shortCode)
    {
        boolean available = false;
        KeywordHelper keywords = new KeywordHelper();
        String clientResponse = keywords.keywordMatchCount(getAccountSignature(), keyword, (shortCode + ""));
        Integer keywordCount = Integer.parseInt(clientResponse);
        if (keywordCount > 0) {
            System.out.println(keyword + " is not available.");
            return available;
        } else {
            System.out.println(keyword + " is available");
            available = true;
        }
        return available;
    }

    public void getKeywordList()
    {
        KeywordHelper keywordHelper = new KeywordHelper();
        String response = keywordHelper.findAll_XML(String.class, getAccountSignature());


    }

    /**
     * calls Kaze platform to optin a subscriber to the master campaign campaign
     *
     * @param msisdn
     */
    public String optInMsisdn(String msisdn, String keyword, String carrier)
    {
        String msgId = "0";
        if (carrier == null || carrier.isEmpty()) {
            carrier = "0";
        }
        CampaignHelper campaignHelper = new CampaignHelper();
        CampaignPromptParam params = new CampaignPromptParam();
        params.setCarrier(carrier);
        params.setKeyword(keyword);
        params.setShortCode(SHORT_CODE_247365);
        params.setEndpoint(msisdn);
        try{
            msgId = campaignHelper.promptCampaignEvent_XML(getAccountSignature(), String.class, params, CampaignPrompts.OptIn.value());
            logger.warn("Kaze Opt-In Message Id: " + msgId);
            return msgId;
        }catch(Exception ex){    
            logger.fatal("Exception interface optInMsisdn: ", ex);
            return msgId;
        }    
    }

    /**
     * calls Kaze platform and removes a subscriber who has opted out of the
     * valu campaign this only removes a subscriber from the master distribution
     * list for campaign id 492
     *
     * @param msisdn
     */
    public void optOutMsisdn(String msisdn)
    {
        CampaignHelper campaignHelper = new CampaignHelper();
        DistributionHelper distributionHelper = new DistributionHelper();

        //1. find campaign -- this is the valutext master campaign(492)
        CampaignExt myCampaign = campaignHelper.findCampaign_XML(getAccountSignature(), CampaignExt.class, Integer.toString(EN_MASTER_CAMPAIGN_ID));

        //2. find the distribution list id of the campaign
        int distributionListId = myCampaign.getDistributionListId();
        DistributionListEntryExts listEntries = distributionHelper.findDistributionListEntries_XML(getAccountSignature(), DistributionListEntryExts.class, Integer.toString(distributionListId));

        //3. search distribution list for msisdn and get distribution list entry id
        for (DistributionListEntryExt entry : listEntries.getEntries()) {
            String mobile = entry.getMobile();
            if (mobile != null) {
                if (mobile.equals(msisdn)) {
                    logger.info("Removing: " + entry.getMobile() + " with dist list entry id: " + entry.getDistributionListEntryId());
                    //4. call Kaze to remove distribution list entry id from distribution list
                    distributionHelper.removeDistributionListEntry(getAccountSignature(), Integer.toString(distributionListId), Long.toString(entry.getDistributionListEntryId()));
                    break;
                }
            }
        }
        distributionHelper.close();
    }

    static BigInteger[] readKeyComponentsFromFile(String keyFileName) throws IOException
    {
        BigInteger[] components = new BigInteger[2];

        File file = new File(keyFileName);
        InputStream in = new FileInputStream(file);
        ObjectInputStream oin = new ObjectInputStream(
                new BufferedInputStream(in));
        try {
            components[0] = (BigInteger) oin.readObject();
            components[1] = (BigInteger) oin.readObject();

            return components;
        } catch (Exception e) {
            throw new RuntimeException("Spurious serialisation error", e);
        } finally {
            oin.close();
        }
    }

    /**
     * this method creates a KAZE campaign keyword, shortcode and distro list
     * need to be built first
     *
     * * @param MobileOfferSettings
     * @param MobileOffer
     * @param distributionListId
     * @return campaign id
     *
     */
    public Long createMobileOffer(MobileOfferSettings mos, MobileOffer offer, String webHash, int distributionListId, Property property)
    {
        Long campaignId;
        CampaignHelper campaigns = new CampaignHelper();
        CampaignExt campaign = new CampaignExt();
        campaign.setAccountId(PROXIMUS_ACCT_ID);

        

        campaign.setKeywordId(VALU_KEYWORD_ID);
        campaign.setShortCodeId(Integer.parseInt(mos.getShortCode().getKazeShortCodeId()));

        //template id = 2 for standard campaign; template id = 1 for broadcast campaign
        campaign.setCampaignTemplateId(1);
        campaign.setDistributionListId(distributionListId);
        campaign.setName(offer.getName());
        campaign.setDescription(offer.getName());
        campaign.setLaunchOn(offer.getStartDate());
        campaign.setExpiresOn(offer.getEndDate());
        CampaignExt newCampaign = campaigns.createCampaign_XML(getAccountSignature(), CampaignExt.class, campaign);
        System.out.println("[Campaign ID: " + newCampaign.getCampaignId() + "]");
        campaignId = newCampaign.getCampaignId();

        // add a step to the campaign. -- message
        CampaignStep step = new CampaignStep();
        step.setCampaignId(newCampaign.getCampaignId());
        //Dynamically adding the webHash
        String realOffer = null;
        if (webHash != null && mos.getOfferUrl() != null && !mos.getOfferUrl().isEmpty()) {
            realOffer = offer.getOfferWithWebHash(mos.getOfferUrl(), webHash);
            if (realOffer == null) {
                realOffer = offer.getCleanOfferText();
            }
        }

        //Dynamically adding the Property's Name and Address
        realOffer = realOffer.replaceAll(Property.ADDRESS_REGEX, property.getAddress());
        realOffer = realOffer.replaceAll(Property.PROPERTY_REGEX, property.getName());
        logger.info("Sending Offer (" + offer.getId() + ") " + offer.getName() + " of length: " + realOffer.length() + " with text: [" + realOffer + "]");
        step.setCampaignMsg(realOffer);
        step.setStepIndex(1);
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 6);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        step.setLaunchOn(cal.getTime());
        step.setTimeZoneId(CampaignTimeZones.Eastern);

        CampaignStep newStep = campaigns.createCampaignStep_XML(getAccountSignature(), CampaignStep.class, step, newCampaign.getCampaignId().toString());
        System.out.println("[Step ID: " + newStep.getCampaignStepId() + "]");

        //notice for delivered status
        CampaignNotice deliveryNotice = new CampaignNotice();
        deliveryNotice.setCampaignId(newCampaign.getCampaignId());
        deliveryNotice.setCampaignStepId(newStep.getCampaignStepId());

        deliveryNotice.setNotificationEndpoint("http://dev.proximusmobility.net:8080/ProximusTomorrow-war/api/smsplatform");
        deliveryNotice.setNotificationMessage("campaign-id=%campaign-id%&msisdn=%campaign-endpoint%&carrier-id=%carrier-id%&status=DELIVERED");
        deliveryNotice.setNotificationTrigger(CampaignNotificationType.AutoNotifyHttpPost);

        CampaignNotice newDelNotice = campaigns.createCampaignNotice_XML(getAccountSignature(), CampaignNotice.class, deliveryNotice, newCampaign.getCampaignId().toString());
        System.out.println("[Delivery Notice ID: " + newDelNotice.getCampaignNoticeId() + "]");

        campaigns.close();
        return campaignId;

    }

    public CampaignExt createTestMobileOffer(MobileOffer offer, int distributionListId)
    {
        CampaignExt newCampaign = null;
        CampaignHelper campaigns = new CampaignHelper();
        CampaignExt campaign = new CampaignExt();
        campaign.setAccountId(PROXIMUS_ACCT_ID);
        campaign.setKeywordId(VALU_KEYWORD_ID);
        campaign.setShortCodeId(Integer.parseInt(SHORT_CODE_247365_ID));

        //template id = 2 for standard campaign; template id = 1 for broadcast campaign
        campaign.setCampaignTemplateId(1);
        campaign.setDistributionListId(distributionListId);
        campaign.setName(offer.getName());
        campaign.setDescription(offer.getName());
        campaign.setLaunchOn(offer.getStartDate());
        campaign.setExpiresOn(offer.getEndDate());
        try {
            newCampaign = campaigns.createCampaign_XML(getAccountSignature(), CampaignExt.class, campaign);
            System.out.println("[Campaign ID: " + newCampaign.getCampaignId() + "]");
        } catch (UniformInterfaceException ex) {
            newCampaign = null;
            System.out.println("Exception in createTestMobileOffer: " + ex);
            return newCampaign;
        }

        // add a step to the campaign. -- message
        CampaignStep step = new CampaignStep();
        step.setCampaignId(newCampaign.getCampaignId());
        step.setCampaignMsg(offer.getOfferText());
        step.setStepIndex(1);
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 6);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        step.setLaunchOn(cal.getTime());
        step.setTimeZoneId(CampaignTimeZones.Eastern);
        try {
            CampaignStep newStep = campaigns.createCampaignStep_XML(getAccountSignature(), CampaignStep.class, step, newCampaign.getCampaignId().toString());
            System.out.println("[Step ID: " + newStep.getCampaignStepId() + "]");
        } catch (UniformInterfaceException exStep) {
            newCampaign = null;
            System.out.println("Exception in creating test mobile offer campaign step: " + exStep);
            return newCampaign;
        }
        campaigns.close();
        return newCampaign;
    }

    public CampaignSteps findCampaignSteps(String campaignId)
    {
        CampaignHelper campaignHelper = new CampaignHelper();
        CampaignSteps steps = null;
        try {
            steps = campaignHelper.findCampaignSteps_XML(getAccountSignature(), CampaignSteps.class, campaignId);
            System.out.println("steps count: " + steps.getSteps().size());
        } catch (UniformInterfaceException ex) {
            System.out.println("Exception in findCampaignSteps: " + ex);
            campaignHelper.close();
            return steps;
        }
        return steps;
    }

    public void findCampaign(String campaignId)
    {
        CampaignHelper campaignHelper = new CampaignHelper();
        CampaignExt myCampaign = campaignHelper.findCampaign_XML(getAccountSignature(), CampaignExt.class, campaignId);
        System.out.println("[my campaign name: " + myCampaign.getName() + "]");
        campaignHelper.close();


    }

    public String createKeywordAlias(com.proximus.data.sms.Keyword keywordAlias, MobileOfferSettings mos,
            MasterCampaignManagerLocal masterCampaignMgr)
    {
        String aliasId = null;
        System.out.println("Keyword:" + keywordAlias.getKeyword());
        System.out.println("Keyword Locale: " + keywordAlias.getLocale().toString());
        if (keywordAvailable(keywordAlias.getKeyword(), mos.getShortCode().getShortCode().toString())) {
            CampaignHelper campaignHelper = new CampaignHelper();
            CampaignKeywordAlias alias = new CampaignKeywordAlias();
            if (masterCampaignMgr == null) {
                System.out.println("master campaign manager is null");
            }
            MasterCampaign mc = masterCampaignMgr.getMasterCampaignByLocale(keywordAlias.getLocale());
            if (mc != null) {
                alias.setCampaignId(Integer.parseInt(mc.getKaze_id()));
            } else {
                logger.info("master campaign is null, setting master campaign to English");
                alias.setCampaignId(EN_MASTER_CAMPAIGN_ID);
            }
            alias.setKeyword(keywordAlias.getKeyword());
            try {
                CampaignKeywordAlias newAlias = campaignHelper.createCampaignKeywordAlias_XML(getAccountSignature(),
                        CampaignKeywordAlias.class, alias, Long.toString(alias.getCampaignId()));
                System.out.println("New element created, id: " + newAlias.getCampaignKeywordAliasId());
                aliasId = newAlias.getCampaignKeywordAliasId().toString();
            } catch (Exception ex) {
                logger.error("Exception createMasterCampaignAlias(): " + ex);
            } finally {
                return aliasId;
            }
        }
        return aliasId;
    }

    /**
     *
     * @return list of distribution list ids
     */
    public List<String> findAllDistributionLists()
    {
        Client otherClient = getClient();
        String myURL = HTTP_BASE_URI + "distribution-lists";
        WebResource webResource = otherClient.resource(myURL);
        String myResponse = webResource.header("x-account-signature", getAccountSignature()).accept(MediaType.APPLICATION_XML).get(String.class);
        System.out.println("find all dist list response: " + myResponse);
        close();

        return null;
    }

    /**
     * this method should only be used for the master campaign
     *
     */
    public void createWelcomeCampaignNotifications()
    {
        //notice for opt-in/out
        CampaignHelper helper = new CampaignHelper();
        CampaignNotice optinNotice = new CampaignNotice();
        optinNotice.setCampaignId(EN_MASTER_CAMPAIGN_ID);
        optinNotice.setCampaignStepId(0);
        optinNotice.setNotificationEndpoint("http://dev.proximusmobility.net:8080/ProximusTomorrow-war/api/optinoptout");
        optinNotice.setNotificationMessage("msisdn=%campaign-endpoint%&message-body=%message-body%");
        optinNotice.setNotificationTrigger(CampaignNotificationType.AutoNotifyHttpPost);

        CampaignNotice newDelNotice = helper.createCampaignNotice_XML(getAccountSignature(), CampaignNotice.class, optinNotice, Integer.toString(EN_MASTER_CAMPAIGN_ID));
        System.out.println("[Delivery Notice ID: " + newDelNotice.getCampaignNoticeId() + "]");
    }

    public void updateCampaignNotification(String campaignId, String noticeId)
    {
        CampaignHelper helper = new CampaignHelper();
        CampaignNotice updateNotice = new CampaignNotice();
        updateNotice.setCampaignId(EN_MASTER_CAMPAIGN_ID);
        updateNotice.setCampaignStepId(0);
        updateNotice.setNotificationEndpoint("http://dev.proximusmobility.net:8080/ProximusTomorrow-war/api/optinoptout");
        updateNotice.setNotificationMessage("msisdn=%campaign-endpoint%&message-body=%message-body%&carrier-id=%carrier-id%");
        updateNotice.setNotificationTrigger(CampaignNotificationType.AutoNotifyHttpPost);
        try {
            helper.updateCampaignNotice_XML(getAccountSignature(), updateNotice, campaignId, noticeId);
            helper.close();
        } catch (Exception e) {
            System.out.println("Exception updating campaign notice: " + e);
        }
    }

    public void createStopCustomizedMessage()
    {
        CampaignHelper helper = new CampaignHelper();
        CustomizedSystemMessage message = new CustomizedSystemMessage();

        //change alias to whatever the word for stop is in the new language
        message.setAlias("PARAR");
        //change to language master campaign id
        message.setCampaignId(ES_MASTER_CAMPAIGN_ID);
        message.setKeyword(ReservedKeywordType.Stop);
        message.setMessage("Nos duele ver que te vas. Has optado "
                + "por no participar y no recibirás más mensajes. "
                + "Envía tus comentarios a contacto@centrosisla.com");
        message.setShortCodeId(Integer.parseInt(SHORT_CODE_247365_ID));
        CustomizedSystemMessage newMessage = helper.createCustomizedSystemMessage_XML(getAccountSignature(), CustomizedSystemMessage.class, message, Integer.toString(ES_MASTER_CAMPAIGN_ID));
        logger.info("new stop customized message id: " + newMessage.getCustomizedSystemMessageId());
    }

    public void createHelpCustomizedMessage()
    {
        CampaignHelper helper = new CampaignHelper();
        CustomizedSystemMessage message = new CustomizedSystemMessage();

        //change alias to whatever the word for help is in the new language
        message.setAlias("AYUDA");
        //change to language master campaign id
        message.setCampaignId(ES_MASTER_CAMPAIGN_ID);
        message.setKeyword(ReservedKeywordType.Help);
        message.setMessage("Envía un correo a contacto@centrosisla.com "
                + "para apoyo técnico. Textea PARAR para cancelar. "
                + "Cargos por mensajes y data pueden aplicar.");
        message.setShortCodeId(Integer.parseInt(SHORT_CODE_247365_ID));
        CustomizedSystemMessage newMessage = helper.createCustomizedSystemMessage_XML(getAccountSignature(), CustomizedSystemMessage.class, message, Integer.toString(ES_MASTER_CAMPAIGN_ID));
        logger.info("new help customized message id: " + newMessage.getCustomizedSystemMessageId());
    }

    public String findCampaignNotices(String campaignId)
    {
        CampaignHelper helper = new CampaignHelper();
        String myResponse = helper.findCampaignNotices_XML(getAccountSignature(), String.class, campaignId);
        return myResponse;
    }

    /**
     * releases a keyword on the kaze platform
     */
    public boolean releaseKeywordAlias(com.proximus.data.sms.Keyword keyword,
            MasterCampaignManagerLocal campaignMgr)
    {
        boolean released = false;
        MasterCampaign mc = campaignMgr.getMasterCampaignByLocale(keyword.getLocale());
        if(mc == null){
            return released;
        }
        CampaignHelper helper = new CampaignHelper();
        try {
            helper.removeAlias(getAccountSignature(), mc.getKaze_id(), keyword.getKazeKeywordId());
            released = true;
            helper.close();
        } catch (UniformInterfaceException ex) {
            logger.error("ERROR releasing keyword alias: " + ex);
            helper.close();
            return released;
        }
        return released;
    }

    public void close()
    {
        client.destroy();
    }

    /**
     * creates a distribution list and adds subscribers to the distribution list
     *
     * @return String distribution list id
     */
    public int createSubscriberList(List<Subscriber> subscriberList, MobileOffer offer)
    {
        int distributionListId;
        DistributionHelper helper = new DistributionHelper();
        DistributionListExt dlext = new DistributionListExt();
        DistributionListEntryExt newDLEntryExt;
        dlext.setName(offer.getName());
        dlext.setNotes(offer.getName());

        // create distribution list
        DistributionListExt newDLExt = helper.createDistributionList_XML(getAccountSignature(), DistributionListExt.class, dlext);
        logger.info("[Distribution List ID: " + newDLExt.getDistributionListId() + "]");
        distributionListId = newDLExt.getDistributionListId();

        // add numbers to the distribution list -- create a entry for each mobile number
        for (Subscriber subscriber : subscriberList) {
            DistributionListEntryExt dlentry = new DistributionListEntryExt();
            dlentry.setDistributionListId(distributionListId);
            dlentry.setMobile(subscriber.getMsisdn());
            newDLEntryExt = helper.createDistributionListEntry_XML(getAccountSignature(), DistributionListEntryExt.class, dlentry, newDLExt.getDistributionListId().toString());
            logger.info("[DLEntry ID: " + newDLEntryExt.getDistributionListEntryId() + "]");
        }
        helper.close();

        return distributionListId;
    }

    public static void main(String[] args)
    {
        SMSPlatformRESTClient rest = new SMSPlatformRESTClient();
        rest.optInMsisdn("14044346968", "PRKIOSKS", null);
        //rest.releaseKeywordAlias(null, null);
        //rest.createHelpCustomizedMessage();
        //rest.createStopCustomizedMessage();

    }
}
