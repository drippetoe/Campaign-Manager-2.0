/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms;

import com.proximus.data.Brand;
import com.proximus.data.sms.LocationData;
import com.proximus.data.sms.Subscriber;
import com.proximus.manager.AbstractManagerInterface;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Angela Mercer
 */
@Local
public interface LocationDataManagerLocal extends AbstractManagerInterface<LocationData> {

    public Long getTotalLookupsInDateRangeByBrand(Date startDate, Date endDate, Brand brand, String status);
    
    public Long getTotalLookupsInDateRangeByMsisdn(Date startDate, Date endDate, String msisdn);

    public Long getTotalSuccessfulLookupsInDateRangeByMsisdn(Date startDate, Date endDate, String msisdn);

    public Long getTotalLookupsByMsisdn(String msisdn);

    public Long getTotalFoundInDateRangeByBrand(Date startDate, Date endDate, Brand brand);

    public List<LocationData> getAllLocationDataInDateRange(Date startDate, Date endDate);

    public List<LocationData> findAllByBrand(Brand brand);

    public List<LocationData> findByBrandAndStatus(Brand brand, String status);

    public List<LocationData> findAllByBrandAndMsisdn(Brand brand, String msisdn);

    public List<LocationData> getLocationDataByBrandAndMsisdnInRange(Brand brand, String msisdn, Date startDate, Date endDate);

    public LocationData findLastBySubscriber(Subscriber subscriber);

    public Long getGeoFenceId(String msisdn);
    
    public Double getAverageDistanceFromGeoFence(Brand brand, Date startDate, Date endDate, String milesLimit);
}
