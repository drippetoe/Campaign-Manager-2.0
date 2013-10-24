#!/bin/bash

list="lighttpd hostapd obexsender"

for service in $list; do
   echo "Checking for $service"
   ps auxw | grep $service | grep -v grep
done

# /usr/sbin/supportinfo
