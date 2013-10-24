<?php
	include 'header.php';
	if (!$_SESSION['login']) {
		header("Location: login.php");
	}

	if(isset($_POST['wifi_channel'])){
		$pwd = shell_exec('pwd');
		$cmd = "bash " . trim($pwd) . "/setChannel.sh ".$_POST['wifi_channel'];		
		$output = shell_exec($cmd);
		$_SESSION['wifi_channel']=$_POST['wifi_channel'];
	} else {
		$pwd = shell_exec('pwd');
		$cmd = "bash " . trim($pwd) . "/setChannel.sh ";
		$output = shell_exec($cmd);
		$_SESSION['wifi_channel']=$_POST['wifi_channel'];
	}
	

	
?>
<div data-role="page" id="wifi_page" data-theme="a">

	<div data-role="header" data-position="inline">
		<a href="index.php" data-role="button" data-icon="arrow-l" >Back</a>
		<h1>Wifi Configuration</h1>
	</div>

	<div data-role="content" data-theme="a">
		<h2>Connectivity Configuration</h2>

		<div id="backhaul"  class="ui-body ui-body-a ui-corner-all">
			
			<form id="refresh_form" action="wifi.php" method="POST">                        
				<input type="hidden" id="refresh_list" name="refresh_list" value="true" data-role="none"/>
				<input type="submit" id="refresh" data-icon="refresh" value="Scan for Wifi Networks" data-position="inline" data-inline="true" />
			</form>
			<form action="connect.php" method="POST">
				<div data-role="fieldcontain">
					<select id="wifi_essid_select" name="wifi_essid" data-standalone-popup="true">
					<option data-placeholder='true'>Select A Wifi Network (SSID)...</option>
						<?php
							$pwd = shell_exec('pwd');
							if ($_POST['refresh_list'] == true) {
								$cmd = "bash " . trim($pwd) . "/scan.sh";
							}else{
								$cmd = "bash " . trim($pwd) . "/list.sh";
							}
							$output = shell_exec($cmd);
							$essids = explode("\n", $output);
							$_POST['refresh_list'] == false;				
							$sid = 0;
						?>
						<?php foreach ($essids as $essid) : ?>  
						<?php
							$sid++;
							if ($essid == "") {
								continue;
							}
						?>
					<?php if ($_SESSION['wifi_essid'] == $sid): ?>
					<option value="<?php echo $sid; ?>" selected="selected"><?php echo $essid; ?></option>
					<?php else: ?>
					<option value="<?php echo $sid; ?>"><?php echo $essid; ?></option>
					<?php endif; ?>                            
					<? endforeach; ?>
					</select>
				</div>
				<div data-role="fieldcontain">
				<label for="key_input">Network Passphrase:</label>
				<input type="text" id="passphrase_input" name="passphrase" value="<?php echo $_SESSION['passphrase']; ?>"/>
				</div>
				<input type="submit" value="Connect" data-icon="check" data-inline="true"  class="ui-btn-right"/><br/>
			</form>
			</div>

			<h2>Wifi Channel Override</h2>
			<form action="wifi.php" method="POST" class="ui-body ui-body-a ui-corner-all">
				<?php if(isset($_POST['wifi_channel'])){echo "<span style=\"color:green;\">Set Wifi Channel To: ".$_SESSION['wifi_channel']."</span>";}?>
				<div data-role='fieldcontain' data-inline="true"> 		
				<select id="wifi_channel_input" name="wifi_channel" data-standalone-popup="true">
					<option data-placeholder='true'>Select a Wifi Channel...</option>
					<?php for ($i = 1; $i <= 11; $i++) : ?>
						<?php if ($_SESSION['wifi_channel'] == $i): ?>
							<option value="<?php echo $i ?>" selected="selected"><?php echo $i ?></option>
						<?php else: ?>
							<option value="<?php echo $i ?>"><?php echo $i ?></option>
						<?php endif; ?>
					<? endfor; ?>
				</select>
				<input type="submit" value="Save" data-inline="true" data-icon="check"/><br/>
				</div>
			</form>
			
		</div><!-- /content -->

	<div data-role="footer" data-theme="a">
		<h4>Proximus Mobility, LLC</h4>
	</div><!-- /footer -->
</div><!-- /page -->
<?php
	include 'footer.php';
?>
