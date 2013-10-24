/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.client.bluetooth.dreamplug;

import com.proximus.client.bluetooth.BluetoothFileData;
import com.proximus.client.config.SystemWriter;
import com.proximus.client.modules.ProcessExecutor;
import com.proximus.util.client.ClientURISettings;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import org.apache.log4j.Logger;
import org.bluez.Error.Canceled;
import org.bluez.Error.Rejected;
import org.bluez.v4.Adapter;
import org.bluez.v4.Agent;
import org.bluez.v4.Manager;
import org.freedesktop.DBus;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.Path;
import org.freedesktop.dbus.UInt32;
import org.freedesktop.dbus.exceptions.DBusException;

/**
 * This class discovers devices and spawns threads that will serve content
 *
 * @author eric
 */
public final class BluetoothDeviceDiscovery extends Thread implements DiscoveryListener, Agent
{

    private static boolean initialized = false;
    private static boolean run = true;
    private static boolean stopped = false;
    private ThreadPoolExecutor executor;
    private BlockingQueue<Runnable> serviceQueue;
    private final Object inquiryCompletedEvent = new Object();
    private final Lock lock = new ReentrantLock();
    private volatile BluetoothDeviceHandler deviceHandler;
    private DiscoveryAgent agent;
    private LocalDevice localDevice;
    private volatile List<BluetoothFileData> files;
    private static final Logger logger = Logger.getLogger(BluetoothDeviceDiscovery.class.getName());
    /*
     * Dwell time variables
     */
    private BluetoothLogger dwellLogger;
    private Set<RemoteDevice> inRange;

    public BluetoothDeviceDiscovery()
    {
//        ProcessExecutor.restartBluetoothService();
        this.deviceHandler = new BluetoothDeviceHandler();
        this.dwellLogger = new BluetoothLogger();
    }

//    public void update(List<BluetoothFileData> files) {
//        this.files = files;
//        for (BluetoothFileData bluetoothFileData : this.files) {
//            logger.debug("\tfile:" + bluetoothFileData.getTitle());
//        }
//        if (!this.initialized) {
//            this.init();
//        }
//        logger.debug("File list size: " + this.files.size());
//
//    }
    @Override
    public void run()
    {
        boolean first = true;
        boolean started = false;
        long startTime;
        long sleepAdjuster;
        long endTime;
        logger.debug("Bluetooth.run()");
        while (!this.initialized)
        {
            this.init();
            try
            {
                Thread.sleep(20000);
            } catch (InterruptedException ex)
            {
                //ignore
            }
        }
        while (run)
        {
            logger.info("Bluetooth Thread Started");
            this.inRange = new HashSet<RemoteDevice>();
            String clear = SystemWriter.loadBluetoothClearCache();
            if (clear != null && (Boolean.parseBoolean(clear) || clear.equalsIgnoreCase("true")))
            {
                logger.info("Loading files...");
                this.files = BluetoothFileData.loadFromProperties();
                logger.info("files:\t" + this.files.size());
                if (this.files.size() > 0)
                {
                    ProcessExecutor.LED(3, true);
                }
                this.deviceHandler.clearCache();
                logger.info("Clearing cache...");
                SystemWriter.storeBluetoothClearCache(false);
            } else
            {
                logger.debug("Do not clear cache");
            }
            startTime = System.currentTimeMillis();
            try
            {
                logger.info("Trying to start bt DiscoveryAgent...");
                started = this.agent.startInquiry(DiscoveryAgent.GIAC, this);
            } catch (BluetoothStateException ex)
            {
                logger.error(ex);
            }
            if (started)
            {
                synchronized (inquiryCompletedEvent)
                { /*
                     * Wait fo inquiry completed event
                     */
                    logger.info("Searching for bluetooth devices...");
                    try
                    {
                        inquiryCompletedEvent.wait(); //Wait for discovery to complete
                    } catch (InterruptedException ex)
                    {
                        logger.error(ex);
                    }

                    int numThreads = this.executor.getActiveCount();
                    int numFiles = this.files.size();
                    logger.debug("BT-Threads:" + numThreads + " Files:" + numFiles);

                    if (numFiles > 0)
                    {
                        if (numThreads > 0)
                        {
                            ProcessExecutor.LED(3, false);
                            ProcessExecutor.LED(4, true);
                        } else
                        {
                            ProcessExecutor.LED(4, false);
                            ProcessExecutor.LED(3, true);
                        }
                    } else
                    {
                        ProcessExecutor.LED(3, false);

                    }
                }

            }

            endTime = System.currentTimeMillis();
            sleepAdjuster = ClientURISettings.MAIN_THREAD_SLEEP - (endTime - startTime);
            if (sleepAdjuster < 0)
            {
                sleepAdjuster = ClientURISettings.MAIN_THREAD_SLEEP;
            }
            started = false;

            try
            {
                logger.debug("Adjusting sleep time to have loop run @ " + ClientURISettings.MAIN_THREAD_SLEEP / 1000 + " rps. Sleep set to " + (sleepAdjuster));
                Thread.sleep(sleepAdjuster);
            } catch (InterruptedException ex)
            { //ignore
            }
        }
        stopped = true;
    }

