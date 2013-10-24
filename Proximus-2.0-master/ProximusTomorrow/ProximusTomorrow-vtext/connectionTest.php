<?php
$mysqli = new mysqli("172.29.242.8", "valutext", "valutextreversehorse", "valutext");
if ($mysqli->connect_errno) {
	echo $mysqli->error;
}
else{
	echo "Connected!";
}
?>
