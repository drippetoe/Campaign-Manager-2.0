package com.proximus.util;

import org.apache.commons.lang3.StringUtils;



/**
 * MAC Address helper functions
 * @author eric
 */
public class MACUtil
{
    /**
     * Test
     * @param args 
     */
    public static void main(String[] args)
    {
        String mac = "0007800160AB";
        String colon = MACUtil.colonize(mac);
        System.out.println("Colonize"+colon);
        System.out.println("DeColonize"+MACUtil.decolonize(colon));
    }
    
    /**
     * Add colons to a MAC address
     * AABBCCDDEEFF becomes AA:BB:CC:DD:EE:FF
     * @param MAC
     * @return the formatted MAC address string
     */
    public static String colonize(String MAC){
        StringBuilder sb = new StringBuilder();
        if(MAC.length()==12){
            int i = 0;
            for (i = 0; i < MAC.length()-2; i+=2)
            {
                sb.append(MAC.substring(i, i+2));
                sb.append(":");
                
            }
            sb.append(MAC.substring(i, i+2));
            return sb.toString();
        }        
        return MAC;
    }
    
    /**
     * Remove colons from MAC address
     * @param MAC
     * @return the formatted MAC address string
     */
    public static String decolonize(String MAC){
        MAC = StringUtils.replace(MAC, ":", "");
        return MAC;
    }
    
    /**
     * Compares MAC addresses ignoring colons and case.
     * @param MAC1 first MAC address
     * @param MAC2 second MAC address
     * @return true if equal, false if not
     */
    public static boolean equalsIgnoreFormatting(String MAC1, String MAC2){
        MAC1 = decolonize(MAC1);
        MAC2 = decolonize(MAC2);
        MAC1 = MAC1.toUpperCase();
        MAC2 = MAC2.toUpperCase();        
        return MAC1.equals(MAC2);
    }
}
