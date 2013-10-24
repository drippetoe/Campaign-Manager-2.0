/*
 * Copyright 2010-2012, Proximus Mobility, LLC.
 * 
 */
package com.proximus.registration.client;

import java.util.ArrayList;
import java.util.List;
import javax.xml.ws.WebServiceRef;
import net.locaid.portico.webservice.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class Registrar implements RegistrarLocal
{
    @WebServiceRef(wsdlLocation = "META-INF/wsdl/RegistrationServices?wsdl")
    private static RegistrationServices_Service service;
    private static RegistrationServices registrationPort;
    public static final String STATUS_SUBSCRIPTION_CANCELLED = "CANCELLED";
    public static final String STATUS_OPTIN_COMPLETE = "OPTIN_COMPLETE";
    public static final String STATUS_OPTIN_PENDING = "OPTIN_PENDING";
    public static final String STATUS_NONE = "NO_LOCAID_STATUS";
    private static final Logger logger = Logger.getLogger(Registrar.class);

    public static RegistrationServices getRegistrationPort()
    {
        if (registrationPort == null) {
            service = new RegistrationServices_Service();
            registrationPort = service.getRegistrationServicesPort();
        }
        return registrationPort;
    }

    private static SubscribePhoneAllResponseBean subscribePhoneAll(String login, String password, String command, List<String> msisdnList)
    {
        return getRegistrationPort().subscribePhoneAll(login, password, command, msisdnList);
    }

    private static SubscribePhoneResponseBean subscribePhone(String login, String password, String command, List<ClassIdList> classIdList)
    {

        return getRegistrationPort().subscribePhone(login, password, command, classIdList);
    }

    private static GetPhoneStatusResponseBean getPhoneStatus(String login, String password, List<String> msisdnList)
    {
        return getRegistrationPort().getPhoneStatus(login, password, msisdnList);
    }

    /**
     * registers a single msisdn on the locaid platform
     *
     * @param msisdn
     * @return boolean registrationComplete
     */
    @Override
    public RegistrarResponse singleMsisdnRegistration(String msisdn)
    {
        logger.info("Class name: Registrar|Method: singleMsisdnRegistration");
        RegistrarResponse registrarResponse = new RegistrarResponse();

        //set up for call to locaid
        List<ClassIdList> myClassIdList = new ArrayList();
        ClassIdList classIdList = new ClassIdList();
        classIdList.setClassId(RegistrarRequest.PROXIMUS_PROD_CLASS_ID);
        classIdList.getMsisdnList().add(msisdn);
        myClassIdList.add(classIdList);

//      //1. register subscriber
        SubscribePhoneResponseBean response = subscribePhone(RegistrarRequest.LOGIN, RegistrarRequest.PASSWORD,
                RegistrarRequest.OPTIN_COMMAND, myClassIdList);

        //received an error
        if (response.getError() != null) {
            logger.error("Error in registration|error-code: " + response.getError().getErrorCode()
                    + "|error-msg: " + response.getError().getErrorMessage()
                    + "|msisdn: " + msisdn);
            registrarResponse.setRegistrationCode(response.getError().getErrorCode());
            registrarResponse.setRegistrationMessage(response.getError().getErrorMessage());
            registrarResponse.setTransactionId(response.getTransactionId());
            return registrarResponse;

            //response is returned   
        } else {
            List<ComplexClassIdResponseBean> responseBeanList = response.getClassIdList();

            logger.info("no. of complex class id response beans: " + responseBeanList.size());
            logger.info("no. msisdn status response beans: " + responseBeanList.get(0).getMsisdnList().size());

            //should only be one response bean
            MsisdnStatusResponseBean msisdnResponseBean = responseBeanList.get(0).getMsisdnList().get(0);

            //getting info about msisdn
            if (msisdnResponseBean.getError() != null) {
                logger.error("Error in optin response|error-code: " + msisdnResponseBean.getError().getErrorCode()
                        + "|error-msg: " + msisdnResponseBean.getError().getErrorMessage()
                        + "|msisdn: " + msisdn);

                registrarResponse.setRegistrationCode(msisdnResponseBean.getError().getErrorCode());
                registrarResponse.setRegistrationMessage(msisdnResponseBean.getError().getErrorMessage());
                registrarResponse.setTransactionId(response.getTransactionId());
                return registrarResponse;

                //no error    
            } else {
                registrarResponse.setRegistrationCode(RegistrarResponse.REGISTRATION_SUCCESS_CODE);
                registrarResponse.setRegistrationMessage(RegistrarResponse.REGISTRATION_SUCCESS_MSG);
                registrarResponse.setTransactionId(response.getTransactionId());
                return registrarResponse;
            }
        }
       
    }

    /**
     * opts-in a single msisdn on the locaid platform after registration is sent
     * i.e the user sent valuetext and replied Y to be sent sms messages and to
     * be tracked
     *
     * @param msisdn
     * @return boolean optInComplete
     */
    @Override
    public RegistrarResponse singleOptIn(String msisdn)
    {
        logger.info("Class name: Registrar|Method: singleOptIn");
        RegistrarResponse registrarResponse = new RegistrarResponse();

        //setup
        List<ClassIdList> myClassIdList = new ArrayList(); //locaid takes a list of a list of class-ids
        ClassIdList classIdList = new ClassIdList();
        classIdList.setClassId(RegistrarRequest.PROXIMUS_PROD_CLASS_ID); //production class-id 
        classIdList.getMsisdnList().add(msisdn);
        myClassIdList.add(classIdList);

        //make call to locaid to optin -- YES command 
        SubscribePhoneResponseBean response = subscribePhone(RegistrarRequest.LOGIN, RegistrarRequest.PASSWORD,
                RegistrarRequest.OPTIN_YES, myClassIdList);

        //got an error
        if (response.getError() != null) {
            logger.error("Error in optin|error-code: " + response.getError().getErrorCode()
                    + "|error-msg: " + response.getError().getErrorMessage()
                    + "|msisdn: " + msisdn);
            registrarResponse.setRegistrationCode(response.getError().getErrorCode());
            registrarResponse.setRegistrationMessage(response.getError().getErrorMessage());
            registrarResponse.setTransactionId(response.getTransactionId());
            return registrarResponse;

            //response is returned    
        } else {
            List<ComplexClassIdResponseBean> responseBeanList = response.getClassIdList();

            logger.info("no. of complex class id response beans: " + responseBeanList.size());
            logger.info("no. msisdn status response beans: " + responseBeanList.get(0).getMsisdnList().size());

            //should only be one response bean
            MsisdnStatusResponseBean msisdnResponseBean = responseBeanList.get(0).getMsisdnList().get(0);

            //getting info about msisdn
            // note 00013 is 'This phone has already been subscribed' which is OK with us and should not cause an error
            if (msisdnResponseBean.getError() != null && !msisdnResponseBean.getError().getErrorCode().equals("00013")) {
                logger.error("Error in optin response|error-code: " + msisdnResponseBean.getError().getErrorCode()
                        + "|error-msg: " + msisdnResponseBean.getError().getErrorMessage()
                        + "|msisdn: " + msisdn);
                registrarResponse.setRegistrationCode(msisdnResponseBean.getError().getErrorCode());
                registrarResponse.setRegistrationMessage(msisdnResponseBean.getError().getErrorMessage());
                registrarResponse.setTransactionId(response.getTransactionId());
                return registrarResponse;


                //no error    
            } else {
                registrarResponse.setRegistrationCode(RegistrarResponse.OPT_IN_SUCCESS_CODE);
                registrarResponse.setRegistrationMessage(RegistrarResponse.OPT_IN_SUCCESS_MSG);
                registrarResponse.setTransactionId(response.getTransactionId());
                return registrarResponse;
            }
        }

    }

    /**
     * cancels a subscription for a single msisdn on the locaid platform after
     * user has opted-in i.e. a subscriber sends STOP
     *
     * @param msisdn
     * @return boolean subscriptionCancelled
     */
    @Override
    public boolean cancelSubscription(String msisdn)
    {
        logger.info("Class name: Registrar|Method: cancelSubscription");
        boolean subscriptionCancelled = false;

        //setup
        List<ClassIdList> myClassIdList = new ArrayList(); //locaid takes a list of a list of class-ids,we only have one
        ClassIdList classIdList = new ClassIdList();
        classIdList.setClassId(RegistrarRequest.PROXIMUS_PROD_CLASS_ID); //production class-id 
        classIdList.getMsisdnList().add(msisdn);
        myClassIdList.add(classIdList);

        //make call to locaid to cancel subscription
        SubscribePhoneResponseBean response = subscribePhone(RegistrarRequest.LOGIN, RegistrarRequest.PASSWORD,
                RegistrarRequest.CANCEL, myClassIdList);

        //got an error
        if (response.getError() != null) {
            logger.error("Error in optout|error-code: " + response.getError().getErrorCode()
                    + "|error-msg: " + response.getError().getErrorMessage()
                    + "|msisdn: " + msisdn);
            return subscriptionCancelled;

            //response is returned    
        } else {
            List<ComplexClassIdResponseBean> responseBeanList = response.getClassIdList();

            logger.info("no. of complex class id response beans: " + responseBeanList.size());
            logger.info("no. msisdn status response beans: " + responseBeanList.get(0).getMsisdnList().size());

            //should only be one response bean
            MsisdnStatusResponseBean msisdnResponseBean = responseBeanList.get(0).getMsisdnList().get(0);

            //getting info about msisdn
            if (msisdnResponseBean.getError() != null) {
                logger.error("Error in optout response|error-code: " + msisdnResponseBean.getError().getErrorCode()
                        + "|error-msg: " + msisdnResponseBean.getError().getErrorMessage()
                        + "|msisdn: " + msisdn);
                return subscriptionCancelled;

                //no error    
            } else {
                subscriptionCancelled = true;
            }
        }
        return subscriptionCancelled;
    }

    /**
     * gets latest locaid status for class-id 5P27J
     *
     * @param msisdn
     * @return CANCELLED -- subscription cancelled OPTIN_COMPLETE -- opt-in
     * completed successfully null -- error
     */
    @Override
    public String getSingleMsisdnStatus(String msisdn)
    {
        String msisdnStatus = "NO_LOCAID_STATUS";
        List<String> msisdnList = new ArrayList();
        msisdnList.add(msisdn);

        GetPhoneStatusResponseBean response = getPhoneStatus(RegistrarRequest.LOGIN, RegistrarRequest.PASSWORD, msisdnList);
        if (response.getError() != null) {
            /*
             * error with entire request
             */
            logger.error("error in get phone status response bean error code: " + response.getError().getErrorCode() + " error msg: " + response.getError().getErrorMessage());
            return msisdnStatus;

        }
        List<ComplexStatusMsisdnResponseBean> statusBeanList = response.getMsisdnList();
        logger.info("complex status msisdn response bean size: " + statusBeanList.size());
        for (ComplexStatusMsisdnResponseBean bean : statusBeanList) {
            if (bean.getError() != null) {
                logger.error("error in complex status msisdn response bean|msisdn: " + bean.getMsisdn() + " | error code: " + bean.getError().getErrorCode() + " | error msg: " + bean.getError().getErrorMessage());
                return msisdnStatus;
            }
            List<ClassIdStatusResponseBean> classIdStatusList = bean.getClassIdList();
            logger.info("class id status response bean size: " + classIdStatusList.size());
            for (ClassIdStatusResponseBean statusBean : classIdStatusList) {
                logger.info("number| " + msisdn + " |LOCAID status| " + statusBean.getStatus() + "|class Id| " + statusBean.getClassId());
                if (statusBean.getClassId().equalsIgnoreCase(RegistrarRequest.PROXIMUS_PROD_CLASS_ID)) {

                    logger.info("Subscriber status for Proximus Production Class ID: " + statusBean.getClassId() + " is " + statusBean.getStatus());
                    msisdnStatus = statusBean.getStatus();
                }
            }
        }
        return msisdnStatus;
    }

    public static void main(String[] args)
    {
        BasicConfigurator.configure();

        Registrar registrar = new Registrar();

        String[] msisdnList = {"14153141172"};
        for (String msisdn : msisdnList) {
            RegistrarResponse in = registrar.singleMsisdnRegistration(msisdn);
            System.out.println("Register msg: " + in.getRegistrationMessage() + " : " + msisdn);
            System.out.println("Register code: " + in.getRegistrationCode() + " : " + msisdn);
            System.out.println("Transaction ID: " + in.getTransactionId() + " : " + msisdn);

            String status = registrar.getSingleMsisdnStatus(msisdn);
            System.out.println("status for " + msisdn + ": " + status);
            

            RegistrarResponse optin = registrar.singleOptIn(msisdn);
            System.out.println("Optin: " + optin.getRegistrationMessage() + " : " + msisdn);
            System.out.println("Optin Transaction ID: " + optin.getTransactionId() + " : " + msisdn);

            String optInStatus = registrar.getSingleMsisdnStatus(msisdn);
            System.out.println("status for " + msisdn + ": " + optInStatus);
        }
    }
}
