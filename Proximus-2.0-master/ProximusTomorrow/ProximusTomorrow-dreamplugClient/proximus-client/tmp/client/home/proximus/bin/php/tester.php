<?php
include '/home/proximus/bin/php/RegistrationManager.php';
include '/home/proximus/bin/php/Proximus.php';

unset($_POST);
$_POST=array('field1'=>'Eric Johansson','field2'=>'kneeven@gmail.com','field3'=>'14042776402');

RegistrationManager::register();

/*register($callback = NULL, $validate = NULL, $timezone = NULL) */
Proximus::register();

Util::test();
?>