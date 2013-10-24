<%@page import="com.proximus.util.ServerURISettings"%>
<%@page import="com.proximus.contentgenerator.bean.HtmlBlock"%>
<%@page import="java.util.*"%>
<%@page import="com.proximus.data.Privilege"%>
<%@page import="java.util.ResourceBundle"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<jsp:useBean id="loginController" class="com.proximus.jsf.LoginController" scope="session"/>
<jsp:useBean id="contentGeneratorController" class="com.proximus.contentgenerator.controller.ContentGeneratorController" scope="session"/>
<jsp:useBean id="campaignController" class="com.proximus.jsf.CampaignController" scope="session"/>
<jsp:setProperty name="contentGeneratorController" property="*"/> 
<jsp:setProperty name="loginController" property="*"/> 
<jsp:setProperty name="campaignController" property="*"/> 




<%
    if (!loginController.isLoggedIn()) {

        String loginPath = application.getContextPath() + "/faces/login.xhtml";
        response.sendRedirect(loginPath);
    }

%> 


<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
        <link rel="stylesheet" type="text/css" href="css/contentgenerator.css" />
        <link rel="stylesheet" type="text/css" href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/base/jquery-ui.css"/>

        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/jquery-ui.min.js"></script>


        <script type="text/javascript" src="js/contentgenerator.js"></script>
        <title>
            Content Generator
        </title>

        <%
            //Need also a General Settings changer for Title of Page and other meta data
            //Need a way to change this by a Settings in the sidebar.jsp or something
            contentGeneratorController.setSelectedTempDir(ServerURISettings.CONTENT_GENERATOR_TMP_DIR + ServerURISettings.OS_SEP + loginController.getUserUUIDTmp());
            if(contentGeneratorController.getSelectedTemplate() == null || contentGeneratorController.getSelectedTemplate().isEmpty()) {
                contentGeneratorController.setSelectedTemplate("3offers");
                contentGeneratorController.changeTemplate();
            }
            
            
        %>

        <style type="text/css">
            <%= contentGeneratorController.getTemplateCSSData()%>
        </style>
    </head>



    <body>
        <div id="overlay" style="position:absolute;top:0px;left:0px;width:100%; height:100%; background-color: white;z-index: 1000;">

        </div>
        <div id="overlay_info" style="position:absolute;top:200px;left:33%;right:33%;margin-left:auto;margin-right:auto; border:2px; z-index:1002;background-color:white;padding:16px;">
            <center>
                <img src="images/loader.gif"/><br />
                <h1>Uploading</h1></center>
        </div>
        <%
            // http://localhost:8080/ProximusTomorrow-war/faces/contentgenerator/index.jsp
            ResourceBundle resource = ResourceBundle.getBundle("/resources/Bundle");
            ResourceBundle message = ResourceBundle.getBundle("/resources/Messages");
            String campaignWizard = resource.getString("CampaignWizard");

        %>
        <% if (!loginController.getPrivileges().contains(campaignWizard) && !loginController.isHasProximityLicense()) {%>
        <h1 style="font-family: Arial;font-size: 16px;color: red;"><%= resource.getString("InsufficientPrivileges")%></h1>
        <% }%>


        <% if (loginController.getPrivileges().contains(campaignWizard) && loginController.isHasProximityLicense()) {%>


        <%

            //Setting requests Params
            String pageChosen = request.getParameter("pageChosen");
            if (pageChosen != null && !pageChosen.isEmpty()) {
                if (pageChosen.equals("goHome")) {
                    String path = request.getServletContext().getContextPath() + "/faces/home.xhtml";
                    response.sendRedirect(path);
                } else {
                    contentGeneratorController.setSelectedPage(pageChosen);
                }
            }


            //Setup Variables
            //Eric's magic function
            Map<String, HtmlBlock> map = contentGeneratorController.getHtmlObjects();
            for (String key : map.keySet()) {

                String value = request.getParameter(key);
                if (value != null) {
                    contentGeneratorController.setValue(key, value);
                }
            }
        %>

        <form action="index.jsp" id="logo_upload_form" method="post" enctype="multipart/form-data">
            <% if (contentGeneratorController.getErrorMsg() != null && !contentGeneratorController.getErrorMsg().isEmpty()) {%>
            <center>


                <div class="ui-state-error ui-corner-all" style="position: absolute; right: 10px; width: 320px; padding: 0 .7em; float:right;"> 

                    <p><span class="ui-icon ui-icon-alert" style=""></span> 
                        <%
                            out.print(contentGeneratorController.getErrorMsg());
                        %>
                    </p>

                </div>
            </center>
            <%
                    contentGeneratorController.setErrorMsg("");
                }
            %>

            <div id="leftsidebar" style="position: absolute; left: 0px; width: 300px; background-color: #999999; border-left:1px solid black; margin:0; padding:0; height:90%;">
                <div id="sidebarContainer">
                    <div id="sidebarHeader">
                        <h2>Settings</h2>
                    </div>

                    <div id="sidebarBody">
                        <p><%= message.getString("choosePageToModify")%></p>

                        <%
                            for (String s : contentGeneratorController.getListOfPages()) {
                                String name = s.substring(0, s.lastIndexOf("."));
                                name = name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
                                String[] split = name.split("(?=\\d+)");
                                name = "";
                                for (int i = 0; i < split.length; i++) {
                                    name += split[i] + " ";
                                }
                                name.trim();

                        %>
                        <a href="<%= s%>" class="button offerButton"><%= name%></a>
                        <br />
                        <% }%>
                    </div>
                    <div id="sidebarFooter">
                    </div>
                </div>
            </div>

            <div id="editor_container">
                <%= contentGeneratorController.getPageData(false)%>
                <input id="pageChosen" type="hidden" name="pageChosen" value="<%= request.getParameter("pageChosen")%>"/>
                <input class="hide" type="submit" name="submit" value="Submit"/>	
            </div>
        </form>
        <% }%>
    </body>
</html>
