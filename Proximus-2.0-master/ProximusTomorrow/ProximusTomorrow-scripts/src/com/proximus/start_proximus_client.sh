#!/bin/bash

###########################################################################
# added by bs to fix problem and put most dir definitions in 1 place
###########################################################################
PROX_LOG=/home/proximus/logs
PROX_BIN=/home/proximus/bin
PROX_INSTALLS=/root/proximus/installs
KERNEL=/home/proximus/kernel
#KERNEL_VERSION=/root/k2.6.39.4
##KERNEL_VERSION=$KERNEL/k2.6.39.4
KERNEL_VERSION=$KERNEL/v9r1
KERNEL_WIFI_MLAN=$KERNEL_VERSION/mlan.ko
KERNEL_WIFI_SD8XXX=$KERNEL_VERSION/sd8787.ko
KERNEL_BT8XXX=$KERNEL_VERSION/bt8787.ko
KERNEL_MBTCHAR=$KERNEL_VERSION/mbtchar.ko
KERNEL_MBT8XXX=$KERNEL_VERSION/mbt8787.ko
mkdir -p $PROX_LOG

###########################################################################
# Echo a string to the log and print it on screen
###########################################################################
function logMsg ()
{
   stamp=`date +'%Y.%m.%d %H:%M:%S'`
   echo "${stamp} $*" | tee -a "$PROX_LOG/proximus_console_script.log"
}


########################################################### 
#ALLOWING MANUAL OVERRIDE TO STOP THE CRON JOB FROM RUNNING
###########################################################
echo "Checking if override file exists"
if [  -f $PROX_BIN/override ]; then
	logMsg "Manual override... stopping process"
	exit 0
else 
	logMsg "No manual override... starting process"
fi



logMsg "START OF start_proximus_start"
####################################################
# Not Allowing re-spawn of Clients without calling
# service proximus start
# service proximus restart
#####################################################
START_SCRIPT_RUNNING=`ps axjf | grep start_proximus_client.sh | grep -v grep | wc -l`
logMsg "Checking if other processses are running"
logMsg "ps axjf | grep start_proximus_client.sh | grep -v grep"
logMsg "Result: `ps axjf | grep start_proximus_client.sh | grep -v grep`"
if [ ${START_SCRIPT_RUNNING} -gt "2" ]; then
        logMsg "yes stopping current process"
	exit 0
fi



echo "No other process is running, Checking if booted"
###########################################################################
if [ ! -f $PROX_BIN/booted ]; then
    # We always bootup in AP mode. Delete any stale files
    rm -f /etc/wlanclient.mode

    # if this is there, it is going to break things.....
    rm -rf /etc/udev/rules.d/70-persistent-net.rules
    #hostname="default"
    HOSTNAME="dp-`/sbin/ifconfig -a | grep eth | grep HWaddr | head -1 | awk '{print $5}' | tr  -d :`"
    echo "$HOSTNAME" > /etc/hostname
    logMsg "booted didn't exist cleaning up"

fi

############################################################
###  CHECKING IF ETH0 IS UP (NEEDED TO GET THE MAC ADDR
###  FOR LOGGING PURPORSES
###
############################################################
## restart networking service
#/etc/init.d/networking restart
logMsg "Bringing eth0 is up"
/sbin/ifconfig eth0 up
logMsg "Bringing eth1 is up"
/sbin/ifconfig eth1 up
checkEth=`/sbin/ifconfig eth0 | grep HWaddr | wc -l`
logMsg "Checking if eth0 is up"
logMsg "ifconfig eth0 | grep HWaddr | wc -l .... Result: $checkEth"
while [ "$checkEth" != "1" ];
do
    logMsg "Eth0 has not started... bringing it up"
    /sbin/ifconfig eth0 up
    sleep 5
    checkEth=`/sbin/ifconfig eth0 | grep HWaddr | wc -l`
    logMsg "ifconfig eth0 | grep HWaddr | wc -l .... Result: $checkEth"
done



