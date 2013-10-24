#!/usr/bin/env python
'''
Created on Jun 26, 2012

@author: eric
'''
import sys, traceback
import time
import dbus.mainloop.glib
import gobject
import bluez

from multiprocessing.synchronize import Lock

from ObexSender.ObexSenderConfig import ObexSenderConfig
from ObexSender.ObexSenderBlocklist import ObexSenderBlocklist
from ObexSender.ObexSenderLogger import ObexSenderLogger
from ObexSender.ObexSender import ObexSender
import ProximusAgent
from ProximusAgent import ProximusAgent
import getopt
#from encodings.punycode import adapt

    

def DeviceFound(address, properties):
    if not queue.has_key(address):
        queue[address] = properties
        #print "# Queued %s Name: %s" % (address, properties["Name"])


manager = bluez.Manager('gobject')    
adapter = manager.DefaultAdapter()     
queueLock = Lock()
queue = {}
logger = None

    
def MePropertyChanged(name, value):
    global adapter
    global logger
    if(logger != None):
        logger.debug("# - property changed (%s: %s)" % (name, value))
    if(name == "Discovering"):
        if(value == 1):
            if(logger != None):
                logger.debug("# - queue sleep 10")
            time.sleep(10)
            queueLock.acquire()                        
            try:
                adapter.StopDiscovery()
            except:
                if(logger != None):
                    logger.debug("# next should be released")
        else:            
            queueLock.release()       
            if(logger != None):
                logger.debug("# - released")
            #TIME BETWEEN SCANS
            time.sleep(30)
            adapter.StartDiscovery()



def DeviceDisappeared(address):
    #print "DeviceDisappeared %s" % address
    pass


def start(config_file, hash_cache, flushblock):
    global logger
    path = "/com/proximus/agent"
    try: 
        dbus.mainloop.glib.DBusGMainLoop(set_as_default=True)
        mainloop = gobject.MainLoop()     
               
        agent = manager.CreateAgent(ProximusAgent, obj_path=path)  
        adapter.HandleSignal(MePropertyChanged, 'PropertyChanged')
        adapter.HandleSignal(DeviceFound, 'DeviceFound')
        adapter.HandleSignal(DeviceDisappeared, 'DeviceDisappeared')
        adapter.StartDiscovery()  
        config = ObexSenderConfig(config_file)  
        bl = ObexSenderBlocklist(config=config, clear_dumpfile=flushblock)
        logger = ObexSenderLogger(config=config)
        o = ObexSender(config=config, blocklist=bl, adapter=adapter, agent=agent, logger=logger, lock=queueLock, queue=queue, hash_cache=hash_cache)
        logger.debug("Starting")
        o.start()       
        mainloop.run()    
        print "Will never end until mainloop.quit()"
        exit(0)
    except Exception as error:
        print "Error: %s" % error
        print __HELP__
        traceback.print_exc(file=sys.stdout)
        mainloop.quit()
        exit(1)

__HELP__ = """
ObexSender 1.0 Eric Johansson

Usage: obexsender [OPTION]
        -h --help           Prints help, this text.        
        -c --config [FILE]  File containing obexsender configuration.
                             - default: ./obexsender.conf                            
        --cache [FILE]      Use cache file        
        --flushblock        Flush the blocklist on startup"""
def main(argv):
    global HELP
    config = None
    hash_cache = None
    flushblock = False
    try:
        options, remainder = getopt.getopt(argv, 'hc:', ['config=', 'cache=', 'flushblock'])       
        for opt, arg in options:
            if opt in ('-h', '--help'):
                print "%s %s" % (opt, arg)
                print __HELP__
                sys.exit(2)
            elif opt in ('-c', '--config'):
                config = arg 
            elif opt in ('--cache'):
                hash_cache = arg 
            elif opt in ('--flushblock'):
                flushblock = True
    except getopt.GetoptError:
        print "Invalid argument!"
        print __HELP__
        sys.exit(2)
        
    start(config, hash_cache, flushblock)    
    

        
    
if __name__ == '__main__':
    exit(main(sys.argv[1:]))
    #main(['--flushblock'])




