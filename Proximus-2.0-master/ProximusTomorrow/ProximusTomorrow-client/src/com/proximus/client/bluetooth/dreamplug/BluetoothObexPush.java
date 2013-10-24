package com.proximus.client.bluetooth.dreamplug;

import com.intel.bluetooth.RemoteDeviceHelper;
import com.intel.bluetooth.obex.BlueCoveOBEX;
import com.proximus.client.bluetooth.BluetoothFileData;
import com.proximus.client.config.SystemWriter;
import com.proximus.util.client.ClientURISettings;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import javax.bluetooth.RemoteDevice;
import javax.microedition.io.Connector;
import javax.obex.ClientSession;
import javax.obex.HeaderSet;
import javax.obex.Operation;
import javax.obex.ResponseCodes;
import org.apache.log4j.Logger;

/**
 *
 * @author eric
 */
public class BluetoothObexPush implements Runnable {

    private volatile ClientSession clientSession;
    private volatile BluetoothDeviceHandler deviceHandler;
    private List<BluetoothFileData> files;
    private String mac;
    private String OBEXUrl;
    private RemoteDevice device; //used when spawned as thread
    private static final Logger logger = Logger.getLogger(BluetoothObexPush.class.getName());

    public BluetoothObexPush(BluetoothDeviceHandler deviceHandler, List<BluetoothFileData> files, String mac) {
        this.clientSession = null;
        this.deviceHandler = deviceHandler;
        this.files = files;
        this.mac = mac;
        this.OBEXUrl = this.deviceHandler.getOBEXUrl(mac);

    }

    /**
     * Setter needs to be called before spawning BluetoothObexPush as a Thread
     *
     * @param device RemoteDevice found during discovery
     */
    public void setDevice(RemoteDevice device) {
        this.device = device;
    }

    /**
     * Setter to set the session when a connection is established outside of
     * this class Used in BluetoothObexServiceDiscovery#tryConnectAndPush
     *
     * @param clientSession
     */
    public void setClientSession(ClientSession clientSession) {
        this.clientSession = clientSession;
    }

    public void OBEXPush() {
        HeaderSet hsPutOperation;
        String dev = this.deviceHandler.deviceToString(mac);
        if (this.clientSession != null) {
            int mtu = BlueCoveOBEX.getPacketSize(clientSession);
            byte[] buffer = new byte[mtu];
            for (BluetoothFileData bluetoothFileData : files) {
                try {
                    logger.debug("[OBEX Object Push]: " + mac + " <=> " + bluetoothFileData.getTitle() + " MTU:" + mtu);

                    hsPutOperation = clientSession.createHeaderSet();
                    hsPutOperation.setHeader(HeaderSet.NAME, bluetoothFileData.getTitle());
                    hsPutOperation.setHeader(HeaderSet.TYPE, bluetoothFileData.getMime());
                    hsPutOperation.setHeader(HeaderSet.LENGTH, new Long(bluetoothFileData.getSize()));
                    Operation po = clientSession.put(hsPutOperation);
                    OutputStream os = po.openOutputStream();
                    ByteArrayInputStream is = new ByteArrayInputStream(bluetoothFileData.getData());
                    int i = is.read(buffer);
                    long startTime = System.currentTimeMillis();
                    int complete = 0;
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        complete += i;
                        i = is.read(buffer);
                    }
                    long endTime = System.currentTimeMillis();
                    os.flush();
                    os.close();
                    int transferResponseCode = po.getResponseCode();
                    if (transferResponseCode == ResponseCodes.OBEX_HTTP_OK) {
                        dev = this.deviceHandler.deviceToString(mac);
                        int rssi = RemoteDeviceHelper.readRSSI(RemoteDevice.getRemoteDevice(this.clientSession));
                        SystemWriter.writeToBluetooth(SystemWriter.BLUETOOTH_TRANSFER, "DELIVERED," + System.currentTimeMillis() + "," + dev + "," + bluetoothFileData.getTitle() + "," + (endTime - startTime) + "," + rssi + "\n");
                        this.deviceHandler.serveFile(mac, bluetoothFileData.getTitle(), rssi);
                    }else{
                        SystemWriter.writeToBluetooth(SystemWriter.BLUETOOTH_TRANSFER, "CANCELLED," + System.currentTimeMillis() + "," + dev + "," + bluetoothFileData.getTitle() + "\n");
                    }
                    po.close();
                } //end for
                catch (EOFException eofx) {
                    SystemWriter.writeToBluetooth(SystemWriter.BLUETOOTH_TRANSFER, "CANCELLED," + System.currentTimeMillis() + "," + dev + "," + bluetoothFileData.getTitle() + "\n");
                    logger.info("CANCELLED: "+eofx.getMessage());
                } catch (IOException ex) {
                    SystemWriter.writeToBluetooth(SystemWriter.BLUETOOTH_TRANSFER, "CANCELLED," + System.currentTimeMillis() + "," + dev + "," + bluetoothFileData.getTitle() + "\n");
                    logger.info("CANCELLED: "+ex);
                } catch (Exception ex) {
                    SystemWriter.writeToBluetooth(SystemWriter.BLUETOOTH_TRANSFER, "CANCELLED," + System.currentTimeMillis() + "," + dev + "," + bluetoothFileData.getTitle() + "\n");
                    logger.info("CANCELLED: "+ex);
                }
            }//end for
        }
        if (this.clientSession != null) {
            try {
                clientSession.disconnect(null);
                this.clientSession.close();
            } catch (IOException ignore) {
            }
        }
        this.clientSession = null;
        this.deviceHandler.disconnect(this.mac);

    }

    /**
     * Used in QuickPush
     */
    @Deprecated
    @Override
    public void run() {

        String url = this.deviceHandler.getOBEXUrl(this.mac);
        if (this.device == null || url.length() <= 0) {
            this.deviceHandler.disconnect(this.mac);
            return;
        }
        establishConnection();
        if (clientSession != null) {
            this.OBEXPush();
        } else {
            logger.debug("In thread OBEX THREAD timeout " + mac);
            this.deviceHandler.timeout(this.mac);
        }
        this.deviceHandler.disconnect(this.mac);
    }

    /**
     * Try and authenticate a device with a code
     *
     * @param authCounter changes the authcode depending on the counter
     */
    @Deprecated
    private void authenticate(int authCounter) {
        String authCode;
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
    @Deprecated
    private void establishConnection() {

        int attemptCounter = 0;
        HeaderSet hsConnectReply;
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
