#!/bin/bash
# @author Eric Johansson
###########################

CONFIG=/home/proximus/config/client.cfg.xml
TMP=/home/proximus/bin/setup/cfg.tmp
CHAN="$1"

if [ -z "$CHAN" ]; then
	channel=`cat $CONFIG | grep "config channel=\"" | cut -f 2 -d '"'`
	echo "$channel"
        exit 0
fi

echo "Setting channel to: $CHAN"

if [ "$CHAN" < "12" ]; then
	if [ "$CHAN" > "0" ]; then
		OUTPUT=`cat $CONFIG | sed "s/channel=\"[1-9][0-1]*\"/channel=\"${CHAN}\"/g"`
		echo "$OUTPUT"
		echo "$OUTPUT" > $TMP
		mv -f $TMP $CONFIG
		bash /home/proximus/bin/bash_scripts/kill_proximus.sh
		exit 0
	fi
fi
echo "Unable to set Channel"
exit 1
