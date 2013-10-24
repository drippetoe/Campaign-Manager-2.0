<?php
include 'header.php';
?>
<div id="content">
<!--  <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis arcu arcu, feugiat eget semper id, porttitor ac dolor. Nulla vitae nunc id neque ullamcorper porttitor. Morbi ut dui velit, sed feugiat nisi. Mauris iaculis sodales velit et sollicitudin. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Donec consectetur euismod pellentesque. Aliquam in ante eu mauris sollicitudin ultricies vitae ac arcu. Suspendisse cursus, erat id suscipit cursus, urna sem faucibus urna, eu eleifend nulla arcu a leo. Aliquam at est nibh. Donec placerat porttitor nulla vel varius. Donec arcu urna, auctor at imperdiet in, hendrerit sit amet sapien. Vestibulum tristique lacus sem, in dignissim dui. Sed nisi justo, ornare quis tempus sagittis, pulvinar a nisi. Quisque consectetur, metus id vehicula lobortis, metus urna aliquet elit, eu fringilla tellus augue ac dui. Proin sed turpis at ante consectetur condimentum sollicitudin in turpis.
  </p><br/>-->
  <input id="filter" type="text" value=""
         <br/>
<!--  <p>
    Sed fringilla imperdiet tortor, et adipiscing metus scelerisque nec. Etiam tempor sodales nisi, nec consectetur velit condimentum eu. Vivamus et mi dolor. Cras et lectus in nisi mollis placerat. Phasellus risus ipsum, porttitor vel porta quis, pulvinar at metus. Vivamus sodales elit id nisi consequat posuere. Sed metus nibh, lacinia ac ullamcorper sed, aliquam in ligula. Nunc pellentesque turpis sit amet turpis volutpat tempor. Morbi ultricies mattis sodales.
  </p>-->
  <?php
  $mysqli = new mysqli("localhost", "valutext", "valutextreversehorse", "valutext");
  //$query = "SELECT * FROM property AS p WHERE id IN (SELECT DISTINCT(PROPERTY_id) FROM offer WHERE locale=?)";
  //$query = "SELECT DISTINCT(PROPERTY_id) FROM offer WHERE locale=?";
  $query = "SELECT * FROM country WHERE id IN(SELECT DISTINCT(COUNTRY_id) FROM property) ORDER BY name";
  if ($mysqli->connect_errno) {
    echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") ";
  } else if ($stmt = $mysqli->prepare($query)) {
    $lang = "en";
    //$stmt->bind_param("s", $lang);
    $stmt->execute();
    $result = $stmt->get_result();
    while ($pproperty = $result->fetch_object()) {
      printf("<div class=\"property\"><a href=\"/%s\"><div id=\"\%s\">%s<span class=\"arrow\">&#9658;</span></div></a></div>", $pproperty->id, $pproperty->name, $pproperty->name, $pproperty->name);
    }
    $result->close();
    $stmt->close();
  } else {
    echo "Error " . $mysqli->error;
  }
  $mysqli->close();
  ?>

</div>
<?php
include 'footer.php';
?>