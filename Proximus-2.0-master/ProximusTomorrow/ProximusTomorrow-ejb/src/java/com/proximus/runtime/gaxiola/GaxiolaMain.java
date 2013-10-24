/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.runtime.gaxiola;

import com.proximus.manager.*;
import javax.persistence.*;
import org.apache.log4j.Logger;

/**
 *
 * @author Gilberto Gaxiola
 */
public class GaxiolaMain {

    private static final Logger logger = Logger.getLogger(GaxiolaMain.class.getName());

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ProximusTomorrow-ejbPUlocalGilberto");
        EntityManager em = emf.createEntityManager();


        CompanyManager companyMgr = new CompanyManager();
        companyMgr.setEntityManager(em);
        CampaignManager campaignMgr = new CampaignManager();
        campaignMgr.setEntityManager(em);

        DeviceManager deviceMgr = new DeviceManager();
        deviceMgr.setEntityManager(em); 
    }
}