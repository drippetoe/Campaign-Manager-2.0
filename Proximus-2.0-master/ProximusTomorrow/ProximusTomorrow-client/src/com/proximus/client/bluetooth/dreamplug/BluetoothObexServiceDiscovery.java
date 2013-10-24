package com.proximus.client.bluetooth.dreamplug;

import com.intel.bluetooth.RemoteDeviceHelper;
import com.proximus.client.bluetooth.BluetoothFileData;
import com.proximus.client.config.SystemWriter;
import com.proximus.util.client.ClientURISettings;
import java.io.IOException;
import java.util.List;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.obex.ClientSession;
import javax.obex.HeaderSet;
import javax.obex.ResponseCodes;
import org.apache.log4j.Logger;

/**
 *
 * @author eric
 */
public class BluetoothObexServiceDiscovery implements Runnable, DiscoveryListener {

    private static final UUID OBEX_OBJECT_PUSH = new UUID(0x1105);
    private volatile BluetoothDeviceHandler deviceHandler;
    private RemoteDevice device;
    private String mac;
    private String OBEXUrl;
    private boolean URLSuccess;
    private volatile ClientSession clientSession;
    private Object serviceSearchCompletedEvent = new Object();
    private DiscoveryAgent agent;
    private List<BluetoothFileData> files;
    private BluetoothObexPush bop;
    private static final Logger logger = Logger.getLogger(BluetoothObexServiceDiscovery.class.getName());

    /**
     * Constructor
     *
     * @param agent
     * @param deviceHandler
     * @param device
     * @param files
     */
    public BluetoothObexServiceDiscovery(DiscoveryAgent agent, BluetoothDeviceHandler deviceHandler, RemoteDevice device, List<BluetoothFileData> files) {

        this.files = files;
        this.deviceHandler = deviceHandler;
        this.device = device;
        this.mac = this.device.getBluetoothAddress();
        this.OBEXUrl = this.deviceHandler.getOBEXUrl(mac);
        this.URLSuccess = false;
        this.agent = agent;
        this.bop = new BluetoothObexPush(this.deviceHandler, this.files, this.mac);
        this.bop.setDevice(this.device);

    }

    @Override
    public void run() {
        if (this.deviceHandler.inBlacklist(this.mac)) {
            return;
        }
        /*
         * Loop until OBEX is found or return
         */
        searchForOBEXPush();
        if (this.URLSuccess) {
            String dev = this.deviceHandler.deviceToString(mac);
            SystemWriter.writeToBluetooth(SystemWriter.BLUETOOTH_LOG, "OBEX," + System.currentTimeMillis() + "," + dev+"\n");
            tryConnectAndPush();
        } else {
            String dev = this.deviceHandler.deviceToString(mac);
            logger.debug("Added to timeout in search for OBEX Push: " + mac);
            SystemWriter.writeToBluetooth(SystemWriter.BLUETOOTH_LOG, "NO-OBEX," + System.currentTimeMillis() + "," + dev+"\n");
            this.deviceHandler.timeout(mac);
        }
        this.deviceHandler.disconnect(mac);/*
         * Disconnect
         */

    }

    private void searchForOBEXPush() {
        UUID[] searchUuidSet = new UUID[]{OBEX_OBJECT_PUSH};
        int nrOfAttempts = 0;
        logger.debug("Searching for service: " + mac);
        while (!this.URLSuccess && nrOfAttempts < ClientURISettings.MAX_SEARCH_ATTEMPTS) {

            /*
             * Start to search a device for OBJECT OBEX Push
             */
            try {
                this.agent.searchServices(null, searchUuidSet, this.device, this);
            } catch (BluetoothStateException ex) {
                logger.error(ex);
                return;

            }
            /*
             * Wait until the service search is completed
             */
            synchronized (serviceSearchCompletedEvent) {
                try {
                    serviceSearchCompletedEvent.wait();
                } catch (InterruptedException ex) {
                    logger.error(ex);
                    return;
                }
            }
            /*
             * If we couldn't find OBJECT OBEX Push support sleep and loop
             * around again
             */
            if (!this.URLSuccess) {
                try {
                    Thread.sleep(ClientURISettings.SEARCH_RETRY_SLEEP);
                } catch (InterruptedException ex) {
                }
            }
            nrOfAttempts++;
        }
        logger.debug("Service done: " + mac);
    }

