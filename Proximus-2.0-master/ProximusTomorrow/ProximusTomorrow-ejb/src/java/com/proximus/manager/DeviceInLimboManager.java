/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager;

import com.proximus.data.DeviceInLimbo;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;

/**
 *
 * @author eric
 */
@Stateless
public class DeviceInLimboManager extends AbstractManager<DeviceInLimbo> implements DeviceInLimboManagerLocal {

    private static final Logger logger = Logger.getLogger(DeviceInLimboManager.class.getName());
    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public DeviceInLimboManager() {
        super(DeviceInLimbo.class);
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
    public DeviceInLimbo getDeviceInLimboByMacAdress(String macAddress) {
        //Normalizing macAddress
        macAddress = macAddress.toUpperCase().replaceAll(":", "");
        try {
            Query q = em.createQuery("SELECT d FROM DeviceInLimbo d WHERE d.macAddress = ?1");
            q.setParameter(1, macAddress);
            List<DeviceInLimbo> results = (List<DeviceInLimbo>) q.getResultList();
            if (results.size() > 0) {
                return results.get(0);
            }
            return null; // none
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public DeviceInLimbo getDeviceInLimboByToken(String token) {
        Query q = em.createQuery("SELECT d FROM DeviceInLimbo d WHERE d.token = ?1");
        q.setParameter(1, token);
        List<DeviceInLimbo> results = (List<DeviceInLimbo>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public DeviceInLimbo getDeviceInLimboByMacOrSerial(String input) {
        Query q = em.createQuery("SELECT d FROM DeviceInLimbo d WHERE d.macAddress LIKE ?1 or d.serialNumber LIKE ?1");
        q.setParameter(1, input);
        q.setParameter(1, input);
        List<DeviceInLimbo> results = (List<DeviceInLimbo>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public void truncateDevicesInLimbo() {
        Query q = em.createNativeQuery("TRUNCATE device_in_limbo");
        q.executeUpdate();
    }

    @Override
    public List<DeviceInLimbo> findAllInLimbo() {
        Query q = em.createQuery("SELECT d FROM DeviceInLimbo");
        List<DeviceInLimbo> results = (List<DeviceInLimbo>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null; // none

    }
}
