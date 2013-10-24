/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.Company;
import com.proximus.data.Role;
import com.proximus.data.User;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author ronald
 */
@Local
public interface UserManagerLocal extends AbstractManagerInterface<User> {

    public User getUserByUsername(String userName);

    public List<User> getUsersByRole(Company c, Role r);

    public List<User> getUsersByCompany(Company co);

    public List<Company> getCompaniesByUser(User u);

    public List<String> getCompanyNamesByUser(String u);

    public List<User> findUserLike(String keyword);

    public List<Company> getCompaniesFromUser(Long id);

    public List<User> getSuperUsers();

    public java.util.List<com.proximus.data.Company> getCompaniesFromUserAndLicense(com.proximus.data.User user, java.lang.String license);
}