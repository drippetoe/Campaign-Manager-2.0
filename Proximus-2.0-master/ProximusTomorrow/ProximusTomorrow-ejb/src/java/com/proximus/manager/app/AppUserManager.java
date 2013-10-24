/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager.app;

import com.proximus.data.app.AppUser;
import com.proximus.manager.AbstractManager;
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
public class AppUserManager extends AbstractManager<AppUser> implements AppUserManagerLocal {

    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public AppUserManager() {
        super(AppUser.class);
    }

    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    @Override
    public AppUser getByMsisdnOrIdentifierOrMac(String msisdn, String uuid, String mac) {
        Query q = em.createQuery("SELECT u FROM AppUser u WHERE u.msisdn = :msisdn OR u.uniqueIdentifier = :uuid OR u.macAddress = :mac");
        q.setParameter("msisdn", msisdn);
        q.setParameter("uuid", uuid);
        q.setParameter("mac", mac);
        List<AppUser> results = (List<AppUser>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }
}
