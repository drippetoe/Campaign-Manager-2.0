/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.registration.client;

/**
 * This class holds the registration response from Locaid -- error code and message
 * @author Angela Mercer
 */
public class RegistrarResponse
{
  private String registrationCode = "";
  private String registrationMessage = "";
  private Long transactionId = null;
  public final static String REGISTRATION_SUCCESS_CODE = "00000";
  public final static String CARRIER_UNSUPPORTED_CODE = "00006";
  public final static String MSISDN_ALREADY_SUBSCRIBED_CODE = "00013";
  public final static String CARRIER_UNSUPPORTED_MSG = "Carrier Unsupported";
  public final static String REGISTRATION_SUCCESS_MSG = "Registration was successful.";
  public final static String MSISDN_ALREADY_SUBSCRIBED_MSG = "This phone has already been subscribed";
  public final static String OPT_IN_SUCCESS_CODE = "00000";
  public final static String OPT_IN_SUCCESS_MSG = "Opt-in was successful.";

    public String getRegistrationCode()
    {
        return registrationCode;
    }

    public void setRegistrationCode(String registrationCode)
    {
        this.registrationCode = registrationCode;
    }

    public String getRegistrationMessage()
    {
        return registrationMessage;
    }

    public void setRegistrationMessage(String registrationMessage)
    {
        this.registrationMessage = registrationMessage;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

}
