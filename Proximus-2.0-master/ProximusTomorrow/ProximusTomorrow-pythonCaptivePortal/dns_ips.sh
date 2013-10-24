#!/bin/bash


# method taken from Linux Journal Jun 26, 2008
function valid_ip()
{
    local  ip=$1
    local  stat=1

    if [[ $ip =~ ^[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}$ ]]; then
        OIFS=$IFS
        IFS='.'
        ip=($ip)
        IFS=$OIFS
        [[ ${ip[0]} -le 255 && ${ip[1]} -le 255 \
            && ${ip[2]} -le 255 && ${ip[3]} -le 255 ]]
        stat=$?
    fi
    return $stat
}

function process_address()
{
        dnsname=$1
        for address in `dig A $dnsname | grep ^$dnsname | sed 's/\s\s*/ /g' | cut -f 5 -d ' ' |sort`
        do
                if valid_ip $address
                then
                		range=`whois $address | grep -E "CIDR" | sed -E 's/CIDR:[ ]*//g'`
                        if [ -z "$range" ]; then
                                echo "$address"
                        else
                                echo "$range"
                        fi
                else
                        #echo "$address -> need to dig deeper"
                        process_address $address
                fi
        done
}

process_address $1