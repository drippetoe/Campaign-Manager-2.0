/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
@DatabaseTable(tableName = "HeaderBarLogo")
public class HeaderBarLogo extends ImageSize {
	@DatabaseField(generatedId = true)
    private transient int id; //ORM only
	
	@DatabaseField(columnName="url")
	@SerializedName("Url")
    private String url;

    public HeaderBarLogo(String url, int width, int height) {
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
