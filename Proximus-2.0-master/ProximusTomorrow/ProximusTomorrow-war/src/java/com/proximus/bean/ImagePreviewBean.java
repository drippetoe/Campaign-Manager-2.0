/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.activation.MimetypesFileTypeMap;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * http://vineetreynolds.blogspot.com/2011/07/building-dynamic-image-based-carousel.html
 *
 * @author eric
 */
@ManagedBean
@RequestScoped
public class ImagePreviewBean
{
    private StreamedContent defaultFileContent;
    private StreamedContent fileContent;
    private String currFilePath = null;

    public void closeContent()
    {
        System.out.println("Closing FileContent for: " + currFilePath);
        System.out.println("FileContent is: " + fileContent);
        if (fileContent == null) {
            currFilePath = null;
            return;
        }
        if (fileContent != null && fileContent.getStream() != null) {
            try {
                fileContent.getStream().close();
                currFilePath = null;
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }
    
    public StreamedContent previewMyImage(String fullpath) {
        if (fullpath == null || fullpath.isEmpty()) {
            currFilePath = null;
            return defaultFileContent;
        }

        System.out.println("Full Path is Not Null");


        try {
            if (fullpath.equals(currFilePath)) {
                return fileContent;
            } else {
                currFilePath = fullpath;
            }

            File image = new File(fullpath);
            if (image != null && image.exists()) {
                closeContent();
                FileInputStream stream = new FileInputStream(image);
                fileContent = new DefaultStreamedContent(stream, new MimetypesFileTypeMap().getContentType(image));
                if (fileContent == null) {
                    currFilePath = null;
                    return defaultFileContent;
                }
            }
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
            currFilePath = null;
            return defaultFileContent;
        }
        return fileContent;
    }
    

    public StreamedContent getImageFileContent()
    {
        String fullpath = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("imageFile");
        System.out.println("The Full Path trying to getImage From is: " + fullpath);
        if (fullpath == null || fullpath.isEmpty()) {
            currFilePath = null;
            return defaultFileContent;
        }

        System.out.println("Full Path is Not Null");


        try {
            if (fullpath.equals(currFilePath)) {
                return fileContent;
            } else {
                currFilePath = fullpath;
            }

            File image = new File(fullpath);
            if (image != null && image.exists()) {
                closeContent();
                FileInputStream stream = new FileInputStream(image);
                fileContent = new DefaultStreamedContent(stream, new MimetypesFileTypeMap().getContentType(image));
                if (fileContent == null) {
                    currFilePath = null;
                    return defaultFileContent;
                }
            }
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
            currFilePath = null;
            return defaultFileContent;
        }
        return fileContent;
    }

    public void setFileContent(StreamedContent fileContent)
    {
        this.fileContent = fileContent;
    }

    public StreamedContent getDefaultFileContent()
    {

        if (defaultFileContent == null) {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            InputStream inputStream = contextClassLoader.getResourceAsStream("/Proximus-logo-sm.png");
            defaultFileContent = new DefaultStreamedContent(inputStream, "image/png");
        }
        return defaultFileContent;
    }

    public void setDefaultFileContent(StreamedContent defaultFileContent)
    {
        this.defaultFileContent = defaultFileContent;
    }
}
