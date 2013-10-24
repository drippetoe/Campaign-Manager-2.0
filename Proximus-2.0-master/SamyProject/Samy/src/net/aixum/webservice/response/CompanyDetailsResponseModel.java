/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice.response;


import java.util.Date;
import java.util.List;

import net.aixum.webservice.CompanyCard;
import net.aixum.webservice.ContactDetails;
import net.aixum.webservice.HeaderBarLogo;
import net.aixum.webservice.MenuServiceItem;
import net.aixum.webservice.PaymentProvider;
import net.aixum.webservice.RatingService;
import net.aixum.webservice.Registration;
import net.aixum.webservice.Setting;
import net.aixum.webservice.TinyLogo;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
@DatabaseTable(tableName = "companyDetailsResponseModel")
public class CompanyDetailsResponseModel extends BaseResponseModel {

	@DatabaseField(id = true)
	@SerializedName("Id")
    private Integer id;
	
	@DatabaseField(columnName="revision")
	@SerializedName("Revision")
    private String revision;
	
	@DatabaseField(columnName="title")
	@SerializedName("Title")
    private String title;
	@DatabaseField(columnName="subline")
	@SerializedName("Subline")
    private String subline;
	@ForeignCollectionField(eager = false)
    private List<Integer> categories;
    @DatabaseField(columnName="rss")
    @SerializedName("RssFeedUrl")
    private String rss;
    @DatabaseField(columnName="url")
    @SerializedName("Url")
    private String url;
    @DatabaseField(columnName="voucherService")
    @SerializedName("VoucherService")
    private Boolean voucherService;
    @DatabaseField(columnName="phone")
    @SerializedName("Phone")
    private String phone;
    @DatabaseField(columnName="sortSize")
    @SerializedName("SortSize")
    private Integer sortSize;
    @DatabaseField(columnName="region")
    @SerializedName("Region")
    private Integer region;
    @ForeignCollectionField(eager = false)
    private List<Integer> possibleVoucherCategories;
    @DatabaseField(columnName="vouchersActivated")
    private Boolean vouchersActivated;
    @DatabaseField(columnName="voucherCount")
    private Integer voucherCount;
    @DatabaseField(columnName="isNew")
    private Boolean isNew;
    @DatabaseField(columnName="newSince")
    private Date newSince;
    @DatabaseField(columnName="daysActive")
    private Integer daysActive;
    @DatabaseField(columnName="tinyLogo", foreign = true)
    @SerializedName("TinyLogo")
    private TinyLogo tinyLogo;    
    @DatabaseField(columnName="headerBarLogo", foreign = true)
    @SerializedName("HeaderBarLogo")
    private HeaderBarLogo headerBarLogo;
    @DatabaseField(columnName="companyCard", foreign = true)
    private CompanyCard companyCard;
    @DatabaseField(columnName="contactDetails", foreign = true)
    private ContactDetails contactDetails;
    @ForeignCollectionField(eager = false)
    private List<PaymentProvider> paymentProviders;
    @ForeignCollectionField(eager = false)
    private List<Setting> settings;
    @DatabaseField(columnName="registration", foreign = true)
    private Registration registration;
    @DatabaseField(columnName="ratingService", foreign = true)
    private RatingService ratingService;
    @ForeignCollectionField(eager = false)
    private List<MenuServiceItem> menuServiceItems;
    
