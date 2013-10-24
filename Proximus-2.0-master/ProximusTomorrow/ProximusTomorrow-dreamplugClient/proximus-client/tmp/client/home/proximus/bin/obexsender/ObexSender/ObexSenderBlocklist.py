#!/usr/bin/env python

#dumpfile /home/proximus/config/blocklist.dump
import datetime
import time
import os
from string import capitalize
from ObexSenderConfig import ObexSenderConfig


        
    
        
    
#format
#00:24:2c:b6:7a:49    1341252266
#00:1c:d4:8c:33:16    1341266423
#40:2c:f4:51:dc:0a    1341266384
#c4:46:19:e9:a5:7b    1341266376
#70:f1:a1:09:b8:a2    1341266372

#blocklist = None
#okdelay = 36

raw = """00:24:2c:b6:7a:49    1341252266
00:1c:d4:8c:33:16    1341266423
40:2c:f4:51:dc:0a    1341266384
c4:46:19:e9:a5:7b    1341266376
70:f1:a1:09:b8:a2    1341266372
"""
class ObexSenderBlocklist:
    
    def __init__(self, config, write_on_update=True, remove_expired=False, clear_dumpfile=False):
        self.__write_on_update = write_on_update
        self.__remove_expired = remove_expired
        self.__blocklist = {}        
        if not isinstance(config, ObexSenderConfig):
            raise TypeError("Invalid config")
        self.__config = config
        okdelay = self.__config.get_values("okdelay")
        dumpfile = self.__config.get_values("dumpfile")
        faildelay = self.__config.get_values("faildelay")
        retrydelay =  self.__config.get_values("retrydelay")
        testerdelay = self.__config.get_values("testerdelay")
        
        if not okdelay == None:
            self.__okdelay = int(okdelay[0])
        else:
            self.__okdelay = 36000  
        if not faildelay == None:
            self.__faildelay = int(faildelay[0])
        else:
            self.__faildelay = 86400
        if not retrydelay == None:
            self.__retrydelay = int(retrydelay[0])
        else:
            self.__retrydelay = 120    
                
        if not testerdelay == None:
            self.__testerdelay = int(testerdelay[0])
        else:
            self.__testerdelay = 60

                      
        if not dumpfile == None:
            self.__dumpfile = dumpfile[0]
        else:
            self.__dumpfile = "/var/lib/obexsender/blocklist.dump"     
        if clear_dumpfile==True:
            self.__write()   
        self.__read()
    
    def __expired(self, now, compare):  
        delta = compare-now
        #print "Delta: %s" % delta 
        if(delta<datetime.timedelta(0)):
            return True
        return False       
        
    def __read(self):
        if self.__blocklist == None:
            self.__blocklist = {}
        try:
            dump_file = open(self.__dumpfile, "r")       
            for line in dump_file.readlines():
                lsplit = line.split()
                self.__blocklist[lsplit[0]] = float(lsplit[1])
#            now = datetime.datetime.utcnow()
#            self.__blocklist['aa:bb:cc:dd:ee:ff'] = time.mktime(now.timetuple())
        except Exception:
            #print "Unable to read config file: %s", self.__dumpfile
            self.__blocklist = {}
            self.__write()  
    
    def __write(self):
        try:
            outfile = open(self.__dumpfile, "w")
            now = datetime.datetime.utcnow()
            for address in self.__blocklist:
                timestamp = datetime.datetime.fromtimestamp((self.__blocklist[address]))
                if(self.__remove_expired):    
                    if not self.__expired(now, timestamp):
                        outfile.write("%s    %i\n" % (address, self.__blocklist[address]))
                else:
                    outfile.write("%s    %i\n" % (address, self.__blocklist[address]))
            outfile.close()
        except Exception as error:
            print error
    
    def __get_timestamp(self,address):
        if self.__blocklist.has_key(address):
            return datetime.datetime.fromtimestamp(self.__blocklist[address])
        return datetime.datetime.utcnow()
            
    def update(self, address, status="OK"):
        if self.__blocklist == None:
            self.__blocklist = {}
        
        if(status=="OK"):
            timepadding = datetime.timedelta(seconds=self.__okdelay)
        elif(status=="FAIL"):
            timepadding = datetime.timedelta(seconds=self.__faildelay)
        elif(status=="RETRY"):
            timepadding = datetime.timedelta(seconds=self.__retrydelay)
        elif(status=="TESTER"):
            timepadding = datetime.timedelta(seconds=self.__testerdelay)
        else:
            raise ValueError("Invalid status %s",status)
        now = datetime.datetime.utcnow()+timepadding
        self.__blocklist[address] = time.mktime(now.timetuple())
        if(self.__write_on_update):
            self.__write()        
    
    def expired(self, address):
        if not self.__blocklist.has_key(address):
            return True
        else:
            now = datetime.datetime.utcnow()
            timestamp = self.__get_timestamp(address)
            return self.__expired(now, timestamp)
    def __total_seconds(self, timed):
        return ((timed.microseconds + (timed.seconds + timed.days * 24 * 3600) * 10**6) // 10**6)
    
    def timeout(self, address):
        now = datetime.datetime.utcnow()
        timestamp = self.__get_timestamp(address)
        delta = timestamp-now
        return int(self.__total_seconds(delta))        
        
    def remove(self, address):
        if self.__blocklist.has_key(address):
            del self.__blocklist[address]
            self.__write()
        
             
    def debug(self):
        """Debug method prints all values"""
        print "##########################################"
        print "BlockList (%i):" % len(self.__blocklist)
        for key in self.__dict__.keys():
            
            var = key.replace("_Blocklist__", "")
            if not var.startswith("blocklist") and not var.startswith("config"):
                var = capitalize(var)
                var = var.replace("_", " ")
                print "- %s: %s" % (var, self.__dict__[key])        
        now = datetime.datetime.utcnow()
        print "- Not expired:"
        for address in self.__blocklist:
            timestamp = datetime.datetime.fromtimestamp(self.__blocklist[address])    
            if not self.__expired(now, timestamp):
                delta = str(timestamp-now)
                print("\t%s\t%i\tExpires in: %s" % (address, self.__blocklist[address], delta))
        print "- Expired:"
        for address in self.__blocklist:
            timestamp = datetime.datetime.fromtimestamp((self.__blocklist[address]))    
            if self.__expired(now, timestamp):
                print("\t%s\t%i" % (address, self.__blocklist[address]))
        print "##########################################"

##########################################
## Test
##########################################
#oc = ObexConfig("./config.conf")
#b = Blocklist(oc)
#b.update("aa:bb:cc:dd:ee:ff")
#b.debug()
#
##b.update("11:22:33:44:55:66",status="RETRY")
##b.update("11:22:33:44:55:66",status="FAIL")
#b.debug()


