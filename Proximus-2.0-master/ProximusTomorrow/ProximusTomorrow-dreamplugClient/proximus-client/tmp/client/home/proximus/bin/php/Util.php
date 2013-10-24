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
        return "dreamplug";
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
		$CLIENT_MAC_ADDR = trim(shell_exec("arp -n | grep " . $REMOTE_ADDR . " | awk '{print $3}'"));
        $CLIENT_MAC_ADDR = strtoupper(str_replace(":", "", $CLIENT_MAC_ADDR));
        return $CLIENT_MAC_ADDR;
    }
    
    public static function getSERVER_URL() {
        $client_config_file = "/home/proximus/bin/client/proximus/config/ClientURISettings.py";
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
	
	public static function test(){
		echo "PLATFORM: ".Util::getPLATFORM()."<br/>\n";
		echo "DEVICE MAC: ".Util::getDEVICE_MAC()."<br/>\n";
		echo "CAMPAIGN: ".Util::getCAMPAIGN()."<br/>\n";
		echo "CONNECTED MAC: ".Util::getCONNECTED_MAC()."<br/>\n";
		echo "SERVER URL: ".Util::getSERVER_URL()."<br/>\n";
	}
}
