/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf;

import com.proximus.bean.ImageFile;
import com.proximus.helper.util.JsfUtil;
import com.proximus.util.ServerURISettings;
import java.io.*;
import java.util.UUID;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.apache.commons.io.FileUtils;
import org.primefaces.event.FileUploadEvent;

/**
 *
 * @author Gilberto Gaxiola
 */
@ManagedBean(name = "wizardControllerTemplateOne")
@SessionScoped
public class WizardControllerTemplateOne extends WizardController {

    private String selectedImage;
    private ImageFile imageFile;

    public WizardControllerTemplateOne() {
        prepareCreate();
    }

    @Override
    public void prepareVars() {

        //Before setting up everything lets delete folders if they exist
        if (serverPath != null) {
            try {
                if (imageFile != null) {
                    imageFile.closeContent();
                }
                FileUtils.deleteDirectory(new File(serverPath));
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
        this.userUUIDPreview = this.getUserIdFromSession() + "_" + UUID.randomUUID().toString();
        serverPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("//tmp") + ServerURISettings.OS_SEP + this.userUUIDPreview + ServerURISettings.OS_SEP;
        imagePath = serverPath + "images" + ServerURISettings.OS_SEP;

        title = "";
        body = "";
        footer = "";
        selectedImage = "";
        facebookUrl = "";
        twitterUrl = "";
        facebookOn = false;
        twitterOn = false;
        imageFile = null;
        progress = 0;
        zipFile = null;
    }

    @Override
    public String prepareCreate() {
        prepareVars();
        return "/contentgenerator/WizardOne";
    }



    public String getSelectedImage() {
        return selectedImage;
    }

    public void setSelectedImage(String selectedImage) {
        this.selectedImage = selectedImage;
    }

    @Override
    public void handleFileUpload(FileUploadEvent event) {
        try {
            if (imageFile != null) {
                imageFile.closeContent();
            }
            File imagePathDir = new File(imagePath);
            imagePathDir.mkdirs();
            //clear other images before copying the this one
            if (imagePathDir.isDirectory()) {
                File[] files = imagePathDir.listFiles();
                for (File file : files) {
                    FileUtils.deleteQuietly(file);
                }
            }
            String filename = event.getFile().getFileName().replaceAll(" ", "_");

            String tocheck = filename.toLowerCase();
            if (!tocheck.endsWith("jpg") && !tocheck.endsWith("jpeg") && !tocheck.endsWith("gif") && !tocheck.endsWith("png")) {
                JsfUtil.addErrorMessage("Invalid File type (only supports jpg, gif and png)");
                return;
            }

            if (event.getFile().getSize() >= MEGABYTE) {
                JsfUtil.addErrorMessage("Invalid File size (image must be less than 1 MB)");
                return;
            }


            InputStream inputStream = event.getFile().getInputstream();
            File f = new File(imagePath + filename);
            FileUtils.copyInputStreamToFile(inputStream, f);
            this.selectedImage = filename;
            if (imageFile != null) {
                imageFile.closeContent();
            }
            imageFile = new ImageFile(f, "");
        } catch (IOException ex) {
            JsfUtil.addErrorMessage("Unable to upload file");
            return;

        }
    }

    public boolean doWeHaveData() {
        if (!title.isEmpty()) {
            return true;
        }
        if (!body.isEmpty()) {
            return true;
        }
        if (!facebookUrl.isEmpty()) {
            return true;
        }
        if (!twitterUrl.isEmpty()) {
            return true;
        }

        if (imageFile != null) {
            return true;
        }

        return false;
    }

    @Override
    public void writeTemplate() {
        try {
            
            new File(serverPath).mkdirs();
            new File(imagePath).mkdirs();


            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(serverPath + "index.html")));
            BufferedReader reader = new BufferedReader(new InputStreamReader(WizardControllerTemplateOne.class.getResourceAsStream("/resources/contentgenerator/templates/one/index.html")));
            String temp;
            String content = "";
            while ((temp = reader.readLine()) != null) {
                if (temp.contains("##HASFACEBOOK##")) {
                    if (this.facebookOn) {
                        temp = temp.replace("##HASFACEBOOK##", "");
                    } else {
                        continue;
                    }
                } else if (temp.contains("##HASTWITTER##")) {
                    if (this.twitterOn) {
                        temp = temp.replace("##HASTWITTER##", "");
                    } else {
                        continue;
                    }
                }

                content += temp;
                content += "\n";
            }
            content = content.replace("##TITLE##", this.title);
            content = content.replace("##BODY##", this.body);
            content = content.replace("##FOOTER##", this.footer);
            content = content.replace("##IMAGE##", "images/" + this.selectedImage);
            content = content.replace("##FACEBOOK##", (this.facebookOn ? this.facebookUrl : "#"));
            content = content.replace("##TWITTER##", (this.twitterOn ? this.twitterUrl : "#"));
            writer.write(content);
            writer.close();
            reader.close();

            //Now move the template content to this folder
            FileUtils.copyInputStreamToFile(WizardControllerTemplateOne.class.getResourceAsStream("/resources/contentgenerator/templates/one/style.css"), new File(serverPath, "style.css"));
            FileUtils.copyInputStreamToFile(WizardControllerTemplateOne.class.getResourceAsStream("/resources/contentgenerator/templates/images/facebook.png"), new File(serverPath, "facebook.png"));
            FileUtils.copyInputStreamToFile(WizardControllerTemplateOne.class.getResourceAsStream("/resources/contentgenerator/templates/images/twitter.png"), new File(serverPath, "twitter.png"));

            //Now Copy the Embedded template
            File ignoreDir = new File(serverPath + "_ignore");
            ignoreDir.mkdirs();

            File previewCss = new File(ignoreDir, "preview.css");
            File phone = new File(ignoreDir, "phone.html");
            File phoneImg = new File(ignoreDir, "preview.png");
            FileUtils.copyInputStreamToFile(WizardControllerTemplateOne.class.getResourceAsStream("/resources/contentgenerator/templates/embed/preview.css"), previewCss);
            FileUtils.copyInputStreamToFile(WizardControllerTemplateOne.class.getResourceAsStream("/resources/contentgenerator/templates/embed/phone.html"), phone);
            FileUtils.copyInputStreamToFile(WizardControllerTemplateOne.class.getResourceAsStream("/resources/contentgenerator/templates/embed/preview.png"), phoneImg);
            progress = 100;
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            progress = 100;
        }

    }

    public void previewContent() {
        writeTemplate();
    }

    public ImageFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(ImageFile imageFile) {
        this.imageFile = imageFile;
    }
}
