import requests
import logging
from proximus.tools.HttpStatus import HttpStatus

logger = logging.root

class RESTClient:
    
    timeout=30
    CHUNK_SIZE=10240

    def GETRequest(self, uri, headers=None):
        try:
            response = requests.get(uri, headers=headers, timeout=RESTClient.timeout)
            return response
        except requests.exceptions.ConnectionError as err:
            logger.error("GET: " + str(err.message))
            return None
        except requests.exceptions.SSLError:
            logger.error("GET: " + str(err.message))
            return None
        
    def POSTRequest(self, uri, content, headers=None):
        logger.debug(uri)
        try:
            response = requests.post(uri, data=content, headers=headers, timeout=RESTClient.timeout)
            return response
        except requests.exceptions.ConnectionError as err:
            logger.error("POST: " + str(err.message))
            return None
        except requests.exceptions.SSLError:
            logger.error("POST: " + str(err.message))
            return None
    
    def POSTFiles(self, uri, files={}, data={}, headers=None):
        logger.debug(uri)
        try:
            response = requests.post(uri, data=data, files=files, headers=headers, timeout=RESTClient.timeout)
            return response
        except requests.exceptions.ConnectionError as err:
            logger.error(err)
            return None
        except requests.exceptions.SSLError:
            logger.error("POST: " + str(err.message))
            return None
    
    def PUTRequest(self, uri, content, headers=None):
        logger.debug(uri, content)
        try:
            response = requests.put(uri, data=content, headers=headers, timeout=RESTClient.timeout)
            return response
        except requests.exceptions.ConnectionError as err:
            logger.error(err)
            return None
        except requests.exceptions.SSLError:
            logger.error("PUT: " + str(err.message))
            return None
    
    def DELETERequest(self, uri, content, headers=None):
        logger.debug(uri)
        try:
            response = requests.delete(uri, data=content, headers=headers, timeout=RESTClient.timeout)
            return response
        except requests.exceptions.ConnectionError as err:
            logger.error(err)
            return None
        except requests.exceptions.SSLError:
            logger.error("DELETE: " + str(err.message))
            return None
        
    """Using new GET that doesn't keep anything in memory"""
    def GETFile(self, uri, savePath, timeout=240):
        try:
            logger.debug(uri)
            response = requests.get(uri, timeout=timeout)
            
            if ( response.status_code == HttpStatus.SC_OK):
                with open(savePath, "w") as outf:
                    for chunk in response.iter_content(chunk_size=RESTClient.CHUNK_SIZE):
                        outf.write(chunk)
                return True
            else:
                logger.error("Status code was %d" % response.status_code)
                return False
        except requests.exceptions.ConnectionError as err:
            logger.error(err)
            return False
        except requests.exceptions.SSLError:
            logger.error("FILEGET: " + str(err.message))
            return False
            
    def GETFileOLD(self, uri, savePath, timeout=120):
        try:
            logger.debug(uri)
            response = requests.get(uri, timeout=timeout)
            
            if ( response.status_code == HttpStatus.SC_OK):
                outf = open(savePath, "w")
                outf.write(response.content)
                outf.close()
                return True
            else:
                logger.error("Status code was %d" % response.status_code)
                return False
            
        except requests.exceptions.ConnectionError as err:
            logger.error(err)
            return False
        except requests.exceptions.SSLError:
            logger.error("FILEGET: " + str(err.message))
            return False
