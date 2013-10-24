<?php
	include 'header.php';
?>


<!-- Start of second page: #two -->
<div data-role="page" id="menu_page" data-theme="a" >
	<?php if (!$_SESSION['login']) : ?>
	<div data-role="header">
	<h1>Login</h1>
	</div><!-- /header -->

	<div data-role="content" data-theme="a" >
		<form action="index.php" method="POST">
			<div data-role="fieldcontain">
				<label for="user_input">Username:</label>
				<input type="text" id="user_input" name="user" value=""/><br/>
				<label for="password_input">Password:</label>
				<input type="password" id="password_input" name="pass" value=""/><br/>
			</div>
			<input type="submit" value="Login" />
		</form>
	</div><!-- /content -->
	
	<?php else: ?>

	<div data-role="header">
		<h1>Setup for <?php echo $_SESSION['device_mac']; ?></h1>
	</div><!-- /header -->

	<div data-role="content" data-theme="a">
		<p><a href="connectivity-status.php" data-role="button" data-icon="arrow-r" data-iconpos="right">Connectivity Status</a></p>
		<p><a href="wifi.php" data-role="button" data-icon="arrow-r" data-iconpos="right">Wifi Configuration</a></p>	
		<?php /* <p><a href="#dongle_page" data-role="button" data-icon="arrow-r" data-iconpos="right">3G/4G Configuration</a></p>  */ ?>
		<form action="index.php" method="POST">
			<input type="hidden" id="signout_input" name="signout" value="true" />
			<input type="submit" value="Logout" data-icon="back" data-iconpos="top"/>
		</form>
		<br/>
		</div><!-- /content -->
	<div data-role="footer" data-theme="a">
		<h4>Proximus Mobility, LLC</h4>
		</div><!-- /footer -->
	</div><!-- /page -->
	<?php endif; ?>
	<!-- Start of second page: #two -->
</div>
	
<?php
	include 'footer.php';
?>
