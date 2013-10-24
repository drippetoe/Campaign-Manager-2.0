/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.config.Config;
import javax.ejb.Local;

/**
 *
 * @author Gilberto Gaxiola
 */
@Local
public interface AuthenticatorManagerLocal
{
    
    public boolean authenticate(String token, String mac);
    public boolean authenticate(Config config);
}
