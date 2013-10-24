/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager.sms;

import com.proximus.data.Company;
import com.proximus.manager.AbstractManagerInterface;
import com.proximus.data.sms.SMSCampaign;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author ronald
 */
@Local
public interface SMSCampaignManagerLocal extends AbstractManagerInterface<SMSCampaign> {

    public List<SMSCampaign> findAllByCompany(Company company);
     public SMSCampaign getSMSCampaignbyName(Company company, String name);
     public SMSCampaign getSMSCampaignById(Long id);
     public SMSCampaign getSMSCampaignById(Company company, Long id);
}
