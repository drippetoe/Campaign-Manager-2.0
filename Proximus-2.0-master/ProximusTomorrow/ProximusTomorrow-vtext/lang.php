<?php
session_start();
header('Cache-control: private');
$languages = array(
        'en',// default
        'es',
        //'sv'
);
$locale = "en";
$result = array();
/*
preg_match("/([a-z]{2})/", $_SERVER['REQUEST_URI'], $result);
if ($result) {
    if (in_array($result[0], $languages)) {
      $locale = $result[0];
    }else{
	$locale = "en";
    }

}
*/
$locale = @http_negotiate_language($languages, $result);
include './i18n/lang.'. $locale .'.php';
extract($MESSAGES,EXTR_PREFIX_ALL,"message");
echo "<!-- Locale: ".$locale." -->";
?>
