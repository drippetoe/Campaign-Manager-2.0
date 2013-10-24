package com.proximus.passbook.rest;

import com.google.common.io.Files;
import com.proximus.data.sms.report.ViewActiveOffers;
import com.proximus.manager.sms.report.ViewActiveOffersManagerLocal;
import com.proximus.passbook.PassbookPass;
import com.proximus.passbook.PassbookSigningInformation;
import com.proximus.passbook.util.PKSigningUtil;
import com.proximus.passbook.util.PassbookResourceCopy;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;

/**
 *
 * @author dshaw
 */
@Path("/{version}/retrievePass/{property_webhash}/{offer_id}")
@RequestScoped
public class PassbookRetrieveOfferREST {

    static final Logger logger = Logger.getLogger(PassbookRetrieveOfferREST.class);
    static PassbookSigningInformation pkSigningInformation = null;

    static final Object SYNC = new Object();

    @EJB
    private ViewActiveOffersManagerLocal activeOfferMgr;

    public static PassbookSigningInformation getSigningInformation() throws FileNotFoundException {
        try {
            if (pkSigningInformation == null) {
                File myCert = new File("/home/proximus/server/passbook/certificates/pass.vtext.p12");
                File appleCert = new File("/home/proximus/server/passbook/certificates/AppleWWDRCA.cer");
                String certPassword = "vtext";

                if (!myCert.exists()) {
                    logger.error(myCert.getAbsolutePath() + " does not exist, so the Pass cannot be signed");
                    throw new FileNotFoundException(myCert.getAbsolutePath());
                }
                else if (!appleCert.exists()) {
                    logger.error(appleCert.getAbsolutePath() + " does not exist, so the Pass cannot be signed");
                    throw new FileNotFoundException(appleCert.getAbsolutePath());
                }
                else
                {
                    pkSigningInformation = PKSigningUtil.loadSigningInformationFromPKCS12FileAndIntermediateCertificateFile(myCert.getAbsolutePath(), certPassword, appleCert.getAbsolutePath());
                }
            }
        } catch (IOException ioerr) {
            logger.error(ioerr);
        } catch (Exception err) {
            logger.error(err);
        }

        return pkSigningInformation;
    }

    @GET
    @Produces("application/vnd.apple.pkpass")
    public Response doGET(@PathParam("version") String version, @PathParam("property_webhash") String propertyWebHash, @PathParam("offer_id") String offerId) {

        String key = propertyWebHash +  "-" + offerId;
        ViewActiveOffers offer = activeOfferMgr.find(key);

        if ( offer == null )
        {
            logger.error("Could not find ViewActiveOffers with key " + key);
            return Response.status(Response.Status.BAD_REQUEST).entity("Could not generate Pass").build();
        }
        else
        {
            logger.debug("Found valid ViewActiveOffers with key " + key);
        }

        String header = offer.getWebOffer().getPassbookHeader();
        String subHeader = offer.getWebOffer().getPassbookSubheader();
        String details = offer.getWebOffer().getPassbookDetails();

        PassbookPass pass = new PassbookPass(offer);
        
        try {
            File outputDir = Files.createTempDir();
            logger.debug("Writing pass data to " + outputDir.getAbsolutePath());

            // copy image resources to the temp Dir
            PassbookResourceCopy.copyResourcesToDirectory(outputDir);

            pass.writeToFile(outputDir, true);

            byte[] zipData;
            // not sure if this thing is thread safe or not, so being safe for now
            synchronized(SYNC)
            {
                logger.debug("Creating zip data in " + outputDir.getAbsolutePath());
                zipData = PKSigningUtil.createSignedAndZippedPkPassArchive(pass, outputDir, getSigningInformation());
                logger.debug("Zip created: " + zipData.length + " bytes");
                //FileUtils.deleteDirectory(outputDir);
            }
            // SUCCESS!
            return Response.ok(new ByteArrayInputStream(zipData)).build();
        } catch (Exception err) {
            logger.error(key, err);
        }

        return Response.status(Response.Status.BAD_REQUEST).entity("Could not generate Pass").build();
    }
}
