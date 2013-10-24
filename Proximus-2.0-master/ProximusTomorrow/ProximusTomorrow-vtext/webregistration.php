<?php

//########################################################
//### SET THESE VALUES AS PROVIDED BY PROXIMUS MOBILITY
//########################################################

$username =  "api@valutext.com";
$api_password = "52d81ead-f70c-4e60-ac1e-6fb6f8926eec";
$company_salt = "ea219dae-bb66-4d48-9869-b1e3515044a1";

$baseurl = "http://api.proximusmobility.com:8080/ProximusTomorrow-war/api/webregistration/";
//########################################################
class ProximusToken
{
    private $salt;
    public function __construct($salt){
        $this->salt = $salt;
    }

    public function generateAuthenticationToken($url)
    {
        return strtoupper(md5($this->salt.$url));
    }
}
//########################################################
//# Sample Sign up form -- should have verification 
//# and Captcha to prevent spamming of the API
//########################################################
if ( $_SERVER["REQUEST_METHOD"] == "GET" )
{
?>
	<html>
	<form method="POST" ACTION="<?php echo $_SERVER['PHP_SELF']; ?>">
	MSISDN: <input name="msisdn" value="14042776402" size="80"><br/>
	Email Address: <input name="email" value="ejohansson@proximusmobility..com" size="80"><br/>
	Mobile Carrier: <input name="carrier" value="AT&T" size="80"><br/>
	ZipCode: <input name="zipcode" value="30309" size="80"><br/>
	Gender: <select name="gender" size=1">
		<option value="M" selected>Male</option>
		<option value="F">Female</option></select><br/>
	Categories: <input name="category" value="Miscellaneous,Computers,Apparel" size="80"/><br/>
	<input type="submit" value="Send me a POSTed registration!">
	</form>
	</html>
<?php
}
elseif ( $_SERVER["REQUEST_METHOD"] == "POST" )
{
//########################################################
//# Example sending via PHP to the Proximus REST API 
//########################################################
	$msisdn = $_REQUEST['msisdn'];
    	$email = $_REQUEST['email'];
    	$zipcode = $_REQUEST['zipcode'];
    	$gender = $_REQUEST['gender'];
    	$carrier = $_REQUEST['carrier'];
    	$category = urlencode($_REQUEST['category']);
	
	$rawurl = $baseurl . $username . "/" . $api_password . 
        "/" . $msisdn .  "/" . $email . "/" . $zipcode . "/" . $carrier . "/" . $gender . "/" . $category;

	$tokenGenerator = new ProximusToken($company_salt);
	$token = $tokenGenerator->generateAuthenticationToken($rawurl);

    	$post = array (  'username' => $username,
                     'token' => $token,
                     'msisdn' => $msisdn,
    				  'email' => $email,
    				  'zipcode' => $zipcode,
    				  'carrier' => $carrier,
    				  'gender' => $gender,
    				  'category' => $category,
    				  'responseType' => 'JSON'
    	);
    	$post_body = http_build_query($post);
    	
    	// send the request using PHP curl
	$ch = curl_init();
    
	$fields = array(
            '__VIEWSTATE'=>urlencode($state),
            '__EVENTVALIDATION'=>urlencode($valid),
            'btnSubmit'=>urlencode('Submit')
        );

	//set the url, number of POST vars, POST data
	//curl_setopt($ch,CURLOPT_URL,$baseurl);
    
    curl_setopt($ch,CURLOPT_URL,"http://192.168.1.211:8080/ProximusTomorrow-war/api/webregistration");
	curl_setopt($ch,CURLOPT_POST,count($fields));
	curl_setopt($ch,CURLOPT_POSTFIELDS,$post_body);

	//execute POST
	$result = curl_exec($ch);
	print($result); 
}
?>