/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf;

import com.proximus.util.ZipUnzip;
import java.io.*;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author Gilberto Gaxiola
 */
public abstract class WizardController extends AbstractController implements Serializable {

    protected final String zipContentName = "generatedContent.zip";
    
    protected String title;
    protected String body;
    protected String footer;
    
    protected int progress;
    protected String userUUIDPreview;
    protected String serverPath;
    protected File zipFile = null;
    protected String imagePath;
    protected boolean facebookOn = false;
    protected boolean twitterOn = false;
    protected String facebookUrl;
    protected String twitterUrl;
    protected final long MEGABYTE = 1024 * 1024;

    public abstract void prepareVars();

    public abstract String prepareCreate();

    public String cancelGoHome() {
        prepareVars();
        return "/home";
    }

    public abstract void handleFileUpload(FileUploadEvent event);

    public StreamedContent getContentZipFile(){
         try {

            writeTemplate();
            zipFile = createZipTemplate();

            if (zipFile != null) {
                InputStream stream = new FileInputStream(zipFile);
                return new DefaultStreamedContent(stream, "zip", zipFile.getName());
            } else {
                String str = "Unable to create content";
                InputStream stream = new ByteArrayInputStream(str.getBytes());
                return new DefaultStreamedContent(stream, "text/plain", "error.txt");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());

        }
        return null;
    }

    public abstract void writeTemplate();

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void cancel() {
        this.progress = 0;
    }

    public String getUserUUIDPreview() {
        return userUUIDPreview;
    }

    public String onFlowProcess(FlowEvent event) {
        progress = 0;
        return event.getNewStep();
    }

    protected File createZipTemplate() {
        try {
            ZipUnzip.ZipAllButIgnore(serverPath, zipContentName);
            return new File(serverPath + zipContentName);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        return null;

    }

    public String getZipFilePath() {
        if (zipFile == null) {
            zipFile = createZipTemplate();
        }
        if (zipFile == null) {
            return null;
        }
        return this.userUUIDPreview + "@" + zipContentName;
    }

    public String getFacebookUrl() {
        return facebookUrl;
    }

    public void setFacebookUrl(String facebookUrl) {
        if (facebookUrl != null && !facebookUrl.isEmpty()) {
            if (!facebookUrl.contains("facebook.com")) {
                this.facebookUrl = "http://facebook.com/" + facebookUrl;
            } else if (!facebookUrl.contains("http://") && !facebookUrl.contains("https://")) {
                this.facebookUrl = "http://" + facebookUrl;
            } else {
                this.facebookUrl = facebookUrl;
            }

            facebookOn = true;
        } else {
            facebookOn = false;
            this.facebookUrl = "";
        }

    }

    public String getTwitterUrl() {
        return twitterUrl;
    }

    public void setTwitterUrl(String twitterUrl) {
        if (twitterUrl != null && !twitterUrl.isEmpty()) {
            if (!twitterUrl.contains("twitter.com")) {
                this.twitterUrl = "http://twitter.com/" + twitterUrl;
            } else if (!twitterUrl.contains("http://") && !twitterUrl.contains("https://")) {
                this.twitterUrl = "http://" + twitterUrl;
            } else {
                this.twitterUrl = twitterUrl;
            }
            twitterOn = true;
        } else {
            twitterOn = false;
            this.twitterUrl = "";
        }
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
    
    
}
