/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.client;

import com.proximus.client.config.ClientActions;
import com.proximus.client.config.ClientCampaign;
import com.proximus.client.config.ClientConfig;
import com.proximus.client.modules.ProcessExecutor;
import com.proximus.data.Registration;
import com.proximus.data.config.Config;
import com.proximus.data.response.ServerResponse;
import com.proximus.util.JAXBUtil;
import com.proximus.util.TimeConstants;
import com.proximus.util.client.ApacheRESTUtil;
import com.proximus.util.client.ClientURISettings;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 *
 * @author Eric Johansson
 */
public class ResponseHandler {

    private ClientConfig config;
    private ClientCampaign clientCampaign;
    private ClientActions actions;
    private RESTClient restClient;
    private Main parent;
    private static final Logger logger = Logger.getLogger(ResponseHandler.class.getName());

    ResponseHandler(Main parent, ClientConfig config, ClientCampaign clientCampaign, ClientActions actions) {
        this.parent = parent;
        this.config = config;
        this.clientCampaign = clientCampaign;
        this.actions = actions;
        this.restClient = new RESTClient(config.getConfig().getKeepAlive());
    }

    public void register() {

        String mac = "AABBCCDDEEFF";

        while (!isRegistered()) {
            try {

                if (!ClientURISettings.isDEV()) {

                    mac = ProcessExecutor.getMACeth0();
                    //logger.log(Priority.DEBUG, "Getting Mac Address: " + mac);
                    if (mac.equals("AABBCCDDEEFF") && !ClientURISettings.isDEV()) {
                        logger.warn("Couldn't figure out MAC setting up to AABBCCDDEEFF... THIS DEVICE WILL NEVER REGISTER LIKE THIS");
                    }

                }

                
                
                String kernel = ProcessExecutor.Bash("uname -m -r");
                Logger.getLogger(ResponseHandler.class.getName()).log(Priority.DEBUG, "Kernel Lookup Result: "+kernel);
                Config cfg = config.getConfig();
                if (kernel != null || !kernel.isEmpty()) {
                    cfg.setSoftwareKernel(kernel);
                }
                config.setConfig(cfg);
                List<NameValuePair> formparams = new ArrayList<NameValuePair>();
                formparams.add(new BasicNameValuePair("mac", mac));
                formparams.add(new BasicNameValuePair("serialNumber", "NO_SERIAL"));
                formparams.add(new BasicNameValuePair("build", "" + config.getConfig().getSoftwareBuild()));
                formparams.add(new BasicNameValuePair("major", "" + config.getConfig().getSoftwareMajor()));
                formparams.add(new BasicNameValuePair("minor", "" + config.getConfig().getSoftwareMinor()));
                formparams.add(new BasicNameValuePair("platform", "DPX"));


                UrlEncodedFormEntity entity = null;
                try {
                    entity = new UrlEncodedFormEntity(formparams, "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    logger.error(ex);
                }

                logger.debug("Registering Device.");
                URI registerUri = null;
                try {
                    registerUri = new URI(ClientURISettings.registrationUri);
                    logger.debug("API URI: " + registerUri.toString());
                } catch (URISyntaxException ex) {
                   logger.error(ex);
                }
                Registration reg = null;
                HttpResponse response = null;
                try {
                    response = restClient.PUTRequest(registerUri, entity);
                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        String rawData = ApacheRESTUtil.ResponseToString(response);
                        reg = JAXBUtil.fromXml(Registration.class, rawData);
                        //reg = JAXBUtil.loadFromStream(Registration.class, response.getEntity().getContent(), response.getEntity().getContentLength());
                        if (reg.getToken() != null || reg.getToken().length() > 0) {
                            config.getConfig().setAuthenticationToken(reg.getToken());
                            config.saveConfiguration();
                        } else {
                            logger.error("Unable to get Token from Server");
                        }
                    } else {
                        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {

                            logger.error("Status " + HttpStatus.SC_NOT_FOUND + " Device was not found on the database or is incorrect API");
                        } else {
                            logger.error("Unable to register device. Check internet connection. ");
                        }
                    }
                    

                } catch (Exception ex) {
                    ex.printStackTrace();
                    logger.error("Error HERE "+ex);
                }finally{
                    if(response!=null){
                        try {
                            EntityUtils.consume(response.getEntity());
                        } catch (IOException ex) {
                            //Ignore
                            //logger.error("Error consuming response "+ex);
                        }
                    }
                }
                
                //If registration failed sleep 60 seconds before retrying
                if (!isRegistered()) {
                    Logger.getLogger(ResponseHandler.class.getName()).log(Priority.INFO, "Unable to register will retry in 60 sec...");
                    Thread.sleep(TimeConstants.SECOND(1));
                    config.loadConfiguration();
                }
            } catch (InterruptedException ex) {
                logger.error(ex);
            }
        }//End while
    }

