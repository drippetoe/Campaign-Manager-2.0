#!/usr/bin/env python


import time

class ObexSenderBluetoothDevice(object):    
    
    def __init__(self, address, obex_service=None):
        self.__timestamp = time.time()
        self.__address = address
        self.__obex_support = False
        if not obex_service == None:
            self.__obex_support = True            
            self.__obex_service = obex_service
            
    def supports_obex(self):
        return self.__obex_support
    
    def get_address(self):
        return self.__address
    
    def get_service(self):
        return self.__obex_service
    
    def accessed(self):
        self.__timestamp = time.time()
        
    def get_accessed(self):
        return self.__timestamp
    
    def __lt__(self, obj):
        return self.__timestamp < obj.get_accessed()    
    
    def __gt__(self, obj):
        return self.__timestamp < obj.get_accessed()
    
    def __ge__(self, obj):
        return self.__timestamp >= obj.get_accessed()
    
    def __le__(self, obj):
        return self.__timestamp <= obj.get_accessed()