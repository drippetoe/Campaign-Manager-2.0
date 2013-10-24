/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.icap.server;

import java.math.BigDecimal;

/**
 *
 * @author Gilberto Gaxiola
 */
public class Item {
    
    
    private long id;
    private String name;
    private String description;
    private String brand;
    private BigDecimal price;
    
    
    
    public Item() {
     
    }
    
    public Item(String jsonStyle) {
        String regex = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
        String[] split = jsonStyle.split(regex);
        for (String s : split) {
            String[] pairs = s.split(":");
            pairs[0] = pairs[0].replaceAll("\"", "").trim();
            pairs[1] = pairs[1].replaceAll("\"", "").trim();
            
            //System.out.println("Key: " + pairs[0] + " Value: " + pairs[1]);
            if(pairs[0].contains("id")) {
                id = Long.valueOf(pairs[1]);
            } else if(pairs[0].contains("price")) {
                price = new BigDecimal(pairs[1]);
            } else if(pairs[0].contains("desc")) {
                description = pairs[1];
            } else if(pairs[0].contains("name")) {
                name = pairs[1];
            } else if(pairs[0].contains("brand")) {
                brand = pairs[1];
            }
        }
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    
    @Override
    public String toString() {
        return "Id: " + id + "\nName: " + name + "\nDescription: " + description + "\nBrand: " + brand + "\nPrice: " + price;
        
    }
    

}
