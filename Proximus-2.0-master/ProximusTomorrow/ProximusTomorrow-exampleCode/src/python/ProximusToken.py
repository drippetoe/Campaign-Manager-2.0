#!/usr/bin/env python
import md5, urllib
'''
@Copyright 2012, Proximus Mobility, LLC., all rights reserved
@author dshaw
'''
class ProximusToken():
    def __init__(self, my_company_salt):
        self.my_company_salt = my_company_salt
    
    def generateAuthenticationToken(self, url):
        m = md5.new()
        toEncode = self.my_company_salt + url
        m.update(toEncode)
        return m.hexdigest().upper()
    
if __name__ == "__main__":
    tokenGenerator = ProximusToken("MYCOMPANYISAWESOME")
        
    URL_BASE = "http://devices.proximusmobility.com/api/"
    username = urllib.quote_plus("user@domain.com".encode('utf-8'))
    password = "iwadasnin2012wisawuts"
            
    encodingUrl = URL_BASE + username + "/" + password + "/params1/params2/params..n"
    token = tokenGenerator.generateAuthenticationToken(encodingUrl)
    requestUrl = URL_BASE + username + "/" + token + "/params1/params2/params..n"
        
    print("Raw URL was " + encodingUrl)
    print("Token would be " + token)
    print("Request URL would be " + requestUrl) 
