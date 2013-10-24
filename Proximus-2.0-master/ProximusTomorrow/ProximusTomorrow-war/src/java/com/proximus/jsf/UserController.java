package com.proximus.jsf;

import com.proximus.bean.ImageFile;
import com.proximus.data.Company;
import com.proximus.data.CompanyLogo;
import com.proximus.data.Role;
import com.proximus.data.User;
import com.proximus.data.sms.Property;
import com.proximus.helper.util.JsfUtil;
import com.proximus.jsf.datamodel.UserDataModel;
import com.proximus.manager.CompanyLogoManagerLocal;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.RoleManagerLocal;
import com.proximus.manager.UserManagerLocal;
import com.proximus.manager.sms.PropertyManagerLocal;
import com.proximus.util.HashUtil;
import com.proximus.util.ListChoosers;
import com.proximus.util.ServerURISettings;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DualListModel;

@ManagedBean(name = "userController")
@SessionScoped
public class UserController extends AbstractController implements Serializable {

    private static final Logger logger = Logger.getLogger(UserController.class.getName());
    private UserDataModel userModel;
    private List<User> filteredUsers;
    @EJB
    private UserManagerLocal userMgr;
    @EJB
    CompanyManagerLocal companyFacade;
    @EJB
    CompanyLogoManagerLocal companyLogoMgr;
    @EJB
    RoleManagerLocal roleMgr;
    @EJB
    PropertyManagerLocal propMgr;
    private User newUser;
    private User selectedUser;
    private List<Company> listCompanies;
    private List<String> timezoneList = ListChoosers.getTimezones();
    private Company selectedCompany;
    private boolean changePassword;
    private String oldPassword;
    private String newPassword;
    private String verifyPassword;
    private final String EMAIL_REGEX = "^[\\w\\-]+(\\.[\\w\\-]+)*@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4}$";
    public final Pattern pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
    /**
     * LOGO PLACEMENT
     */
    protected String imagePath;
    private String selectedImage;
    private ImageFile imageFile;
    protected final long MEGABYTE = 1024 * 1024;
    protected String userUUIDPreview;
    protected String serverPath;
    protected boolean noLogo = false;
    private List<User> listOfUsers;
    private DualListModel<String> picklistModel;
    private List<String> picklistSource;
    private List<String> picklistTarget;
    private boolean hasImageChanged = false;
    private ResourceBundle message;

    public UserController() {
        message = this.getHttpSession().getMessages();
    }

    public User getNewUser() {
        if (newUser == null) {
            newUser = new User();
        }
        return newUser;
    }

    public String getVerifyPassword() {
        return verifyPassword;
    }

    public void setVerifyPassword(String verifyPassword) {
        this.verifyPassword = verifyPassword;
    }

