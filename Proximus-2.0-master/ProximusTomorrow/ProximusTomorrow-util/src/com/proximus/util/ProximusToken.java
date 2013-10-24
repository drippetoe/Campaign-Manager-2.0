package com.proximus.util;

import java.io.UnsupportedEncodingException;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * @Copyright 2012, Proximus Mobility, LLC., all rights reserved
 * @author dshaw
 *
 * Note that this class uses the Apache Commons Codec Library
 * Available here:  http://commons.apache.org/codec/
 */
public class ProximusToken {
     
    private String myCompanySalt = "null";
    
    public ProximusToken(String myCompanySalt)
    {
        this.myCompanySalt = myCompanySalt;
    }
    
    public String generateAuthenticationToken(String url)
    {
        String digestedToken = DigestUtils.md5Hex(this.myCompanySalt + url).toUpperCase();
        return digestedToken;
    }
    
    public static void main(String[] args) throws UnsupportedEncodingException
    {
        ProximusToken tokenGenerator = new ProximusToken("ea219dae-bb66-4d48-9869-b1e3515044a1");
        String URL_BASE = "http://localhost:8080/ProximusTomorrow-war/api/closestpropertyoffers/";        
        String username = "api@valutext.com";
        String password = "52d81ead-f70c-4e60-ac1e-6fb6f8926eec";
        String encodingUrl = URL_BASE + username + "/" + password ;
        String token = tokenGenerator.generateAuthenticationToken(encodingUrl);
        String requestUrl = URL_BASE + username + "/" + token;
            
        System.out.println("Raw URL was " + encodingUrl);
        System.out.println("Token would be " + token);
        System.out.println("Request URL would be " + requestUrl);
    }
}

