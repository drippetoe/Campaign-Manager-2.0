<?php
$REMOTE_ADDR = $_SERVER['REMOTE_ADDR'];
$MAC_ADDR = trim(shell_exec("arp -n $REMOTE_ADDR | cut -d' ' -f4"));
if ( isset($_REQUEST['URL']) )
{
    $REDIRECT= $_REQUEST['URL'];
}
else
{
    $REDIRECT="http://www.google.com/";
}
if ( $MAC_ADDR == "in"  )
{
    print "No internet for you!";
    exit;
}

$portalpath = "/tmp/captiveportal";
if ( ! file_exists($portalpath) )
{
        mkdir($portalpath, 0700);
}

$device_allowed = $portalpath . "/" . $MAC_ADDR . "_" . $REMOTE_ADDR;
//if ( ! file_exists($device_allowed) )
//{
$success = shell_exec("/usr/sbin/captiveportal_ctrl accept " . $MAC_ADDR . " " . $REMOTE_ADDR);
touch($device_allowed);
//}
Header("Location: " . $REDIRECT);
?>