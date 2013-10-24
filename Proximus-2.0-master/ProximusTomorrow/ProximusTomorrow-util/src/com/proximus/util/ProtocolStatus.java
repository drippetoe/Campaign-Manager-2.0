/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.util;

import org.apache.http.HttpStatus;

/**
 *
 * @author Gaxiola
 */
public class ProtocolStatus
{
    
    //HTTP STATUS CODE
    //2xx Success
    public static final int OK = HttpStatus.SC_OK;
    public static final int CREATED = HttpStatus.SC_CREATED;
    public static final int ACCEPTED = HttpStatus.SC_ACCEPTED;
    public static final int NO_CONTENT = HttpStatus.SC_NO_CONTENT;
    
    //3xx Redirection
    public static final int NOT_MODIFIED = HttpStatus.SC_NOT_MODIFIED;
    
    //4xx Client Error
    public static final int NOT_FOUND = HttpStatus.SC_NOT_FOUND;
    public static final int FORBIDDEN = HttpStatus.SC_FORBIDDEN;
    public static final int BAD_REQUEST = HttpStatus.SC_BAD_REQUEST;
    public static final int UNAUTHORIZED = HttpStatus.SC_UNAUTHORIZED;
    public static final int METHOD_NOT_ALLOWED = HttpStatus.SC_METHOD_NOT_ALLOWED;
    public static final int GONE = HttpStatus.SC_GONE;
    
    //5xx Server Error
    public static final int INTERNAL_SERVER_ERROR = HttpStatus.SC_INTERNAL_SERVER_ERROR;
    
   
    //6xx Proximus Customs Actions
    public static final int CONFIG_CHANGE = 600;
    public static final int RESTART_SOFTWARE = 602;
    public static final int SOFTWARE_UPGRADE = 603;
    
    
}
