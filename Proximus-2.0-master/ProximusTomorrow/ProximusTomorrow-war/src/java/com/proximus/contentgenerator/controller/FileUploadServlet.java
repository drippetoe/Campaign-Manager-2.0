package com.proximus.contentgenerator.controller;

import com.proximus.contentgenerator.bean.HtmlBlock;
import com.proximus.contentgenerator.bean.ImageUploader;
import com.proximus.helper.util.JsfUtil;
import com.proximus.jsf.LoginController;
import com.proximus.util.AbstractFacesServlet;
import com.proximus.util.MultipartMap;
import com.proximus.util.ServerURISettings;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

@WebServlet(name = "FileUploadServlet", urlPatterns = {"/faces/contentgenerator/upload"})     // specify urlPattern for servlet
@MultipartConfig
public class FileUploadServlet extends AbstractFacesServlet {

    private List<String> supportedMimeTypes = new ArrayList<String>();
    private LoginController loginController = null;
    private ContentGeneratorController contentGeneratorController = null;

    public List<String> getSupportedMimeTypes() {
        if (supportedMimeTypes == null || supportedMimeTypes.isEmpty()) {
            supportedMimeTypes.add("image/gif");
            supportedMimeTypes.add("image/jpeg");
            supportedMimeTypes.add("image/png");
            supportedMimeTypes.add("image/bmp");
        }
        return supportedMimeTypes;
    }
    public final String[] SUPPORTED_MIME_TYPES = {"image/gif", "image/jpeg", "image/png", "image/bmp"};

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        loginController = (LoginController) request.getSession().getAttribute("loginController");
        contentGeneratorController = (ContentGeneratorController) request.getSession().getAttribute("contentGeneratorController");

        if (loginController == null) {
            System.err.println("LoginController was not injected in FileUploadServlet");
            redirectToLogin(request, response);
            return;
        }

        if (contentGeneratorController == null) {
            System.err.println("ContentGeneratorController was not injected in FileUploadServlet");
            redirectToHome(request, response);
            return;
        }
        String tempDir = ServerURISettings.CONTENT_GENERATOR_TMP_DIR + ServerURISettings.OS_SEP + loginController.getUserUUIDTmp();
        new File(tempDir).mkdirs();

        response.setContentType("text/html;charset=UTF-8");
        MultipartMap multipartMap = new MultipartMap(request, tempDir);
        
        contentGeneratorController.setErrorMsg("");
        String pageChosen = null;

        //Setup Variables
        //Eric's magic function
        Map<String, HtmlBlock> map = contentGeneratorController.getHtmlObjects();
        for (String key : map.keySet()) {
            if (map.get(key) instanceof ImageUploader) { //CHANGE ME TO ImageUploader
                String value = multipartMap.getParameter(key);
                String temp = multipartMap.getParameter("pageChosen");
                if(!temp.equalsIgnoreCase("null")) {
                    pageChosen = temp;
                }
               
                if (value != null) {

                    Part p1 = request.getPart(key);
                   
                    File file = multipartMap.getFile(key);
                    if(file.length() > 327680) {
                        FileUtils.deleteQuietly(file);
                        contentGeneratorController.setErrorMsg("File Size must be 320Kb or less");
                        redirectToGenerator(request, response,pageChosen);
                        return;

                    }
                    String ext = FilenameUtils.getExtension(file.getName());
                    String name = file.getName();
                    if (getSupportedMimeTypes().contains(p1.getContentType())) {
                        //String completePath = tempDir + ServerURISettings.OS_SEP + p1.getName() + "." + ext;
                        System.out.println("Filepath: " + file.getAbsolutePath());
                        
                        try {
                            String virtualFile = "/cgtemp/"+ loginController.getUserUUIDTmp() +"/"+name;
                            System.out.println("Virtual file is: " + virtualFile);
                            contentGeneratorController.setValue(key,virtualFile);
                        } catch (Exception e) {
                            System.err.println("Oh noes something went boo boo");
                            e.printStackTrace();
                        }
                    }
                }

            }
        }
        
        redirectToGenerator(request, response, pageChosen);
    } // end of doPost()

    public void addAlertMessage(FacesMessage.Severity severity, String message, HttpServletRequest request, HttpServletResponse response) {
        FacesMessage msg = new FacesMessage(severity, message, "");
        this.getFacesContext(request, response).addMessage(null, msg);
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        return;
    }

    private void redirectToHome(HttpServletRequest request, HttpServletResponse response) throws IOException {

        FacesContext fc = this.getFacesContext(request, response);
        response.sendRedirect(fc.getExternalContext().getRequestContextPath() + "/faces/home.xhtml");
        this.removeFacesContext();
    }

    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {

        FacesContext fc = this.getFacesContext(request, response);
        response.sendRedirect(fc.getExternalContext().getRequestContextPath() + "/faces/login.xhtml");
        this.removeFacesContext();
    }

    private void redirectToGenerator(HttpServletRequest request, HttpServletResponse response, String pageChosen) throws IOException {

        FacesContext fc = this.getFacesContext(request, response);
        if(pageChosen == null || pageChosen.isEmpty() || pageChosen.equalsIgnoreCase("null")) {
            pageChosen = "index.html";
        }
        response.sendRedirect(fc.getExternalContext().getRequestContextPath() + "/faces/contentgenerator/index.jsp?pageChosen="+pageChosen);
        this.removeFacesContext();
    }
} // end of UploadServlet