#!/bin/bash
##########################
# @author Eric Johansson #
##########################

WPACONFIG=/etc/wpa_supplicant.conf 
CONNECTSCRIPT=/home/proximus/bin/setup/networkConnect.sh
RETRYDHCP="3"
SSID=""
CHANNEL="auto"
KEYMGMT=""
SIGNAL=""
PASSPHRASE=""
KEY=""
SELECTED=-1
ETHINTERFACE="nap"
INTERFACE=""
APINTERFACE=""
WIFICONNECTED="/home/proximus/config/wificonnected"
################


function autoInterface()
{
	for (( i=0; i<=9; i++ ))
	do
		/sbin/ifconfig "wlan$i" up 2>/dev/null
	done
	IFS=$'\n'
	interfaces=(`/sbin/ifconfig -a | grep "wlan[0-9]" | awk '{print $1" "$5}' | sed 's/://g'`)
	for interface in ${interfaces[@]}
	do
		octet=`echo $interface | awk '{print $2}' | tr '[:lower:]' '[:upper:]'`
		octet=${octet:0:6} 
		if [ "$octet" == "001F1F" ]
		then
			APINTERFACE=`echo $interface | awk '{print $1}'`
		else
			INTERFACE=`echo $interface | awk '{print $1}'`
		fi				
	done
	return 1
}

function collectPassphrase()
{
	echo "Using Passphrase: $PASSPHRASE"
}



function writeConnectScript()
{
  encOnOff="$1"
	
	echo "#!/bin/bash" > $CONNECTSCRIPT
	echo "#########################" >> $CONNECTSCRIPT
	echo "# AUTO GENERATED SCRIPT #" >> $CONNECTSCRIPT
	echo "#########################" >> $CONNECTSCRIPT
	echo "# NETWORK INFORMATION   #" >> $CONNECTSCRIPT
	echo "#########################" >> $CONNECTSCRIPT
	echo "# SSID: $SSID" >> $CONNECTSCRIPT
	echo "# Channel: $CHANNEL" >> $CONNECTSCRIPT
	echo "# Encryption is: $encOnOff" >> $CONNECTSCRIPT
	echo "# #######################" >> $CONNECTSCRIPT
	echo "INTERFACE=\`/home/proximus/bin/bash_scripts/autoInterface.sh\`" >> $CONNECTSCRIPT
	echo "if [ -z "$INTERFACE" ]; then"
	echo "exit 1"
	echo "fi"
	echo "killall -q wpa_supplicant" >> $CONNECTSCRIPT
	echo "/sbin/ifconfig \$INTERFACE down" >> $CONNECTSCRIPT
	if [ "$encOnOff"=="on" ]; then
		if [ "$KEYMGMT"=="WPA-PSK" ]; then
			echo "/usr/sbin/wpa_supplicant  -B -dd -Dwext -i "\$INTERFACE" -c $WPACONFIG" >> $CONNECTSCRIPT
			echo "/sbin/ifconfig \$INTERFACE up" >> $CONNECTSCRIPT
			echo "/usr/sbin/iwconfig \$INTERFACE essid $SSID channel \"$CHANNEL\" mode \"Managed\"" >> $CONNECTSCRIPT
		else
			echo "/sbin/ifconfig \$INTERFACE up" >> $CONNECTSCRIPT
			echo "/usr/sbin/iwconfig \"\$INTERFACE\" essid \"$SSID\" channel \"$CHANNEL\" key \"s:$PASSPHRASE\"" >> $CONNECTSCRIPT
		fi
	else
			echo "/sbin/ifconfig \$INTERFACE up" >> $CONNECTSCRIPT
			echo "/usr/sbin/iwconfig \$INTERFACE essid $SSID channel \"$CHANNEL\"" >> $CONNECTSCRIPT
	fi
	echo "/sbin/udhcpc -i \$INTERFACE -t $RETRYDHCP" >> $CONNECTSCRIPT
	echo "hasIp=\`/sbin/ifconfig -a \$INTERFACE | grep \"inet addr\" | awk '{print \$2}' | cut -f 2 -d ':' 2>/dev/null\`" >> $CONNECTSCRIPT
	echo "if [ -z \"\$hasIp\" ]; then" >> $CONNECTSCRIPT
	echo "echo \"Unable to connect to $SSID\"" >> $CONNECTSCRIPT
	echo "rm -f /home/proximus/config/wificonnected" >> $CONNECTSCRIPT 
	echo "exit 1" >> $CONNECTSCRIPT
	echo "else" >> $CONNECTSCRIPT
	echo "touch /home/proximus/config/wificonnected" >> $CONNECTSCRIPT
	echo "echo \$INTERFACE > /home/proximus/config/backhaul.conf" >> $CONNECTSCRIPT
	echo "echo \"Stopping Ethernet..\"" >> $CONNECTSCRIPT
	echo "/sbin/ifconfig $ETHINTERFACE down 2>/dev/null" >> $CONNECTSCRIPT
	echo "exit 0" >> $CONNECTSCRIPT
	echo "fi" >> $CONNECTSCRIPT
	chmod a+x $CONNECTSCRIPT
	chown root:wheel $CONNECTSCRIPT
	touch $WIFICONNECTED
        echo $INTERFACE > /home/proximus/config/backhaul.conf
}

