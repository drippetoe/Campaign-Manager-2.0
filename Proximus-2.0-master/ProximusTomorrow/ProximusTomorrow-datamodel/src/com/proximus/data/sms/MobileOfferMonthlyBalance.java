/*
 * Copyright 2010-2012, Proximus Mobility, LLC.
 * 
 */
package com.proximus.data.sms;

import com.proximus.data.util.DateUtil;
import java.io.Serializable;
import java.util.Date;
import javax.jdo.annotations.Index;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 *
 * @author dshaw
 */
@Entity
@Table(name="mobile_offer_balance")
public class MobileOfferMonthlyBalance implements Serializable {
     private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id = 0L;
    /**
     * Tracking for the month -- should be dated day:1
     */
    @Index
    @Column(name="event_date")
    @Temporal(TemporalType.DATE)
    private Date eventDate;
    @Index
    @ManyToOne
    private MobileOffer mobileOffer;
    /**
     * Balance is a send count, weighted by the date on which the offer was
     * added into the system.  When a mobile offer is added, the average send
     * count of all other offers for the company is persisted in the balance 
     * field for future sends
     */
    @Column(name="balance")
    private Long balance;
    
    public MobileOfferMonthlyBalance()
    {
        this.balance = 0L;
    }
    
    public MobileOfferMonthlyBalance(MobileOffer mobileOffer)
    {
        this.mobileOffer = mobileOffer;
        this.balance = 0L;
    }
    
    public MobileOfferMonthlyBalance(MobileOffer mobileOffer, Long startingBalance)
    {
        this.mobileOffer = mobileOffer;
        this.balance = startingBalance;
    }
    

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }
    
    public void incrementBalance(Long incrementCount)
    {
        this.balance += incrementCount;
    }
    
    public void incrementBalance()
    {
        this.balance++;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = DateUtil.getFirstDayOfMonth(eventDate);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MobileOffer getMobileOffer() {
        return mobileOffer;
    }

    public void setMobileOffer(MobileOffer mobileOffer) {
        this.mobileOffer = mobileOffer;
    }
}
