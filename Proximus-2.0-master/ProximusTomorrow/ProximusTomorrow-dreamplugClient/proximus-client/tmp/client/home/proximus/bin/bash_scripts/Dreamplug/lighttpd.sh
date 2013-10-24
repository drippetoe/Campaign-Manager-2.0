#!/bin/bash

BASEDIR=/home/proximus/bin

ACTIVE_UAP_IFACE=$(/sbin/ifconfig uap0 | grep 'inet addr' | sed 's/:/ /g' | awk '{print $3}')


function startRestartAP()
{
    APD_CONF="/etc/hostapd.conf"
    # ssid=Proximus Mobility Offer Network
    # channel=9
    if [ -f "${APD_CONF}" ]; then
        channel="$(/bin/grep 'channel=' ${APD_CONF} | cut -f2 -d '=')"
        ssid="$(/bin/grep 'ssid=' ${APD_CONF}| /bin/grep -v ignore_broadcast_ssid | /usr/bin/cut -f2 -d '=')"
    else
        channel='9'
        ssid='Proximus Mobility Offer Network'
    fi
    echo "Channel *${channel}* SSID *${ssid}*"
    /usr/sbin/uaputl bss_stop
    sleep 1
    if [ -z "${ACTIVE_UAP_IFACE}" ]; then 
    	/sbin/ifconfig uap0 up 192.168.45.3
    	ACTIVE_UAP_IFACE=192.168.45.3
    fi
    if [ -d /usr/sbin/uaputl ]; then 
    	ruaputl=/usr/sbin/uaputl
    elif [ -d /usr/bin/uaputl ]; then
    	ruaputl=/usr/sbin/uaputl
    else
    	ruaputl=`which uaputl`
    fi
    echo `$ruaputl sys_cfg_ssid "$ssid"`
    echo `$ruaputl sys_cfg_channel "$channel"`
    echo `$ruaputl bss_start`
}
function setInternetHotspotMode()
{
   if [ -z "${ACTIVE_UAP_IFACE}" ]; then 
    	/sbin/ifconfig uap0 up 192.168.45.3
   fi
   CAPTIVE_CONF="/etc/sysconfig/captiveportal"
   if [ -f "${CAPTIVE_CONF}" ]; then
       INETACCESS=$(grep 'INETACCESS=' ${CAPTIVE_CONF} | cut -f2 -d '=')
       LIMITEDINTERNET=$(grep 'LIMITEDINTERNET=' ${CAPTIVE_CONF} | cut -f2 -d '=')
       if [ "${INETACCESS}" == "Yes" ]; then
           if [ "${LIMITEDINTERNET}" == "Yes" ]; then
               ${BASEDIR}/portal.py closed start ${ACTIVE_UAP_IFACE}
           else
               ${BASEDIR}/portal.py open start ${ACTIVE_UAP_IFACE}
           fi
       else
           ${BASEDIR}/portal.py closed start ${ACTIVE_UAP_IFACE}
       fi
   else
       # assume captive
       ${BASEDIR}/portal.py closed start ${ACTIVE_UAP_IFACE}
   fi
}

function killAccessLogger()
{

	SERVICES=(`ps auxww | /bin/grep "accesslog_addmac.py" | /bin/grep -v grep | /bin/grep -v sh | awk '{print $2}'`)
	TOTAL=${#SERVICES[@]}
	if [ "$TOTAL" > "0" ]; then
	for (( i=0; i<=$(( $TOTAL -1 )); i++ ))
	do
	  echo "Killing ${SERVICES[$i]}"
	  kill -9 ${SERVICES[$i]}
	done
	else
	  echo "Accesslog not running"
	fi
		
}

if [ "$1" == "start" ]; then
  /etc/init.d/lighttpd start
  startRestartAP
elif [ "$1" == "stop" ]; then
  /etc/init.d/lighttpd stop
  killall lighttpd
  killAccessLogger
elif [ "$1" == "restart" ]; then
  /etc/init.d/lighttpd stop
  killall lighttpd
  killAccessLogger
  /etc/init.d/lighttpd start
  sleep 2
  startRestartAP
elif [ "$1" == "status" ]; then
  status=`/etc/init.d/lighttpd status`
fi
