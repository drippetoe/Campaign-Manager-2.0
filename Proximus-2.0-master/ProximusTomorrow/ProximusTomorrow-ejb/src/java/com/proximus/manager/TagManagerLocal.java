/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.Company;
import com.proximus.data.Tag;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author ronald
 */
@Local
public interface TagManagerLocal extends AbstractManagerInterface<Tag> {

    public Tag findByName(String name, Company c);
    
    public List<String> getTagNamesByCompany(Company c);
    
    public List<Tag> findAllByCompany(Company c);
}
