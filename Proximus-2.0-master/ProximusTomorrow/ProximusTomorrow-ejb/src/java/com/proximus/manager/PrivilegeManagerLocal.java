/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */

package com.proximus.manager;

import com.proximus.data.Privilege;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author ronald
 */
@Local
public interface PrivilegeManagerLocal extends AbstractManagerInterface<Privilege> {
  public List<Privilege> findPrivilegesLike(String keyword);
}
