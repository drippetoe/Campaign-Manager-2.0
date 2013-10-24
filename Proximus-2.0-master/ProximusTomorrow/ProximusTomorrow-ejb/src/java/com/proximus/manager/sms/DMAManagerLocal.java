/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager.sms;

import com.proximus.data.sms.DMA;
import com.proximus.data.sms.Property;
import com.proximus.manager.AbstractManagerInterface;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author ronald
 */
@Local
public interface DMAManagerLocal extends AbstractManagerInterface<DMA> {

    public DMA getDMAByName(String name);

    public DMA getDMAsByProperty(Property property);

    public List<DMA> getAllSorted();

    public List<DMA> getDmaFromKeyword();
}
