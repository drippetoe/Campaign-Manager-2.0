/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import net.aixum.webservice.CompanySummary;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class CompaniesResponseModel extends BaseResponseModel {

    @SerializedName("Companies")
    private List<CompanySummary> companies;

    public CompaniesResponseModel(List<CompanySummary> companies, boolean success, String message) {
        super(success, message);
        this.companies = companies;
    }

    public List<CompanySummary> getCompanies() {
        return companies;
    }

    public void setCompanies(List<CompanySummary> companies) {
        this.companies = companies;
    }

    @Override
    public String toString() {
        String str = "CompaniesResponseModel{" + "companies=";
        /*for (CompanySummary companySummary : companies) {
         str += companySummary;
         }*/
        str += companies;
        str += '}';
        return str;
    }
}
