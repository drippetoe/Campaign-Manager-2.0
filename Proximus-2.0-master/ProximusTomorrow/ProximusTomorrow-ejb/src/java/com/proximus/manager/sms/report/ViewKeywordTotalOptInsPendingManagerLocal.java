/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms.report;

import com.proximus.data.Brand;
import com.proximus.data.Company;
import com.proximus.data.sms.Keyword;
import com.proximus.data.sms.Locale;
import com.proximus.data.sms.report.ViewKeywordTotalOptIns;
import com.proximus.data.sms.report.ViewKeywordTotalOptInsPending;
import com.proximus.manager.AbstractManagerInterface;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;

/**
 *
 * @author Gilberto Gaxiola
 */
@Local
public interface ViewKeywordTotalOptInsPendingManagerLocal extends AbstractManagerInterface<ViewKeywordTotalOptInsPending> {

    public List<ViewKeywordTotalOptInsPending> getAllByBrand(Brand brand);

    public List<ViewKeywordTotalOptInsPending> getAllByCompany(Company company);

    public List<ViewKeywordTotalOptInsPending> getAllByKeyword(Keyword keyword);

    public long getTotalByKeywordAndDate(Keyword keyword, Date startDate, Date endDate);

    public Map<Keyword, Long> getTotalPerKeywordByCompanyAndDate(Company company, Date startDate, Date endDate);

    public Map<Keyword, Long> getTotalPerKeywordByCompanyAndLocaleAndDate(Company company, Locale locale, Date startDate, Date endDate);
}
