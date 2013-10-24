/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.client.modules;

import com.proximus.data.config.Config;
import com.proximus.util.JAXBUtil;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 *
 * @author ejohansson
 */
public class DebugModule extends Module
{
    private static final Logger logger = Logger.getLogger(DebugModule.class.getName());
    @Override
    public void runTask(Object ... params)
    {
        try {
            logger.log(Priority.DEBUG, "DebugModule: \n"+JAXBUtil.toXml(Config.class, this.config.getConfig()));
        } catch (Exception ex) {
            logger.log(Priority.ERROR, ex);
        }
    }

    @Override
    public void init()
    {
    }
}
