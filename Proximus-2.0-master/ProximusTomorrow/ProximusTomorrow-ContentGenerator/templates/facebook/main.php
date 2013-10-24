<?php
session_start();
	if(isset($_REQUEST['facebookId']) && isset($_REQUEST['companyName']) ) {
		$companyChange = false;
		$idChange = false;
		if( !isset($_SESSION['companyName']) || $_SESSION['companyName'] != $_REQUEST['companyName']) {
			$companyChange = true;
			$_SESSION['companyName'] = $_REQUEST['companyName'];
		}
		if(!isset($_SESSION['facebookId']) || $_SESSION['facebookId'] != $_REQUEST['facebookId']) {
		$idChange = true;
		$_SESSION['facebookId'] = $_REQUEST['facebookId'];
		}
		if($companyChange || $idChange) {
			$currTime = time();
			$filename = 'facebook_'.$currTime.".txt";
		}
	}
?>

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://www.facebook.com/2008/fbml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<meta name="viewport" content="width=device-width, user-scalable=no" />
<title>Like Us on Facebook</title>
</head>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
<link type="text/css" rel="stylesheet" href="facebook.css" />

<script>
function get(name){
	if(name=(new RegExp('[?&]'+encodeURIComponent(name)+'=([^&]*)')).exec(location.search))
	return decodeURIComponent(name[1]);
}

function reloadPage(){
	window.location.reload()
}

function post_to_url(path, facebookId, companyName, method) {
	method = method || "post"; // Set method to post by default, if not specified.
	var form = document.createElement("form");
	form.setAttribute("method", method);
	form.setAttribute("action", path);
	var hiddenField = document.createElement("input");
	hiddenField.setAttribute("type", "hidden");
	hiddenField.setAttribute("name", 'facebookId');
	hiddenField.setAttribute("value", facebookId);
	form.appendChild(hiddenField);
	
	var hiddenField2 = document.createElement("input");
	hiddenField2.setAttribute("type", "hidden");
	hiddenField2.setAttribute("name", 'companyName');
	hiddenField2.setAttribute("value", companyName);
	form.appendChild(hiddenField2);
	
	document.body.appendChild(form);
	form.submit();
}


$(document).ready(function(){
	var companyName = get("companyName");
	$(".fb-like-box").attr("data-href", "http://www.facebook.com/" + companyName);
	$(".fb-like-box").hide();
	$(".name").hide();
});
</script>


<div id="fb-root"></div>
<script>
(function(d, s, id) {
	var js, fjs = d.getElementsByTagName(s)[0];
	if (d.getElementById(id)) return;
	js = d.createElement(s); js.id = id;
	js.src = "//connect.facebook.net/en_US/all.js#xfbml=1&appId=437508149621375";
	fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));
</script>

<script type="text/javascript">
	var text;
	/*#PROX#TextEditor_editor#PROX#*/
	if(text == null || text.length == 0) {
		text = "<h2>Like us on Facebook and get FREE Internet</h2>";
	}
	window.fbAsyncInit = function() {
	FB.init({appId: '437508149621375', status: true, cookie: true, xfbml: true});

	/* All the events registered */
	FB.Event.subscribe('auth.login', function(response) {
	// do something with response
	login();
	});

	
	FB.Event.subscribe('auth.logout', function(response) {
	// do something wih response
	logout();
	});

	FB.Event.subscribe("edge.create", function(targetUrl) {
	
	$(".fb-like-box").hide();
	$(".name").hide();
	document.getElementById('name').innerHTML = '<h2>Thanks!</h2><div class="faceButton"><a href="'+targetUrl+'">Access the Internet</a></div>';
	$(".name").show();
	});

	FB.Event.subscribe("edge.remove", function(targetUrl) {
	//check if someone has unliked company
	});
	
	FB.getLoginStatus(function(response) {
	$(".name").hide();
	if (response.status === 'connected') {
	FB.api('/me', function(response) {
	document.getElementById('name').innerHTML = "";
	
	});
	doesItLikeCompany();
	} else {
	document.getElementById('name').innerHTML = text;
	}

	$(".name").show();
	});
	};
	(function() {
	var e = document.createElement('script');
	e.type = 'text/javascript';
	e.src = document.location.protocol +
	'//connect.facebook.net/en_US/all.js';
	e.async = true;
	document.getElementById('fb-root').appendChild(e);
	}());
	
	
function login(){
	FB.api('/me', function(response) {
	document.getElementById('name').innerHTML = "";
	var id = response.id;
	companyName = get("companyName");
	post_to_url('main.php?companyName=' + companyName, id, companyName);
	
});
	$(".name").hide();
	doesItLikeCompany();
	$(".name").show();
	//reloadPage();
}

function logout(){
	$(".fb-like-box").hide();
	document.getElementById('login').style.display = "none";
	document.getElementById('name').innerHTML = text;
}


function doesItLikeCompany(){
	FB.api('/me', function(response) {
	var companyName = get("companyName");
	var pageIdQuery = FB.Data.query('SELECT page_id FROM page WHERE username=\"{0}\"', companyName);
		pageIdQuery.wait(function(rows) {
			if(rows.length === 0) {
				alert("Seems like " + companyName + " is not a page on Facebook.");
			}
		
			for(var i = 0; i < rows.length; i++) {
				var companyId = rows[i].page_id;
				var query = FB.Data.query('SELECT uid, page_id FROM page_fan WHERE uid={0}', response.id);
				query.wait(function(rows) {
				var isFound = false;
				for(var i = 0; i < rows.length; i++) {
					if(rows[i].page_id == companyId) {
						isFound = true;
						break;
					}
				}
				if(isFound) {
					document.getElementById('name').innerHTML = "<h2>Thanks!</h2><div class=\"faceButton\"><a href=\"http://www.facebook.com/"+companyName+"\">Access the internet</a></div>";
				} else {
					document.getElementById('name').innerHTML = text;
					$(".fb-like-box").show();
				}
			});
			break;
			}
		});
	});
}
</script>
<center>
<div class="container">
	<div class="content">
		<div class="contact">
		#PROX#ImageUploader_logo#PROX#
		<br />
		<fb:login-button autologoutlink="true" perms="email,publish_stream,user_likes"></fb:login-button>
			<div id="name"></div>
			<div class="fb-like-box" data-href="http://www.facebook.com/proximusmobility" data-width="292" data-show-faces="false" data-stream="false" data-header="true"></div>
			<div id="login" style ="display:none"></div>
		</div>
		<div class="footer">
			Powered By <a href="http://www.proximusmobility.com">Proximus Mobility</a>
		</div>
	</div>
</div>
</center>
</html>