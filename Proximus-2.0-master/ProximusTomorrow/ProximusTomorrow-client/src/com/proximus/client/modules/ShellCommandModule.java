/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.client.modules;

import com.proximus.client.Main;
import com.proximus.client.RESTClient;
import com.proximus.client.cron.LogCron;
import com.proximus.data.ShellCommandAction;
import com.proximus.util.client.ApacheRESTUtil;
import com.proximus.util.client.ClientURISettings;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 *
 * @author Gilberto Gaxiola
 */
public class ShellCommandModule extends Module
{
    private Logger logger = Logger.getLogger(ShellCommandModule.class.getName());
    private RESTClient restClient;

    
    
    @Override
    public void init()
    {
        try {
            restClient = new RESTClient(this.config.getConfig().getKeepAlive());
        } catch (Exception e) {
            restClient = new RESTClient(3000);
        }

    }
    
    @Override
    public void runTask(Object... params)
    {
        for (Object obj : this.actions.getActions()) {
            if (obj instanceof ShellCommandAction) {
                ShellCommandAction action = (ShellCommandAction) obj;
                logger.log(Priority.DEBUG, "Got ShellCommand Action: " + action.getCommand() + " with params: " + action.getParameters());
                //Define all different types of Shell Command Actions
                if (action.getCommand().equalsIgnoreCase("bluetooth")) {
                    handleBluetoothCommand(action);
                } else if (action.getCommand().equalsIgnoreCase("dongle")) {
                    handleDongleCommand(action);
                } else if (action.getCommand().equalsIgnoreCase("lighttpd")) {
                    handleLighttpdCommand(action);
                } else if (action.getCommand().equalsIgnoreCase("system info")) {
                    handleSystemInfoCommand(action);
                } else if (action.getCommand().equalsIgnoreCase("ssid")) {
                    handleSSIDCommand(action);
                } else if (action.getCommand().equalsIgnoreCase("bash")) {
                    handleBashCommand(action);
                } else if (action.getCommand().equalsIgnoreCase("reverseSSH")) {
                    handleReverseSSHCommand(action);
                }
            }
        }
    }

    private void handleReverseSSHCommand(ShellCommandAction action) {
        String result;
        String filename = "shellcommand_" + Main.deviceIdentifier + "_reverseSSH";
        String timename = LogCron.sdf.format(new Date()) + ".txt";
        
        String hostPort;
        String devicePort;
        String[] params = action.getParameters().split("\\s+");
        if(params.length >= 2) {
            hostPort = params[0];
            devicePort = params[1];
        } else {
            devicePort = "0";
        }
        try {
            Integer.parseInt(params[0]);
            hostPort = params[0];
        } catch(Exception e) {
            logger.log(Priority.ERROR, e);
            writeOutputLog(filename + "_" + timename, "NO Host Port was Defined");
            return;
        }
        
        result = ProcessExecutor.reverseSSH(hostPort, devicePort);
        writeOutputLog(filename + "_" + timename, result);
    }
    
    private void handleBashCommand(ShellCommandAction action)
    {
        String result;
        String filename = "shellcommand_" + Main.deviceIdentifier + "_bash";
        String timename = LogCron.sdf.format(new Date()) + ".txt";
        result = ProcessExecutor.Bash(action.getParameters());
        writeOutputLog(filename + "_" + timename, result);
    }

    private void handleSSIDCommand(ShellCommandAction action) {
        String result;
        String filename = "shellcommand_" + Main.deviceIdentifier + "_ssid";
        String timename = LogCron.sdf.format(new Date()) + ".txt";
        result = ProcessExecutor.changeSSID(action.getParameters());
        writeOutputLog(filename + "_" + timename, result);
        
    }
    private void handleSystemInfoCommand(ShellCommandAction action)
    {
        String result = "System Info testing";
        String filename = "shellcommand_" + Main.deviceIdentifier + "_systeminfo";
        String timename = LogCron.sdf.format(new Date()) + ".txt";
        result = ProcessExecutor.systemInfo();
        writeOutputLog(filename + "_" + timename, result);

    }

    private void handleLighttpdCommand(ShellCommandAction action)
    {
        String result;
        String filename = "shellcommand_" + Main.deviceIdentifier + "_lighttpd";
        String timename = LogCron.sdf.format(new Date()) + ".txt";
        if (action.getParameters().startsWith("restart")) {
            result = ProcessExecutor.restartLighttpd();
            writeOutputLog(filename + "restart_" + timename, result);
        }
    }

    private void handleBluetoothCommand(ShellCommandAction action)
    {
        String result;
        String filename = "shellcommand_" + Main.deviceIdentifier + "_bluetooth";
        String timename = LogCron.sdf.format(new Date()) + ".txt";
        if (action.getParameters().startsWith("on")) {
            String name = action.getParameters().substring(action.getParameters().indexOf("on ", 0));
            name = name.trim();
            result = ProcessExecutor.startBluetoothService(name);
            writeOutputLog(filename + "on_" + timename, result);
        } else {
            if (action.getParameters().startsWith("off")) {
                result = ProcessExecutor.stopBluetoothService();
                writeOutputLog(filename + "off_" + timename, result);
            }
        }
    }

    private void handleDongleCommand(ShellCommandAction action)
    {
        String result;
        String filename = "shellcommand_" + Main.deviceIdentifier + "_dongle";
        String timename = LogCron.sdf.format(new Date()) + ".txt";
        if (action.getParameters().startsWith("on")) {
            result = ProcessExecutor.dongleOn();
            writeOutputLog(filename + "on_" + timename, result);
        } else {
            if (action.getParameters().startsWith("off")) {
                result = ProcessExecutor.dongleOff();
                writeOutputLog(filename + "off_" + timename, result);
            }
        }
    }

    private void writeOutputLog(String filename, String message)
    {
        String filePath = ClientURISettings.LOG_WORKING + "/tmp/" + filename;
        File f = new File(filePath);
        new File(ClientURISettings.LOG_WORKING + "/tmp").mkdirs();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(f));
            writer.write(message);
            writer.close();
            uploadFile(f);
        } catch (IOException ex) {
            logger.log(Priority.ERROR, ex);
        }

    }

    /**
     * actual POST to upload the file to server
     * @param file 
     */
    private void uploadFile(File file)
    {
        try {
            URI uploadURI = new URI(ClientURISettings.uploadUri);
            HttpResponse response = this.restClient.POSTRequest(uploadURI, ApacheRESTUtil.CreateFileUploadEntity(file));
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                try {
                    FileUtils.moveFileToDirectory(file, new File(ClientURISettings.LOG_COMPLETED), true);
                } catch (FileExistsException fee) {
                    logger.log(Priority.WARN, "File: " + file.getName() + " was already on completed folder");
                    FileUtils.deleteQuietly(file);
                }
            } else {
                //Leave the file where it is we will try again next time;
                logger.log(Priority.ERROR, "Couldn't upload log to the server... Server Status: " + response.getStatusLine().getStatusCode());
            }
            EntityUtils.consume(response.getEntity());
        } catch (URISyntaxException ex) {
            logger.log(Priority.ERROR, ex);
        } catch (IOException ex) {
            logger.log(Priority.ERROR, ex);
        }
    }


}
