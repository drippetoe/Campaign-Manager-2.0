/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager.events;

import com.proximus.data.User;
import com.proximus.data.events.EventSubscription;
import com.proximus.manager.AbstractManagerInterface;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author ronald
 */
@Local
public interface EventSubscriptionManagerLocal extends AbstractManagerInterface<EventSubscription> {

    public List<EventSubscription> getEventSubscriptionByUser(User user);
}
