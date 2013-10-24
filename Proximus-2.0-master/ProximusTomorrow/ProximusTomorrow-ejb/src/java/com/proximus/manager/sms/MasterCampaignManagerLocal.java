/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager.sms;

import com.proximus.data.sms.Locale;
import com.proximus.data.sms.MasterCampaign;
import com.proximus.manager.AbstractManagerInterface;
import javax.ejb.Local;

/**
 *
 * @author Angela Mercer
 */
@Local
public interface MasterCampaignManagerLocal extends AbstractManagerInterface<MasterCampaign> {
    
    public MasterCampaign getMasterCampaignByKazeId(String kazeId);
    public MasterCampaign getMasterCampaignByLocale(Locale locale);
    
       
}
