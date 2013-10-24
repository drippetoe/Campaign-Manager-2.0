package com.proximus.jsf;

import com.proximus.data.*;
import com.proximus.jsf.datamodel.CompanyDataModel;
import com.proximus.jsf.util.JsfUtil;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.PricingModelManagerLocal;
import com.proximus.manager.UserManagerLocal;
import com.proximus.manager.report.WifiDaySummaryManagerLocal;
import com.proximus.manager.sms.BrandManagerLocal;
import com.proximus.util.HashUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "companyController")
@SessionScoped
public class CompanyController extends AbstractController implements Serializable {

    private Company newCompany;
    private CompanyDataModel companyModel = null;
    private List<Company> filteredCompanies;
    private Company selectedCompany;
    private List<User> users;
    private License selectedLicense;
    private List<String> licenseList;
    private Brand selectedBrand;
    private List<Brand> brandList;
    private List<PricingModel> pricingModelList;
    private PricingModel selectedPricingModel;
    private String salt;
    @EJB
    CompanyManagerLocal companyMgr;
    @EJB
    UserManagerLocal userMgr;
    @EJB
    BrandManagerLocal brandMgr;
    @EJB
    PricingModelManagerLocal pricingModelMgr;
    ResourceBundle message;
    @EJB
    WifiDaySummaryManagerLocal wifiDaySummaryMgr;