###########################################################################
# Turn the LED lights on the device on or off
###########################################################################
# colors are "blue" or "green" or "bluetooth"
# on_or_off is "on" or "off"
function setLED ()
{ 
    color="$1"
    on_or_off="$2"

    if [ "${on_or_off}" == "on" ]; then
        command="default-on"
    else
        command="none"
    fi

    if [ "${color}" == "blue" ]; then
        echo "${command}" > /sys/class/leds/guruplug\:green\:wmode/trigger
    fi
    if [ "${color}" == "green" ]; then
        echo "${command}" > /sys/class/leds/guruplug\:red\:wmode/trigger
    fi
    if [ "${color}" == "bluetooth" ]; then
        if [ "${command}" == "default-on" ]; then
            blinkled > /dev/null
        else
            blinkbtled 0xf1010148 w 0x000 > /dev/null
        fi

    fi
}
###########################################################################
# Set up the Wireless Access Point
###########################################################################
function startWifiAccessPoint ()
{ 
    logMsg "ENABLING WIFI ACCESS POINT"
    /sbin/insmod $KERNEL_WIFI_MLAN
    /sbin/insmod $KERNEL_WIFI_SD8XXX drv_mode=2
    sleep 1
    /sbin/ifconfig uap0 192.168.3.1 up
    /usr/sbin/uaputl sys_cfg_channel 9
    /usr/sbin/uaputl sys_cfg_ssid Proximus-"$HOSTNAME" 
    /usr/sbin/uaputl bss_start
    /sbin/iptables -t nat -A POSTROUTING -o eth0 -j MASQUERADE
    /sbin/iptables -t nat -A POSTROUTING -o eth1 -j MASQUERADE
    /sbin/iptables -t nat -A POSTROUTING -o ppp0 -j MASQUERADE
    echo 1 > /proc/sys/net/ipv4/ip_forward
    /etc/init.d/dnsmasq start    
}
###########################################################################
# Enable Wireless Access Point
###########################################################################
function enableWifiAccessPoint ()
{ 
    logMsg "ENABLING WIFI ACCESS POINT"
    /sbin/ifconfig uap0 192.168.3.1 up
    /usr/sbin/uaputl bss_start
    /sbin/iptables -t nat -A POSTROUTING -o eth0 -j MASQUERADE
    /sbin/iptables -t nat -A POSTROUTING -o eth1 -j MASQUERADE
    /sbin/iptables -t nat -A POSTROUTING -o ppp0 -j MASQUERADE
    echo 1 > /proc/sys/net/ipv4/ip_forward
    /etc/init.d/dnsmasq start    
}
###########################################################################
# Insert bluetooth modules
###########################################################################
function insertModBluetooth ()
{
    /etc/init.d/bluetooth stop
    logMsg "Starting Bluetooth" 
    /sbin/insmod $KERNEL_MBTCHAR 2> /dev/null
    /sbin/insmod $KERNEL_BT8XXX 2> /dev/null
    /etc/init.d/bluetooth start
    $PROX_BIN/bash_scripts/bluetooth.sh start "Proximus Bluetooth"
}

###########################################################################
# Check to see if the Proximus Java code is running, and start it if needed
###########################################################################
function startProximusClient ()
{
    logMsg "startProximusClient() "
    IS_RUNNING=`ps axjf | grep proximus-suite.jar | grep -v grep | wc -l`
    logMsg "PS CALL: ps axjf | grep proximus-suite.jar | grep -v grep" 
    logMsg "We have ${IS_RUNNING} proximus-suite.jar running"
    if [ ${IS_RUNNING} -eq "0" ]; then
	logMsg "Starting proximus-suite.jar in nohup"
        echo `/sbin/ifconfig eth0 | grep HWaddr | awk '{print $5}' | tr -d :` > /home/proximus/bin/.macAddr
        #logMsg "Mac Addr is $macAddr"
        nohup java -jar $PROX_BIN/proximus-suite.jar > /dev/null 2>&1 &
        logMsg "proximus-suite.jar started in background"
    else
	logMsg "No need to start proximus-suite.jar"
    fi
    
}


###########################################################################
# Main Boot Sequence
###########################################################################
# Note the /home/proximus/bin/booted is to ensure we don't do this but once
# the file is deleted in /etc/rc.local on startup
if [ ! -f $PROX_BIN/booted ]; then
    logMsg "First Time Boot"
    startWifiAccessPoint
    insertModBluetooth
    #runAutoUpdate
    touch $PROX_BIN/booted
else
    logMsg "Booted: Making sure Wifi is up"
    enableWifiAccessPoint
fi

while [ 1 ];
do
     logMsg "checking if ProximusClient is running"
     startProximusClient
     logMsg "Sleep for 10 minutes "
     sleep 600
done
exit 0
