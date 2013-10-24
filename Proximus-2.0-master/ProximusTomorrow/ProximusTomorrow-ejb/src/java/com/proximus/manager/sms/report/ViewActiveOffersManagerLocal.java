/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms.report;

import com.proximus.data.sms.Category;
import com.proximus.data.sms.Property;
import com.proximus.data.sms.report.ViewActiveOffers;
import com.proximus.manager.AbstractManagerInterface;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Gilberto Gaxiola
 */
@Local
public interface ViewActiveOffersManagerLocal extends AbstractManagerInterface<ViewActiveOffers> {

    public List<ViewActiveOffers> getByPropertyAndLanguageCode(Property p, String languageCode);

    public List<ViewActiveOffers> getByPropertyAndCategoryAndLanguageCode(Property p, Category c, String languageCode);

    public List<ViewActiveOffers> getByProperty(Property p);

    public List<ViewActiveOffers> getByPropertyAndLocaleAndCategoryList(Property p, String locale, List<Category> categories);
}
