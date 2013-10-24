#!/usr/bin/env python
import os


class ObexSenderConfig():
    def __init__(self, filepath):
        self.__configuration = {}
        if(filepath==None):
            self.__filepath = os.getcwd()+"/obexsender.conf"
        else:
            self.__filepath = filepath
        self.read()    
    
    def read(self):
        try:
            configFile = open(self.__filepath, "r")
        except Exception as error:
            raise Exception("Unable to open config file: %s", self.__filepath)
        buffer = []
        endBracketFound = True
        for line in configFile.readlines():
            line = line.strip()
            try:
                if(line.startswith('#') or line.startswith('\n') or line.startswith('\r')):
                    continue
                elif(line.startswith('send') and endBracketFound == True):
                    buffer = []
                    endBracketFound = False
                    params = line.split()
                    for param in params:
                        if not param.startswith('send') and not param.startswith('{') and len(param) > 0:
                            buffer.append(param)
                elif "{" in line and endBracketFound == False:
                    params = line.split()
                elif "}" in line and endBracketFound == False:
                    endBracketFound = True
                    params = line.split()
                    for param in params:
                        if not param.startswith('send') and not param.startswith('{') and not param.startswith('}') and not line.startswith('reply'):
                            buffer.append(param)
                    self.__configuration['send'] = buffer
                    buffer = []      
                elif endBracketFound == False:    
                    params = line.split()
                    for param in params:
                        if not param.startswith('send') and not param.startswith('{') and not param.startswith('}'):
                            buffer.append(param)
                else:
                    params = line.split()
                    if(len(params) > 0):
                        if(self.__configuration.has_key(params[0])):
                            for i in range(1, len(params)):
                                self.__configuration[params[0]].append(params[i] )
                        else:
                            self.__configuration[params[0]] = [params[i] for i in range(1, len(params))]
            except Exception as error:
                continue
            
            
    def get_values(self, key):            
        if(self.__configuration == None or len(self.__configuration) == 0):
            self.read()            
        if(self.__configuration.has_key(key) and len(self.__configuration.get(key)) > 0):
            return self.__configuration.get(key)
        else:
            return None
        
    def get_send_files(self):
        if(self.__configuration == None or len(self.__configuration) == 0):
            self.read()            
        if(self.__configuration.has_key("send") and self.__configuration.has_key("basedir")):
            sendConfig = self.__configuration.get("send")
            basedir = self.__configuration.get("basedir")
            if(len(basedir) > 0):
                basedir = basedir[0]
            else:
                return None
            files = []
            for i in range(0, len(sendConfig)):
                if(sendConfig[i] == "file"):
                    files.append({sendConfig[i + 1]:basedir + sendConfig[i + 1]})
            return files
        else:
            return None
            
#            
#            
#
##print config['basedir'][0]+""+
##dumpfile /home/proximus/config/blocklist.dump
#print "Logging to :"+config['log'][0]
#print config['send']
#print config['dumpfile']
#print config
#
#sendConfig = config['send']
#for i in range(0,len(sendConfig)):
#    if(sendConfig[i]=="file"):        
#        print "Send: "+config['basedir'][0]+sendConfig[i+1]
#                
#
#
#    
#t = {'bluegiga1.gif': 'matchbluegiga1.gif'}
#
#oc = ObexConfig("./config.conf")
#for i in oc.get_send_files():
#    for k in i.keys():
#        print("name: %s %s" % (k, i.get(k)))
