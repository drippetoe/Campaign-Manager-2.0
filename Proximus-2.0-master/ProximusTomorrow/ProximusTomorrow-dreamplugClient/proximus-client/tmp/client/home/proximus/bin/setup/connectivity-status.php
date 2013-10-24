<?php
	include 'header.php';
	if (!$_SESSION['login']) {
		header("Location: login.php");
	}
?>

<div data-role="page" id="connectivity_page" data-theme="a">

	<div data-role="header" data-position="inline">
		<a href="index.php" data-role="button" data-icon="arrow-l" >Back</a>
		<h1>Connectivity Status</h1>
	</div>

	<div data-role="content" data-theme="a">
		<h2>Connectivity Configuration:</h2>

		<div id="backhaul"  class="ui-body ui-body-a ui-corner-all">   
                       
			<?php
				$pwd = shell_exec('pwd');
				$cmd = "bash " . trim($pwd) . "/connectivityStatus.sh";
				$output = shell_exec($cmd);
			?>
			<pre><?php echo $output; ?></pre>

		</div>

	</div><!-- /content -->

	<div data-role="footer" data-theme="a">
		<h4>Proximus Mobility, LLC</h4>
	</div><!-- /footer -->

</div><!-- /page -->

<?php
	include 'footer.php';
?>
