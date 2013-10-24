<?php
	/*
	 * http://localhost/rest.php
	 * Setup DATA
	 */
	$url = 'http://localhost:8080/ProximusTomorrow-war/api/gateway';

	$method = 'POST';

	$data = array(
		'device_macaddress' => 'aa:bb:cc:dd:ee:ff',
		'campaign_id'=> '1',
		'user_macaddress' => 'aa:aa:aa:bb:bb:bb'
	);

	$fields_string = http_build_query($data);

	$headers = array(
			'Content-Type: application/x-www-form-urlencoded',
	);
		
	$ch = curl_init();
	curl_setopt($ch, CURLOPT_URL, $url);
	curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, TRUE);
	curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, FALSE);
	curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, FALSE);
	curl_setopt($ch, CURLOPT_HEADER, TRUE);
	
	if($method=='POST'){	
			curl_setopt($ch, CURLOPT_NOBODY, TRUE);
			curl_setopt($ch, CURLOPT_FOLLOWLOCATION, TRUE);
			curl_setopt($ch, CURLOPT_POST, TRUE);
			curl_setopt($ch, CURLOPT_POSTFIELDS, $fields_string);
	}

	$response = curl_exec($ch);
	$code = curl_getinfo($ch, CURLINFO_HTTP_CODE);

	$location = curl_getinfo($ch, CURLINFO_EFFECTIVE_URL);
	curl_close ( $ch );

	if(!empty($location)){
		header("Location: ${location}");
	}
	
?>
