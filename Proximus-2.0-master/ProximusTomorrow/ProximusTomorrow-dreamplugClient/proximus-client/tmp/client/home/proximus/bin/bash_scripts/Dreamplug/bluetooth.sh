#!/bin/bash
log="/tmp/bt_log.txt"

BTNAME=`cat /etc/bluetooth.conf | grep "SET BLUETOOTH NAME" | sed 's/SET BLUETOOTH NAME //g' | sed 's/\"//g'`


function hciUp ()
{
  IFS=$'\n'
  HCI=(`/usr/sbin/hciconfig -a | grep hci | awk '{print substr($1,0,5)}'`)
  TYPE=(`/usr/sbin/hciconfig -a | grep hci | awk '{print $5}'`)
  total=${#HCI[@]}
  fusb=0
  for (( i=0; i<=$(( $total -1 )); i++ ))
  do
    if [ "${TYPE[$i]}" = "USB" ]
    then
      echo "Starting ${HCI[$i]}"
      /usr/sbin/hciconfig "${HCI[$i]}" up
      sleep 1
      /usr/sbin/hciconfig "${HCI[$i]}" piscan
      fusb=1
    else
      /usr/sbin/hciconfig "${HCI[$i]}" down
    fi
  done
  if [ "$fusb" = "0" ]
    then
    for (( i=0; i<=$(( $total -1 )); i++ ))
    do
      echo "Starting ${HCI[$i]}"
      /usr/sbin/hciconfig "${HCI[$i]}" up
      sleep 1
      /usr/sbin/hciconfig "${HCI[$i]}" piscan
    done
   fi
}


function startBluetooth()
{
  if [ -n "$BTNAME" ]
    then
    /etc/init.d/bluetooth start
    IFS=$'\n'
    HCI=(`/usr/sbin/hciconfig -a | grep hci | awk '{print substr($1,0,5)}'`)
    TYPE=(`/usr/sbin/hciconfig -a | grep hci | awk '{print $5}'`)
    total=${#HCI[@]}
    fusb=0
    for (( i=0; i<=$(( $total -1 )); i++ ))
    do
        if [ "${TYPE[$i]}" = "USB" ]
            then
            echo "Starting ${HCI[$i]} with name $BTNAME"
            /usr/sbin/hciconfig "${HCI[$i]}" up
            sleep 1
            /usr/sbin/hciconfig "${HCI[$i]}" piscan
            sleep 1
            /usr/sbin/hciconfig "${HCI[$i]}" name "$BTNAME"
            fusb=1
        else
        /usr/sbin/hciconfig "${HCI[$i]}" down
        fi
    done
    if [ "$fusb" = "0" ]
        then
        for (( i=0; i<=$(( $total -1 )); i++ ))
        do
        echo "Starting ${HCI[$i]} with name $BTNAME"
        /usr/sbin/hciconfig "${HCI[$i]}" up
        sleep 1
        /usr/sbin/hciconfig "${HCI[$i]}" piscan
        sleep 1
        /usr/sbin/hciconfig "${HCI[$i]}" name "$BTNAME"
        done
    fi
  else
    hciUp
  fi
}

function stopBluetooth()
{
  /etc/init.d/bluetooth stop
}

function restartBluetooth()
{
    /etc/init.d/bluetooth restart
    startBluetooth
}


if [ "$1" == "start" ]; then
    startBluetooth >> $log 2>&1  
    /etc/init.d/obexsender start >> $log 2>&1  
elif [ "$1" == "stop" ]; then
    /etc/init.d/bluetooth stop  >> $log 2>&1  
    /etc/init.d/obexsender stop >> $log 2>&1  
elif [ "$1" == "restart" ]; then
    restartBluetooth  >> $log 2>&1  
    /etc/init.d/obexsender stop >> $log 2>&1  
    /etc/init.d/obexsender start >> $log 2>&1  
elif [ "$1" == "status" ]; then
    /etc/init.d/bluetooth status
    /etc/init.d/obexsender status
fi
