#!/bin/bash
##########################
# @author Eric Johansson #
##########################
WPACONFIG=/etc/wpa_supplicant.conf 
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
autoInterface
if [ -z "$INTERFACE" ]; then  
	  echo "No Client Interface."
	  exit 1
fi
PWD=`pwd`
echo "" > $PWD/network.list
/usr/sbin/iwlist $INTERFACE scan >> network.list
IFS=$'$'
infos=(`cat $PWD/network.list | egrep "Cell|Channel:|key|ESSID:|Signal level=|Mode:|WPA2 Version 1|WPA Version 1" | sed 's/Cell/\n$/'`)
info_total=${#infos[@]}
if [ -z "${infos[0]}" ]; then
	  
	  echo "Wifi Networks Unavailable."
	  exit 1
fi
IFS=$'$'
for (( i=1; i<=$(($info_total )); i++ ))
do
	#echo "${infos[$i]}"
	ssid=`echo "${infos[$i]}" | grep "ESSID:" | cut -f 2 -d '"'`
	if [ -z "$ssid" ]; then
		ssid="unknown"
	fi
	channel=`echo "${infos[$i]}" | grep "Channel" | cut -f 2 -d ':'`
	echo "$ssid  - channel: $channel"
done
