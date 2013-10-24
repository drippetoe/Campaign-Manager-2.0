/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.client;

import com.proximus.client.bluetooth.dreamplug.BluetoothDeviceDiscovery;
import com.proximus.client.config.ClientCampaign;
import com.proximus.client.config.ClientConfig;
import com.proximus.client.config.ClientActions;
import com.proximus.client.config.SystemWriter;
import com.proximus.client.modules.cron.ClientScheduler;
import com.proximus.client.modules.CampaignModule;
import com.proximus.client.modules.ProcessExecutor;
import com.proximus.client.modules.ProximusPropertiesModule;
import com.proximus.client.modules.ShellCommandModule;
import com.proximus.client.modules.UploadModule;
import com.proximus.util.client.ClientURISettings;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author Eric Johansson
 */
public final class Main
{
    private ClientConfig config;
    private ClientCampaign clientCampaign;
    private ClientActions actions;
    private ResponseHandler responseHandler;
    public ClientScheduler scheduler;
    public static String deviceIdentifier;
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    public static BluetoothDeviceDiscovery bdd;

    /**
     * constructor pre-setting everything that needs to be in place
     * before running the program
     */
    public Main()
    {
        //Filesystem setup
        ArrayList<File> dirs = new ArrayList<File>();
        //Config and BIN dir
        dirs.add(new File(ClientURISettings.CONFIG_ROOT));
        dirs.add(new File(ClientURISettings.BIN_DIR));
        //Campaigns Dir
        dirs.add(new File(ClientURISettings.CAMPAIGNS_ROOT_DIR));
        //Logs Dirs
        dirs.add(new File(ClientURISettings.LOG_COMPLETED));
        dirs.add(new File(ClientURISettings.LOG_QUEUE));
        dirs.add(new File(ClientURISettings.LOG_WORKING));


        //Create all the folders
        FilesystemChecker.initDirs(dirs);
        //init log4j
        initLog4J();
        logger.log(Priority.DEBUG, "Init Log4J");

        //Setting up the default WEB CONTENT and config properties
        ProcessExecutor.stopLighttpd();
        SystemWriter.makeDefaultIndexPage();
        SystemWriter.makeErrorsPage();
        SystemWriter.makeDefaultLighttpdConfFile();
        SystemWriter.createBluetoothConfig();
        ProcessExecutor.giveLogPermission();
    }

    /**
     * Init log4J configuration
     */
    private void initLog4J()
    {

        URL url = Main.class.getResource("log4j.properties");
        if (url != null) {
            PropertyConfigurator.configure(url);
        } else {
            System.err.println("Couldn't load Log4j Properties");
        }

    }

    /**
     * Main Thread running the program
     * @throws InterruptedException 
     */
    public void go() throws InterruptedException
    {
        
        

        //SETTING UP Bluetooth Deployer is such exists
        //WE HAVE TO DETERMINE IF A DREAMPLUG OR BLUEGIGA
        SystemWriter.allowBluetoothLogging(true);
        SystemWriter.touchBluetoothLogProperties();
        SystemWriter.storeBluetoothClearCache(true);
        bdd = new BluetoothDeviceDiscovery();
        bdd.start();//Start discovery thread
 
        scheduler = new ClientScheduler();
        

        //Setup - Init
        this.config = new ClientConfig();
        //ADDING ALL OBSERVERS
        this.config.addModule(new ProximusPropertiesModule());



        this.clientCampaign = new ClientCampaign();
        //ADDING ALL OBSERVERS
        clientCampaign.addModule(new CampaignModule());
        clientCampaign.addModule(scheduler);

        this.actions = new ClientActions();
        //ADDING ALL OBSERVERS
        this.actions.addModule(new UploadModule());
        this.actions.addModule(new ShellCommandModule());


        //Load config files
        this.config.loadConfiguration();
        this.clientCampaign.loadConfiguration();


        //Registration
        this.responseHandler = new ResponseHandler(this, config, clientCampaign, actions);
        this.responseHandler.register();//While inside
        //registration complete        

        //BEFORE RUNNING
        //CALLING ALL MODULES init method
        this.config.initModules();
        this.clientCampaign.initModules();
        this.actions.initModules();



        if (ClientURISettings.isDEV()) {
            Main.deviceIdentifier = "AABBCCDDEEFF";
        } else {
            String mac = ProcessExecutor.getMACeth0();
            logger.log(Priority.DEBUG, "Getting Mac Address: " + mac);
            if (mac.equals("AABBCCDDEEFF") && !ClientURISettings.isDEV()) {
                logger.log(Priority.ERROR, "ERROR: could get MAC Address as unique identifier for the Device");
                Main.deviceIdentifier = "AABBCCDDEEFF";
            } else {
                Main.deviceIdentifier = mac;
            }
        }

        
        //STARTING First Log Name as well as Lighttpd
        SystemWriter.touchWifiLogProperties();
        SystemWriter.touchBluetoothLogProperties();
        ProcessExecutor.restartLighttpd();
        logger.log(Priority.DEBUG, "Starting lighttpd");


        //STARTING ALL THE SCHEDULERS        
        scheduler.run(ClientScheduler.ONE_MINUTE);
        scheduler.runLog(ClientScheduler.FIVE_MINUTES);
        boolean run = true;
        //Start Client main loop
        while (run) {
            logger.log(Priority.DEBUG, "Checking Status");
            this.responseHandler.checkStatus();
            logger.log(Priority.DEBUG, "Reconnect Interval: " + config.getConfig().getReconnectInterval());
            logger.log(Priority.DEBUG, "SLEEPING");
            Thread.sleep(config.getConfig().getReconnectInterval());
        }
    }

    /**
     * MAIN PROGRAM RUNNING THE DEVICE 
     * @param args 
     */
    public static void main(String[] args)
    {
        try {
            Main main = new Main();
            //BluetoothTEST.start(); // Don NOT run
            main.go();
        } catch (Exception ex) {
            logger.log(Priority.ERROR, ex);
        }
    }
}
