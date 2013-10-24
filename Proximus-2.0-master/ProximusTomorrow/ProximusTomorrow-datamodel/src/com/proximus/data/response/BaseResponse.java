/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.data.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Angela Mercer
 */
@XmlRootElement(name = "baseResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class BaseResponse
{
  protected String status;
  protected String status_message;
  
  //generic messages
  public final static String SUCCESSFUL ="Successful";
  public final static String ERROR = "Error";
  
  //Response.Status.BAD_REQUEST
  public final static String INVALID_CREDENTIALS = "Invalid credentials";
  public final static String MISSING_PARAMETERS = "Check request parameters for accuracy";
  
  //Response.Status.SERVICE_UNAVAILABLE
  public final static String SERVICE_UNAVAILABLE = "Service is unavailable. Please try your request again later.";
  

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus_message()
    {
        return status_message;
    }

    public void setStatus_message(String status_message)
    {
        this.status_message = status_message;
    }
  
  
}
