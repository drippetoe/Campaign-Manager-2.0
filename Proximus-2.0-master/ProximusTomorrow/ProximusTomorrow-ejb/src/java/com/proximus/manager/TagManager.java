/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.Company;
import com.proximus.data.Tag;
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
public class TagManager extends AbstractManager<Tag> implements TagManagerLocal {

    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public TagManager() {

        super(Tag.class);
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
    public Tag findByName(String name, Company c) {
        Query q = em.createQuery("SELECT t FROM Tag t WHERE t.name = ?1 and t.company = ?2");
        q.setParameter(1, name);
        q.setParameter(2, c);
        List<Tag> results = (List<Tag>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public List<String> getTagNamesByCompany(Company c) {
        Query q = em.createQuery("SELECT t FROM Tag t WHERE t.company = ?1 order by t.name");
        q.setParameter(1, c);
        List<Tag> results = (List<Tag>) q.getResultList();
        if (results.size() > 0) {
            List<String> names = new ArrayList<String>();
            for (Tag t : results) {
                names.add(t.getName());
            }
            return names;
        }
        return null;
    }

    @Override
    public List<Tag> findAllByCompany(Company c) {
        Query q = em.createQuery("SELECT t FROM Tag t WHERE t.company = ?1 order by t.name");
        q.setParameter(1, c);
        List<Tag> results = (List<Tag>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }
}
