/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.util;

import java.io.*;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import javax.crypto.Cipher;

/**
 *
 * @author dev core [support@kazemobile.com]
 */
public class RSAUtils {
    private BigInteger decryptMod;
    private BigInteger decryptExp;
    private BigInteger ecryptMod;
    private BigInteger ecryptExp;
    
    private static String HexAlphaSet = "0123456789ABCDEF";
    
    public PublicKey readKeyFromFileForEncryption(String keyFileName) throws IOException {
        try {
            BigInteger[] components = readKeyComponentsFromFile(keyFileName);
            setEcryptComponents(components[0], components[1]);

            return getEcryptKey();
        } catch (Exception e) {
            throw new RuntimeException("Serialisation error", e);
        }
    }
    
    public PrivateKey readKeyFromFileForDecryption(String keyFileName) throws IOException {
        try {
            BigInteger[] components = readKeyComponentsFromFile(keyFileName);
            setDecryptComponents(components[0], components[1]);

            return getDecryptKey();
        } catch (Exception e) {
            throw new RuntimeException("Serialisation error", e);
        }
    }
    
    public BigInteger[] readKeyComponentsFromFile(String keyFileName) throws IOException {
        BigInteger[] components = new BigInteger[2];
        
        File file = new File(keyFileName);
        InputStream in = new FileInputStream(file);
        ObjectInputStream oin = new ObjectInputStream(
                                    new BufferedInputStream(in));
        try {
            components[0] = (BigInteger) oin.readObject();
            components[1] = (BigInteger) oin.readObject();

            return components;
        } catch (Exception e) {
            throw new RuntimeException("Serialisation error", e);
        } finally {
            oin.close();
        }
    }

    public void setDecryptComponents(BigInteger modulus, BigInteger exponent) {
        decryptMod = modulus;
        decryptExp = exponent;
    }

    public void setEcryptComponents(BigInteger modulus, BigInteger exponent) {
        ecryptMod = modulus;
        ecryptExp = exponent;
    }

    PrivateKey getDecryptKey() {
        try {
            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(decryptMod, decryptExp);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PrivateKey privKey = fact.generatePrivate(keySpec);
            return privKey;
        } catch (Exception ex) {
            System.out.println("Decrypt Key: " + ex);
        }

        return null;
    }

    PublicKey getEcryptKey() {
        try {
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(ecryptMod, ecryptExp);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PublicKey pubKey = fact.generatePublic(keySpec);
            return pubKey;
        } catch (Exception ex) {
            System.out.println("Encrypt Key: " + ex);
        }

        return null;
    }
    
    public byte[] encrypt(byte[] data) throws Exception {
        PublicKey pubKey = getEcryptKey();
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return cipher.doFinal(data);
    }
    
    public byte[] decrypt(byte[] data) throws Exception {
        PrivateKey privKey = getDecryptKey();
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privKey);
        return cipher.doFinal(data);
    }
    
    public static String toHexString(byte[] data) {
        //System.out.println("toHexString: " + new String(data));
        if( null == data ){
            System.out.println("toHexString: data is null");
            return null;
        }    
        final StringBuilder hex = new StringBuilder( 2 * data.length );
        for( final byte b : data ) {
            //System.out.println("in for loop");
            hex.append(HexAlphaSet.charAt(( b & 0xF0 ) >> 4))
                    .append(HexAlphaSet.charAt(( b & 0x0F )));
        }
        
        return hex.toString();
    }
    
    public static byte[] toByteArray(String hexData) {
        int len = hexData.length();
        byte[] data = new byte[len / 2];
        for(int ndx=0;ndx < len;ndx+=2) {
            data[ndx/2] = (byte)((Character.digit(hexData.charAt(ndx), 16) << 4)
                                + Character.digit(hexData.charAt(ndx+1), 16));
        }
        
        return data;
    }
}