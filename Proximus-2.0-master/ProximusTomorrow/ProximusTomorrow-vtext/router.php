<?php
include_once 'manager.php';
include_once 'lang.php';
$lang = "en";
$list = "";
$q = NULL;
$COUNTRY_id = NULL;
$PROPERTY_id = NULL;
$OFFER_id = NULL;
$CSS = "US";
$title = "";
$page = NULL;

function isValidGet($var) {
  if (isset($_GET[$var]) && $_GET[$var] != NULL && $_GET[$var] != "") {
    return TRUE;
  }
  return FALSE;
}

function setCountry($country) {
  global $COUNTRY_id;
  global $CSS;
  preg_match("/([A-Z]{2})/", $country, $result);
  if ($result) {
    $COUNTRY_id = $result[0];
    $CSS = $COUNTRY_id;
  }
}

function setPropertyOffer($property) {
  global $PROPERTY_id;
  global $OFFER_id;
  preg_match("/([0-9]{4})(/[0-9]+)?", $property, $result);
  if ($result) {
    $PROPERTY_id = $result[0];
    $OFFER_id = $result[2];
  }
}

function setLanguageLocale($locale) {
  global $languages;
  global $locale;
  preg_match("/([a-z]{2})/", $locale, $result);
  if ($result) {
    if (in_array($result[0], $languages)) {
      $locale = $result[0];
      include './i18n/lang.' . $result[0] . '.php';
      extract($MESSAGES, EXTR_PREFIX_ALL | EXTR_OVERWRITE, "message");

      //echo $locale;
    }
  }
}

$page = NULL;
if (isValidGet("q")) {
  header("Location: /");
}
if (isValidGet("locale")) {
  $locale = $_GET["locale"];
}
if (isValidGet("COUNTRY_id")) {
  setCountry($_GET["COUNTRY_id"]);
}
if (isValidGet("PROPERTY_id")) {
  $PROPERTY_id = $_GET["PROPERTY_id"];
}
if (isValidGet("OFFER_id")) {
  $OFFER_id = $_GET["OFFER_id"];
}
if (isValidGet("page")) {
  $page = $_GET["page"];
}
include './i18n/lang.' . $locale . '.php';
extract($MESSAGES, EXTR_PREFIX_ALL | EXTR_OVERWRITE, "message");


/*
 * //$mysqli = new mysqli("localhost", "valutext", "valutextreversehorse", "valutext");
  //if ($mysqli->connect_errno) {
  //  echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") ";
  //}
 */
/* $mysqli = new mysqli("localhost", "valutext", "valutextreversehorse", "valutext");
  if ($mysqli->connect_errno) {
  header("Location: 404.php");
  } */
/*
 * /0001 usecase
 */
//var_dump($_GET);
//print_r($_GET);
if (isset($PROPERTY_id) && $COUNTRY_id == NULL) {
  $location = "/";
  //SELECT * FROM `offer` where property_id='0001' and locale ='en';
  $country = getCountryByProperty($mysqli, $PROPERTY_id);
  if ($country != NULL) {
    $location = "/" . $country->code . "/" . $locale . "/" . $PROPERTY_id;
    header("Location: " . $location);
  }
}
?>

<?php

/*
 * Redirect
 */


//if ($offer_id != NULL) {
//  include_once 'offer.php';
//} else if ($property != NULL) {
//  include_once 'property.php';
//} else {
//  //include_once 'registration.php';
//  include_once 'retina.php';
//}
?>
<?php

/*
 * Redirect
 */
$list = array();
$query = NULL;
$param = "";
$share = "<div id='share' class='listitem'>
<span class='st_facebook_large' displayText='Facebook'></span>
<span class='st_twitter_large' displayText='Tweet'></span>
<span class='st_googleplus_large' displayText='Google +'></span>
<span class='st_email_large' displayText='Email'></span></div>";

