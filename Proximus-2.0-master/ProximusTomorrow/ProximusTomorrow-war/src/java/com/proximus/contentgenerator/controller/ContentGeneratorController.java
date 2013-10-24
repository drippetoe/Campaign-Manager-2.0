/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.contentgenerator.controller;

import com.proximus.contentgenerator.bean.*;
import com.proximus.helper.util.JsfUtil;
import com.proximus.jsf.AbstractController;
import com.proximus.util.ServerURISettings;
import com.proximus.util.ZipUnzip;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author Gilberto Gaxiola
 */
@ManagedBean(name = "contentGeneratorController")
@SessionScoped
public class ContentGeneratorController extends AbstractController implements Serializable {

    private static final Logger logger = Logger.getLogger(ContentGeneratorController.class.getName());
    private final String HTML_CONTENT_REGEX = ".*<body.*?>(.*)</body>.*";
    private final Pattern htmlBodyPattern = Pattern.compile(HTML_CONTENT_REGEX, Pattern.DOTALL);
    private final String MACRO_REGEX = HtmlBlock.MACRO_TAG + "(.*)" + HtmlBlock.MACRO_TAG;
    private final Pattern macroPattern = Pattern.compile(MACRO_REGEX);
    private final String HEAD_CONTENT_REGEX = "(.*)<body.*?>.*";
    private final Pattern htmlMetaDataPattern = Pattern.compile(HEAD_CONTENT_REGEX, Pattern.DOTALL);
    private final String[] VALID_EXTENSIONS = {"html"};
    Map<String, String> pagesMap;
    private String selectedCSSStyle;
    private String selectedTemplate;
    private String selectedPage;
    private String errorMsg;
    private String selectedTempDir;
    private Map<String, HtmlBlock> htmlObjects;
    protected File zipFile = null;
    public final static String ZIP_CONTENT_NAME = "generatedContent.zip";
    private boolean isFacebookOnly;
    private boolean facebookContent;

    public ContentGeneratorController() {
        this.htmlObjects = new HashMap<String, HtmlBlock>();
        pagesMap = new HashMap<String, String>();
        isFacebookOnly = false;
        facebookContent = false;
    }

