/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.client.modules;

import com.proximus.util.client.ClientURISettings;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author ejohansson
 */
public class ProcessExecutor {

    private static final Logger logger = Logger.getLogger(ProcessExecutor.class.getName());
    private static String[] BASH = {"/bin/bash", "-c"};
    private static String SET_UID_CMD = ClientURISettings.BIN_DIR + "/setUIDWrapper";

    public static String getMACeth0() {
        String raw = Bash("/sbin/ifconfig eth0 | grep HWaddr | awk '{print $5}'");
        if (raw == null || raw.isEmpty()) {
            return "AABBCCDDEEFF";
        } else {
            raw = raw.trim().toUpperCase().replaceAll(":", "");
            return raw;
        }
    }

    public static String getIp() {
        try {
            String raw = Bash("/sbin/ifconfig eth0 | grep \"inet addr:\" | awk \"{print \\$1}\"");
            String[] list = raw.split("Bcast");
            String result = list[0].split("inet addr:")[1];
            return result;
        } catch (Exception e) {
            try {
                String raw = Bash("/sbin/ifconfig ppp0 | grep \"inet addr:\" | awk \"{print \\$1}\"");
                String[] list = raw.split("Bcast");
                String result = list[0].split("inet addr:")[1];
                return result;
            } catch (Exception e2) {
                return "Not known";
            }
        }
    }

    public static String getAccessPointIP() {
        try {
            //String raw = Bash("ifconfig uap0 | grep \"inet addr:\" | awk \"{print \\$1}\"");
            String cmd = "/sbin/ifconfig uap0 | grep \"inet addr:\" | awk \"{print \\$1}\"";
            String raw = Bash(cmd);
            String[] list = raw.split("Bcast");
            String result = list[0].split("inet addr:")[1];
            return result;
        } catch (Exception e) {
            return ClientURISettings.accessPoint;
        }
    }
    
    
    public static String reverseSSH(String hostPort, String devicePort) {
        String[] cmdList = {SET_UID_CMD, "reverseSSH", hostPort, devicePort};
        return Exec(cmdList);
    }

    /**
     * Set read-write-execute permissions to user: www-data For this folder
     *
     * @return
     */
    public static String giveLogPermission() {
        String[] cmdList = {SET_UID_CMD, "permit", "logs"};
        return Exec(cmdList);

    }

    public static String startBluetoothService(String name) {
        String[] cmdList = {SET_UID_CMD, "bluetooth", "start", "\"" + name + "\""};
        return Exec(cmdList);
    }

    public static String restartBluetoothService() {
        String[] cmdList = {SET_UID_CMD, "bluetooth", "restart"};
        return Exec(cmdList);
    }

    public static String setBluetoothName(String name) {
        String[] cmdList = {SET_UID_CMD, "bluetooth", "name", "\"" + name + "\""};
        return Exec(cmdList);
    }

    public static String stopBluetoothService() {
        String[] cmdList = {SET_UID_CMD, "bluetooth", "stop"};
        return Exec(cmdList);
    }

    public static String dongleOff() {
        String[] cmdList = {SET_UID_CMD, "dongle", "off"};
        return Exec(cmdList);
    }

    public static String dongleOn() {
        String[] cmdList = {SET_UID_CMD, "dongle", "on"};
        return Exec(cmdList);
    }

    public static String getKernel() {
        return Bash("uname -r");
    }

    public static String restart(String time) {
        if (time == null || time.isEmpty()) {
            return Bash("reboot");
        }
        return Bash("reboot " + time);
    }

    public static String restartLighttpd() {
        String[] cmdList = {SET_UID_CMD, "lighttpd", "restart"};
        String result = Exec(cmdList);
        return result;
    }

    public static String stopLighttpd() {
        String[] cmdList = {SET_UID_CMD, "lighttpd", "stop"};
        return Exec(cmdList);
    }
    
    public static String startLighttpd() {
        String[] cmdList = {SET_UID_CMD, "lighttpd", "start"};
        return Exec(cmdList);
    }
    
    public static String statusLighttpd() {
        String[] cmdList = {SET_UID_CMD, "lighttpd", "status"};
        return Exec(cmdList);
    }
    

    public static String Bash(String... cmd) {
        String[] run = (String[]) ArrayUtils.addAll(BASH, cmd);
        if (ClientURISettings.isDEV()) {

            String result = "";
            for (String s : run) {
                result += s + " ";
            }
            return "Executing: Exec(" + result + ")";

        }
        return Exec(run);
    }

    private static String Exec(String[] cmd) {
        ProcessBuilder pb = new ProcessBuilder(cmd);

        String result = null;
        try {
            Process p = pb.start();
            result = IOUtils.toString(p.getInputStream(), "UTF-8");
            p.waitFor();
        } catch (InterruptedException ex) {
            logger.error(ex);
        } catch (IOException ex) {
            logger.error(ex);

        }
        return result;
    }

    public static String changeWifiChannel(String channel) {
        String[] cmdList = {SET_UID_CMD, "channel", channel};
        return Exec(cmdList);
    }

    public static String changeSSID(String ssid) {

//        String params = "ssid #" + new_ssid + "#";
//        String[] cmdList = new String[2];
//        cmdList[0] = SET_UID_CMD;
//        cmdList[1] = params;
//        return Exec(cmdList);
        String[] cmdList = {SET_UID_CMD, "ssid", "\"" + ssid + "\""};
        return Exec(cmdList);
    }

    /*
     * The portal has two modes open and closed. The open portal mode sets up
     * the device as a captive portal. The system can add or remove devices
     * based upon an IP address. The closed mode sets up the devices as a closed
     * system where all DNS is managed by the device.
     *
     * open start <IP> -> configs and starts open portal with router <IP> closed
     * start <IP> -> configs and starts closed portal witho router <IP>
     */
    public static String startCaptivePortal(String openClosed) {
        String[] cmdList = {SET_UID_CMD, "portal", openClosed, "start", ClientURISettings.accessPoint};
        return Exec(cmdList);
    }

    public static String addClosedPortalDomain(String domain) {
        String[] cmdList = {SET_UID_CMD, "portal", "closed", "add", domain};
        return Exec(cmdList);
    }

    public static String removeClosedPortalDomain(String domain) {
        String[] cmdList = {SET_UID_CMD, "portal", "closed", "remove", domain};
        return Exec(cmdList);
    }

    public static String systemInfo() {
        String[] cmdList = {SET_UID_CMD, "system", "info"};
        return Exec(cmdList);
    }

    public static String LED(int led, boolean on) {
        String onOff = "0";
        if (on) {
            onOff = "1";
        }
        String[] cmdList = {SET_UID_CMD, "led", String.valueOf(led), onOff};
        return Exec(cmdList);
    }
}