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
@ManagedBean(name = "wizardControllerTemplateTwo")
@SessionScoped
public class WizardControllerTemplateTwo extends WizardController {

    private String titleOne;
    private String bodyOne;
    private String titleTwo;
    private String bodyTwo;
    private String[] selectedImages;
    private ImageFile[] selectedImageFiles;

    public WizardControllerTemplateTwo() {
        prepareCreate();
    }

    @Override
    public void prepareVars() {
        //Before setting up everything lets delete folders if they exist
        if (serverPath != null) {
            try {
                //Close ImageFile previews if open
                for (ImageFile file : this.selectedImageFiles) {
                    if(file != null) {
                        file.closeContent();
                    }
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
        titleOne = "";
        bodyOne = "";
        titleTwo = "";
        bodyTwo = "";
        facebookUrl = "";
        twitterUrl = "";
        facebookOn = false;
        twitterOn = false;
        progress = 0;
        zipFile = null;
        selectedImages = new String[2];
        selectedImageFiles = new ImageFile[2];
    }

    @Override
    public String prepareCreate() {
        prepareVars();
        return "/contentgenerator/WizardTwo";
    }

    /**
     * handle File Uploading depending on which Image is being upload (the
     * index)
     *
     * @param event
     * @param index
     */
    private void handleImageUploadByIndex(FileUploadEvent event, int index) {
        File imagePathDir = new File(imagePath);
        imagePathDir.mkdirs();
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
        InputStream inputStream;
        File f;
        try {
            inputStream = event.getFile().getInputstream();
            f = new File(imagePath + filename);
            FileUtils.copyInputStreamToFile(inputStream, f);
        } catch (IOException ex) {
            JsfUtil.addErrorMessage("Unable to upload file");
            return;
        }
        //Handle case when an Image already existed for the 2nd page
        if (selectedImageFiles[index] != null) {
            selectedImageFiles[index].closeContent();
            //delete this image befor copying the current one
            File[] files = imagePathDir.listFiles();
            for (File file : files) {
                if (file.getName().equals(selectedImageFiles[index].getName())) {
                    FileUtils.deleteQuietly(file);
                    break;
                }
            }
        }

        //Either base case or special case (removing old image) we just set up the selectedImageFile and name
        this.selectedImageFiles[index] = new ImageFile(f, "image: " + (index + 1));
        this.selectedImages[index] = filename;
    }

    public void handleFileUploadOne(FileUploadEvent event) {
        this.handleImageUploadByIndex(event, 0);
    }

    public void handleFileUploadTwo(FileUploadEvent event) {
        this.handleImageUploadByIndex(event, 1);
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
        if (!titleTwo.isEmpty()) {
            return true;
        }
        if (!bodyTwo.isEmpty()) {
            return true;
        }

        return false;
    }

    public void previewContent() {
        writeTemplate();
    }

    @Override
    public void writeTemplate() {
        try {
            progress = 5;
            new File(serverPath).mkdirs();
            new File(imagePath).mkdirs();
            progress = 10;
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(serverPath + "index.html")));
            BufferedReader reader = new BufferedReader(new InputStreamReader(WizardControllerTemplateTwo.class.getResourceAsStream("/resources/contentgenerator/templates/two/index.html")));
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
                progress++;
                content += temp;
                content += "\n";
            }
            content = content.replace("##TITLE##", this.title);
            content = content.replace("##OFFER1TITLE##", this.titleOne);
            content = content.replace("##OFFER2TITLE##", this.titleTwo);
            content = content.replace("##FOOTER##", this.footer);
            content = content.replace("##FACEBOOK##", (this.facebookOn ? this.facebookUrl : "#"));
            content = content.replace("##TWITTER##", (this.twitterOn ? this.twitterUrl : "#"));
            writer.write(content);
            writer.close();
            reader.close();

            writeOfferOne();
            writeOfferTwo();
            progress = 85;
            //Now move the template content to this folder
            FileUtils.copyInputStreamToFile(WizardControllerTemplateTwo.class.getResourceAsStream("/resources/contentgenerator/templates/two/style.css"), new File(serverPath, "style.css"));
            FileUtils.copyInputStreamToFile(WizardControllerTemplateTwo.class.getResourceAsStream("/resources/contentgenerator/templates/images/facebook.png"), new File(imagePath, "facebook.png"));
            FileUtils.copyInputStreamToFile(WizardControllerTemplateTwo.class.getResourceAsStream("/resources/contentgenerator/templates/images/twitter.png"), new File(imagePath, "twitter.png"));
            FileUtils.copyInputStreamToFile(WizardControllerTemplateTwo.class.getResourceAsStream("/resources/contentgenerator/templates/images/home.png"), new File(imagePath, "home.png"));

            //Now Copy the Embedded template
            File ignoreDir = new File(serverPath + "_ignore");
            ignoreDir.mkdirs();

            File previewCss = new File(ignoreDir, "preview.css");
            File phone = new File(ignoreDir, "phone.html");
            File phoneImg = new File(ignoreDir, "preview.png");
            FileUtils.copyInputStreamToFile(WizardControllerTemplateTwo.class.getResourceAsStream("/resources/contentgenerator/templates/embed/preview.css"), previewCss);
            FileUtils.copyInputStreamToFile(WizardControllerTemplateTwo.class.getResourceAsStream("/resources/contentgenerator/templates/embed/phone.html"), phone);
            FileUtils.copyInputStreamToFile(WizardControllerTemplateTwo.class.getResourceAsStream("/resources/contentgenerator/templates/embed/preview.png"), phoneImg);

            progress = 100;
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            progress = 100;

        }
    }

