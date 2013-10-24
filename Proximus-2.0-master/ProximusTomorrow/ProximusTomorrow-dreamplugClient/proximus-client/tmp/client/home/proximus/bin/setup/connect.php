<?php
	include 'header.php';
	if (!$_SESSION['login']) {
		header("Location: login.php");
	}
	if(!isset($_POST['wifi_essid'])){
		shell_exec("rm -f /home/proximus/bin/setup/networkConnect.sh");
		header("Location: wifi.php");
	}
	$_SESSION['wifi_essid']=$_POST['wifi_essid'];
	$_SESSION['passphrase']=$_POST['passphrase'];
?>
<div data-role="page" id="connect_page" data-theme="a">

		<div data-role="header" data-position="inline">
			<a href="wifi.php" data-role="button" data-icon="arrow-l" >Back</a>
			<h1>Wifi Configuration</h1>
		</div>

		<div data-role="content" data-theme="a">
			<h2>Connect:</h2>

			<div id="backhaul"  class="ui-body ui-body-a ui-corner-all">
				<?php
					$pwd = shell_exec('pwd');
					$cmd = "bash " . trim($pwd) . "/save.sh wifi ".$_POST['wifi_essid']." ".$_POST['passphrase'];
					$output = shell_exec($cmd);                    
				?>
				<pre><?php echo $output; ?></pre>
			</div>

			<p><a href="index.php" data-role="button" data-icon="home">Home</a></p>
		</div><!-- /content -->

		<div data-role="footer" data-theme="a">
			<h4>Proximus Mobility, LLC</h4>
		</div><!-- /footer -->

</div><!-- /page -->
<?php
	include 'footer.php';
?>
