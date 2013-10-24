#!/bin/bash
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
