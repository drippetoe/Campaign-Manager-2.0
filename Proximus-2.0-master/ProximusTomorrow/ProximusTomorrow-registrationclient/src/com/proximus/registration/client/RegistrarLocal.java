/*
 * Copyright 2010-2012, Proximus Mobility, LLC.
 * 
 */
package com.proximus.registration.client;

/**
 *
 * @author dshaw
 */
public interface RegistrarLocal {
    public RegistrarResponse singleMsisdnRegistration(String msisdn);
    public RegistrarResponse singleOptIn(String msisdn);
    public boolean cancelSubscription(String msisdn);
    public String getSingleMsisdnStatus(String msisdn);
}
