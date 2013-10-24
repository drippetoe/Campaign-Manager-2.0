#!/usr/bin/env python
from ObexSenderConfig import ObexSenderConfig
from ObexSenderBlocklist import ObexSenderBlocklist
from ObexSenderLogger import ObexSenderLogger
from ObexSenderHashCache import ObexSenderHashCache
from ObexSenderBluetoothDevice import ObexSenderBluetoothDevice
from lightblue.obex import OBEXClient
from bluez.Adapter import Adapter
from threading import Thread
from Queue import Queue
import bluetooth
import lightblue
from lightblue import _lightbluecommon, _obexcommon, obex
import os
import bluez
import gobject
import dbus.mainloop.glib
from multiprocessing.synchronize import Lock
import time
from lightblue._obexcommon import OBEXError





class ObexSender(Thread):
    
    
    def __init__(self, config, blocklist, adapter, agent, logger, lock, queue=None, hash_cache=None):
        Thread.__init__(self)
        #print "#########################################"     
        #print "# Starting ObexSender"
        if not isinstance(config, ObexSenderConfig):
            raise TypeError("Invalid config")
        if not isinstance(blocklist, ObexSenderBlocklist):
            raise TypeError("Invalid blocklist")
        if not isinstance(adapter, Adapter):
            raise TypeError("Invalid adapter")
        if not isinstance(logger, ObexSenderLogger):
            raise TypeError("Invalid logger")
        ObexSenderHashCache
        
        self.__config = config
        self.__blocklist = blocklist
        self.__adapter = adapter   
        self.__agent = agent      
        self.__logger = logger
        self.__queue = queue
        self.__lock = lock
        
        ignore = self.__config.get_values("ignore")
        if not ignore == None:
            self.__ignore = ignore
        else:
            self.__ignore = []
            
        tester = self.__config.get_values("tester")
        if not tester == None:
            self.__tester = tester
        else:
            self.__tester = []
            
        files = self.__config.get_send_files()
        if not files == None:
            self.__files = files
        else:
            self.__files = []
            
        inquirydelay = self.__config.get_values("inquirydelay")
        if(inquirydelay == None):
            inquirydelay = 10
        else:
            inquirydelay = int(inquirydelay[0])
        self.__inquirydelay = inquirydelay
        
        whitelistrssi = self.__config.get_values("whitelistrssi")
        if not whitelistrssi == None:
            self.__whitelistrssi = int(whitelistrssi[0])
        else:
            self.__whitelistrssi = 0
            
        rssi = self.__config.get_values("rssi")
        if not rssi == None:
            self.__rssi = int(rssi[0])
        else:
            self.__rssi = -80
        
        testerdelay = self.__config.get_values("testerdelay")
        if not testerdelay == None:
            self.__testerdelay = int(testerdelay[0])
        else:
            self.__testerdelay = 60
        
        ##
        # Enable/Disable - Caching 1000 last seen devices
        #
        if(hash_cache==None):
            hash_cache = os.getcwd()+"/devices.cache"
        self.__cache = None
        hashcache = self.__config.get_values("hashcache")
        if hashcache == None:
            hashcache = self.__config.get_values("disablehashing ")
            if not hashcache == None:
                hashcache = hashcache[0]
                if hashcache.lower() == "no":
                    self.__cache = ObexSenderHashCache(filename=hash_cache)
        else:
            hashcache = hashcache[0]            
            if hashcache.lower() == "yes":
                self.__cache = ObexSenderHashCache(filename=hash_cache)    
        
    def __log_retry(self, address, offset=0):
        nr_of_files = len(self.__files)
        it = offset
        for i in self.__files:
            for k in i.keys():
                it += 1
                if(it == nr_of_files):
                    self.__logger.sent(filename=k, address=address, error_code=ObexSenderLogger.ERROR_RETRY_3)
                else:
                    self.__logger.sent(filename=k, address=address, error_code=ObexSenderLogger.ERROR_RETRY_8)
                    
    def __log_no_oop(self, address):
        for i in self.__files:
            for k in i.keys():
                self.__logger.sent(filename=k, address=address, error_code=ObexSenderLogger.ERROR_NO_OOP)
                
        
    ##
    ## TODO Separate into thread
    ##    
    def __send_files(self, service):
        address = service["host"]
        port = service["port"]
        #device.HandleSignal(handler, signal)
        #print device
        
        try:
            client = OBEXClient(service["host"], service["port"])
            
            
            attempts = self.__config.get_values('attempts')
            if attempts == None:
                attempts = 1
            else:
                attempts = int(attempts[0])                    
            
            attemptdelay = self.__config.get_values('attemptdelay')
            if attemptdelay == None:
                attemptdelay = 10
            else:
                attemptdelay = int(attemptdelay[0])      
            
            # Unsupported attemptrfcommtimeout
              
            connected = False
            for i in range(1, attempts + 1):
                try:
                    #print "# * Connecting(%d/%d) to %s port:%s" % (i, attempts, address, port)                        
                    cresp = client.connect()
                    if cresp.code == lightblue.obex.OK:
                        connected = True
                        break
                    else:
                        device = self.__adapter.CreatePairedDevice(address, self.__agent, 'NoInputNoOutput', reply_handler=None, error_handler=None)
                        continue                    
                except Exception as error:
                    self.__log_retry(address)
                    time.sleep(attemptdelay)                                    
                    continue
                
            if not connected:
                self.__blocklist.update(address, status="FAIL")
                return False
            else:
                nr_of_files = len(self.__files)
                it = 0
                if(len(self.__files) > 0):
                    for i in self.__files:
                        for k in i.keys():
                            try:
                                try:
                                    send_file = file(i.get(k), "rb")
                                except Exception as ioerror:
                                    self.__logger.sent(filename=k, address=address, error_code=ObexSenderLogger.ERROR_FILE_NOT_FOUND)
                                    continue                                
                                client.put({'name':k}, send_file)
                                it += 1
                                if(it == nr_of_files):
                                    self.__logger.sent(filename=k, address=address, error_code=ObexSenderLogger.ERROR_FILE_SENT)
                                else:
                                    self.__logger.sent(filename=k, address=address, error_code=ObexSenderLogger.ERROR_MORE)
                            except Exception as error:
                                self.__log_retry(address,offset=it)
                                self.__blocklist.update(service["host"], status="FAIL")
                                return False 
                    return True
                else:
                    self.__logger.sent(filename=k, address=address, error_code=ObexSenderLogger.ERROR_CONNECTED)
                    return True
        
        except OBEXError as error:
            print (error)
            self.__blocklist.update(address, status="FAIL")
            return False
        
    
    def __in_ignore(self, address):    
        for ignore in self.__ignore:
            if(address.startswith(ignore)):
                return True
        return False
    def __in_tester(self, address):
        for tester in self.__tester:
            if(address.startswith(tester)):
                return True
        return False
        
    
    def __supports_push(self, address):
        ##
        # first look in cache
        ##
        if not self.__cache == None:
            device = self.__cache.get(address)
            if not device == None:
                if(device.supports_obex() == True):
                    self.__logger.debug("# * Found in cache")
                    return device.get_service()    
        ##
        # Do a double search with different libraries to ensure support
        ##
        # Nokia "OBEX Object Push"
        # Android "Object Push"
        ##
        all_services = bluetooth.find_service(address=address)
        if not all_services == None:
            for services in all_services:
                if not services["name"] == None:
                    #print "%s %s" % (services["name"], services["port"])
                    if(services["name"].find("Object Push")>=0):
                        return {"host": str(address), "port":services["port"]}
        services = lightblue.findservices(addr=address, name=None, servicetype=_lightbluecommon.OBEX)
        if(services != None):
            for service in services:
                if not service[2] == None:
                    if(services[2].find("Object Push")>=0):
                        return {"host": str(address), "port":service[1]}
        return None
        
        
    def run (self):
        while True:
            try:
                self.__lock.acquire()
                #self.__logger.debug("# * acquired")
                while len(self.__queue) > 0:
                     
                        address, properties = self.__queue.popitem()
                        rssi = properties["RSSI"]
                                  
                        if not self.__in_ignore(address):
                            if self.__in_tester(address):
                                #print "# Tester: %s" % address
                                self.__logger.debug("# Tester: %s" % address)
                                if self.__blocklist.expired(address):
                                    obex_service = self.__supports_push(address)    
                                    self.__logger.found(address, rssi)                    
                                    if not obex_service == None:                                
                                        self.__cache.put(ObexSenderBluetoothDevice(address, obex_service))
                                        self.__logger.hash(address)
                                        if(self.__send_files(obex_service)==True):
                                            self.__blocklist.update(address, status="TESTER") 
                                    else:
                                        self.__cache.put(ObexSenderBluetoothDevice(address))
                                        
                                        self.__logger.sent(filename="-",address=address,error_code=ObexSenderLogger.ERROR_NO_OOP)
                                else:                            
                                    self.__logger.blocked(address, rssi, self.__blocklist.timeout(address))
                            else:
                                if (rssi >= self.__whitelistrssi):
                                    #print "# Removing from blocklist"
                                    self.__blocklist.remove(address)
                                if(rssi < self.__rssi):
                                    self.__logger.weak(address, rssi)
                                else:                                                    
                                    if self.__blocklist.expired(address):    
                                        self.__logger.found(address, rssi)                    
                                        obex_service = self.__supports_push(address)                        
                                        if not obex_service == None:
                                            self.__cache.put(ObexSenderBluetoothDevice(address, obex_service))
                                            self.__logger.hash(address)
                                            if(self.__send_files(obex_service)==True):
                                                self.__blocklist.update(address, status="OK") 
                                        else:
                                            self.__logger.sent(filename="-",address=address,error_code=ObexSenderLogger.ERROR_NO_OOP)
                                            self.__cache.put(ObexSenderBluetoothDevice(address))
                                            self.__blocklist.update(address, status="FAIL") 
                                            self.__logger.blocked(address, rssi, self.__blocklist.timeout(address))
                                    else:                            
                                        self.__logger.blocked(address, rssi, self.__blocklist.timeout(address))
                            
                        else:                    
                            self.__logger.blocked(address, rssi)
                self.__lock.release()
                #print "# * released"
                #self.__logger.debug("# * released")
                time.sleep(1)
            except Exception as error:
                self.__logger.debug(error)
            
                     
            