    public void init()
    {
        try
        {
            DBusConnection conn = DBusConnection.getConnection(DBusConnection.SYSTEM);
            logger.debug("Got D-BUS Connection: " + conn);
            logger.debug("Exporting the agent");
            conn.exportObject("/test/agent", this);
            logger.debug("Exported the agent");
            logger.debug("Listing connection names: ");
            for (String n : conn.getNames())
            {
                logger.debug("Name: " + n);
            }
            DBus dbus = conn.getRemoteObject("org.freedesktop.DBus", "/org/freedesktop/DBus",
                    DBus.class);
            logger.debug("Got D-BUS: " + dbus);
            Manager manager = (Manager) conn.getRemoteObject("org.bluez", "/", Manager.class);
            logger.debug("Got manager: " + manager);
            logger.info("Listing adapters...");
            for (Path p : manager.ListAdapters())
            {
                logger.info("Adapter: " + p.getPath());
            }
            Path defaultAdapter = manager.DefaultAdapter();
            /**
             * Bluetooth adapter fix for external bluetooth radio
             * always select hci 0
             */
            for (Path p : manager.ListAdapters())
            {
                defaultAdapter = p;
                break;
            }


            logger.info("Default adapter path: " + defaultAdapter.getPath());
            Adapter adapter = (Adapter) conn.getRemoteObject("org.bluez", defaultAdapter.getPath(),
                    Adapter.class);
            logger.info("Got the default adapter: " + adapter);
            logger.info("Registering agent");
            adapter.RegisterAgent(new Path("/test/agent"), "DisplayYesNo");
            logger.info("Registered agent");

        } catch (DBusException ex)
        {
            logger.error(ex);
            this.initialized = false;
            return;
        }
        try
        {
            this.localDevice = LocalDevice.getLocalDevice(); //Get machine bluetooth
            this.agent = localDevice.getDiscoveryAgent(); //Get discovery agent
        } catch (BluetoothStateException ex)
        {
            logger.error(ex);
            this.initialized = false;
            return;
        }
        int maxServiceSearches = Integer.parseInt(LocalDevice.getProperty("bluetooth.sd.trans.max"));
        System.setProperty("bluecove.obex.mtu", "65535");
        this.serviceQueue = new LinkedBlockingQueue<Runnable>();
        this.executor = new ThreadPoolExecutor(maxServiceSearches, maxServiceSearches, ClientURISettings.THREAD_POOL_KEEP_ALIVE, TimeUnit.MILLISECONDS, serviceQueue);

        this.inRange = new HashSet<RemoteDevice>();
        this.initialized = true;
        logger.info("Bluetooth Initialized correctly");
    }

    public void stopRunning()
    {
        this.run = false;
    }

    public boolean isRunning()
    {
        return this.stopped;
    }

    @Override
    public void deviceDiscovered(RemoteDevice rd, DeviceClass dc)
    {
        logger.info("Found device: " + rd.getBluetoothAddress());
        this.stdMode(rd, dc);
    }

    public void stdMode(RemoteDevice rd, DeviceClass dc)
    {
        String mac = rd.getBluetoothAddress();

        if (!this.deviceHandler.inBlacklist(mac))
        {
            this.inRange.add(rd);
            this.deviceHandler.addDevice(mac);
        }
        if (this.files.size() > 0)
        {
            boolean timedout = this.deviceHandler.inTimeout(mac);
            boolean connected = this.deviceHandler.isConnected(mac);
            if (!timedout && !connected)
            {
                startServiceDiscoveryOn(rd);
            }
        }
    }

    private void startServiceDiscoveryOn(RemoteDevice rd)
    {
        String mac = rd.getBluetoothAddress();
        if (!this.deviceHandler.isConnected(mac))
        {
            this.deviceHandler.connecting(mac); //Connecting
            logger.debug("*[Queuing Service Discovery] (" + mac + ")");
            BluetoothObexServiceDiscovery bsd = new BluetoothObexServiceDiscovery(this.agent, this.deviceHandler, rd, this.files);
            this.executor.execute(bsd);
        } else
        {
            logger.debug(mac + " busy...");
        }
    }

