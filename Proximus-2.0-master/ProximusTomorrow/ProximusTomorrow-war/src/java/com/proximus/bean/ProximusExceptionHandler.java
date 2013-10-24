/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.bean;

import com.proximus.jsf.LoginController;
import com.sun.faces.context.FacesFileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class ProximusExceptionHandler extends ExceptionHandlerWrapper {

    Logger logger = Logger.getLogger(ProximusExceptionHandler.class);
    private ExceptionHandler wrapped;
    protected static final long ONE_WEEK = 1000 * 60 * 60 * 24 * 7;
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

    public Long getUserIdFromSession() {
        return getHttpSession().getUser_id();
    }

    public Long getCompanyIdFromSession() {
        return getHttpSession().getCompany_id();
    }

    public String getUsernameFromSession() {
        return getHttpSession().getUsername();
    }

    public String getSeriesColors() {
        return "aa0000,0000aa,767676";
    }

    public LoginController getHttpSession() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        LoginController lc = (LoginController) session.getAttribute("loginController");
        return lc;
    }

    /**
     * Be sure to "return" after calling this
     *
     * @param severity FacesMessage.Severity
     * @param message message to send
     */
    public void addAlertMessage(FacesMessage.Severity severity, String message) {
        FacesMessage msg = new FacesMessage(severity, message, "");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    ProximusExceptionHandler(ExceptionHandler exception) {
        this.wrapped = exception;
    }

    @Override
    public ExceptionHandler getWrapped() {
        return wrapped;
    }

    @Override
    public void handle() throws FacesException {

        final Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator();
        while (i.hasNext()) {
            ExceptionQueuedEvent event = i.next();
            ExceptionQueuedEventContext context =
                    (ExceptionQueuedEventContext) event.getSource();

            // get the exception from context
            Throwable t = context.getException();

            final FacesContext fc = FacesContext.getCurrentInstance();
            final Map<String, Object> requestMap = fc.getExternalContext().getRequestMap();
            final NavigationHandler nav = fc.getApplication().getNavigationHandler();
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);

            LoginController lc;
            try
            {
                lc = (LoginController) session.getAttribute("loginController");
            }
            catch (Exception err)
            {
                System.err.println(err);
                return;
            }
            String servername = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getServerName();
            //here you do what ever you want with exception
            try {
                if (t.getCause() instanceof FacesFileNotFoundException) {
                    try {
                        FacesContext.getCurrentInstance().getExternalContext().redirect(fc.getExternalContext().getRequestContextPath() + "/faces/404.xhtml");
                    } catch (IOException ex) {
                        System.err.println("Couldn't redirect to 404 handler on FacesFileNotFoundException");
                    }
                } else if (t instanceof ViewExpiredException) {
                    System.err.println("View expired path:" + fc.getExternalContext().getRequestContextPath());
                    addAlertMessage(FacesMessage.SEVERITY_WARN, "Your session has expired for your security.");
                    addAlertMessage(FacesMessage.SEVERITY_INFO, "Please Sign In again.");
                    nav.handleNavigation(fc, fc.getExternalContext().getRequestContextPath(), "/login.xhtml");
                    fc.renderResponse();
                } else if (t instanceof IllegalArgumentException) {
                    System.err.println("Illegal Argument");
                    //nav.handleNavigation(fc, null, null);
                } else if (t instanceof IllegalStateException) {
                    //System.err.println("Illegal State: "+fc.getExternalContext().getRequestContextPath());
                    //nav.handleNavigation(fc, fc.getExternalContext().getRequestContextPath(), null);
                    return;

                } else {
                    //Check for null
                    if (lc != null && lc.getCompany_id() != null && lc.getCompanyName() != null && lc.getUsername() != null) {

                        if (servername.equalsIgnoreCase("localhost")) {
                            System.err.println("Will not report error for server:" + servername);
                            System.err.println("[" + lc.getCompany_id() + "]" + lc.getCompanyName() + "-" + getUsernameFromSession() + ": " + t.getMessage());
                            logger.error(t);
                        } else {
                            System.err.println("Adding bug in Bugtracker" + t.getMessage());
                            BugTracker.ReportError("[" + lc.getCompany_id() + "]" + lc.getCompanyName() + "-" + getUsernameFromSession() + ": " + t.getMessage(), "", t);
                        }
                    } else {
                        System.err.println("Will not report error for server:" + servername);
                        logger.error(t);
                    }
                    //redirect error page
                    //requestMap.put("exceptionMessage", t.getMessage());
                    addAlertMessage(FacesMessage.SEVERITY_WARN, "Something went wrong, we have been notified. We apologize for any inconvenience.");
                    nav.handleNavigation(fc, null, "/login.xhtml");
                    fc.renderResponse();
                }



                // remove the comment below if you want to report the error in a jsf error message
                //JsfUtil.addErrorMessage(t.getMessage());

            } finally {
                //remove it from queue
                i.remove();
            }
        }
        //parent hanle
        if(getWrapped() != null) {
            getWrapped().handle();
        }
    }
}
