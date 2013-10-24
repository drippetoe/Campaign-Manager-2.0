/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.Company;
import com.proximus.data.PubNubKey;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Gilberto Gaxiola
 */
@Local
public interface PubNubKeyManagerLocal extends AbstractManagerInterface<PubNubKey>  {
    
    public PubNubKey getPubNubKeyByChannel(String channel, Company c);

    public List<PubNubKey> findAllByCompany(Company c);
    
    public boolean doesPubNubKeyChannelExistsByCompany(String channel, Company c);
    
}
