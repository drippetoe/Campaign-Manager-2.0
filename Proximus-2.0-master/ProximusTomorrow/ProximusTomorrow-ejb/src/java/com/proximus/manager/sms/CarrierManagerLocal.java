/*
 * Copyright 2010-2012, Proximus Mobility, LLC.
 * 
 */
package com.proximus.manager.sms;

import com.proximus.data.sms.Carrier;
import com.proximus.manager.AbstractManagerInterface;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author dshaw
 */
@Local
public interface CarrierManagerLocal extends AbstractManagerInterface<Carrier> {

    public Carrier findByName(String name);

    public List<Carrier> findAllSupported();

    public Carrier findCarrierById(Long id);

    public List<Carrier> getAllSorted();
}
