/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import javax.activation.MimetypesFileTypeMap;
import org.apache.commons.io.FileUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author eric
 */
public class ImageFile implements Serializable
{
    private static DecimalFormat df = new DecimalFormat("#.##");
    private String name;
    private String location;
    private long size;
    private String description;
    private String mime;
    private StreamedContent content;

    public ImageFile()
    {
    }

    public ImageFile(String name, String location, long size, String description, String mime)
    {
        this.name = name;
        this.location = location;
        this.size = size;
        this.description = description;
        this.mime = mime;
    }

    public ImageFile(File image, String description)
    {
        this.name = image.getName();
        this.location = image.getAbsolutePath();
        long sz = FileUtils.sizeOf(image);
        this.size = sz;
        this.description = description;
        this.mime = new MimetypesFileTypeMap().getContentType(image);
    }

    public String getDescription()
    {
        if (description == null) {
            return this.name;
        }
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getMime()
    {
        return mime;
    }

    public void setMime(String mime)
    {
        this.mime = mime;
    }

    public long getSize()
    {
        return size;
    }

    public String getFriendlySize()
    {
        return df.format(this.getSize() / 1024) + "Kb";
    }

    public void setSize(long size)
    {
        this.size = size;
    }

    public void closeContent()
    {
        System.out.println("FileContent is: " + content);
        if (content == null) {
            return;
        }
        if (content != null && content.getStream() != null) {
            try {
                content.getStream().close();
                content = null;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public StreamedContent previewImage()
    {

        if (content != null) {
            try {
                content.getStream().close();
                content = null;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else if (location == null || location.isEmpty()) {
            return null;
        }

        try {
            File image = new File(location);
            if (image != null && image.exists()) {

                FileInputStream stream = new FileInputStream(image);
                content = new DefaultStreamedContent(stream, new MimetypesFileTypeMap().getContentType(image));
                if (content == null) {
                    return null;
                }
                return content;
            }
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
            return null;
        }
        return null;
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
        final ImageFile other = (ImageFile) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.location == null) ? (other.location != null) : !this.location.equals(other.location)) {
            return false;
        }
        if (this.size != other.size) {
            return false;
        }
        if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
            return false;
        }
        if ((this.mime == null) ? (other.mime != null) : !this.mime.equals(other.mime)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 79 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 79 * hash + (this.location != null ? this.location.hashCode() : 0);
        hash = 79 * hash + (int) (this.size ^ (this.size >>> 32));
        hash = 79 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 79 * hash + (this.mime != null ? this.mime.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString()
    {
        return "ImageFile{" + "name=" + name + ", location=" + location + ", size=" + size + ", description=" + description + ", mime=" + mime + '}';
    }
}
