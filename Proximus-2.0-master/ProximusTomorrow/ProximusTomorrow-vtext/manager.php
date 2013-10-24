<?php
$mysqli = new mysqli("localhost", "valutext", "valutextreversehorse", "ProximusDenverCoder"); 
//$mysqli = new mysqli("localhost", "root", "password", "proximus");
if ($mysqli->connect_errno) {
  echo "error ".$mysqli->error;  
  var_dump($mysqli);
//header("Location: /404?reason=dbdown");
}else{
  echo "<!-- Connected -->";
}


function getProperty($mysqli, $id) {
  $query = "SELECT * FROM property WHERE web_hash COLLATE latin1_general_cs = ? LIMIT 1";
  if ($stmt = $mysqli->prepare($query)) {
    $stmt->bind_param("s", $id);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($obj = $result->fetch_object()) {
      $result->close();
      $stmt->close();
      //var_dump($obj);
      return $obj;
    }
    $result->close();
    $stmt->close();
  } else {
    echo "Error: " . $mysqli->error;
  }
  return NULL;
}

function listPropertiesByCountry($mysqli, $countryCode) {
  $list = array();
  $query = "SELECT * FROM property WHERE COUNTRY_id = (SELECT id from country WHERE code = ?) AND COMPANY_id IN (SELECT id FROM company WHERE BRAND_id = 1 ) ORDER BY name";
  if ($stmt = $mysqli->prepare($query)) {
    $stmt->bind_param("s", $countryCode);
    $stmt->execute();
    $result = $stmt->get_result();
    while ($obj = $result->fetch_object()) {
      $list[] = $obj;
    }
    $result->close();
    $stmt->close();
  } else {
    echo "Error: " . $mysqli->error;
  }

  return $list;
}


function listCountries($mysqli) {
  $list = array();
  $query = "SELECT * FROM country WHERE id IN (SELECT DISTINCT(COUNTRY_id) FROM property) ORDER BY name";
  if ($stmt = $mysqli->prepare($query)) {
    $stmt->execute();
    $result = $stmt->get_result();
    while ($obj = $result->fetch_object()) {
      $list[] = $obj;
    }
    $result->close();
    $stmt->close();
  } else {
    echo "Error: " . $mysqli->error;
  }
  return $list;
}

function getOffer($mysqli, $id) {
  $query = "SELECT * FROM web_offer WHERE id = ? LIMIT 1";
  if ($stmt = $mysqli->prepare($query)) {
    $stmt->bind_param("s", $id);
    $stmt->execute();
    $result = $stmt->get_result();
    if ($obj = $result->fetch_object()) {
      $result->close();
      $stmt->close();
      return $obj;
    }
  } else {
    echo "Error: " . $mysqli->error;
  }
  return NULL;
}

function listOffersByPropertyAndLocale($mysqli, $property_webhash, $locale) {
  $list = array();
  $query = "SELECT w.* FROM web_offer w WHERE  
w.locale = ? AND(
(w.retail_only = 1 AND w.retailer_id in (SELECT ret.id FROM retailer ret WHERE ret.id in(SELECT pr.retailer_id FROM property_retailer pr WHERE pr.property_id = (SELECT id FROM property WHERE web_hash COLLATE latin1_general_cs = ?))))
OR w.id in(SELECT pw.web_offer_id FROM property_web_offer pw WHERE pw.property_id = (SELECT id FROM property WHERE web_hash COLLATE latin1_general_cs = ?))) AND w.status='Approved' AND w.deleted=0 AND DATE(NOW()) BETWEEN DATE(w.start_date) AND DATE(w.end_date) ORDER BY w.retailer_id

";
  if ($stmt = $mysqli->prepare($query)) {
    $stmt->bind_param("sss", $locale, $property_webhash, $property_webhash);
    $stmt->execute();
    $result = $stmt->get_result();
    while ($obj = $result->fetch_object()) {
      $list[] = $obj;
    }
    $result->close();
    $stmt->close();
  } else {
    echo "Error: " . $mysqli->error;
  }
  return $list;
}

function getRetailerByOffer($mysqli, $offer_id) {
  $query = "SELECT r.* FROM retailer AS r, web_offer AS w WHERE w.id = ? AND w.RETAILER_id = r.id";
  if ($stmt = $mysqli->prepare($query)) {
    $stmt->bind_param("s", $offer_id);
    $stmt->execute();
    $result = $stmt->get_result();
    if ($obj = $result->fetch_object()) {
      $result->close();
      $stmt->close();
      return $obj;
    }
  } else {
    echo "Error: " . $mysqli->error;
  }
}

function getCountryByProperty($mysqli, $property_web_hash) {

  $query = "SELECT c.* FROM country AS c, property AS p WHERE c.id = p.COUNTRY_id AND p.web_hash = ?";
  if ($stmt = $mysqli->prepare($query)) {
    $stmt->bind_param("s", $property_web_hash);
    $stmt->execute();
    $result = $stmt->get_result();
    if ($obj = $result->fetch_object()) {
      $result->close();
      $stmt->close();
      return $obj;
    }
  } else {
    echo "Error: " . $mysqli->error;
  }
  return NULL;
}

function countOffersForProperty($mysqli, $propertyId) {
  $query = "(SELECT count(*) as 'total' FROM (
    (SELECT property_id FROM property_web_offer WHERE property_id = ?) 
      UNION
    (SELECT id FROM web_offer WHERE retail_only = 1 and retailer_id in(SELECT retailer_id from property_retailer WHERE property_id = ?))
    ) as dummyq)";
  if ($stmt = $mysqli->prepare($query)) {
    $stmt->bind_param("ss", $propertyId, $propertyId);
    $stmt->execute();
    $result = $stmt->get_result();
    if ($obj = $result->fetch_object()) {
      $result->close();
      $stmt->close();
      return $obj;
    }
  } else {
    echo "Error: " . $mysqli->error;
  }
  return NULL;
}

function getCategories($mysqli) {
  $list = array();
  $query = "SELECT * FROM category WHERE BRAND_id = 1 ORDER BY name";
  if ($stmt = $mysqli->prepare($query)) {
    $stmt->bind_param("s", $countryCode);
    $stmt->execute();
    $result = $stmt->get_result();
    while ($obj = $result->fetch_object()) {
      $list[] = $obj;
    }
    $result->close();
    $stmt->close();
  } else {
    echo "Error: " . $mysqli->error;
  }

  return $list;
}


//var_dump(countOffersForProperty($mysqli, "20"));
//var_dump(listOffersByPropertyAndLocale($mysqli, "000u", 'en'));
//var_dump(getCountryByProperty($mysqli, "000W"));
//$property = getProperty($mysqli,45);
//$offers = listOffersByPropertyAndLocale($mysqli, "45", 'en');
////var_dump($offers);
//foreach($offers as $offer){
//	$clean = preg_replace("/#PROX#ADDRESS#PROX#/",$property->address,$offer->clean_offer_text);
//	echo $clean."/".$offer->locale.$property->web_hash;
//}
//
//$countries = listCountries($mysqli);
//var_dump($countries);
//
//$props = listPropertiesByCountry($mysqli, "US");
//var_dump($props);
?>