    private void writeOfferOne() throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(serverPath + "offer1.html")));
        BufferedReader reader = new BufferedReader(new InputStreamReader(WizardControllerTemplateTwo.class.getResourceAsStream("/resources/contentgenerator/templates/two/offer1.html")));
        String temp;
        String content = "";
        progress = 45;
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
            progress++;
            content += temp;
            content += "\n";
        }
        content = content.replace("##OFFER1TITLE##", this.titleOne);
        content = content.replace("##OFFER1CONTENT##", this.bodyOne);
        content = content.replace("##IMAGEONE##", "images/" + this.selectedImages[0]);
        content = content.replace("##FOOTER##", this.footer);
        content = content.replace("##FACEBOOK##", (this.facebookOn ? this.facebookUrl : "#"));
        content = content.replace("##TWITTER##", (this.twitterOn ? this.twitterUrl : "#"));
        writer.write(content);
        writer.close();
        reader.close();
        progress = 60;
    }

    private void writeOfferTwo() throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(serverPath + "offer2.html")));
        BufferedReader reader = new BufferedReader(new InputStreamReader(WizardControllerTemplateTwo.class.getResourceAsStream("/resources/contentgenerator/templates/two/offer2.html")));
        String temp;
        String content = "";
        progress = 70;
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
            progress++;
            content += temp;
            content += "\n";
        }
        content = content.replace("##OFFER2TITLE##", this.titleTwo);
        content = content.replace("##OFFER2CONTENT##", this.bodyTwo);
        content = content.replace("##IMAGETWO##", "images/" + this.selectedImages[1]);
        content = content.replace("##FOOTER##", this.footer);
        content = content.replace("##FACEBOOK##", (this.facebookOn ? this.facebookUrl : "#"));
        content = content.replace("##TWITTER##", (this.twitterOn ? this.twitterUrl : "#"));
        writer.write(content);
        writer.close();
        reader.close();
        progress = 80;
    }

    public String getBodyOne() {
        return bodyOne;
    }

    public void setBodyOne(String bodyOne) {
        this.bodyOne = bodyOne;
    }

    public String getTitleOne() {
        return titleOne;
    }

    public void setTitleOne(String titleOne) {
        this.titleOne = titleOne;
    }

    public String getBodyTwo() {
        return bodyTwo;
    }

    public void setBodyTwo(String bodyTwo) {
        this.bodyTwo = bodyTwo;
    }

    public String getTitleTwo() {
        return titleTwo;
    }

    public void setTitleTwo(String titleTwo) {
        this.titleTwo = titleTwo;
    }

    public ImageFile getFirstImageFile() {
        return selectedImageFiles[0];
    }

    public ImageFile getSecondImageFile() {
        return selectedImageFiles[1];
    }

    public String getFirstImageName() {
        return selectedImages[0];
    }

    public String getSecondImageName() {
        return selectedImages[1];
    }

    @Override
    public void handleFileUpload(FileUploadEvent event) {
        this.handleFileUploadOne(event);
    }
}