function wpaConnect()
{
	network=`/usr/sbin/wpa_passphrase "$SSID" "$PASSPHRASE"`
	KEY=`echo "$network" | grep psk | grep -v '#psk' | sed 's/psk=//' | awk '{print $1}'`
	DATE=`date`
	echo "#WIFI SETUP @ $DATE" > "$WPACONFIG"
	echo "ctrl_interface=/var/run/wpa_supplicant" >> "$WPACONFIG"
	echo "ctrl_interface_group=wheel" >> "$WPACONFIG"
	echo "ap_scan=1" >> "$WPACONFIG"
	echo "$network" | sed "s/{/{\n\tkey_mgmt=${KEYMGMT}/" >> "$WPACONFIG"
	echo "*****************************************"
	echo "Writing $WPACONFIG.."
	cat $WPACONFIG
	echo "*****************************************"
	/usr/bin/killall -q wpa_supplicant
	/sbin/ifconfig "$INTERFACE" down
	/usr/sbin/wpa_supplicant  -B -dd -Dwext -i "$INTERFACE" -c $WPACONFIG
	/sbin/ifconfig "$INTERFACE" up
	/usr/sbin/iwconfig "$INTERFACE" essid "$SSID" channel "$CHANNEL" mode "Managed"
	/sbin/udhcpc -i "$INTERFACE" -t "$RETRYDHCP"
	
}



###########
# MAIN
###########


function main()
{
	#Select a good interface
	autoInterface
	if [ -z "$INTERFACE" ]; then
		  
		  echo "No Client Interface."
		  exit 1
	fi
	echo "Host Access Point Interface: $APINTERFACE"
	echo "Client Interface: $INTERFACE"
	#List/Scan for networks
	PWD=`pwd`
	IFS=$'$'
	infos=(`cat "$PWD"/network.list | egrep "Cell|Channel:|key|ESSID:|Signal level=|Mode:|WPA2 Version 1|WPA Version 1" | sed 's/Cell/\n$/'`)
	info_total=${#infos[@]}
	if [ -z "${infos[0]}" ]; then
		  
		  echo "Wifi Networks Unavailible."
		  exit 1
	fi



	#Manual Wifi select
	#selectWifi
	wifiInfo=${infos[$SELECTED]}
	echo "*****************************************"
	SSID=`echo "$wifiInfo" | grep "ESSID:" | cut -f 2 -d '"'`
	echo "SSID: $SSID"
	testChannel=`echo "$wifiInfo" | grep "Channel" | cut -f 2 -d ':'`
	if [ -n "$testChannel" ]
	then
		CHANNEL=$testChannel
	fi
	echo "Channel: $CHANNEL"
	testSignal=`echo "$wifiInfo" | grep "Signal level=" | cut -f 3 -d '='`
	if [ -n "$testSignal" ]
	then
		SIGNAL=$testSignal
	fi
	echo "Signal: $testSignal"
	#Encryption and Security
	encOnOff=`echo "$wifiInfo" | grep "Encryption key:" | cut -f 2 -d ':'`
	echo "Encryption is: $encOnOff"
	echo "*****************************************"

	if [ "$encOnOff" == "on" ]
	then
		test=`echo "$wifiInfo" | grep "WPA2 Version 1" | cut -f 2 -d ':'`
		if [ -z "$test" ]
		then
			test=`echo "$wifiInfo" | grep "WPA Version 1" | cut -f 2 -d ':'`
			if [ -z "$test" ]
			then
				echo "Unknown Encryption."						
				collectPassphrase
				/sbin/ifconfig "$INTERFACE" up
				/usr/sbin/iwconfig "$INTERFACE" essid "$SSID" channel "$CHANNEL" key "s:$PASSPHRASE"
				/sbin/udhcpc -i "$INTERFACE" -t "$RETRYDHCP"		
			else
				KEYMGMT="WPA-PSK"
				echo "Supported encryption WPA."
				wpaConnect
			fi
		else
			KEYMGMT="WPA-PSK"
			echo "Supported encryption WPA2."	
			wpaConnect	
		fi
	else
		rm $WPACONFIG
		/sbin/ifconfig "$INTERFACE" up
		/usr/sbin/iwconfig "$INTERFACE" essid "$SSID" channel "$CHANNEL"
		/sbin/udhcpc -i "$INTERFACE" -t $RETRYDHCP			
	fi
		

	hasIp=`/sbin/ifconfig -a "$INTERFACE" | grep "inet addr" | awk '{print $2}' | cut -f 2 -d ':' 2>/dev/null`
	if [ -z "$hasIp" ]
	then
		echo "Unable to connect to $SSID"
		exit 1
	else
		writeConnectScript $encOnOff
		echo "Connected to $SSID"
		exit 0
	fi
}


#Run here
if [ "$1" == "wifi" ]; then
	if [ -n "$2" ]; then
	SELECTED="$2"
	PASSPHRASE="$3"
	main	
	fi
fi 

