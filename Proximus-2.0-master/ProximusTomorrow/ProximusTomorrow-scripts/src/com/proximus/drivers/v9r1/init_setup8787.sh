#!/bin/sh

# This is called from /etc/rc.local to perform the initial setup.

# We always bootup in AP mode. Delete any stale files
#rm -f /etc/wlanclient.mode
curdir=/root/k2.6.39.4
insmod $curdir/mlan.ko
insmod $curdir/sd8787.ko drv_mode=2
ifconfig uap0 192.168.1.1 up

SSID=dream-uAP-`ifconfig uap0 | awk -F ":" '/HWaddr/ {print $6$7}'`

uaputl sys_cfg_ssid $SSID
uaputl bss_start
iptables -t nat -A POSTROUTING -o eth0 -j MASQUERADE
iptables -t nat -A POSTROUTING -o ppp0 -j MASQUERADE
echo 1 > /proc/sys/net/ipv4/ip_forward 
/etc/init.d/udhcpd start
/etc/init.d/dnsmasq start
iptables -A INPUT -i uap0 -p tcp -m tcp --dport 80 -j ACCEPT

# Re-enable bluetooth. In the earlier case, it didn't find the firmware.
#rmmod libertas_sdio libertas btmrvl_sdio btmrvl bluetooth 2>/dev/null
/etc/init.d/bluetooth start

insmod $curdir/mbtchar.ko
insmod $curdir/bt8787.ko
hciconfig hci0 up
hciconfig hci0 piscan
mute-agent &

blinkled >> /dev/null

# Set leds
echo 1 > `eval ls /sys/class/leds/guruplug\:green\:wmode/brightness`
echo 0 > `eval ls /sys/class/leds/guruplug\:red\:wmode/brightness`

