/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.bean;

import com.proximus.util.ServerURISettings;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class BugTracker implements Serializable {

    private static final String REDMINE_URI = "http://redmine.proximusmobility.net";
    private static final SimpleDateFormat sdf = new SimpleDateFormat(ServerURISettings.DATEFORMAT);

    public static void ReportError(String subject, String description, Throwable ex) {

        if (ex == null) {
            return;
        }

        try {

            StringBuilder sb = new StringBuilder();
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            sb.append("<issue>");
            sb.append("<project_id>4</project_id>");
            sb.append("<priority_id>4</priority_id>");
            sb.append("<subject>");
            sb.append(subject);
            sb.append("</subject>");
            sb.append("<description>");
            sb.append(description + "\n\nStackTrace:\n");
            
            if (ex.getCause()!=null && ex.getCause().getStackTrace() != null) {
                for (StackTraceElement stackTraceElement : ex.getCause().getStackTrace()) {
                    sb.append("> " + stackTraceElement.toString());
                }
            }
            if (ex.getStackTrace() != null) {
                for (StackTraceElement stackTraceElement : ex.getStackTrace()) {
                    sb.append("> " + stackTraceElement.toString());
                }
            }
            sb.append("</description>");
            sb.append("<assigned_to_id>4</assigned_to_id>");
            sb.append("</issue>");
            System.err.println(sb.toString());
            Client client = Client.create();
            client.addFilter(new HTTPBasicAuthFilter("cmanager", "correcthorsebatterystaple"));
            WebResource webResource = client.resource(REDMINE_URI + "/issues.xml");
            ClientResponse response = webResource.type(MediaType.APPLICATION_XML_TYPE).accept(MediaType.APPLICATION_XML_TYPE).post(ClientResponse.class, sb.toString());

            if (response.getStatus() != 201) {
                System.err.println("Error creating issue!");
            } else {
                System.err.println("Success!");
            }

        } catch (Exception e) {
            System.err.println("Throwable is missing info. Unable to track error:" + e.toString());
            e.printStackTrace();
        }
    }
}
