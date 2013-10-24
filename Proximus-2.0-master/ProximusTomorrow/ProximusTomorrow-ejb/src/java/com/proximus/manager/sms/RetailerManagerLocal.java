/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager.sms;

import com.proximus.data.Brand;
import com.proximus.data.sms.Property;
import com.proximus.data.sms.Retailer;
import com.proximus.manager.AbstractManagerInterface;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author ronald
 */
@Local
public interface RetailerManagerLocal extends AbstractManagerInterface<Retailer> {

    public List<Retailer> getAllRetailersByBrand(Brand b);

    public Retailer getRetailerByName(Brand b, String name);

    public List<Retailer> getRetailersByProperty(Property property);
}
