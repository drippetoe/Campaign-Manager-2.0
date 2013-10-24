/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.bean.sms;

import com.proximus.data.sms.report.RegistrationSummary;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 *
 * @author Angela Mercer
 */
public class DmaRegistrationSummaryReport
{
    Long finalCountRegistrations;
    Long finalCountSubscribers;
    Long finalCountOptOut;
    HashMap<Long,DmaRegistrationSummaryRow> dmaSummaries;

    public HashMap<Long, DmaRegistrationSummaryRow> getDmaSummaries()
    {
        return dmaSummaries;
    }

    public void setDmaSummaries(HashMap<Long, DmaRegistrationSummaryRow> dmaSummaries)
    {
        this.dmaSummaries = dmaSummaries;
    }

    public Long getFinalCountOptOut()
    {
        return finalCountOptOut;
    }

    public void setFinalCountOptOut(Long finalCountOptOut)
    {
        this.finalCountOptOut = finalCountOptOut;
    }

    public Long getFinalCountRegistrations()
    {
        return finalCountRegistrations;
    }

    public void setFinalCountRegistrations(Long finalCountRegistrations)
    {
        this.finalCountRegistrations = finalCountRegistrations;
    }

    public Long getFinalCountSubscribers()
    {
        return finalCountSubscribers;
    }

    public void setFinalCountSubscribers(Long finalCountSubscribers)
    {
        this.finalCountSubscribers = finalCountSubscribers;
    }

    public List<DmaRegistrationSummaryRow> getDmaRegistrationRowsAsList() {
        return new ArrayList<DmaRegistrationSummaryRow>(dmaSummaries.values());
    }

   
    
    
    
}
