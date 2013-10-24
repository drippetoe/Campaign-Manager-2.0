/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.client.bluetooth.dreamplug;

import com.proximus.client.bluetooth.BluetoothFileData;
import com.proximus.util.client.ClientURISettings;
import java.util.*;
import org.apache.log4j.Logger;

/**
 *
 * @author eric
 */
@SuppressWarnings("UseOfObsoleteCollectionType")
public class BluetoothDeviceHandler {


    private static final String ERIC = "74A7221DDE77";
   
    
    private volatile HashSet<String> ignore;
    private volatile HashSet<String> blacklist;
    private volatile HashSet<String> whitelist;
    private volatile HashSet<String> connected;
    private static final Logger logger = Logger.getLogger(BluetoothDeviceHandler.class.getName());
    private volatile Hashtable<String, BluetoothDevice> deviceCache;

    public BluetoothDeviceHandler() {
        //this.dbm = new BluetoothDatabaseManager();

        this.ignore = new HashSet<String>();
        this.ignore.add("000780"); //Bluegiga
        this.ignore.add("F0AD4E"); //Dreamplug
        this.blacklist = new HashSet<String>();
        //this.blacklist.add(ERIC);
        this.whitelist = new HashSet<String>();
        this.connected = new HashSet<String>();
        this.deviceCache = new Hashtable<String, BluetoothDevice>();

    }
    
    /**
     * Called when campaign content is switched out
     */
    public synchronized void clearCache(){
        this.deviceCache = new Hashtable<String, BluetoothDevice>();
    }

    /**
     * Cache a new device
     * @param mac format "74A7221DDE77"
     */
    public synchronized void addDevice(String mac) {
        if (this.ignore.contains(mac.substring(0, 6))) {
            return;
        }
        if (!this.deviceCache.containsKey(mac)) {
            this.deviceCache.put(mac, new BluetoothDevice(mac));
        }
    }

    public synchronized boolean isConnected(String mac) {
        if (this.ignore.contains(mac.substring(0, 6))) {
            return true;
        }
        if (this.blacklist.contains(mac)) {
            return true;
        }
        return this.connected.contains(mac);
    }

    public synchronized void connecting(String mac) {
        if (this.ignore.contains(mac.substring(0, 6))) {
            return;
        }
        if (this.blacklist.contains(mac)) {
            return;
        }
        this.connected.add(mac);
    }

    public synchronized void disconnect(String mac) {
        if (this.ignore.contains(mac.substring(0, 6))) {
            return;
        }
        if (this.blacklist.contains(mac)) {
            return;
        }
        if (this.connected.contains(mac)) {
            this.connected.remove(mac);
        }
    }
//

    public synchronized void serveFile(String mac, String file, int rssi) {
        long now = new Date().getTime();
        if (this.deviceCache.containsKey(mac)) {
            BluetoothDevice bd = this.deviceCache.get(mac);
            bd.serveFile(file);
            bd.timeout();
        
            this.deviceCache.remove(mac);
            this.deviceCache.put(mac, bd);
        }
    }


    public synchronized boolean inBlacklist(String mac) {
        if (this.ignore.contains(mac.substring(0, 6))) {
            return true;
        }
        return this.blacklist.contains(mac);
    }


    public synchronized void timeout(String mac) {
        if (this.deviceCache.containsKey(mac)) {
            BluetoothDevice bd = this.deviceCache.get(mac);
            bd.timeout();
            this.deviceCache.remove(mac);
            this.deviceCache.put(mac, bd);
        }
    }

    public synchronized boolean supportsOBEXObjectPush(String mac) {
        if (this.ignore.contains(mac.substring(0, 6))) {
            return false;
        }
        if (this.deviceCache.containsKey(mac)) {
            BluetoothDevice bd = this.deviceCache.get(mac);
            return bd.supportsOBEX();
        }
        return false;
    }

    public synchronized boolean inTimeout(String mac) {
        if (this.ignore.contains(mac.substring(0, 6))) {
            return true;
        }
        if (this.deviceCache.containsKey(mac)) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, (-ClientURISettings.FILE_TIMEOUT_MINUTES));
            long past = calendar.getTimeInMillis();
            BluetoothDevice bd = this.deviceCache.get(mac);
            if (bd.getTimeout() == 0) {
                return false;
            } else if (past < bd.getTimeout()) {
                return true;
            }
        }
        return false;
    }

    public synchronized List<BluetoothFileData> getFilesToServe(String mac, List<BluetoothFileData> files) {
        if (this.ignore.contains(mac.substring(0, 6))) {
            return new ArrayList<BluetoothFileData>();
        }
        if (this.deviceCache.containsKey(mac)) {
            BluetoothDevice bd = this.deviceCache.get(mac);
            return bd.getFilesToServe(files, ClientURISettings.FILE_TIMEOUT_MINUTES);
        }
        return new ArrayList<BluetoothFileData>();
    }

    public synchronized String getOBEXUrl(String mac) {
        if (this.ignore.contains(mac.substring(0, 6))) {
            return null;
        }
        if (this.deviceCache.containsKey(mac)) {
            BluetoothDevice bd = this.deviceCache.get(mac);
            return bd.getOBEX();
        }
        return null;
    }

    public synchronized void updateOBEXUrl(String mac, String url) {
        if (this.deviceCache.containsKey(mac)) {
            BluetoothDevice bd = this.deviceCache.get(mac);
            bd.setOBEX(url);
            this.deviceCache.remove(mac);
            this.deviceCache.put(mac, bd);
        }
    }

    public synchronized void updateFriendlyName(String mac, String friendlyName) {
        if (this.deviceCache.containsKey(mac)) {
            BluetoothDevice bd = this.deviceCache.get(mac);
            bd.setFriendlyName(friendlyName);
            this.deviceCache.remove(mac);
            this.deviceCache.put(mac, bd);
        }
    }
    
    public synchronized String deviceToString(String mac){
        if (this.deviceCache.containsKey(mac)) {
            BluetoothDevice bd = this.deviceCache.get(mac);
            return bd.toString();
        }
        return null;
    }
    
    
}
