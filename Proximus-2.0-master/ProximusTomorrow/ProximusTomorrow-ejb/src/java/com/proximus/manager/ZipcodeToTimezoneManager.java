/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.ZipcodeToTimezone;
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
public class ZipcodeToTimezoneManager extends AbstractManager<ZipcodeToTimezone> implements ZipcodeToTimezoneManagerLocal {

    private static final Logger logger = Logger.getLogger(ZipcodeToTimezoneManager.class.getName());
    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public ZipcodeToTimezoneManager() {
        super(ZipcodeToTimezone.class);
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
    public ZipcodeToTimezone getByZipcode(String zipcode) {
        Query q = em.createQuery("SELECT tz FROM ZipcodeToTimezone tz where tz.zipcode = ?1");
        q.setParameter(1, zipcode);
        List<ZipcodeToTimezone> results = (List<ZipcodeToTimezone>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null; // none
    }
}