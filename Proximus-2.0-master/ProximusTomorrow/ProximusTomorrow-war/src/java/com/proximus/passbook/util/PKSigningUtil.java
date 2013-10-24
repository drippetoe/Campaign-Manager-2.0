package com.proximus.passbook.util;

/**
 * Copyright (C) 2012 Patrice Brend'amour <p.brendamour@bitzeche.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.proximus.passbook.PassbookPass;
import com.proximus.passbook.PassbookSigningInformation;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSProcessableFile;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.annotate.JsonFilter;

public final class PKSigningUtil {

    private static final int ZIP_BUFFER_SIZE = 8192;
    private static final String MANIFEST_JSON_FILE_NAME = "manifest.json";
    private static final String PASS_JSON_FILE_NAME = "pass.json";

    private PKSigningUtil() {
    }

    /*
    public static byte[] createSignedAndZippedPkPassArchive(final PassbookPass pass, final URL fileUrlOfTemplateDirectory,
            final PassbookSigningInformation signingInformation) throws Exception {
        String pathToTemplateDirectory = URLDecoder.decode(fileUrlOfTemplateDirectory.getFile(), "UTF-8");
        return createSignedAndZippedPkPassArchive(pass, pathToTemplateDirectory, signingInformation);
    }
    * 
    */

    public static byte[] createSignedAndZippedPkPassArchive(final PassbookPass pass, final File filesDirectory,
            final PassbookSigningInformation signingInformation) throws Exception {

        //File tempPassDir = Files.createTempDir();
        //FileUtils.copyDirectory(new File(pathToTemplateDirectory), tempPassDir);

        ObjectMapper jsonObjectMapper = JSONFormatter.getObjectMapper();

        createPassJSONFile(pass, filesDirectory, jsonObjectMapper);

        File manifestJSONFile = createManifestJSONFile(filesDirectory, jsonObjectMapper);

        signManifestFile(filesDirectory, manifestJSONFile, signingInformation);

        byte[] zippedPass = createZippedPassAndReturnAsByteArray(filesDirectory);

        //FileUtils.deleteDirectory(filesDirectory);
        return zippedPass;
    }

    public static void signManifestFile(final File temporaryPassDirectory, final File manifestJSONFile,
            final PassbookSigningInformation signingInformation) throws Exception {

        if (temporaryPassDirectory == null || manifestJSONFile == null || signingInformation == null || !signingInformation.isValid()) {
            throw new IllegalArgumentException("Null params are not supported");
        }
        addBCProvider();

        CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
        ContentSigner sha1Signer = new JcaContentSignerBuilder("SHA1withRSA").setProvider(BouncyCastleProvider.PROVIDER_NAME).build(
                signingInformation.getSigningPrivateKey());

        generator.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider(
                BouncyCastleProvider.PROVIDER_NAME).build()).build(sha1Signer, signingInformation.getSigningCert()));

        List<X509Certificate> certList = new ArrayList<X509Certificate>();
        certList.add(signingInformation.getAppleWWDRCACert());
        certList.add(signingInformation.getSigningCert());

        Store certs = new JcaCertStore(certList);

        generator.addCertificates(certs);

        CMSSignedData sigData = generator.generate(new CMSProcessableFile(manifestJSONFile), false);
        byte[] signedDataBytes = sigData.getEncoded();

        File signatureFile = new File(temporaryPassDirectory.getAbsolutePath() + File.separator + "signature");
        FileOutputStream signatureOutputStream = new FileOutputStream(signatureFile);
        signatureOutputStream.write(signedDataBytes);
    }

    public static PassbookSigningInformation loadSigningInformationFromPKCS12FileAndIntermediateCertificateFile(final String pkcs12KeyStoreFilePath,
            final String keyStorePassword, final String appleWWDRCAFilePath) throws IOException, NoSuchAlgorithmException, CertificateException,
            KeyStoreException, NoSuchProviderException, UnrecoverableKeyException {
        addBCProvider();

        KeyStore pkcs12KeyStore = loadPKCS12File(pkcs12KeyStoreFilePath, keyStorePassword);
        Enumeration<String> aliases = pkcs12KeyStore.aliases();

        PrivateKey signingPrivateKey = null;
        X509Certificate signingCert = null;

        while (aliases.hasMoreElements()) {
            String aliasName = aliases.nextElement();

            Key key = pkcs12KeyStore.getKey(aliasName, keyStorePassword.toCharArray());
            if (key instanceof PrivateKey) {
                signingPrivateKey = (PrivateKey) key;
                Object cert = pkcs12KeyStore.getCertificate(aliasName);
                if (cert instanceof X509Certificate) {
                    signingCert = (X509Certificate) cert;
                    break;
                }
            }
        }

        X509Certificate appleWWDRCACert = loadDERCertificate(appleWWDRCAFilePath);
        if (signingCert == null || signingPrivateKey == null || appleWWDRCACert == null) {
            throw new IOException("Couldn't load all the neccessary certificates/keys");
        }

        return new PassbookSigningInformation(signingCert, signingPrivateKey, appleWWDRCACert);
    }

    public static KeyStore loadPKCS12File(final String filePath, final String password) throws IOException, NoSuchAlgorithmException,
            CertificateException, KeyStoreException, NoSuchProviderException {
        addBCProvider();
        KeyStore keystore = KeyStore.getInstance("PKCS12", BouncyCastleProvider.PROVIDER_NAME);

        keystore.load(new FileInputStream(filePath), password.toCharArray());
        return keystore;
    }

    public static X509Certificate loadDERCertificate(final String filePath) throws IOException, CertificateException {
        FileInputStream certificateFileInputStream = null;
        try {
            certificateFileInputStream = new FileInputStream(filePath);
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", BouncyCastleProvider.PROVIDER_NAME);
            Certificate certificate = certificateFactory.generateCertificate(certificateFileInputStream);
            if (certificate instanceof X509Certificate) {
                return (X509Certificate) certificate;
            }
            throw new IOException("The key from '" + filePath + "' could not be decrypted");
        } catch (IOException ex) {
            throw new IOException("The key from '" + filePath + "' could not be decrypted", ex);
        } catch (NoSuchProviderException ex) {
            throw new IOException("The key from '" + filePath + "' could not be decrypted", ex);
        } finally {
            IOUtils.closeQuietly(certificateFileInputStream);
        }
    }

    private static void addBCProvider() {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

    }

    private static void createPassJSONFile(final PassbookPass pass, final File tempPassDir, final ObjectMapper jsonObjectMapper) throws IOException,
            JsonGenerationException, JsonMappingException {
        File passJSONFile = new File(tempPassDir.getAbsolutePath() + File.separator + PASS_JSON_FILE_NAME);

        ObjectWriter objectWriter = jsonObjectMapper.writer();
        objectWriter.writeValue(passJSONFile, pass);
    }

    private static File createManifestJSONFile(final File tempPassDir, final ObjectMapper jsonObjectMapper) throws IOException,
            JsonGenerationException, JsonMappingException {
        Map<String, String> fileWithHashMap = new HashMap<String, String>();

        HashFunction hashFunction = Hashing.sha1();
        File[] filesInTempDir = tempPassDir.listFiles();
        hashFilesInDirectory(filesInTempDir, fileWithHashMap, tempPassDir.getAbsolutePath() + File.separator, hashFunction);
        File manifestJSONFile = new File(tempPassDir.getAbsolutePath() + File.separator + MANIFEST_JSON_FILE_NAME);
        jsonObjectMapper.writeValue(manifestJSONFile, fileWithHashMap);
        return manifestJSONFile;
    }

    private static void hashFilesInDirectory(final File[] files, final Map<String, String> fileWithHashMap, final String workingDirectory,
            final HashFunction hashFunction) throws IOException {
        for (File passResourceFile : files) {
            if (passResourceFile.isFile()) {
                HashCode hash = Files.hash(passResourceFile, hashFunction);
                String name = passResourceFile.getAbsolutePath().replace(workingDirectory, "");
                fileWithHashMap.put(name, Hex.encodeHexString(hash.asBytes()));
            } else if (passResourceFile.isDirectory()) {
                hashFilesInDirectory(passResourceFile.listFiles(), fileWithHashMap, workingDirectory, hashFunction);
            }
        }
    }

    private static byte[] createZippedPassAndReturnAsByteArray(final File tempPassDir) throws IOException {
        ByteArrayOutputStream byteArrayOutputStreamForZippedPass = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStreamForZippedPass);
        zip(tempPassDir, tempPassDir, zipOutputStream);
        zipOutputStream.close();
        return byteArrayOutputStreamForZippedPass.toByteArray();
    }

    private static final void zip(final File directory, final File base, final ZipOutputStream zipOutputStream) throws IOException {
        File[] files = directory.listFiles();
        byte[] buffer = new byte[ZIP_BUFFER_SIZE];
        int read = 0;
        for (int i = 0, n = files.length; i < n; i++) {
            if (files[i].isDirectory()) {
                zip(files[i], base, zipOutputStream);
            } else {
                FileInputStream fileInputStream = new FileInputStream(files[i]);
                ZipEntry entry = new ZipEntry(getRelativePathOfZipEntry(files[i], base));
                zipOutputStream.putNextEntry(entry);
                while (-1 != (read = fileInputStream.read(buffer))) {
                    zipOutputStream.write(buffer, 0, read);
                }
                fileInputStream.close();
            }
        }
    }

    private static String getRelativePathOfZipEntry(final File fileToZip, final File base) {
        String relativePathOfFile = fileToZip.getPath().substring(base.getPath().length() + 1);
        if (File.separatorChar != '/') {
            relativePathOfFile = relativePathOfFile.replace(File.separatorChar, '/');
        }

        return relativePathOfFile;
    }

    @JsonFilter("pkPassFilter")
    class PkPassFilterMixIn {
        // just a dummy
    }

    @JsonFilter("validateFilter")
    class ValidateFilterMixIn {
        // just a dummy
    }

    @JsonFilter("charsetFilter")
    class CharsetFilterMixIn {
        // just a dummy
    }
}