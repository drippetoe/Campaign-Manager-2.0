/*
 * Copyright 2010-2012, Proximus Mobility, LLC.
 * 
 */
package com.proximus.registration.client;

import com.proximus.data.response.RegistrationResponse;

/**
 *
 * @author dshaw
 */
public class FakeRegistrar implements RegistrarLocal {

    private String regStatus = null;
    
    
    @Override
    public RegistrarResponse singleMsisdnRegistration(String msisdn) {
        RegistrarResponse r = new RegistrarResponse();
        r.setRegistrationCode(RegistrarResponse.REGISTRATION_SUCCESS_CODE);
        r.setRegistrationMessage(RegistrarResponse.REGISTRATION_SUCCESS_MSG);
        return r;
    }

    @Override
    public RegistrarResponse singleOptIn(String msisdn) {
        RegistrarResponse r = new RegistrarResponse();
        r.setRegistrationCode(RegistrarResponse.OPT_IN_SUCCESS_CODE);
        r.setRegistrationMessage(RegistrarResponse.OPT_IN_SUCCESS_MSG);
        return r;
    }

    @Override
    public boolean cancelSubscription(String msisdn) {
        return true;
    }

    @Override
    public String getSingleMsisdnStatus(String msisdn) {
        return regStatus;
    }
    
    public void setSingleMsisdnStatus(String status)
    {
        this.regStatus = status;
    }
    
}
