/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.startup.rulesengine;

import com.proximus.data.Brand;
import java.util.Date;

/**
 *
 * @author dshaw
 */
public abstract class AbstractRulesEngineThread extends Thread {

    private boolean complete = false;
    Date runStart = null;
    Brand brand;

    public AbstractRulesEngineThread(Brand brand) {
        runStart = new Date();
        this.brand = brand;
    }

    public boolean isComplete() {
        return complete;
    }

    protected synchronized void setComplete(boolean complete) {
        this.complete = complete;
    }

    public Date getRunStart() {
        return runStart;
    }
}
