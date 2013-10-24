/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.sms;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 *
 * @author ronald
 */
@Entity
@Table(name = "short_code")
public class ShortCode implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;
    @Column(name = "short_code")
    private Integer shortCode;
    @Column(name = "kaze_short_code_id")
    private String kazeShortCodeId;


    public ShortCode() {
        this.id = 0L;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getShortCode() {
        return shortCode;
    }

    public void setShortCode(Integer shortCode) {
        this.shortCode = shortCode;
    }

    public String getKazeShortCodeId() {
        return kazeShortCodeId;
    }

    public void setKazeShortCodeId(String kazeShortCodeId) {
        this.kazeShortCodeId = kazeShortCodeId;
    }
}
