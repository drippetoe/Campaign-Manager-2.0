#!/bin/bash
#/home/proximus/bin/bash_scripts/Dreamplug/autoinstall.sh


LOG_DIR="/home/proximus/logs/client"
LOG_FILENAME="shell.$(date +'%Y.%m.%d').log"
OVERRIDE="/home/proximus/bin/override"
DETECTDONGLE="/home/proximus/bin/bash_scripts/Dreamplug/detectWifiDongle.sh"

mkdir -p ${LOG_DIR}
echo $(/bin/date)
echo "Start of start_proximus_client.sh Python Version"

echo "Checking if manual override..."
if [ -f $OVERRIDE  ]; then
  echo "OVERRIDE... stopping process"
  exit 0
fi

echo "No override found"

echo "Checking if python is already running"
echo "ps aux | grep 'DeviceClient.py DENVERCODER' | grep -v grep | wc -l"
echo "Result: `ps aux | grep 'DeviceClient.py DENVERCODER' | grep -v grep | wc -l`" 
#Checking if python is already running
PYTHON_IS_RUNNING=`ps auxww | grep 'DeviceClient.py DENVERCODER' | grep -v grep | wc -l`
if [ ${PYTHON_IS_RUNNING} -gt "0" ]; then
	echo "Stopping Current Process"
	exit 0
fi

#Check wifi connection
bash $DETECTDONGLE
echo "No other python client is running... starting it"
(cd /home/proximus/bin/client/ && nohup /home/proximus/bin/client/DeviceClient.py DENVERCODER > ${LOG_DIR}/${LOG_FILENAME} 2>&1 &)
echo "Restarting lighttpd"
/home/proximus/bin/setUIDWrapper lighttpd restart
