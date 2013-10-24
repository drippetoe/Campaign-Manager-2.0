/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf;

import com.proximus.data.Company;
import com.proximus.data.Privilege;
import com.proximus.data.Role;
import com.proximus.data.User;
import com.proximus.data.sms.Property;
import com.proximus.jsf.datamodel.PrivilegeDataModel;
import com.proximus.jsf.datamodel.RoleDataModel;
import com.proximus.jsf.datamodel.UserDataModel;
import com.proximus.jsf.util.JsfUtil;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.PrivilegeManagerLocal;
import com.proximus.manager.RoleManagerLocal;
import com.proximus.manager.UserManagerLocal;
import java.io.Serializable;
import java.util.*;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.model.DualListModel;

/**
 *
 * @author Ronald Williams
 */
@ManagedBean(name = "roleController")
@SessionScoped
public class RoleController extends AbstractController implements Serializable {

    private static final long serialVersionUID = 1;
    //GENERAL
    private String roleName;
    private RoleDataModel roleModel;
    private Role selectedRole;
    private Role newRole;
    @EJB
    RoleManagerLocal roleFacade;
    @EJB
    CompanyManagerLocal companyFacade;
    @EJB
    PrivilegeManagerLocal privilegeFacade;
    //Assign Privilege to Roles
    private Privilege newPrivilege;
    private Role rolePrivilege;
    private Privilege[] selectedPrivileges;
    private PrivilegeDataModel privilegeModel;
    @EJB
    UserManagerLocal userFacade;
    //Assign Roles to Users
    private Role userRole;
    private UserDataModel userModel;
    private User[] selectedUsers;
    private boolean link;
    private DualListModel<String> picklistModel;
    private List<String> picklistSource;
    private List<String> picklistTarget;
    private List<String> listPrivileges;
    private HashMap<String, Privilege> privilegeMap;
    private String selectedPrivilegeName;
    private DualListModel<String> picklistRoleModel;
    private List<String> picklistRoleSource;
    private List<String> picklistRoleTarget;
    private List<String> listRoles;
    private HashMap<String, Role> roleMap;
    private String selectedRoleName;
    private ResourceBundle message;

    public RoleController() {
        message = this.getHttpSession().getMessages();
    }

    public boolean isLink() {
        return link;
    }

    public void setLink(boolean link) {
        this.link = link;
    }

    public void linkOn() {
        this.link = true;
    }

    public void linkOff() {
        this.link = false;
    }

    public void deleteRole() {
        try {
            roleFacade.delete(selectedRole);
        } catch (Exception e) {
            JsfUtil.addErrorMessage(message.getString("roleDeleteError") + " " + selectedRole.getName() + ".\n" + message.getString("roleDeleteError2"));
        }
        prepareList();
    }

    public void createNewRole() {

        if (newRole == null || newRole.getName() == null || newRole.getName().isEmpty()) {
            JsfUtil.addErrorMessage(message.getString("emptyRole"));
            newRole = new Role();
            return;
        }
        List<String> roleNames = new ArrayList<String>();
        if (getRoleModel().getRoleData() != null && !getRoleModel().getRoleData().isEmpty()) {
            for (Role r : roleModel.getRoleData()) {
                roleNames.add(r.getName().toLowerCase());
            }
            if (roleNames.contains(newRole.getName().toLowerCase())) {
                JsfUtil.addErrorMessage(message.getString("roleDuplicated"));
                newRole = new Role();
                return;
            }
        }
        roleFacade.create(newRole);
        JsfUtil.addSuccessMessage(message.getString("roleCreated"));
        newRole = new Role();
        roleModel = null;
        prepareRoleToUser();
    }

