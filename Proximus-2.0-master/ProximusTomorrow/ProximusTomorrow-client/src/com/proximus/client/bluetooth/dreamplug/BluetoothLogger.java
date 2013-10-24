package com.proximus.client.bluetooth.dreamplug;

import com.proximus.client.config.SystemWriter;
import com.proximus.util.client.ClientURISettings;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.bluetooth.RemoteDevice;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author eric
 */
public class BluetoothLogger {
    private Map<RemoteDevice, Long> inRange;
    private static final Logger logger = Logger.getLogger(BluetoothLogger.class.getName());

    public BluetoothLogger() {
        this.inRange = new HashMap<RemoteDevice, Long>();

    }

    /**
     * Calls SystemWriter#writeToBluetooth when a device is no longer visible
     * @param devices 
     */
    public void dwell(Set<RemoteDevice> devices) {
        long now = System.currentTimeMillis();
        /*
         * B - A = A
         */
        List<RemoteDevice> outOfRange = new ArrayList<RemoteDevice>(CollectionUtils.subtract(this.inRange.keySet(), devices));
        for (RemoteDevice remoteDevice : outOfRange) {
            Long start = this.inRange.get(remoteDevice);
            this.inRange.remove(remoteDevice);
            String mac = remoteDevice.getBluetoothAddress();
            String friendlyName = "";
            try {
                friendlyName = remoteDevice.getFriendlyName(false);
            } catch (IOException ex) {
            }
            friendlyName = friendlyName.replaceAll(",", "");
            String entry = ""+mac+","+friendlyName+","+start+","+now+"\n";
            SystemWriter.writeToBluetooth(SystemWriter.BLUETOOTH_DWELL, entry);
            logger.debug(entry);
        }
        /*
         * A - B = A
         */
        List<RemoteDevice> A = new ArrayList<RemoteDevice>(CollectionUtils.subtract(devices, this.inRange.keySet()));
        for (RemoteDevice remoteDevice : A) {
            this.inRange.put(remoteDevice, now);
        }

    }
}
