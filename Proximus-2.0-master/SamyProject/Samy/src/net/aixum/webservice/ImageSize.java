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
@DatabaseTable(tableName = "imageSize")
public class ImageSize {
	@DatabaseField(generatedId = true)
    private transient int id; //ORM only transient=GSON Transient
	
	@DatabaseField(columnName="width")
	@SerializedName("Width")
    private int width;
	@DatabaseField(columnName="height")
	@SerializedName("Height")
    private int height;

    public ImageSize(){
    	
    }
    public ImageSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
