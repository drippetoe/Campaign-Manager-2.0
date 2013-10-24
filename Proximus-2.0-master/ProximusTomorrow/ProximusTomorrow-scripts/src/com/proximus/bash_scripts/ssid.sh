#!/bin/bash
echo "Changing SSID to: $1"
/usr/sbin/uaputl bss_stop
/usr/sbin/uaputl sys_cfg_ssid "${1}"
/usr/sbin/uaputl bss_start
