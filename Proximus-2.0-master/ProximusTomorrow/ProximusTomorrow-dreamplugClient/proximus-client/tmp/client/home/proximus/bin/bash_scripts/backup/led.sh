#!/bin/sh
################
# A is BELL
# B is 4
# C is 3
# D is 2
# E is 1
PLATFORM_LINUX_BLUEGIGA="Bluegiga"
PLATFORM_LINUX_DREAMPLUG="Dreamplug"
PLATFORM_LINUX_DDWRT="DDWRT"
PLATFORM_MAC="MacOSX"
PLATFORM_UNKNOWN="Unknown"

PLATFORM="$PLATFORM_UNKNOWN"

if [ -d  "/etc/bluegiga-release" ]; then
  PLATFORM="$PLATFORM_LINUX_BLUEGIGA"
elif [ -d  "/etc/rc.local" ]; then
  PLATFORM="$PLATFORM_LINUX_DREAMPLUG"
elif [ -d  "/jffs" ]; then
  PLATFORM="$PLATFORM_LINUX_DDWRT"
elif [ -d  "/Library" ]; then
  PLATFORM="$PLATFORM_MAC"
else
  PLATFORM="$PLATFORM_UNKNOWN"
fi

killAllLEDBG ()
{
    echo "abcde" > /dev/led 
}


blinkAll ()
{
    for i in 1 2 3 4 5 6 7 8 9 10
    do
        echo "ABCDE" > /dev/led
        sleep 1
        echo "abcde" > /dev/led
        sleep 1
    done
   
}

killAllLEDDP ()
{ 
    echo "none" > /sys/class/leds/guruplug\:green\:wmode/trigger
    echo "none" > /sys/class/leds/guruplug\:red\:wmode/trigger
    blinkbtled 0xf1010148 w 0x000 > /dev/null
    echo "none" > /sys/class/leds/guruplug\:green\:health/trigger
}


locateOne ()
{
    for i in 1 2 3 4 5 6 7 8 9 10
    do
        echo "default-on" > /sys/class/leds/guruplug\:green\:wmode/trigger
        echo "none" > /sys/class/leds/guruplug\:red\:wmode/trigger
        echo "none" > /sys/class/leds/guruplug\:green\:health/trigger
        sleep .3
        echo "none" > /sys/class/leds/guruplug\:green\:wmode/trigger
        echo "default-on" > /sys/class/leds/guruplug\:red\:wmode/trigger
        echo "none" > /sys/class/leds/guruplug\:green\:health/trigger
        sleep .3
        echo "none" > /sys/class/leds/guruplug\:green\:wmode/trigger
        echo "none" > /sys/class/leds/guruplug\:red\:wmode/trigger
        echo "default-on" > /sys/class/leds/guruplug\:green\:health/trigger
        sleep .3
        echo "none" > /sys/class/leds/guruplug\:green\:wmode/trigger
        echo "none" > /sys/class/leds/guruplug\:red\:wmode/trigger
        echo "default-on" > /sys/class/leds/guruplug\:green\:health/trigger
        sleep .3
    done
   
}




if [ "$1" = "killAll" ]; then
	if [ "$PLATFORM"=="$PLATFORM_LINUX_BLUEGIGA" ]; then
    	killAllLEDBG
  	elif [ "$PLATFORM"=="$PLATFORM_LINUX_DREAMPLUG" ]; then
  		killAllLEDDP
  	fi    
elif [ "$1" = "locate" ]; then
	if [ "$PLATFORM"=="$PLATFORM_LINUX_BLUEGIGA" ]; then
    	blinkAll
    	killAllLEDBG
  	elif [ "$PLATFORM"=="$PLATFORM_LINUX_DREAMPLUG" ]; then
  		locateOne
    	killAllLEDDP
  	fi
elif [ "$1" = "status" ]; then
	if [ "$PLATFORM"=="$PLATFORM_LINUX_DREAMPLUG" ]; then
  		cat /sys/class/leds/guruplug\:green\:wmode/trigger | egrep '\[none\]'
	    cat /sys/class/leds/guruplug\:red\:wmode/trigger
	    cat /sys/class/leds/guruplug\:green\:health/trigger
  	fi    
else
    echo "Usage: killAll | locate | status"
fi


