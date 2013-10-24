package com.proximus.client.bluetooth.dreamplug;

import com.proximus.client.bluetooth.BluetoothFileData;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Represents a bluetooth device e.g. a phone, contains a placeholder for a obex
 * url cache etc.
 *
 * @author eric
 */
@SuppressWarnings("UseOfObsoleteCollectionType")
public class BluetoothDevice implements Serializable {

    private static final Logger logger = Logger.getLogger(BluetoothDevice.class.getName());
    private String MAC;
    private String friendlyName;
    private String OBEX;
    private Hashtable<String, Long> recentlyServedFiles;
    private long timeout;
    private int FILE_TIMEOUT_MINUTES = 5;

    /**
     * Default constructor
     */
    public BluetoothDevice() {
        this.MAC = null;
        this.friendlyName = null;
        this.OBEX = null;
        this.timeout = 0;
        this.recentlyServedFiles = new Hashtable<String, Long>();
    }

    public BluetoothDevice(String MAC) {
        this.MAC = MAC;
        this.friendlyName = null;
        this.OBEX = null;
        this.timeout = 0;
        this.recentlyServedFiles = new Hashtable<String, Long>();
    }

    public BluetoothDevice(String MAC, String friendlyName, String OBEX, Hashtable<String, Long> recentlyServedFiles, long timeout) {
        this.MAC = MAC;
        this.friendlyName = friendlyName;
        this.OBEX = OBEX;
        this.recentlyServedFiles = recentlyServedFiles;
        this.timeout = timeout;
    }

    /**
     * Copy Constructor
     *
     * @param bd
     */
    public BluetoothDevice(BluetoothDevice bd) {
        this.MAC = bd.MAC;
        this.OBEX = bd.OBEX;
        this.recentlyServedFiles = bd.recentlyServedFiles;
    }

    /**
     * Checks if a Device supports OBEX Object Push
     *
     * @return
     */
    public synchronized boolean supportsOBEX() {
        if (this.OBEX != null) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param filename
     */
    public synchronized void serveFile(String filename) {
        if (this.recentlyServedFiles.containsKey(filename)) {
            this.recentlyServedFiles.remove(filename);
        }
        this.recentlyServedFiles.put(filename, System.currentTimeMillis());
    }

    /**
     * Exclude the files that has been served depending on a certain interval
     *
     * @param files
     * @return A subset of files
     */
    public synchronized List<BluetoothFileData> getFilesToServe(List<BluetoothFileData> files, int intervalInMinutes) {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, (-intervalInMinutes));
        long past = calendar.getTimeInMillis();

        List<BluetoothFileData> result = new ArrayList<BluetoothFileData>(files);
        for (BluetoothFileData bluetoothFileData : files) {
            String filename = bluetoothFileData.getTitle();

            if (this.recentlyServedFiles.containsKey(filename)) {
                if (this.recentlyServedFiles.get(filename) > past) {
                    result.remove(bluetoothFileData);
                }
            }
        }
        logger.debug(this.MAC + " Device has " + result.size() + " unserved  files");
        return result;

    }

    /**
     * Getter for the MAC address
     *
     * @return
     */
    public synchronized String getMAC() {
        return MAC;
    }

    /**
     * Setter for the MAC address
     *
     * @param MAC
     */
    public synchronized void setMAC(String MAC) {
        this.MAC = MAC;
    }

    /**
     * Getter for the OBEX url
     *
     * @return
     */
    public synchronized String getOBEX() {
        return OBEX;
    }

    /**
     * Setter for the OBEX url
     *
     * @param OBEX
     */
    public synchronized void setOBEX(String OBEX) {
        this.OBEX = OBEX;
    }

    /**
     * Getter to get the recently served files
     *
     * @return
     */
    public synchronized Hashtable<String, Long> getRecentlyServedFiles() {
        return recentlyServedFiles;
    }

    /**
     * Set recently served files
     *
     * @param recentlyServedFiles
     */
    public synchronized void setRecentlyServedFiles(Hashtable<String, Long> recentlyServedFiles) {
        this.recentlyServedFiles = recentlyServedFiles;
    }

//    public void saveToFile() {
//        FileOutputStream fos = null;
//        ObjectOutputStream out = null;
//        try {
//            fos = new FileOutputStream(filename);
//            out = new ObjectOutputStream(fos);
//            out.writeObject(this);
//            out.close();
//        } catch (IOException ex) {
//            logger.error(ex);
//        }
//    }
//
//    public void loadFromFile() {
//        FileInputStream fis = null;
//        ObjectInputStream in = null;
//        try {
//            fis = new FileInputStream(filename);
//            in = new ObjectInputStream(fis);
//            BluetoothDevice bd = (BluetoothDevice) in.readObject();
//            in.close();
//        } catch (IOException ex) {
//            logger.error(ex);
//            return;
//        } catch (ClassNotFoundException ex) {
//            logger.error(ex);
//            return;
//        }
//        this = bd;
//    }
    public synchronized long getTimeout() {
        return timeout;
    }

    public synchronized void setTimeout(long timeout) {
        logger.debug("Set timeout: " + this.MAC);
        this.timeout = timeout;
    }

    public synchronized void timeout() {
        this.timeout = System.currentTimeMillis();
    }

    public synchronized String getFriendlyName() {
        return friendlyName;
    }

    public synchronized void setFriendlyName(String friendlyName) {
        friendlyName = friendlyName.replaceAll(",", "");
        this.friendlyName = friendlyName;
    }

    @Override
    public String toString() {
        return "" + this.MAC + "," + this.friendlyName;
    }
}
