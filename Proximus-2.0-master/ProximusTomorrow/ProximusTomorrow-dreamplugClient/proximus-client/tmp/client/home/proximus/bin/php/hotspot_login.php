<?php
if ( isset($_REQUEST['URL']) )
{
    $REDIRECT= $_REQUEST['URL'];
}
else
{
    $REDIRECT="http://www.proximusmobility.com/";
}
$REMOTE_ADDR = $_SERVER['REMOTE_ADDR'];
$output ="";
#echo "Connected IP:".$REMOTE_ADDR;
$output = shell_exec('/usr/bin/python /home/proximus/bin/portal.py open add '.$REMOTE_ADDR);
shell_exec($output);
header("Location: ".$REDIRECT);
?>