    public CompanyController() {
        message = this.getHttpSession().getMessages();
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public License getSelectedLicense() {
        if (selectedLicense == null) {
            selectedLicense = new License();
            selectedLicense.setLicenseText(License.LICENSE_PROXIMITY);
        }
        return selectedLicense;
    }

    public void setSelectedLicense(License selectedLicense) {
        this.selectedLicense = selectedLicense;
    }

    public List<String> getLicenseList() {
        if (licenseList == null) {
            populateLicenseList();
        }
        return licenseList;
    }

    public void setLicenseList(List<String> licenseList) {
        this.licenseList = licenseList;
    }

    private void populateLicenseList() {
        List<License> list = companyMgr.getAllLicenses();
        licenseList = new ArrayList<String>();
        for (License license : list) {
            licenseList.add(license.getLicenseText());
        }
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Brand getSelectedBrand() {
        if (selectedBrand == null) {
            selectedBrand = new Brand();
        }
        return selectedBrand;
    }

    public void setSelectedBrand(Brand selectedBrand) {
        this.selectedBrand = selectedBrand;
    }

    public List<Brand> getBrandList() {
        if (brandList == null) {
            populateBrandList();
        }
        return brandList;
    }

    public void setBrandList(List<Brand> brandList) {
        this.brandList = brandList;
    }

    public void populateBrandList() {
        brandList = brandMgr.getAllSorted();
    }

    public List<PricingModel> getPricingModelList() {
        if (pricingModelList == null) {
            populatePricingModelList();
        }
        return pricingModelList;
    }

    public void setPricingModelList(List<PricingModel> pricingModelList) {
        this.pricingModelList = pricingModelList;
    }

    private void populatePricingModelList() {
        pricingModelList = pricingModelMgr.findAllSortedPricingModels();
    }

    public PricingModel getSelectedPricingModel() {
        if (selectedPricingModel == null) {
            selectedPricingModel = new PricingModel();
        }
        return selectedPricingModel;
    }

    public void setSelectedPricingModel(PricingModel selectedPricingModel) {
        this.selectedPricingModel = selectedPricingModel;
    }

    public Company getSelectedCompany() {
        if (selectedCompany == null) {
            selectedCompany = new Company();
            selectedCompany.setLicense(new License());
        }
        return selectedCompany;
    }

    public Company getNewCompany() {
        if (newCompany == null) {
            newCompany = new Company();
        }
        return newCompany;
    }

    public void setNewCompany(Company newCompany) {
        this.newCompany = newCompany;
    }

    public void setSelectedCompany(Company selectedCompany) {
        this.selectedCompany = selectedCompany;
    }

    public CompanyDataModel getCompanyModel() {
        if (companyModel == null) {
            populateCompanyModel();
        }
        return companyModel;
    }

    public void setCompanyModel(CompanyDataModel companyModel) {
        this.companyModel = companyModel;
    }

    public List<Company> getFilteredCompanies() {
        return filteredCompanies;
    }

    public void setFilteredCompanies(List<Company> filteredCompanies) {
        this.filteredCompanies = filteredCompanies;
    }

    public Company getCompanyById(Long id) {
        Company c = companyMgr.find(id);
        return c;
    }

    public void populateCompanyModel() {
        List<Company> companies = companyMgr.findAll();
        companyModel = new CompanyDataModel(companies);
        filteredCompanies = new ArrayList<Company>(companies);
    }

    private void prepareVars() {
        companyModel = null;
        newCompany = null;
        selectedCompany = null;
        pricingModelList = null;
        brandList = null;
        licenseList = null;
        selectedBrand = null;
        selectedLicense = null;
        selectedPricingModel = null;
    }

    public String prepareList() {
        prepareVars();
        return "/company/List?faces-redirect=true";
    }

    public void editCompany(Company company) {

        if (company == null || company.getName() == null || company.getName().isEmpty()) {
            JsfUtil.addErrorMessage(message.getString("companyNameError"));
            return;
        }

        if (company.getLicense() == null || company.getLicense().getLicenseText() == null || company.getLicense().getLicenseText().isEmpty()) {
            JsfUtil.addErrorMessage(message.getString("companyLicenseError"));
            return;
        }

        if (company.getId() == 0) {
            return;
        }

        License realLicense = companyMgr.getLicenseByText(company.getLicense().getLicenseText());
        company.setLicense(realLicense);
        if (selectedBrand != null && selectedBrand.getName() != null && !selectedBrand.getName().isEmpty()) {
            Brand realBrand = brandMgr.getBrandByName(selectedBrand.getName());
            company.setBrand(realBrand);
        }
        if (selectedPricingModel != null && selectedPricingModel.getName() != null && !selectedPricingModel.getName().isEmpty()) {
            PricingModel realPricingModel = pricingModelMgr.getPricingModelByName(selectedPricingModel.getName());
            company.setPricingModel(realPricingModel);
        }
        companyMgr.update(company);
        JsfUtil.addSuccessMessage(message.getString("companyEdited"));
        prepareList();

    }

    /**
     * Brand New Company Creation
     */
    public void createNewCompany(Company company) {

        if (company == null || company.getName() == null || company.getName().isEmpty()) {
            JsfUtil.addErrorMessage(message.getString("companyNameError"));
            return;
        }

        if (selectedLicense == null || selectedLicense.getLicenseText() == null || selectedLicense.getLicenseText().isEmpty()) {
            JsfUtil.addErrorMessage(message.getString("companyLicenseError"));
            selectedLicense = new License();
            return;
        }
        List<String> companyNames = new ArrayList<String>();
        if (companyModel.getCompanyData() != null && !companyModel.getCompanyData().isEmpty()) {
            for (Company c : companyModel.getCompanyData()) {
                companyNames.add(c.getName().toLowerCase());
            }
            if (companyNames.contains(company.getName().toLowerCase())) {
                JsfUtil.addErrorMessage(message.getString("companyDuplicatedError"));
                return;
            }

        }
        License realLicense = companyMgr.getLicenseByText(selectedLicense.getLicenseText());
        company.setLicense(realLicense);

        users = new ArrayList<User>();
        List<User> superUsers = userMgr.getSuperUsers();
        for (User user : superUsers) {
            users.add(user);
        }
        User userByUsername = userMgr.getUserByUsername(getUsernameFromSession());

        if (!users.contains(userByUsername)) {
            users.add(userByUsername);
        }
        company.setUsers(users);
        userByUsername.addCompany(company);
        if (salt != null) {
            company.setSalt(HashUtil.generateMD5Hash(company.getName(), salt));
        }

        if (selectedBrand != null && selectedBrand.getName() != null && !selectedBrand.getName().isEmpty()) {
            Brand realBrand = brandMgr.getBrandByName(selectedBrand.getName());
            company.setBrand(realBrand);
        }

        if (selectedPricingModel != null && selectedPricingModel.getName() != null && !selectedPricingModel.getName().isEmpty()) {
            PricingModel realPricingModel = pricingModelMgr.getPricingModelByName(selectedPricingModel.getName());
            company.setPricingModel(realPricingModel);
        }
        companyMgr.create(company);
        for (User user : users) {
            user.addCompany(company);
            userMgr.update(user);
        }
        JsfUtil.addSuccessMessage(message.getString("companyCreated"));
        prepareList();
    }

    public void prepareCreate() {
        newCompany = new Company();
    }
}
