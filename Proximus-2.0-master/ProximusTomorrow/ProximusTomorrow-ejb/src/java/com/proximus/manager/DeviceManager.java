/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.Company;
import com.proximus.data.Device;
import com.proximus.data.DeviceInLimbo;
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
public class DeviceManager extends AbstractManager<Device> implements DeviceManagerLocal {

    private static final Logger logger = Logger.getLogger(DeviceManager.class.getName());
    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public DeviceManager() {
        super(Device.class);
    }
    
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    public Device getDeviceByMacAddress(String macAddress) {
        //Normalizing macAddress
        macAddress = macAddress.toUpperCase().replaceAll(":", "");
        Query q = em.createQuery("SELECT d FROM Device d WHERE d.macAddress = ?1");
        q.setParameter(1, macAddress);
        List<Device> results = (List<Device>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null; // none
    }

    @Override
    public Device getDeviceByToken(String token) {
        Query q = em.createQuery("SELECT d FROM Device d WHERE d.token = ?1");
        q.setParameter(1, token);
        List<Device> results = (List<Device>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public Device getDeviceByTokenAndMac(String token, String mac) {
        //Normalize mac
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

    @Override
    public List<Device> getDeviceByCompany(Company co) {
        Query q = em.createQuery("SELECT d FROM Device d WHERE d.company = ?1 order by d.name");
        q.setParameter(1, co);
        List<Device> results = (List<Device>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }
    
        @Override
    public List<Device> getActiveDeviceByCompany(Company co) {
        Query q = em.createQuery("SELECT d FROM Device d WHERE d.company = ?1 and d.active = ?2 order by d.name");
        q.setParameter(1, co);
        q.setParameter(2, true);
        List<Device> results = (List<Device>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public List<Device> findAllActive(Company co) {
        Query q = em.createQuery("SELECT d FROM Device d WHERE d.company = ?1 AND d.active=1");
        q.setParameter(1, co);
        List<Device> results = (List<Device>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public List<Device> findAllInactive(Company co) {
        Query q = em.createQuery("SELECT d FROM Device d WHERE d.company = ?1 AND d.active=0");
        q.setParameter(1, co);
        List<Device> results = (List<Device>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public List<Device> getDeviceByMacOrSerial(String input) {

        input = "%" + input + "%";
        Query q = em.createQuery("SELECT d FROM Device d WHERE d.macAddress LIKE ?1 or d.serialNumber LIKE ?1");
        q.setParameter(1, input);
        q.setParameter(1, input);
        List<Device> results = (List<Device>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public List<Device> findDevicesByCompanyAndModifiedDateLike(Company company, String keyword) {
        Query q = em.createQuery("SELECT device FROM Device device WHERE device.company = ?1 AND device.name LIKE ?2 AND device.active = 1");
        //Add this back to the query after testing ORDER BY device.lastSeen desc
        q.setParameter(1, company);
        q.setParameter(2, "%" + keyword.toUpperCase() + "%");
        List<Device> results = (List<Device>) q.getResultList();

        return results;
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
    public void createDeviceInLimbo(DeviceInLimbo dl) {
        em.persist(dl);
    }

    @Override
    public void updateDeviceInLimbo(DeviceInLimbo dl) {
        em.merge(dl);
    }

    @Override
    public void deleteDeviceInLimbo(DeviceInLimbo dl) {
        em.remove(em.merge(dl));
    }

    @Override
    public DeviceInLimbo findDeviceInLimbo(Object id) {
        return em.find(DeviceInLimbo.class, id);
    }

    @Override
    public Device getDevicebyName(String name) {
        Query q = em.createQuery("SELECT d FROM Device d WHERE d.name = ?1");
        q.setParameter(1, name);
        List<Device> results = (List<Device>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null; // none
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
