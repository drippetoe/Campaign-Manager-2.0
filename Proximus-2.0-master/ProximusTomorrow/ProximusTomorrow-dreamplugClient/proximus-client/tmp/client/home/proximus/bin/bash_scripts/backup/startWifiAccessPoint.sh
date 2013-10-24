#!/bin/bash
##############################################################
# Set up the Wireless Access Point
# Note that this does not bring it online (bss_start)
# We let the java code do that
##############################################################
KERNEL=/home/proximus/kernel
KERNEL_VERSION=$KERNEL/k2.6.39.4
KERNEL_WIFI_MLAN=$KERNEL_VERSION/mlan.ko
KERNEL_WIFI_SD8XXX=$KERNEL_VERSION/sd8787.ko
hostname="dp-`ifconfig -a | grep eth | grep HWaddr | head -1 | awk '{print $5}' | tr  -d :`"
echo ${hostname} > /etc/hostname

insmod $KERNEL_WIFI_MLAN
insmod $KERNEL_WIFI_SD8XXX drv_mode=2
/sbin/ifconfig uap0 192.168.3.1 up
/usr/sbin/uaputl sys_cfg_channel 9
/usr/sbin/uaputl uaputl sys_cfg_ssid Proximus-${hostname}
/usr/sbin/uaputl bss_start
/sbin/iptables -t nat -A POSTROUTING -o eth0 -j MASQUERADE
/sbin/iptables -t nat -A POSTROUTING -o ppp0 -j MASQUERADE
echo 1 > /proc/sys/net/ipv4/ip_forward
/etc/init.d/dnsmasq start
echo "default-on" > /sys/class/leds/guruplug\:green\:wmode/trigger