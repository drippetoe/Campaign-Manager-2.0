#!/bin/bash
PLATFORM_LINUX_BLUEGIGA="Bluegiga"
PLATFORM_LINUX_DREAMPLUG="Dreamplug"
PLATFORM_LINUX_DDWRT="DDWRT"
PLATFORM_MAC="MacOSX"
PLATFORM_UNKNOWN="Unknown"
PLATFORM="PLATFORM_UNKNOWN"

BINDIR="/home/proximus/bin"

if [ -f  /etc/bluegiga-release ]; then
  PLATFORM="$PLATFORM_LINUX_BLUEGIGA"
elif [ -f  /etc/rc.local ]; then
  PLATFORM="$PLATFORM_LINUX_DREAMPLUG"
elif [ -f  /jffs ]; then
  PLATFORM="$PLATFORM_LINUX_DDWRT"
  BINDIR=/jffs/home/proximus/bin
elif [ -f  /Library ]; then
  PLATFORM="$PLATFORM_MAC"
else
  PLATFORM="$PLATFORM_UNKNOWN"
fi

###
# Move and then source aliases
###

mv /root/aliases/$PLATFORM.bashrc /root/.bashrc
mv /root/aliases/$PLATFORM.aliases /root/.aliases
source /root/.aliases