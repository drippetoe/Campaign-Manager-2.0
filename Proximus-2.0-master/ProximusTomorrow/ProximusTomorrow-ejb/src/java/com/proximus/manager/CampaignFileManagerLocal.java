/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */

package com.proximus.manager;

import com.proximus.data.CampaignFile;
import com.proximus.data.CampaignType;
import javax.ejb.Local;

/**
 *
 * @author ronald 
 */
@Local
public interface CampaignFileManagerLocal extends AbstractManagerInterface<CampaignFile>{

    public CampaignFile getFileByType(CampaignType ct);

}
