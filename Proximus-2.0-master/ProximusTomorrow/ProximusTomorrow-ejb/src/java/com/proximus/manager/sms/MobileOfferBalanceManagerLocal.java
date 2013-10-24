/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager.sms;

import com.proximus.data.Company;
import com.proximus.data.sms.MobileOffer;
import com.proximus.data.sms.MobileOfferMonthlyBalance;
import com.proximus.manager.AbstractManagerInterface;
import java.util.Date;
import javax.ejb.Local;

/**
 *
 * @author dshaw
 */
@Local
public interface MobileOfferBalanceManagerLocal extends AbstractManagerInterface<MobileOfferMonthlyBalance> {
    
    public void createOrUpdate(MobileOfferMonthlyBalance balance);
    
    public Long getBalance(Company company, Date eventDate);
    
    public MobileOfferMonthlyBalance findByMobileOfferAndMonth(MobileOffer mobileOffer, Date eventDate);
    
}
