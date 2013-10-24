package com.proximus.jsf;

import com.proximus.data.Company;
import com.proximus.data.SoftwareRelease;
import com.proximus.jsf.util.JsfUtil;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.DeviceManagerLocal;
import com.proximus.parsers.DeviceCSVParser;
import com.proximus.util.ServerURISettings;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.FileUtils;
import org.primefaces.event.FileUploadEvent;

/**
 *
 * @author eric
 */
@ManagedBean(name = "deviceImportController")
@SessionScoped
public class DeviceImportController implements Serializable
{

    @EJB
    private DeviceManagerLocal deviceMgr;
    @EJB
    private CompanyManagerLocal companyMgr;
    public String file;
    public String platform;
    private Company company;

    public DeviceImportController()
    {
    }

    public Company getCompany()
    {
        return company;
    }

    public void setCompany(Company company)
    {
        this.company = company;
    }

    public String getFile()
    {
        return file;
    }

    public void setFile(String file)
    {
        this.file = file;
    }

    public String getPlatform()
    {
        return platform;
    }

    public void setPlatform(String platform)
    {
        this.platform = platform;
    }

    public List getPlatforms()
    {
        return Arrays.asList(SoftwareRelease.PLATFORMS);
    }

    public LoginController getLoginController()
    {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        session.getAttribute("loginController");
        LoginController lc = (LoginController) session.getAttribute("loginController");
        return lc;
    }

    public String prepareImport(){
        this.platform = null;
        this.company = null;
        
        return "/device-import/Import?faces-redirect=true";
    }
    /**
     * Takes a CSV file and uploads and parses it
     *
     * @param event
     */
    public void handleFileUpload(FileUploadEvent event)
    {
        if (this.platform == null || this.platform.isEmpty())
        {
            JsfUtil.addErrorMessage("Please select a platform");
        } else
        {

            if (this.company == null)
            {
                this.company = companyMgr.find(getLoginController().getCompany_id());
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat(ServerURISettings.FILE_DATEFORMAT);
        this.file = event.getFile().getFileName();
        InputStream inputStream;
        try
        {
            File dir = new File(ServerURISettings.DEVICE_IMPORT_DIR + ServerURISettings.OS_SEP + this.platform);
            dir.mkdirs();
            File f = new File(dir, "device_" + sdf.format(new Date()) + "_" + this.file);
            inputStream = event.getFile().getInputstream();
            FileUtils.copyInputStreamToFile(inputStream, f);
            JsfUtil.addSuccessMessage("Successfully Uploaded File " + this.file);
            DeviceCSVParser parser = new DeviceCSVParser();
            int count = parser.parse(f, deviceMgr, companyMgr, company);
            JsfUtil.addSuccessMessage("Imported " + count);

        } catch (IOException ex)
        {
            JsfUtil.addErrorMessage("Unable to upload file: " + this.file + "\n" + ex.getMessage());
        }

    }
}
