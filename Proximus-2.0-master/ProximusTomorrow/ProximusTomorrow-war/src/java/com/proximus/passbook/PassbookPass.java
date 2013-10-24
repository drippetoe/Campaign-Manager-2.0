package com.proximus.passbook;

import com.proximus.data.sms.report.ViewActiveOffers;
import com.proximus.passbook.exceptions.MaxLocationsExceededException;
import com.proximus.passbook.util.JSONFormatter;
import com.proximus.passbook.util.PKSigningUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Class to create and persist an Apple Passbook "Coupon" pass
 * @author dshaw
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY)
public class PassbookPass {

    static final Logger logger = Logger.getLogger(PassbookPass.class);

    /**
     * Logo Text: 21  - Retailer Name
     *
     * Header Label: 14  - 'EXPIRES'
     * Header Value: 13  end date
     * Primary Field Label: 46  - user specified
     * Primary Field Value: 7   - big user specified
     *
     * Secondary Field Label: 36 - property name
     * Secondary Field Value: 36 - user specified
     *
     * Coupons with a square barcode can have a total of up to four secondary and auxiliary fields, combined.
     *
     */
    
    private Integer formatVersion = 1;
    // single instance of a pass is passTypeIdentifier + serialNumber
    private String passTypeIdentifier = "pass.vtext.mobileoffer";  // registered on the developer portal, used for grouping in Passbook
    private String serialNumber = "123456"; // unique number for every pass that you issue, a String,
    private String teamIdentifier = "3QMFYLSXZX";  // GET THIS FROM APPLE, 10 character identifier
    // this must be SSL if it is provided
    private String webServiceURL; // = "http://192.168.1.109:8080/Passbook/v1/passes/";
    private String authenticationToken;
    private String backgroundColor;
    private String forgroundColor;
    private String labelColor;
    private String backgroundImage;
    private String thumbnailImage; // for some passes
    private String logo; // image name, recommend "iconic logo"
    private String logoText; // words by the logo if the art doesnt have it
    private String description = "ValuText Offer"; // this is required in the JSON
    private PassbookBarcode barcode;
    // holds all the passBody specific fields
    private PassbookType passBody = new PassbookType();

    // shows up in the email
    private String organizationName = "";
    // back of pass
    // ATOM id / itunes store ID / open your APP on the iOS device
    private List<Long> associatedStoreIdentifiers;
    // end back of pass
    private List<PassbookRelevanceLocation> locations;


    public PassbookPass(ViewActiveOffers offer)
    {
        this.setBackgroundColor("rgb(121,173,54)"); // ValuText green
        
        Date endDate = offer.getEndDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        this.setLogoText(offer.getRetailer().getName());

        this.setSerialNumber(offer.getId());

        Locale locale = new Locale(offer.getLocale());
        ResourceBundle messages = ResourceBundle.getBundle("resources.Messages", locale);
        String expires = messages.getString("passbookExpires");
        if ( expires == null || expires.isEmpty())
        {
            logger.warn("Translation failed on passbook generation");
            expires = "Expires";
        }

        this.getPassbookType().addHeaderField(new PassbookField("header", expires, sdf.format(endDate)));
        
        String header = offer.getWebOffer().getPassbookHeader();
        String subHeader = offer.getWebOffer().getPassbookSubheader();
        String details = offer.getWebOffer().getPassbookDetails();

        this.getPassbookType().addPrimaryField(new PassbookField("primaryField", subHeader, header));
        this.getPassbookType().addSecondaryField(new PassbookField("secondaryField", offer.getProperty().getName(), details));

        String offerBarcode = offer.getWebOffer().getPassbookBarcode();
        if ( offerBarcode != null && !offerBarcode.isEmpty())
        {
            this.setBarcode(offerBarcode);
        }
    }
    
    public Integer getFormatVersion() {
        return formatVersion;
    }

    public void setFormatVersion(Integer formatVersion) {
        this.formatVersion = formatVersion;
    }

    public String getPassTypeIdentifier() {
        return passTypeIdentifier;
    }

    public void setPassTypeIdentifier(String passTypeIdentifier) {
        this.passTypeIdentifier = passTypeIdentifier;
    }

    public PassbookBarcode getBarcode() {
        return barcode;
    }

    public final void setBarcode(String message)
    {
        this.barcode = new PassbookBarcode(message);
    }

