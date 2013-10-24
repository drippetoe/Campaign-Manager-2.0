package com.proximus.util;

import com.proximus.data.User;
import org.apache.log4j.Logger;

/**
 *
 * @author Angela Mercer
 */
public class RESTAuthenticationUtil {

    private static final Logger logger = Logger.getLogger(RESTAuthenticationUtil.class.getName());

    public static boolean apiAuthorized(String username, String password, String requestToken, String requestURL,
            String companySALT) {

        logger.info("auth params: username| " + username
                + "|password| " + password
                + "|token| " + requestToken
                + "|SALT| " + companySALT);
        logger.info("requestURL: " + requestURL);
        ProximusToken token = new ProximusToken(companySALT);
        if (requestToken.equals(token.generateAuthenticationToken(requestURL))) {
            logger.warn("PASSED AUTHENTICATION");
            return true;
        }
        return false;
    }

    /**
     * Use this method only on secured calls that don't use URL encoded tokens
     * @param username
     * @param password
     * @param user
     * @return
     */
    public static boolean apiAuthorized(String username, String password, User user)
    {
        if ( !username.equalsIgnoreCase(user.getUserName()) )
        {
            return false;
        }
        if ( !password.equalsIgnoreCase(user.getApiPassword()) )
        {
            return false;
        }
        return true;
        
    }
}
