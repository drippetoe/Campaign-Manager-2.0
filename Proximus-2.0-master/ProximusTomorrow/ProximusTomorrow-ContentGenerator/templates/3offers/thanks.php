<?php
session_start();
include_once("/home/proximus/bin/php/RegistrationManager.php");
// verify, redirect to form if needed
// set to session, incase you send back to form

RegistrationManager::register();
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta name="viewport" content="width=device-width, user-scalable=no" />
	<meta http-equiv="refresh" content="2;URL='index.html'">
	<title>Thanks</title>
	<link type="text/css" rel="stylesheet" href="prox.css" />
</head>
<body>
	<div id="container">
	
		<div id="header">
			#PROX#ImageUploader_logo#PROX#
		</div>
		<br />
		<div id="header">
			<p class="header">THANKS FOR REGISTERING!</p>
			<br />
		</div>
		
		
		
		<div id="container-footer">
			<p class="legal">
			#PROX#TextEditor_COPYRIGHT#PROX#
			</p>
		</div> 
	</div>
</body>
</html>