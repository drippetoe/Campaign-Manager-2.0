/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms;

import com.proximus.data.Brand;
import com.proximus.data.Company;
import com.proximus.data.sms.MobileOffer;
import com.proximus.data.sms.Property;
import com.proximus.data.sms.Retailer;
import com.proximus.data.util.DateUtil;
import com.proximus.manager.AbstractManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

/**
 *
 * @author Gilberto Gaxiola
 */
@Stateless
public class MobileOfferManager extends AbstractManager<MobileOffer> implements MobileOfferManagerLocal {

    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public MobileOfferManager() {
        super(MobileOffer.class);
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
    public MobileOffer getMobileOfferbyId(Long id) {
        Query q = em.createQuery("SELECT mo FROM MobileOffer mo WHERE mo.id = ?1");
        q.setParameter(1, id);
        List<MobileOffer> results = (List<MobileOffer>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null; // none
    }

    @Override
    public List<MobileOffer> findAllByCompany(Company company) {
        Query query = em.createQuery("SELECT mo FROM MobileOffer mo WHERE mo.company = ?1");
        query.setParameter(1, company);
        List<MobileOffer> result = (List<MobileOffer>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    @Override
    public List<MobileOffer> findActiveAndApprovedByCompany(Company company) {
        Query query = em.createQuery("SELECT mo FROM MobileOffer mo"
                + " WHERE mo.company = :company"
                + " AND mo.status = :status"
                + " AND mo.deleted = :deleted"
                + " AND :date BETWEEN mo.startDate and mo.endDate");
        query.setParameter("company", company);
        query.setParameter("status", MobileOffer.APPROVED);
        query.setParameter("deleted", false);
        query.setParameter("date", DateUtil.getStartOfDay(new Date()));
        List<MobileOffer> result = (List<MobileOffer>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    @Override
    public List<MobileOffer> findByCompanyNotDeleted(Company company) {
        Query query = em.createQuery("SELECT mo FROM MobileOffer mo WHERE mo.company = ?1 AND mo.deleted = ?2 ORDER BY mo.endDate, mo.lastModified");
        query.setParameter(1, company);
        query.setParameter(2, false);
        List<MobileOffer> result = (List<MobileOffer>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    @Override
    public List<MobileOffer> findByCompanyPendingApproval(Company company) {
        Query query = em.createQuery("SELECT mo FROM MobileOffer mo WHERE mo.company = :company AND mo.deleted = :deleted AND mo.status = :pending ORDER BY mo.endDate, mo.lastModified");
        query.setParameter("company", company);
        query.setParameter("deleted", false);
        query.setParameter("pending", MobileOffer.PENDING);
        List<MobileOffer> result = (List<MobileOffer>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    @Override
    public List<MobileOffer> findByCompanyNotExpired(Company company) {
        Query query = em.createQuery("SELECT mo FROM MobileOffer mo"
                + " WHERE mo.company = :company"
                + " AND mo.endDate >= :date"
                + " AND mo.deleted = :deleted"
                + " ORDER BY mo.endDate, mo.lastModified");
        query.setParameter("company", company);
        query.setParameter("date", new Date());
        query.setParameter("deleted", false);
        List<MobileOffer> result = (List<MobileOffer>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    @Override
    public List<MobileOffer> findActiveByRetailerOnly(Company company, Retailer retailer) {
        Query query = em.createQuery("SELECT mo FROM MobileOffer mo"
                + " WHERE mo.retailer = :retailer"
                + " AND mo.deleted = :deleted"
                + " AND mo.company = :company"
                + " AND mo.status != :status"
                + " AND :date BETWEEN mo.startDate AND mo.endDate");
        query.setParameter("retailer", retailer);
        query.setParameter("deleted", false);
        query.setParameter("company", company);
        query.setParameter("status", MobileOffer.DELETED);
        query.setParameter("date", DateUtil.getStartOfDay(new Date()));
        List<MobileOffer> result = (List<MobileOffer>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    @Override
    public List<MobileOffer> findActiveByPropertyOnly(Property property) {
        Query query = em.createQuery("SELECT mo FROM MobileOffer mo"
                + " WHERE mo.property = :property"
                + " AND mo.deleted = :deleted"
                + " AND mo.status != :status"
                + " AND :date BETWEEN mo.startDate AND mo.endDate");
        query.setParameter("property", property);
        query.setParameter("deleted", false);
        query.setParameter("status", MobileOffer.DELETED);
        query.setParameter("date", DateUtil.getStartOfDay(new Date()));

        List<MobileOffer> result = (List<MobileOffer>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    @Override
    public List<MobileOffer> findActiveOffersForProperty(Property property) {
        Query query;
        if (property == null) {
            query = em.createQuery("SELECT mo FROM MobileOffer mo"
                    + " WHERE mo.retailer.id IN :retailers "
                    + " AND mo.deleted = :deleted"
                    + " AND mo.status = :status"
                    + " AND :date BETWEEN mo.startDate AND mo.endDate");
            List<Retailer> retailers = property.getRetailers();
            List<Long> ids = new ArrayList<Long>();
            for (Retailer retailer : retailers) {
                ids.add(retailer.getId());
            }
            query.setParameter("retailers", ids);
            query.setParameter("deleted", false);
            query.setParameter("status", MobileOffer.APPROVED);
            query.setParameter("date", DateUtil.getStartOfDay(new Date()));
            List<MobileOffer> result = (List<MobileOffer>) query.getResultList();
            if (result.size() > 0) {
                return result;
            }
            return null; // none
        } else {
            query = em.createQuery("SELECT mo FROM MobileOffer mo"
                    + " WHERE :prop MEMBER OF mo.properties"
                    + " AND mo.deleted = :deleted"
                    + " AND mo.status = :status"
                    + " AND :date BETWEEN mo.startDate AND mo.endDate");
            query.setParameter("prop", property);
            query.setParameter("deleted", false);
            query.setParameter("status", MobileOffer.APPROVED);
            query.setParameter("date", DateUtil.getStartOfDay(new Date()));
            List<MobileOffer> result = (List<MobileOffer>) query.getResultList();
            if (result.size() > 0) {
                return result;
            }
            return null; // none

        }

    }

    @Override
    public List<MobileOffer> findByCompanyExpired(Company company) {
        Query query = em.createQuery("SELECT mo FROM MobileOffer mo"
                + " WHERE mo.company = :company"
                + " AND mo.endDate < :date"
                + " AND mo.deleted = :deleted"
                + " ORDER BY mo.endDate, mo.lastModified");
        query.setParameter("company", company);
        query.setParameter("date", new Date());
        query.setParameter("deleted", false);
        List<MobileOffer> result = (List<MobileOffer>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    @Override
    public List<MobileOffer> findActiveByCompanies(List<Company> companies) {
        Query query = em.createQuery("SELECT mo FROM MobileOffer mo"
                + " WHERE mo.company.id IN :compIds"
                + " AND :date BETWEEN mo.startDate AND mo.endDate"
                + " AND mo.deleted = :deleted"
                + " ORDER BY mo.endDate, mo.lastModified");
        List<Long> compId = new ArrayList<Long>();
        for (Company company : companies) {
            compId.add(company.getId());
        }
        query.setParameter("compIds", compId);
        query.setParameter("date", DateUtil.getStartOfDay(new Date()));
        query.setParameter("deleted", false);
        List<MobileOffer> result = (List<MobileOffer>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    @Override
    public List<MobileOffer> findActiveByCompany(Company company) {
        Query query = em.createQuery("SELECT mo FROM MobileOffer mo"
                + " WHERE mo.company = :company"
                + " AND mo.deleted = :deleted"
                + " AND :date BETWEEN mo.startDate AND mo.endDate");

        query.setParameter("company", company);
        query.setParameter("deleted", false);
        query.setParameter("date", DateUtil.getStartOfDay(new Date()));
        List<MobileOffer> result = (List<MobileOffer>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    @Override
    public List<MobileOffer> findExpiredByCompanies(List<Company> companies) {
        Query query = em.createQuery("SELECT mo FROM MobileOffer mo"
                + " WHERE mo.company.id IN :compIds"
                + " AND mo.deleted = :deleted"
                + " AND mo.endDate < :date"
                + " ORDER BY mo.endDate, mo.lastModified");
        List<Long> compId = new ArrayList<Long>();
        for (Company company : companies) {
            compId.add(company.getId());
        }
        query.setParameter("compIds", compId);
        query.setParameter("date", DateUtil.getEndOfDay(new Date()));;
        query.setParameter("deleted", false);
        List<MobileOffer> result = (List<MobileOffer>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    @Override
    public List<MobileOffer> findNotExpiredByCompanies(List<Company> companies) {
        Query query = em.createQuery("SELECT mo FROM MobileOffer mo"
                + " WHERE mo.company.id IN :compIds"
                + " AND mo.deleted = :deleted"
                + " AND mo.endDate >= :date"
                + " ORDER BY mo.endDate, mo.lastModified");
        List<Long> compId = new ArrayList<Long>();
        for (Company company : companies) {
            compId.add(company.getId());
        }
        query.setParameter("compIds", compId);
        query.setParameter("date", DateUtil.getEndOfDay(new Date()));;
        query.setParameter("deleted", false);
        List<MobileOffer> result = (List<MobileOffer>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    @Override
    public List<MobileOffer> findNotDeletedByCompanies(List<Company> companies) {
        Query query = em.createQuery("SELECT mo FROM MobileOffer mo"
                + " WHERE mo.company.id IN :compIds"
                + " AND mo.deleted = :deleted"
                + " ORDER BY mo.endDate, mo.lastModified");
        List<Long> compId = new ArrayList<Long>();
        for (Company company : companies) {
            compId.add(company.getId());
        }
        query.setParameter("compIds", compId);
        query.setParameter("deleted", false);
        List<MobileOffer> result = (List<MobileOffer>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    @Override
    public List<MobileOffer> findRetailWideActiveByBrand(Brand b) {
        Query query = em.createQuery("SELECT mo FROM MobileOffer mo"
                + " WHERE mo.retailOnly = :retail"
                + " AND mo.deleted = :deleted"
                + " AND mo.brand = :brand"
                + " AND :date BETWEEN mo.startDate AND mo.endDate");

        query.setParameter("retail", true);
        query.setParameter("deleted", false);
        query.setParameter("brand", b);
        query.setParameter("date", DateUtil.getStartOfDay(new Date()));
        List<MobileOffer> result = (List<MobileOffer>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none

    }

    @Override
    public List<MobileOffer> findRetailWidePendingApprovalByBrand(Brand b) {
        Query query = em.createQuery("SELECT mo FROM MobileOffer mo"
                + " WHERE mo.retailOnly = :retail"
                + " AND mo.deleted = :deleted"
                + " AND mo.brand = :brand"
                + " AND mo.status = :pending"
                + " AND :date BETWEEN mo.startDate AND mo.endDate");

        query.setParameter("retail", true);
        query.setParameter("deleted", false);
        query.setParameter("brand", b);
        query.setParameter("pending", MobileOffer.PENDING);
        query.setParameter("date", DateUtil.getStartOfDay(new Date()));
        List<MobileOffer> result = (List<MobileOffer>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    @Override
    public List<MobileOffer> findRetailWideExpiredByBrand(Brand b) {
        Query query = em.createQuery("SELECT mo FROM MobileOffer mo"
                + " WHERE mo.retailOnly = :retail"
                + " AND mo.brand = :brand"
                + " AND mo.deleted = :deleted"
                + " AND mo.endDate < :date"
                + " ORDER BY mo.endDate, mo.lastModified");
        query.setParameter("retail", true);
        query.setParameter("deleted", false);
        query.setParameter("brand", b);
        query.setParameter("date", DateUtil.getEndOfDay(new Date()));
        List<MobileOffer> result = (List<MobileOffer>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    @Override
    public List<MobileOffer> findRetailWideNotExpiredByBrand(Brand b) {
        Query query = em.createQuery("SELECT mo FROM MobileOffer mo"
                + " WHERE mo.retailOnly = :retail"
                + " AND mo.brand = :brand"
                + " AND mo.deleted = :deleted"
                + " AND mo.endDate >= :date"
                + " ORDER BY mo.endDate, mo.lastModified");
        query.setParameter("retail", true);
        query.setParameter("deleted", false);
        query.setParameter("brand", b);
        query.setParameter("date", DateUtil.getEndOfDay(new Date()));
        List<MobileOffer> result = (List<MobileOffer>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    @Override
    public List<MobileOffer> findRetailWideNotDeletedByBrand(Brand b) {
        Query query = em.createQuery("SELECT mo FROM MobileOffer mo"
                + " WHERE mo.retailOnly = :retail"
                + " AND mo.deleted = :deleted"
                + " AND mo.brand = :brand"
                + " ORDER BY mo.endDate, mo.lastModified");
        query.setParameter("retail", true);
        query.setParameter("deleted", false);
        query.setParameter("brand", b);
        List<MobileOffer> result = (List<MobileOffer>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    @Override
    public List<MobileOffer> findRetailWideActiveAndApproved(Brand b) {
        Query query = em.createQuery("SELECT mo FROM MobileOffer mo"
                + " WHERE mo.retailOnly = :retail"
                + " AND mo.deleted = :deleted"
                + " AND mo.brand = :brand"
                + " AND :date BETWEEN mo.startDate AND mo.endDate"
                + " AND mo.status = :status"
                + " ORDER BY mo.endDate, mo.lastModified");
        query.setParameter("retail", true);
        query.setParameter("deleted", false);
        query.setParameter("brand", b);
        query.setParameter("date", DateUtil.getStartOfDay(new Date()));
        query.setParameter("status", MobileOffer.APPROVED);
        List<MobileOffer> result = (List<MobileOffer>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    @Override
    public List<MobileOffer> findActiveByProperties(List<Property> properties) {
        Query query = em.createQuery("SELECT mo FROM MobileOffer mo"
                + " INNER JOIN mo.properties property"
                + " WHERE property.id in :propIds"
                + " AND :date BETWEEN mo.startDate AND mo.endDate"
                + " AND mo.deleted = :deleted"
                + " ORDER BY mo.endDate, mo.lastModified");



        List<Long> propIds = new ArrayList<Long>();
        for (Property p : properties) {
            propIds.add(p.getId());
        }
        query.setParameter("propIds", propIds);
        query.setParameter("date", DateUtil.getStartOfDay(new Date()));
        query.setParameter("deleted", false);

        List<MobileOffer> result = (List<MobileOffer>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    @Override
    public List<MobileOffer> findPendingApprovalByCompanies(List<Company> companies) {
        Query query = em.createQuery("SELECT mo FROM MobileOffer mo"
                + " WHERE mo.company.id IN :compIds"
                + " AND mo.deleted = :deleted"
                + " AND mo.status = :pending"
                + " ORDER BY mo.endDate, mo.lastModified");
        List<Long> compId = new ArrayList<Long>();
        for (Company company : companies) {
            compId.add(company.getId());
        }
        query.setParameter("compIds", compId);
        query.setParameter("deleted", false);
        query.setParameter("pending", MobileOffer.PENDING);
        List<MobileOffer> result = (List<MobileOffer>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    @Override
    public List<MobileOffer> findExpiredByProperties(List<Property> properties) {
        Query query = em.createQuery("SELECT mo FROM MobileOffer mo"
                + " INNER JOIN mo.properties property"
                + " WHERE property.id in :propIds"
                + " AND mo.deleted = :deleted"
                + " AND mo.endDate < :date"
                + " ORDER BY mo.endDate, mo.lastModified");
        List<Long> propIds = new ArrayList<Long>();
        for (Property property : properties) {
            propIds.add(property.getId());
        }
        query.setParameter("propIds", propIds);
        query.setParameter("date", DateUtil.getEndOfDay(new Date()));
        query.setParameter("deleted", false);
        List<MobileOffer> result = (List<MobileOffer>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    @Override
    public List<MobileOffer> findNotExpiredByProperties(List<Property> properties) {
        Query query = em.createQuery("SELECT mo FROM MobileOffer mo"
                + " INNER JOIN mo.properties property"
                + " WHERE property.id in :propIds"
                + " AND mo.deleted = :deleted"
                + " AND mo.endDate >= :date"
                + " ORDER BY mo.endDate, mo.lastModified");
        List<Long> propIds = new ArrayList<Long>();
        for (Property property : properties) {
            propIds.add(property.getId());
        }
        query.setParameter("propIds", propIds);
        query.setParameter("date", DateUtil.getEndOfDay(new Date()));
        query.setParameter("deleted", false);
        List<MobileOffer> result = (List<MobileOffer>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    @Override
    public List<MobileOffer> findPendingApprovalByProperties(List<Property> properties) {
        Query query = em.createQuery("SELECT mo FROM MobileOffer mo"
                + " INNER JOIN mo.properties property"
                + " WHERE property.id in :propIds"
                + " AND mo.deleted = :deleted"
                + " AND mo.status = :pending"
                + " ORDER BY mo.endDate, mo.lastModified");
        List<Long> propIds = new ArrayList<Long>();
        for (Property property : properties) {
            propIds.add(property.getId());
        }
        query.setParameter("propIds", propIds);
        query.setParameter("deleted", false);
        query.setParameter("pending", MobileOffer.PENDING);
        List<MobileOffer> result = (List<MobileOffer>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    @Override
    public List<MobileOffer> findNotDeletedByProperties(List<Property> properties) {

        Query query = em.createQuery("SELECT mo FROM MobileOffer mo"
                + " INNER JOIN mo.properties property"
                + " WHERE property.id in :propIds"
                + " AND mo.deleted = :deleted"
                + " ORDER BY mo.endDate, mo.lastModified");
        List<Long> propIds = new ArrayList<Long>();
        for (Property property : properties) {
            propIds.add(property.getId());
        }
        query.setParameter("propIds", propIds);
        query.setParameter("deleted", false);
        List<MobileOffer> result = (List<MobileOffer>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    @Override
    public List<MobileOffer> findRetailWideActiveInProperties(List<Property> properties) {
        Query query = em.createQuery("SELECT mo FROM MobileOffer mo"
                + " WHERE mo.retailer.id IN :retIds"
                + " AND mo.retailOnly = :retailOnly"
                + " AND mo.deleted = :deleted"
                + " AND :date BETWEEN mo.startDate AND mo.endDate");
        List<Long> retIds = getRetailerIds(properties);
        query.setParameter("retailOnly", true);
        query.setParameter("retIds", retIds);
        query.setParameter("deleted", false);
        query.setParameter("date", DateUtil.getStartOfDay(new Date()));
        List<MobileOffer> result = (List<MobileOffer>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    @Override
    public List<MobileOffer> findRetailWideExpiredInProperties(List<Property> properties) {
        Query query = em.createQuery("SELECT mo FROM MobileOffer mo"
                + " WHERE mo.retailOnly = :retailOnly"
                + " AND mo.retailer.id IN :retIds"
                + " AND mo.deleted = :deleted"
                + " AND mo.endDate < :date"
                + " ORDER BY mo.endDate, mo.lastModified");

        List<Long> retIds = getRetailerIds(properties);
        query.setParameter("retailOnly", true);
        query.setParameter("retIds", retIds);
        query.setParameter("date", DateUtil.getEndOfDay(new Date()));;
        query.setParameter("deleted", false);
        List<MobileOffer> result = (List<MobileOffer>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    @Override
    public List<MobileOffer> findRetailWideNotExpiredInProperties(List<Property> properties) {
        Query query = em.createQuery("SELECT mo FROM MobileOffer mo"
                + " WHERE mo.retailOnly = :retailOnly"
                + " AND mo.retailer.id IN :retIds"
                + " AND mo.deleted = :deleted"
                + " AND mo.endDate >= :date"
                + " ORDER BY mo.endDate, mo.lastModified");

        List<Long> retIds = getRetailerIds(properties);
        query.setParameter("retailOnly", true);
        query.setParameter("retIds", retIds);
        query.setParameter("date", DateUtil.getEndOfDay(new Date()));;
        query.setParameter("deleted", false);
        List<MobileOffer> result = (List<MobileOffer>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    @Override
    public List<MobileOffer> findRetailWidePendingApprovalInProperties(List<Property> properties) {
        Query query = em.createQuery("SELECT mo FROM MobileOffer mo"
                + " WHERE mo.retailOnly = :retailOnly"
                + " AND mo.retailer.id IN :retIds"
                + " AND mo.deleted = :deleted"
                + " AND mo.status = :pending"
                + " ORDER BY mo.endDate, mo.lastModified");

        List<Long> retIds = getRetailerIds(properties);
        query.setParameter("retailOnly", true);
        query.setParameter("retIds", retIds);
        query.setParameter("deleted", false);
        query.setParameter("pending", MobileOffer.PENDING);
        List<MobileOffer> result = (List<MobileOffer>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    @Override
    public List<MobileOffer> findRetailWideNotDeletedInProperties(List<Property> properties) {
        Query query = em.createQuery("SELECT mo FROM MobileOffer mo"
                + " WHERE mo.retailOnly = :retailOnly"
                + " AND mo.retailer.id IN :retIds"
                + " AND mo.deleted = :deleted"
                + " ORDER BY mo.endDate, mo.lastModified");

        List<Long> retIds = getRetailerIds(properties);
        query.setParameter("retailOnly", true);
        query.setParameter("retIds", retIds);
        query.setParameter("deleted", false);
        List<MobileOffer> result = (List<MobileOffer>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    @Override
    public List<MobileOffer> findActiveAndApprovedByBrand(Brand brand) {
        Query query = em.createQuery("SELECT mo FROM MobileOffer mo"
                + " WHERE mo.brand = :brand"
                + " AND mo.status = :status"
                + " AND mo.deleted = :deleted"
                + " AND :date BETWEEN mo.startDate and mo.endDate");
        query.setParameter("brand", brand);
        query.setParameter("status", MobileOffer.APPROVED);
        query.setParameter("deleted", false);
        query.setParameter("date", DateUtil.getStartOfDay(new Date()));
        List<MobileOffer> result = (List<MobileOffer>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }

    private List<Long> getRetailerIds(List<Property> properties) {
        if (properties == null) {
            return new ArrayList<Long>();
        }
        List<Retailer> uniqueRetailers = new ArrayList<Retailer>();
        for (Property p : properties) {
            uniqueRetailers.addAll(p.getRetailers());
        }
        HashSet hs = new HashSet();
        hs.addAll(uniqueRetailers);
        uniqueRetailers.clear();
        uniqueRetailers.addAll(hs);
        List<Long> retIds = new ArrayList<Long>();
        for (Retailer r : uniqueRetailers) {
            retIds.add(r.getId());
        }
        return retIds;
    }

    @Override
    public List<MobileOffer> findByCompanyExpiringAtDate(Company company, Date date) {
        Query query = em.createQuery("SELECT mo FROM MobileOffer mo"
                + " WHERE mo.company = :company"
                + " AND mo.endDate = :date"
                + " AND mo.deleted = :deleted"
                + " ORDER BY mo.endDate, mo.lastModified");
        query.setParameter("company", company);
        query.setParameter("date", date);// TRUNCATES THE TIMESTAMP 
        query.setParameter("deleted", false);
        List<MobileOffer> result = (List<MobileOffer>) query.getResultList();
        if (result.size() > 0) {
            return result;
        }
        return null; // none
    }
}
