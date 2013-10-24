/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager.sms;

import com.proximus.data.Company;
import com.proximus.data.sms.MobileSystemMessage;
import com.proximus.manager.AbstractManagerInterface;
import java.util.Date;
import java.util.Map;
import javax.ejb.Local;

/**
 *
 * @author ronald
 */
@Local
public interface MobileSystemMessageManagerLocal extends AbstractManagerInterface<MobileSystemMessage> {

    public Long getTotalMessageCountByCompanyAndDate(Company company, Date startDate, Date endDate);

    public Map<String, Number> getSystemMessageMap(Company company, Date startDate, Date endDate);
}
