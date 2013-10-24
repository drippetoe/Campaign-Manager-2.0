/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.bean.sms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 *
 * @author Angela Mercer
 */
public class RegistrationsBySourceSummaryReport
{
    Long finalCountRegistrationsCurrentMonth;
    Long finalCountRegistrationsPreviousOneMonth;
    Long finalCountRegistrationsPreviousTwoMonths;
    Long finalCountRegistrationsPreviousThreeMonths;
    HashMap<Long,RegistrationsBySourceSummaryRow> sourceSummaries;
    String currentMonthHeader;
    String previousMonthHeader;
    String previousTwoMonthsHeader;
    String previousThreeMonthsHeader;

    public Long getFinalCountRegistrationsCurrentMonth()
    {
        return finalCountRegistrationsCurrentMonth;
    }

    public void setFinalCountRegistrationsCurrentMonth(Long finalCountRegistrationsCurrentMonth)
    {
        this.finalCountRegistrationsCurrentMonth = finalCountRegistrationsCurrentMonth;
    }

    public Long getFinalCountRegistrationsPreviousOneMonth()
    {
        return finalCountRegistrationsPreviousOneMonth;
    }

    public void setFinalCountRegistrationsPreviousOneMonth(Long finalCountRegistrationsPreviousOneMonth)
    {
        this.finalCountRegistrationsPreviousOneMonth = finalCountRegistrationsPreviousOneMonth;
    }

    public Long getFinalCountRegistrationsPreviousThreeMonths()
    {
        return finalCountRegistrationsPreviousThreeMonths;
    }

    public void setFinalCountRegistrationsPreviousThreeMonths(Long finalCountRegistrationsPreviousThreeMonths)
    {
        this.finalCountRegistrationsPreviousThreeMonths = finalCountRegistrationsPreviousThreeMonths;
    }

    public Long getFinalCountRegistrationsPreviousTwoMonths()
    {
        return finalCountRegistrationsPreviousTwoMonths;
    }

    public void setFinalCountRegistrationsPreviousTwoMonths(Long finalCountRegistrationsPreviousTwoMonths)
    {
        this.finalCountRegistrationsPreviousTwoMonths = finalCountRegistrationsPreviousTwoMonths;
    }

   

    public HashMap<Long, RegistrationsBySourceSummaryRow> getSourceSummaries()
    {
        return sourceSummaries;
    }

    public void setSourceSummaries(HashMap<Long, RegistrationsBySourceSummaryRow> sourceSummaries)
    {
        this.sourceSummaries = sourceSummaries;
    }

    
    public List<RegistrationsBySourceSummaryRow> getSourceRegistrationRowsAsList() {
        return new ArrayList<RegistrationsBySourceSummaryRow>(sourceSummaries.values());
    }

    public String getCurrentMonthHeader()
    {
        return currentMonthHeader;
    }

    public void setCurrentMonthHeader(String currentMonthHeader)
    {
        this.currentMonthHeader = currentMonthHeader;
    }

    public String getPreviousMonthHeader()
    {
        return previousMonthHeader;
    }

    public void setPreviousMonthHeader(String previousMonthHeader)
    {
        this.previousMonthHeader = previousMonthHeader;
    }

    public String getPreviousThreeMonthsHeader()
    {
        return previousThreeMonthsHeader;
    }

    public void setPreviousThreeMonthsHeader(String previousThreeMonthsHeader)
    {
        this.previousThreeMonthsHeader = previousThreeMonthsHeader;
    }

    public String getPreviousTwoMonthsHeader()
    {
        return previousTwoMonthsHeader;
    }

    public void setPreviousTwoMonthsHeader(String previousTwoMonthsHeader)
    {
        this.previousTwoMonthsHeader = previousTwoMonthsHeader;
    }
    

   
    
    
    
}
