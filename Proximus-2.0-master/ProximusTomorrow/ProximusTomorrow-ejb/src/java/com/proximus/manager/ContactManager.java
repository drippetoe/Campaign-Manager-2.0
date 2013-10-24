/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.Company;
import com.proximus.data.Contact;
import com.proximus.data.Device;
import java.util.ArrayList;
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
public class ContactManager extends AbstractManager<Contact> implements ContactManagerLocal {

    private static final Logger logger = Logger.getLogger(CompanyManager.class.getName());
    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public ContactManager() {
        super(Contact.class);
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
    public Contact getContactByEmail(String email, Company c) {
        Query q = em.createQuery("SELECT co FROM Contact co WHERE co.email = ?1 and co.company = ?2");
        q.setParameter(1, email.toUpperCase());
        q.setParameter(2, c);
        List<Contact> results = (List<Contact>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null; // none
    }

    @Override
    /**
     * find Contacts by different keywords TODO: Only searching by address_1 it
     * should be changed to take all the fields as search criteria
     */
    public List<Contact> findContactLike(String keyword) {
        Query q = em.createQuery("SELECT co FROM Contact co WHERE UPPER(co.address_one) LIKE ?1 ORDER BY co.address_one");
        q.setParameter(1, "%" + keyword.toUpperCase() + "%");
        List<Contact> results = (List<Contact>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public void delete(Contact entity) {
        for (Device d : entity.getDevices()) {
            d.setContact(null);
            getEntityManager().merge(d);
        }
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    @Override
    public List<Contact> getContactByCompany(Company co) {
        Query q = em.createQuery("SELECT c FROM Contact c WHERE c.company = ?1");
        q.setParameter(1, co);
        List<Contact> results = (List<Contact>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public List<String> getContactEmailByCompany(Company c) {
        Query q = em.createQuery("SELECT co FROM Contact co WHERE co.company = ?1 order by co.email");
        q.setParameter(1, c);
        List<Contact> results = (List<Contact>) q.getResultList();
        if (results.size() > 0) {
            List<String> emails = new ArrayList<String>();
            for (Contact co : results) {
                emails.add(co.getEmail());
            }
            return emails;
        }
        return null;
    }

    @Override
    public List<Contact> findAllByCompany(Company c) {
        Query q = em.createQuery("SELECT co FROM Contact co WHERE co.company = ?1 order by co.email");
        q.setParameter(1, c);
        List<Contact> results = (List<Contact>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }
}
