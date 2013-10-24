<?php

/**
 * Description of Logger
 * Example usage:
 * Logger::register($_POST,$_SERVER['REMOTE_ADDR'])
 * @author Eric Johansson
 */
class Util {

    public static $LOG_DIR = "/home/proximus/logs/queue";
    public static $DATE_FORMAT = "Y-m-d.H-i-s";

    public static function getPLATFORM() {
        return "bluegiga";
    }

    public static function getDEVICE_MAC() {
        $DATA = explode("_", trim(shell_exec("grep wifi /home/proximus/config/log.properties")));
        if (isset($DATA[3])) {
            return $DATA[3];
        }
        return null;
    }

    public static function getCAMPAIGN() {
        $DATA = explode("_", trim(shell_exec("grep wifi /home/proximus/config/log.properties")));        
        if (isset($DATA[4])) {
            return $DATA[4];
        }
        return null;
    }

    public static function getCONNECTED_MAC($REMOTE_ADDR = NULL) {
        if ($REMOTE_ADDR == null || $REMOTE_ADDR == "") {
            if (!isset($_SERVER['REMOTE_ADDR'])) {
                return "-";
            } else {
                $REMOTE_ADDR = $_SERVER['REMOTE_ADDR'];
            }
        }
        $CLIENT_MAC_ADDR = trim(shell_exec("arp -n ${REMOTE_ADDR} | grep captive | cut -d' ' -f4"));
        if ($CLIENT_MAC_ADDR == null || $CLIENT_MAC_ADDR == "") {
            $CLIENT_MAC_ADDR = trim(shell_exec("arp -vn ${REMOTE_ADDR} | grep eth | cut -d' ' -f4"));
        }
        $CLIENT_MAC_ADDR = strtoupper(str_replace(":", "", $CLIENT_MAC_ADDR));
        return $CLIENT_MAC_ADDR;
    }
    
    public static function getSERVER_URL() {
        $client_config_file = "/home/proximus/client/proximus/config/ClientURISettings.py";
        $is_dev = trim(shell_exec("grep 'DEV =' " . $client_config_file . '| cut -f2 -d "="'));
        
        if ( $is_dev == "True" )
        {
            $port = 8080;
            $server = trim(shell_exec("grep ' DEV_SERVER = ' " . $client_config_file . ' | cut -f2 -d \'"\''));
        }
        else
        {
            $port = 80;
            $server = trim(shell_exec("grep 'PROD_SERVER = ' " . $client_config_file . ' | cut -f2 -d \'"\''));
        }
        
        return $server . ":" . $port . '/ProximusTomorrow-war';
    
    }
}
