/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice.response;


import java.util.List;
import net.aixum.webservice.AlertSummary;


/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class AlertsResponseModel extends BaseResponseModel {

    private List<AlertSummary> alerts;

    public AlertsResponseModel() {
        super();
    }

    public AlertsResponseModel(List<AlertSummary> alerts, boolean success, String message) {
        super(success, message);
        this.alerts = alerts;
    }
    
}
