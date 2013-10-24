#!/bin/bash
JARFILE=ProximusTomorrow-client.jar
BASEDIR="$( cd "$( dirname "$0" )" && pwd )"
echo "Running from:$BASEDIR"
if [ "$(id -u)" != "0" ]; then
   echo "This script must be run as root" 1>&2
   exit 1
fi
echo "Killing Bluetooth"
bash $BASEDIR/kill.sh
echo "Done!"
sleep 5
blinkbtled 0xf1010148 w 0x000 > /dev/null
KERNEL_MODS=/root/k2.6.39.4
service bluetooth start
insmod $KERNEL_MODS/mbtchar.ko 2> /dev/null
insmod $KERNEL_MODS/bt8787.ko 2> /dev/null
hciconfig hci0 up
hciconfig hci0 name 'DreamPlug 2.0 Bluetooth'
java -jar $BASEDIR/$JARFILE > /dev/null &
echo "#!/bin/bash" > $BASEDIR/kill.sh
echo "# AUTO CREATED SCRIPT BY run.sh" >> $BASEDIR/kill.sh
echo "service bluetooth stop" >> $BASEDIR/kill.sh
echo "blinkbtled 0xf1010148 w 0x000 > /dev/null" >> $BASEDIR/kill.sh
echo "kill $!" >> $BASEDIR/kill.sh
echo "rm $BASEDIR/kill.sh" >> $BASEDIR/kill.sh
sleep 5
blinkled > /dev/null
echo "Follow main log"
tail -f /home/proximus/logs/main-proximus.log