    public String getNewPassword() {
        return newPassword;

    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public Company getSelectedCompany() {
        if (this.selectedCompany == null) {
            this.selectedCompany = new Company();
        }
        return selectedCompany;
    }

    public void setSelectedCompany(Company selectedCompany) {
        this.selectedCompany = selectedCompany;
    }

    public List<Company> getListCompanies() {
        if (listCompanies == null) {
            this.populateListCompany();
        }
        return listCompanies;
    }

    public void setListCompanies(List<Company> listCompanies) {
        this.listCompanies = listCompanies;
    }

    public List<String> getTimezoneList() {
        return timezoneList;
    }

    public void setTimezoneList(List<String> timezoneList) {
        this.timezoneList = timezoneList;
    }

    public void setNewUser(User newUser) {
        this.newUser = newUser;
    }

    public User getSelectedUser() {
        if (selectedUser == null) {
            selectedUser = new User();
        }
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }

    public UserDataModel getUserModel() {
        if (this.userModel == null) {
            populateUserModel();
        }

        return this.userModel;
    }

    public void setUserModel(UserDataModel userModel) {
        this.userModel = userModel;
    }

    public List<User> getFilteredUsers() {
        return filteredUsers;
    }

    public void setFilteredUsers(List<User> filteredUsers) {
        this.filteredUsers = filteredUsers;
    }

    /**
     * In order to populate the User Model we must check if the current User's
     * priority is HIGHER than the list of Users. Remember that LOW Priority
     * Number means HIGHER PRIORITY I.E. Super User has priority 1... and
     * Company Viewer has priority 3
     */
    public void populateUserModel() {
        User currUser = userMgr.find(this.getHttpSession().getUser_id());
        List<User> users = companyFacade.getUsersFromCompany(getCompanyIdFromSession());
        List<User> newUsers = new ArrayList<User>();
        for (User u : users) {
            if (u.getRole() != null && u.getRole().getPriority() < currUser.getRole().getPriority()) {
                continue;
                //either there was no role assigned or the priority is lower than current user    
            } else {
                newUsers.add(u);
            }
        }
        userModel = new UserDataModel(newUsers);
        filteredUsers = new ArrayList<User>(newUsers);


    }

    private UserManagerLocal getUserMgr() {
        return userMgr;
    }

    public String prepareList() {
        this.newUser = new User();
        this.userModel = null;
        this.listCompanies = null;
        this.selectedCompany = null;
        this.selectedUser = null;
        this.hasImageChanged = false;
        return "/user/List?faces-redirect=true";
    }

    public String prepareUserToProperty() {
        this.selectedUser = null;
        this.listOfUsers = null;
        this.picklistModel = null;
        this.picklistSource = null;
        this.picklistTarget = null;
        this.hasImageChanged = false;
        return "/user/UserProperty?faces-redirect=true";
    }

    public boolean isChangePassword() {
        return changePassword;
    }

    public void setChangePassword(boolean changePassword) {
        this.changePassword = changePassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public void deleteUser() {
        if (!getHttpSession().getUsername().equals(selectedUser.getUserName())) {
            userMgr.delete(selectedUser);
            prepareList();
        } else {

            JsfUtil.addErrorMessage(message.getString("cannotDeleteCurrUser"));
            prepareList();
        }
    }

    public void populateListCompany() {
        listCompanies = userMgr.getCompaniesFromUser(getUserIdFromSession());
        Collections.sort(listCompanies, new Comparator<Company>() {

            @Override
            public int compare(Company o1, Company o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }

    public List<String> companyComplete(String query) {
        query = query.toLowerCase();
        if (listCompanies == null) {
            populateListCompany();
        }
        query = query.toLowerCase();
        List<String> filtered = new ArrayList<String>();
        for (Company c : listCompanies) {
            String s = c.getName().toLowerCase();
            if (s.contains(query)) {
                filtered.add(c.getName());
            }
        }
        return filtered;
    }

    public void changeCompany(Company company) {


        if (company == null || company.getName() == null || company.getName().isEmpty()) {
            return;
        }
        if (this.imageFile != null) {
            this.imageFile.closeContent();
        }
        Company realCompany = companyFacade.getCompanybyName(company.getName());
        if (realCompany != null) {
            getHttpSession().setCompany_id(realCompany.getId());
            getHttpSession().setCompany_name(realCompany.getName());
            getHttpSession().setHasGeoFenceLicense(realCompany.getLicense().hasGeofence());
            getHttpSession().setHasProximityLicense(realCompany.getLicense().hasProximity());
            getHttpSession().setHasNotificationLicense(realCompany.getLicense().hasPubNubNotification());
            getHttpSession().setHasContentGeneratorLicense(realCompany.getLicense().hasContentGenerator());
            getHttpSession().populateCompanyLogo();
            if (getHttpSession().getCompanyLogo() != null) {
                File f = new File(getHttpSession().getCompanyLogo().getServerPath());
                if (f.exists() && f.isFile()) {
                    this.imageFile = new ImageFile(f, "");
                    this.selectedImage = getHttpSession().getCompanyLogo().getFilename();
                    noLogo = false;
                } else {
                    this.imageFile = null;
                    this.selectedImage = null;
                    noLogo = true;
                    logger.error("Company has custom logo but the file does not exist: " + f.getAbsolutePath());
                }
            } else {
                this.imageFile = null;
                this.selectedImage = null;
                noLogo = true;
            }
            JsfUtil.addSuccessMessage(message.getString("companyChanged"));
        }
    }

    private boolean isValidEmailAddress(String emailAddress) {
        CharSequence inputStr = emailAddress;
        return pattern.matcher(inputStr).matches();

    }

    public void createNewUser() {
        RequestContext context = RequestContext.getCurrentInstance();
        boolean validation = false;
        if (newUser == null || newUser.getUserName().isEmpty() || !isValidEmailAddress(newUser.getUserName())) {
            validation = false;
            JsfUtil.addErrorMessage(message.getString("usernameValidEmail"));
            context.addCallbackParam("validation", validation);
            return;
        }

        if (newPassword == null || newPassword.isEmpty() || newPassword.length() < 8) {
            validation = false;
            JsfUtil.addErrorMessage(message.getString("passValidation"));
            context.addCallbackParam("validation", validation);
            return;

        }
        if (verifyPassword == null || verifyPassword.isEmpty() || !newPassword.equals(verifyPassword)) {
            validation = false;
            JsfUtil.addErrorMessage(message.getString("passMismatch"));
            context.addCallbackParam("validation", validation);
            return;
        }

        Company companyById = companyFacade.find(getCompanyIdFromSession());
        newUser.addCompany(companyById);
        newUser.setPassword(HashUtil.getEncodedPassword(newUser.getUserName(), newPassword));
        User u = userMgr.getUserByUsername(newUser.getUserName());
        if (u != null) {
            validation = false;
            context.addCallbackParam("validation", validation);
            JsfUtil.addErrorMessage(message.getString("email") + " " + newUser.getUserName() + " " + message.getString("alreadyExists"));
            return;
        }



        userMgr.create(newUser);
        validation = true;
        context.addCallbackParam("validation", validation);
        JsfUtil.addSuccessMessage(message.getString("userCreated"));
        prepareList();
    }

    public String setUserSettings(String username) {
        if (username == null || username.isEmpty()) {
            username = getHttpSession().getUsername();
        }
        hasImageChanged = false;
        changePassword = false;
        this.selectedImage = null;
        selectedUser = getUserMgr().getUserByUsername(username);

        //Preparing Image Templating
        //Before setting up everything lets delete folders if they exist
        if (serverPath != null) {
            try {
                if (imageFile != null) {
                    imageFile.closeContent();
                }
                FileUtils.deleteDirectory(new File(serverPath));
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
        this.userUUIDPreview = this.getUserIdFromSession() + "_" + UUID.randomUUID().toString();
        serverPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("//tmp") + ServerURISettings.OS_SEP + this.userUUIDPreview + ServerURISettings.OS_SEP;
        imagePath = serverPath + "images" + ServerURISettings.OS_SEP;
        CompanyLogo logo = companyLogoMgr.getByCompany(companyFacade.getCompanybyId(this.getCompanyIdFromSession()));
        noLogo = true;
        if (logo != null) {
            File realFile = new File(logo.getServerPath());
            if (realFile.exists() && realFile.isFile()) {
                this.imageFile = new ImageFile(realFile, "");
                this.selectedImage = this.imageFile.getName();
                noLogo = false;
            }
        }

        return "/user/Edit?faces-redirect=true";

    }

    public void editUser() {
        RequestContext context = RequestContext.getCurrentInstance();

        boolean validation = false;
        if (selectedUser.getFirstName() == null || selectedUser.getFirstName().isEmpty()) {
            JsfUtil.addErrorMessage(message.getString("typeFirstName"));
            context.addCallbackParam("validation", validation);
            return;
        }
        if (selectedUser.getLastName() == null || selectedUser.getLastName().isEmpty()) {
            JsfUtil.addErrorMessage(message.getString("typeLastName"));
            context.addCallbackParam("validation", validation);
            return;
        }
        userMgr.update(selectedUser);
        validation = true;
        context.addCallbackParam("validation", validation);
    }

    public void removeLogo() {
        if (imageFile != null) {
            imageFile.closeContent();
            imageFile = null;
        }
        imagePath = null;
        selectedImage = null;
        imageFile = null;
        noLogo = true;
        hasImageChanged = true;
    }

    /**
     * Method that gets called when a user edits its own information through
     * settings
     *
     * @param user
     * @return
     */
    public String editUserSettings(User user) {
        if (user.getFirstName() == null || user.getFirstName().isEmpty()) {
            JsfUtil.addErrorMessage(message.getString("firstNameEmpty"));
            return "/user/Edit";
        }
        if (user.getLastName() == null || user.getLastName().isEmpty()) {
            JsfUtil.addErrorMessage(message.getString("lastNameEmpty"));
            return "/user/Edit";
        }

        //Check for Logo Change or not;
        if (hasImageChanged) {
            CompanyLogo logo = companyLogoMgr.getByCompany(this.getCompanyFromSession());
            if (selectedImage != null && !selectedImage.isEmpty()) {
                String realLogoPath = ServerURISettings.LOGOS_ROOT_DIR + ServerURISettings.OS_SEP + getHttpSession().getCompany_id();
                new File(realLogoPath).mkdirs();

                String destFile = realLogoPath + ServerURISettings.OS_SEP + selectedImage;

                if (logo == null) {
                    logo = new CompanyLogo();
                    logo.setCompany(this.getCompanyFromSession());
                } else {
                    //Delete old Logo
                    FileUtils.deleteQuietly(new File(logo.getServerPath()));
                }
                logo.setFilename(selectedImage);
                logo.setServerPath(destFile);


                try {
                    FileUtils.copyFile(new File(imagePath + selectedImage), new File(destFile));
                } catch (IOException ex) {
                    logger.fatal(ex);
                }

                companyLogoMgr.update(logo);
                if (logo != null) {
                    companyLogoMgr.delete(logo);
                    logo = null;
                }
                getHttpSession().setCompanyLogo(logo);

            }
            if (selectedImage == null) {
                if (logo != null) {
                    companyLogoMgr.delete(logo);
                    logo = null;
                }
                getHttpSession().setCompanyLogo(logo);
            }
        }


        if (changePassword) {
            if (oldPassword != null && !oldPassword.isEmpty()) {
                if (!oldPassword.equals(getHttpSession().getPassword())) {
                    JsfUtil.addErrorMessage(message.getString("oldPasswordIncorrect"));
                    return "/user/Edit";
                }
            } else {
                JsfUtil.addErrorMessage(message.getString("oldPasswordMessage"));
                return "/user/Edit";
            }

            if (newPassword == null || newPassword.isEmpty() || newPassword.length() < 8) {
                JsfUtil.addErrorMessage(message.getString("newPasswordLength"));
                return "/user/Edit";
            }
            if (verifyPassword == null || verifyPassword.isEmpty() || !newPassword.equals(verifyPassword)) {
                JsfUtil.addErrorMessage(message.getString("passMismatch"));
                return "/user/Edit";
            }
            user.setPassword(HashUtil.getEncodedPassword(user.getUserName(), newPassword));
            userMgr.update(user);
            changePassword = false;
            getHttpSession().setFirst_name(user.getFirstName());
            getHttpSession().setLast_name(user.getLastName());
            getHttpSession().setPassword(user.getPassword());
            return "/home";
        }

        user.setLocale(getHttpSession().getSelectedLanguage().getValue());
        getHttpSession().reloadLocale();
        userMgr.update(user);
        changePassword = false;
        getHttpSession().setFirst_name(user.getFirstName());
        getHttpSession().setLast_name(user.getLastName());
        getHttpSession().setPassword(user.getPassword());
        return "/home";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(userMgr.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(userMgr.findAll(), true);
    }

    public List<User> returnUserList(String query) {
        return userMgr.findUserLike(query);
    }

    public void handleFileUpload(FileUploadEvent event) {
        try {
            if (imageFile != null) {
                imageFile.closeContent();
            }
            File imagePathDir = new File(imagePath);
            imagePathDir.mkdirs();
            //clear other images before copying the this one
            if (imagePathDir.isDirectory()) {
                File[] files = imagePathDir.listFiles();
                for (File file : files) {
                    FileUtils.deleteQuietly(file);
                }
            }
            String filename = event.getFile().getFileName().replaceAll(" ", "_");
            String suffix = filename.substring(filename.lastIndexOf('.'));
            if (suffix.startsWith(".")) {
                suffix = suffix.substring(1);
            }
            filename = "logo." + suffix;


            String tocheck = filename.toLowerCase();
            if (!tocheck.endsWith("jpg") && !tocheck.endsWith("jpeg") && !tocheck.endsWith("gif") && !tocheck.endsWith("png")) {
                JsfUtil.addErrorMessage(message.getString("invalidFileType"));

                hasImageChanged = false;
                return;
            }

            if (event.getFile().getSize() >= MEGABYTE) {
                JsfUtil.addErrorMessage(message.getString("invalidFileSize"));
                hasImageChanged = false;
                return;
            }

            InputStream inputStream = event.getFile().getInputstream();
            File f = new File(imagePath + filename);
            FileUtils.copyInputStreamToFile(inputStream, f);



            Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);

            int height = 0;
            if (iter.hasNext()) {
                ImageReader reader = iter.next();
                ImageInputStream stream = new FileImageInputStream(f);
                reader.setInput(stream);
                height = reader.getHeight(reader.getMinIndex());
                reader.dispose();
            }

            if (height > 100) {
                JsfUtil.addErrorMessage(message.getString("invalidDimension"));

                hasImageChanged = false;
                FileUtils.deleteQuietly(f);
                this.selectedImage = null;
                if (imageFile != null) {
                    imageFile.closeContent();
                }
                this.imageFile = null;
                return;
            }

            this.selectedImage = filename;

            if (imageFile != null) {
                imageFile.closeContent();
            }
            imageFile = new ImageFile(f, "");
            noLogo = false;
            hasImageChanged = true;
        } catch (IOException ex) {
            JsfUtil.addErrorMessage(message.getString("uploadFileError"));
            noLogo = true;
        }
    }

    public ImageFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(ImageFile imageFile) {
        this.imageFile = imageFile;
    }

    public String getSelectedImage() {
        return selectedImage;
    }

    public void setSelectedImage(String selectedImage) {
        this.selectedImage = selectedImage;
    }

    public String getUserUUIDPreview() {
        return userUUIDPreview;
    }

    public void setUserUUIDPreview(String userUUIDPreview) {
        this.userUUIDPreview = userUUIDPreview;
    }

    public boolean isNoLogo() {
        return noLogo;
    }

    public void setNoLogo(boolean noLogo) {
        this.noLogo = noLogo;
    }

    public List<User> getListOfUsers() {
        if (listOfUsers == null) {
            populateListOfUsers();
        }
        return listOfUsers;
    }

    public void setListOfUsers(List<User> listOfUsers) {
        this.listOfUsers = listOfUsers;
    }

    private void populateListOfUsers() {
        Role r = roleMgr.findByName("Property Manager");
        this.listOfUsers = userMgr.getUsersByRole(this.getCompanyFromSession(), r);
    }

    public DualListModel<String> getPicklistModel() {
        if (this.picklistModel == null) {
            populatePicklist();
        }
        return picklistModel;
    }

    public void setPicklistModel(DualListModel<String> picklistModel) {
        this.picklistModel = picklistModel;
    }

    public List<String> getPicklistSource() {
        if (this.picklistSource == null) {
            List<Property> allByCompany = propMgr.getPropertiesByCompany(this.getCompanyFromSession());
            if (allByCompany != null) {
                this.picklistSource = new ArrayList<String>();
                for (Property p : allByCompany) {
                    this.picklistSource.add(p.getName());
                }
                Iterator<String> it = this.picklistSource.iterator();
                while (it.hasNext()) {
                    String value = it.next();
                    if (getPicklistTarget().contains(value)) {
                        it.remove();
                    }
                }

            }
        }
        if (picklistSource != null) {
            Collections.sort(picklistSource);
        }
        return picklistSource;
    }

    public void setPicklistSource(List<String> picklistSource) {
        this.picklistSource = picklistSource;
    }

    public List<String> getPicklistTarget() {
        if (picklistTarget == null) {
            picklistTarget = new ArrayList<String>();
        }
        return picklistTarget;
    }

    public void setPicklistTarget(List<String> picklistTarget) {
        this.picklistTarget = picklistTarget;
    }

    private void populatePicklist() {
        setPicklistSource(null);
        this.picklistModel = new DualListModel<String>(getPicklistSource(), getPicklistTarget());
    }

    public void recreatePickList() {
        if(selectedUser == null || selectedUser.getUserName() == null || selectedUser.getUserName().isEmpty()) {
            this.picklistSource = new ArrayList<String>();
            this.picklistTarget = new ArrayList<String>();
        }
        else {
            User u = userMgr.getUserByUsername(selectedUser.getUserName());
            if (u != null) {
                this.picklistSource = null;
                this.picklistTarget = new ArrayList<String>();
                List<Property> props = u.getProperties();
                if (props != null) {
                    for (Property p : props) {
                        this.picklistTarget.add(p.getName());
                    }
                }
            }
        }
        populatePicklist();

    }

    private List<Property> getTargetProperties() {
        return getPropertiesFromNames(getPicklistTarget());
    }

    private List<Property> getPropertiesFromNames(List<String> names) {
        List<Property> result = new ArrayList<Property>();

        for (String propertyName : names) {
            Property p = propMgr.getPropertyByCompanyAndName(this.getCompanyFromSession(), propertyName);
            if (p != null) {
                result.add(p);
            }
        }
        return result;
    }

    public void saveUserToProperty() {
        /**
         * Persist Properties
         */
        if(this.selectedUser == null || this.selectedUser.getUserName() == null || this.selectedUser.getUserName().isEmpty()) {
            JsfUtil.addErrorMessage(message.getString("cantAssignPropertyToNoUsers"));
            return;
        }
        List<Property> targetProperties = getTargetProperties();
        this.selectedUser.clearProperties();
        userMgr.update(this.selectedUser);

        if (targetProperties != null) {
            for (Property p : targetProperties) {
                p.clearUsers();
                this.selectedUser.addProperty(p);
                p.addUser(this.selectedUser);
                userMgr.update(this.selectedUser);
                propMgr.update(p);

            }
        }
        JsfUtil.addSuccessMessage(message.getString("propertiesAssignedToUser") + ": " + this.selectedUser);

    }

    @FacesConverter(forClass = User.class, value = "userControllerConverter")
    public static class UserControllerConverter implements Converter, Serializable {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            try {
                Long id = Long.parseLong(value);

                User controller = ((UserController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "userController")).userMgr.find(id);
                return controller;
            } catch (NumberFormatException e) {
                //throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Value not found"));
                return null;
            }
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
            if (value == null || value.toString().isEmpty() || !(value instanceof User)) {
                return null;
            }
            return String.valueOf(((User) value).getId());
        }
    }
}