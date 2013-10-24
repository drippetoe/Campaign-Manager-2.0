/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager.events;

import com.proximus.data.Company;
import com.proximus.data.events.EventType;
import com.proximus.manager.AbstractManagerInterface;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author ronald
 */
@Local
public interface EventTypeManagerLocal extends AbstractManagerInterface<EventType> {

    public List<EventType> getEventTypesFromCompanyAndAccessLevel(Company company, Long accessLevel);

    public java.util.List<com.proximus.data.events.EventType> getAvailibleEventTypes(com.proximus.data.License license, com.proximus.data.User user);
}
