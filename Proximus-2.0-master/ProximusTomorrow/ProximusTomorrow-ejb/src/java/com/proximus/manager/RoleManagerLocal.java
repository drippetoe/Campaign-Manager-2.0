/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.License;
import com.proximus.data.Role;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author ronald
 */
@Local
public interface RoleManagerLocal extends AbstractManagerInterface<Role> {

    public Role getUserByName(String roleName);

    public List<String> getRoleNames();

    public Role findByName(String name);
    
    public List<Role> findByLicense(License license);
}
