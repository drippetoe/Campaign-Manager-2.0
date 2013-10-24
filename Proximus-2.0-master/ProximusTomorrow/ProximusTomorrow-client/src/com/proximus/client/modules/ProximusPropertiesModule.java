/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.client.modules;

import com.proximus.data.ConfigProperty;
import com.proximus.util.client.ClientURISettings;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 *
 * @author Gilberto Gaxiola
 */
public class ProximusPropertiesModule extends Module
{
    private static final Logger logger = Logger.getLogger(ProximusPropertiesModule.class.getName());
    private String curr_ssid = "";
    private String curr_accessPoint = "";
    private String curr_server = "";
    private String curr_port = "";
    private String curr_channel = "";

    @Override
    public void runTask(Object... params)
    {
        loadProperties();
    }

    @Override
    public void init()
    {
        runTask();
    }

    /**
     * read proximus.properties file and get the properties loaded
     */
    private void loadProperties()
    {
        Properties prop = new Properties();
        if (this.config.getConfig().getConfigProperties().isEmpty()) {
            if (new File(ClientURISettings.PROXIMUS_PROPERTIES_FILE).exists()) {
                try {
                    prop.load(new FileInputStream(new File(ClientURISettings.PROXIMUS_PROPERTIES_FILE)));
                } catch (Exception e) {
                    logger.log(Priority.WARN, "A " + ClientURISettings.PROXIMUS_PROPERTIES_FILE + " file was found but with no properties");
                    return;
                }
            } else {
                return;
            }
        } else {
            prop = this.loadProximusProperties(this.config.getConfig().getConfigProperties());
        }
        String server = prop.getProperty("server");
        ClientURISettings.changeServer(server);
        String port = prop.getProperty("port");
        ClientURISettings.changePort(port);
        String accessPoint = prop.getProperty("access-point");
        ClientURISettings.changeAccessPoint(accessPoint);
        String ssid = prop.getProperty("ssid");
        if (ssid != null && !ssid.isEmpty() && !curr_ssid.equals(ssid)) {
            ssid = ssid.trim();
            ProcessExecutor.changeSSID(ssid);
            logger.log(Priority.DEBUG, "Changing SSID to: " + ssid);
            curr_ssid = ssid;
        }
        String channel_str = prop.getProperty("channel");
        if (channel_str != null && !channel_str.isEmpty() && !curr_channel.contains(channel_str)) {
            try {
                int channel = Integer.parseInt(channel_str);
                if (channel != this.config.getConfig().getChannel()) {
                    this.config.getConfig().setChannel(channel);
                    ProcessExecutor.changeWifiChannel(channel_str);
                    logger.log(Priority.DEBUG, "Changing Wifi Channel to: " + channel);
                    curr_channel = channel_str;
                }
                //this.config.saveConfiguration();
            } catch (Exception e) {
                logger.log(Priority.DEBUG, "Incorrect Wifi Channel");
            }
        }

    }

    public Properties loadProximusProperties(List<ConfigProperty> list)
    {
        Properties prop = new Properties();
        for (ConfigProperty p : list) {
            prop.put(p.getPropKey(), p.getPropValue());
        }
        return prop;
    }
}
