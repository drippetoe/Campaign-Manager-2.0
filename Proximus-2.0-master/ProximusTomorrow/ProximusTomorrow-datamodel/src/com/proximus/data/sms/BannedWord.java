/*
 * Copyright 2010-2012, Proximus Mobility, LLC.
 */
package com.proximus.data.sms;

import com.proximus.data.Company;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author eric
 */
@Entity
@Table(name = "banned_word")
public class BannedWord implements Serializable{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;
    @Size(max = 100)
    @Column(name = "word")
    private String word;    
    @ManyToOne
    private Company company;

    public BannedWord() {
        this.id = 0L;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word.toUpperCase();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return this.word.equalsIgnoreCase(obj.toString());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 47 * hash + (this.word != null ? this.word.hashCode() : 0);
        hash = 47 * hash + (this.company != null ? this.company.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return this.word;
    }
}
