<?php

require_once('recaptchalib.php');
$privatekey = "6Lc-3tUSAAAAABGV6sshozm4SrOG4n1fmnbLzbnw";
$publickey = "6Lc-3tUSAAAAAM9kG08qdG0C-KJKQkGngeMCbWk1";
$resp = null;
$error = null;
if (isset($_POST["recaptcha_response_field"])) {
  $resp = recaptcha_check_answer($privatekey, $_SERVER["REMOTE_ADDR"], $_POST["recaptcha_challenge_field"], $_POST["recaptcha_response_field"]);

  if ($resp->is_valid) {
    header("Location: success.php");
  } else {
    # set the error code so that we can display it
    header("Location: /");
  }
}
echo recaptcha_get_html($publickey, $error);
?>