    public String resetContent() {
        if (selectedTempDir != null) {
            File f = new File(selectedTempDir);
            if (f.exists()) {
                try {
                    FileUtils.deleteDirectory(f);
                } catch (IOException ex) {
                }
            }
        }
        this.htmlObjects = new HashMap<String, HtmlBlock>();
        pagesMap = new HashMap<String, String>();
        isFacebookOnly = false;
        facebookContent = false;
        if (this.selectedTemplate == null) {
            this.selectedTemplate = "3offers";
        }

        //From template we can get all the .css .html and .php files and create a the pagesMap from them
        pagesMap = new HashMap<String, String>();
        File templateDir = new File(ServerURISettings.TEMPLATE_ROOT_DIR + ServerURISettings.OS_SEP + selectedTemplate);
        if (templateDir.isDirectory()) {
            File[] listFiles = templateDir.listFiles();
            Arrays.sort(listFiles);
            for (File f : listFiles) {
                if (FilenameUtils.isExtension(f.getName(), VALID_EXTENSIONS)) {
                    pagesMap.put(f.getName(), "");
                    if (f.getName().contains("index.")) {
                        selectedPage = f.getName();
                    }
                } else if (FilenameUtils.isExtension(f.getName(), "css")) {
                    selectedCSSStyle = f.getName();
                }
            }
        }
        return "/contentgenerator/Generator/?faces-redirect=true";
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Map<String, HtmlBlock> getHtmlObjects() {
        return htmlObjects;
    }

    public void setHtmlObjects(Map<String, HtmlBlock> htmlObjects) {
        this.htmlObjects = htmlObjects;
    }

    public void setValue(String key, String value) {
        if (this.htmlObjects.containsKey(key)) {
            HtmlBlock block = this.htmlObjects.get(key);
            block.setValue(value);
            this.htmlObjects.put(key, block);
        }
    }

    public String getValue(String key) {
        if (this.htmlObjects.containsKey(key)) {
            HtmlBlock block = this.htmlObjects.get(key);
            return block.getValue();
        }
        return null;
    }

    public String getEditor(String key) {
        if (this.htmlObjects.containsKey(key)) {
            HtmlBlock block = this.htmlObjects.get(key);
            return block.getEditor();
        }
        return null;
    }

    public String getTemplateCSSData() {
        File f = new File(ServerURISettings.TEMPLATE_ROOT_DIR + ServerURISettings.OS_SEP + selectedTemplate + ServerURISettings.OS_SEP + selectedCSSStyle);
        String contents = "";
        BufferedReader input = null;
        try {
            input = new BufferedReader(new FileReader(f));
            String line = null;
            while ((line = input.readLine()) != null) {
                contents += line + System.getProperty("line.separator");
            }
            return contents;
        } catch (Exception ex) {
            logger.error(ex);
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (Exception ex) {
            }
        }
        return null;


    }

    public String getMetaData() {
        File f = new File(ServerURISettings.TEMPLATE_ROOT_DIR + ServerURISettings.OS_SEP + selectedTemplate + ServerURISettings.OS_SEP + selectedPage);
        String contents = "";
        BufferedReader input;
        try {
            input = new BufferedReader(new FileReader(f));
            String temp = null;
            while ((temp = input.readLine()) != null) {
                contents += temp.trim() + System.getProperty("line.separator");
            }
            Matcher matcher = htmlMetaDataPattern.matcher(contents);
            String result = "";
            while (matcher.find()) {
                result += matcher.group(1);
            }

            return result;
        } catch (Exception ex) {
            logger.error(ex);
        }
        return null;
    }

    public String getPageData(boolean getRealValue) {
        File f = new File(ServerURISettings.TEMPLATE_ROOT_DIR + ServerURISettings.OS_SEP + selectedTemplate + ServerURISettings.OS_SEP + selectedPage);
        StringBuilder contents = new StringBuilder();
        BufferedReader input = null;
        try {
            input = new BufferedReader(new FileReader(f));
            String line = null;
            while ((line = input.readLine()) != null) {
                line = line.trim();
                contents.append(line);
                contents.append(System.getProperty("line.separator"));
            }

            String code = LeeLooMultipass(contents.toString(), getRealValue);

            //Stripping out only the content within the <body> tags
            Matcher matcher = htmlBodyPattern.matcher(code);
            String result = "";
            while (matcher.find()) {
                result += matcher.group(1);
            }
            return result.isEmpty() ? code : result;
        } catch (Exception ex) {
            logger.error(ex);
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (Exception ex) {
            }
        }
        return null;


    }

    private String LeeLooMultipass(String raw, boolean isRealValue) {

        String[] lines = raw.split("\n");
        List<String> queue = new ArrayList<String>();
        boolean inRepeaterMode = false;
        int repeaterCounter = 0;
        String resultSet = "";
        for (String line : lines) {
            boolean isAMacro = false;
            Matcher m = macroPattern.matcher(line);
            while (m.find()) {
                if (m.groupCount() == 1) {
                    String macro = m.group(1);
                    String[] result = macro.replaceAll(HtmlBlock.MACRO_TAG, "").split("_");
                    //Valid Tag
                    if (result.length >= 2) {
                        isAMacro = true;
                        String clazz = result[0];
                        String id = result[1];
                        //Checking if a Repeater or not


                        if (result[0].equals(ContentRepeater.class.getSimpleName())) {

                            if (result[2].equals(ContentRepeater.START_COMMAND)) {
                                if (inRepeaterMode) {
                                    System.err.println("Can't initialize a ContentRepeater inside another ContentRepeater");
                                    return null;
                                }
                                inRepeaterMode = true;
                                repeaterCounter = Integer.valueOf(result[3]);
                            } else if (result[2].equals(ContentRepeater.END_COMMAND)) {
                                inRepeaterMode = false;
                                for (int i = 0; i < repeaterCounter; i++) {
                                    for (int j = 0; j < queue.size(); j++) {
                                        if (queue.get(j).matches(MACRO_REGEX)) {
                                            resultSet += getReplacement(queue.get(j), true, "" + i, isRealValue) + "\n";
                                        } else {
                                            resultSet += queue.get(j) + "\n";
                                        }
                                    }
                                }
                                repeaterCounter = 0;

                            }
                            //Need to repeat blocks until Ending Tag
                            //If start init the repeater counter have a inRepeaterMode and any other elements are added to a queue
                            //If end replace x number of times the contents on the Queue get out of inRepeaterMode and clear counter
                        } else {
                            if (inRepeaterMode) {
                                queue.add(line);
                            } else {

                                resultSet += getReplacement(line, false, "", isRealValue) + "\n";

                            }
                        }
                    }
                }

            }
            if (!isAMacro) {
                if (!inRepeaterMode) {
                    resultSet += line + "\n";
                } else {
                    queue.add(line);
                }
            } else {
            }
        }
        return resultSet;
    }

    private String getReplacement(String tag, boolean inRepeaterMode, String idOffset, boolean isRealValue) {
        String[] params = tag.replaceAll(HtmlBlock.MACRO_TAG, "").split("_");
        if (params.length >= 2) {
            String clazz = params[0].trim();
            String id = params[1].trim();
            if (inRepeaterMode) {
                id += "_" + idOffset;

            }

            if (clazz.equals(TextEditor.class.getSimpleName())) {
                //Do logic for clazz in order to see if extra params are needed
                TextEditor te = null;

                if (this.htmlObjects.containsKey(id)) {
                    te = (TextEditor) this.htmlObjects.get(id);
                } else {
                    te = new TextEditor(id);
                    this.htmlObjects.put(id, te);
                }

                if (isRealValue) {
                    return te.toString();
                }

                return te.getEditor();
            }


            if (clazz.equals(ImageUploader.class.getSimpleName())) {
                ImageUploader iu = null;

                if (this.htmlObjects.containsKey(id)) {
                    iu = (ImageUploader) this.htmlObjects.get(id);
                } else {
                    iu = new ImageUploader(id);
                    this.htmlObjects.put(id, iu);
                }

                if (isRealValue) {
                    return iu.toString();
                }
                return iu.getEditor();
            }

            if (clazz.equals(FacebookButton.class.getSimpleName())) {
                FacebookButton fb = null;
                if (this.htmlObjects.containsKey(id)) {
                    fb = (FacebookButton) this.htmlObjects.get(id);
                } else {
                    fb = new FacebookButton(id);
                    fb.setIsFacebookOnly(isFacebookOnly);
                    this.facebookContent = true;
                    this.htmlObjects.put(id, fb);
                }
                if (isRealValue) {
                    return fb.toString();
                }
                return fb.getEditor();
            }
        }
        return "";
    }

    public String getSelectedPage() {
        return selectedPage;
    }

    public void setSelectedPage(String selectedPage) {
        this.selectedPage = selectedPage;
    }

    public String getSelectedTemplate() {
        return selectedTemplate;
    }

    public void changeTemplate() {
        resetContent();
        if (this.selectedTemplate == null) {
            this.selectedTemplate = "3offers";
        }
        if (this.selectedTemplate.equalsIgnoreCase("facebook")) {
            isFacebookOnly = true;
        } else {
            isFacebookOnly = false;
        }
    }

    public void setSelectedTemplate(String selectedTemplate) {
        this.selectedTemplate = selectedTemplate;
    }

    public String getSelectedCSSStyle() {
        return selectedCSSStyle;
    }

    public void setSelectedCSSStyle(String selectedCSSStyle) {
        this.selectedCSSStyle = selectedCSSStyle;
    }

    public void createCSSPage() {
        File src = new File(ServerURISettings.TEMPLATE_ROOT_DIR + ServerURISettings.OS_SEP + selectedTemplate + ServerURISettings.OS_SEP + selectedCSSStyle);
        File dest = new File(selectedTempDir + ServerURISettings.OS_SEP + selectedCSSStyle);
        try {
            FileUtils.copyFile(src, dest);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    public void createSelectedPage() {
        File dest = new File(selectedTempDir + ServerURISettings.OS_SEP + selectedPage);
        String meta = getMetaData();
        String body = getPageData(true);
        String completeHtml = meta + "\n" + body + "\n</html>";
        try {
            FileUtils.writeStringToFile(dest, completeHtml);
        } catch (IOException ex) {
            logger.error(ex);
        }

    }

    private void createFacebookTemplate() throws IOException {
        FacebookButton fb = null;
        String content = "";
        if (this.htmlObjects.containsKey("accessInternet")) {
            fb = (FacebookButton) this.htmlObjects.get("accessInternet");
            content = fb.toString();
        } else {
            JsfUtil.addErrorMessage("Something went wrong on with FACEBOOK generation please contact support@proximusmobility.com to notify us");
            logger.error("FACEBOOK content Generator couldn't find htmlObject maybe the Template has the wrong id which shouuld be accessInternet");
            return;
        }
        //Creating the company.php file
        File dest = new File(selectedTempDir + ServerURISettings.OS_SEP + "company.php");
        try {
            FileUtils.writeStringToFile(dest, content);
        } catch (IOException ex) {
            logger.error(ex);
        }

    }

    public void createTemplate() {
        if (selectedTempDir == null) {
            return;
        }
        File tempDir = new File(selectedTempDir);
        if (!tempDir.exists()) {
            new File(selectedTempDir).mkdirs();
        }
        if (this.isFacebookOnly) {
            try {
                createFacebookTemplate();
                File src = new File(ServerURISettings.TEMPLATE_ROOT_DIR + ServerURISettings.OS_SEP + selectedTemplate);
                if (src.isDirectory()) {
                    //Now copy everything except the html pages
                    File[] listFiles = src.listFiles();
                    for (File f : listFiles) {
                        if (!FilenameUtils.isExtension(f.getName(), "html")) {
                            //Simply Copy
                            File dest = new File(selectedTempDir + ServerURISettings.OS_SEP + f.getName());
                            FileUtils.copyFile(f, dest);
                        }
                    }
                }



            } catch (IOException ex) {
                logger.warn("Couldn't create Facebook Content Generator");
            }
        } else {
            try {
                createCSSPage();
                List<String> list = getListOfPages();
                String tempSelected = this.selectedPage;
                for (String s : list) {
                    this.selectedPage = s;
                    createSelectedPage();
                }
                this.selectedPage = tempSelected;
                File src = new File(ServerURISettings.TEMPLATE_ROOT_DIR + ServerURISettings.OS_SEP + selectedTemplate);
                if (src.isDirectory()) {
                    //Now copy all the non Editing files (php and javascript)
                    File[] listFiles = src.listFiles();
                    for (File f : listFiles) {
                        if (FilenameUtils.isExtension(f.getName(), "js")) {
                            //Simply Copy
                            File dest = new File(selectedTempDir + ServerURISettings.OS_SEP + f.getName());
                            FileUtils.copyFile(f, dest);

                        } else if (FilenameUtils.isExtension(f.getName(), "php")) {
                            tempSelected = this.selectedPage;
                            this.selectedPage = f.getName();
                            createSelectedPage();
                            this.selectedPage = tempSelected;
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e);
            }
        }
        zipFile = createZipTemplate(selectedTempDir);

    }

    protected File createZipTemplate(String serverPath) {
        try {
            ZipUnzip.ZipAllButIgnore(serverPath, ZIP_CONTENT_NAME);
            return new File(serverPath + ServerURISettings.OS_SEP + ZIP_CONTENT_NAME);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        return null;

    }

    public String getSelectedTempDir() {
        return selectedTempDir;
    }

    public void setSelectedTempDir(String selectedTempDir) {
        this.selectedTempDir = selectedTempDir;
    }

    public List<String> getListOfPages() {
        List<String> result = new ArrayList<String>(pagesMap.keySet());
        Collections.sort(result);
        return result;
    }

    public File getZipFile() {
        return zipFile;
    }

    public void setZipFile(File zipFile) {
        this.zipFile = zipFile;
    }

    public boolean isFacebookContent() {
        return facebookContent;
    }

    public void setFacebookContent(boolean facebookContent) {
        this.facebookContent = facebookContent;
    }
}