#                            #http://padovan.org/pub/bluetooth/bluetooth-security-article.pdf
#                            #adapter.CreatePairedDevice(address, agent, 'DisplayOnly', reply_handler=pairing_success, error_handler=pairing_error)
#                            #adapter.CreatePairedDevice(device, agent, 'NoInputNoOutput', reply_handler=pairing_success, error_handler=pairing_error)


#def enqueue_device(address, properties):
#    if not queue.has_key(address):
#        queue[address] = properties
#        print "# Queued %s Name: %s" % (address,properties["Name"])
#
#    
#queueLock = Lock()
#queue = {}
#
#
#    
#    
#    
#def MePropertyChanged(name, value):
##    print "- property changed"
##    print " - name: %s" % name
##    print " - value: %s" % value
#    if(name == "Discovering"):
#        if(value == 1):
#            #print "# - queue sleep 10"
#            time.sleep(10)
#            queueLock.acquire()                        
#            try:
#                adapter.StopDiscovery()
#            except:
#                pass
#                #print "# next should be released"
#        else:            
#            queueLock.release()            
#            #print "# - released"
#            adapter.StartDiscovery()
#
#
#
#def DeviceDisappeared(address):
#    #print "DeviceDisappeared %s" % address
#    pass
#
#    
#    
#if __name__ == '__main__':
#    
#
#
#    
#    try: 
#        dbus.mainloop.glib.DBusGMainLoop(set_as_default=True)
#        mainloop = gobject.MainLoop()      
#        manager = bluez.Manager('gobject')    
#        adapter = manager.DefaultAdapter()
#        path = "/com/proximus/agent"
#        agent = manager.CreateAgent(ProximusAgent, obj_path=path)  
#        adapter.HandleSignal(MePropertyChanged, 'PropertyChanged')
#        adapter.HandleSignal(enqueue_device, 'DeviceFound')
#        adapter.HandleSignal(DeviceDisappeared, 'DeviceDisappeared')
#        adapter.StartDiscovery()  
#        config = ObexConfig("./config.conf")  
#        bl = Blocklist(config=config)
#        logger = ObexSenderLogger()
#        o = ObexSender(config=config, blocklist=bl, adapter=adapter, logger=logger, queue=queue)
#        o.start()       
#        mainloop.run()    
#        print "Will never end until mainloop.quit()"
#        exit(0)
#    except Exception as error:
#        print "Error %s" % error
#        mainloop.quit()
#        exit(1)

























