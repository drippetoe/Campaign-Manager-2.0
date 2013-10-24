#!/bin/bash

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

if [ "$1" == "start" ]; then
  /etc/init.d/captiveportal start
  startRestartHostApd
elif [ "$1" == "stop" ]; then
  /etc/init.d/captiveportal stop
elif [ "$1" == "restart" ]; then
  /etc/init.d/captiveportal restart
  sleep 2
  startRestartHostApd
elif [ "$1" == "status" ]; then
  status=`/etc/init.d/captiveportal status`
fi
