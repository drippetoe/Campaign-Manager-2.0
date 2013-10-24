/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice.response;

import java.util.List;
import net.aixum.webservice.Region;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class RegionsResponseModel extends BaseResponseModel {

    private List<Region> regions;

    public RegionsResponseModel(List<Region> regions) {
        this.regions = regions;
    }

    public RegionsResponseModel(List<Region> regions, boolean success, String message) {
        super(success, message);
        this.regions = regions;
    }

    public List<Region> getRegions() {
        return regions;
    }

    public void setRegions(List<Region> regions) {
        this.regions = regions;
    }
}
