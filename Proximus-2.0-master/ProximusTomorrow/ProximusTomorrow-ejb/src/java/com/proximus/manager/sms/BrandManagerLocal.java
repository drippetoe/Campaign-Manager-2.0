/*
 * Copyright 2010-2012, Proximus Mobility, LLC.
 * 
 */
package com.proximus.manager.sms;

import com.proximus.data.Brand;
import com.proximus.manager.AbstractManagerInterface;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author dshaw
 */
@Local
public interface BrandManagerLocal extends AbstractManagerInterface<Brand> {

    public Brand getBrandByName(String name);

    public List<Brand> getAllSorted();
}