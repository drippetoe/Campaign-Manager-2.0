<?php

require_once ('/home/proximus/bin/php/barcode/BCGFontFile.php');
require_once ('/home/proximus/bin/php/barcode/BCGColor.php');
require_once ('/home/proximus/bin/php/barcode/BCGDrawing.php');
require_once ('/home/proximus/bin/php/barcode/BCGupca.barcode.php');
require_once ('/home/proximus/bin/php/Util.php');
require_once ('/home/proximus/bin/php/Logger.php');

/**
 * Proximus Utility PHP Class
 * @author Eric Johansson
 */
class Proximus {

	/**
	 * Use the attribute validate="<type>" on any input that needs to be validated before submit.
	 * the different types are:
	 *  -   length
	 *  -   numeric
	 *  -   alpha
	 *  -   alphanumeric
	 *  -   name
	 *  -   age
	 *  -   email
	 *  -   phone
	 *  -   regexp
	 *      * With regexp use attributes pattern="<pattern>" and message="<message>"
	 *
	 * @example
	 <?php require_once('/home/proximus/bin/php/Proximus.php');
	 ?>
	 <!DOCTYPE html>
	 <html>
	 <head>
	 <?php Proximus::formValidation();
	 ?>
	 </head>
	 <body>
	 <form action="thanks.html" method="POST">
	 <p>Name:</p>
	 <input type="text" name="name" validate="length" /><br/>
	 <p>Age:</p>
	 <input type="text" name="age" validate="age"/><br/>
	 <p>Email:</p>
	 <input type="text" name="email" validate="email"/><br/>
	 <input type="submit" value="Register"/>
	 </form>
	 </body>
	 </html>
	 * @return string
	 */
	public static function formValidation() {

		$jquery = "/home/proximus/bin/js/jquery-1.7.2.min.js";
		$proximus = "/home/proximus/bin/js/proximus.js";
		$file_handle = null;
		if (file_exists($jquery)) {
			echo '<script type="text/javascript">';
			$file_handle = @fopen($jquery, 'r');
			while ($theData = fgets($file_handle)) {
				echo $theData;
			}
			echo '</script>';
			fclose($file_handle);
		}
		if (file_exists($proximus)) {
			echo '<script type="text/javascript">';
			$file_handle = @fopen($proximus, 'r');
			while ($theData = fgets($file_handle)) {
				echo $theData;
			}
			echo '</script>';
			fclose($file_handle);
		}
	}

	private static function validate($array, $keys) {
		if (is_array($array)) {
			foreach ($keys as $key => $val) {
				if (isset($array[$val]) && $array[$val] != "") {
					$valid[$val] = true;
				} else {
					$valid[$val] = false;
				}
			}
			return $valid;
		}
		return false;
	}

	public static function hotspot($default_webpage, $callback = NULL) {
		if (isset($_REQUEST['url'])) {
			$url = $_REQUEST['url'];
		} else if (isset($_REQUEST['URL'])) {
			$url = $_REQUEST['URL'];
		} else if (isset($_REQUEST['Url'])) {
			$url = $_REQUEST['Url'];
		} else {
			$url = $default_webpage;
		}
		$MAC_ADDR = Util::getCONNECTED_MAC();
		if ($MAC_ADDR == "in") {
			print "No internet for you!";
			exit ;
		}
		$REMOTE_ADDR = $_SERVER['REMOTE_ADDR'];
		$output = "";
		#echo "Connected IP:".$REMOTE_ADDR;
		$output = shell_exec('/usr/bin/python /home/proximus/bin/portal.py open add ' . $REMOTE_ADDR);
		shell_exec($output);

		header("Location: " . $url);
	}

	// render a PNG file with a timestamp embedded
	public static function timeStampImage($imgPath, $text, $timestamp = NULL, $fontsize, $xpos, $ypos, $red, $green, $blue, $timezone = NULL) {
		header('Content-Type: image/png');
		$im = imagecreatefrompng($imgPath);
		$color = imagecolorallocate($im, $red, $green, $blue);
		$angle = 0;
		if ($timezone == NULL) {
			date_default_timezone_set('UTC');
		} else {
			date_default_timezone_set($timezone);
		}
		if ($timestamp == NULL) {
			$timestamp = strftime("%m/%d/%Y");
		}
		$font = '/home/proximus/bin/php/barcode/font/Arial.ttf';
		$finaltext = str_replace('{timestamp}', $timestamp, $text);

		imagettftext($im, $fontsize, $angle, $xpos, $ypos, $color, $font, $finaltext);
		imagepng($im);
		imagedestroy($im);
	}

	public static function register($callback = NULL, $validate = NULL, $timezone = NULL) {
		if (!isset($_POST) || empty($_POST)) {
			return FALSE;
		}
		if ($timezone == NULL) {
			date_default_timezone_set('UTC');
		} else {
			date_default_timezone_set($timezone);
		}
		$result = true;
		if ($validate == NULL && isset($_POST['pm_validate'])) {
			$validate = explode(",", $_POST['pm_validate']);
		}
		$valid = NULL;
		if ($validate != NULL && is_array($validate)) {
			$valid = Proximus::validate($_POST, $validate);
			if ($valid != NULL && is_array($valid)) {
				if (!in_array(FALSE, $valid)) {
					$result = Logger::register(NULL, NULL, $timezone);
				} else {
					$result = FALSE;
				}
			} else {
				$result = FALSE;
			}
		} else {
			$result = Logger::register(NULL, NULL, $timezone);
		}
		/*
		 * If proivided with a callback function return values
		 */
		if ($callback != NULL && is_callable($callback)) {
			call_user_func($callback, $result, $_POST, $valid);
		}
		return $result;
	}