if (isset($OFFER_id)) {
  $obj = getOffer($mysqli, $OFFER_id);
  $property = getProperty($mysqli, $PROPERTY_id);
  $retailer = getRetailerByOffer($mysqli, $OFFER_id);
  $title .= $property->name . " - " . $retailer->name;

  $list[] = "<div id='offer' class=\"backitem\">
                  <a href=\"/" . $COUNTRY_id . "/" . $locale . "/" . $PROPERTY_id . "\"><div><span class='larrow'>&#9668;</span>" . $message_back . "</a></div>"
      . "</div><h2>" . $retailer->name . "</h2><p>" . $property->name . "</p>";


  $url = "http://" . $_SERVER['SERVER_NAME'] . "/" . $locale . $PROPERTY_id;
  if ($obj != NULL) {
    $offer_text = "" . $obj->clean_offer_text;
    $address = "";
    $propertyName = "";
    if ($property != NULL) {
      $address = $property->address;
      $propertyName = $property->name;
    }
    $offer_text = preg_replace("/#PROX#ADDRESS#PROX#/", $address, $offer_text);
    $offer_text = preg_replace("/#PROX#PROPERTY#PROX#/", $propertyName, $offer_text);
    $offer_text = preg_replace("/MORE OFFERS AT:/", "", $offer_text);
    $offer_text = preg_replace("/Mas ofertas/", "", $offer_text);
    $offer_text = preg_replace("/ValuText:/", "", $offer_text);
    //$offer_text = preg_replace("/http:\/\/vtext.me(\/)?/", $url, $offer_text);
    $offer_text = preg_replace("/http:\/\/vtext.me(\/)?/", "", $offer_text);
    //$offer_text = preg_replace("/#PROX#ADDRESS#PROX#/",$property->address,$obj->clean_offer_text);
    $list[] = "<div id='offer' class=\"listitem\"><div>" . $offer_text . "
            </div></div>";
    $list[] = $share;
    $title .= " - " . $offer_text;
  } else {
    header("Location: /");
  }
} elseif (isset($PROPERTY_id)) {
  $property = getProperty($mysqli, $PROPERTY_id);
  if ($property != NULL) {
    $title .= $property->name;
    $oList = listOffersByPropertyAndLocale($mysqli, $PROPERTY_id, $locale);
    $list[] = "<div id='offer' class=\"backitem\">
                  <a href=\"/" . $COUNTRY_id . "/" . $locale . "\"><div><span class='larrow'>&#9668;</span>" . $message_back . "</a></div>"
        . "</div><h2>" . $property->name . "</h2><p>" . $property->address . "</p>";
    foreach ($oList as $obj) {
      $retailer = getRetailerByOffer($mysqli, $obj->id);

      $list[] = "
            <div class=\"listitem\">
              <a href=\"/" . $COUNTRY_id . "/" . $locale . "/" . $PROPERTY_id . "/" . $obj->id . "\">
                <div id=\"offer-" . $obj->id . "\">" . $retailer->name . "
                  <span class=\"arrow\">&#9658;</span>
                </div>
              </a>
            </div>";
    }
  } else {
    header("Location: /404?reason=propertynotfound");
  }
} elseif (isset($COUNTRY_id)) {
  $pList = listPropertiesByCountry($mysqli, $COUNTRY_id);
  $list[] = "<div id='offer' class=\"backitem\">
                  <a href=\"/\"><div><span class='larrow'>&#9668;</span>" . $message_back . "</a></div>"
      . "</div><br/><br/>";
  foreach ($pList as $obj) {
//    $t = countOffersForProperty($mysqli, "" . $obj->id);
//
//    if ($t == NULL) {
//      $total = 0;
//    } else {
//      $total = $t->total;
//    }
    $list [] = "<div class=\"listitem\">
              <a href=\"/" . $COUNTRY_id . "/" . $locale . "/" . $obj->web_hash . "\">
                <div id=\"property-" . $obj->id . "\">" . $obj->name . "
                  <span class=\"arrow\">&#9658;</span>
                </div>
              </a>
            </div>";
  }
} else {
  $list[] = "<h2>".$MESSAGES['welcome']."</h2>";
  $list[] = "<div class=\"listitem\">
              <a href=\"\" id=\"enableMyLocation\">
                <div>".$message_closestLocation."
                  <span class=\"arrow\">&#9658;</span>
                </div>
              </a>
            </div><br/>";
  $cList = listCountries($mysqli);
  foreach ($cList as $obj) {
    $list[] = "
            <div class=\"listitem\">
              <a href=\"/" . $obj->code . "/" . $locale . "\">
                <div id=\"" . $obj->code . "\">" . $obj->name . "
                  <span class=\"arrow\">&#9658;</span>
                </div>
              </a>
            </div>";
  }
  //default to US
  $COUNTRY_id = "US";
  if ($page != NULL) {
    $location = "/US" . $COUNTRY_id . "/" . $locale . "/" . $page;
    header("Location: " . $location);
  }
}
//var_dump($_GET);
?>
