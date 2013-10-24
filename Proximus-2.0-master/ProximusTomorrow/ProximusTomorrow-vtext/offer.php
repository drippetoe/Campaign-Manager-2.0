<?php
$offer = NULL;
$mysqli = new mysqli("localhost", "valutext", "valutextreversehorse", "valutext");
if ($mysqli->connect_errno) {
  echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") ";
} else if ($stmt = $mysqli->prepare("SELECT * FROM offer WHERE id = ? AND locale = ? ORDER BY retailer ASC")) {
  $id = mysql_escape_string($offer_id);
  $stmt->bind_param("ss", $id, $lang);
  $stmt->execute();
  $result = $stmt->get_result();
  $offer = $result->fetch_object();
  echo $offer->retailer;
  $result->close();
  $stmt->close();
}
$mysqli->close();
if ($offer == NULL) {
  if ($property_id != NULL) {
    header("Location: /" . $lang . "/" . $property_id);
  } else {
    header("Location: /");
  }
}
include 'header.php';
?>
<div class="content">
  <div id="header" >
    <h1 class="title"><?php echo $offer->retailer; ?></h1>
  </div>
  <div id="ad1space" >
    <p>Ad Space</p>
  </div>
  <div id="main">
    <div id="offer">
      <?php echo $offer->text; ?>
    </div>
    <div id="social"></div>
  </div>
  <div id="footer"class="">Copyright &copy; 2012 ValuText, LLC</div>
</div>

<?php
include 'footer.php';
?>
