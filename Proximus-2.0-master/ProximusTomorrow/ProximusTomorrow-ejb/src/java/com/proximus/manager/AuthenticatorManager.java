/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.Device;
import com.proximus.data.config.Config;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;

/**
 *
 * @author Gilberto Gaxiola
 */
@Stateless
public class AuthenticatorManager implements AuthenticatorManagerLocal
{
    private Logger logger = Logger.getLogger(AuthenticatorManager.class.getName());
    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;


    @Override
    public boolean authenticate(String token, String mac)
    {
        if (getDeviceByTokenAndMac(token, mac) != null) {
            return true;
        }
        return false;
    }

    @Override
    public boolean authenticate(Config config)
    {
        if (getDeviceByTokenAndMac(config.getAuthenticationToken(), config.getMacAddress()) != null) {
            return true;
        }
        return false;

    }

    private Device getDeviceByTokenAndMac(String token, String mac)
    {
        //Normalize mac
        if ( mac == null )
            return null;
        mac = mac.toUpperCase().replaceAll(":", "");
        Query q = em.createQuery("SELECT d FROM Device d WHERE d.token = ?1 and d.macAddress = ?2");
        q.setParameter(1, token);
        q.setParameter(2, mac);
        List<Device> results = (List<Device>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }
}
