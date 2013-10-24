#!/bin/bash

echo "*-----------------------------*"
echo "|      Connectivity           |"
echo "*-----------------------------*"
echo "Ping: devices.proximusmobility.com"
PING=`ping -c 5 devices.proximusmobility.com | grep packets`
echo "$PING"
echo "*-----------------------------*"
IFS=$'\n'
interfaces=(`/sbin/ifconfig -a | egrep "eth[0-9]|wlan[0-9]|nap|captive|gn"`)
if [ -n "${interfaces[0]}" ]
then
	for interface in ${interfaces[@]}
	do
		iface=`echo $interface | awk '{print $1}'`
		ip=`/sbin/ifconfig -a $iface | grep "inet addr" | awk '{print $2}' | cut -f 2 -d ':'`
		mac=`/sbin/ifconfig -a $iface | grep "HWaddr" | awk '{print $5}'`
		echo "Interface: $iface MAC: $mac IP: $ip"
	done
else
	echo "IMPOSSIBLE (`date`): No Interfaces Are Up Runnning "
fi
echo "*-----------------------------*"
NETSTAT=`netstat -nr`
echo "$NETSTAT"
echo "*-----------------------------*"
echo ""
echo "*-----------------------------*"
echo "|      Software               |"
echo "*-----------------------------*"
IFS=$' '
list="lighttpd hostapd obexsender"
for service in $list; do
	running=`ps w | grep $service | grep -v grep`
	if [ -n "$running" ]
	then
		echo "$service is running"
	else
		echo "WARNING (`date`): $service is NOT running"
	fi
done
echo "*-----------------------------*"
IFS=$'\n'
processes=(`ps w | egrep "DENVERCODER|proximus" | grep -v grep`)
if [ -n "${processes[0]}" ]
then
	echo "Proximus Software Running:"
	for process in ${processes[@]}
	do	
			echo "Process: $process";
	done
else
	echo "WARNING (`date`): No Proximus Software Runnning "
fi
echo "*-----------------------------*"

