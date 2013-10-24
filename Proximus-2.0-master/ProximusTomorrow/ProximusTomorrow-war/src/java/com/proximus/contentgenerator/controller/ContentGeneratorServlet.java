package com.proximus.contentgenerator.controller;

import com.proximus.jsf.CampaignController;
import com.proximus.jsf.LoginController;
import com.proximus.util.AbstractFacesServlet;
import com.proximus.util.ServerURISettings;
import java.io.IOException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ContentGeneratorServlet", urlPatterns = {"/faces/contentgenerator/generate"})     // specify urlPattern for servlet
@MultipartConfig
public class ContentGeneratorServlet extends AbstractFacesServlet {
    
    private LoginController loginController = null;
    private ContentGeneratorController contentGeneratorController = null;
    private CampaignController campaignController = null;
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
//        loginController = (LoginController) request.getSession().getAttribute("loginController");
//        contentGeneratorController = (ContentGeneratorController) request.getSession().getAttribute("contentGeneratorController");
//        campaignController = (CampaignController)request.getSession().getAttribute("campaignController");
//        
//        
//        if (loginController == null) {
//            System.err.println("LoginController was not injected in ContentGeneratorServlet");
//            redirectToLogin(request, response);
//            return;
//        }
//        
//        if (contentGeneratorController == null) {
//            System.err.println("ContentGeneratorController was not injected in ContentGeneratorServlet");
//            redirectToHome(request, response);
//            return;
//        }
//        
//        
//        if (campaignController == null) {
//            System.err.println("CampaignController was not injected in ContentGeneratorServlet");
//            redirectToHome(request, response);
//            return;
//        }
//        
//        System.out.println("before createTemplate");
//        contentGeneratorController.createTemplate(loginController.getUserUUIDTmp());
//        String toRedirect = campaignController.prepareFromContentGenerator();
//        System.out.println("before redirect");
//        this.redirectToCampaignManager(request, response, toRedirect);
        
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
    
    private void redirectToCampaignManager(HttpServletRequest request, HttpServletResponse response, String path) throws IOException {
        
        FacesContext fc = this.getFacesContext(request, response);
        if(fc !=null) { 
                System.out.println("Path: " + fc.getExternalContext().getRequestContextPath() + "/faces" + path);
                response.sendRedirect(fc.getExternalContext().getRequestContextPath() + "/faces" + path + ".xhtml");
                this.removeFacesContext();
        }
    }
} // end of UploadServlet
