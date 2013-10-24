/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager.events;

import com.proximus.data.events.Event;
import com.proximus.manager.AbstractManagerInterface;
import javax.ejb.Local;

/**
 *
 * @author ronald
 */
@Local
public interface EventManagerLocal extends AbstractManagerInterface<Event> {

    public java.util.List<com.proximus.data.events.Event> getQueue(long queue, int maxResults);
}
