#!/bin/bash

list="lighttpd hostapd obexsender"

for service in $list; do
   echo "Checking for $service"
   ps w | grep $service | grep -v grep
done

# /usr/sbin/supportinfo
