/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.BluetoothDwell;
import com.proximus.data.Company;
import com.proximus.data.Device;
import com.proximus.data.report.BluetoothFileSendSummary;
import com.proximus.data.report.UserProfileDwell;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Gilberto Gaxiola
 */
@Stateless
public class BluetoothDwellManager extends AbstractManager<BluetoothDwell> implements BluetoothDwellManagerLocal {

    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public BluetoothDwellManager() {
        super(BluetoothDwell.class);
    }

    @Override
    public List<UserProfileDwell> getTotalDevicesSeen(Company company, Device device, Date startDate, Date endDate) {
        String queryString = "SELECT mac_address, friendly_name, count(id), SUM(dwell_time), AVG(dwell_time), MIN(event_date), MAX(event_date)";
        queryString += " FROM bluetooth_dwell";
        queryString += " WHERE DATE(event_date) BETWEEN ?1 AND ?2 AND company_id = ?3";
        if (device != null) {
            queryString += " AND device_id = ?4";
        }

        queryString += " GROUP BY mac_address";
        Query q = em.createNativeQuery(queryString);

        q.setParameter(1, startDate);
        q.setParameter(2, endDate);
        q.setParameter(3, company.getId());
        if (device != null) {
            q.setParameter(4, device.getId());
        }

        List<UserProfileDwell> results = new ArrayList<UserProfileDwell>();
        List<Object[]> result = q.getResultList();
        for (Object[] object : result) {
            String macAddress = (String) object[0];
            String friendlyName = (String) object[1];
            Long totalSessions = (Long) object[2];
            BigDecimal dwellTime = (BigDecimal) object[3];
            long totalDwellTime = dwellTime.longValue();
            BigDecimal avg = (BigDecimal) object[4];
            double averageDwellTime = avg.doubleValue();
            Date firstSeen = (Date) object[5];
            Date lastSeen = (Date) object[6];
            UserProfileDwell dwellRecord = new UserProfileDwell(macAddress, friendlyName, totalSessions, totalDwellTime, averageDwellTime, firstSeen, lastSeen);
            results.add(dwellRecord);
        }

        if (results.isEmpty()) {
            return null;
        }
        return results;

    }
}