#!/bin/bash
log="/tmp/bt_log.txt"

if [ "$1" == "start" ]; then
    /etc/init.d/bluetooth start  >> $log 2>&1  
    /etc/init.d/obexsender start >> $log 2>&1  
elif [ "$1" == "stop" ]; then
    /etc/init.d/bluetooth stop  >> $log 2>&1  
    /etc/init.d/obexsender stop >> $log 2>&1  
elif [ "$1" == "restart" ]; then
    /etc/init.d/bluetooth stop  >> $log 2>&1  
    /etc/init.d/obexsender stop >> $log 2>&1  
    /etc/init.d/bluetooth start  >> $log 2>&1  
    /etc/init.d/obexsender start >> $log 2>&1  
elif [ "$1" == "status" ]; then
    /etc/init.d/bluetooth status
    /etc/init.d/obexsender status
fi
