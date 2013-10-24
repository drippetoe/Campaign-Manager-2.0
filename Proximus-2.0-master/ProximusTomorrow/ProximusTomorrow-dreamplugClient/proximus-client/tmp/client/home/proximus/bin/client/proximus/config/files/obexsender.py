CONTENT="""
###############################################################################
# Config file for ObexSender

###############################################################################
# NOTE! The following settings can be only configured from this file!
###############################################################################

###############################################################################
# Configure content files

# File-directives in send/reply are relative to this base directory.

PROXIMUSOBEXBODYHERE

###############################################################################
# Advanced settings

# Use these iWRAPs for sending/inquiry.
#
# Syntax: baseband <ip> <port> <password>

baseband 127.0.0.1 10101
baseband 127.0.0.1 10102
baseband 127.0.0.1 10103

# Don't send to these Bluetooth devices. The default setting "ignore 00:07:80:"
# is recommended. It disables sending files to other Bluegiga Access Servers.
#
# Syntax: ignore <bdaddr-prefix>

# Ignore Bluegiga
ignore 00:07:80:
# Ignore Globalscale
ignore F0:AD:4E:

# Always send to these test devices when found.
#
# Syntax: tester <bdaddr>

#tester 00:07:80:80:00:bf
#tester ac:93:2f:e9:29:b4
#tester c8:df:7c:4d:31:cd

# Scan these directories for incoming remote requests. Special meta, @, means
# Obexserver's root directory.
#
# Syntax: scandir <directory>
#         scandir @

scandir @

# Broadcast already served devices to other ObexSenders (can be unicast or
# broadcast IP address or interface name).
#
# Syntax: broadcast <unicast-ip>
#         broadcast <broadcast-ip>
#         broadcast <interface>

broadcast nap

# UUID where to send files.
#
# Syntax: uuid <uuid<,uuid<,...>>>

uuid OBEXOBJECTPUSH,OBEXFILETRANSFER

# Hash caching
# Last 1000 hashes found can be kept in cache, to avoid re-hashing.
#
# Syntax: hashcache <Yes/No>

hashcache Yes

# Disable hashing completely?
#
# Note that manufacturer/model -based profiling will break if hashing
# is disabled.

disablehashing No

# Connection attempt configuration
# <attempts> value defines how many consequtive attempts to push data is done
# <attemptdelay> defines the delay between consequtive attempts
# <attemptrfcommtimeout> defines the rfcomm timeout for consequtive attempts,
# in the last attempt in the series, normal rfcommtimeout is used.
# After the last attempt there is again a delay of <retrydelay> before
# new attempts batch

attempts 3
attemptdelay 10
attemptrfcommtimeout 7

# RFCOMM timeout value
#
# Syntax: rfcommtimeout <value>

rfcommtimeout 30

# OBEX init timeout

# Timeout in seconds for the first OBEX data packet sent. Some OBEX
# implementations allow the connection to open but wait for user permission
# to actually transfer data.

obexinittimeout 10

###############################################################################
# NOTE! The following settings can be configured from WWW menu, so there's
# no need to edit them here!
###############################################################################

# If this is enabled, ObexSender will reboot Access Server automatically if
# Bluetooth basebands have stopped responding.
#
# Syntax: watchdog <Yes/No>

watchdog Yes

# This setting applies if you're using REPLY-feature of ObexSender and you
# send a file to Access Server to receive specific content. Now, if the file
# you sent doesn't match to ObexSender configuration, the file will be
# deleted if this settings is set to "Yes". Otherwise the file is saved.
# Matching files are always deleted. Disable this if you have some other
# program doing ObjP/FTP.
#
# Syntax: delnomatch <Yes/No>

delnomatch Yes

# Use pairing to inform "I want to receive files".
#
# Enabling this means that user must first manually pair his or hers
# device with Access Server.
#
# Syntax: pair <Yes/No>

pair No

###############################################################################
# Timeouts

# Delay between inquiries (Bluetooth device discoveries) in seconds.
#
# Syntax: inquirydelay <seconds>

inquirydelay 10

# If a file has been successfully sent to a device, this timeout
# (in seconds) defines when content can be sent again to the same device.
#
# Syntax: okdelay <seconds>

# default is 4 hours
okdelay 14400

# If a file transmission to a device has failed or user has declined
# the file, this timeout (in seconds) defines when ObexSender can
# send content to the same device again.
#
# Syntax: faildelay <seconds>

faildelay 14400

# When user doesn't accept or reject the file, ObexSender will try to
# send the file again. This setting determines the timeout (in seconds)
# before resend occurs.
#
# If you wish to disable this feature you can use the same value as in
# OK-delay or FAIL-delay, i.e. the two previous settings.
#
# Syntax: retrydelay <seconds>

retrydelay 120

# When a remote request from user has been received, this setting
# determines how long (in seconds) ObexSender will wait until the
# response file is sent back to the user.
#
# Default value is 5 seconds, because some mobile phones are not
# able to receive files over Bluetooth until at least 5 seconds
# has passed from sending.
#
# Syntax: replydelay <seconds>

replydelay 5

# Determines how often content is pushed to a tester device. Tester
# device is a device that is always offered content, so it isn't
# blocked in any case.
#
# Syntax: testerdelay <seconds>

testerdelay 60

# How long to keep the pairing. Zero means forever.
#
# Syntax: pairexpire <seconds>

pairexpire 0

# The working range of ObexSender can be configured or limited with
# this setting. When ObexSender searches for devices, the RSSI
# (Receiver Signal Strength Indicator) value is also measured.
# This value ranges from -128 to -1.
#
# -128 to -90 means the signal strength is very weak. A connection attempt
# would very likely fail.
#
# -80 to -65 means the signal strength is ok. Connection can be created.
# With Class 2 devices, like most mobile phones, this means the
# phone is 10-20 meters away. A Class 1 device can be even more
# than 100 meters away. Please note that -65 is recommended value for
# Access Server with serial number 0607239999 and smaller.
#
# -45 to -30 means the signal is very strong. The devices are most likely
# very close to each other (less than a meter away). For example testing
# purposes value -45 is ideal because you send only to devices very near
# to Access Server. With the serial numbers of 0607239999 and smaller,
# -35 or -40 can also be suitable.
#
# Syntax: rssi <value>

#increased it a little bit for the dreamplug
rssi -90

# Determines an RSSI limit that allows remote device to be removed from
# all block lists. This can be used in a way that you bring your device
# very close to Access Server and it will be able to receive content
# again, without waiting for OK- or FAIL-delays to pass.
#
# For example, you have received a file succesfully from ObexSender and
# you then have to wait for OK-delay to pass. You have configured
# whitelist RSSI limit to -45 (or -35 if serial number is less than
# 0607239999) and then you bring your device practically attached to
# Access Server. Now you have to wait for an inquiry to pass (blue led
# starts blinking and then stops). Then after a short while you should
# receive content again.
#
# Syntax: whitelistrssi <value>

whitelistrssi 0

###############################################################################
# Logging

# Determines the verbosity level of ObexSender logging. The value can
# be from 0 to 4. If this setting is set to "0", there will be minimal
# logging and with setting "4" there will be maximum amount of logging.
#
# WARNING! Full verbose logging (4) should be used only for debugging
# purposes, since it creates a lot of logs and the flash memory can
# be filled rather quickly. The only verbose level that should be used
# in production is 0 because other verbosity levels generate too much
# log and will eventually fill up Access Server.
#
# Syntax: verbose <level>

verbose 0

# Inquiry logging toggle. Value "No" disables inquiry logging, "Yes" enables
# it. When set to "No" entries containing "Device Found", "Device Blocked",
# "Device Weak" etc. are not logged, decreasing the log file size. However,
# some detailed information is lost if inquiries are not logged.

inquirylog Yes

# Log and summarize each obexsender-put execution. Values "Yes" and "No".
# NOTE: Increases memory consumption and log size. Independent of verbosity
# level.

putlog No

# Log new device hashlogs to a separate file.
#
# Use "-" to disable.
#
# Syntax: hashlog <filename>
#         hashlog -
#
# NOTE: Logging hashes consumes huge amounts of disk space!

hashlog -

# Defines the path and name of the ObexSender log file
# (for example "/usr/local/obexsender/obexsender.log"). Log file contains
# information about successful and unsuccessful transmissions, timestamps
# and information about sent files.
#
# You can also use an IP address of a log server, which must be
# another Access Server running ObexSender.
#
# Type "-" to use syslog.
#
# Syntax: log <filename>        (local file)
#         log @<ip>             (remote logging)
#         log -                 (syslog)

#log /home/proximus/logs/bluetooth-obexsender.log

# Append datestamp (.YYYYMMDD) to local log file name (for example
# /usr/local/obexsender/obexsender.log.20091203).

logsplit No

# Prefix is put in front of every event in the log file.
# Type "-" for none.
#
# Syntax: logprefix <string>

logprefix $S:

# If this is enabled failed transmissions will be logged too.
#
# Syntax: logfail <Yes/No>

logfail Yes

# You can choose to save the information about already served devices,
# so you can form a so-called "block list". If this block list is
# saved in flash memory, it will be preserved even if Access Server is
# rebooted. This basically ensures that remote devices don't receive
# the same content even if Access Server is rebooted.
#
# Syntax: dumpfile <filename>

dumpfile /home/proximus/config/blocklist.dump

# Determines how often (in seconds) a dump file is updated. Using dump file
# allows blocklist to be saved in case of power failure of Access Server.
# "0" disables this feature. We recommend to use a rather big value, for
# example 15min = 900s.
#
# WARNING: Using a small value here can physically burn the flash memory
# over time.
#
# Syntax: dumpdelay <seconds>

dumpdelay 300

########################################################################### EOF

"""