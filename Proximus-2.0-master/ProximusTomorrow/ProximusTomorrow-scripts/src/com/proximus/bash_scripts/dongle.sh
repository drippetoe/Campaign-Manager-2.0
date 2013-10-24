#!/bin/bash

function startPPPDongle() {

  DMAN=$1
  carrier=""
 
  #echo "inside startPPPDongle"
  #echo "the DMAN is $DMAN and curr carrier is $carrier"

  case $DMAN in
  "Novatel Wireless Inc.")
  carrier="verizon"
  ;;
  "Sierra Wireless, Incorporated")
  carrier="sprint"
  ;;
  "HUAWEI Technology")
  carrier="tmobile"
  ;;
  *)
  esac

  echo "Current carrier is: $carrier"
  if [ `ifconfig -s | grep [p]pp | wc -l` > 0 ]; 
  then
      echo "/usr/bin/pon $carrier"
      /usr/bin/pon $carrier
  else 
      echo "No PPP in ifconfig"
  fi  
}

function PPPCheckHelper() {
echo "trying: $1"
if [ -f $1 ]; then
   DMAN=`cat $1`
   echo "It exists and DMAN is $DMAN"
   startPPPDongle "$DMAN"
fi
}


function PPPCheck() {
#see if a dongle is plugged in
base_path="/sys/devices/platform/orion-ehci.0/usb1/1-1"
actual_path=${base_path}"/1-1.2/manufacturer"
PPPCheckHelper $actual_path
actual_path=${base_path}"/1-1.2/1-1.2.2/manufacturer"
PPPCheckHelper $actual_path
actual_path=${base_path}"/1-1.3/manufacturer"
PPPCheckHelper $actual_path
actual_path=${base_path}"/1-1.3/1-1.3.2/manufacturer"
PPPCheckHelper $actual_path
}


function stopPPPDongle() {
#Just forcing all different kinds
/usr/bin/poff -a
}



if [ $1 = "on" ]
then
   echo "is on"
   PPPCheck
else
  echo "is off"
  stopPPPDongle
fi
