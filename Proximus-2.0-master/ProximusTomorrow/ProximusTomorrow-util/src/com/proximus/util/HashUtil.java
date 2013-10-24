/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.util;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;

/**
 *
 * @author Angela Mercer
 */
public class HashUtil {

    private static final Logger logger = Logger.getLogger(HashUtil.class.getName());
    private static final char kHexChars[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'A', 'B', 'C', 'D', 'E', 'F'};
    private static final String MD5 = "MD5";
    private static final String SALT = "Bedrock10IsAthens";

    public static void main(String[] args)
    {
        String password = "correcthorsebatterystaple";
        String firstname = "super";
        String lastname = "repus";
        String username = "super@proximusmobility.com";
        Formatter formatter = new Formatter();
        String out = formatter.format("INSERT INTO Proximus.user(first_name,last_name,username,password,COMPANY_id) VALUES('%1$s', '%2$s', '%3$s', '%4$s', 1);", firstname, lastname, username, getEncodedPassword(username,password)).toString();
        System.err.println(out);
        System.err.println("GENERATED PASSWORD:\n\t123456789 = "+toSHA1("123456789"));
        System.err.println("GENERATED PASSWORD:\n\tuser@host - 123456789 = "+getEncodedPassword("user@host", "123456789"));
    }
    /**
     * returns the byte array as String
     *
     * @param bytes
     * @return
     */
    public static String getString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[ i];
            sb.append((int) (0x00FF & b));
            if (i + 1 < bytes.length) {
                sb.append("-");
            }
        }
        return sb.toString();
    }

    /**
     * takes a String and returns as a byte array
     *
     * @param str
     * @return
     */
    public static byte[] getBytes(String str) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        StringTokenizer st = new StringTokenizer(str, "-", false);
        while (st.hasMoreTokens()) {
            int i = Integer.parseInt(st.nextToken());
            bos.write((byte) i);
        }
        return bos.toByteArray();
    }

    /**
     * @param toHash
     * @return takes the param and returns the hash value as a String
     *
     */
    public static String toHashMd5(String toHash) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(toHash.getBytes());
            return getString(bytes);
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

    public static String toSHA1(String toHash) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] bytes = md.digest(toHash.getBytes());
            return getString(bytes);
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

    /**
     * Generate a user password to store in the database.
     *
     * @param username - used as a salt to the hash function.
     * @param clearTextPassword The user's password. If the value passed in
     * already contains the prefix "MD5", then it is returned as-is under the
     * assumption that it has already been hashed.
     * @return "MD5" prefix plus the hex digits of a straight MD5 hash of the
     * user's password.
     */
    public static String getEncodedPassword(String username, String clearTextPassword) {
        if (isEncodedPassword(clearTextPassword)) {
            // don't double-encode
            return clearTextPassword;
        }

        // Use a fixed salt + a varying salt (the username) together
        // to reduce the likelihood of a successful brute force attack.
        return MD5 + generateMD5Hash(SALT + username, clearTextPassword);
    }

    public static boolean isEncodedPassword(String password) {
        return password.startsWith(MD5);
    }

    /**
     * Generate an MD5 hash for the given text. The salt is used to protect
     * against any potential pre-generated table lookup attack, also known as a
     * dictionary attack, or similar brute force attacks, to determine the
     * original value.
     *
     * @param salt A set of bytes that is prepended to the text before
     * generating the hash to deter dictionary attacks.
     * @param text The text to hash.
     * @return The MD5 hash of the salt + text as a string containing 32 hex
     * digits.
     */
    public static String generateMD5Hash(String salt, String text) {
        return generateMD5Hash(salt + text);
    }

    /**
     * Generate an MD5 hash for the given text. This method is marked as private
     * because generateMD5Hash(String salt, String text) should be used as it is
     * more secure.
     *
     * @param text The text to hash.
     * @return The MD5 hash of the text as a string containing 32 hex digits.
     * @see generateMD5Hash(String salt, String text)
     */
    private static String generateMD5Hash(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance(MD5);
            md.update(text.getBytes());
            byte[] digest = md.digest();

            StringBuffer hexString = new StringBuffer(2 * digest.length);
            int endOffset = digest.length;

            for (int i = 0; i < endOffset; i++) {
                char highNibble = kHexChars[(digest[i] & 0xF0) >> 4];
                char lowNibble = kHexChars[digest[i] & 0x0F];

                hexString.append(highNibble);
                hexString.append(lowNibble);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error(e);
            return null;
        }
    }
}