/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms.report;

import com.proximus.data.Brand;
import com.proximus.data.sms.report.ViewActiveUsersByKeyword;
import com.proximus.data.sms.Keyword;
import com.proximus.manager.AbstractManagerInterface;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Gilberto Gaxiola
 */
@Local
public interface ViewActiveUsersByKeywordManagerLocal extends AbstractManagerInterface<ViewActiveUsersByKeyword> {

    public List<ViewActiveUsersByKeyword> getActiveUsersByKeyword(Brand brand, Date date, Keyword keyword);

    public Long getCountActiveUsersByKeyword(Brand brand, Date date, Keyword keyword);

    public List<Keyword> getTopKeywordsByActiveUsers(Brand brand, Date date, int topX);

    public List<Keyword> getTopKeywordsByActiveUsersAndLocale(Brand brand, com.proximus.data.sms.Locale locale, Date date, int topX);
}
