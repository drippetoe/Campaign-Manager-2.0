/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.Privilege;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;

/**
 *
 * @author ronald
 */
@Stateless
public class PrivilegeManager extends AbstractManager<Privilege> implements PrivilegeManagerLocal {

    private static final Logger logger = Logger.getLogger(DeviceManager.class.getName());
    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Privilege> findPrivilegesLike(String keyword) {
        Query q = em.createQuery("SELECT p FROM Privileges p WHERE UPPER(p.privilegeName) LIKE ?1 ORDER BY p.privilegeName");
        q.setParameter(1, "%" + keyword.toUpperCase() + "%");
        List<Privilege> results = (List<Privilege>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    public PrivilegeManager() {
        super(Privilege.class);
    }
}