    public void createNewPrivilege() {

        if (newPrivilege == null || newPrivilege.getPrivilegeName() == null || newPrivilege.getPrivilegeName().isEmpty()) {
            JsfUtil.addErrorMessage(message.getString("emptyPrivilege"));
            newPrivilege = new Privilege();
            return;
        }
        List<String> privilegeNames = new ArrayList<String>();
        if (getPrivilegeModel().getPrivilegeData() != null && !getPrivilegeModel().getPrivilegeData().isEmpty()) {
            for (Privilege p : privilegeModel.getPrivilegeData()) {
                privilegeNames.add(p.getPrivilegeName().toLowerCase());
            }

            if (privilegeNames.contains(newPrivilege.getPrivilegeName().toLowerCase())) {
                JsfUtil.addErrorMessage(message.getString("privilegeDuplicated"));
                newPrivilege = new Privilege();
                return;
            }
        }
        privilegeFacade.create(newPrivilege);
        JsfUtil.addSuccessMessage(message.getString("privilegeCreated"));
        newPrivilege = new Privilege();
        privilegeModel = null;
        preparePrivilegeToRole();
    }

    /**
     * Method for in-line editing on CRUD view
     *
     * @param role
     * @return
     */
    public boolean saveRole(Role role) {
        try {
            if (role.getId() == 0) {
                roleFacade.create(role);
                JsfUtil.addSuccessMessage(message.getString("roleCreated"));
                prepareList();
                newRole = new Role();
            } else {
                roleFacade.update(role);
            }

            //remodel the Roles since we just update/create something
            this.listRoles = null;
        } catch (Exception e) {
            prepareList();
            JsfUtil.addErrorMessage(message.getString("roleDuplicated"));
            return false;
        }
        return true;
    }

    public Role getNewRole() {
        if (newRole == null) {
            newRole = new Role();
        }
        return newRole;
    }

    public void setNewRole(Role newRole) {
        this.newRole = newRole;
    }

    public Privilege getNewPrivilege() {
        if (newPrivilege == null) {
            newPrivilege = new Privilege();
        }
        return newPrivilege;
    }

    public void setNewPrivilege(Privilege newPrivilege) {
        this.newPrivilege = newPrivilege;
    }

    public Role getSelectedRole() {
        return selectedRole;
    }

    public void setSelectedRole(Role selectedRole) {
        this.selectedRole = selectedRole;
    }

    public RoleDataModel getRoleModel() {
        if (roleModel == null) {
            populateRoleModel();
        }
        return roleModel;
    }

    public void setRoleModel(RoleDataModel roleModel) {
        this.roleModel = roleModel;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        if (!this.listRoles.contains(roleName.toLowerCase())) {
            this.roleName = null;
        } else {
            this.roleName = roleName;
        }
    }

    public List<String> getListRoles() {
        if (listRoles == null) {
            this.populateRoleList();
        }
        return listRoles;
    }

    public void setListRoles(List<String> listRoles) {
        this.listRoles = listRoles;
    }

    public List<String> getListPrivileges() {
        if (listPrivileges == null) {
            this.populatePrivilegeList();
        }
        return listPrivileges;
    }

    public void setListPrivileges(List<String> listPrivileges) {
        this.listPrivileges = listPrivileges;
    }

    /**
     * Return the filtered list on auto complete fields
     *
     * @param query
     * @return
     */
    public List<String> roleComplete(String query) {
        query = query.toLowerCase();
        if (listRoles == null) {
            populateRoleList();
        }

        List<String> filtered = new ArrayList<String>();
        for (String s : listRoles) {
            if (s.toLowerCase().contains(query)) {
                filtered.add(s);
            }
        }
        return filtered;
    }

    public Role getUserRole() {
        if (userRole == null) {
            userRole = new Role();
        }
        return userRole;
    }

    public void setUserRole(Role userRole) {
        this.userRole = userRole;
    }

    public void setRolePrivilege(Role rolePrivilege) {
        this.rolePrivilege = rolePrivilege;
    }

    public Role getRolePrivilege() {
        if (rolePrivilege == null) {
            rolePrivilege = new Role();
        }
        return rolePrivilege;
    }

    public PrivilegeDataModel getPrivilegeModel() {
        if (this.privilegeModel == null) {
            populatePrivilegeModel();
        }

        return this.privilegeModel;
    }

