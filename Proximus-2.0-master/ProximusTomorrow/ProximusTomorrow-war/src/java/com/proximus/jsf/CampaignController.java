package com.proximus.jsf;

import com.proximus.bean.ImageFile;
import com.proximus.contentgenerator.controller.ContentGeneratorController;
import com.proximus.data.*;
import com.proximus.jsf.datamodel.CampaignDataModel;
import com.proximus.jsf.datamodel.FileDataModel;
import com.proximus.jsf.datamodel.HotspotDomainDataModel;
import com.proximus.jsf.util.JsfUtil;
import com.proximus.manager.*;
import com.proximus.util.ServerURISettings;
import com.proximus.util.ZipUnzip;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@ManagedBean(name = "campaignController")
@SessionScoped
public class CampaignController extends AbstractController implements Serializable {

    /*
     * 2.0.0
     */
    private static final long serialVersionUID = 1;
    private Campaign selectedCampaign;
    private CampaignDataModel campaignModel;
    private List<Campaign> filteredCampaigns;
    private Campaign createdCampaign;
    @EJB
    CampaignManagerLocal campaignFacade;
    @EJB
    CompanyManagerLocal companyFacade;
    @EJB
    private CampaignFileManagerLocal campaignFileManager;
    @EJB
    HotspotDomainManagerLocal hotspotMgr;
    @EJB
    TagManagerLocal tagFacade;
    @EJB
    PubNubKeyManagerLocal pubNubMgr;
    private String filterInput;
    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;
    private boolean sunday;
    private List<String> daysOfWeek;
    private List<String> files;
    private String tmpFolder;
    private String wifiFileName;
    private BluetoothCampaign bt;
    private boolean wifiCondition;
    private boolean bluetoothCondition;
    private WifiCampaign wifi;
    private PubNubKey selectedPubNubKey;
    private List<PubNubKey> pubNubList;
    /*
     * 2.1.0
     */
    private static DecimalFormat df = new DecimalFormat("#.##");
    private ImageFile selectedFile;
    private FileDataModel fileModel;
    /*
     * 2.2.0
     */
    private HotspotDomain domain;
    private HotspotDomain selectedDomain;
    private HotspotDomainDataModel domainModel;
    private List<HotspotDomain> domains;
    //Setting up the CAMPAIGN_TMP_DIR base Path for previewing
    private final String CAMPAIGN_TMP_DIR = FacesContext.getCurrentInstance().getExternalContext().getRealPath("//tmp");
    private String phonePreviewPath;
    private final int KILOBYTE = 1024;
    private List<TagChooser> selectedTags;
    private List<TagChooser> listTags;
    private boolean generatedContent;
    @ManagedProperty(value = "#{contentGeneratorController}")
    private ContentGeneratorController contentGeneratorController;
    private boolean hasFacebookContent;
    private ResourceBundle message;

    public CampaignController() {
        message = this.getHttpSession().getMessages();
    }

    public List<TagChooser> getSelectedTags() {
        if (selectedTags == null) {
            selectedTags = new ArrayList<TagChooser>();
        }
        return selectedTags;
    }

    public void setSelectedTags(List<TagChooser> selectedTags) {
        this.selectedTags = selectedTags;
    }

    public List<TagChooser> getListTags() {
        if (listTags == null || listTags.isEmpty()) {
            listTags = new ArrayList<TagChooser>();
            List<String> names = tagFacade.getTagNamesByCompany(companyFacade.find(this.getCompanyIdFromSession()));
            if (names != null) {
                for (String n : names) {
                    listTags.add(new TagChooser(n, false));
                }
            }
        }
        return listTags;
    }

    public void setListTags(List<TagChooser> listTags) {
        this.listTags = listTags;
    }

    public ImageFile getSelectedFile() {
        return selectedFile;
    }

    public void setSelectedFile(ImageFile selectedFile) {
        this.selectedFile = selectedFile;
    }

    public String getTmpFolder() {
        return tmpFolder;
    }

