#!/bin/bash
###################################################
# Copyright (c) 2010-2012, Proximus Mobility, LLC #
###################################################
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

service="/etc/init.d/lighttpd"
function killPythonLogger ()
{
    PID=`ps aux  | grep accesslog_addmac.py | grep -v grep | awk '{print $2}'`
    if [ -n "$PID" ] ; then
        echo "Killing lighttpd python logger"
        kill $PID
    else
        echo "Logger already killed"
    fi
}


function startLighttpd ()
{
    /etc/init.d/lighttpd start
}

function stopLighttpd ()
{
    /etc/init.d/lighttpd stop
    killPythonLogger
}

function restartLighttpd ()
{
    echo "Restarting lighttpd"
    stopLighttpd
    startLighttpd
    echo "Done!"
}

function statusLighttpd ()
{
    /etc/init.d/lighttpd status
    if [ "$?" -ne "0" ]; then
        echo "false"
        exit 1
    else
        echo "true"
        exit 0
    fi
}

function startRestartHostApd()
{
    RUNNING=$(ps w | grep hostapd | grep -v grep | wc -l)
    if [ "$RUNNING" -gt "0" ]; then
        PID=$(ps w | grep hostapd | grep -v grep | awk '{print $1}')
        kill -9 $PID
        echo "sending SIGKILL to $PID"
        nohup /usr/sbin/hostapd /etc/hostapd.conf >/dev/null 2>&1 &
    else
        echo "HOSTAPD not running, starting it"
        nohup /usr/sbin/hostapd /etc/hostapd.conf >/dev/null 2>&1 &
    fi
}

if [ "$1" = "start" ]; then    
    if [ "$PLATFORM"=="$PLATFORM_LINUX_BLUEGIGA" ]; then
    	/etc/init.d/captiveportal start
  		startRestartHostApd
  	elif [ "$PLATFORM"=="$PLATFORM_LINUX_DREAMPLUG" ]; then
  		startLighttpd
  	fi
elif [ "$1" = "stop" ]; then
	if [ "$PLATFORM"=="$PLATFORM_LINUX_BLUEGIGA" ]; then
    	/etc/init.d/captiveportal stop
  	elif [ "$PLATFORM"=="$PLATFORM_LINUX_DREAMPLUG" ]; then
  		stopLighttpd
  	fi    
elif [ "$1" = "restart" ]; then
	if [ "$PLATFORM"=="$PLATFORM_LINUX_BLUEGIGA" ]; then
    	/etc/init.d/captiveportal restart
	    sleep 2
	    startRestartHostApd
  	elif [ "$PLATFORM"=="$PLATFORM_LINUX_DREAMPLUG" ]; then
  		restartLighttpd
  	fi    
elif [ "$1" = "status" ]; then
	if [ "$PLATFORM"=="$PLATFORM_LINUX_BLUEGIGA" ]; then
    	status=`/etc/init.d/captiveportal status`
  	elif [ "$PLATFORM"=="$PLATFORM_LINUX_DREAMPLUG" ]; then
  		statusLighttpd
  	fi
fi

