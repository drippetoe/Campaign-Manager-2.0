#!/bin/sh
# Detect and return Platform

PLATFORM_LINUX_BLUEGIGA="Bluegiga"
PLATFORM_LINUX_DREAMPLUG="Dreamplug"
PLATFORM_LINUX_DDWRT="DDWRT"
PLATFORM_MAC="MacOSX"
PLATFORM_UNKNOWN="Unknown"

if [ -d  "/etc/bluegiga-release" ]; then
  echo "$PLATFORM_LINUX_BLUEGIGA"
elif [ -d  "/etc/rc.local" ]; then
  echo "$PLATFORM_LINUX_DREAMPLUG"
elif [ -d  "/jffs" ]; then
  echo "$PLATFORM_LINUX_DDWRT"
elif [ -d  "/Library" ]; then
  echo "$PLATFORM_MAC"
else
  echo "$PLATFORM_UNKNOWN"
fi
exit 0
