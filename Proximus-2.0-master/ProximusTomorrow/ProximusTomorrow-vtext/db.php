<?php
$mysqli = new mysqli("localhost", "valutext", "valutextreversehorse", "valutext");
if ($mysqli->connect_errno) {
  //header("Location: 404.php?reason=dbdown");
  echo $mysql->error;
}
/*
 * Property
 */

function getProperty($mysqli, $id) {
  $query = "SELECT * FROM property WHERE id = ? LIMIT 1";
  if ($stmt = $mysqli->prepare($query)) {
    $stmt->bind_param("s", $id);
    $stmt->execute();
    $result = $stmt->get_result();
    if ($obj = $result->fetch_object()) {
      $result->close();
      $stmt->close();

      return $obj;
    }
    $result->close();
    $stmt->close();
  }
  return NULL;
}

function listPropertiesByCountry($mysqli, $countryId) {
  $list = array();
  $query = "SELECT * FROM property WHERE COUNTRY_id = ? ORDER BY name";
  if ($stmt = $mysqli->prepare($query)) {
    $stmt->bind_param("s", $countryId);
    $stmt->execute();
    $result = $stmt->get_result();
    while ($obj = $result->fetch_object()) {
      $list[] = $obj;
    }
    $result->close();
    $stmt->close();
  }
  return $list;
}

/**
 * Country
 */
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
  }
  return $list;
}

/**
 * Offer
 */

function getOffer($mysqli, $id){
  $query = "SELECT * FROM offer WHERE id = ? LIMIT 1";
  if ($stmt = $mysqli->prepare($query)) {
    $stmt->bind_param("s", $id);
    $stmt->execute();
    $result = $stmt->get_result();
    if ($obj = $result->fetch_object()) {
      $result->close();
      $stmt->close();
      return $obj;
    }
  }
  return NULL;
}
function listOffersByPropertyAndLocale($mysqli, $propertyId, $locale) {
  $list = array();
  $query = "SELECT * FROM offer WHERE PROPERTY_id = ? AND locale = ? ORDER BY retailer";
  if ($stmt = $mysqli->prepare($query)) {
    $stmt->bind_param("ss", $propertyId, $locale);
    $stmt->execute();
    $result = $stmt->get_result();
    while ($obj = $result->fetch_object()) {
      $list[] = $obj;
    }
    $result->close();
    $stmt->close();
  }
  return $list;
}

?>
