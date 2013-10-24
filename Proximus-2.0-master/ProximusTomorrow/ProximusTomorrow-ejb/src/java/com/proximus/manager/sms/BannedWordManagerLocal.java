/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms;

import com.proximus.data.Company;
import com.proximus.data.sms.BannedWord;
import com.proximus.data.sms.MobileOffer;
import com.proximus.manager.AbstractManagerInterface;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author eric
 */
@Local
public interface BannedWordManagerLocal extends AbstractManagerInterface<BannedWord> {

    public BannedWord findByWord(Company company, String word);

    public List<BannedWord> findLike(Company company, String like);

    public List<BannedWord> findAllByCompany(Company company);
    
    public boolean approve(Company company, MobileOffer offer);
    
}
