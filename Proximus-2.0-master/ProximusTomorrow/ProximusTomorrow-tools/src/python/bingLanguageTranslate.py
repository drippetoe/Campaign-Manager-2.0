#!/usr/bin/env python
"""
Written by David Shaw, using a "free" Bing Translate account.  2 Million chars per month max

http://msdn.microsoft.com/en-us/library/dd576287

"""
import sys, requests, json, os
import xml.etree.ElementTree as ET
from xml.sax.saxutils import escape

def getAccessToken():
    url = "https://datamarket.accesscontrol.windows.net/v2/OAuth2-13"
    client_id = "TestClientProx"
    client_secret = "Womnr3zLLt+KGK1I5yJodV9tYbq/7VTyG0GwYDDF4qI="
    scope = "http://api.microsofttranslator.com"
    grant_type = "client_credentials"
    
    payload = { "url" : url,
                "client_id" : client_id,
                "client_secret" : client_secret,
                "scope" : scope,
                "grant_type" : grant_type
              }

    r = requests.post(url, data=payload)
    return r.json["access_token"]


def getTranslatedString(fromLang, toLang, accessToken, stringtoTranslate):
    url = "http://api.microsofttranslator.com/v2/Http.svc/Translate"
    payload = { "Text" : stringtoTranslate,
                "from" : fromLang,
                "to"   : toLang }
    
    headers = { "Authorization" : "Bearer %s" % accessToken }

    r = requests.get(url, params=payload, headers=headers)
    
    try:
        root = ET.fromstring(r.text.encode("utf-8"))
        return root.text
    except Exception as err:
        
        print("Error translating %s: result was %s" % ( stringtoTranslate, r.text ))
        raise SystemExit
        #return r.text

if __name__ == "__main__":
    if ( len(sys.argv) != 3 ):
        sys.stderr.write("Usage: %s <infile> <locale>\n" % sys.argv[0])
        raise SystemExit
    
    original_language = "en"
    
    infile = sys.argv[1]
    infile_path = os.path.abspath(infile)
    infile_directory = os.path.abspath(infile + os.sep + os.path.pardir)
    
    new_language = sys.argv[2]
    
    token = getAccessToken()
    
    if ( not os.path.exists(infile_path) ):
        sys.stderr.write("File does not exist: %s\n" % infile_path)
        raise SystemExit
    
    inf = open(infile_path, "r")
    
    name, ext = infile.split(".")
    
    outfile = name + "_" + new_language + "."  + ext
    outf = open(outfile, "w")
    for line in inf.readlines():
        if ( line.find("=") > -1 ):
            key, value = line.split("=")
            key = key.strip()
            value = value.strip()
            
            translated_value = getTranslatedString(original_language, new_language, token, value)
            translated_value = translated_value.strip().encode("utf-8")
            if ( translated_value != None ):
                outf.write("%s=%s\n" % ( key, translated_value ))
    
    outf.close()
    inf.close()
    
    print "Translation completed at: " + outfile
