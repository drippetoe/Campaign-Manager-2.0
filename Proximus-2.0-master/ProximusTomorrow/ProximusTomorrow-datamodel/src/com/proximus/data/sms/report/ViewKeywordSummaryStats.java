/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.sms.report;

import com.proximus.data.Company;
import com.proximus.data.sms.Keyword;
import com.proximus.data.sms.SourceType;
import java.io.Serializable;
import java.text.DecimalFormat;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Gilberto Gaxiola
 */
public class ViewKeywordSummaryStats{


    private Keyword keyword;
    private Company company;
    private SourceType sourceType;
    private Long totalOptIns;
    private Long totalOptInsPending;
    private Long totalOptOuts;
    private float marketShare;
    private final DecimalFormat formatter = new DecimalFormat("##.##");

    
    public ViewKeywordSummaryStats(Keyword keyword) {
        this();
        this.keyword = keyword;
    }
    
    public ViewKeywordSummaryStats() {
        this.keyword = null;
        this.sourceType = null;
        this.company = null;
        this.totalOptIns = 0L;
        this.totalOptInsPending = 0L;
        this.totalOptOuts = 0L;
        this.marketShare = 0f;
    }


    public Keyword getKeyword() {
        return keyword;
    }

    public void setKeyword(Keyword keyword) {
        this.keyword = keyword;
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }

    public Long getTotalOptIns() {
        return totalOptIns;
    }

    public void setTotalOptIns(Long totalOptIns) {
        this.totalOptIns = totalOptIns;
    }

    public Long getTotalOptInsPending() {
        return totalOptInsPending;
    }

    public void setTotalOptInsPending(Long totalOptInsPending) {
        this.totalOptInsPending = totalOptInsPending;
    }

    public Long getTotalOptOuts() {
        return totalOptOuts;
    }

    public void setTotalOptOuts(Long totalOptOuts) {
        this.totalOptOuts = totalOptOuts;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public float getMarketShare() {
        return marketShare;
    }

    public void setMarketShare(float marketShare) {
        this.marketShare = marketShare;
    }

    public void calculateMarketShare(long overallOptIns) {
        if (overallOptIns == 0) {
            this.marketShare = 0;
        } else {
            this.marketShare = (float) this.totalOptIns / (float) overallOptIns;
        }
    }

    public String getFormattedMarketShare() {
        String percentage = formatter.format(this.marketShare * 100);

        return percentage + "%";
    }
}
