/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager.sms;

import com.proximus.data.Company;
import com.proximus.data.sms.BannedWord;
import com.proximus.data.sms.MobileOffer;
import com.proximus.manager.AbstractManager;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.util.ProfanityFilter;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;

/**
 *
 * @author eric
 */
@Stateless
public class BannedWordManager extends AbstractManager<BannedWord> implements BannedWordManagerLocal {

    private static final Logger logger = Logger.getLogger(BannedWordManager.class);
    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;
    @EJB
    CompanyManagerLocal companyMgr;

    public BannedWordManager() {
        super(BannedWord.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }
    
    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    /**
     *
     * @param offer Mobile offer to process
     * @return true if the offer does not contain any banned keywords
     */
    @Override
    public boolean approve(Company company, MobileOffer offer) {
          boolean banned = true;
        String offerText = offer.getOfferText().toLowerCase();
        List<BannedWord> all = this.findAllByCompany(company);
        if (all == null) {
            return true;
        }
        for (BannedWord bannedWord : all) {
            banned = ProfanityFilter.checkIfBannedWord(bannedWord.getWord().toLowerCase(), offerText);
        }
        return banned;
    }

    @Override
    public List<BannedWord> findLike(Company c, String like) {
        Query query = this.em.createQuery("SELECT w FROM BannedWord w WHERE w.word LIKE ?1 AND w.company = ?2");
        query.setParameter(1, like);
        query.setParameter(2, c);
        List<BannedWord> results = (List<BannedWord>) query.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    @Override
    public BannedWord findByWord(Company c, String word) {
        Query query = this.em.createQuery("SELECT w FROM BannedWord w WHERE w.word = ?1 AND w.company = ?2");
        query.setParameter(1, word);
        query.setParameter(2, c);
        List<BannedWord> results = (List<BannedWord>) query.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public List<BannedWord> findAllByCompany(Company c) {
        Query query = this.em.createQuery("SELECT w FROM BannedWord w WHERE w.company = ?1");
        query.setParameter(1, c);
        List<BannedWord> results = (List<BannedWord>) query.getResultList();
        if (results.size() > 0) {
            return results;
        }
        return null;
    }
}
