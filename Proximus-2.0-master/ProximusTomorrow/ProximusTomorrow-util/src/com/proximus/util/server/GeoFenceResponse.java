/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */

package com.proximus.util.server;

import com.proximus.data.sms.GeoFence;
import org.primefaces.model.map.LatLng;

/**
 *
 * @author ronald
 */
public class GeoFenceResponse{
        private boolean isWithinFence;
        private GeoFence geoFence;
        private double distance;
        private LatLng point;

        public GeoFenceResponse() {
        }

        public GeoFence getGeoFence() {
            return geoFence;
        }

        public void setGeoFence(GeoFence geoFence) {
            this.geoFence = geoFence;
        }

        public boolean isIsWithinFence() {
            return isWithinFence;
        }

        public void setIsWithinFence(boolean isWithinFence) {
            this.isWithinFence = isWithinFence;
        }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public LatLng getPoint() {
        return point;
    }

    public void setPoint(LatLng point) {
        this.point = point;
    }
        
    }
    