#!/bin/bash
# @Author Gilberto Gaxiola
## Kill all the proximus processes

function getPID() {
	ps w | grep 'DeviceClient.py DENVERCODER' | grep -v grep
}

if [ "$(id -u)" != "0" ]; then
	echo "oh noes can't use this script if not root" 1>&2
	exit 1
fi

getPID > /tmp/pid.txt
cat /tmp/pid.txt | while read line do; do
	kill -9 $line
done
rm -f /tmp/pid.txt
