/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.Campaign;
import com.proximus.data.CampaignType;
import com.proximus.data.Company;
import java.io.File;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Gilberto Gaxiola
 */
@Local
public interface CampaignManagerLocal extends AbstractManagerInterface<Campaign> {

    public File getFile(Long id, String type);

    public void calculateChecksum(Long id);

    public List<Campaign> findCampaignLike(String keyword);

    public List<Campaign> findAllByCompanyActive(Company company);

    public List<Campaign> findAll(Company company);
    
    public List<Campaign> findCampaignsByCompanyAndModifiedDateLike(Company company, String keyword);

    public void deleteCampaignType(CampaignType ct);

//    public Campaign getCampaignbyName(String name);
}
