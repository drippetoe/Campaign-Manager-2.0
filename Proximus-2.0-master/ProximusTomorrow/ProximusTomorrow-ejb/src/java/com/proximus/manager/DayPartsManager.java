/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.DayParts;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.log4j.Logger;

/**
 *
 * @author Gilberto Gaxiola
 */
@Stateless
public class DayPartsManager extends AbstractManager<DayParts> implements DayPartsManagerLocal {

    private static final Logger logger = Logger.getLogger(DayParts.class.getName());
    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public DayPartsManager() {

        super(DayParts.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }
}