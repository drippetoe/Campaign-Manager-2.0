/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms;

import com.proximus.data.sms.Country;
import com.proximus.manager.AbstractManagerInterface;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Gilberto Gaxiola
 */
@Local
public interface CountryManagerLocal extends AbstractManagerInterface<Country> {

    public Country findByName(String name);

    public Country findByCode(String code);

    public List<Country> findAllSorted();

    public List<Country> findAllSortedCountriesByProperty();
}