    @JsonProperty(value="coupon")
    public final PassbookType getPassbookType()
    {
        return passBody;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public final void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getTeamIdentifier() {
        return teamIdentifier;
    }

    public void setTeamIdentifier(String teamIdentifier) {
        this.teamIdentifier = teamIdentifier;
    }

    public String getWebServiceURL() {
        return webServiceURL;
    }

    public void setWebServiceURL(String webServiceURL) {
        this.webServiceURL = webServiceURL;
    }

    public String getAuthenticationToken() {
        return authenticationToken;
    }

    public void setAuthenticationToken(String authenticationToken) {
        this.authenticationToken = authenticationToken;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public final void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getForgroundColor() {
        return forgroundColor;
    }

    public void setForgroundColor(String forgroundColor) {
        this.forgroundColor = forgroundColor;
    }

    public String getLabelColor() {
        return labelColor;
    }

    public void setLabelColor(String labelColor) {
        this.labelColor = labelColor;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public String getThumbnailImage() {
        return thumbnailImage;
    }

    public void setThumbnailImage(String thumbnailImage) {
        this.thumbnailImage = thumbnailImage;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getLogoText() {
        return logoText;
    }

    public final void setLogoText(String logoText) {
        this.logoText = logoText;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public List<Long> getAssociatedStoreIdentifiers() {
        return associatedStoreIdentifiers;
    }

    public void setAssociatedStoreIdentifiers(List<Long> associatedStoreIdentifiers) {
        this.associatedStoreIdentifiers = associatedStoreIdentifiers;
    }

    public List<PassbookRelevanceLocation> getLocations() {
        return locations;
    }

    public void setLocations(List<PassbookRelevanceLocation> locations) {
        this.locations = locations;
    }

    public void addLocation(PassbookRelevanceLocation location) throws MaxLocationsExceededException {
        if (locations == null) {
            locations = new ArrayList<PassbookRelevanceLocation>();
        }
        else if (locations.size() == 10) {
            throw new MaxLocationsExceededException("Cannot add more than 10 locations to a Pass");
        }
        locations.add(location);
    }

    /**
     * Write out the pass to a file in the given folder
     * @param tempFolder folder to write the pass.json
     * @param prettyPrint if true, format the JSON to be larger but human readable
     * @throws FileNotFoundException if the parent folder does not exist
     * @throws IOException if the file csnnot be written
     */
    public void writeToFile(File tempFolder, boolean prettyPrint) throws FileNotFoundException, IOException {
        File signatureFile = new File(tempFolder.getAbsolutePath() + File.separator + "pass.json");
        FileOutputStream signatureOutputStream = new FileOutputStream(signatureFile);
        String json = JSONFormatter.toJson(this, prettyPrint);
        System.out.println(json);
        signatureOutputStream.write(json.getBytes());
    }

    public static void main(String[] args) throws Exception {
        PassbookPass pass = new PassbookPass(null);

        /**
         * Logo Text: 21  - Retailer Name
         *
         * Header Label: 14  - 'EXPIRES'
         * Header Value: 13  end date
         * Primary Field Label: 46  - user specified
         * Primary Field Value: 7   - big user specified
         *
         * Secondary Field Label: 36 - property name
         * Secondary Field Value: 36 - user specified
         */

        pass.setLogoText("AAAAAAAAAAAAAAAAAAAAB");
        pass.setDescription("DESCRIPTION 2");
       
        pass.setForgroundColor("rgb(0,0,0)");

        pass.setBarcode("12345678");
        pass.barcode.setBarcodeFormat("PKBarcodeFormatQR");

        pass.getPassbookType().addHeaderField(new PassbookField("headerField", "AAAAAAAAAAAAAB", "AAAAAAAAAAAAB"));

        pass.getPassbookType().addPrimaryField(new PassbookField("primaryKey", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB", "1234567"));
        pass.getPassbookType().addSecondaryField(new PassbookField("secondaryKey", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB"));
        
        pass.addLocation(new PassbookRelevanceLocation(-122.3748889, 37.6189722));
        pass.addLocation(new PassbookRelevanceLocation(-122.03118, 37.33182));

        pass.getPassbookType().addBackField(new PassbookField("disclaimer", "DISCLAIMER", "Copyright 2012 Proximus Mobility, LLC.  All rights reserved."));

        // Apple docs suggest this is not supported on a passBody
        //Date expirationDate = new Date(System.currentTimeMillis() + 1000*60*60*24*7);
        //pass.setRelevantDate(expirationDate);

        File tempDir = new File("/home/proximus/server/passbook/template");
        String outputDir = "/Library/WebServer/Documents";

        pass.writeToFile(tempDir, true);

        String myCertPath = "/home/proximus/server/passbook/certificates/pass.vtext.p12";
        String appleCertPath = "/home/proximus/server/passbook/certificates/AppleWWDRCA.cer";
        String certPassword = "vtext";
        PassbookSigningInformation pkSigningInformation = PKSigningUtil.loadSigningInformationFromPKCS12FileAndIntermediateCertificateFile(myCertPath, certPassword, appleCertPath);
        byte[] zipData = PKSigningUtil.createSignedAndZippedPkPassArchive(pass, tempDir, pkSigningInformation);
        File outputFile = new File(outputDir, "finalpass.pkpass");
        System.out.println("Writing to " + outputFile.getAbsolutePath());
        FileOutputStream fos = new FileOutputStream(outputFile);
        fos.write(zipData);
        fos.close();

    }
}
