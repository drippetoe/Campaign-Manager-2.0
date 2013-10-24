/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager.sms;

import com.proximus.data.Company;
import com.proximus.data.sms.MobileOffer;
import com.proximus.data.sms.MobileOfferMonthlyBalance;
import com.proximus.data.util.DateUtil;
import com.proximus.manager.AbstractManager;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import org.apache.log4j.Logger;

/**
 *
 * @author dshaw
 */
@Stateless
public class MobileOfferBalanceManager extends AbstractManager<MobileOfferMonthlyBalance> implements MobileOfferBalanceManagerLocal {

    private static final Logger logger = Logger.getLogger(MobileOfferBalanceManager.class.getName());
    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public MobileOfferBalanceManager() {
        super(MobileOfferMonthlyBalance.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    public void createOrUpdate(MobileOfferMonthlyBalance balance) {
        MobileOfferMonthlyBalance existing = this.find(balance.getId());
        if (existing != null) {
            this.update(balance);
        } else {
            this.create(balance);
        }
    }
    
    /**
     * For a given Company on a given date of the month, calculate the average # of sends for this month
     * @param company
     * @return 
     */
    @Override
    public Long getBalance(Company company, Date eventDate)
    {
        Query query = em.createQuery("SELECT COUNT(s.id) FROM MobileOfferSendLog s WHERE s.company = ?1 AND s.eventDate BETWEEN ?2 AND ?3");
        query.setParameter(1, company);
        query.setParameter(2, DateUtil.getFirstDayOfMonth(eventDate));
        query.setParameter(3, DateUtil.getEndOfDay(eventDate));
        Long totalSendsThisMonthForCompany = (Long) query.getSingleResult();
        if ( totalSendsThisMonthForCompany == null || totalSendsThisMonthForCompany == 0 )
        {
            return 0L;
        }
        
        Query queryTwo = em.createQuery("SELECT COUNT(mo.id) FROM MobileOffer mo WHERE mo.company = ?1 AND ?2 >= mo.startDate AND ?3 <= mo.endDate AND mo.status = ?4");
        queryTwo.setParameter(1, company);
        queryTwo.setParameter(2, eventDate, TemporalType.DATE);
        queryTwo.setParameter(3, eventDate, TemporalType.DATE);
        queryTwo.setParameter(4, MobileOffer.APPROVED);
        Long totalActiveOffersForCompany = (Long) query.getSingleResult();
        if ( totalActiveOffersForCompany == null || totalActiveOffersForCompany == 0 )
        {
            return 0L;
        }
        
        Long averageSends = Math.round(totalSendsThisMonthForCompany / (totalActiveOffersForCompany * 1.0));
        
        return averageSends;
    }
    

    /**
     * Get the Balance entity for this month.  If none exists, create it and return that
     * @param mobileOffer
     * @param eventDate
     * @return 
     */
    @Override
    public MobileOfferMonthlyBalance findByMobileOfferAndMonth(MobileOffer mobileOffer, Date eventDate) {
        Query q = em.createQuery("SELECT b FROM MobileOfferMonthlyBalance b WHERE b.mobileOffer = ?1 AND b.eventDate BETWEEN ?2 AND ?3");
        q.setParameter(1, mobileOffer);
        q.setParameter(2, DateUtil.getFirstDayOfMonth(eventDate), TemporalType.DATE);
        q.setParameter(3, DateUtil.getEndOfDay(eventDate), TemporalType.DATE);
        List<MobileOfferMonthlyBalance> results = (List<MobileOfferMonthlyBalance>) q.getResultList();
        if ( results != null && !results.isEmpty())
        {
            return results.get(0);
        }
        else
        {
            MobileOfferMonthlyBalance b = new MobileOfferMonthlyBalance(mobileOffer);
            b.setBalance(getBalance(mobileOffer.getCompany(), eventDate));
            b.setEventDate(DateUtil.getFirstDayOfMonth(eventDate));
            em.persist(b);
            return b;
        }
    }
}
