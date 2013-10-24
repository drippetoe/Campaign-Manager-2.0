#!/usr/bin/env python

import os, sys, re, datetime

"""
written by David Shaw, dshaw@proximusmobility.com

A simple python class to take lighttpd log messages piped through stdin,
look up the MAC address through the DHCP lease file, and add the MAC address
to the log entry.  It also handles log rotation through a simple log naming
scheme.
"""
class HttpLogMod:

    def __init__(self,wifi_file=None,log_dir='/home/proximus/logs/working', lease_file='/home/proximus/config/dnsmasq.leases'):
        self.log_dir = log_dir
        self.lease_file = lease_file
        if wifi_file == None:
		now_date = datetime.datetime.now()
		today_format = now_date.strftime("%Y-%m-%d.%H-%M-%S")
                if os.path.isfile('/home/proximus/bin/.macAddr'):
		    file = "%s" % ('/home/proximus/bin/.macAddr')
                    inf = open(file)
                    macAddr = inf.read()
		    macAddr = macAddr.strip()
                    macAddr = macAddr.upper()
                else:
		    macAddr = "UNKNOWN"
		self.wifi_file = "wifi_"+macAddr+"_-1_"+today_format+".txt"
        else:
		self.wifi_file = wifi_file
	print self.wifi_file
    
    def getLogMessageFromStdIn(self):
        data = sys.stdin.read()
        return data.strip()

    def logMessageToFile(self,logMessage='-', type='log'):
        if ( type == 'log' ):
	    if not os.path.exists(self.log_dir):
                os.makedirs(self.log_dir)        
            file = "%s/%s" % ( self.log_dir, self.wifi_file)
            outf = open(file, "a")
            outf.write("%s\n" % logMessage)
            outf.close()
        if ( type == 'error'):
           
           if not os.path.exists('/home/proximus/logs/errors'):
               os.makedirs('/home/proximus/logs/errors')

           file = "%s/%s" % ('/home/proximus/logs/errors', 'error.log')
           outf = open(file, "a")
           outf.write("%s\n" % logMessage)
	   outf.close()

        if ( type == 'debug'):
           if not os.path.exists('/home/proximus/logs/'):
		os.makedirs('/home/proximus/logs/')
	   file = "%s/%s" % ('/home/proximus/logs', 'debugLighttpd.log')
	   outf = open(file, "a")
	   outf.write("%s\n" % logMessage)
	   outf.close()
            
    def getIpAddressFromLogMessage(self, logMessage):
        # [192.168.3.123] [02/Jan/2012:23:15:23 -0500] [GET / HTTP/1.1] [200] [Mozilla]
	splitLine = re.split("[\[\]]", logMessage)
        try:
            return splitLine[1]
        except:
            self.logMessageToFile("Error splitting message " + logMessage, 'error')
            return None
    
    def getMacAddressForIP(self, ipAddress):
        leaseDict = {}
        # 1325825603 b8:8d:12:00:cf:e2 192.168.3.123 macbook 01:b8:8d:12:00:c1:e2
        inf = open(self.lease_file, 'r')
        for line in inf.readlines():
            spl = line.split()
            leaseDict[spl[2]] = spl[1]
        inf.close()
        if ( leaseDict.has_key(ipAddress) ):
            return leaseDict[ipAddress]
        else:
            return '-'
    
    def run(self):
        while 1:
            try: 
	      print "Reading from standard in"      
	      logMessage = sys.stdin.readline()
              #Before running checking the name of the logfile
	      f = open('/home/proximus/config/log.properties', 'r')
	      line = f.readline()
              print "Reading log.properties" 
	      while line:
                  split = line.partition('=')
                  key = split[0].strip()
                  value = split[2].strip()
                  #print("Key-Value Pair: " + key + "--> " + value)
                
	          if key == 'wifi_transfer':
                    print("wifi_file is: " + value)
	            self.wifi_file = value
                  elif key == 'log_dir':
                    self.log_dir = value
 	          elif key == 'lease_file':
	            self.lease_file = value
	          
                  line = f.readline()
              if ( logMessage != '' ):
                self.logMessageToFile(logMessage, "debug")
                logMessage = logMessage.strip()
                ipAddress = self.getIpAddressFromLogMessage(logMessage)
                print str(ipAddress)
                if ( ipAddress != None ):
                    macAddress = self.getMacAddressForIP(ipAddress)
                    newMessage = "%s [%s]" % ( logMessage, macAddress )
                    self.logMessageToFile(newMessage)

	      
            except Exception as err:
                self.logMessageToFile(str(err), "error")
    


if __name__ == "__main__":
    lease_file = None
    log_dir = None
    wifi_file = None

    if len(sys.argv) > 1:
        wifi_file = sys.argv[1]
    if len(sys.argv) > 2:
        log_dir = sys.argv[2]
    if len(sys.argv) > 3:
	lease_file = sys.argv[3]

    if(lease_file != None):
    	print("lease_file is: " + lease_file)
    if(wifi_file != None):
    	print("wifi_file is: " + wifi_file)
    if(log_dir != None):
	print("log_dir is: " + log_dir)
 
    if ( wifi_file != None ) and (log_dir != None) and (lease_file != None):
	logMod = HttpLogMod(wifi_file, log_dir, lease_file)
    elif (wifi_file != None ) and (log_dir != None):
	logMod = HttpLogMod(wifi_file, log_dir)	      
    elif (wifi_file != None ):
        logMod = HttpLogMod(wifi_file)
    else:
        logMod = HttpLogMod()
        
    logMod.run()

