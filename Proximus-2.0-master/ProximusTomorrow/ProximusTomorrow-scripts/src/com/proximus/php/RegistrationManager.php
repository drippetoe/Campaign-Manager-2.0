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
    $CLIENT_MAC_ADDR = RegistrationManager::getMacAddressForIP($_SERVER['REMOTE_ADDR']);
    
    
    $LOG_DATA = explode("_", trim(shell_exec("grep wifi /home/proximus/config/log.properties")));
    $MAC_ADDR = $LOG_DATA[3];
    $CAMPAIGN_ID = $LOG_DATA[4];
    
    $timestamp = time();
    $filename = "registration_dreamplug_" . $MAC_ADDR . "_" . $CAMPAIGN_ID . "_" . strftime("%Y-%m-%d.%H-%M-%S") . ".log";
    $mode = "w";
    $handle = fopen(RegistrationManager::$REGISTRATION_LOG_DIR . $filename, $mode);
    fwrite($handle, "eventDate=".$timestamp . "\n");
    fwrite($handle, "macAddress=". $CLIENT_MAC_ADDR . "\n");
    
    foreach($_POST as $key => $val )
    {
        fwrite($handle, $key . "=" . $val . "\n");
    }
    fclose($handle);
  }

  static private function getMacAddressForIP($ipAddress) {
    $filename='/var/lib/misc/dnsmasq.leases';
    $lease = array();
    $handle =fopen($filename, "r");
    $ln= 0;
    while ($line= fgets ($f)) {
        ++$ln;
        $spl = explode(" ",$line);
        $lease[$spl[2]] = $spl[1];
        
    }
    fclose ($handle);
    //1325825603 b8:8d:12:00:cf:e2 192.168.3.123 macbook 01:b8:8d:12:00:cf:e2
    //$lease['127.0.0.1']="b8:8d:12:00:cf:e2";
    if(isset ($lease[$ipAddress])){
      return $lease[$ipAddress];
    }
    else{
      return "";
    }
  }

}

?>
