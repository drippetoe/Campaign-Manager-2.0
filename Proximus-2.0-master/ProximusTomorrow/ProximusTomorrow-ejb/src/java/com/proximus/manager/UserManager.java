/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.Company;
import com.proximus.data.License;
import com.proximus.data.Role;
import com.proximus.data.User;
import java.util.ArrayList;
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
public class UserManager extends AbstractManager<User> implements UserManagerLocal {

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
    public User getUserByUsername(String userName) {
        Query q = em.createQuery("SELECT u FROM User u WHERE u.userName = ?1");
        q.setParameter(1, userName);
        List<User> results = (List<User>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null; // none
    }

    @Override
    public List<User> getUsersByCompany(Company co) {

        Query q = em.createQuery("SELECT u FROM User u WHERE :company MEMBER OF u.companies");
        q.setParameter("company", co);
        List<User> results = (List<User>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public List<String> getCompanyNamesByUser(String u) {
        User user = this.getUserByUsername(u);
        List<Company> results = user.getCompanies();
        if (results.size() > 0) {
            List<String> names = new ArrayList<String>();
            for (Company c : results) {
                names.add(c.getName());
            }
            return names;
        }
        return null;
    }

    @Override
    public List<Company> getCompaniesByUser(User u) {
        Query q = em.createQuery("SELECT u FROM User WHERE c.user = ?1");
        q.setParameter(1, u);
        List<User> results = (List<User>) q.getResultList();
        if (results.size() > 0) {
            User user = results.get(0);
            return user.getCompanies();
        }
        return null;
    }

    @Override
    public List<User> getUsersByRole(Company c, Role r) {
        Query q = em.createQuery("SELECT u FROM User u WHERE u.role = :role AND :company MEMBER OF u.companies ");
        q.setParameter("role", r);
        q.setParameter("company", c);

        List<User> results = (List<User>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public List<User> findUserLike(String keyword) {
        Query q = em.createQuery("SELECT u FROM User u WHERE UPPER(u.userName) LIKE ?1 ORDER BY u.userName");
        q.setParameter(1, "%" + keyword.toUpperCase() + "%");
        List<User> results = (List<User>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public List<Company> getCompaniesFromUser(Long id) {
        User u = this.find(id);
        return u.getCompanies();
    }

    @Override
    public List<Company> getCompaniesFromUserAndLicense(User user, String license) {
        Query q = em.createQuery("SELECT c FROM Company c WHERE :user MEMBER OF c.users AND c.license.licenseText LIKE :license ORDER BY c.name");
        q.setParameter("user", user);
        q.setParameter("license", "%" + license + "%");
        List<Company> results = (List<Company>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public List<User> getSuperUsers() {
        Query q = em.createQuery("SELECT u FROM User u WHERE u.role.name = ?1");
        q.setParameter(1, Role.SUPER_USER);
        List<User> results = (List<User>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    public UserManager() {
        super(User.class);
    }
}