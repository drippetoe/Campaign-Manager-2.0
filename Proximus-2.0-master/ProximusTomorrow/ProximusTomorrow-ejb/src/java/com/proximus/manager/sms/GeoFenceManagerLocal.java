package com.proximus.manager.sms;

import com.proximus.data.Brand;
import com.proximus.data.Company;
import com.proximus.data.sms.Country;
import com.proximus.data.sms.GeoFence;
import com.proximus.data.sms.Property;
import com.proximus.manager.AbstractManagerInterface;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author eric
 */
@Local
public interface GeoFenceManagerLocal extends AbstractManagerInterface<GeoFence> {

    public GeoFence findByName(String name, Property p);

    public List<String> getGeoFenceNamesByProperty(Property p);

    public List<GeoFence> findAllByProperty(Property property);

    public List<GeoFence> getGeoFencesByCompany(Company company);

    public List<GeoFence> getGeoFencesByCompanyAndPriority(Company company, Long priority);

    public Property getPropertyFromGeoFence(Long geoFenceId);

    public List<GeoFence> getGeoFencesByCompanyAndCountry(Company company, Country country);
    
    public List<GeoFence> getGeoFencesByBrand(Brand brand);
}