    /**
     * Try to connect to a device and then push content
     */
    private void tryConnectAndPush() {

        if (this.OBEXUrl != null) {
            establishConnection();
            String dev = this.deviceHandler.deviceToString(mac);
            if (this.clientSession != null) {                
                SystemWriter.writeToBluetooth(SystemWriter.BLUETOOTH_TRANSFER, "ACCEPTED," + System.currentTimeMillis() + "," + dev+"\n");
                this.bop.setClientSession(clientSession);
                this.bop.OBEXPush();
            } else {
                SystemWriter.writeToBluetooth(SystemWriter.BLUETOOTH_TRANSFER, "REJECTED," + System.currentTimeMillis() + "," + dev+"\n");
                this.deviceHandler.timeout(mac);
            }
        }
    }

    @Override
    public void servicesDiscovered(int i, ServiceRecord[] srs) {
        /*
         * If we found a valid serivce Record
         */
        if (srs != null && srs.length > 0) {
            ServiceRecord record = srs[0];
            /*
             * Get the URL to OBEX Serivce
             */
            String url = record.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
            if (url != null && url.length() > 0) {
                this.URLSuccess = true;
                this.OBEXUrl = url;
                this.deviceHandler.updateOBEXUrl(mac, url);

            } else {
                this.URLSuccess = false;
            }
            /*
             * Try and get the friendly name of the device
             */
            String friendlyName = null;
            try {
                friendlyName = record.getHostDevice().getFriendlyName(false);
                if (friendlyName != null || friendlyName.length() > 0) {
                    this.deviceHandler.updateFriendlyName(mac, friendlyName);
                }
            } catch (IOException ex) {
            }

            /*
             * Debug message only
             */
            if (url != null && url.length() > 0) {
                logger.debug(friendlyName + "[" + this.mac + "] supports OBEX Object Tranfer.");
            }

        }
    }

    @Override
    public void serviceSearchCompleted(int transID, int respCode) {
        synchronized (serviceSearchCompletedEvent) {
            serviceSearchCompletedEvent.notifyAll();
        }
        switch (respCode) {
            case DiscoveryListener.SERVICE_SEARCH_COMPLETED:
                logger.debug("SEARCH_COMPLETED : " + this.mac);
                break;
            case DiscoveryListener.SERVICE_SEARCH_DEVICE_NOT_REACHABLE:
                logger.debug("DEVICE_NOT_REACHABLE : " + this.mac);
                break;
            case DiscoveryListener.SERVICE_SEARCH_ERROR:
                logger.debug("SEARCH_ERROR : " + this.mac);
                break;
            case DiscoveryListener.SERVICE_SEARCH_NO_RECORDS:
                logger.debug("NO_RECORDS : " + this.mac);
                break;
            case DiscoveryListener.SERVICE_SEARCH_TERMINATED:
                logger.debug("SEARCH_TERMINATED : " + this.mac);
                break;
            default:
                logger.debug("UNKOWN_RESPONSE_CODE : " + this.mac);
                break;
        }
    }

    @Override
    public void inquiryCompleted(int i) {
    }

    @Override
    public void deviceDiscovered(RemoteDevice rd, DeviceClass dc) {
    }

    /**
     * Try and authenticate a device with a code
     *
     * @param authCounter changes the authcode depending on the counter
     */
    private void authenticate(int authCounter) {
        String authCode = "0000";
        switch (authCounter) {
            case 1:
                authCode = "1111";
                break;
            case 2:
                authCode = "1234";
                break;
            case 3:
                authCode = "2222";
                break;
            case 4:
                authCode = String.valueOf((int) Math.floor(Math.random() * 9000) + 1000);
                break;
            default:
                authCode = "0000";
        }
        try {
            RemoteDeviceHelper.authenticate(this.device, authCode);
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }

    /**
     * Try and establish a connection to the OBEX url
     */
    private void establishConnection() {

        int attemptCounter = 0;
        HeaderSet hsConnectReply = null;
        int responseCode = 0;
        while (attemptCounter < ClientURISettings.MAX_CONNECTION_ATTEMPTS) {
            try {
                authenticate(attemptCounter);
                this.clientSession = (ClientSession) Connector.open(this.OBEXUrl);
                hsConnectReply = this.clientSession.connect(null);
                responseCode = hsConnectReply.getResponseCode();
                if (responseCode == ResponseCodes.OBEX_HTTP_OK) {
                    logger.debug(mac + " connected");
                    return;
                }
            } catch (IOException ex) {
                //ignore
            }
            try {
                Thread.sleep(ClientURISettings.CONNECT_RETRY_SLEEP); // sleep 1s
            } catch (InterruptedException iex) {
            }
            attemptCounter++;
        }
    }
}
