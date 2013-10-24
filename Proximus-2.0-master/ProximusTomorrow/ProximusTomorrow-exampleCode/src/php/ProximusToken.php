<?php

/*
 * @Copyright 2012, Proximus Mobility, LLC., all rights reserved
 * @author dshaw
 */
class ProximusToken {

	public function __construct($salt) {
       $this->salt = $salt;
    }

	public function generateAuthenticationToken($url)
	{
		$toEncode = $this->salt . $url;
	     return strtoupper(md5($toEncode));   
	}
}
        
$tokenGenerator = new ProximusToken("MYCOMPANYISAWESOME");

$URL_BASE = "http://devices.proximusmobility.com/api/";
$username = urlencode("user@domain.com");
$password = "iwadasnin2012wisawuts";

$encodingUrl = $URL_BASE . $username . "/" . $password . "/params1/params2/params..n";
$token = $tokenGenerator->generateAuthenticationToken($encodingUrl); 
$requestUrl = $URL_BASE . $username . "/" . $token . "/params1/params2/params..n";

print("Raw URL was " . $encodingUrl . "<br/>\n");
print("Token would be " . $token . "<br/>\n");
print("Request URL would be " . $requestUrl . "<br/>\n");

?>