    public void setTmpFolder(String tmpFolder) {
        this.tmpFolder = tmpFolder;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public boolean isFriday() {
        return friday;
    }

    public void setFriday(boolean friday) {
        this.friday = friday;
    }

    public boolean isMonday() {
        return monday;
    }

    public void setMonday(boolean monday) {
        this.monday = monday;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public void setSaturday(boolean saturday) {
        this.saturday = saturday;
    }

    public boolean isSunday() {
        return sunday;
    }

    public void setSunday(boolean sunday) {
        this.sunday = sunday;
    }

    public boolean isThursday() {
        return thursday;
    }

    public void setThursday(boolean thursday) {
        this.thursday = thursday;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public void setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public void setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
    }

    public boolean isWifiCondition() {
        return wifiCondition;
    }

    public void setWifiCondition(boolean wifiCondition) {
        this.wifiCondition = wifiCondition;
    }

    public Campaign getSelectedCampaign() {
        return selectedCampaign;
    }

    public boolean isBluetoothCondition() {
        return bluetoothCondition;
    }

    public void setBluetoothCondition(boolean bluetoothCondition) {
        this.bluetoothCondition = bluetoothCondition;
    }

    public Campaign getCreatedCampaign() {
        if (createdCampaign == null) {
            prepareCreate();
        }
        return createdCampaign;
    }

    public void setCreatedCampaign(Campaign createdCampaign) {
        this.createdCampaign = createdCampaign;
    }

    public WifiCampaign getSelectedWifi() {
        if (wifi == null) {
            wifi = new WifiCampaign();
        }
        return wifi;
    }

    public BluetoothCampaign getSelectedBluetooth() {
        if (bt == null) {
            bt = new BluetoothCampaign();
        }
        return bt;
    }

    public HotspotDomain getSelectedDomain() {
        if (selectedDomain == null) {
            selectedDomain = new HotspotDomain();
        }
        return selectedDomain;
    }

    public void setSelectedDomain(HotspotDomain selectedDomain) {
        this.selectedDomain = selectedDomain;
    }

    public void setHotspotDomain(HotspotDomain domain) {
        this.domain = domain;
    }

    public HotspotDomain getHotspotDomain() {
        if (domain == null) {
            domain = new HotspotDomain();
        }
        return domain;
    }

    public List<HotspotDomain> getDomains() {
        if (domains == null) {
            domains = new ArrayList<HotspotDomain>();
        }
        return domains;
    }

    public void setDomains(List<HotspotDomain> domains) {
        this.domains = domains;
    }

    public void addToDomainList() {
        if (domain != null && domain.getDomainName() != null && !domain.getDomainName().isEmpty()) {
            getDomains().add(domain);
            populateDomainModel();
        }

        domain = new HotspotDomain();
    }

    public void removeFromDomainList() {

        getDomains().remove(selectedDomain);
        if (selectedDomain.getId() != 0 || selectedDomain.getId() != null) {
            hotspotMgr.delete(selectedDomain);
        }
        domainModel.removeDomain(selectedDomain);
        populateDomainModel();
    }

    public HotspotDomainDataModel getDomainModel() {
        if (domainModel == null) {

            domainModel = new HotspotDomainDataModel(new ArrayList<HotspotDomain>());
        }
        return domainModel;
    }

    public void setDomainModel(HotspotDomainDataModel domainModel) {
        this.domainModel = domainModel;
    }

    public CampaignDataModel getCampaignModel() {
        if (campaignModel == null) {
            populateCampaignModel();
        }
        return campaignModel;
    }

    public void setCampaignModel(CampaignDataModel campaignModel) {
        this.campaignModel = campaignModel;
    }

    public List<Campaign> getFilteredCampaigns() {
        return filteredCampaigns;
    }

    public void setFilteredCampaigns(List<Campaign> filteredCampaigns) {
        this.filteredCampaigns = filteredCampaigns;
    }

    public void setSelectedCampaign(Campaign selectedCampaign) {
        this.selectedCampaign = selectedCampaign;
    }

    public String getFilterInput() {
        return filterInput;
    }

    public void setFilterInput(String filterInput) {
        this.filterInput = filterInput;
    }

    public void populateCampaignModel() {
        List<Campaign> campaigns = campaignFacade.findAllByCompanyActive(this.getCompanyFromSession());
        campaignModel = new CampaignDataModel(campaigns);
        filteredCampaigns = new ArrayList<Campaign>(campaigns);
    }

    public void populateDomainModel() {
        domainModel = new HotspotDomainDataModel();
        if (domains != null && !domains.isEmpty()) {
            domainModel.setDomains(domains);
        } else {
            domains = new ArrayList<HotspotDomain>();
            domainModel.setDomains(domains);
        }
    }

    public String save(Campaign campaign) {

        if (campaign.getName() == null || campaign.getName().isEmpty()) {
            
            JsfUtil.addErrorMessage(message.getString("campaignUniqueName"));
            return null;
        }

        if (campaign.getName().contains(",")) {

            JsfUtil.addErrorMessage(message.getString("campaignNameNoCommas"));
        }

        populateFileModel();
        try {

            this.daysOfWeek = new ArrayList<String>();
            if (this.monday) {
                this.daysOfWeek.add("M");
            }
            if (this.tuesday) {
                this.daysOfWeek.add("T");
            }
            if (this.wednesday) {
                this.daysOfWeek.add("W");
            }
            if (this.thursday) {
                this.daysOfWeek.add("R");
            }
            if (this.friday) {
                this.daysOfWeek.add("F");
            }
            if (this.saturday) {
                this.daysOfWeek.add("S");
            }
            if (this.sunday) {
                this.daysOfWeek.add("U");
            }
            if (this.daysOfWeek.size() > 0) {
                String days = StringUtils.join(this.daysOfWeek, ",");
                campaign.setDayOfWeek(days);
            } else {
                JsfUtil.addErrorMessage(message.getString("pleaseSelectMinimumOneDay"));
                return null;
            }

            if (!wifiCondition && !bluetoothCondition) {
                JsfUtil.addErrorMessage(message.getString("pleaseSelectBluetoothWifi"));
                return null;
            }

            if (bluetoothCondition) {

                if (bt.getFriendlyName() == null || bt.getFriendlyName().isEmpty()) {
                    JsfUtil.addErrorMessage(message.getString("pleaseSpecifyBluetoothName"));
                    return null;
                }
                boolean found = false;
                for (String string : this.files) {
                    if (string.startsWith(new BluetoothCampaign().getType() + ServerURISettings.OS_SEP)) {
                        found = true;
                    }

                }
                if (!found) {
                    JsfUtil.addErrorMessage(message.getString("pleaseUploadBluetooth"));
                    return null;
                }
            } else {
                for (String string : this.files) {
                    if (string.startsWith(new BluetoothCampaign().getType() + ServerURISettings.OS_SEP)) {
                        JsfUtil.addErrorMessage(message.getString("uploadsMismatch"));
                        return null;
                    }
                }
            }
            if (wifiCondition) {
                if (wifi.getNetworkName() == null || wifi.getNetworkName().isEmpty()) {
                    JsfUtil.addErrorMessage(message.getString("pleaseSpecifyWifi"));
                    return null;
                }
                if (wifiFileName == null || wifiFileName.isEmpty()) {
                    JsfUtil.addErrorMessage(message.getString("pleaseUploadWifi"));
                    return null;
                }
            } else {
                if (wifiFileName != null && !wifiFileName.isEmpty()) {
                    JsfUtil.addErrorMessage(message.getString("wifiFileMismatch"));
                    return null;
                }
            }

            campaign.setLastModified(new Date());
            campaign.setGeneratedContent(this.generatedContent);


            //If Creating a new Campaign
            if (campaign.getId() == 0) {
                if (bt != null && bluetoothCondition) {
                    bt.setCampaign(campaign);
                    bt.setChecksum(System.currentTimeMillis() + "");
                    campaign.addCampaignType(bt);
                }

                if (wifi != null && wifiCondition) {
                    wifi.setCampaign(campaign);
                    wifi.setChecksum(System.currentTimeMillis() + "");

                    if (generatedContent && hasFacebookContent) {
                        wifi.setHotSpotMode(WifiCampaign.FACEBOOK);
                    }

                    if (wifi.getHotSpotMode() == 2 || wifi.getHotSpotMode() == 4) {
                        for (HotspotDomain hotspotDomain : domains) {
                            hotspotDomain.setCampaignType(wifi);
                            wifi.addHotspotDomain(hotspotDomain);
                        }
                    }
                    campaign.addCampaignType(wifi);
                }

                Company companybyId = companyFacade.find(getCompanyIdFromSession());
                campaign.setCompany(companybyId);
                campaignFacade.create(campaign);


                if (bt != null && bluetoothCondition) {
                    CampaignFile campFile = new CampaignFile();
                    String btTempPath = CAMPAIGN_TMP_DIR + ServerURISettings.OS_SEP + tmpFolder + ServerURISettings.OS_SEP + bt.getType();
                    new File(btTempPath).mkdirs();
                    File tempFile = new File(btTempPath + ServerURISettings.OS_SEP + campaign.getId() + "_" + bt.getFriendlyName().replaceAll(" ", "_") + "_bluetooth.zip");
                    ZipUnzip.Zip(tempFile.getParent(), tempFile.getName());
                    String btFilePath = ServerURISettings.CAMPAIGNS_ROOT_DIR + ServerURISettings.OS_SEP + campaign.getId() + ServerURISettings.OS_SEP + bt.getType() + ServerURISettings.OS_SEP + tempFile.getName();
                    new File(new File(btFilePath).getParent()).mkdirs();
                    campFile.setServerPath(btFilePath);
                    campFile.setCampaignType(bt);
                    campaignFileManager.create(campFile);
                    File destFile = new File(btFilePath);
                    if (destFile.exists()) {
                        FileUtils.deleteQuietly(destFile);
                    }
                    FileUtils.moveFile(tempFile, destFile);
                }

                if (wifi != null && wifiCondition) {
                    //Deleting _ignore file used for Previewing
                    if (phonePreviewPath != null) {
                        if (!phonePreviewPath.isEmpty() && new File(phonePreviewPath).exists()) {
                            FileUtils.deleteDirectory(new File(phonePreviewPath));
                        }
                    }
                    CampaignFile campFile = new CampaignFile();
                    String basePath = CAMPAIGN_TMP_DIR + ServerURISettings.OS_SEP + tmpFolder + ServerURISettings.OS_SEP + wifi.getType();
                    new File(basePath).mkdirs();
                    File tempFile;
                    if (!wifiFileName.isEmpty()) {

                        tempFile = new File(basePath + ServerURISettings.OS_SEP + wifiFileName);
                        /**
                         * Added new unzip special
                         */
                        String newBasePath = ZipUnzip.findIndexBasePath(basePath, wifiFileName);
                        ZipUnzip.UnzipSpecial(basePath, newBasePath, wifiFileName);
                        FileUtils.deleteQuietly(tempFile);
                        if (phonePreviewPath != null) {
                            if (!phonePreviewPath.isEmpty() && new File(phonePreviewPath).exists()) {
                                FileUtils.deleteDirectory(new File(phonePreviewPath));
                            }
                        }
                        ZipUnzip.ZipAllButIgnore(basePath, wifiFileName);
                    } else {
                        File[] listFiles = new File(CAMPAIGN_TMP_DIR + ServerURISettings.OS_SEP + tmpFolder + ServerURISettings.OS_SEP + wifi.getType()).listFiles();
                        for (int i = 0; i < listFiles.length; i++) {
                            if (listFiles[i].getName().endsWith("zip") || listFiles[i].getName().endsWith("ZIP")) {
                                wifiFileName = listFiles[i].getName();
                                break;
                            }
                        }
                        tempFile = new File(CAMPAIGN_TMP_DIR + ServerURISettings.OS_SEP + tmpFolder + ServerURISettings.OS_SEP + wifi.getType() + ServerURISettings.OS_SEP + wifiFileName);
                        System.out.println("");
                        /**
                         * Added new unzip special
                         */
                        String newBasePath = ZipUnzip.findIndexBasePath(basePath, wifiFileName);
                        ZipUnzip.UnzipSpecial(basePath, newBasePath, wifiFileName);
                        FileUtils.deleteQuietly(tempFile);
                        if (phonePreviewPath != null) {
                            if (!phonePreviewPath.isEmpty() && new File(phonePreviewPath).exists()) {
                                FileUtils.deleteDirectory(new File(phonePreviewPath));
                            }
                        }
                        ZipUnzip.ZipAllButIgnore(basePath, wifiFileName);
                    }

                    String wifiFilePath = ServerURISettings.CAMPAIGNS_ROOT_DIR + ServerURISettings.OS_SEP + campaign.getId() + ServerURISettings.OS_SEP + wifi.getType() + ServerURISettings.OS_SEP + tempFile.getName();
                    new File(new File(wifiFilePath).getParent()).mkdirs();
                    campFile.setServerPath(wifiFilePath);
                    campFile.setCampaignType(wifi);
                    campaignFileManager.create(campFile);

                    File destFile = new File(wifiFilePath);
                    if (destFile.exists()) {
                        FileUtils.deleteQuietly(destFile);
                    }
                    FileUtils.moveFile(tempFile, destFile);
                }

                try {
                    FileUtils.deleteDirectory(new File(CAMPAIGN_TMP_DIR + ServerURISettings.OS_SEP + tmpFolder));
                } catch (Exception e) {
                    System.err.println("Couldn't delete TEMP FILE right now");
                    e.printStackTrace();
                }
                JsfUtil.addSuccessMessage(message.getString("campaignCreated"));


                //Setting up the Group for This Campaign   
                campaign.setTags(new ArrayList<Tag>()); //clearing tags
                selectedTags = new ArrayList<TagChooser>();
                for (TagChooser tag : this.getListTags()) {
                    if (tag.isChosen()) {
                        selectedTags.add(tag);
                    }
                }
                if (!selectedTags.isEmpty()) {
                    for (TagChooser tagName : selectedTags) {
                        Tag tag = tagFacade.findByName(tagName.getName(), companyFacade.find(this.getCompanyIdFromSession()));
                        if (!campaign.getTags().contains(tag)) {
                            campaign.addTag(tag);
                            tag.addCampaign(campaign);
                            campaignFacade.update(campaign);
                            tagFacade.update(tag);
                        }
                    }
                }

                //Setting up the PubNubKey
                campaign.setPubNubKeys(new ArrayList<PubNubKey>()); //clearing pubnubkeys
                if (selectedPubNubKey != null && selectedPubNubKey.getChannel() != null && !selectedPubNubKey.getChannel().isEmpty()) {
                    selectedPubNubKey = pubNubMgr.getPubNubKeyByChannel(selectedPubNubKey.getChannel(), campaign.getCompany());
                    if (!selectedPubNubKey.getCampaigns().contains(campaign)) {
                        campaign.addPubNubKey(selectedPubNubKey);
                        selectedPubNubKey.addCampaign(campaign);
                        campaignFacade.update(campaign);
                        pubNubMgr.update(selectedPubNubKey);
                    }
                }
                return prepareList();
            } else {
                //EDITING A CAMPAIGN
                if (bt != null) {
                    CampaignFile campFile = campaignFileManager.getFileByType(bt);
                    if (campFile == null) {
                        campFile = new CampaignFile();
                    }
                    if (bluetoothCondition) {
                        bt.setCampaign(campaign);
                        bt.setChecksum(System.currentTimeMillis() + "");
                        campaign.addCampaignType(bt);
                        new File(CAMPAIGN_TMP_DIR + ServerURISettings.OS_SEP + tmpFolder + ServerURISettings.OS_SEP + bt.getType()).mkdirs();
                        File tempFile = new File(CAMPAIGN_TMP_DIR + ServerURISettings.OS_SEP + tmpFolder + ServerURISettings.OS_SEP + bt.getType() + ServerURISettings.OS_SEP + campaign.getId() + "_" + bt.getFriendlyName().replaceAll(" ", "_") + "_bluetooth.zip");
                        ZipUnzip.Zip(tempFile.getParent(), tempFile.getName());
                        String btFilePath = ServerURISettings.CAMPAIGNS_ROOT_DIR + ServerURISettings.OS_SEP + campaign.getId() + ServerURISettings.OS_SEP + bt.getType() + ServerURISettings.OS_SEP + tempFile.getName();
                        new File(new File(btFilePath).getParent()).mkdirs();
                        campFile.setServerPath(btFilePath);
                        campFile.setCampaignType(bt);
                        campaignFileManager.update(campFile);
                        File destFile = new File(btFilePath);
                        if (destFile.exists()) {
                            FileUtils.deleteQuietly(destFile);
                        }
                        FileUtils.moveFile(tempFile, destFile);
                    } else {
                        campaignFileManager.delete(campFile);
                        campaign.removeCampaignType(bt);
                        campaignFacade.deleteCampaignType(bt);
                        for (String string : this.files) {
                            if (string.startsWith(bt.getType() + ServerURISettings.OS_SEP)) {
                                removeFile(string);
                            }
                        }

                    }
                }

                if (wifi != null) {
                    CampaignFile campFile = campaignFileManager.getFileByType(wifi);
                    if (campFile == null) {
                        campFile = new CampaignFile();
                    }
                    if (wifiCondition) {

                        wifi.setChecksum(System.currentTimeMillis() + "");
                        //backup
                        List<HotspotDomain> currList = new ArrayList<HotspotDomain>();
                        for (HotspotDomain hs : this.getDomains()) {
                            currList.add(hs);
                        }
                        //clear
                        wifi.setHotspotDomains(new ArrayList<HotspotDomain>());
                        if (wifi.getHotSpotMode() == 2 || wifi.getHotSpotMode() == 4) {
                            for (HotspotDomain hs : currList) {
                                hs.setCampaignType(wifi);
                                wifi.addHotspotDomain(hs);
                            }
                        }
                        String basePath = CAMPAIGN_TMP_DIR + ServerURISettings.OS_SEP + tmpFolder + ServerURISettings.OS_SEP + wifi.getType();
                        new File(basePath).mkdirs();
                        File tempFile;
                        //Deleting _ignore file used for Previewing
                        if (phonePreviewPath != null) {
                            if (!phonePreviewPath.isEmpty() && new File(phonePreviewPath).exists()) {
                                FileUtils.deleteDirectory(new File(phonePreviewPath));
                            }
                        }


                        if (!wifiFileName.isEmpty()) {

                            tempFile = new File(basePath + ServerURISettings.OS_SEP + wifiFileName);
                            /**
                             * Added new unzip special
                             */
                            String newBasePath = ZipUnzip.findIndexBasePath(basePath, wifiFileName);
                            ZipUnzip.UnzipSpecial(basePath, newBasePath, wifiFileName);
                            //ZipUnzip.Unzip(basePath, wifiFileName);
                            FileUtils.deleteQuietly(tempFile);
                            ZipUnzip.Zip(basePath, wifiFileName);
                        } else {
                            File[] listFiles = new File(CAMPAIGN_TMP_DIR + ServerURISettings.OS_SEP + tmpFolder + ServerURISettings.OS_SEP + wifi.getType()).listFiles();
                            for (int i = 0; i < listFiles.length; i++) {
                                if (listFiles[i].getName().endsWith("zip") || listFiles[i].getName().endsWith("ZIP")) {
                                    wifiFileName = listFiles[i].getName();
                                    break;
                                }
                            }
                            tempFile = new File(CAMPAIGN_TMP_DIR + ServerURISettings.OS_SEP + tmpFolder + ServerURISettings.OS_SEP + wifi.getType() + ServerURISettings.OS_SEP + wifiFileName);
                            /**
                             * Added new unzip special
                             */
                            String newBasePath = ZipUnzip.findIndexBasePath(basePath, wifiFileName);
                            ZipUnzip.UnzipSpecial(basePath, newBasePath, wifiFileName);
                            //ZipUnzip.Unzip(basePath, wifiFileName);
                            FileUtils.deleteQuietly(tempFile);
                            ZipUnzip.Zip(basePath, wifiFileName);
                        }

                        String wifiFilePath = ServerURISettings.CAMPAIGNS_ROOT_DIR + ServerURISettings.OS_SEP + campaign.getId() + ServerURISettings.OS_SEP + wifi.getType() + ServerURISettings.OS_SEP + tempFile.getName();
                        new File(new File(wifiFilePath).getParent()).mkdirs();
                        campFile.setServerPath(wifiFilePath);
                        campFile.setCampaignType(wifi);
                        campaignFileManager.update(campFile);

                        File destFile = new File(wifiFilePath);
                        if (destFile.exists()) {
                            FileUtils.deleteQuietly(destFile);
                        }
                        FileUtils.moveFile(tempFile, destFile);
                    } else {
                        campaignFileManager.delete(campFile);
                        campaign.removeCampaignType(wifi);
                        campaignFacade.deleteCampaignType(wifi);
                        removeFile(wifiFileName);

                    }
                }
                try {
                    FileUtils.deleteDirectory(new File(CAMPAIGN_TMP_DIR + ServerURISettings.OS_SEP + tmpFolder));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (campaign.getEndDate().before(new Date())) {
                    campaign.setActive(false);
                }
                //Setting up the Group for This Campaign
                campaign.setTags(new ArrayList<Tag>());
                campaignFacade.update(campaign);

                selectedTags = new ArrayList<TagChooser>();
                for (TagChooser tag : listTags) {
                    if (tag.isChosen()) {
                        selectedTags.add(tag);
                    }
                }
                if (!selectedTags.isEmpty()) {
                    for (TagChooser tagName : selectedTags) {
                        Tag tag = tagFacade.findByName(tagName.getName(), companyFacade.find(this.getCompanyIdFromSession()));
                        if (!campaign.getTags().contains(tag)) {
                            campaign.addTag(tag);
                            tag.addCampaign(campaign);
                            campaignFacade.update(campaign);
                            tagFacade.update(tag);
                        }
                    }
                }

                //Setting up the PubNubKey
                if (selectedPubNubKey != null && selectedPubNubKey.getChannel() != null && !selectedPubNubKey.getChannel().isEmpty()) {
                    selectedPubNubKey = pubNubMgr.getPubNubKeyByChannel(selectedPubNubKey.getChannel(), campaign.getCompany());
                    if (!selectedPubNubKey.getCampaigns().contains(campaign)) {
                        campaign.addPubNubKey(selectedPubNubKey);
                        selectedPubNubKey.addCampaign(campaign);
                        campaignFacade.update(campaign);
                        pubNubMgr.update(selectedPubNubKey);
                    }
                }

                JsfUtil.addSuccessMessage(message.getString("campaignUpdated"));
                return cancelToList();
            }
        } catch (FileNotFoundException ex) {
            JsfUtil.addErrorMessage(ex, message.getString("pleaseUploadFile"));
            return null;
        } catch (NullPointerException ei) {
            JsfUtil.addErrorMessage(ei, "NullPointerException!");
            ei.printStackTrace();
            return null;
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, message.getString("persistenceError") + " " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private void prepareVars() {
        createdCampaign = new Campaign();
        daysOfWeek = new ArrayList<String>();
        monday = true;
        tuesday = true;
        wednesday = true;
        thursday = true;
        friday = true;
        saturday = true;
        sunday = true;
        wifi = null;
        bt = null;
        domain = new HotspotDomain();
        domains = new ArrayList<HotspotDomain>();
        wifiFileName = "";
        wifiCondition = true;
        bluetoothCondition = true;
        createdCampaign.setActive(true);
        files = new ArrayList<String>();
        fileModel = new FileDataModel();
        domainModel = null;
        phonePreviewPath = null;
        selectedTags = new ArrayList<TagChooser>();
        listTags = null;
        selectedPubNubKey = null;
        pubNubList = null;
        generatedContent = false;
        hasFacebookContent = false;
    }

    public String cancelToList() {
        try {
            FileUtils.deleteDirectory(new File(CAMPAIGN_TMP_DIR + ServerURISettings.OS_SEP + tmpFolder));
        } catch (IOException ex) {
        }
        prepareVars();
        return prepareList();
    }

    public String deleteCampaign() {
        if (selectedCampaign != null) {
            /*
             * Uncomment if we want to delete campaign content
             *
             * BluetoothCampaign bluetoothCampaign =
             * selectedCampaign.getBluetoothCampaign(); if (bluetoothCampaign !=
             * null) { CampaignFile btType =
             * campaignFileManager.getFileByType(bluetoothCampaign); if (btType
             * != null) { campaignFileManager.delete(btType); }
             * campaignFacade.deleteCampaignType(bluetoothCampaign);
             * selectedCampaign.removeCampaignType(bluetoothCampaign); }
             * WifiCampaign wifiCampaign = selectedCampaign.getWifiCampaign();
             * if (wifiCampaign != null) { CampaignFile wifiType =
             * campaignFileManager.getFileByType(wifiCampaign); if (wifiType !=
             * null) { campaignFileManager.delete(wifiType); }
             * campaignFacade.deleteCampaignType(wifiCampaign);
             * selectedCampaign.removeCampaignType(wifiCampaign); }
             *
             */
            List<Tag> tags = new ArrayList<Tag>();
            for (Tag t : selectedCampaign.getTags()) {
                tags.add(t);
            }
            for (Tag t : tags) {
                selectedCampaign.removeTag(t);
                t.removeCampaign(selectedCampaign);
                tagFacade.update(t);
            }
            selectedCampaign.setActive(false);
            selectedCampaign.setDeleted(true);
            campaignFacade.update(selectedCampaign);
        }

        return prepareList();
    }

    public void removeImageFile() {
        if (this.selectedFile == null || fileModel == null) {
            return;
        }
        selectedFile.closeContent();
        String serverPath = CAMPAIGN_TMP_DIR + ServerURISettings.OS_SEP + tmpFolder + ServerURISettings.OS_SEP + (new BluetoothCampaign().getType()) + ServerURISettings.OS_SEP;
        try {
            //Logically delete the file from the datamodel
            fileModel.removeFile(selectedFile, serverPath);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        for (String file : this.files) {
            if (file.endsWith(selectedFile.getName())) {
                removeFile(file);
                String ext = FilenameUtils.getExtension(file);
                if (ext.equalsIgnoreCase("zip")) {
                    this.wifiFileName = "";
                }
            }
        }
        populateFileModel();
    }

    /**
     * Actually delete the file from the filesystem
     *
     * @param filepath
     */
    public void removeFile(String filepath) {
        if (filepath != null && !filepath.isEmpty() && this.files != null && this.files.size() > 0) {
            try {
                File f = new File(CAMPAIGN_TMP_DIR + ServerURISettings.OS_SEP + tmpFolder + ServerURISettings.OS_SEP + filepath);
                try {
                    FileDeleteStrategy.FORCE.delete(f);
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        File f2 = new File(f.getParent() + ServerURISettings.OS_SEP + "toDelete");
                        FileUtils.moveFile(f, f2);
                        FileDeleteStrategy.FORCE.delete(f2);
                    } catch (Exception ie) {
                        ie.printStackTrace();
                    }
                }
                boolean exists = f.exists();
                if (exists) {
                    Thread.sleep(1);
                    boolean secondDeleted = FileUtils.deleteQuietly(f);
                }

                if (this.files.isEmpty()) {
                    File tempFolder = new File(CAMPAIGN_TMP_DIR + ServerURISettings.OS_SEP + tmpFolder);
                    FileUtils.deleteDirectory(tempFolder);
                }
            } catch (Exception ex) {
                JsfUtil.addErrorMessage(ex, message.getString("unableToRemoveFile") + " " + filepath);
                ex.printStackTrace();
            }
        }
    }

    private void createPhoneTemplate(String serverPath) throws IOException {
        //Now Copy the Embedded template

        File ignoreDir = new File(serverPath + ServerURISettings.OS_SEP + "_ignore" + ServerURISettings.OS_SEP + "embed");
        if (phonePreviewPath == null || phonePreviewPath.isEmpty()) {
            phonePreviewPath = serverPath + ServerURISettings.OS_SEP + "_ignore";
        }
        if (ignoreDir.exists()) {
            return;
        }
        ignoreDir.mkdirs();
        File previewCss = new File(ignoreDir, "preview.css");
        File phone = new File(ignoreDir, "phone.html");
        File phoneImg = new File(ignoreDir, "preview.png");
        FileUtils.copyInputStreamToFile(WizardControllerTemplateOne.class.getResourceAsStream("/resources/contentgenerator/templates/embed/preview.css"), previewCss);
        FileUtils.copyInputStreamToFile(WizardControllerTemplateOne.class.getResourceAsStream("/resources/contentgenerator/templates/embed/phone.html"), phone);
        FileUtils.copyInputStreamToFile(WizardControllerTemplateOne.class.getResourceAsStream("/resources/contentgenerator/templates/embed/preview.png"), phoneImg);

    }

    public void handleFileUploadFromContentGenerator(File file) {
        try {
            String filename = file.getName();
            String type;
            String ext = FilenameUtils.getExtension(file.getName());
            if (ext.equalsIgnoreCase("zip")) {
                type = new WifiCampaign().getType();
                if (wifiFileName != null && !wifiFileName.isEmpty()) {
                    removeFile("" + type + ServerURISettings.OS_SEP + wifiFileName);
                }
                wifiFileName = filename;
            } else {
                type = new BluetoothCampaign().getType();
            }
            String serverPath = CAMPAIGN_TMP_DIR + ServerURISettings.OS_SEP + tmpFolder + ServerURISettings.OS_SEP + type;
            new File(serverPath).mkdirs();
            File f = new File(serverPath + ServerURISettings.OS_SEP + filename);
            FileUtils.copyFile(file, f);
            files.add(type + ServerURISettings.OS_SEP + filename);
            populateFileModel();
        } catch (IOException ex) {
            ex.printStackTrace();
            if (FacesContext.getCurrentInstance() != null) {
                JsfUtil.addErrorMessage(message.getString("unableToUploadFile"));
            }
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        try {
            //Strip down UpperCase Extension and White Space
            boolean isZipFile = false;
            String filename = event.getFile().getFileName().replaceAll(" ", "_");
            filename = filename.substring(0, filename.lastIndexOf(".")) + "." + filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
            String type;
            String ext = FilenameUtils.getExtension(filename);
            if (ext.equalsIgnoreCase("zip")) {
                isZipFile = true;
                if (generatedContent) {
                    JsfUtil.addErrorMessage(message.getString("cantUploadZipFileOnGeneratedContent"));
                    return;
                }
                type = new WifiCampaign().getType();
                if (wifiFileName != null && !wifiFileName.isEmpty()) {
                    removeFile("" + type + ServerURISettings.OS_SEP + wifiFileName);
                }
                wifiFileName = filename;
            } else {

                type = new BluetoothCampaign().getType();
            }
            InputStream inputStream = event.getFile().getInputstream();
            String serverPath = CAMPAIGN_TMP_DIR + ServerURISettings.OS_SEP + tmpFolder + ServerURISettings.OS_SEP + type;
            new File(serverPath).mkdirs();
            File f = new File(serverPath + ServerURISettings.OS_SEP + filename);
            if (f.exists()) {
                JsfUtil.addErrorMessage(message.getString("duplicateNameUpload"));
                return;
            }
            if (event.getFile().getSize() < (KILOBYTE) / 2) {
                if (ext.equalsIgnoreCase("zip")) {
                    JsfUtil.addErrorMessage(message.getString("zipFileEmpty"));
                    return;
                } else {
                    JsfUtil.addErrorMessage(message.getString("fileTooSmall"));
                    return;
                }
            }
            //If file exist then return and put a warning that it has the same name
            FileUtils.copyInputStreamToFile(inputStream, f);
            files.add(type + ServerURISettings.OS_SEP + filename);
            if (isZipFile) {
                //Unzip into phonePreviewPath
                if (phonePreviewPath != null) {
                    if (!phonePreviewPath.isEmpty() && new File(phonePreviewPath).exists()) {
                        FileUtils.deleteDirectory(new File(phonePreviewPath));
                    }
                }
                this.createPhoneTemplate(serverPath);
                FileUtils.copyFile(f, new File(phonePreviewPath, filename));
                /**
                 * Added new unzip special
                 */
                String newBasePath = ZipUnzip.findIndexBasePath(phonePreviewPath, wifiFileName);
                ZipUnzip.UnzipSpecial(phonePreviewPath, newBasePath, wifiFileName);
                //ZipUnzip.Unzip(basePath, wifiFileName);
            } else {
                //add the filename to the bt.con properties file (bluetooth ordering)
                this.fileModel.writeToBtConf(filename);
            }
            populateFileModel();
        } catch (Exception ex) {
            ex.printStackTrace();
            JsfUtil.addErrorMessage(message.getString("unableToUploadFile"));
            
        }
    }

    public String prepareGenerator() {

        prepareVars();
        tmpFolder = this.getHttpSession().getUserUUIDTmp() + ServerURISettings.OS_SEP + "create";
        populateFileModel();
        populateDomainModel();
        contentGeneratorController.changeTemplate();

        return "/contentgenerator/Generator?faces-redirect=true";
    }

    public String reloadContentGenerator() {
        return "/contentgenerator/Generator?faces-redirect=true";
    }

    public String prepareCreate() {
        prepareVars();
        tmpFolder = this.getHttpSession().getUserUUIDTmp() + ServerURISettings.OS_SEP + "create";
        populateFileModel();
        populateDomainModel();
        return "/campaign/Create?faces-redirect=true";
    }

    public void updateDayCheckboxes(String[] days) {
        monday = false;
        tuesday = false;
        wednesday = false;
        thursday = false;
        friday = false;
        saturday = false;
        sunday = false;
        for (String day : days) {
            if (day.equalsIgnoreCase("M")) {
                monday = true;
            }
            if (day.equalsIgnoreCase("T")) {
                tuesday = true;
            }
            if (day.equalsIgnoreCase("W")) {
                wednesday = true;
            }
            if (day.equalsIgnoreCase("R")) {
                thursday = true;
            }
            if (day.equalsIgnoreCase("F")) {
                friday = true;
            }
            if (day.equalsIgnoreCase("S")) {
                saturday = true;
            }
            if (day.equalsIgnoreCase("U")) {
                sunday = true;
            }
        }
    }

    /**
     * Method that gets call to prepare the Campaign Creation from within the
     * Content Generator
     *
     * @return
     */
    public String prepareFromContentGenerator() {
        prepareVars();
        if (contentGeneratorController != null) {
            hasFacebookContent = contentGeneratorController.isFacebookContent();

        }
        String filename = ServerURISettings.CONTENT_GENERATOR_TMP_DIR + ServerURISettings.OS_SEP + this.getHttpSession().getUserUUIDTmp() + ServerURISettings.OS_SEP + ContentGeneratorController.ZIP_CONTENT_NAME;
        File fileToUpload = new File(filename);

        generatedContent = true;
        tmpFolder = this.getHttpSession().getUserUUIDTmp() + ServerURISettings.OS_SEP + "create";
        handleFileUploadFromContentGenerator(fileToUpload);
        return "/campaign/Create?faces-redirect=true";
    }

    public String editCampaign() {
        files = new ArrayList<String>();
        tmpFolder = this.getHttpSession().getUserUUIDTmp() + ServerURISettings.OS_SEP + "edit";
        updateDayCheckboxes(selectedCampaign.getDayOfWeek().split(","));
        wifiCondition = false;
        bluetoothCondition = false;

        generatedContent = selectedCampaign.getGeneratedContent();


        /*
         * 1. copy bluetooth zip to a tmp dir 2. extract the bluetooth files 3.
         * populate the list 4. upload or delete files 5. rezip on save
         */
        this.bt = selectedCampaign.getBluetoothCampaign();
        if (bt != null) {
            CampaignFile cfBluetooth = campaignFileManager.getFileByType(this.bt);
            if (cfBluetooth != null) {
                this.bluetoothCondition = true;
                File tmpBtType = new File(CAMPAIGN_TMP_DIR + ServerURISettings.OS_SEP + tmpFolder + ServerURISettings.OS_SEP + bt.getType());
                try {
                    FileUtils.deleteDirectory(tmpBtType);
                } catch (IOException ex) {
                    //ignore
                }
                tmpBtType.mkdirs();

                String btZip = FilenameUtils.getName(cfBluetooth.getServerPath());
                try {
                    FileUtils.copyFileToDirectory(new File(cfBluetooth.getServerPath()), tmpBtType);
                } catch (IOException ex) {
                    JsfUtil.addErrorMessage(message.getString("unableRecoverBluetoothFile"));
                    return "";
                }
                ZipUnzip.Unzip(tmpBtType.getAbsolutePath(), btZip);
                FileUtils.deleteQuietly(new File(tmpBtType.getAbsolutePath(), btZip));
                File[] tfiles = tmpBtType.listFiles();
                for (File file : tfiles) {
                    if (!file.isDirectory() || !FilenameUtils.isExtension(file.getName(), "zip")) {
                        this.files.add("" + bt.getType() + ServerURISettings.OS_SEP + file.getName());
                    }
                }
            } else {
                JsfUtil.addErrorMessage(message.getString("unableRecoverBluetoothFile"));
            }
        }

        /*
         * wifi stuff
         */
        this.wifi = selectedCampaign.getWifiCampaign();

        if (wifi != null) {

            hasFacebookContent = this.wifi.getHotSpotMode() == WifiCampaign.FACEBOOK;

            CampaignFile cfWifi = campaignFileManager.getFileByType(this.wifi);
            if (cfWifi != null) {
                wifiFileName = FilenameUtils.getName(cfWifi.getServerPath());
                wifiCondition = true;
                File tmpWifiType = new File(CAMPAIGN_TMP_DIR + ServerURISettings.OS_SEP + tmpFolder + ServerURISettings.OS_SEP + wifi.getType());
                phonePreviewPath = CAMPAIGN_TMP_DIR + ServerURISettings.OS_SEP + tmpFolder + ServerURISettings.OS_SEP + wifi.getType() + ServerURISettings.OS_SEP + "_ignore";
                tmpWifiType.mkdirs();
                try {
                    File f = new File(cfWifi.getServerPath());
                    FileUtils.copyFileToDirectory(f, tmpWifiType);
                    //Create Phone Previewing Files as Well
                    //Unzip into phonePreviewPath
                    if (!generatedContent) {
                        if (phonePreviewPath != null) {
                            if (!phonePreviewPath.isEmpty() && new File(phonePreviewPath).exists()) {
                                FileUtils.deleteDirectory(new File(phonePreviewPath));
                            }
                        }
                        this.createPhoneTemplate(CAMPAIGN_TMP_DIR + ServerURISettings.OS_SEP + tmpFolder + ServerURISettings.OS_SEP + wifi.getType());
                        FileUtils.copyFile(f, new File(phonePreviewPath, wifiFileName));
                        /**
                         * Added new unzip special
                         */
                        String newBasePath = ZipUnzip.findIndexBasePath(phonePreviewPath, wifiFileName);
                        ZipUnzip.UnzipSpecial(phonePreviewPath, newBasePath, wifiFileName);
                    }
                } catch (Exception ex) {
                    JsfUtil.addErrorMessage(message.getString("unableRecoverWifiFiles"));
                    return prepareList();
                }
                this.files.add("" + wifi.getType() + ServerURISettings.OS_SEP + wifiFileName);
            } else {
                JsfUtil.addErrorMessage(message.getString("unableRecoverWifiFiles"));
            }
            if (wifi.getHotSpotMode() == 2) {
                domains = wifi.getHotspotDomains();
                populateDomainModel();
            }
        }

        //Setting up the Group Tags
        this.listTags = null;
        List<Tag> tags = selectedCampaign.getTags();

        List<TagChooser> listNames = new ArrayList<TagChooser>();
        if (tags != null && !tags.isEmpty()) {
            for (Tag t : tags) {
                listNames.add(new TagChooser(t.getName(), true));
            }
        }
        List<TagChooser> listOfTags = this.getListTags();
        for (TagChooser t : listOfTags) {
            for (TagChooser toLoad : listNames) {
                if (t.getName().equals(toLoad.getName())) {
                    t.setChosen(true);
                }
            }
        }
        this.setListTags(listOfTags);
        this.setSelectedTags(null);

        //TODO Set up the PubNubKey
        this.pubNubList = null;
        List<PubNubKey> pubNubKeys = selectedCampaign.getPubNubKeys();
        if (pubNubKeys != null && !pubNubKeys.isEmpty()) {
            selectedPubNubKey = pubNubKeys.get(0);
        }

        populateFileModel();
        return "/campaign/Edit?faces-redirect=true";
    }

    public String prepareList() {
        populateCampaignModel();
        return "/campaign/List?faces-redirect=true";
    }

    public void populateFileModel() {
        if (fileModel == null) {
            fileModel = new FileDataModel();
        }
        String btPath = CAMPAIGN_TMP_DIR + ServerURISettings.OS_SEP + tmpFolder + ServerURISettings.OS_SEP + new BluetoothCampaign().getType();
        File tempFolder = new File(btPath);
        File tempFile = null;
        if (this.wifiFileName != null && !this.wifiFileName.isEmpty()) {
            tempFile = new File(CAMPAIGN_TMP_DIR + ServerURISettings.OS_SEP + tmpFolder + ServerURISettings.OS_SEP + new WifiCampaign().getType() + ServerURISettings.OS_SEP + wifiFileName);
        }
        List<File> fileList = new ArrayList<File>();
        List<File> resultList = new ArrayList<File>();
        this.files.clear();
        if (tempFolder.exists()) {
            fileList.addAll(Arrays.asList(tempFolder.listFiles()));
            try {
                //Ordering the Bluetooth Files according to the metadata properties
                File btProps = new File(btPath + ServerURISettings.OS_SEP + fileModel.BT_RPOPERTIES_FILE);
                BufferedReader reader = new BufferedReader(new FileReader(btProps));
                String curr;
                while ((curr = reader.readLine()) != null) {
                    for (File file : fileList) {
                        if (file.getName().equals(curr.trim())) {
                            this.files.add("" + new BluetoothCampaign().getType() + ServerURISettings.OS_SEP + file.getName());
                            resultList.add(file);
                            break;
                        }
                    }
                }
                reader.close();
            } catch (Exception ex) {
                System.err.println(ex);
            }
        }
        if (tempFile != null && tempFile.exists()) {
            resultList.add(tempFile);
            wifiFileName = tempFile.getName();
            this.files.add("" + new WifiCampaign().getType() + ServerURISettings.OS_SEP + wifiFileName);
        }
        fileModel = new FileDataModel(btPath);
        fileModel.setFiles(resultList);
        fileModel.setRowIndex(-1);
    }

    public FileDataModel getFileModel() {
        return fileModel;
    }

    public void setFileModel(FileDataModel fileModel) {
        this.fileModel = fileModel;
    }

    public String getPhonePreviewPath() {
        return phonePreviewPath;
    }

    public void setPhonePreviewPath(String phonePreviewPath) {
        this.phonePreviewPath = phonePreviewPath;
    }

    public void closeSelectedFileContent() {
        if (this.selectedFile != null) {
            this.selectedFile.closeContent();
        }
    }

    public StreamedContent downloadWifi() {
        try {
            if (wifiFileName != null && !wifiFileName.isEmpty()) {
                String type = new WifiCampaign().getType();
                String serverPath = CAMPAIGN_TMP_DIR + ServerURISettings.OS_SEP + tmpFolder + ServerURISettings.OS_SEP + type;
                File f = new File(serverPath + ServerURISettings.OS_SEP + this.wifiFileName);
                if (f != null) {
                    InputStream stream = new FileInputStream(f);
                    return new DefaultStreamedContent(stream, "application/octet-stream", this.wifiFileName);
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void moveUp(ImageFile file) {
        List<ImageFile> fileModelList = (ArrayList<ImageFile>) fileModel.getWrappedData();
        //If Only one element or is already on the top
        if (this.fileModel.getRowCount() <= 1 || this.fileModel.getRowIndex() == 0) {
            return; //No need to move
        }
        int targetIndex = this.fileModel.getRowIndex();
        //Swapping values
        ImageFile temp = fileModelList.get(targetIndex - 1);
        fileModelList.set(targetIndex - 1, fileModelList.get(targetIndex));
        fileModelList.set(targetIndex, temp);
        fileModel.setWrappedData(fileModelList);
        fileModel.rewriteBtConf();
    }

    public void moveDown(ImageFile file) {
        List<ImageFile> fileModelList = (ArrayList<ImageFile>) fileModel.getWrappedData();
        //If Only one element or is already on the bottom
        if (this.fileModel.getRowCount() <= 1 || this.fileModel.getRowIndex() == this.fileModel.getRowCount() - 1) {
            return; //No need to move
        }
        int targetIndex = this.fileModel.getRowIndex();
        //Swapping values
        ImageFile temp = fileModelList.get(targetIndex + 1);
        fileModelList.set(targetIndex + 1, fileModelList.get(targetIndex));
        fileModelList.set(targetIndex, temp);
        fileModel.setWrappedData(fileModelList);
        fileModel.rewriteBtConf();
    }

    public void selectAllTags() {
        for (TagChooser n : getListTags()) {
            n.setChosen(true);
        }

    }

    public void unselectAllTags() {
        for (TagChooser n : getListTags()) {
            n.setChosen(false);
        }
    }

    public class TagChooser {

        private String name;
        private boolean chosen;

        public TagChooser(String name, boolean chosen) {
            this.name = name;
            this.chosen = chosen;
        }

        public boolean giveMeChosen() {
            return chosen;
        }

        public boolean isChosen() {
            return chosen;
        }

        public boolean getChosen() {
            return chosen;
        }

        public void setChosen(boolean chosen) {
            this.chosen = chosen;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public boolean canDownloadWifi() {
        return (wifiFileName != null && !wifiFileName.isEmpty());
    }

    public PubNubKey getSelectedPubNubKey() {
        if (selectedPubNubKey == null) {
            selectedPubNubKey = new PubNubKey();
        }
        return selectedPubNubKey;
    }

    public void setSelectedPubNubKey(PubNubKey selectedPubNubKey) {
        this.selectedPubNubKey = selectedPubNubKey;
    }

    public List<PubNubKey> getPubNubList() {
        if (pubNubList == null) {
            populatePubNubList();
        }
        return pubNubList;
    }

    public void setPubNubList(List<PubNubKey> pubNubList) {
        this.pubNubList = pubNubList;
    }

    public void populatePubNubList() {
        this.pubNubList = pubNubMgr.findAllByCompany(companyFacade.getCompanybyId(this.getCompanyIdFromSession()));
    }

    public boolean isGeneratedContent() {
        return generatedContent;
    }

    public void setGeneratedContent(boolean generatedContent) {
        this.generatedContent = generatedContent;
    }

    public ContentGeneratorController getContentGeneratorController() {
        return contentGeneratorController;
    }

    public void setContentGeneratorController(ContentGeneratorController contentGeneratorController) {
        this.contentGeneratorController = contentGeneratorController;
    }

    public boolean isHasFacebookContent() {
        return hasFacebookContent;
    }

    public void setHasFacebookContent(boolean hasFacebookContent) {
        this.hasFacebookContent = hasFacebookContent;
    }
}