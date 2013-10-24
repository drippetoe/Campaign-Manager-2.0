#!/bin/bash
function startBluetoothLED()
{
  blinkbtled 0xf1010148 w 0x000 > /dev/null
  echo "default-on" > /sys/class/leds/guruplug\:green\:health/trigger
  #blinkled > /dev/null
}

function stopBluetoothLED()
{
  blinkbtled 0xf1010148 w 0x000 > /dev/null
  echo "none" > /sys/class/leds/guruplug\:green\:health/trigger
}

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



function restartBluetooth()
{
    /etc/init.d/bluetooth restart
    hciUp
}
function startBluetooth()
{
  BTNAME=$1
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


function setName ()
{
  BTNAME=$1
  IFS=$'\n'
  HCI=(`/usr/sbin/hciconfig -a | grep hci | awk '{print substr($1,0,5)}'`)
  TYPE=(`/usr/sbin/hciconfig -a | grep hci | awk '{print $5}'`)
  total=${#HCI[@]}
  fusb=0
  for (( i=0; i<=$(( $total -1 )); i++ ))
  do
    if [ "${TYPE[$i]}" = "USB" ]
    then
      echo "Setting ${HCI[$i]} with name $BTNAME"
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
      echo "Setting ${HCI[$i]} with name $BTNAME"
      /usr/sbin/hciconfig "${HCI[$i]}" up
      sleep 1
      /usr/sbin/hciconfig "${HCI[$i]}" piscan
      sleep 1
      /usr/sbin/hciconfig "${HCI[$i]}" name "$BTNAME"
    done
   fi
}

function getName ()
{
  IFS=$'\n'
  NAMES=(`/usr/sbin/hciconfig -a | grep Name | awk '{print $2}'`)
  total=${#NAMES[@]}
  fusb=0
  for (( i=0; i<=$(( $total -1 )); i++ ))
  do
    echo "Bluetooth name: ${NAMES[$i]}"
  done
  
}

function statusBluetooth ()
{
    /etc/init.d/bluetooth status
    if [ "$?" -ne "0" ]; then
        echo "false"
        exit 1
    else
        echo "true"
        exit 0
    fi
}

function keepAliveBluetooth ()
{
    /etc/init.d/bluetooth status
    if [ "$?" -ne "0" ]; then
        /etc/init.d/bluetooth start
    fi
}

if [ "$(id -u)" != "0" ]; then
	echo "NO BLUETOOTH 4 U" 1>&2
	exit 1
fi

if [ "$1" = "start" ]; then
    startBluetooth "$2"
elif [ "$1" = "name" ]; then
    if [ -n "$2" ]; then
        setName "$2"
    else
        getName
    fi 
elif [ "$1" = "stop" ]; then
    stopBluetooth
elif [ "$1" = "up" ]; then
    hciUp
elif [ "$1" = "restart" ]; then
    restartBluetooth
elif [ "$1" = "keepAlive" ]; then
    keepAliveBluetooth
elif [ "$1" = "status" ]; then
    status=`/etc/init.d/bluetooth status`
    if [ "$status" = "bluetooth is running." ]; then
        echo "OK: $status"
        exit 0
    else
        echo "BAD: $status"
        exit 1
    fi
else
    echo "Usage: start <name> | restart | stop | name | status | up <name>";
fi

#/usr/sbin/hciconfig -a