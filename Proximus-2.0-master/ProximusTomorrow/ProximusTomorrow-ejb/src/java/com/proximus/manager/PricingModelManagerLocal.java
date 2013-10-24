/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager;

import com.proximus.data.PricingModel;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author ronald
 */
@Local
public interface PricingModelManagerLocal extends AbstractManagerInterface<PricingModel> {

    public PricingModel getPricingModelByName(String name);

    public List<PricingModel> findAllSortedPricingModels();
}
