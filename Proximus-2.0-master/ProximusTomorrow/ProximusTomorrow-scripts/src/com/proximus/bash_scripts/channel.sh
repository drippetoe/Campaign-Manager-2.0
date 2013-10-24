#!/bin/bash
echo "Changing Wifi Channel to: $1"
/usr/sbin/uaputl bss_stop
/usr/sbin/uaputl sys_cfg_channel $1
/usr/sbin/uaputl bss_start
