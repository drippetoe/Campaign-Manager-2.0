/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
@DatabaseTable(tableName = "companySummary")
public class CompanySummary {

	@DatabaseField(id = true)
    @SerializedName("CompanyId")
    private Integer companyId;
	//@DatabaseField(columnName="revision")
    @SerializedName("Revision")
    private Integer revision;

    public CompanySummary(){
    	// all persisted classes must define a no-arg constructor
        // with at least package visibility
    	this.companyId = 0;
    	this.revision = 0;
    }
    public CompanySummary(Integer companyId, Integer revision) {
        this.companyId = companyId;
        this.revision = revision;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    @Override
    public String toString() {
        return "CompanySummary{" + "companyId=" + companyId + ", revision=" + new Date(revision*1000) + '}';
    }
    
    
}
