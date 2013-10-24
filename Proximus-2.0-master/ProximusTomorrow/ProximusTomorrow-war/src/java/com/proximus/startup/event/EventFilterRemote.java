/*
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.startup.event;

import javax.ejb.Remote;

/**
 *
 * @author Eric Johansson 
 */
@Remote
public interface EventFilterRemote {

    public void start();
}
