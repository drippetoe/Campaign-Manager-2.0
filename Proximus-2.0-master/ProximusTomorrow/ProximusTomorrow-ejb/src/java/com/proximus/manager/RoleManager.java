/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.License;
import com.proximus.data.Role;
import java.util.ArrayList;
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
public class RoleManager extends AbstractManager<Role> implements RoleManagerLocal {

    
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

    public RoleManager() {
        super(Role.class);
    }

    @Override
    public Role getUserByName(String roleName) {
        Query q = em.createQuery("SELECT r FROM User r WHERE r.rolename = ?1");
        q.setParameter(1, roleName);
        List<Role> results = (List<Role>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null; // none
    }

    @Override
    public List<String> getRoleNames() {
        Query q = em.createQuery("SELECT r FROM Role r order by r.name");
        List<Role> results = (List<Role>) q.getResultList();
        if (results.size() > 0) {
            List<String> names = new ArrayList<String>();
            for (Role r : results) {
                names.add(r.getName());
            }
            return names;
        }
        return null;
    }

    @Override
    public Role findByName(String name) {
        Query q = em.createQuery("SELECT r FROM Role r WHERE r.name = ?1");
        q.setParameter(1, name.toUpperCase());
        List<Role> results = (List<Role>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public List<Role> findByLicense(License license) {
        Query q = em.createQuery("SELECT r FROM Role r WHERE :license MEMBER OF r.licenses");
        q.setParameter("license", license);
        List<Role> results = (List<Role>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }
}
