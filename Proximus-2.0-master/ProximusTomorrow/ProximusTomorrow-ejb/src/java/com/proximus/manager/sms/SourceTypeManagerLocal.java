/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager.sms;

import com.proximus.data.sms.SourceType;
import com.proximus.manager.AbstractManagerInterface;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author ronald
 */
@Local
public interface SourceTypeManagerLocal extends AbstractManagerInterface<SourceType> {

    public SourceType getSourceTypeByName(String source);

    public List<SourceType> getAllSorted();
}
