#!/bin/bash

#if ethernet is valid
#dongle off

ip_addr=`ifconfig -a | grep -m 1 inet | grep -m 1 Bcast | grep -m 1 Mask | awk '{print $2}'`
ip_addr=${ip_addr##*:}
if [ -z ${ip_addr} ];
then
	echo "NO ETHERNET"
	/home/proximus/bin/bash_scripts/dongle.sh on
else 
	echo "WE HAVE ETHERNET"
	/home/proximus/bin/bash_scripts/dongle.sh off
fi 
