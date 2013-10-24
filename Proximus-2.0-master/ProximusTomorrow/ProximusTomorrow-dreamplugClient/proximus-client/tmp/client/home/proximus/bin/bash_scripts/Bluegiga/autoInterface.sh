#!/bin/bash
INTERFACE=""
APFILE=/home/proximus/config/access_point.conf
EXTFILE=/home/proximus/config/backhaul.conf
#############
# Automatically sets the interface in the hostapd.py file
# Then echoes the availible free interface to use for a client
#############

function autoInterface()
{
	for (( i=0; i<=9; i++ ))
        do
                /sbin/ifconfig "wlan$i" up 2>/dev/null
        done
        ##List all interfaces starting with wlan NOT mon.wlan
        IFS=$'\n'
        interfaces=(`/sbin/ifconfig -a | egrep "^wlan[0-9]" | awk '{print $1" "$5}' | sed 's/://g'`)
        for interface in ${interfaces[@]}
        do
                octet=`echo $interface | awk '{print $2}' | tr '[:lower:]' '[:upper:]'`
                octet=${octet:0:6}
                if [ "$octet" == "001F1F" ]
                then
                        APINTERFACE=`echo $interface | awk '{print $1}'`
                        if [ -f $APFILE ]; then
    	                    EXISTING_AP=`cat $APFILE`
    	                else
    	                    EXISTING_AP=""
    	                fi
		        if [ "$APINTERFACE" != "$EXISTING_AP" ]; then
                            echo "$APINTERFACE" > $APFILE
                        fi
                else
                        INTERFACE=`echo $interface | awk '{print $1}'`
                fi
        done
        echo "$INTERFACE"
        exit 0
                                                               
}

function autoBackhaul()
{
    possible_interfaces="nap `autoInterface` ppp0"
    for interface in $possible_interfaces; do
    	VALID_IP=$(ifconfig $interface 2>/dev/null | grep 'inet addr')
    	if [ "$VALID_IP" != "" ]; then
    	    if [ -f $EXTFILE ]; then
    	        EXISTING_BACKHAUL=`cat $EXTFILE`
    	    else
    	        EXISTING_BACKHAUL="nap"
    	    fi
    	    if [ "$interface" != "$EXISTING_BACKHAUL" ]; then
    	        echo $interface > $EXTFILE
    	    fi
    	    echo "$interface"
    	    exit 0
    	fi
    done
    # if we get here, no valid interface
    rm -f $EXTFILE
}

#Run
if [ "$1" == "backhaul" ]; then
    autoBackhaul
else
    autoInterface
fi