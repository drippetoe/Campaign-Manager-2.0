/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.Device;
import com.proximus.data.ShellCommandAction;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Gilberto Gaxiola
 */
@Stateless
public class ShellCommandActionManager extends AbstractManager<ShellCommandAction> implements ShellCommandActionManagerLocal {

    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public ShellCommandActionManager() {
        super(ShellCommandAction.class);
    }

    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<ShellCommandAction> getShellCommandsForDevice(Device d) {
        Query q = em.createQuery("SELECT sh FROM ShellCommandAction sh WHERE sh.completed = 0 and sh.device_id = ?1 ");
        q.setParameter(1, d.getId());
        List<ShellCommandAction> results = (List<ShellCommandAction>) q.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public void updateShellCommandAction(ShellCommandAction action) {
        em.merge(action);
    }

    @Override
    public void openSSHFor(Device d, String port) {
        ShellCommandAction action = new ShellCommandAction();
        action.setCommand("reverseSSH");
        action.setCompleted(false);
        action.setDevice_id(d.getId());
        action.setParameters(port);
        em.persist(action);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
