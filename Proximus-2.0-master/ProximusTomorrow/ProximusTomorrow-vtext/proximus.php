<?php

/**
 *  ProximusToken
 * salt ea219dae-bb66-4d48-9869-b1e3515044a1
 * api_password 52d81ead-f70c-4e60-ac1e-6fb6f8926eec
 */
class ProximusApiHelper {

  private $baseURL = "http://api.proximusmobility.com:8080/ProximusTomorrow-war/api";
  private $user;
  private $salt;
  private $password;
  private $api;
  private $params;

  function __construct($user, $salt, $password, $api = NULL, $params = NULL) {
    $this->user = $user;
    $this->salt = $salt;
    $this->password = $password;
    $this->api = $api;
    $this->params = $params;
  }

  public function getUser() {
    return $this->user;
  }

  public function getSalt() {
    return $this->salt;
  }

  public function getPassword() {
    return $this->password;
  }

  public function setApi($api) {
    $this->api = $api;
  }

  public function getApi() {
    if ($this->api == NULL) {
      throw new Exception("API not specified!");
    }
    return $this->api;
  }

  public function setParams($params) {
    if (count($params) > 0) {
      $this->params = $params;
    }
  }

  public function getParams() {
    if (count($this->params) < 1) {
      $this->params = NULL;
    }
    return $this->params;
  }

  public function getAPIURL($DEBUG = NULL) {
    if ($DEBUG != NULL) {
      return $DEBUG . "/" . $this->getApi();
    }
    return $this->baseURL . "/" . $this->getApi();
  }

  public function getPOSTFields() {
    $postFields = array('username' => $this->getUser(), 'token' => $this->getToken());
    if ($this->getParams() != NULL && count($this->getParams()) > 0) {
      $params = $this->getParams();
      $postFields = array_merge($postFields, $params);
    }
    print_r($postFields);
    return $postFields;
  }

  private function getToken() {
    $url = $this->getAPIURL() . "/" . $this->getUser() . "/" . $this->getPassword();
    if ($this->getParams() != NULL && count($this->getParams()) > 0) {
      $params = $this->getParams();
      unset($params['responseType']);
      $url .= "/" . implode("/", $params);
    }
    
    $token = strtoupper(md5($this->getSalt() . $url));
    return $token;
  }

  public function POST($api = NULL, $params = NULL, $accept = "application/json") {
    if ($api != NULL) {
      $this->setApi($api);
    }
    if ($params != NULL) {
      $this->setParams($params);
    }
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $this->getAPIURL("http://192.168.1.211:8080/ProximusTomorrow-war/api"));
    curl_setopt($ch, CURLOPT_POST, count($this->getPOSTFields()));
    curl_setopt($ch, CURLOPT_POSTFIELDS, http_build_query($this->getPOSTFields()));
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, TRUE);
    curl_setopt($ch, CURLOPT_HTTPHEADER, array(
      'Accept: ' . $accept)
    );
    $raw_result = curl_exec($ch);
    curl_close($ch);
    return $raw_result;
  }

}


$t = new ProximusApiHelper("api@valutext.com", "ea219dae-bb66-4d48-9869-b1e3515044a1", "52d81ead-f70c-4e60-ac1e-6fb6f8926eec");
if ($_SERVER["REQUEST_METHOD"] == "GET") {
  $raw_result = $t->POST("categorylist");

  $result = json_decode($raw_result);
  if ($result->status != "OK") {
    //echo $result->status_message;
  } else {
    $rows = 0;
    echo "<form method=\"POST\" action=\"proximus.php\">
Phone: <input name=\"msisdn\" value=\"14042776402\" /><br/>  
Email Address: <input name=\"email\" value=\"ejohansson@proximusmobility.com\"><br/>
Mobile Carrier: <input name=\"carrier\" value=\"AT&T\" ><br/>
	ZipCode: <input name=\"zipcode\" value=\"30309\" ><br/>
	Gender: <select name=\"gender\" size=\"1\">
		<option value=\"M\" selected>Male</option>
		<option value=\"F\">Female</option></select><br/>

<table><tr>";
    foreach ($result->category as $index => $category) {
      if ($rows % 3 == 0) {
        echo "</tr><tr>";
      }
      printf("<td><input id=\"%s\" type=\"checkbox\" name=\"categories[]\" value=\"%s\" /><label for=\"%s\">%s</label><td>", $category->id, $category->name, $category->id, $category->name);
      $rows++;
    }
    echo "</tr></table><br/>\n<input type=\"submit\" value=\"Set Preferences\"/></form>";
  }
} else if ($_SERVER["REQUEST_METHOD"] == "POST") {
  //$raw_result = $t->POST("categorylist");
  $category = NULL;
  $msisdn = $_POST['msisdn'];
  if (isset($_POST["categories"])) {
    $category = implode(",", $_POST["categories"]);
  }
  //echo "Categories: " . urlencode($category);

  $email = $_REQUEST['email'];
  $zipcode = $_REQUEST['zipcode'];
  $gender = $_REQUEST['gender'];
  $carrier = $_REQUEST['carrier'];
  $params = array(
    'msisdn' => $msisdn,
    'email' => $email,
    'zipcode' => $zipcode,
    'carrier' => $carrier,
    'gender' => $gender,
    'category' => $category,
    'responseType' => 'JSON'
  );


  //$rawurl = $baseurl . $username . "/" . $api_password .  "/" . $msisdn . "/" . $email . "/" . $zipcode . "/" . $carrier . "/" . $gender . "/" . $category;
  $raw_result = $t->POST("webregistration", $params);

  $result = json_decode($raw_result);
//  ["subscriber"]=>
//  object(stdClass)#3 (7) {
//    ["id"]=>
//    int(5153)
//    ["registrationDate"]=>
//    int(1347392562000)
//    ["msisdn"]=>
//    string(11) "14042776402"
//    ["email"]=>
//    NULL
//    ["zipcode"]=>
//    NULL
//    ["gender"]=>
//    NULL
//    ["status"]=>
//    string(18) "SMS_OPT_IN_PENDING"
//  }
  
} else {
  
}
?>