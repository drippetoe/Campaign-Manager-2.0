/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.bean.sms;

import com.proximus.data.sms.DMA;

/**
 *
 * @author Angela Mercer
 */
public class DmaRegistrationSummaryRow
{
    DMA dma;
    Long totalRegistrations;
    Long totalActiveSubscribers;
    Long totalOptOuts;
    String marketPercent;

  
    public String getMarketPercent()
    {
        return marketPercent;
    }

    public void setMarketPercent(String marketPercent)
    {
        this.marketPercent = marketPercent;
    }

    public DMA getDma()
    {
        return dma;
    }

    public void setDma(DMA dma)
    {
        this.dma = dma;
    }

    public Long getTotalActiveSubscribers()
    {
        return totalActiveSubscribers;
    }

    public void setTotalActiveSubscribers(Long totalActiveSubscribers)
    {
        this.totalActiveSubscribers = totalActiveSubscribers;
    }

    public Long getTotalOptOuts()
    {
        return totalOptOuts;
    }

    public void setTotalOptOuts(Long totalOptOuts)
    {
        this.totalOptOuts = totalOptOuts;
    }

    public Long getTotalRegistrations()
    {
        return totalRegistrations;
    }

    public void setTotalRegistrations(Long totalRegistrations)
    {
        this.totalRegistrations = totalRegistrations;
    }
    
    
    
    
}
