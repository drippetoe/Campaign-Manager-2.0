<?php
require_once('/home/proximus/bin/php/Util.php');
$MAC_ADDR = Util::getCONNECTED_MAC();

class ProximusUniqueUser {
	public function __construct()
	{
		$this->sessionId = $currentSessionId;
		$this->timesSeen = 1;
		date_default_timezone_set('UTC');
	}
	
	public static function withMac($macAddress)
	{
		session_start( $MAC_ADDR );
		$currentSessionId = session_id();

		$instance = new self();
    		$instance->macAddress = $macAddress;
    		$instance->sessionId = $currentSessionId;

		$instance->firstSeen = time();
		$instance->lastSeen = $instance->firstSeen;
		
    		return $instance;
	}
	
	public static function withData($macAddress, $oldSessionId, $firstSeen, $lastSeen, $timesSeen)
	{
		session_start( $MAC_ADDR );
		$currentSessionId = session_id();
		
		$instance = new self();
    		$instance->macAddress = $macAddress;
    		$instance->sessionId = $currentSessionId;
		$instance->timesSeen = $timesSeen;
		
		$instance->firstSeen = $firstSeen;
		$instance->lastSeen = $lastSeen;
		$instance->timesSeen = $timesSeen;
		
		# What is the criteria for a session?  Gotta be time.  We'll say 15 minutes
		$now = time();                                                             
		$FIFTEEN_MINS_SEC = 15 * 60;                                               
		if ( ($now - $instance->lastSeen) > $FIFTEEN_MINS_SEC )                    
		{                                                                          
			$instance->timesSeen = $timesSeen + 1;                             
			$instance->lastSeen = time();                                      
		}  		
		return $instance;
	}
	
	public static function fromString($line)
	{
		$entry = explode(",", $line);
		$instance= ProximusUniqueUser::withData($entry[0], $entry[1], $entry[2], $entry[3], $entry[4]);
		return $instance;
	}
	
	public function getFirstSeenAsString()
	{
		return strftime("%Y-%m-%d %r", $this->firstSeen);
	
	}
	
	public function getLastSeenAsString()
	{
		return strftime("%Y-%m-%d %r", $this->lastSeen);
	
	}
	
	public function getPersistenceLine()
	{
		return $this->macAddress . "," . $this->sessionId . "," . $this->firstSeen . "," . $this->lastSeen . "," . $this->timesSeen . "\n";
	}
}
?>