    public void setPrivilegeModel(PrivilegeDataModel privilegeModel) {
        this.privilegeModel = privilegeModel;
    }

    public void populatePrivilegeList() {
        listPrivileges = new ArrayList<String>();
        List<Privilege> privs = privilegeFacade.findAll();
        privilegeMap = new HashMap<String, Privilege>();
        for (Privilege p : privs) {
            privilegeMap.put(p.getPrivilegeName(), p);
            listPrivileges.add(p.getPrivilegeName());
        }
        Collections.sort(listPrivileges);
    }

    public void populateRoleList() {
        User currUser = userFacade.find(this.getHttpSession().getUser_id());
        Company c = companyFacade.getCompanybyId(this.getCompanyIdFromSession());
        Role currRole = currUser.getRole();
        List<Role> roles;
        roleMap = new HashMap<String, Role>();
        if (c.getLicense().hasGeofence() && c.getLicense().hasProximity()) {
            roles = roleFacade.findAll();
        } else {

            roles = roleFacade.findByLicense(c.getLicense());
        }
        List<String> newList = new ArrayList<String>();
        if (roles != null) {
            for (Role r : roles) {
                if (currRole.getPriority() < r.getPriority()) {
                    newList.add(r.getName());
                    roleMap.put(r.getName(), r);
                }
            }

            listRoles = newList;
        }

    }

    private void populatePrivilegeModel() {
        privilegeModel = new PrivilegeDataModel(privilegeFacade.findAll());
    }

    private void populateUserModel() {
        User currUser = userFacade.find(this.getHttpSession().getUser_id());
        userModel = new UserDataModel(companyFacade.getUsersFromCompany(getCompanyIdFromSession()));
        List<User> newList = new ArrayList<User>();
        for (User u : userModel) {
            if (u.getRole() == null) {
                newList.add(u);
            } else if (currUser.getRole().getPriority() < u.getRole().getPriority()) {
                newList.add(u);
            }
        }

        userModel.setWrappedData(newList);

    }

    private void populateRoleModel() {

        roleModel = new RoleDataModel(roleFacade.findAll());
    }

    public Privilege[] getSelectedPrivileges() {

        return selectedPrivileges;
    }

    public void setSelectedPrivileges(Privilege[] privileges) {
        this.selectedPrivileges = privileges;
    }

    /**
     * Saving the tag to the selected Campaigns
     *
     * @param role
     */
    public void saveRoleToUser(Role role) {

        if (selectedUsers == null || selectedUsers.length == 0) {
            JsfUtil.addErrorMessage(message.getString("selectUser"));
            return;
        }

        if (role == null || role.getName() == null || role.getName().isEmpty() || role.getName().equals("...")) {

            JsfUtil.addErrorMessage(message.getString("selectRole"));
            return;

        }

        Role r = roleFacade.findByName(role.getName());
        if (r == null) {
            return;
        } else {
            role = r;
        }
        //Checking if any of the roles are updated
        boolean roleChanged = false;

        User currUser = userFacade.find(this.getHttpSession().getUser_id());

        for (User u : selectedUsers) {
            if (u.getRole() == null || !u.getRole().equals(role)) {
                if (currUser.equals(u)) {
                    continue;
                }
                u.setRole(role);
                role.addUser(u);
                userFacade.update(u);
                roleChanged = true;
            }
        }
        if (roleChanged) {
            roleFacade.update(role);
            JsfUtil.addSuccessMessage(message.getString("roleLinkSuccess"));
            this.preparePrivilegeToRole();
        }

    }

