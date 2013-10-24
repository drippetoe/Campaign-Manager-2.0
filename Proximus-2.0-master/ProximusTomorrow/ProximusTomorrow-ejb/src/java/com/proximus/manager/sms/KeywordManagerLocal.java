/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms;

import com.proximus.data.Company;
import com.proximus.data.sms.Keyword;
import com.proximus.data.sms.ShortCode;
import com.proximus.data.sms.SourceType;
import com.proximus.manager.AbstractManagerInterface;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author ronald
 */
@Local
public interface KeywordManagerLocal extends AbstractManagerInterface<Keyword> {

    public Keyword getKeywordByShortCode(String keyword, ShortCode shortCode);

    public List<Keyword> findAllByCompany(Company c);

    public List<Keyword> findAllNotDeletedByCompany(Company c);

    public List<Keyword> findAllByShortCode(ShortCode shortcode);

    public Company getCompanyByKeyword(String keyword);

    public Keyword getKeywordByKeywordString(String keyword);

    public java.util.List<Keyword> getKeywordByDMA(Company company, com.proximus.data.sms.DMA dma);

    public java.util.List<Keyword> getKeywordByCompanyNoDMA(com.proximus.data.Company company);

    public List<Keyword> findAllByCompanyWithProperty(Company c);
    
    public List<Keyword> getKeywordsBySourceType(SourceType sourcetype);
}
