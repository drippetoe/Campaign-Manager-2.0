/*
 * Copyright 2010-2012, Proximus Mobility, LLC.
 * 
 */
package com.proximus.icap.server;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * An attempt, for demo purposes, to use an IP address as a time-dependent 
 * unique identifier. Will of course not work behind a firewall...
 *
 * @author dshaw
 */
public class UniqueUserTracker {

    private static UniqueUserTracker theInstance;

    /**
     * @return singleton instance of this entity
     */
    public static UniqueUserTracker getInstance()
    {
        if ( theInstance == null )
        {
            theInstance = new UniqueUserTracker();
        }

        return theInstance;
    }

    // use a ConcurrentHashMap since it's synchronized
    ConcurrentHashMap<String, AuthorizationEntry> ipTimeMap;

    private UniqueUserTracker()
    {
        ipTimeMap = new ConcurrentHashMap<String, AuthorizationEntry>();
    }

    public void setAuthorized(String ipAddress, boolean authorized)
    {
        AuthorizationEntry entry = getEntryForIp(ipAddress);
        entry.setAuthorized(authorized);
        ipTimeMap.put(ipAddress, entry);
    }

    public boolean isAuthorized(String ipAddress)
    {
        if ( !ipTimeMap.containsKey(ipAddress) )
        {
            setAuthorized(ipAddress, false);
            return false;
        }
        else
        {
            AuthorizationEntry entry = getEntryForIp(ipAddress);

            
            ipTimeMap.put(ipAddress, entry);

            return entry.isAuthorized();
        }
    }

    public String getUserString(String ipAddress)
    {
        return getEntryForIp(ipAddress).toString();
    }

    private AuthorizationEntry getEntryForIp(String ipAddress) {

        AuthorizationEntry entry;

        if ( ipTimeMap.containsKey(ipAddress))
        {
            entry = ipTimeMap.get(ipAddress);
        }
        else
        {
            entry = new AuthorizationEntry();
        }
        if (!entry.isValidSession()) {
            entry = new AuthorizationEntry();
        } else {
            entry.updateLastSeen();
        }
        return entry;
    }


    // internal class to track auth fields
    private class AuthorizationEntry
    {
        // how long after we see someone that their session invalidates
        private static final long SESSION_TIMEOUT = 5 * 60 * 1000;

        private Date firstSeen;
        private Date lastSeen;
        private boolean authorized = false;
        private String sessionIdentifier; // IP address for now, MAC later

        public AuthorizationEntry()
        {
            firstSeen = new Date();
            lastSeen = firstSeen;
            authorized = false;
        }

        public boolean isAuthorized()
        {
            return authorized;
        }

        public void setAuthorized(boolean authorized)
        {
            this.authorized = authorized;
        }

        public void updateLastSeen()
        {
            lastSeen = new Date();
        }

        /**
         *
         * @return true if this session has not expired
         */
        public boolean isValidSession()
        {
            if (( System.currentTimeMillis() - lastSeen.getTime() ) >= SESSION_TIMEOUT )
            {
                return false; // expired
            }
            return true; // still valid
        }

        @Override
        public String toString()
        {
            final String sep = " | ";
            
            StringBuilder buff = new StringBuilder();
            buff.append("[Identifier: ");
            buff.append(this.sessionIdentifier);
            buff.append(sep);
            buff.append("First Seen: ");
            buff.append(this.firstSeen);
            buff.append(sep);
            buff.append("Last Seen: ");
            buff.append(this.lastSeen);
            buff.append(sep);
            buff.append("Authorized: ");
            buff.append(this.authorized);
            buff.append(sep);
            buff.append("]");

            return buff.toString();
        }

    }
}
