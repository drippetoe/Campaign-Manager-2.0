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
    curl_setopt($ch, CURLOPT_URL, $this->getAPIURL());
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
?>