    /**
     * Delete the role from the selectedUsers
     */
    public void deleteRoleFromUsers(Role role) {


        if (selectedUsers == null || selectedUsers.length == 0) {
            JsfUtil.addErrorMessage(message.getString("selectUser"));
            return;
        }

        if (role == null || role.getName() == null || role.getName().isEmpty() || role.getName().equals("...")) {
            JsfUtil.addErrorMessage(message.getString("selectRole"));
            return;
        }

        if (role.getName().equalsIgnoreCase("super user")) {
            return;
        }

        Role r = roleFacade.findByName(role.getName());

        if (r == null) {
            return;
        } else {
            role = r;
        }

        //Checking if any of the roles are updated
        boolean roleChanged = false;

        for (User u : selectedUsers) {

            if (u.getRole() != null && u.getRole().equals(role)) {
                u.setRole(null);
                role.removeUser(u);
                userFacade.update(u);
                roleChanged = true;
            }
        }
        if (roleChanged) {
            roleFacade.update(role);
            JsfUtil.addSuccessMessage(message.getString("roleLinkSuccess"));

            this.preparePrivilegeToRole();
        }

    }

    /**
     * Saving the tag to the selected Devices
     *
     * @param role
     */
    public void savePrivilegeToRoles(Role role) {
        if (selectedPrivileges == null || selectedPrivileges.length == 0) {
            JsfUtil.addErrorMessage(message.getString("selectPrivilege"));
            return;
        }

        if (role == null || role.getName() == null || role.getName().isEmpty() || role.getName().equals("...")) {
            JsfUtil.addErrorMessage(message.getString("selectRole"));
            return;
        }


        Role r = roleFacade.findByName(role.getName());
        if (r == null) {
            return;
        } else {
            role = r;
        }

        //Checking if any of the roles are updated
        boolean roleChanged = false;

        for (Privilege privilege : selectedPrivileges) {
            if (privilege.getRoles() == null || !privilege.getRoles().contains(role)) {
                privilege.addRole(role);
                role.addPrivilege(privilege);
                privilegeFacade.update(privilege);
                roleChanged = true;
            }
        }

        if (roleChanged) {
            roleFacade.update(role);
            JsfUtil.addSuccessMessage(message.getString("roleLinkPrivilege"));
            prepareRoleToUser();
        }
    }

    /**
     * delete Roles from certain Privileges
     *
     * @param role
     */
    public void deleteRoleFromPrivileges(Role role) {

        if (selectedPrivileges == null || selectedPrivileges.length == 0) {
            JsfUtil.addErrorMessage(message.getString("selectPrivilege"));
            return;
        }

        if (role == null || role.getName() == null || role.getName().isEmpty()) {
            JsfUtil.addErrorMessage(message.getString("selectRole"));
            return;
        }

        if (role.getName().equalsIgnoreCase("SUPER USER")) {
            return;
        }

        Role r = roleFacade.findByName(role.getName());
        if (r == null) {
            return;
        } else {
            role = r;
        }

        //Checking if any of the roles are updated
        boolean roleChanged = false;
        for (Privilege p : selectedPrivileges) {

            if (p.getRoles().contains(role)) {
                p.dropRole(role);
                role.removePrivilege(p);
                privilegeFacade.update(p);
                roleChanged = true;
            }
        }

        if (roleChanged) {
            roleFacade.update(role);
            JsfUtil.addSuccessMessage(message.getString("roleUnlinkedPrivilege"));
            prepareRoleToUser();
        }
    }

    public UserDataModel getUserModel() {
        if (userModel == null) {
            populateUserModel();
        }
        return userModel;
    }

    public void setUserModel(UserDataModel userModel) {
        this.userModel = userModel;
    }

    public User[] getSelectedUsers() {
        return selectedUsers;
    }

    public void setSelectedUsers(User[] users) {
        this.selectedUsers = users;
    }

    private void prepareVars() {

        roleModel = null;
        rolePrivilege = new Role();
        userRole = new Role();
        privilegeModel = null;
        selectedPrivileges = null;
        selectedRole = null;
        newRole = null;
        newPrivilege = null;
        roleName = null;
        userModel = null;
        selectedUsers = null;
        selectedPrivilegeName = null;
        privilegeMap = null;
        picklistModel = null;
        picklistSource = null;
        picklistTarget = null;
        listPrivileges = null;

        selectedRoleName = null;
        roleMap = null;
        picklistRoleModel = null;
        picklistRoleSource = null;
        picklistRoleTarget = null;
        listRoles = null;
    }

