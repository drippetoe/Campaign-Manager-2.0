#!/bin/bash
##############################
# Basic Dreamplug wpk package
##############################


##############################
# 0. DO NOT TOUCH
##############################
export DEBIAN_FRONTEND=noninteractive
NOW=$(date)
UPDATED=0
UPGRADED=0
CURDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"


rollback() {
   echo "Something failed!"
   exit 1
}

trap rollback INT TERM

function update()
{
	if [ "$UPDATED" != "0" ]; then
		apt-get update
		UPDATED=1
	fi
}

function upgrade()
{
	if [ "$UPGRADED" != "0" ]; then
		apt-get -o DPkg::Options="–force-confold" upgrade -y --assume-yes --force-yes
		UPGRADED=1
	fi
}

function install()
{
	echo "default-on" > /sys/class/leds/guruplug\:green\:wmode/trigger
	echo "default-on" > /sys/class/leds/guruplug\:red\:wmode/trigger
	echo "default-on" > /sys/class/leds/guruplug\:green\:health/trigger					
	if [ -n "$1" ]; then
		apt-get -o DPkg::Options="–force-confold" -f install -y --assume-yes --force-yes "$@"
	fi
	echo "none" > /sys/class/leds/guruplug\:green\:wmode/trigger
	echo "none" > /sys/class/leds/guruplug\:red\:wmode/trigger
	blinkbtled 0xf1010148 w 0x000 > /dev/null
	echo "none" > /sys/class/leds/guruplug\:green\:health/trigger
}

function remove()
{
	if [ -n "$1" ]; then
		apt-get -o DPkg::Options="–force-confold" -f  remove -y --assume-yes --force-yes "$@"
	fi
}

function clean()
{
	apt-get autoremove -y --assume-yes --force-yes 
	apt-get autoclean -y --assume-yes --force-yes
	echo "Auto remove and Clean... [ OK ]"
}

# unrolls tgx in same dir to specified path
function unroll()
{
	if [ -n "$1" ]; then
		if [ -n "$2" ]; then
			tar -xvf $CURDIR/$1 -C $2
			echo "Unrolled archive... [ OK ]"
		else
			echo "No directory specified, assuming $CURDIR/$1-tmp ... [ WARN ]"
			tar -xvf $CURDIR/$1 -C $CURDIR/$1-tmp
			echo "Unrolled archive... [ OK ]"
		fi
	else
		echo "No archive in argument... [ FAIL ]"
	fi
}

##############################
# 1. Add debian packages here
##############################
REMOVE_PACKAGES="apache2"
#ndiswrapper-source kernel-package
INSTALL_PACKAGES="dnsmasq lighttpd gcc dbus python-dbus python-dev libbluetooth-dev libbluetooth3 bluetooth bluez bluez-compat bluez-hcidump bluez-alsa bluez-cups bluez-pcmcia-support bluez-audio bluez-gstreamer bluez-utils python-bluez python-lightblue curl libcurl3 libcurl3-dev php5 php5-common php5-cgi php5-curl php5-cli dnsutils whois"
UNROLL_ARCHIVE=""
UNROLL_DIR=""


mv /etc/apt/sources.list /etc/apt/sources.list.old

cat <<EOF >/etc/apt/sources.list
# Backup in /etc/apt/sources.list.old
deb http://ftp.us.debian.org/debian/ squeeze main contrib non-free
deb http://http.us.debian.org/debian stable main contrib non-free
deb http://security.debian.org squeeze/updates main contrib non-free
deb http://debian.cs.binghamton.edu/debian-backports squeeze-backports main contrib non-free
deb http://backports.debian.org/debian-backports squeeze-backports main contrib non-free
EOF

chown -R man:root /var/cache/man
ifconfig uap0 down
ifconfig uap0 up 192.168.45.3

/usr/bin/killall lighttpd
/usr/bin/killall apache2
clean
update


##############################
# 2. AUTO REMOVE
##############################
if [ -n "$REMOVE_PACKAGES" ]; then
	remove $REMOVE_PACKAGES
	clean
else
	echo "No packages to remove... [ OK ]"
fi
clean
upgrade
update
##############################
# 3. AUTO INSTALL 
##############################

if [ -n "$INSTALL_PACKAGES" ]; then
	IFS=$' '
	for PACKAGE in $INSTALL_PACKAGES; do
		echo "Installing: $PACKAGE" 
		install $PACKAGE
		echo "[ OK ]"
	done
else
	echo "No packages to install... [ OK ]"
fi

##############################
# 3. AUTO UNROLL 
##############################

if [ -n "$UNROLL_ARCHIVE" ]; then
	if [ -n "$UNROLL_DIR" ]; then
		unroll $UNROLL_ARCHIVE $UNROLL_DIR
	else
		unroll $UNROLL_ARCHIVE
	fi
else
	echo "No archives to install... [ OK ]"
fi

##############################
# 3. CLEAN UP 
##############################
clean

##############################
# 4. DONE INSTALLING!
##############################
echo "Done Installing... [ OK ]"
echo "none" > /sys/class/leds/guruplug\:green\:wmode/trigger
echo "none" > /sys/class/leds/guruplug\:red\:wmode/trigger
echo "none" > /sys/class/leds/guruplug\:green\:health/trigger
				
 
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

