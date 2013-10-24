/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf;

import com.proximus.helper.util.JsfUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author Gilberto Gaxiola
 */
@ManagedBean(name = "logLevelController")
@SessionScoped
public class LogLevelController extends AbstractController {

    private List<String> logLevels;
    private Logger logger;
    private Level logLevel;
    private String selectedLevel;
    private String selectedClassPath;
    private ResourceBundle message;
    
    public LogLevelController() {
        logLevels = new ArrayList<String>();
       // logLevels.add("OFF");
        logLevels.add("ALL");
        logLevels.add("DEBUG");
        logLevels.add("INFO");
        logLevels.add("WARN");
        logLevels.add("ERROR");
        logLevels.add("FATAL");
        message = this.getHttpSession().getMessages();
        
    }

    public void prepareVars() {
        selectedLevel = null;
        selectedClassPath = null;
        logLevel = null;
    }

    public void changeLevel() {
        if (selectedLevel == null || selectedLevel.isEmpty()) {
            JsfUtil.addErrorMessage(message.getString("noLoggerLevelSelected"));
            return;
        }
        
        logLevel = Level.toLevel(selectedLevel, Level.INFO);
        
        if (logLevel.equals(Level.OFF)) {
            JsfUtil.addErrorMessage(message.getString("cantTurnOffLogging"));
            return;
        }
        String logPath;
        if (selectedClassPath != null && !selectedClassPath.isEmpty()) {
            logger = Logger.getLogger(selectedClassPath);
            logPath = selectedClassPath;
        } else {
            logger = Logger.getRootLogger();
            logPath = logger.getName();
        }
        logger.setLevel(logLevel);
        
        JsfUtil.addSuccessMessage(message.getString("levelChangedTo") + " : " + logLevel.toString() + " " + message.getString("inClassPath") + " " + logPath);
        prepareVars();
    }

    public List<String> getLogLevels() {
        return logLevels;
    }

    public void setLogLevels(List<String> logLevels) {
        this.logLevels = logLevels;
    }

    public String getSelectedLevel() {
        return selectedLevel;
    }

    public void setSelectedLevel(String selectedLevel) {
        this.selectedLevel = selectedLevel;
    }

    public String getSelectedClassPath() {
        return selectedClassPath;
    }

    public void setSelectedClassPath(String selectedClassPath) {
        this.selectedClassPath = selectedClassPath;
    }
}
