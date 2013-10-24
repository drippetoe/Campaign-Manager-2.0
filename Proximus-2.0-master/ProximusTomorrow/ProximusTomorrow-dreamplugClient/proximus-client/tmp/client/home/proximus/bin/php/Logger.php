<?php

require_once("Util.php");

/**
 * Description of Logger
 * Example usage:
 * Logger::register($_POST,$_SERVER['REMOTE_ADDR'])
 * @author Eric Johansson
 */
class Logger {

    private static $LOG_DIR = "/home/proximus/logs/queue";
    private static $DATE_FORMAT = "Y-m-d.H-i-s";

    private static function writeToLog($LOGTYPE, $MSG, $TIMEZONE=NULL) {
        if ($TIMEZONE == NULL) {
            date_default_timezone_set('UTC');
        } else {
            date_default_timezone_set($TIMEZONE);
        }
        
        $timestamp = date(Util::$DATE_FORMAT);
        $file = Util::$LOG_DIR . "/" . $LOGTYPE . "_" . Util::getPLATFORM() . "_" . Util::getDEVICE_MAC() . "_" . Util::getCAMPAIGN() . "_${timestamp}.log";
        $file_handle = null;
        if (file_exists($file)) {
            $file_handle = fopen($file, 'a') or die("Unable to open ${file}, check permissions");
        } else {
            $file_handle = fopen($file, 'w') or die("Unable to create ${DIR}${file}, check permissions");
        }
        if (is_writable($file)) {
            fputs($file_handle, $MSG);
            fclose($file_handle);
            return true;
        } else {
            echo "${file} Is not writable.";
        }
        return false;
    }

    public static function register($POST = NULL, $REMOTE_ADDR = NULL, $TIMEZONE = NULL) {
        if ($POST == null || $POST == "") {
            $POST = $_POST;
        }
        if ($TIMEZONE == NULL) {
            date_default_timezone_set('UTC');
        } else {
            date_default_timezone_set($TIMEZONE);
        }
        $timestamp = date(Util::$DATE_FORMAT);
        $msg = "";
        $msg .= "eventDate=${timestamp}\n";
        $msg .= "macAddress=" . Util::getCONNECTED_MAC($REMOTE_ADDR) . "\n";
        foreach ($POST as $key => $val) {
            $msg .= "" . $key . "=" . $val . "\n";
        }
        return Logger::writeToLog("registration", $msg);
    }

    public static function barcode($barcode, $identifier, $mac, $timezone = NULL) {
        if ($mac == NULL) {
            $mac = Util::getCONNECTED_MAC(null);
        }
        if ($timezone == NULL) {
            date_default_timezone_set('UTC');
        } else {
            date_default_timezone_set($timezone);
        }
        $timestamp = date(Logger::$DATE_FORMAT);
        $msg = "" . $timestamp . "," . $barcode . "," . $mac . "," . $identifier . "\n";
        return Logger::writeToLog("barcode", $msg);
    }

}

