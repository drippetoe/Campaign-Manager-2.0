<?php
$params = array();
try {
  if (isset($_REQUEST['lat']) && isset($_REQUEST['long'])) {
    $lat = $_REQUEST['lat'];
    $lon = $_REQUEST['long'];
    $params = array("latitude" => $_REQUEST['lat'], "longitude" => $_REQUEST['long'], "username" => "api@valutext.com");
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, "http://api.proximusmobility.com:8080/ProximusTomorrow-war/api/closestproperty");
    curl_setopt($ch, CURLOPT_POST, count($params));
    curl_setopt($ch, CURLOPT_POSTFIELDS, http_build_query($params));
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, TRUE);
    curl_setopt($ch, CURLOPT_HTTPHEADER, array(
      'Accept: ' . "application/json")
    );
    $raw_result = curl_exec($ch);
    curl_close($ch);
    $response = json_decode($raw_result);
    if ($raw_result != NULL) {
      echo $response->closestPropertyWebHash;
    }else{
      echo "NULL"; 
    }
  }
} catch (Exception $ex) {
  echo "NULL"; 
  //echo 'Caught exception: ', $ex->getMessage(), "<br/>\n";
}
?>
