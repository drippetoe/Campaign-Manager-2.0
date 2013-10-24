/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice.request;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class CompanyDetailsRequestModel extends BaseRequestModel {

	@SerializedName("CompanyIds")
    private List<Integer> companyIds;
	
	public CompanyDetailsRequestModel() {
		super();
	}

	public CompanyDetailsRequestModel( String userId, int clientVersion, Date lastSync, double latitude, double longitude, double locationAccuracy, Date locationTimestamp) {
        super(userId, clientVersion, lastSync, latitude, longitude, locationAccuracy, locationTimestamp);
    
    }

	/**
	 * @return the companyIds
	 */
	public List<Integer> getCompanyIds() {
		return companyIds;
	}

	/**
	 * @param companyIds the companyIds to set
	 */
	public void setCompanyIds(List<Integer> companyIds) {
		this.companyIds = companyIds;
	}
	
	public void addCompanyId(Integer companyId) {
		if(null==this.companyIds){
			this.companyIds = new ArrayList<Integer>();
		}
		this.companyIds.add(companyId);
	}
	

    
}
