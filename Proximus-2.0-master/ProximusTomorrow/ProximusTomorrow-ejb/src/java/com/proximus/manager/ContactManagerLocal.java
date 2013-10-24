/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager;

import com.proximus.data.Company;
import com.proximus.data.Contact;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Gilberto Gaxiola
 */
@Local
public interface ContactManagerLocal extends AbstractManagerInterface<Contact> {

    public Contact getContactByEmail(String email, Company c);

    public List<Contact> findContactLike(String keyword);

    public List<Contact> getContactByCompany(Company co);

    public List<String> getContactEmailByCompany(Company c);

    public List<Contact> findAllByCompany(Company c);
}
