#!/bin/bash
###################################################
# Copyright (c) 2010-2012, Proximus Mobility, LLC #
###################################################


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

if [ "$1" = "start" ]; then
    startLighttpd
elif [ "$1" = "stop" ]; then
    stopLighttpd
elif [ "$1" = "restart" ]; then
    restartLighttpd
elif [ "$1" = "status" ]; then
    statusLighttpd
fi

