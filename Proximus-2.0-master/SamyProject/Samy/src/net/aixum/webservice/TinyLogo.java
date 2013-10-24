/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
@DatabaseTable(tableName = "tinyLogo")
public class TinyLogo extends ImageSize {

	@DatabaseField(generatedId = true)
    private int id; //ORM only
	
	@DatabaseField(columnName="url")
    private String url;
	
	public TinyLogo(){
		
	}

    public TinyLogo(String url, int width, int height) {
        super(width, height);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
}