    /**
     * Something with the stack that makes this function not work as well as it
     * is supposed to do
     *
     * @param rd
     * @deprecated
     */
    @Deprecated
    private void startPush(RemoteDevice rd)
    {
        logger.debug("IN quickPUSH");
        String mac = rd.getBluetoothAddress();
        if (!this.deviceHandler.isConnected(mac))
        {
            List<BluetoothFileData> newFiles = this.deviceHandler.getFilesToServe(mac, this.files);
            String url = this.deviceHandler.getOBEXUrl(mac);
            if (url != null && newFiles.size() > 0)
            {
                BluetoothObexPush bop = new BluetoothObexPush(this.deviceHandler, newFiles, mac);
                bop.setDevice(rd);//<-important
                logger.debug("*[Queuing Bluetooth Obex Push] (" + mac + ")");
                this.deviceHandler.connecting(mac);
                this.executor.execute(bop);
            } else
            {
                //this.deviceHandler.timeout(mac);
                logger.debug("No url");
            }
        } else
        {
            logger.debug(mac + " busy...");
        }
    }

    @Override
    public void inquiryCompleted(int discType)
    {
        synchronized (inquiryCompletedEvent)
        {
            inquiryCompletedEvent.notifyAll();
        }
        switch (discType)
        {
            case DiscoveryListener.INQUIRY_COMPLETED:
                logger.debug("INQUIRY_COMPLETED");
                break;
            case DiscoveryListener.INQUIRY_TERMINATED:
                logger.debug("INQUIRY_TERMINATED");
                break;
            case DiscoveryListener.INQUIRY_ERROR:
                logger.error("INQUIRY_ERROR");
                break;
            default:
                logger.debug("UNKNOWN_RESPONSE_CODE");
                break;
        }
        this.dwellLogger.dwell(this.inRange);
    }

    @Override
    public void servicesDiscovered(int transID, ServiceRecord[] srs)
    {
    }

    @Override
    public void serviceSearchCompleted(int transID, int respCode)
    {
    }

    @Override
    public void Authorize(Path device, String uuid) throws Rejected, Canceled
    {
        logger.debug("Not supported yet - AUTHORIZE");
        logger.debug("DEVICE: " + device);
        logger.debug("UUID: " + uuid);
    }

    @Override
    public void Cancel()
    {
        logger.debug("Not supported yet - CANCEL");
    }

    @Override
    public void ConfirmModeChange(String mode) throws Rejected, Canceled
    {
        logger.debug("Not supported yet - CONFIRM MODE CHANGE");
        logger.debug("MODE: " + mode);
    }

    @Override
    public void DisplayPasskey(Path device, UInt32 passkey, byte entered)
    {
        logger.debug("Not supported yet - DISPLAY PASSKEY");
        logger.debug("DEVICE: " + device);
        logger.debug("PASSKEY: " + passkey.toString());
        logger.debug("ENTERED: " + Integer.toString(entered));
    }

    @Override
    public void Release()
    {
        logger.debug("Not supported yet - RELEASE");
        //this.init();
    }

    @Override
    public void RequestConfirmation(Path device, UInt32 passkey) throws Rejected, Canceled
    {
        ///org/bluez/5935/hci0/dev_74_A7_22_1D_DE_77
        String path = device.toString();
        path = path.substring(path.lastIndexOf("dev_") + 4, path.length());
        path = path.replaceAll("_", "");
        SystemWriter.writeToBluetooth(SystemWriter.BLUETOOTH_LOG, "PAIR," + System.currentTimeMillis() + "," + path + "," + passkey.toString() + "\n");
        logger.debug("REQUEST CONFIRMATION - DEVICE: " + path + " - PASSKEY: " + passkey.toString());
    }

    @Override
    public UInt32 RequestPasskey(Path device) throws Rejected, Canceled
    {
        logger.debug("Not supported yet - REQUEST PASSKEY");
        logger.debug("DEVICE: " + device);
        return new UInt32(0);
    }

    @Override
    public String RequestPinCode(Path device) throws Rejected, Canceled
    {
        logger.debug("Not supported yet - REQUEST PINCODE");
        logger.debug("DEVICE: " + device);
        return "0000";
    }

    @Override
    public boolean isRemote()
    {
        return false;
    }
}
