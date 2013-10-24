package com.proximus.manager.sms;

import com.proximus.data.sms.GeoFence;
import com.proximus.data.sms.GeoPoint;
import com.proximus.data.sms.Property;
import com.proximus.manager.AbstractManagerInterface;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author eric
 */
@Local
public interface GeoPointManagerLocal extends AbstractManagerInterface<GeoPoint> {

    public Double getLatitude(GeoFence geoFence);

    public Double getLongitude(GeoFence geoFence);

    public void createGeoPoints(List<GeoPoint> geoPoints);

    public void updateGeoPoints(List<GeoPoint> geoPoints);

    public Integer getRadius(GeoFence geoFence);

    public void deleteGeoPoints(List<GeoPoint> geoPoints);

    public GeoPoint findGeoPointByLocation(Double latitude, Double longitude);

    public GeoPoint findGeoPointByGeoFenceAndProperty(GeoFence geoFence, Property property);
}