	public static function onetimeUniqueUser($file, $mac = NULL, $data = NULL, $timezone = NULL) {
		if ($mac == NULL) {
			$mac = Util::getCONNECTED_MAC();
		}
		if ($timezone == NULL) {
			date_default_timezone_set('UTC');
		} else {
			date_default_timezone_set($timezone);
		}
		$date = date("m/d/Y");
		if (is_readable($file) && is_writable($file)) {
			$file_handle = @fopen($file, 'r+');
			$found = false;
			while (!feof($file_handle) && !$found) {
				$line = fgets($file_handle);
				$entries = explode(",", $line);
				if ($entries[0] == $mac) {
					$found = true;
					$date = $entries[1];
					$data = $entries[2];
					fclose($file_handle);
					return $entries;
				}
			}
			if (!$found) {
				fwrite($file_handle, $mac . "," . $date . "," . $data . "\n");
			}
			fclose($file_handle);
		}
		$entries[0] = $mac;
		$entries[1] = $date;
		$entries[2] = $data;
		return $entries;
	}

	public static function barcode() {
		$identifier = "" . rand(11111111111, 99999999999);
		$identifier = trim($identifier);
		Logger::barcode('UPC-A', $identifier, NULL);
		$font = new BCGFontFile('/home/proximus/bin/php/barcode/font/Arial.ttf', 18);
		$color_black = new BCGColor(0, 0, 0);
		$color_white = new BCGColor(255, 255, 255);
		$drawException = null;
		try {
			$code = new BCGupca();
			$code -> setScale(2);
			// Resolution
			$code -> setThickness(30);
			// Thickness
			$code -> setForegroundColor($color_black);
			// Color of bars
			$code -> setBackgroundColor($color_white);
			// Color of spaces
			$code -> setFont($font);
			// Font (or 0)

			$code -> parse($identifier);
			// Text
		} catch (Exception $exception) {
			$drawException = $exception;
		}
		$drawing = new BCGDrawing('', $color_white);
		if ($drawException) {
			$drawing -> drawException($drawException);
		} else {
			$drawing -> setBarcode($code);
			$drawing -> draw();
		}
		header('Content-Type: image/png');
		$drawing -> finish(BCGDrawing::IMG_FORMAT_PNG);
	}

	public static function gatewayForm($buttonName = 'Get Offers') {
		$mac = Util::getCONNECTED_MAC(NULL);
		$device_mac = Util::getDEVICE_MAC();
		$campaign_id = Util::getCAMPAIGN();
		$server = Util::getSERVER_URL();

		$formData = '<form method="POST" action="http://' . $server . '/api/gateway">
<input type="hidden" name="device_macaddress" value="' . $device_mac . '">
<input type="hidden" name="user_macaddress" value="' . $mac . '">
<input type="hidden" name="campaign_id" value="' . $campaign_id . '">
<br/><input type="submit" value="' . $buttonName . '"></form>
';

		print $formData;
	}

	public static function uniqueBarcode($mac = NULL, $min = 11111111111, $max = 99999999999) {
		if ($mac == NULL) {
			$mac = Util::getCONNECTED_MAC(NULL);
		}
		if ($mac == NULL) {
			return Proximus::barcode();
		}
		$identifier = "" . mt_rand($min, $max);

		try {
			$res = Proximus::onetimeUniqueUser("./uniqueBarcode.txt", $mac, $identifier);
			if ($res != NULL || isset($res[2])) {
				$identifier = trim($res[2]);
			}
		} catch (Exception $e) {
			$identifier = trim($identifier);
		}

		Logger::barcode('UPC-A', $identifier, $mac);
		$font = new BCGFontFile('/home/proximus/bin/php/barcode/font/Arial.ttf', 18);
		$color_black = new BCGColor(0, 0, 0);
		$color_white = new BCGColor(255, 255, 255);
		$drawException = null;
		try {
			$code = new BCGupca();
			$code -> setScale(2);
			// Resolution
			$code -> setThickness(30);
			// Thickness
			$code -> setForegroundColor($color_black);
			// Color of bars
			$code -> setBackgroundColor($color_white);
			// Color of spaces
			$code -> setFont($font);
			// Font (or 0)

			$code -> parse($identifier);
			// Text
		} catch (Exception $exception) {
			$drawException = $exception;
		}
		$drawing = new BCGDrawing('', $color_white);
		if ($drawException) {
			$drawing -> drawException($drawException);
		} else {
			$drawing -> setBarcode($code);
			$drawing -> draw();
		}
		header('Content-Type: image/png');
		$drawing -> finish(BCGDrawing::IMG_FORMAT_PNG);
	}

}
?>