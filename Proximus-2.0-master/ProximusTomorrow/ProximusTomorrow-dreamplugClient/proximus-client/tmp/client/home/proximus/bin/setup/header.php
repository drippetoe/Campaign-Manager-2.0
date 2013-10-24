<?php
session_start();
session_register('login');

function signIn($post) {
	shell_exec("rm -f /home/proximus/bin/setup/network.list");
	if ($post['user'] == 'proximus') {
		if ($post['pass'] == 'multipass') {
			$_SESSION['login'] = true;
			return true;
		}
	}
	$_SESSION['login'] = false;
	session_destroy();
	return false;
}

function signOut() {
	//Save stuff here
	$_SESSION = null;
	session_destroy();
}

if (!$_SESSION['login']) {
	if(signIn($_POST)){
		$pwd = shell_exec('pwd');
		$cmd = "bash " . trim($pwd) . "/setChannel.sh";		
		$channel = shell_exec($cmd);
		$_SESSION['wifi_channel']=$channel;
		$ssid = shell_exec("cat /etc/hostapd.conf | grep ssid= | grep -v _ssid | cut -f 2 -d '='");
		$mac = shell_exec("/sbin/ifconfig -a nap | grep HWaddr | awk '{print $5}' | sed 's/://g'");
		if (empty($mac)) {
				$mac = shell_exec("/sbin/ifconfig -a eth0 | grep HWaddr | awk '{print $5}' | sed 's/://g'");
		}
		$_SESSION['ap_ssid'] = $ssid;
		$_SESSION['device_mac'] = strtoupper($mac);
	}
}

if ($_POST['signout']) {
	signOut();
}
if ($_POST['save_reboot']) {
	signOut();
}


?>
<!DOCTYPE html>
<html> 
	<head> 
		<title>Proximus Mobility, LLC</title>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<link rel="stylesheet" href="jquery.mobile-1.0.min.css" />
		<link rel="stylesheet" href="jquery.mobile.structure-1.0.1.min.css" />
		<link rel="stylesheet" href="themes/proximus.css" />
		<script type="text/javascript" src="jquery-1.7.1.min.js"></script>
		<script type="text/javascript" src="jquery.mobile-1.0.min.js"></script>
	</head>
	<body> 
