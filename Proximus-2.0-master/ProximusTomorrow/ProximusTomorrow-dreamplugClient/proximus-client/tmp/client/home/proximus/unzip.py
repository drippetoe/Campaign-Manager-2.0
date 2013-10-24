#!/usr/bin/env python

import sys, shutil, logging, os
from zipfile import ZipFile

""" TEST ZipFile """

logger = logging.root

def initDir(dir):
    if not os.path.exists(dir):
        try:
            os.makedirs(dir)
            logger.debug("Created directory: %s" % dir)
        except Exception as err:
            logger.error(err)
    else:
        logger.debug("Directory exists: %s" % dir)

def errorExit():
    sys.stderr.write("Usage: %s <file> [folder]\n" % sys.argv[0])
    raise SystemExit

if __name__ == "__main__":
    if len(sys.argv) < 1:
        errorExit()
    if len(sys.argv) > 2:
        path = sys.argv[2]
    else:
        path = "."
    
    zipFilePath = sys.argv[1]
    

    zipfile = ZipFile(zipFilePath, 'r')
    outputFolder = os.path.abspath(path)
    print "Extracting %s to %s" % ( zipFilePath, outputFolder )
    #shutil.rmtree(outputFolder, ignore_errors=True)
    initDir(outputFolder)
    zipfile.extractall(outputFolder)