    public CompanyDetailsResponseModel(){
    	
    }
    public CompanyDetailsResponseModel(Integer id, String revision, String title, String subline, List<Integer> categories, String rss, String url, Boolean voucherService, String phone, Integer sortSize, Integer region, List<Integer> possibleVoucherCategories, Boolean vouchersActivated, Integer voucherCount, Boolean isNew, Date newSince, Integer daysActive, TinyLogo tinyLogo, HeaderBarLogo headerBarLogo, CompanyCard companyCard, ContactDetails contactDetails, List<PaymentProvider> paymentProviders, List<Setting> settings, Registration registration, RatingService ratingService, List<MenuServiceItem> menuServiceItems, Boolean success, String message) {
        super(success, message);
        this.id = id;
        this.revision = revision;
        this.title = title;
        this.subline = subline;
        this.categories = categories;
        this.rss = rss;
        this.url = url;
        this.voucherService = voucherService;
        this.phone = phone;
        this.sortSize = sortSize;
        this.region = region;
        this.possibleVoucherCategories = possibleVoucherCategories;
        this.vouchersActivated = vouchersActivated;
        this.voucherCount = voucherCount;
        this.isNew = isNew;
        this.newSince = newSince;
        this.daysActive = daysActive;
        this.tinyLogo = tinyLogo;
        this.headerBarLogo = headerBarLogo;
        this.companyCard = companyCard;
        this.contactDetails = contactDetails;
        this.paymentProviders = paymentProviders;
        this.settings = settings;
        this.registration = registration;
        this.ratingService = ratingService;
        this.menuServiceItems = menuServiceItems;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubline() {
        return subline;
    }

    public void setSubline(String subline) {
        this.subline = subline;
    }

    public List<Integer> getCategories() {
        return categories;
    }

    public void setCategories(List<Integer> categories) {
        this.categories = categories;
    }

    public String getRss() {
        return rss;
    }

    public void setRss(String rss) {
        this.rss = rss;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean isVoucherService() {
        return voucherService;
    }

    public void setVoucherService(Boolean voucherService) {
        this.voucherService = voucherService;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getSortSize() {
        return sortSize;
    }

    public void setSortSize(Integer sortSize) {
        this.sortSize = sortSize;
    }

    public Integer getRegion() {
        return region;
    }

    public void setRegion(Integer region) {
        this.region = region;
    }

    public List<Integer> getPossibleVoucherCategories() {
        return possibleVoucherCategories;
    }

    public void setPossibleVoucherCategories(List<Integer> possibleVoucherCategories) {
        this.possibleVoucherCategories = possibleVoucherCategories;
    }

    public Boolean isVouchersActivated() {
        return vouchersActivated;
    }

    public void setVouchersActivated(Boolean vouchersActivated) {
        this.vouchersActivated = vouchersActivated;
    }

    public Integer getVoucherCount() {
        return voucherCount;
    }

    public void setVoucherCount(Integer voucherCount) {
        this.voucherCount = voucherCount;
    }

    public Boolean isIsNew() {
        return isNew;
    }

    public void setIsNew(Boolean isNew) {
        this.isNew = isNew;
    }

    public Date getNewSince() {
        return newSince;
    }

    public void setNewSince(Date newSince) {
        this.newSince = newSince;
    }

    public Integer getDaysActive() {
        return daysActive;
    }

    public void setDaysActive(Integer daysActive) {
        this.daysActive = daysActive;
    }

    public TinyLogo getTinyLogo() {
        return tinyLogo;
    }

    public void setTinyLogo(TinyLogo tinyLogo) {
        this.tinyLogo = tinyLogo;
    }

    public HeaderBarLogo getHeaderBarLogo() {
        return headerBarLogo;
    }

    public void setHeaderBarLogo(HeaderBarLogo headerBarLogo) {
        this.headerBarLogo = headerBarLogo;
    }

    public CompanyCard getCompanyCard() {
        return companyCard;
    }

    public void setCompanyCard(CompanyCard companyCard) {
        this.companyCard = companyCard;
    }

    public ContactDetails getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(ContactDetails contactDetails) {
        this.contactDetails = contactDetails;
    }

    public List<PaymentProvider> getPaymentProviders() {
        return paymentProviders;
    }

    public void setPaymentProviders(List<PaymentProvider> paymentProviders) {
        this.paymentProviders = paymentProviders;
    }

    public List<Setting> getSettings() {
        return settings;
    }

    public void setSettings(List<Setting> settings) {
        this.settings = settings;
    }

    public Registration getRegistration() {
        return registration;
    }

    public void setRegistration(Registration registration) {
        this.registration = registration;
    }

    public RatingService getRatingService() {
        return ratingService;
    }

    public void setRatingService(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    public List<MenuServiceItem> getMenuServiceItems() {
        return menuServiceItems;
    }

    public void setMenuServiceItems(List<MenuServiceItem> menuServiceItems) {
        this.menuServiceItems = menuServiceItems;
    }
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CompanyDetailsResponseModel [id=" + id + ", revision="
				+ revision + ", title=" + title + ", subline=" + subline
				+ ", categories=" + categories + ", rss=" + rss + ", url="
				+ url + ", voucherService=" + voucherService + ", phone="
				+ phone + ", sortSize=" + sortSize + ", region=" + region
				+ ", possibleVoucherCategories=" + possibleVoucherCategories
				+ ", vouchersActivated=" + vouchersActivated
				+ ", voucherCount=" + voucherCount + ", isNew=" + isNew
				+ ", newSince=" + newSince + ", daysActive=" + daysActive
				+ ", tinyLogo=" + tinyLogo + ", headerBarLogo=" + headerBarLogo
				+ ", companyCard=" + companyCard + ", contactDetails="
				+ contactDetails + ", paymentProviders=" + paymentProviders
				+ ", settings=" + settings + ", registration=" + registration
				+ ", ratingService=" + ratingService + ", menuServiceItems="
				+ menuServiceItems + "]";
	}
    
    
}
