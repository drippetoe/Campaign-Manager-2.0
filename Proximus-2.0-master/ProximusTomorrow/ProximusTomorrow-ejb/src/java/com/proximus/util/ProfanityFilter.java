/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.util;

/**
 *
 * @author Gilberto Gaxiola
 */
public class ProfanityFilter {

    private static String[] BLACKLIST = {"fuck", "asshole", "cunt", "bitch", "bastard", "shit"};

    private static boolean checkBlackList(String message) {
        message = message.toLowerCase();

        for (String s : BLACKLIST) {
            if (message.contains(s)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkIfBannedWord(String word, String message) {
        word = word.toLowerCase();
        message = message.toLowerCase();
        if (checkBlackList(message)) {
            return false;
        }
        boolean flag = message.matches("(.*)(\\s+|\\W+|\\d+)" + word + "(\\s+|\\W+|\\d+)(.*)");
        if (flag) {
            return false;
        } else {
            flag = message.matches("^" + word + "(\\s+|\\W+|\\d+)(.*)");
            if (flag) {
                return false;
            } else {
                flag = message.matches("(.*)" + word + "$");
                if (flag) {
                    return false;
                }
            }
        }
        return true;
    }
}
