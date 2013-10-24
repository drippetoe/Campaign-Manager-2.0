#!/bin/bash
#########################
# AUTO GENERATED SCRIPT #
#########################
# NETWORK INFORMATION   #
#########################
# SSID: Proximus-WIFI
# Channel: 1
# Encryption is: on
# #######################
INTERFACE=`/home/proximus/bin/bash_scripts/autoInterface.sh`
killall -q wpa_supplicant
/sbin/ifconfig $INTERFACE down
/usr/sbin/wpa_supplicant  -B -dd -Dwext -i $INTERFACE -c /etc/wpa_supplicant.conf
/sbin/ifconfig $INTERFACE up
/usr/sbin/iwconfig $INTERFACE essid Proximus-WIFI channel "1" mode "Managed"
/sbin/udhcpc -i $INTERFACE -t 3
hasIp=`/sbin/ifconfig -a $INTERFACE | grep "inet addr" | awk '{print $2}' | cut -f 2 -d ':' 2>/dev/null`
if [ -z "$hasIp" ]; then
echo "Unable to connect to Proximus-WIFI"
rm -f /home/proximus/config/wificonnected
exit 1
else
touch /home/proximus/config/wificonnected
echo $INTERFACE > /home/proximus/config/backhaul.conf
echo "Stopping Ethernet.."
/sbin/ifconfig nap down 2>/dev/null
exit 0
fi
