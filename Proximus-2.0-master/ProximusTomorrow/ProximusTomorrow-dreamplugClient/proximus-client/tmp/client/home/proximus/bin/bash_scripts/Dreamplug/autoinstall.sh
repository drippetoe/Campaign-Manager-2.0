#!/bin/bash

##
# @AUTHOR: Eric Johansson
# @EMAIL: ejohansson@proximusmobility.com
#
LOGUPLOAD=/home/proximus/logs/queue
UPDATEDIR=/tmp/obex
NOW=$(date)

mkdir -p $UPDATEDIR

##
# Installs a wpk file
# @param $1 file1.dp.wpk
#
install() {
	if [ -n "$1" ]; then 
		chown root:root $UPDATEDIR/$1
		#echo "-- Installing --" | tee -a $UPDATEDIR/$1.txt
		echo "# ${NOW}" | tee -a $UPDATEDIR/$1.txt
		mkdir -p $UPDATEDIR/$1-tmp  2>&1 | tee -a $UPDATEDIR/$1.txt
		tar -xvf $UPDATEDIR/$1 -C $UPDATEDIR/$1-tmp 2>&1 | tee -a $UPDATEDIR/$1.txt
		rm -f $UPDATEDIR/$1 2>&1 | tee -a $UPDATEDIR/$1.txt
		chown -R root:root $UPDATEDIR/$1-tmp
		chmod 755 $UPDATEDIR/$1-tmp/wpkg.pif
		. $UPDATEDIR/$1-tmp/wpkg.pif 2>&1 | tee -a $UPDATEDIR/$1.txt
		cp $UPDATEDIR/$1.txt $LOGUPLOAD/$1.txt
	fi
}

##
# Cleanup after installation.
# @param $file1.dp.wpk
#
clean() {
        if [ -d $UPDATEDIR/$1-tmp ]; then
        	rm -f -r $UPDATEDIR/$1-tmp 2>&1 | tee -a $UPDATEDIR/$1.txt
		fi
}

##
# Installs files in $UPDATEDIR
#
loop() {
	#Check for *.dp.wpk files
	if [ -d $UPDATEDIR ]; then
		IFS=''
		INSTALLFILES=(`ls $UPDATEDIR | grep "dp.wpk$"`)
		if [ ${#INSTALLFILES[@]} -gt 0 ]; then
			for f in ${INSTALLFILES[@]}; do
				echo "Found: ${#INSTALLFILES[@]} to install."
				#echo "Installing \"$f\"."
				echo "default-on" > /sys/class/leds/guruplug\:green\:wmode/trigger
		        echo "default-on" > /sys/class/leds/guruplug\:red\:wmode/trigger
		        echo "default-on" > /sys/class/leds/guruplug\:green\:health/trigger
				install $f
				#echo "Cleaning up \"$f\"."
				echo "none" > /sys/class/leds/guruplug\:green\:wmode/trigger
		        echo "none" > /sys/class/leds/guruplug\:red\:wmode/trigger
		        echo "none" > /sys/class/leds/guruplug\:green\:health/trigger
				clean $f
			done
		fi
	fi
}

##
# Install from /tmp/obex
#
loop

##
# Install from all mounted usb
#

for i in 0 1 2 3 4 5 6 7 8 9; do
	if [ -d /media/usb$i ]; then
		UPDATEDIR=/media/usb$i
		loop
	fi
done
