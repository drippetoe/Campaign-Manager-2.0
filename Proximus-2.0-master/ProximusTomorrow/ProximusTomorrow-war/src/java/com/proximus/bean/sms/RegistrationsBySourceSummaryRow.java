/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.bean.sms;

import com.proximus.data.sms.Keyword;



/**
 *
 * @author Angela Mercer
 */
public class RegistrationsBySourceSummaryRow implements Comparable<RegistrationsBySourceSummaryRow>
{
    Keyword keyword;
    Long totalRegistrationsCurrentMonth;
    Long totalRegistrationsPreviousOneMonth;
    Long totalRegistrationsPreviousTwoMonths;
    Long totalRegistrationsPreviousThreeMonths;
    String percentChange;

    public Keyword getKeyword()
    {
        return keyword;
    }

    public void setKeyword(Keyword keyword)
    {
        this.keyword = keyword;
    }

    public String getPercentChange()
    {
        return percentChange;
    }

    public void setPercentChange(String percentChange)
    {
        this.percentChange = percentChange;
    }

    public Long getTotalRegistrationsCurrentMonth()
    {
        return totalRegistrationsCurrentMonth;
    }

    public void setTotalRegistrationsCurrentMonth(Long totalRegistrationsCurrentMonth)
    {
        this.totalRegistrationsCurrentMonth = totalRegistrationsCurrentMonth;
    }

    public Long getTotalRegistrationsPreviousOneMonth()
    {
        return totalRegistrationsPreviousOneMonth;
    }

    public void setTotalRegistrationsPreviousOneMonth(Long totalRegistrationsPreviousOneMonth)
    {
        this.totalRegistrationsPreviousOneMonth = totalRegistrationsPreviousOneMonth;
    }

    public Long getTotalRegistrationsPreviousThreeMonths()
    {
        return totalRegistrationsPreviousThreeMonths;
    }

    public void setTotalRegistrationsPreviousThreeMonths(Long totalRegistrationsPreviousThreeMonths)
    {
        this.totalRegistrationsPreviousThreeMonths = totalRegistrationsPreviousThreeMonths;
    }

    public Long getTotalRegistrationsPreviousTwoMonths()
    {
        return totalRegistrationsPreviousTwoMonths;
    }

    public void setTotalRegistrationsPreviousTwoMonths(Long totalRegistrationsPreviousTwoMonths)
    {
        this.totalRegistrationsPreviousTwoMonths = totalRegistrationsPreviousTwoMonths;
    }
    
    @Override
    public String toString() {
        return "["  + this.getKeyword().getKeyword() + 
                "|" + this.getKeyword().getSource() +
                "|" + this.getKeyword().getSourceType() +
                "|" + this.getTotalRegistrationsPreviousThreeMonths() +
                "|" + this.getTotalRegistrationsPreviousTwoMonths() + 
                "|" + this.getTotalRegistrationsPreviousOneMonth() +
                "|" + this.getTotalRegistrationsCurrentMonth() + 
                "|" + this.getPercentChange() +
                "]";
    }
   
    @Override
    public int compareTo(RegistrationsBySourceSummaryRow summaryRow)
    {
        int result = this.totalRegistrationsCurrentMonth.compareTo(summaryRow.totalRegistrationsCurrentMonth);
        return result;
    }
   


    
    

    
    
    
    
    
}
