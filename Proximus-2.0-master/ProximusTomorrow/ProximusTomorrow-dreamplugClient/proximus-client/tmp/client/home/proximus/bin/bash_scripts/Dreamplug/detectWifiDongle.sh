#!/bin/bash
##############################
# Vars
########
CONFIG_DIR="/home/proximus/config"
AUTOINTERFACE="/home/proximus/bin/bash_scripts/Dreamplug/autoInterface.sh"
APFILE=/home/proximus/config/access_point.conf
EXTFILE=/home/proximus/config/backhaul.conf
BACKHAULCONFIG=$CONFIG_DIR"/backhaul.conf"
DETECTDONGLE="/home/proximus/bin/bash_scripts/Dreamplug/detectWifiDongle.sh"
WIFICONNECT="/home/proximus/bin/setup/networkConnect.sh"
WIFICONNECTED="/home/proximus/config/wificonnected"

VENDORNAME="BELKIN"
VENDORID="050d"

if [ -f $EXTFILE ]; then                                          
  CLEANUP=`cat $EXTFILE | egrep "^wlan[0-9]"`                     
fi 

for v in $VENDORID                                                
do                                                                
  found=`lsusb | grep $v`                                         
  if [ -n "$found" ]; then                                        
    if [ $? == "0" ]; then                                        
      echo "Found WIFI Dongle"                                  
      if [ -f $WIFICONNECT ]; then                                
        if [ ! -f $WIFICONNECTED ]; then                          
          echo "Connecting to WIFI"                               
          bash $WIFICONNECT                                       
        else                                            
          echo "Already CONNECTED to a WIFI Network"
        fi                                          
      else                                          
        echo "WIFI connection not setup"            
        if [ -n "$CLEANUP" ]; then                  
          rm -f $EXTFILE                            
          bash $AUTOINTERFACE                       
        fi                                          
      fi                                            
    else 
     echo "No WIFI dongle connected"                
     if [ -n "$CLEANUP" ]; then                     
       rm -f $EXTFILE                               
       bash $AUTOINTERFACE                          
     fi                                             
    fi                                              
  fi                                                
done 