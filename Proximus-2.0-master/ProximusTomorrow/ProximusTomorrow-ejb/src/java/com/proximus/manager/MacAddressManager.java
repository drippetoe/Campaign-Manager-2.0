/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.MacAddress;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author ronald
 */
@Stateless
public class MacAddressManager extends AbstractManager<MacAddress> implements MacAddressManagerLocal {

    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public MacAddressManager() {
        super(MacAddress.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    public MacAddress getMacAddressByMAC(String mac) {
        Query q = em.createQuery("SELECT m FROM MacAddress m WHERE m.macAddress = ?1");
        q.setParameter(1, mac);
        List<MacAddress> results = (List<MacAddress>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null; // none
    }
}
