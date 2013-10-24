package com.proximus.jsf;

import com.proximus.data.SoftwareRelease;
import com.proximus.jsf.util.JsfUtil;
import com.proximus.manager.SoftwareReleaseManagerLocal;
import com.proximus.util.ServerURISettings;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.apache.commons.io.FileUtils;
import org.primefaces.event.FileUploadEvent;

@ManagedBean(name = "softwareReleaseController")
@SessionScoped
public class SoftwareReleaseController extends AbstractController implements Serializable {

    private SoftwareRelease softwareRelease;
    @EJB
    private SoftwareReleaseManagerLocal softwareFacade;
    private String tmpFolder;
    private String file = null;

    public String reinit() {
        file = null;

        return null;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public void removeFile() {
        File dir = new File(ServerURISettings.SOFTWARE_RELEASE_DIR + ServerURISettings.OS_SEP + tmpFolder);
        File f = new File(dir, this.file);
        try {
            FileUtils.forceDelete(f);
            FileUtils.deleteDirectory(dir);
        } catch (IOException ex) {
            JsfUtil.addErrorMessage(ex, "Unable to remove file:");
        }
        this.file = null;
    }

    public List getPlatforms() {
        return Arrays.asList(SoftwareRelease.PLATFORMS);
    }

    public SoftwareReleaseController() {
    }

    public SoftwareRelease getSoftwareRelease() {
        if (softwareRelease == null) {
            softwareRelease = new SoftwareRelease();
        }
        return softwareRelease;
    }

    public void setSoftwareRelease(SoftwareRelease softwareRelease) {
        this.softwareRelease = softwareRelease;
    }

    public String prepareCreate() {
        softwareRelease = new SoftwareRelease();
        file = null;
        tmpFolder = "tmp" + ServerURISettings.OS_SEP + getUserIdFromSession().toString();
        return "/software/Create";
    }

    public String create() {
        try {
            if (this.file != null) {
                File from = new File(ServerURISettings.SOFTWARE_RELEASE_DIR + ServerURISettings.OS_SEP + tmpFolder + ServerURISettings.OS_SEP + this.file);
                new File(ServerURISettings.SOFTWARE_RELEASE_DIR + ServerURISettings.OS_SEP + softwareRelease.getPlatform() + "-" + softwareRelease.getMajor() + "." + softwareRelease.getMinor() + "." + softwareRelease.getBuild()).mkdirs();
                File to = new File(ServerURISettings.SOFTWARE_RELEASE_DIR + ServerURISettings.OS_SEP + softwareRelease.getPlatform() + "-" + softwareRelease.getMajor() + "." + softwareRelease.getMinor() + "." + softwareRelease.getBuild() + ServerURISettings.OS_SEP + this.file);
                FileUtils.moveFile(from, to);
                FileUtils.deleteDirectory(new File(ServerURISettings.SOFTWARE_RELEASE_DIR + ServerURISettings.OS_SEP + tmpFolder));
                softwareRelease.setPath(to.getAbsolutePath());
                softwareFacade.create(softwareRelease);
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("SoftwareReleaseCreated"));
                return prepareCreate();
            } else {
                JsfUtil.addErrorMessage(ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
                return null;
            }
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        this.file = event.getFile().getFileName();
        InputStream inputStream = null;
        try {
            inputStream = event.getFile().getInputstream();
            new File(ServerURISettings.SOFTWARE_RELEASE_DIR + ServerURISettings.OS_SEP + tmpFolder + ServerURISettings.OS_SEP).mkdirs();
            File f = new File(ServerURISettings.SOFTWARE_RELEASE_DIR + ServerURISettings.OS_SEP + tmpFolder + ServerURISettings.OS_SEP + event.getFile().getFileName());
            FileUtils.copyInputStreamToFile(inputStream, f);
        } catch (IOException ex) {
            System.err.println("handleFileUpload() : Unable to copy InputStream: "+ex);
        } finally {
            try {

                inputStream.close();
            } catch (IOException ex) {
            }
        }
    }
}
