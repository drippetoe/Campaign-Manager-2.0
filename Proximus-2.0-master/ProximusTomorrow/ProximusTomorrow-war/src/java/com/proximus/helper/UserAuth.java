/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.helper;

import com.proximus.data.Privilege;
import java.util.List;

/**
 *
 * @author Angela Mercer
 */
public class UserAuth
{
    public String myName;
    public Long myCompany_id;
    public List<Privilege> myPrivileges;

    public UserAuth(String myName, Long myCompany_id, List<Privilege> myPrivileges)
    {
        this.myName = myName;
        this.myCompany_id = myCompany_id;
        this.myPrivileges = myPrivileges;
    }
    
    public Long getMyCompany_id()
    {
        return myCompany_id;
    }

    public void setMyCompany_id(Long myCompany_id)
    {
        this.myCompany_id = myCompany_id;
    }

    public String getMyName()
    {
        return myName;
    }

    public void setMyName(String myName)
    {
        this.myName = myName;
    }

    public List<Privilege> getMyPrivileges()
    {
        return myPrivileges;
    }

    public void setMyPrivileges(List<Privilege> myPrivileges)
    {
        this.myPrivileges = myPrivileges;
    }
    
    
    
}
