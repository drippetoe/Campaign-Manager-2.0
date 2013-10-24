/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager.sms;

import com.proximus.manager.AbstractManagerInterface;
import com.proximus.data.sms.SubscriberCampaign;
import javax.ejb.Local;

/**
 *
 * @author ronald
 */
@Local
public interface SubscriberCampaignManagerLocal extends AbstractManagerInterface<SubscriberCampaign> {
}
