#!/usr/bin/env python

#from ObexSenderBluetoothDevice import ObexSenderBluetoothDevice
try:
    import cPickle as pickle
except:
    import pickle
   
class ObexSenderHashCache(object):
    
    def __init__(self, filename, max_size=1000, clear_cache=False):
        self.__max_size = max_size
        self.__filename = filename
        self.__cache = {}        
        if(clear_cache == True):
            self.clear_cache()
        else:
            try:
                self.__load()                
            except:
                pass
        print "Cache(%s) size: %i" % (filename,len(self.__cache))
        
        
    def __load(self):
        pfile = open(self.__filename, "rb")
        self.__cache = pickle.load(pfile)
        pfile.close()
        
    def __write(self):
        pfile = open(self.__filename, "wb")
        pickle.dump(obj=self.__cache, file=pfile, protocol=pickle.HIGHEST_PROTOCOL)
        pfile.close()
        
    def __delete_last(self):        
        res = list(sorted(self.__cache, key=self.__cache.__getitem__, reverse=True))
        key = res.pop()
        del self.__cache[key] 
                
    def print_sorted(self):
        res = list(sorted(self.__cache, key=self.__cache.__getitem__, reverse=True))
        for val in res:
            print ("%s: - %s" % (val, self.__cache[val].get_accessed()))
    
    def get(self, address):
        if self.__cache.has_key(address) == True:
            device = self.__cache[address]
            device.accessed()
            self.__cache[address] = device
            self.__write()
            return device
        else:
            return None
        
    def put(self, device):
        address = device.get_address()
        if(device.supports_obex() == True):
            self.__cache[address] = device
        else:
            old_device = self.get(address)
            if old_device == None:
                self.__cache[address] = device
        if(len(self.__cache) > self.__max_size):
            self.__delete_last()
        self.__write()
    
    def clear_cache(self):
        self.__cache = {}
        self.__write()







