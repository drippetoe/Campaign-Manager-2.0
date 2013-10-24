#!/usr/bin/env python
import os
import sys
import time
import datetime
import logging
import logging.handlers
from ObexSenderConfig import ObexSenderConfig



class ObexSenderLogger():

	#20120530/175639 1211100100: Device blocked: c4:17:fe:f6:b2:d1 (RSSI -68, 6650s)
	ERROR_FILE_SENT = "OK/0"
	ERROR_CONNECTED = "OK/5"
	ERROR_MORE = "MORE/0"
	ERROR_RETRY_3 = "RETRY/3"
	ERROR_RETRY_8 = "RETRY/8"
	ERROR_REJECTED = "FAIL/4"
	ERROR_FILE_NOT_FOUND = "FAIL/6"
	ERROR_NO_OOP = "FAIL/7"
	
	LOG_FILENAME = '/tmp/obexsender_debug.log'

	def __init__(self, config):
		if not isinstance(config, ObexSenderConfig):
			raise TypeError("Invalid config")
		self.__config = config
		
		log = self.__config.get_values("log")
		if not log == None:
			log = str(log[0])
			if(log == "-"):
				self.__log = os.getcwd() + "/obexsender.log"
			elif(log.startswith("@")):
				self.__log = os.getcwd() + "/obexsender.log"
			else:
				self.__log = log
		else:
			self.__log = os.getcwd() + "/obexsender.log"



		#bluetoothTransfer.log
		self.__logger = logging.getLogger("obexsender")
		self.__logger.setLevel(logging.DEBUG)
		watched_file_handler = logging.handlers.WatchedFileHandler(filename=self.__log, encoding='utf-8')
		self.__logger.addHandler(watched_file_handler)
		
		
		#Debugging
		
		self.__debug_logger = logging.getLogger('debug.obexsender')
		debugHandler = logging.StreamHandler(sys.stdout)
		self.__debug_logger.addHandler(debugHandler)
		self.__debug_logger.setLevel(logging.DEBUG)	
		
#		handler = logging.handlers.RotatingFileHandler(self.LOG_FILENAME, maxBytes=1024 * 1000, backupCount=2)
#		self.__debug_logger.addHandler(handler)
		
		
	def debug(self, msg):
		self.__debug_logger.debug(msg)

	def __timestamp(self):
		#datetime.datetime.utcnow().
		return "" + time.strftime("%y%m%d/%H%M%S", time.gmtime(time.time()))
	
	def log(self, msg):
		self.__logger.info(msg)
	
	def __write(self, action, address, param):	
		msg = "%s: %s %s %s" % (self.__timestamp(), action, address, param)
		self.debug(msg)
		self.log(msg)
	
	def found(self, address, rssi):
		action = "Device found:\t"
		param = "(RSSI " + str(rssi) + ")"	
		self.__write(action, address, param)
	
	def blocked(self, address, rssi, blocked_s="0"):
		action = "Device blocked:\t"
		param = "(RSSI " + str(rssi) + ", " + str(blocked_s) + "s)"	
		self.__write(action, address, param)
		
	def weak(self, address, rssi):
		action = "Device too weak:\t"
		param = "(RSSI " + str(rssi) + ")"	
		self.__write(action, address, param)
		
	def hash(self, hashed_device):
		self.__write("Hash:", hashed_device, "")
		
	def sent(self, filename, address, error_code):
		action = "Sent " + filename + " to"
		param = error_code
		self.__write(action, address, param)


#logger = ObexSenderLogger()
#
#logger.found("00:21:08:d4:a7:45", -86)	
#logger.blocked("00:05:4f:98:85:31", -73, 12392)	
#logger.weak("address", 12)
#logger.hash("""Nokia_E71,retry:40-42-464,retry:118-125-465,@DI@,,,,""@BB@83dc1a97fd1538b3@BN@72610b17b8185105@BD@00:21:fe:c9:8d:26@BP@10814816@FN@"Jarno"@""")
#
#
#logger.sent("picture.jpg", "00:24:7e:27:58:46", ObexSenderLogger.ERROR_RETRY)
