/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author ronald
 */
@Entity
@DiscriminatorValue("GENERIC")
@Table(name = "privilege")
public class Privilege implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;
    @ManyToMany
    @JoinTable(name = "privilege_role", joinColumns =
    @JoinColumn(name = "privilege_id", referencedColumnName = "id"), inverseJoinColumns =
    @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles;
    @Size(max = 255)
    @Basic(optional = false)
    @NotNull
    @Column(name = "privilege", unique = true)
    private String privilegeName;

    public Privilege()
    {
        id = 0L;
        roles = new ArrayList<Role>();

    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getPrivilegeName()
    {
        return privilegeName;
    }

    public void setPrivilegeName(String privilegeName)
    {
        this.privilegeName = privilegeName;
    }

    public List<Role> getRoles()
    {
        return roles;
    }

    public void setRoles(List<Role> role)
    {
        this.roles = role;
    }

    public void addRole(Role role)
    {
        this.roles.add(role);
    }

    public void dropRole(Role toDrop)
    {
        this.roles.remove(toDrop);
    }

    @Override
    public String toString()
    {
        return this.privilegeName;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Privilege other = (Privilege) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            if(!this.privilegeName.equals(other.privilegeName)) {
                return false; 
            }
        }
        return true;
    }
}