    public String prepareRoleToUser() {
        prepareVars();
        return "/role/UserRole?faces-redirect=true";
    }

    public String preparePrivilegeToRole() {
        prepareVars();
        return "/role/RolePrivilege?faces-redirect=true";
    }

    public String prepareList() {
        prepareVars();
        return "/role/List?faces-redirect=true";
    }

    public String getSelectedPrivilegeName() {
        return selectedPrivilegeName;
    }

    public void setSelectedPrivilegeName(String selectedPrivilegeName) {
        this.selectedPrivilegeName = selectedPrivilegeName;
    }

    public String getSelectedRoleName() {
        return selectedRoleName;
    }

    public void setSelectedRoleName(String selectedRoleName) {
        this.selectedRoleName = selectedRoleName;
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
            List<Role> allRoles = roleFacade.findAll();
            if (allRoles != null) {
                this.picklistSource = new ArrayList<String>();
                for (Role r : allRoles) {
                    this.picklistSource.add(r.getName());
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
        Privilege p = privilegeMap.get(selectedPrivilegeName);

        if (p != null) {
            this.picklistSource = null;
            this.picklistTarget = new ArrayList<String>();
            List<Role> roles = p.getRoles();
            if (roles != null) {
                for (Role r : roles) {
                    this.picklistTarget.add(r.getName());
                }
            }
        }
        populatePicklist();

    }

    private List<Role> getTargetRoles() {
        return getRolesFromNames(this.getPicklistTarget());
    }

    private List<Role> getSourceRoles() {
        return getRolesFromNames(this.getPicklistSource());
    }

    private List<Role> getRolesFromNames(List<String> names) {
        List<Role> result = new ArrayList<Role>();

        for (String roleName : names) {
            Role r = roleFacade.findByName(roleName);
            if (r != null) {
                result.add(r);
            }
        }
        return result;
    }

    public void saveRolesToPrivilege() {
        /**
         * Persist Roles
         */
        List<Role> targetRoles = getTargetRoles();
        List<Role> sourceRoles = roleFacade.findAll();
        sourceRoles.removeAll(targetRoles);

        Privilege p = privilegeMap.get(selectedPrivilegeName);

        if (targetRoles != null) {

            //Adding
            for (Role r : targetRoles) {
                if (!r.getPrivileges().contains(p)) {
                    p.addRole(r);
                    r.addPrivilege(p);
                    privilegeFacade.update(p);
                    roleFacade.update(r);
                }
            }


        }
        if (sourceRoles != null) {

            //Removing
            for (Role r : sourceRoles) {
                if (r.getPrivileges().contains(p)) {
                    p.dropRole(r);
                    r.removePrivilege(p);
                    privilegeFacade.update(p);
                    roleFacade.update(r);
                }

            }
        }


        JsfUtil.addSuccessMessage(message.getString("roleAssignedPrivilege") + this.selectedPrivilegeName);
    }

    public DualListModel<String> getPicklistRoleModel() {
        if (this.picklistRoleModel == null) {
            populatePicklistRole();
        }
        return picklistRoleModel;
    }

    public void setPicklistRoleModel(DualListModel<String> picklistRoleModel) {
        this.picklistRoleModel = picklistRoleModel;
    }

    public List<String> getPicklistRoleSource() {
        if (this.picklistRoleSource == null) {
            List<User> userInCompany = userFacade.getUsersByCompany(this.getCompanyFromSession());
            User currUser = this.getHttpSession().getCurrUser();

            //If not SuperUser limiting what you can see
            if (currUser.getRole().getPriority() != 1) {
                List<User> realUsers = new ArrayList<User>();
                for (User u : userInCompany) {

                    if (currUser.hasHigherRankThan(u)) {
                        realUsers.add(u);
                    }

                }
                userInCompany = realUsers;

            }
            if (userInCompany != null) {
                this.picklistRoleSource = new ArrayList<String>();
                for (User u : userInCompany) {
                    
                    this.picklistRoleSource.add(u.getUserName());
                }
                
                this.picklistRoleSource.remove(currUser.getUserName());
                Iterator<String> it = this.picklistRoleSource.iterator();
                while (it.hasNext()) {
                    String value = it.next();
                    if (getPicklistRoleTarget().contains(value)) {
                        it.remove();
                    }
                }

            }
        }
        if (picklistRoleSource != null) {
            Collections.sort(picklistRoleSource);
        }
        return picklistRoleSource;
    }

    public void setPicklistRoleSource(List<String> picklistRoleSource) {
        this.picklistRoleSource = picklistRoleSource;
    }

    public List<String> getPicklistRoleTarget() {
        if (picklistRoleTarget == null) {
            picklistRoleTarget = new ArrayList<String>();
        }
        return picklistRoleTarget;
    }

    public void setPicklistRoleTarget(List<String> picklistRoleTarget) {
        this.picklistRoleTarget = picklistRoleTarget;
    }

    private void populatePicklistRole() {
        setPicklistRoleSource(null);
        this.picklistRoleModel = new DualListModel<String>(getPicklistRoleSource(), getPicklistRoleTarget());
    }

    public void recreatePickListRole() {
        Role r = roleMap.get(selectedRoleName);
        if (r != null) {
            this.picklistRoleSource = null;
            this.picklistRoleTarget = new ArrayList<String>();
            List<User> users = r.getUsers();
            List<User> realDeal = new ArrayList<User>();
            for(User u: users) {
                if(u.getCompanies().contains(this.getCompanyFromSession())) {
                    realDeal.add(u);
                }
            }
            users = new ArrayList<User>(realDeal);
            User currUser = this.getHttpSession().getCurrUser();

            //If Current User is Not Super User
            if (currUser.getRole().getPriority() != 1) {
                List<User> realUsers = new ArrayList<User>();
                for (User u : users) {
                    if (currUser.hasHigherRankThan(u) && u.getCompanies().contains(getCompanyFromSession())) {
                        realUsers.add(u);
                    }
                }
                users = realUsers;
            }
            if (users != null) {
                for (User u : users) {
                    this.picklistRoleTarget.add(u.getUserName());
                }
            }

        }
        populatePicklistRole();


    }
    
    private List<User> getSourceUsers() {
        return getUsersFromNames(this.getPicklistRoleSource());
    }

    private List<User> getTargetUsers() {
        return getUsersFromNames(this.getPicklistRoleTarget());
    }

    private List<User> getUsersFromNames(List<String> names) {
        List<User> result = new ArrayList<User>();

        for (String userName : names) {
            User u = userFacade.getUserByUsername(userName);
            if (u != null) {
                result.add(u);
            }
        }
        return result;
    }

    public void saveUsersToRole() {
        /**
         * Persist Roles
         */
        Role r = roleMap.get(selectedRoleName);
        
        List<User> sourceUsers = getSourceUsers();
        for (User user : sourceUsers) {
            if(user.getRole() != null && user.getRole().getName().equalsIgnoreCase(r.getName())) {
                user.setRole(null);
                user.setProperties(null);
                userFacade.update(user);
                r.removeUser(user);
                roleFacade.update(r);
            }
        }
        
        
        List<User> targetUsers = getTargetUsers();
        
        r.setUsers(new ArrayList<User>());
        roleFacade.update(r);

        if (targetUsers != null) {
            for (User u : targetUsers) {
                if(u.getRole() != null && u.getRole().getName().equalsIgnoreCase(Role.PROPERTY_MANAGER) && u.getProperties() == null) {
                    u.setProperties(new ArrayList<Property>());
                }
                u.setRole(r);
                r.addUser(u);
                roleFacade.update(r);
                userFacade.update(u);

            }
        }
        listRoles = null;
        roleMap = null;
        JsfUtil.addSuccessMessage(message.getString("userAssignedRole") + this.selectedRoleName);
    }
}
