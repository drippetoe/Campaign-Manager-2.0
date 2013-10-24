<?php

/**
 * Description of RegistrationManager
 *
 * @author eric
 */
class RegistrationManager {

  //public static $REGISTRATION_LOG_DIR = "/var/lib/tomcat6/logs/registrations";

  public static $REGISTRATION_LOG_DIR = "/home/proximus/logs/queue";

  static public function register() {
    date_default_timezone_set('UTC');
    $REMOTE_ADDR = $_SERVER['REMOTE_ADDR'];
    $CLIENT_MAC_ADDR = trim(shell_exec("arp -n $REMOTE_ADDR | grep captive | cut -d' ' -f4"));
    $CLIENT_MAC_ADDR = strtoupper(str_replace(":", "", $CLIENT_MAC_ADDR));

    $LOG_DATA = explode("_", trim(shell_exec("grep wifi /home/proximus/config/log.properties")));
    $MAC_ADDR = $LOG_DATA[3];
    $CAMPAIGN_ID = $LOG_DATA[4];
    
    $timestamp = time();
    $filename = "registration_bluegiga_" . $MAC_ADDR . "_" . $CAMPAIGN_ID . "_" . strftime("%Y-%m-%d.%H-%M-%S") . ".log";
    $mode = "w";
    $handle = fopen(RegistrationManager::$REGISTRATION_LOG_DIR . "/" . $filename, $mode);
    fwrite($handle, "eventDate=$timestamp\n");
    fwrite($handle, "macAddress=" . $CLIENT_MAC_ADDR . "\n");

    foreach($_POST as $key => $val )
    {
        fwrite($handle, $key . "=" . $val . "\n");
    }
    fclose($handle);
  }
}
?>