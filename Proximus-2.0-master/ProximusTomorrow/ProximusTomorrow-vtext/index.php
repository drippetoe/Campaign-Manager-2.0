<?php
//include_once 'processor.php';
include_once 'router.php';
//var_dump($_GET);
if ($page != NULL) {
  $page = $page . '.php';
  if (file_exists($page)) {
    include $page;
  } else {
    include '404.php';
  }
} else {
  include 'main.php';
}
?>

