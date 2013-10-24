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
//echo "Change on either company or id writing to file<br />";
$currTime = time();
$filename = 'facebook_'.$currTime.".txt";
//echo $filename."<br />";
//$fh = fopen($filename,'w+');
//fwrite($fh, "facebookId=".$_SESSION['facebookId']."\ncompanyName=".$_SESSION['companyName']."\n");
//fclose($fh);
}
}
?>

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://www.facebook.com/2008/fbml">

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<meta name="viewport" content="width=device-width, user-scalable=no" />
<title>Like Us on Facebook</title>
<link type="text/css" rel="stylesheet" href="prox.css" />
<script type="text/javascript" src="jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="jquery.validate.min.js"></script>
<script type="text/javascript" src="jquery.maskedinput-1.0.js"></script>
<script type="text/javascript" src="validation.js"></script>
</head>
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

// The rest of this code assumes you are not using a library.
// It can be made less wordy if you use one.
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
document.getElementById('name').innerHTML = '<h2>Thanks!</h2><div class="access"><a href="'+targetUrl+'">Access the Internet</a></div>';
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
document.getElementById('name').innerHTML = "<h2>Like us on Facebook and get FREE Internet</h2>";
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
post_to_url('facebook.php?companyName=' + companyName, id, companyName);

});
$(".name").hide();
doesItLikeCompany();
$(".name").show();
//reloadPage();



}
function logout(){
$(".fb-like-box").hide();
document.getElementById('login').style.display = "none";
document.getElementById('name').innerHTML = "<h2>Like us on Facebook and get FREE Internet</h2>";
}


function doesItLikeCompany(){
FB.api('/me', function(response) {
var companyName = get("companyName");
var pageIdQuery = FB.Data.query('SELECT page_id FROM page WHERE username=\"{0}\"', companyName);
pageIdQuery.wait(function(rows) {
if(rows.length === 0) {
alert("Could not find the company_id for: " + companyName);
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
document.getElementById('name').innerHTML = "<h2>Thanks!</h2><div class=\"access\"><a href=\"http://www.facebook.com/"+companyName+"\">Access the internet</a></div>";

} else {
document.getElementById('name').innerHTML = "<h2>Like us on Facebook and get FREE Internet</h2>";
$(".fb-like-box").show();

}
});
break;

}
});

});
}


</script>


<div id="container">
<div id="header">
<a href="index.html">
<img src="Atlanta_Skyline_bw_7586001195973076493.png" id="logo"/>
</a>
</div>
<div id="navigation">
<a class="button navButton alignRight" href="index.html" title="View Offers">BACK</a>
</div>
<br>
<p><fb:login-button autologoutlink="true" perms="email,publish_stream,user_likes"></fb:login-button></p>
<div id="register-form">

<div id="name"></div>
<div class="fb-like-box" data-href="http://www.facebook.com/proximusmobility" data-width="292" data-show-faces="false" data-stream="false" data-header="true"></div>
<div id="login" style ="display:none"></div>

</div>
<div id="container-footer">
<p class="legal">
2012 (c) Prox
</p>
</div>
</div>

</html>