/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.startup;

import javax.ejb.Remote;

/**
 *
 * @author Gilberto Gaxiola
 */
@Remote
interface ProximityParserEJBRemote
{
    
    public void start();
    
}