    private boolean isRegistered() {
        if (config.getConfig().getAuthenticationToken().length() > 0) {
            return true;
        }
        return false;
    }

    public void checkStatus() {
        try {

            String mac = "AABBCCDDEEFF";
            String ipAddr = "localhost";
            if (!ClientURISettings.isDEV()) {
                mac = ProcessExecutor.getMACeth0();
                //logger.log(Priority.DEBUG, "Getting Mac Address: " + mac);
                if (mac.equals("AABBCCDDEEFF")) {
                    logger.log(Priority.WARN, "Couldn't figure out MAC setting up to AABBCCDDEEFF... THIS DEVICE WILL NEVER REGISTER LIKE THIS");
                }
            }
            config.loadConfiguration();
            config.getConfig().setMacAddress(mac);
            URI statusURI = new URI(ClientURISettings.statusUri);
            HttpResponse response = this.restClient.POSTRequest(statusURI, ApacheRESTUtil.CreateXMLEntity(JAXBUtil.toXml(Config.class, config.getConfig())));
            handleResponse(response);
            ProcessExecutor.LED(2, true);

        } catch (Exception ex) {
            logger.error(ex);
            ProcessExecutor.LED(2, false);
        }
    }

    private void handleResponse(HttpResponse response) throws IOException, Exception {
        switch (response.getStatusLine().getStatusCode()) {
            case HttpStatus.SC_OK:
                //Nothing change on CONFIG, but the are some DATA,ACTION things the client must do
                //This is for dev
                String rawData = ApacheRESTUtil.ResponseToString(response);
                ServerResponse serverResponse = JAXBUtil.fromXml(ServerResponse.class, rawData);
                clientCampaign.setCamps(serverResponse.getCampaigns());
                clientCampaign.saveConfiguration();
                actions.setActions(serverResponse.getActions());
                config.setConfig(serverResponse.getConfig());
                config.getConfig().setMacAddress("");
                config.getConfig().setCampaignsList(clientCampaign.getShortCampaigns());
                config.saveConfiguration();
                parent.scheduler.runLog(config.getConfig().getLoggingExpression());
                break;
            case HttpStatus.SC_NO_CONTENT:
                //Nothing hast changed
                Logger.getLogger(ResponseHandler.class.getName()).log(Priority.WARN, "No Content");
                break;
            case HttpStatus.SC_NOT_MODIFIED:
                //Nothing changed and NO STATUS on Config moving on
                Logger.getLogger(ResponseHandler.class.getName()).log(Priority.WARN, "Not Modified");
                break;
            case HttpStatus.SC_NOT_FOUND:
                logger.error("404 Response from Server (Unable to find device in Database)");
                break;
            case HttpStatus.SC_FORBIDDEN:
                logger.error("403 Response from Server (Forbidden Access to this device License has expired or Token is invalid");
                break;
            case HttpStatus.SC_INTERNAL_SERVER_ERROR:
                logger.error("500 Response from Server (Internal Server Error)");
                break;
            default:
                Logger.getLogger(ResponseHandler.class.getName()).log(Priority.WARN, "Non 200 Response from server unable to handle response!");
                break;
        }
        EntityUtils.consume(response.getEntity());
    }
}
