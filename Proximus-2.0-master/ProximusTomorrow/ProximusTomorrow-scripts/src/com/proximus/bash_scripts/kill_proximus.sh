#!/bin/bash
# @Author Gilberto Gaxiola
## Kill all the proximus processes

function getPID() {
	ps aux | grep proximus | grep -v grep | grep -v kill_proximus | awk '{print $2}'
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
/home/proximus/bin/bash_scripts/led.sh killAll